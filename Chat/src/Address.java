class Address {
  final String ip;
  final int port;

  private Address(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  @Override
  public String toString() {
    return "Address{" +
      "ip='" + ip + '\'' +
      ", port=" + port +
      '}';
  }

  public static Address from(String ip, int port) {
    return new Address(ip, port);
  }
}
