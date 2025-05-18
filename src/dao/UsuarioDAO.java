package dao;
import model.Usuario;

import java.util.List;
import java.util.Optional;
import java.sql.SQLException;

public interface UsuarioDAO {

    // CRUD básico usando Usuario.java
    void create(Usuario usuario) throws SQLException;
    Optional<Usuario> read(Integer uid) throws SQLException;
    void update(Usuario usuario) throws SQLException;
    void delete(Integer uid) throws SQLException;
    
    // Métodos específicos
    Optional<Usuario> findByEmail(String email) throws SQLException;
    boolean checkIfBlocked(String email) throws SQLException;
    void updatePassword(Integer uid, String newPasswordHash) throws SQLException;
    void updateTOTPKey(Integer uid, byte[] encryptedKey) throws SQLException;
    void blockUser(Integer uid) throws SQLException;
    void unblockUser(Integer uid) throws SQLException;
    
    List<Usuario> findAll() throws SQLException;
}