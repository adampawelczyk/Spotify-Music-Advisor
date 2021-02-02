package advisor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SpotifyAPI {
    static boolean isAuth = false;
    static Map<String, String> categoriesWithId = new HashMap<>();

    static void authorize() throws IOException, InterruptedException {
        getAuthCode();

        if (!SpotifyData.AUTH_CODE.equals("")) {
            getAccessToken();

            isAuth = true;
            System.out.println("Success!");
        }
    }

    static void getAuthCode() throws IOException, InterruptedException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);

        server.start();

        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                SpotifyData.AUTH_SERVER_URL, SpotifyData.CLIENT_ID, SpotifyData.REDIRECT_URI);
        System.out.println("\nwaiting for code...");

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String message;
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.contains("code")) {
                    message = "Got the code. Return back to your program.";
                    System.out.println("code received");
                    SpotifyData.AUTH_CODE = query.substring(5);
                } else {
                    message = "Authorization code not found. Try again.";
                }
                exchange.sendResponseHeaders(200, message.length());
                exchange.getResponseBody().write(message.getBytes());
                exchange.getResponseBody().close();
            }
        });
        while ("".equals(SpotifyData.AUTH_CODE)) {
            TimeUnit.SECONDS.sleep(1);
        }
        server.stop(1);
    }

    static void getAccessToken() throws IOException, InterruptedException {
        System.out.println("making http request for access_token...");

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(SpotifyData.GET_ACCESS_TOKEN_URL))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "client_id=" + SpotifyData.CLIENT_ID
                                + "&client_secret=" + SpotifyData.CLIENT_SECRET
                                + "&grant_type=authorization_code"
                                + "&code=" + SpotifyData.AUTH_CODE
                                + "&redirect_uri=" + SpotifyData.REDIRECT_URI
                ))
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SpotifyData.ACCESS_TOKEN = JsonParser.parseString(response.body()).getAsJsonObject().get("access_token").getAsString();
    }

    static String getRequestedData(String requestedURI) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + SpotifyData.ACCESS_TOKEN)
                .uri((URI.create(requestedURI)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    static JsonObject getNewReleases() throws IOException, InterruptedException {
        String newReleasesJson = getRequestedData(SpotifyData.API_URL + "/v1/browse/new-releases");
        return JsonParser.parseString(newReleasesJson).getAsJsonObject().get("albums").getAsJsonObject();
    }

    static JsonObject getFeaturedPlaylists() throws IOException, InterruptedException {
        String featuredPlaylistsJson = getRequestedData(SpotifyData.API_URL + "/v1/browse/featured-playlists");
        return JsonParser.parseString(featuredPlaylistsJson).getAsJsonObject().get("playlists").getAsJsonObject();
    }

    static JsonObject getCategories() throws IOException, InterruptedException {
        String categoriesJson = getRequestedData(SpotifyData.API_URL + "/v1/browse/categories");
        return JsonParser.parseString(categoriesJson).getAsJsonObject().get("categories").getAsJsonObject();
    }

    static boolean categoryExists(String name) {
        return categoriesWithId.containsKey(name);
    }

    static JsonObject getPlaylistByName(String name) throws IOException, InterruptedException {
        String categoryJson = getRequestedData(SpotifyData.API_URL + "/v1/browse/categories/" +
                categoriesWithId.get(name) + "/playlists");
        return JsonParser.parseString(categoryJson).getAsJsonObject().get("playlists").getAsJsonObject();
    }
}