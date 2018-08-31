import java.util.Objects;

public class Player {
    final String ip;
    final int port;

    public Player(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return port == player.port &&
                Objects.equals(ip, player.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
