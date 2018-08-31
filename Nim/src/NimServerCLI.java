import java.util.Scanner;

public class NimServerCLI {

    final NimServer server = new NimServer(8000, this::onConnect);

    Scanner in = new Scanner(System.in);

    void onConnect(Player p) {
        System.out.println("New connection: " + p.toString());
        System.out.println("Want to start the game?");
        if (in.nextBoolean()) {
            server.startGame();
            System.out.println("Started the game.");
        }
    }

    public static void main(String... args) {
        new NimServerCLI();
    }
}
