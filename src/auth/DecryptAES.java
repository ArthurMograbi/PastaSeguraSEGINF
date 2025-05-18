package auth;

import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class DecryptAES {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    //Encryption
    public static String encrypt(String plainText, String key) throws Exception {
        DecryptAES.printBytes(key.getBytes());
        SecretKeySpec secretKey = new SecretKeySpec(padKey(key), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    //Decryption
    public static String decrypt(String encryptedText, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(padKey(key), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    public static byte[] padKey(String key) {
        int keyPadding = 16 - (key.length() % 16);
        byte[] paddedKey = new byte[key.length() + keyPadding];
        for (int index = 0; index < paddedKey.length; index++) {
           if(index>= key.length()){
                paddedKey[index] = 0;
            } else {
                paddedKey[index] = (byte) key.charAt(index);
            }
        }
        return paddedKey;
    }

    public static void printBytes(byte[] bytes) {
        for (int index = 0; index < bytes.length; index++) {
            System.out.print(bytes[index] + " ");
        }
        System.out.println();
    }

    public static String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static void main(String[] args) throws Exception {
        //Take user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter password: ");
        String key = scanner.nextLine();
        scanner.close();
        

        String plainText = DecryptAES.readFile("C:/Users/arthu/Downloads/Pacote-T3/Pacote-T4/Keys/admin-pkcs8-aes.key"); //The secret message

        //Checks if encryption is working
        String encryptedText = encrypt(plainText, key);
        System.out.println("Encrypted: \n" + encryptedText);

        //Checks if decryption is working
        String decryptedText = decrypt(encryptedText, key);
        System.out.println("Decrypted: \n" + decryptedText);

        //Turns message in bytes
        byte[] kp = decryptedText.getBytes();
        //Uses Encoded KeySpec PKCS8 to turn the private key into a obj type PKCS8
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(kp);

        //Uses keyobj type PKCS8 to make the final PrivateKey
        ASN1InputStream bin = new ASN1InputStream(new ByteArrayInputStream(spec.getEncoded()));
        PrivateKeyInfo pki = PrivateKeyInfo.getInstance(bin.readObject());
        String algOid = pki.getPrivateKeyAlgorithm().getAlgorithm().getId();
        KeyFactory.getInstance(algOid).generatePrivate(spec);
        bin.close();
         
    }
}