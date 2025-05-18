package dao;

import model.Grupo;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;

public interface GrupoDAO {
    // CRUD básico
    void create(Grupo grupo) throws SQLException;
    Optional<Grupo> read(Integer gid) throws SQLException;
    void update(Grupo grupo) throws SQLException;
    void delete(Integer gid) throws SQLException;
    
    // Métodos específicos
    Optional<Grupo> findByNomeGrupo(String nomeGrupo) throws SQLException;
    List<Grupo> findAll() throws SQLException;
}