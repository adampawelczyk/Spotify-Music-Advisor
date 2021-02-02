package advisor;

import java.io.IOException;
import java.util.Scanner;

public class Controller {
    public static void run() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            View.printMenu();
            int choice = scanner.nextInt();

            if (!SpotifyAPI.isAuth && (choice != 1 && choice != 5)) {
                View.print("Please, provide access for application.");
            } else {
                switch (choice) {
                    case 1:
                        if (SpotifyAPI.isAuth) {
                            View.print("Application is already authorized.");
                        } else {
                            SpotifyAPI.authorize();
                        }
                        break;
                    case 2:
                        View.print("\n---NEW RELEASES---\n");
                        View.printNewReleases();
                        break;
                    case 3:
                        View.print("\n---FEATURED PLAYLISTS---\n");
                        View.printFeaturedPlaylists();
                        break;
                    case 4:
                        View.print("\n---CATEGORIES---\n");
                        View.printCategories();
                        View.print("Enter category name: ");
                        scanner.nextLine();
                        String categoryName = scanner.nextLine();
                        while (!SpotifyAPI.categoryExists(categoryName)) {
                            View.print("Unknown category");
                            View.print("Category name: ");
                            categoryName = scanner.nextLine();
                        }
                        View.print("\n---"+ categoryName.toUpperCase() + " PLAYLIST---\n");
                        View.printPlaylistByName(categoryName);
                        break;
                    case 5:
                        View.print("Bye");
                        System.exit(0);
                        break;
                    default:
                        View.print("Unknown menu option");
                }
            }
            View.print("");
        }
    }
}