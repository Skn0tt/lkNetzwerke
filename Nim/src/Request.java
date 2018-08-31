import java.util.*;

public class Request {

    final Protocol p;
    final String[] args;

    public Request(Protocol p, String[] args) {
        this.p = p;
        this.args = args;
    }

    static Request parse(String msg) {
        String[] args = msg.split(":");
        Protocol p = Protocol.parse(msg);
        return new Request(p, dropFirst(args));
    }

    static String[] dropFirst(String[] oldArr) {
        return Arrays.copyOfRange(oldArr, 1, oldArr.length);
    }

}
