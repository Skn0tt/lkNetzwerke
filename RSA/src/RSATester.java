import java.math.BigInteger;
import java.util.Scanner;

public class RSATester {

    static Scanner scanner = new Scanner(System.in);

    static int askForNumber(String prompt) {
        System.out.println(prompt);
        return scanner.nextInt();
    }

    static String askForString(String prompt) {
        System.out.println(prompt);
        return scanner.next();
    }

    public static void main(String... args) {
        KeyPair keyPair = RSA.generateKeyPair(1024);
        System.out.println(keyPair);

        int msg = askForNumber("What do you want to encrypt?");
        BigInteger encrypted = RSA.encrypt(new BigInteger("" + msg), keyPair.publicKey);

        System.out.println("Encrypted: ");
        System.out.println(encrypted);

        BigInteger decrypted = RSA.decrypt(encrypted, keyPair);

        System.out.println("Decrypted: ");
        System.out.println(decrypted);

    }

}
