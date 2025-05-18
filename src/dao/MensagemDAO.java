package dao;
import model.Mensagem;

import java.util.List;
import java.util.Optional;
import java.sql.SQLException;

public interface MensagemDAO {
    // CRUD básico
    void create(Mensagem mensagem) throws SQLException;
    Optional<Mensagem> read(Integer mid) throws SQLException;
    void update(Mensagem mensagem) throws SQLException;
    void delete(Integer mid) throws SQLException;
    
    // Métodos específicos
    Optional<Mensagem> findByCodigo(Integer codigo) throws SQLException;
    List<Mensagem> findAll() throws SQLException;
}