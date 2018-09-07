import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class DiffieHellman {

    public static PublicKey generatePublicKey() {
        Random random = new Random(System.nanoTime());
        while (true) {
            BigInteger p = BigInteger.probablePrime(1024, random);
            BigInteger g = new BigDecimal(p).multiply(new BigDecimal(random.nextDouble())).toBigInteger();
            return new PublicKey(p, g);
        }
    }

    public static BigInteger computeExchangeKey(PublicKey key, BigInteger privateKey) {
        return key.g.pow(privateKey.intValue()).mod(key.p);
    }

    public static BigInteger computeKey(PublicKey key, BigInteger exchangeKey, BigInteger privateKey) {
        return exchangeKey.pow(privateKey.intValue()).mod(key.p);
    }

}
