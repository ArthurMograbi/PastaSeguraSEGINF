package auth;

import java.security.PrivateKey;
import java.security.KeyFactory;
import java.security.spec.*;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


import javax.crypto.KeyGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.Key;
import java.security.SecureRandom;

public class DecryptAES {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    //Encryption
    public static String encrypt(String plainText, String key) throws Exception {
        byte[] seed = key.getBytes();
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(secureRandom);
        Key k = keyGen.generateKey();
        System.out.println("[PK][1]: \n" + k);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    //Decryption
    public static String decrypt(String encryptedText, String key) throws Exception {
        byte[] seed = key.getBytes();
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(secureRandom);
        Key k = keyGen.generateKey();
        System.out.println("[PK][2]: \n" + k);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    public static String readFile(String filePath) throws Exception {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line);
            }
        }
        return contentBuilder.toString();
    }

    public static void main(String[] args) throws Exception {
        //Take user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter password: ");
        String key = scanner.nextLine();

        String plainText =  DecryptAES.readFile("C:/Users/arthu/Downloads/Pacote-T3/Pacote-T4/Keys/admin-pkcs8-aes.key"); //The secret message

        //Checks if encryption is working
        String encryptedText = encrypt(plainText, key);
        System.out.println("Encrypted: \n" + encryptedText);

        //Checks if decryption is working
        String decryptedText = decrypt(plainText, key);
        System.out.println("Decrypted: \n" + decryptedText);
    }
}