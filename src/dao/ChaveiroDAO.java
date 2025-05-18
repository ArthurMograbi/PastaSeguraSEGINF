package dao;



import java.util.List;
import java.util.Optional;
import java.sql.SQLException;
import model.Chaveiro;

public interface ChaveiroDAO {
    // CRUD básico
    void create(Chaveiro chaveiro) throws SQLException;
    Optional<Chaveiro> read(Integer kid) throws SQLException;
    void update(Chaveiro chaveiro) throws SQLException;
    void delete(Integer kid) throws SQLException;

    // Métodos específicos
    List<Chaveiro> findByUid(Integer uid) throws SQLException; // Retorna todas as chaves de um usuário
    Optional<Chaveiro> findLatestByUid(Integer uid) throws SQLException; // Retorna a chave mais recente de um usuário
    void updateCertificado(Integer kid, String novoCertificado) throws SQLException;
    void updateChavePrivada(Integer kid, byte[] novaChavePrivada) throws SQLException;
}