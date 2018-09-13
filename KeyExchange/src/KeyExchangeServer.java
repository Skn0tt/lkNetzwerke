import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class KeyExchangeServer extends Server {

    private final PublicKey publicKey = DiffieHellman.generatePublicKey();
    private final TwoObjectRepository<User> users = new TwoObjectRepository<User>();


    private KeyExchangeServer(int port) {
        super(port);
        System.out.println(publicKey.toString());
    }

    @Override
    void processNewConnection(String pClientIP, int pClientPort) {
        final User user = new User(pClientIP, pClientPort);
        try {
          users.add(user);
        } catch (TwoObjectRepository.TooManyObjectsAddedException e) {
          send(pClientIP, pClientPort, "TOO_MANY_USERS");
          return;
        }

        if (users.isFull()) {
          publishPublicKey();
        }
    }

    private void publishPublicKey() {
      final String message = String.format("PUBLIC_KEY:%s:%s", publicKey.p, publicKey.g);
      sendToAll(message);
    }

    @Override
    void processMessage(String pClientIP, int pClientPort, String pMessage) {
        if (!users.isFull()) {
            send(pClientIP, pClientPort, "WAITING_FOR_SECOND_USER");
            return;
        }

        final Pair<User, User> bothUsers= users.get(new User(pClientIP, pClientPort));
        final User currentUser = bothUsers.getKey();
        final User otherUser = bothUsers.getValue();

        final String[] parts = pMessage.split(":");
        final String command = parts[0];

        switch (command) {
            case "PUBLISH_EXCHANGE_KEY":
                String exchangeKey = parts[1];
                String msg = String.format("OTHER_EXCHANGE_KEY:%s", exchangeKey);
                send(otherUser.ip, otherUser.port, msg);
                break;
            default:
                send(pClientIP, pClientPort, "UNKNOWN_COMMAND");
        }
    }

    @Override
    void processClosedConnection(String pClientIP, int pClientPort) {
        users.remove(new User(pClientIP, pClientPort));
    }

    public static void main(String... args) {
      Scanner in = new Scanner(System.in);
      System.out.println("Port:");
      int port = in.nextInt();
      new KeyExchangeServer(port);
    }

}
