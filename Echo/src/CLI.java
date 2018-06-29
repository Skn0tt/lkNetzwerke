import java.util.Scanner;

public class CLI {

  enum Mode {
    Client,
    Server
  }

  private static class ConnectionAddress {
    String ip;
    int port;
  }

  static Scanner scanner = new Scanner(System.in);

  static int serverPortAbfragen() {
    System.out.println("Gib den Port an, auf dem der Server gestartet werden soll.");
    return scanner.nextInt();
  }

  static Mode modusAbfragen() {
    System.out.println("Möchtest du einen Client oder einen Server starten?");
    System.out.println("Server: true");
    System.out.println("Client: false");
    System.out.println();

    boolean b = scanner.nextBoolean();
    scanner.nextLine();

    return b ? Mode.Server : Mode.Client;
  }

  static ConnectionAddress askForAddress() {
    System.out.println("Bitte geben sie die zu verbindende Addresse ein.");
    System.out.println("Beispiel: time.fu-berlin.de:13");
    System.out.println();

    String s = scanner.nextLine();

    System.out.println();

    String[] arr = s.split(":");

    if (arr.length != 2) {
      errorInvalidAddress();
    }

    ConnectionAddress result = new ConnectionAddress();
    result.ip = arr[0];
    try {
      result.port = Integer.parseInt(arr[1]);
    } catch (NumberFormatException e) {
      errorInvalidAddress();
    }

    return result;
  }

  static void errorInvalidAddress() {
    System.err.println("Geben sie eine gültige Addresse an.");
    System.exit(1);
  }

  static void startServer() {
    int port = serverPortAbfragen();
    new EchoServer(port);
  }

  static void startClient() {
    ConnectionAddress c = askForAddress();
    new EchoClient(c.ip, c.port);
  }

  public static void main(String... args) {
    Mode m = modusAbfragen();
    if (m == Mode.Server) {
      startServer();
    } else {
      startClient();
    }
  }

}
