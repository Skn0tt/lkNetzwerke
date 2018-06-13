public class EchoServer extends Server {

  public EchoServer(int port) {
    super(port);
  }

  @Override
  public void processNewConnection(String clientIp, int clientPort) {
    send(clientIp, clientPort, "Du bist angenommen. Scurr!");
  }

  @Override
  public void processClosedConnection(String clientIp, int clientPort) {
    super.processClosedConnection(clientIp, clientPort);
  }

  @Override
  public void processMessage(String clientIp, int clientPort, String msg) {
    send(clientIp, clientPort, "Return: " + msg);
  }
}
