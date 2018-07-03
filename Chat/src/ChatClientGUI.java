import javax.swing.*;

/**
 * Created by simon.knott on 29.06.2018.
 */
public class ChatClientGUI {
    private JTextArea messageView;
    private JPanel mainPanel;
    private JTextField messageInput;
    private JButton sendButton;
    private JTextField ipInput;
    private JTextField portInput;
    private JTextField nickInput;
    private JButton connectButton;
    private JComboBox<String> recipientBox;
    private JButton disconnectButton;
    private JButton banButton;

    private ChatClient client;

    private static final String ALL_USERS = "ALL_USERS";

    private ChatClientGUI() {
        JFrame frame = new JFrame("Composer");

        sendButton.addActionListener(e -> sendMessage());
        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        banButton.addActionListener(e -> banUser());

        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void connect() {
        String ip = ipInput.getText();
        int port = Integer.parseInt(portInput.getText());
        String nick = nickInput.getText();

        try {
            client = new ChatClient(ip, port, nick);
        } catch (ChatClient.CommandFailedException ignored) {}

        registerListeners();
    }

    private void disconnect() {
        try {
            client.leave();
        } catch (ChatClient.CommandFailedException e) {
            handleException(e);
        }
    }

    private void registerListeners() {
        client.onMessage(this::onMessage);
        client.onRecipiableChange(this::onUsersChange);
    }

    private void onMessage(ChatClient.MessageInformation msg) {
        String line = msg.isPublic
          ? String.format("%s: %s", msg.sender, msg.msg)
          : String.format("%s to %s: %s", msg.sender, msg.recipient, msg.msg);


        messageView.append(line);
        messageView.append("\n");
    }

    private void onUsersChange(java.util.List<String> users) {
        recipientBox.removeAllItems();
        recipientBox.addItem(ALL_USERS);
        users.forEach(recipientBox::addItem);
    }

    private void sendMessage() {
        String recipient = (String) recipientBox.getSelectedItem();
        String msg = messageInput.getText();

        assert recipient != null;

        try {
            if (recipient.equals(ALL_USERS)) {
                client.sendPublicMessage(msg);
            } else {
                client.sendMessage(recipient, msg);
            }
        } catch (ChatClient.CommandFailedException e) {
            handleException(e);
        }
    }

    private void banUser() {
        String user = (String) recipientBox.getSelectedItem();
        if (user.equals(ALL_USERS)) {
            return;
        }

        try {
            client.ban(user);
        } catch (ChatClient.CommandFailedException ignored) {}
    }

    private void handleException(ChatClient.CommandFailedException e) {
        JOptionPane.showMessageDialog(mainPanel, e.getMessage());
    }

    private void createUIComponents() {}

    public static void main(String[] args) {
        new ChatClientGUI();
    }

}
