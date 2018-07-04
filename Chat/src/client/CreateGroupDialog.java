package client;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CreateGroupDialog extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField identifierTxt;
  private JTextField membersTxt;

  private Consumer<CreateGroupResult> onOKListener;

  public CreateGroupDialog(Consumer<CreateGroupResult> onOKListener) {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    this.onOKListener = onOKListener;

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
    contentPane.registerKeyboardAction(
      e -> onCancel(),
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
      JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
    );

    // Show
    this.pack();
    this.setVisible(true);
  }

  private void onOK() {
    String identifier = identifierTxt.getText();
    String membersCSV = membersTxt.getText();
    String[] members = membersCSV.split(",");
    CreateGroupResult r = new CreateGroupResult(identifier, members);
    onOKListener.accept(r);
    dispose();
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  public static class CreateGroupResult {
    final String identifier;
    final String[] members;

    public CreateGroupResult(String identifier, String[] members) {
      this.identifier = identifier;
      this.members = members;
    }
  }

  public static void main(String[] args) {
    CreateGroupDialog dialog = new CreateGroupDialog(d -> {});
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }
}
