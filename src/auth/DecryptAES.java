package auth;

import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DecryptAES {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    //Encryption
    public static String encrypt(String plainText, String key) throws Exception {
        key = padKey(key);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    //Decryption
    public static String decrypt(String encryptedText, String key) throws Exception {
        key = padKey(key);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    public static String padKey(String key) {
        int keyPadding = 16 - (key.length() % 16);
        for (int index = 0; index < keyPadding; index++) {
            key += "0";
        }
        return key;
    }

    public static void main(String[] args) throws Exception {
        //Take user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter password: ");
        String key = scanner.nextLine();
        scanner.close();
        

        String plainText = "This is a secret message."; //The secret message

        //Checks if encryption is working
        String encryptedText = encrypt(plainText, key);
        System.out.println("Encrypted: \n" + encryptedText);

        //Checks if decryption is working
        String decryptedText = decrypt(encryptedText, key);
        System.out.println("Decrypted: \n" + decryptedText);

        /*
        //Turns message in bytes
        byte[] kp = decryptedText.getBytes();
        //Uses Encoded KeySpec PKCS8 to turn the private key into a obj type PKCS8
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(kp);

        //Uses keyobj type PKCS8 to make the final PrivateKey
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        System.out.println(privateKey);
         */
    }
}