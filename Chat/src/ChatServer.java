import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by simon.knott on 29.06.2018.
 */
public class ChatServer extends Server {

    private HashMap<String, UserInformation> usersByAddress = new HashMap<>();
    private HashMap<String, UserInformation> usersByNick = new HashMap<>();

    public ChatServer(int port) {
        super(port);
    }

    @Override
    void processNewConnection(String pClientIP, int pClientPort) {}

    @Override
    void processMessage(String pClientIP, int pClientPort, String pMessage) {
        Commands.CommandInfo info = Commands.parse(pMessage);
        Consumer<String> answer = s -> send(pClientIP, pClientPort, s);

        UserInformation sender = findUserByAddress(pClientIP, pClientPort);
        if (sender != null) {
            log(sender, info);
        }

        switch (info.cmd) {
            case Commands.SET_NICK:
                setNick(answer, pClientIP, pClientPort, info.args);
                break;
            case Commands.WHISPER:
                whisper(sender, info.args);
                break;
            case Commands.SEND_ALL:
                sendAll(sender, info.args);
                break;
        }
    }

    private void sendAll(UserInformation sender, String... args) {
        String msg = args[0];

        String cmd = Commands.build(Commands.NEW_MESSAGE, sender.nick, msg);
        sendToAll(cmd);
    }

    private void whisper(UserInformation from, String... args) {
        String toNick = args[0];
        String msg = args[1];
        UserInformation to = findUserByNick(toNick);

        String cmd = Commands.build(Commands.NEW_MESSAGE, from.nick, msg);
        sendToUser(to, cmd);
    }

    private UserInformation findUserByNick(String nick) {
        return usersByNick.get(nick);
    }

    private UserInformation findUserByAddress(String host, int port) {
        return usersByAddress.get(userAddress(host, port));
    }

    private void setNick(Consumer<String> answer, String ip, int port, String... args) {
        String nick = args[0];
        if (usersByNick.containsKey(nick)) {
            answer.accept("-400 Username already taken.");
            return;
        }

        UserInformation info = new UserInformation(ip, port, nick);

        addUser(info);

        answer.accept("+200 Success.");
    }

    private void sendToUser(UserInformation u, String msg) {
        send(u.ip, u.port, msg);
    }

    private static String userAddress(UserInformation userInformation) {
        return userAddress(userInformation.ip, userInformation.port);
    }

    private static String userAddress(String host, int port) {
        return host + ";" + port;
    }

    private void log(String msg) {
        System.out.println(msg);
    }

    private void log(UserInformation user, Commands.CommandInfo cmd) {
        String log = String.format(
                "%s@%s:%s: %s %s",
                user.nick, user.ip, user.port,
                cmd.cmd, String.join(" ", cmd.args)
        );
        log(log);
    }

    private void publishUsers() {
        Set<String> users = usersByNick.keySet();
        String cmd = Commands.build(Commands.USER_CHANGE, users);
        sendToAll(cmd);
    }

    private void addUser(UserInformation user) {
        usersByNick.put(user.nick, user);
        usersByAddress.put(userAddress(user), user);
        publishUsers();
    }

    private void removeUser(UserInformation user) {
        usersByAddress.remove(userAddress(user));
        usersByNick.remove(user.nick);
        publishUsers();
    }

    @Override
    void processClosedConnection(String pClientIP, int pClientPort) {
        UserInformation user = findUserByAddress(pClientIP, pClientPort);
        removeUser(user);
    }

    static class UserInformation {
        final String ip;
        final int port;
        final String nick;

        public UserInformation(String ip, int port, String nick) {
            this.ip = ip;
            this.port = port;
            this.nick = nick;
        }
    }

    static Scanner scanner = new Scanner(System.in);
    public static void main(String... args) {
        System.out.println("Port: ");
        int port = scanner.nextInt();
        new ChatServer(port);
    }
}
