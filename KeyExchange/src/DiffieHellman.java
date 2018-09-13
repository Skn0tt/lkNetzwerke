import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class DiffieHellman {

    public static PublicKey generatePublicKey() {
        Random random = new Random(System.nanoTime());
        BigInteger p = BigInteger.probablePrime(1024, random);
        BigInteger g = new BigDecimal(p).multiply(new BigDecimal(random.nextDouble())).toBigInteger();
        return new PublicKey(p, g);
    }

    public static BigInteger generatePrivateKey(PublicKey publicKey) {
        BigDecimal decimal = new BigDecimal(publicKey.g);
        BigDecimal pK = decimal.multiply(new BigDecimal(Math.random()));
        return pK.toBigInteger();
    }

    public static BigInteger computeExchangeKey(PublicKey key, BigInteger privateKey) {
        return key.g.modPow(privateKey, key.p);
    }

    public static BigInteger computeKey(PublicKey key, BigInteger exchangeKey, BigInteger privateKey) {
        return exchangeKey.modPow(privateKey, key.p);
    }

}
