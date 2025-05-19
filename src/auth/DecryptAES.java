package auth;


import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import java.security.Key;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyFactory;
import java.util.Base64;

public class DecryptAES {

    public static void saveKeyToFile_s(String textToSave, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(textToSave);
            System.out.println("String saved to file successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred while saving the string to the file: " + e.getMessage());
        }
    }

    public static void saveKeyToFile_k(Key key, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            fos.write(key.getEncoded());
        }
    }

    //Encryption
    public static void encrypt(String key, String FilePath) throws Exception {
        byte[] seed = key.getBytes();
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(secureRandom);
        Key k = keyGen.generateKey();

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, k);

        byte[] encoded = cipher.doFinal(k.getEncoded());

        Path filePath = Paths.get("admin.key");

        String base64Encoded = Base64.getEncoder().encodeToString(encoded);
        Files.write(filePath, base64Encoded.getBytes());
    }

    //Decryption
    public static void decrypt(String key, String FilePath) throws Exception {
        byte[] seed = key.getBytes();
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(secureRandom);
        Key k = keyGen.generateKey();

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, k);

        byte[] keycontent = Files.readAllBytes(Paths.get(FilePath));

        byte[] decoded = cipher.doFinal(keycontent);

        Path filePath = Paths.get("D_admin.key");

        String base64Decoded = Base64.getEncoder().encodeToString(decoded);
        Files.write(filePath, base64Decoded.getBytes());
    }

    public static PrivateKey getPrivateKey(String filePath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));

        // Decode the key if it's stored in Base64 format
        // This depends on how you saved the decrypted key.
        // If it's raw bytes, you can skip this decoding.
        //byte[] decodedKeyBytes = Base64.getDecoder().decode(keyBytes);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC"); // Or "EC", "DSA", depending on your key algorithm
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static void main(String[] args) throws Exception {
        //Take user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter password: ");
        String key = scanner.nextLine();

        //Checks if encryption is working
        encrypt(key, "admin.key");

        //Checks if decryption is working
        //decrypt(key, "admin.key");

        PrivateKey privateKey = getPrivateKey("admin.key"); 
        System.out.println("Private Key generated: " + privateKey);
    }
}