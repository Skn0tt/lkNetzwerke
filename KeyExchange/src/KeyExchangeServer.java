import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KeyExchangeServer extends Server {

    private final PublicKey publicKey = DiffieHellman.generatePublicKey();
    private List<User> users = new ArrayList<>();


    public KeyExchangeServer(int port) {
        super(port);
        System.out.println(publicKey.toString());
    }

    @Override
    void processNewConnection(String pClientIP, int pClientPort) {
        final User user = new User(pClientIP, pClientPort);
        users.add(user);
        if (users.size() > 2) {
            send(pClientIP, pClientPort, "TOO_MANY_USERS");
            return;
        }

        String message = String.format("PUBLIC_KEY:%s:%s", publicKey.p, publicKey.g);
        send(pClientIP, pClientPort, message);
    }

    @Override
    void processMessage(String pClientIP, int pClientPort, String pMessage) {
        if (users.size())
        final User user = new User(pClientIP, pClientPort);
        final User otherUser = (User) users.stream().filter(u -> !u.equals(user)).toArray()[0];

        if (otherUser == null) {
            send(pClientIP, pClientPort, "WAIT_FOR_OTHER_USER");
            return;
        }

        String[] parts = pMessage.split(":");
        String command = parts[0];

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
        final User user = new User(pClientIP, pClientPort);
        users.removeIf(u -> u.equals(user));
    }

    public static void main(String... args) {
        new KeyExchangeServer(4000);
    }

}
