package dao;
import model.Registro;

import java.util.List;
import java.util.Optional;
import java.sql.SQLException;


public interface RegistroDAO {

    //Basic CRUD
    void create(Registro registro) throws SQLException;
    Optional<Registro> read(Integer rid) throws SQLException;
    void update(Registro registro) throws SQLException;
    void delete(Integer rid) throws SQLException;
    

    List<Registro> findAll() throws SQLException;
}
