import java.math.BigInteger;
import java.util.Scanner;

public class KeyExchangeClient extends Client {

  private static final Scanner in = new Scanner(System.in);

  private PublicKey publicKey;
  private BigInteger privateKey;

  private static String read(String prompt) {
    System.out.println(prompt);
    return in.next();
  }

  KeyExchangeClient(String ip, int port) {
    super(ip, port);
  }

  @Override
  public void processMessage(String pMessage) {
    String[] parts = pMessage.split(":");
    String command = parts[0];

    switch (command) {
      case "PUBLIC_KEY":
        String p = parts[1];
        String g = parts[2];
        publicKey = new PublicKey(p, g);
        privateKey = DiffieHellman.generatePrivateKey(publicKey);
        BigInteger exchangeKey = DiffieHellman.computeExchangeKey(publicKey, privateKey);
        publishExchangeKey(exchangeKey);
        break;
      case "OTHER_EXCHANGE_KEY":
        BigInteger otherExchangeKey = new BigInteger(parts[1]);
        BigInteger key = DiffieHellman.computeKey(publicKey, otherExchangeKey, privateKey);
        announceKey(key);
        break;
    }

  }

  private void announceKey(BigInteger key) {
    String msg = String.format("Key: %s", key.toString());
    System.out.println(msg);
  }

  void publishExchangeKey(BigInteger key) {
    final String msg = String.format("PUBLISH_EXCHANGE_KEY:%s", key.toString());
    this.send(msg);
  }


  public static void main(String... args) {
    String ip = read("IP:");
    int port = Integer.parseInt(read("Port:"));
    new KeyExchangeClient(ip, port);
  }

}
