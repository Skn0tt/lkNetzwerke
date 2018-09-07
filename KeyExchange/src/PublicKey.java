import java.math.BigInteger;

public class PublicKey {

    final BigInteger p;
    final BigInteger g;

    public PublicKey(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
    }

    public static boolean isLegalP(BigInteger p) {
        return PrimeHelper.isPrime(p);
    }

    public static boolean isLegalPublicKey(BigInteger p, BigInteger g) {
        int a = p.compareTo(g);
        return p.compareTo(g) > 0 && isLegalP(p);
    }

    @Override
    public String toString() {
        return "PublicKey{" +
                "p=" + p +
                ", g=" + g +
                '}';
    }

    static class PublicKeyIllegalException extends Exception {}

}
