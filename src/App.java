import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class App {
    public static void main(String[] args) {
        // Adiciona o Bouncy Castle como provider
        Security.addProvider(new BouncyCastleProvider());
        
        // Exemplo: Gerar hash bcrypt
        String hash = org.bouncycastle.crypto.generators.OpenBSDBCrypt.generate(
            "senha123".toCharArray(),
            "salt123".getBytes(),
            8 // cost
        );
        System.out.println("Hash bcrypt: " + hash);
    }
    
}
