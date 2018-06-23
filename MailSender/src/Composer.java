import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Composer {
  private JEditorPane compositionTextArea;
  private JPanel mainPanel;
  private JTextField subjectTextField;
  private JTextField recipientTextField;
  private JButton sendButton;

  private List<Consumer<MessageSender.Envelope>> onSendSubscriptions = new ArrayList<>();

  public Composer() {
    JFrame frame = new JFrame("Composer");

    sendButton.addActionListener(this::onClickSendButton);

    frame.setContentPane(mainPanel);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  private MessageSender.Envelope getEnvelope() {
    String recipient = recipientTextField.getText();
    String subject = subjectTextField.getText();
    String contents = compositionTextArea.getText();

    return new MessageSender.Envelope(recipient, subject, contents);
  }

  private void onClickSendButton(ActionEvent a) {
    MessageSender.Envelope e = getEnvelope();
    for (Consumer<MessageSender.Envelope> c : onSendSubscriptions) {
      c.accept(e);
    }
  }

  public void onSend(Consumer<MessageSender.Envelope> c) {
    onSendSubscriptions.add(c);
  }

  public static void main(String[] args) {
    Composer c = new Composer();

    c.onSend(System.out::println);
  }
}
