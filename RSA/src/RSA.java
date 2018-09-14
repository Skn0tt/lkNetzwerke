import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;

public class RSA {

    static Random random = new Random(System.nanoTime());

    static BigInteger generateProbablePrime(int bitLength) {
        return BigInteger.probablePrime(bitLength, random);
    }

    static BigInteger phi(BigInteger a, BigInteger b) {
        return a.subtract(BigInteger.ONE).multiply(b.subtract(BigInteger.ONE));
    }

    static BigInteger computeE(BigInteger r) {
        BigInteger e;

        do e = new BigInteger(r.bitLength(), random);
        while (e.compareTo(BigInteger.ONE) <= 0
                || e.compareTo(r) >= 0
                || !e.gcd(r).equals(BigInteger.ONE));

        return e;
    }

    static KeyPair generateKeyPair(int bitLength) {
        BigInteger p = generateProbablePrime(bitLength);
        BigInteger g = generateProbablePrime(bitLength);

        BigInteger N = p.multiply(g);
        BigInteger r = phi(p, g);

        BigInteger e = computeE(r);

        BigInteger d = e.modInverse(r);

        return new KeyPair(new PublicKey(N, e), d);
    }

    static BigInteger encrypt(BigInteger data, PublicKey publicKey) {
        return data.modPow(publicKey.e, publicKey.N);
    }

    static BigInteger decrypt(BigInteger chiffre, KeyPair keyPair) {
        return chiffre.modPow(keyPair.privateKey, keyPair.publicKey.N);
    }

}
