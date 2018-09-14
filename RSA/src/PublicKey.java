import java.math.BigInteger;

public class PublicKey {
    final BigInteger N;
    final BigInteger e;

    public PublicKey(BigInteger n, BigInteger e) {
        N = n;
        this.e = e;
    }

    @Override
    public String toString() {
        return "PublicKey{\n" +
                "N=" + N +
                "\n, e=" + e +
                '}';
    }

}
