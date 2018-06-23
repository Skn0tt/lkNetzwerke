public class BlockingConnection extends Connection {

  public BlockingConnection(String serverName, int port) {
    super(serverName, port);
  }

  @Override
  public void send(String nachricht) {
    super.send(nachricht);
    this.receive();
  }
}
