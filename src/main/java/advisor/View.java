package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class View {
    static void print(String string) {
        System.out.println(string);
    }

    static void printMenu() {
        System.out.println("---Spotify Music Advisor---\n");
        System.out.println("1. Authorize");
        System.out.println("2. Show new releases");
        System.out.println("3. Show featured playlists");
        System.out.println("4. Show category playlist");
        System.out.println("5. Exit");
    }

    static void printNewReleases() throws IOException, InterruptedException {
        JsonObject albums = SpotifyAPI.getNewReleases();
        int albumSizes = albums.get("items").getAsJsonArray().size();

        for (int i = 0; i < albumSizes; i++) {
            JsonObject album = albums.get("items").getAsJsonArray().get(i).getAsJsonObject();
            String albumName = album.get("name").getAsString();
            String albumUrl = album.get("external_urls").getAsJsonObject().get("spotify").getAsString();
            List<String> artists = new ArrayList<>();
            for (JsonElement jsonElement : album.get("artists").getAsJsonArray()) {
                artists.add(jsonElement.getAsJsonObject().get("name").getAsString());
            }
            System.out.println(albumName);
            System.out.println(artists);
            System.out.println(albumUrl);
            System.out.println();
        }
    }

    static void printFeaturedPlaylists() throws IOException, InterruptedException {
        JsonObject playlists = SpotifyAPI.getFeaturedPlaylists();
        int playlistsSize = playlists.get("items").getAsJsonArray().size();

        for (int i = 0; i < playlistsSize; i++) {
            JsonObject playlist = playlists.get("items").getAsJsonArray().get(i).getAsJsonObject();
            String playlistName = playlist.get("name").getAsString();
            String playlistUrl = playlist.get("external_urls").getAsJsonObject().get("spotify").getAsString();

            System.out.println(playlistName);
            System.out.println(playlistUrl);
            System.out.println();
        }
    }

    static void printCategories() throws IOException, InterruptedException {
        JsonObject categories = SpotifyAPI.getCategories();
        int categoriesSize = categories.get("items").getAsJsonArray().size();

        for (int i = 0; i < categoriesSize; i++) {
            JsonObject category = categories.get("items").getAsJsonArray().get(i).getAsJsonObject();
            String categoryName = category.get("name").getAsString();
            String categoryId = category.get("id").getAsString();

            SpotifyAPI.categoriesWithId.put(categoryName, categoryId);

            System.out.println(categoryName);
        }
        System.out.println();
    }

    static void printPlaylistByName(String name) throws IOException, InterruptedException {
        JsonObject categories = SpotifyAPI.getPlaylistByName(name);
        int categoriesSize = categories.get("items").getAsJsonArray().size();

        for (int i = 0; i < categoriesSize; i++) {
            JsonObject category = categories.get("items").getAsJsonArray().get(i).getAsJsonObject();
            String categoryName = category.get("name").getAsString();
            String categoryUrl = category.get("external_urls").getAsJsonObject().get("spotify").getAsString();

            System.out.println(categoryName);
            System.out.println(categoryUrl);
            System.out.println();
        }
    }
}