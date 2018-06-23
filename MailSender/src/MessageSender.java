import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

public class MessageSender {

  private final Connection con;

  private final UserCredentials uc;

  private MessageSender(ConnectionOptions o, UserCredentials uc) {
    this.uc = uc;
    con = new LoggedBlockingConnection(o.uri, o.port);

    this.login();
  }

  private void login() {
    String ehlo = String.format("EHLO %s", uc.email);
    con.send(ehlo);

    String user = String.format("AUTH LOGIN %s", base64Encode(uc.email));
    con.send(user);

    String pass = base64Encode(uc.password);
    con.send(pass);
  }

  private void sendMessage(Envelope e) {
    String mailFrom = String.format("MAIL FROM: %s", uc.email);
    con.send(mailFrom);

    String rcpt = String.format("RCPT TO: %s", e.recipient);
    con.send(rcpt);

    con.send("DATA");

    String from = String.format("From: %s", uc.email);
    con.send(from);

    String to = String.format("To: %s", e.recipient);
    con.send(to);

    String subject = String.format("Subject: %s", e.subject);
    con.send(subject);

    String date = String.format("Date: %s", nowAsISO());
    con.send(date);

    con.send(e.body);

    con.send(".");
  }

  private static String nowAsISO() {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    df.setTimeZone(tz);
    return df.format(new Date());
  }

  private static String base64Encode(String txt) {
    return Base64.getEncoder().encodeToString(txt.getBytes());
  }

  public static void send(ConnectionOptions o, UserCredentials c, Envelope e) {
    MessageSender s = new MessageSender(o, c);
    s.sendMessage(e);
  }

  static class ConnectionOptions {
    final String uri;
    final int port;

    public ConnectionOptions(String uri, int port) {
      this.uri = uri;
      this.port = port;
    }

    @Override
    public String toString() {
      return "ConnectionOptions{" +
        "uri='" + uri + '\'' +
        ", port=" + port +
        '}';
    }
  }

  static class Envelope {
    final String recipient;
    final String subject;
    final String body;

    public Envelope(String recipient, String subject, String body) {
      this.recipient = recipient;
      this.subject = subject;
      this.body = body;
    }

    @Override
    public String toString() {
      return "Envelope{" +
        ", recipient='" + recipient + '\'' +
        ", subject='" + subject + '\'' +
        ", body='" + body + '\'' +
        '}';
    }
  }

  static class UserCredentials {
    final String email;
    final String password;

    public UserCredentials(String email, String password) {
      this.email = email;
      this.password = password;
    }

    @Override
    public String toString() {
      return "UserCredentials{" +
        "email='" + email + '\'' +
        ", password='" + password + '\'' +
        '}';
    }
  }

  public static void main(String... args) {
    MessageSender s = new MessageSender(new ConnectionOptions("mail.vfemail.net", 587), new UserCredentials("wer.klopft@vfemail.net", "wer.klopft"));
    s.sendMessage(new Envelope("simoknott@gmail.com", "Test2", "tjsdfjsadklfj"));
  }
}
