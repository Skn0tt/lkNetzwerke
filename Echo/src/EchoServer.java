public class EchoServer extends Server {

  public EchoServer(int port) {
    super(port);
  }

  public void processNewConnection(String clientIp, int clientPort) {
    send(clientIp, clientPort, "Du bist angenommen. Scurr!");
  }

  public void processClosedConnection(String clientIp, int clientPort) {}

  public void processMessage(String clientIp, int clientPort, String msg) {
    send(clientIp, clientPort, "Return: " + msg);
  }
}
