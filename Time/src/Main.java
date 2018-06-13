import java.util.Scanner;

public class Main {

  private static class ConnectionAddress {
    String ip;
    int port;
  }

  static Scanner scanner = new Scanner(System.in);

  static ConnectionAddress askForAddress() {
    System.out.println("Bitte geben sie die anzufragende Addresse ein.");
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

  static String request(ConnectionAddress a) {
    Connection c = new Connection(a.ip, a.port);
    return c.receive();
  }

  static void welcomeUser() {
    System.out.println("Time-Connect - Am Puls der Zeit!");
    System.out.println("################################");
    System.out.println();
  }

  static void output(String s) {
    System.out.println("Response: ");
    System.out.println(s);
  }

  public static void main(String... args) {
    welcomeUser();
    ConnectionAddress a = askForAddress();
    String result = request(a);
    output(result);
  }
}
