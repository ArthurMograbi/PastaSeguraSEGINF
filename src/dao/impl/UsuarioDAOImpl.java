package dao.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import dao.UsuarioDAO;
import model.Usuario;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {
    private final String csvFile;

    public UsuarioDAOImpl(String csvFile) {
        this.csvFile = csvFile;
        initializeCSV();
    }

    private void initializeCSV() {
        try {
            File file = new File(csvFile);
            if (!file.exists()) {
                file.createNewFile();
                // Write header if needed
                try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                    String[] header = {
                        "uid", "email", "nome", "grupoGid", "senhaPessoal",
                        "chaveSecretaTOTP", "bloqueado", "dataBloqueio", "kid"
                    };
                    writer.writeNext(header);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error initializing CSV file", e);
        }
    }

    @Override
    public void create(Usuario usuario) throws SQLException {
        List<Usuario> usuarios = readAllUsuarios();
        int maxUid = usuarios.stream()
                .mapToInt(Usuario::getUid)
                .max()
                .orElse(0);
        usuario.setUid(maxUid + 1);
        usuarios.add(usuario);
        writeAllUsuarios(usuarios);
    }

    @Override
    public Optional<Usuario> read(Integer uid) throws SQLException {
        return readAllUsuarios().stream()
                .filter(u -> u.getUid().equals(uid))
                .findFirst();
    }

    @Override
    public void update(Usuario usuario) throws SQLException {
        List<Usuario> usuarios = readAllUsuarios();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getUid().equals(usuario.getUid())) {
                usuarios.set(i, usuario);
                writeAllUsuarios(usuarios);
                return;
            }
        }
        throw new SQLException("Usuario not found with UID: " + usuario.getUid());
    }

    @Override
    public void delete(Integer uid) throws SQLException {
        List<Usuario> usuarios = readAllUsuarios();
        boolean removed = usuarios.removeIf(u -> u.getUid().equals(uid));
        if (!removed) {
            throw new SQLException("Usuario not found with UID: " + uid);
        }
        writeAllUsuarios(usuarios);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) throws SQLException {
        return readAllUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public boolean checkIfBlocked(String email) throws SQLException {
        return findByEmail(email)
                .map(Usuario::isBloqueado)
                .orElseThrow(() -> new SQLException("User not found with email: " + email));
    }

    @Override
    public void updatePassword(Integer uid, String newPasswordHash) throws SQLException {
        List<Usuario> usuarios = readAllUsuarios();
        for (Usuario u : usuarios) {
            if (u.getUid().equals(uid)) {
                u.setSenhaPessoal(newPasswordHash);
                writeAllUsuarios(usuarios);
                return;
            }
        }
        throw new SQLException("Usuario not found with UID: " + uid);
    }

    @Override
    public void updateTOTPKey(Integer uid, byte[] encryptedKey) throws SQLException {
        List<Usuario> usuarios = readAllUsuarios();
        for (Usuario u : usuarios) {
            if (u.getUid().equals(uid)) {
                u.setChaveSecretaTOTP(encryptedKey);
                writeAllUsuarios(usuarios);
                return;
            }
        }
        throw new SQLException("Usuario not found with UID: " + uid);
    }

    @Override
    public void blockUser(Integer uid) throws SQLException {
        List<Usuario> usuarios = readAllUsuarios();
        for (Usuario u : usuarios) {
            if (u.getUid().equals(uid)) {
                u.setBloqueado(true);
                u.setDataBloqueio(LocalDateTime.now());
                writeAllUsuarios(usuarios);
                return;
            }
        }
        throw new SQLException("Usuario not found with UID: " + uid);
    }

    @Override
    public void unblockUser(Integer uid) throws SQLException {
        List<Usuario> usuarios = readAllUsuarios();
        for (Usuario u : usuarios) {
            if (u.getUid().equals(uid)) {
                u.setBloqueado(false);
                u.setDataBloqueio(null);
                writeAllUsuarios(usuarios);
                return;
            }
        }
        throw new SQLException("Usuario not found with UID: " + uid);
    }

    @Override
    public List<Usuario> findAll() throws SQLException {
        return readAllUsuarios();
    }

    private List<Usuario> readAllUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            // Skip header
            if (!lines.isEmpty()) lines = lines.subList(1, lines.size());
            
            for (String[] line : lines) {
                if (line.length != 9) continue;

                Usuario u = new Usuario();
                u.setUid(parseInt(line[0]));
                u.setEmail(line[1]);
                u.setNome(line[2]);
                u.setGrupoGid(parseInt(line[3]));
                u.setSenhaPessoal(line[4]);
                u.setChaveSecretaTOTP(line[5].isEmpty() ? null : Base64.getDecoder().decode(line[5]));
                u.setBloqueado(Boolean.parseBoolean(line[6]));
                u.setDataBloqueio(line[7].isEmpty() ? null : LocalDateTime.parse(line[7]));
                u.setKid(parseInt(line[8]));
                
                usuarios.add(u);
            }
        } catch (IOException | CsvException e) {
            throw new SQLException("Error reading CSV file", e);
        }
        return usuarios;
    }

    private void writeAllUsuarios(List<Usuario> usuarios) throws SQLException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            List<String[]> lines = new ArrayList<>();
            // Write header
            lines.add(new String[]{
                "uid", "email", "nome", "grupoGid", "senhaPessoal",
                "chaveSecretaTOTP", "bloqueado", "dataBloqueio", "kid"
            });
            
            for (Usuario u : usuarios) {
                String[] line = new String[9];
                line[0] = u.getUid().toString();
                line[1] = u.getEmail();
                line[2] = u.getNome();
                line[3] = u.getGrupoGid() != null ? u.getGrupoGid().toString() : "";
                line[4] = u.getSenhaPessoal();
                line[5] = u.getChaveSecretaTOTP() != null ? 
                          Base64.getEncoder().encodeToString(u.getChaveSecretaTOTP()) : "";
                line[6] = Boolean.toString(u.isBloqueado());
                line[7] = u.getDataBloqueio() != null ? u.getDataBloqueio().toString() : "";
                line[8] = u.getKid() != null ? u.getKid().toString() : "";
                
                lines.add(line);
            }
            writer.writeAll(lines);
        } catch (IOException e) {
            throw new SQLException("Error writing to CSV file", e);
        }
    }

    private Integer parseInt(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}