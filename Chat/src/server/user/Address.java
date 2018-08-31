package server.user;

public class Address {
  public final String ip;
  public final int port;

  private Address(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  @Override
  public String toString() {
    return "server.user.Address{" +
      "ip='" + ip + '\'' +
      ", port=" + port +
      '}';
  }

  public static Address from(String ip, int port) {
    return new Address(ip, port);
  }
}
