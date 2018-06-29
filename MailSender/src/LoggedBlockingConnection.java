public class LoggedBlockingConnection extends BlockingConnection {

  public LoggedBlockingConnection(String host, int port) {
    super(host, port);
  }

  @Override
  public void send(String nachricht) {
    System.out.println(nachricht);
    super.send(nachricht);
  }

  @Override
  public String receive() {
    String msg = super.receive();
    System.out.println("> " + msg);
    return msg;
  }
}
