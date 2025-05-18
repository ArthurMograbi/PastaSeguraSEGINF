package dao.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import dao.GrupoDAO;
import model.Grupo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GrupoDAOImpl implements GrupoDAO {
    private final String csvFile;

    public GrupoDAOImpl(String csvFile) {
        this.csvFile = csvFile;
        initializeCSV();
    }

    private void initializeCSV() {
        try {
            File file = new File(csvFile);
            if (!file.exists()) {
                file.createNewFile();
                // Write header
                try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                    String[] header = {"gid", "nomeGrupo"};
                    writer.writeNext(header);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error initializing CSV file", e);
        }
    }

    @Override
    public void create(Grupo grupo) throws SQLException {
        List<Grupo> grupos = readAllGrupos();
        int maxGid = grupos.stream()
                .mapToInt(Grupo::getGid)
                .max()
                .orElse(0);
        grupo.setGid(maxGid + 1);
        grupos.add(grupo);
        writeAllGrupos(grupos);
    }

    @Override
    public Optional<Grupo> read(Integer gid) throws SQLException {
        return readAllGrupos().stream()
                .filter(g -> g.getGid().equals(gid))
                .findFirst();
    }

    @Override
    public void update(Grupo grupo) throws SQLException {
        List<Grupo> grupos = readAllGrupos();
        boolean found = false;
        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).getGid().equals(grupo.getGid())) {
                grupos.set(i, grupo);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("Grupo not found with GID: " + grupo.getGid());
        }
        writeAllGrupos(grupos);
    }

    @Override
    public void delete(Integer gid) throws SQLException {
        List<Grupo> grupos = readAllGrupos();
        boolean removed = grupos.removeIf(g -> g.getGid().equals(gid));
        if (!removed) {
            throw new SQLException("Grupo not found with GID: " + gid);
        }
        writeAllGrupos(grupos);
    }

    @Override
    public Optional<Grupo> findByNomeGrupo(String nomeGrupo) throws SQLException {
        return readAllGrupos().stream()
                .filter(g -> g.getNomeGrupo().equalsIgnoreCase(nomeGrupo))
                .findFirst();
    }

    @Override
    public List<Grupo> findAll() throws SQLException {
        return readAllGrupos();
    }

    private List<Grupo> readAllGrupos() throws SQLException {
        List<Grupo> grupos = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            // Skip header
            if (!lines.isEmpty()) lines = lines.subList(1, lines.size());
            
            for (String[] line : lines) {
                if (line.length != 2) continue;
                
                Grupo grupo = new Grupo(
                    Integer.parseInt(line[0]),  // gid
                    line[1]                      // nomeGrupo
                );
                grupos.add(grupo);
            }
        } catch (IOException | CsvException e) {
            throw new SQLException("Error reading CSV file", e);
        } catch (NumberFormatException e) {
            throw new SQLException("Error parsing CSV data", e);
        }
        return grupos;
    }

    private void writeAllGrupos(List<Grupo> grupos) throws SQLException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            List<String[]> lines = new ArrayList<>();
            // Write header
            lines.add(new String[]{"gid", "nomeGrupo"});
            
            for (Grupo g : grupos) {
                String[] line = {
                    String.valueOf(g.getGid()),
                    g.getNomeGrupo()
                };
                lines.add(line);
            }
            writer.writeAll(lines);
        } catch (IOException e) {
            throw new SQLException("Error writing to CSV file", e);
        }
    }
}