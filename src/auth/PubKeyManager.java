package auth;
import java.security.cert.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;


public class PubKeyManager {
    public static java.security.PublicKey RecoverPK(String filename) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        BufferedInputStream bis = new BufferedInputStream(fis);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        Certificate cert = cf.generateCertificate(bis);

        java.security.PublicKey publicKey = cert.getPublicKey();
        return publicKey;
    }

    public static void main(String[] args) throws Exception {
        java.security.PublicKey encryptedText = RecoverPK("C:/Users/arthu/Downloads/Pacote-T3/Pacote-T4/Keys/admin-x509.crt");
        System.out.println("Encrypted: \n" + encryptedText);
    }
}