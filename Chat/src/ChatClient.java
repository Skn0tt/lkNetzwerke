import java.util.*;
import java.util.function.Consumer;

/**
 * Created by simon.knott on 29.06.2018.
 */
public class ChatClient extends Client {

    private final java.util.List<Consumer<MessageInformation>> messageListeners = new ArrayList<>();

    private final java.util.List<Consumer<java.util.List<String>>> recipiableChangeListeners = new ArrayList<>();

    ChatClient(String serverHost, int serverPort, String nickname) throws CommandFailedException {
        super(serverHost, serverPort);
        login(nickname);
    }

    @Override
    public void processMessage(String pMessage) {
        try {
            Command cmd = Command.parse(pMessage);

            switch (cmd.verb) {
                case NEW_MESSAGE:
                    newMessage(cmd.args);
                    break;
                case NEW_PUBLIC_MESSAGE:
                    newMessage(cmd.args);
                    break;
                case RECIPIENTS_CHANGE:
                    recipiableChange(cmd.args);
                    break;
            }
        } catch (Command.CommandVerbUnknownException ignored) {}
    }

    /*
      # Methods
     */

    private void login(String nickname) throws CommandFailedException {
        send(CommandVerb.LOGIN, nickname);
    }

    public void ban(String user) throws CommandFailedException {
        send(CommandVerb.BAN_USER, user);
    }

    public void sendPublicMessage(String message) throws CommandFailedException {
        if (!isMessageValid(message)) {
            return;
        }

        send(CommandVerb.SEND_PUBLIC, message);
    }

    public void sendMessage(String recipient, String message) throws CommandFailedException {
        if (!isMessageValid(message)) {
            return;
        }

        send(CommandVerb.SEND, recipient, message);
    }

    public void leave() throws CommandFailedException {
        send(CommandVerb.QUIT);
    }

    /*
      # Listeners
     */

    private void newMessage(List<String> args) {
        String sender = args.get(0);
        String recipient = args.get(1);
        String msg = args.get(2);

        MessageInformation info = new MessageInformation(msg, sender, recipient, false);
        publishMessageToListeners(info);
    }

    private void newPublicMessage(List<String> args) {
        String sender = args.get(0);
        String msg = args.get(1);

        MessageInformation info = new MessageInformation(msg, sender, null, true);
        publishMessageToListeners(info);
    }

    private void publishMessageToListeners(MessageInformation info) {
        for (Consumer<MessageInformation> m : messageListeners) {
            m.accept(info);
        }
    }

    private void recipiableChange(List<String> users) {
        for (Consumer<java.util.List<String>> l : recipiableChangeListeners) {
            l.accept(users);
        }
    }

    public void onMessage(Consumer<MessageInformation> listener) {
        messageListeners.add(listener);
    }

    public void onRecipiableChange(Consumer<java.util.List<String>> listener) {
        recipiableChangeListeners.add(listener);
    }

    /*
      # Helpers
     */
    private boolean isMessageValid(String message) {
        return !message.isEmpty();
    }

    private void send(CommandVerb verb, String... args) throws CommandFailedException {
        String cmd = Command.build(verb, args);
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
        final String recipient;
        final boolean isPublic;

        MessageInformation(String msg, String sender, String recipient, boolean isPublic) {
            this.msg = msg;
            this.sender = sender;
            this.recipient = recipient;
            this.isPublic = isPublic;
        }
    }

}
