import javax.swing.*;
import java.awt.event.*;

public class ConnectionOptionsModal extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField portTextField;
  private JTextField ipTextField;

  private ConnectionOptionsModal() {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(e -> onOK());

    buttonCancel.addActionListener(e -> onCancel());

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  private void onOK() {
    if (isPortValid()) {
      dispose();
    }
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  private boolean isPortValid() {
    try {
      Integer.parseInt(portTextField.getText());
      return true;
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(contentPane, "Please type in a valid port.");
      return false;
    }
  }

  private MessageSender.ConnectionOptions getConnectionOptions() throws NumberFormatException {
    String uri = ipTextField.getText();
    int port = Integer.parseInt(portTextField.getText());

    return new MessageSender.ConnectionOptions(uri, port);
  }

  public static MessageSender.ConnectionOptions askForConnectionOptions() {
    ConnectionOptionsModal m = new ConnectionOptionsModal();
    m.pack();
    m.setVisible(true);
    return m.getConnectionOptions();
  }

  public static void main(String[] args) {
    askForConnectionOptions();
  }
}
