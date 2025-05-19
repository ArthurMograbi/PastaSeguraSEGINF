package auth;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.spec.GCMParameterSpec;

public class KeyManager {

private static final String AES_ALGORITHM = "AES";
private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"; // Usando GCM para maior segurança
private static final String KEY_GENERATION_ALGORITHM = "PBKDF2WithHmacSHA256";
private static final String PRNG_ALGORITHM = "SHA1PRNG";
private static final int KEY_SIZE = 256;
private static final int IV_SIZE = 12; // Tamanho do IV para GCM
private static final int TAG_BIT_LENGTH = 128; // Tamanho da tag de autenticação para GCM
private static final int SALT_LENGTH = 16;
private static final int ITERATION_COUNT = 65536;

public static void generateAndEncryptPrivateKey(String password, String outputFilePath) throws Exception {
    // 1. Gerar um par de chaves RSA
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    PrivateKey privateKey = keyPair.getPrivate();

    // 2. Obter a representação PKCS#8 da chave privada e codificá-la em Base64
    byte[] privateKeyEncoded = privateKey.getEncoded();
    String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKeyEncoded);

    // 3. Gerar o salt aleatório
    SecureRandom secureRandom = SecureRandom.getInstanceStrong();
    byte[] salt = new byte[SALT_LENGTH];
    secureRandom.nextBytes(salt);

    // 4. Derivar a chave AES de 256 bits usando PBKDF2
    SecretKey secretKey = generateKeyFromPassword(password, salt);

    // 5. Gerar um Initialization Vector (IV) aleatório para o modo GCM
    byte[] iv = new byte[IV_SIZE];
    secureRandom.nextBytes(iv);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);

    // 6. Inicializar o Cipher para criptografia
    Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

    // 7. Criptografar a chave privada codificada em Base64
    byte[] encryptedPrivateKey = cipher.doFinal(privateKeyBase64.getBytes(StandardCharsets.UTF_8));

    // 8. Salvar o salt, o IV e a chave privada criptografada no arquivo
    try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
        fos.write(salt);
        fos.write(iv);
        fos.write(encryptedPrivateKey);
    }
    System.out.println("Chave privada criptografada e salva em: " + outputFilePath);
}

public static PrivateKey decryptAndRestorePrivateKey(String password, String inputFilePath) throws Exception {
    // 1. Ler o salt, o IV e a chave privada criptografada do arquivo
    byte[] salt = new byte[SALT_LENGTH];
    byte[] iv = new byte[IV_SIZE];
    byte[] encryptedPrivateKey;

    try (FileInputStream fis = new FileInputStream(inputFilePath)) {
        if (fis.read(salt) != SALT_LENGTH || fis.read(iv) != IV_SIZE) {
            throw new IOException("Arquivo de chave privada corrompido.");
        }
        encryptedPrivateKey = fis.readAllBytes();
    }

    // 2. Derivar a chave AES de 256 bits usando a senha e o salt lidos
    SecretKey secretKey = generateKeyFromPassword(password, salt);

    // 3. Inicializar o Cipher para descriptografia
    Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
    cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_BIT_LENGTH, iv));

    // 4. Descriptografar a chave privada
    byte[] decryptedPrivateKeyBase64Bytes = cipher.doFinal(encryptedPrivateKey);
    String decryptedPrivateKeyBase64 = new String(decryptedPrivateKeyBase64Bytes, StandardCharsets.UTF_8);

    // 5. Decodificar a chave privada de Base64 para o formato PKCS#8
    byte[] decryptedPrivateKeyBytes = Base64.getDecoder().decode(decryptedPrivateKeyBase64);

    // 6. Restaurar a chave privada a partir da representação PKCS#8
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedPrivateKeyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(keySpec);
}

private static SecretKey generateKeyFromPassword(String password, byte[] salt) throws Exception {
    javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance(KEY_GENERATION_ALGORITHM);
    javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATION_COUNT,
            KEY_SIZE
    );
    return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AES_ALGORITHM);
}

public static void main(String[] args) {
    String password = "minhaSenhaSecreta"; // Substitua pela senha do usuário
    String outputFilePath = "privateKey.enc";
    String inputFilePath = "privateKey.enc"; // Usando o mesmo arquivo para demonstração

    try {
        // Gerar, criptografar e salvar a chave privada
        generateAndEncryptPrivateKey(password, outputFilePath);

        // Descriptografar e restaurar a chave privada
        PrivateKey restoredPrivateKey = decryptAndRestorePrivateKey(password, inputFilePath);
        System.out.println("\nChave privada restaurada com sucesso!");
        System.out.println("Algoritmo da chave privada: " + restoredPrivateKey.getAlgorithm());
        System.out.println("Formato da chave privada: " + restoredPrivateKey.getFormat());

    } catch (Exception e) {
        System.err.println("Ocorreu um erro: " + e.getMessage());
        e.printStackTrace();
    }
}
}