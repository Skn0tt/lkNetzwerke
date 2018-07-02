import java.util.*;
import java.util.function.Consumer;

/**
 * Created by simon.knott on 29.06.2018.
 */
public class ChatClient extends Client {

    private final java.util.List<Consumer<MessageInformation>> messageListeners = new ArrayList<>();

    private final java.util.List<Consumer<java.util.List<String>>> userChangeListeners = new ArrayList<>();

    public ChatClient(String serverHost, int serverPort, String nickname) {
        super(serverHost, serverPort);
        setNick(nickname);
    }

    @Override
    public void processMessage(String pMessage) {
        Commands.CommandInfo cmd = Commands.parse(pMessage);
        switch (cmd.cmd) {
            case Commands.NEW_MESSAGE:
                newMessage(cmd.args);
            case Commands.USER_CHANGE:
                userChange(cmd.args);
        }
    }

    private void setNick(String nick) {
        String cmd = Commands.build(Commands.SET_NICK, nick);
        send(cmd);
    }

    private void newMessage(String... args) {
        String sender = args[0];
        String msg = args[1];

        MessageInformation info = new MessageInformation(msg, sender);

        for (Consumer<MessageInformation> m : messageListeners) {
            m.accept(info);
        }
    }

    private void userChange(String... args) {
        java.util.List<String> users =  Arrays.asList(args);

        for (Consumer<java.util.List<String>> l : userChangeListeners) {
            l.accept(users);
        }
    }

    public void onMessage(Consumer<MessageInformation> listener) {
        messageListeners.add(listener);
    }

    public void onUserChange(Consumer<java.util.List<String>> listener) {
        userChangeListeners.add(listener);
    }

    public void login(String nickname) throws CommandFailedException {
        String cmd = Commands.build(Commands.SET_NICK, nickname);
        send(cmd);
        checkResponse();
    }

    public void sendPublicMessage(String message) throws CommandFailedException {
        String cmd = Commands.build(Commands.SEND_ALL, message);
        send(cmd);
        checkResponse();
    }

    public void whisper(String recipient, String message) throws CommandFailedException {
        String cmd = Commands.build(Commands.WHISPER, recipient, message);
        send(cmd);
        checkResponse();
    }

    public void leave() throws CommandFailedException {
        String cmd = Commands.build(Commands.QUIT);
        send(cmd);
        checkResponse();
    }

    private void checkResponse() throws CommandFailedException {
        String response = hatVerbindung.receive();
        if (response.startsWith("-")) {
            throw new CommandFailedException(response);
        }
    }

    static class CommandFailedException extends Exception {
        CommandFailedException(String msg) {
            super(msg);
        }
    }

    static class MessageInformation {
        final String msg;
        final String sender;

        MessageInformation(String msg, String sender) {
            this.msg = msg;
            this.sender = sender;
        }
    }

}
