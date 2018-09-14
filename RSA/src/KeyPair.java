import java.math.BigInteger;

public class KeyPair {

    final PublicKey publicKey;
    final BigInteger privateKey;

    @Override
    public String toString() {
        return "KeyPair{\n" +
                "publicKey=" + publicKey +
                "\n, privateKey=" + privateKey +
                '}';
    }

    public KeyPair(PublicKey publicKey, BigInteger privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
