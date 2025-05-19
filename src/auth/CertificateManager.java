package auth;

import java.security.*;
import java.security.cert.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.asn1.misc.*;
import org.bouncycastle.operator.ContentSigner;

public class CertificateManager {
    
    public static void main(String[] args) throws Exception {
        // Assuming you already have the private key from a KeyPair
        KeyManager keyManager = new KeyManager();
        keyManager.generateAndEncryptPrivateKey("admin", "index.env");
        KeyPair keyPair = keyManager.keyPair;
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Step 1: Define the subject for the certificate
        X500Principal subject = new X500Principal("CN=User, O=Example Corp, L=City, C=US");

        // Step 2: Set certificate validity period
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + (365L * 24 * 60 * 60 * 1000)); // Valid for 1 year

        // Step 3: Generate a self-signed certificate
        X509Certificate certificate = generateSelfSignedCertificate(privateKey, publicKey, subject, startDate, endDate);

        // Step 4: Save the certificate to a file (user.crt)
        saveCertificateToFile(certificate, "user.crt");

        // Step 5: Load the certificate from the file and extract the public key
        PublicKey extractedPublicKey = loadCertificateAndGetPublicKey("user.crt");

        // Print the extracted public key
        System.out.println("Extracted Public Key: " + extractedPublicKey);
    }

    private static X509Certificate generateSelfSignedCertificate(PrivateKey privateKey, PublicKey publicKey,
                                                                  X500Principal subject, Date startDate, Date endDate) throws Exception {
        // Generate a random serial number
        SecureRandom random = new SecureRandom();
        BigInteger serialNumber = new BigInteger(130, random);

        // Create the certificate using the private key
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                subject,
                serialNumber,
                startDate,
                endDate,
                subject,
                publicKey
        );

        // Build the content signer using the private key
        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        ContentSigner contentSigner = contentSignerBuilder.build(privateKey);

        // Convert the builder into a certificate
        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));
        
        return certificate;
    }

    private static void saveCertificateToFile(X509Certificate certificate, String filename) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(certificate.getEncoded());
        }
        System.out.println("Certificate saved to: " + filename);
    }

    private static PublicKey loadCertificateAndGetPublicKey(String filename) throws Exception {
        try (FileInputStream fis = new FileInputStream(filename)) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(fis);
            return certificate.getPublicKey(); // Extract the public key
        }
    }
}