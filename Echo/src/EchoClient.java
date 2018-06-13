import java.util.Scanner;

public class EchoClient extends Client {

  public EchoClient(String ip, int port) {
    super(ip, port);
    start();
  }

  Scanner s = new Scanner(System.in);
  void start() {
    while (true) {
      String msg = s.nextLine();
      send(msg);
    }
  }

  @Override
  public void processMessage(String msg) {
    output(msg);
  }

  void output(String s) {
    System.out.println(s);
  }

}
