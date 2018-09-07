import java.math.BigInteger;

public class PrimeHelper {

    private static BigInteger TWO = new BigInteger("2");
    private static BigInteger THREE = new BigInteger("3");

    public static boolean isPrime(BigInteger n) {
        if (n.equals(TWO)) {
            return true;
        }

        if (isEven(n)) {
            return false;
        }

        for (BigInteger i = THREE; i.pow(2).compareTo(n) < 0; i = i.add(TWO)) {
            if (isEven(i)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isEven(BigInteger n) {
        return n.mod(TWO).equals(BigInteger.ZERO);
    }

    public static boolean isPrime(int n) {
        return isPrime(new BigInteger("" + n));
    }

}
