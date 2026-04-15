import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Scanner;

public class CryptoUtil {
    private static final String secretKey = "MySuperSecretKey";

    public static String encrypt(String rowMsg) throws Exception{
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(rowMsg.getBytes());
//        System.out.println("encryptedBytes: "+encryptedBytes);

        return java.util.Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedMessage) throws Exception{
        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedMessage);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(),"AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
//        System.out.println("decryptedBytes: "+decryptedBytes);

        return new String(decryptedBytes);
    }
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String originalMessage = sc.nextLine();
        System.out.println("Original: " + originalMessage);

        String scrambled = encrypt(originalMessage);
        System.out.println("Encrypted: " + scrambled);

        String cracked = decrypt(scrambled);
        System.out.println("Decrypted: " + cracked);
    }
}
