package dao.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import dao.RegistroDAO;
import model.Registro;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegistroDAOImpl implements RegistroDAO {
    private final String csvFile;

    public RegistroDAOImpl(String csvFile) {
        this.csvFile = csvFile;
        initializeCSV();
    }

    private void initializeCSV() {
        try {
            File file = new File(csvFile);
            if (!file.exists()) {
                file.createNewFile();
                try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                    String[] header = {"rid", "uid", "mid", "dataRegistro"};
                    writer.writeNext(header);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error initializing CSV file", e);
        }
    }

    @Override
    public void create(Registro registro) throws SQLException {
        List<Registro> registros = readAllRegistros();
        int maxRid = registros.stream()
                .mapToInt(Registro::getRid)
                .max()
                .orElse(0);
        Registro newRegistro = new Registro(
            maxRid + 1,
            registro.getUid(),
            registro.getMid(),
            LocalDateTime.now()
        );
        registros.add(newRegistro);
        writeAllRegistros(registros);
    }

    @Override
    public Optional<Registro> read(Integer rid) throws SQLException {
        return readAllRegistros().stream()
                .filter(r -> r.getRid().equals(rid))
                .findFirst();
    }

    @Override
    public void update(Registro registro) throws SQLException {
        List<Registro> registros = readAllRegistros();
        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getRid().equals(registro.getRid())) {
                registros.set(i, registro);
                writeAllRegistros(registros);
                return;
            }
        }
        throw new SQLException("Registro not found with RID: " + registro.getRid());
    }

    @Override
    public void delete(Integer rid) throws SQLException {
        List<Registro> registros = readAllRegistros();
        boolean removed = registros.removeIf(r -> r.getRid().equals(rid));
        if (!removed) {
            throw new SQLException("Registro not found with RID: " + rid);
        }
        writeAllRegistros(registros);
    }

    @Override
    public List<Registro> findAll() throws SQLException {
        return readAllRegistros();
    }

    private List<Registro> readAllRegistros() throws SQLException {
        List<Registro> registros = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            if (!lines.isEmpty()) lines = lines.subList(1, lines.size());
            
            for (String[] line : lines) {
                if (line.length != 4) continue;
                
                Registro registro = new Registro(
                    Integer.parseInt(line[0]),  // rid
                    Integer.parseInt(line[1]),  // uid
                    Integer.parseInt(line[2]),  // mid
                    LocalDateTime.parse(line[3]) // dataRegistro
                );
                registros.add(registro);
            }
        } catch (IOException | CsvException e) {
            throw new SQLException("Error reading CSV file", e);
        } catch (NumberFormatException e) {
            throw new SQLException("Invalid data format in CSV", e);
        }
        return registros;
    }

    private void writeAllRegistros(List<Registro> registros) throws SQLException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            List<String[]> lines = new ArrayList<>();
            lines.add(new String[]{"rid", "uid", "mid", "dataRegistro"});
            
            for (Registro r : registros) {
                lines.add(new String[]{
                    String.valueOf(r.getRid()),
                    String.valueOf(r.getUid()),
                    String.valueOf(r.getMid()),
                    r.getDataRegistro().toString()
                });
            }
            writer.writeAll(lines);
        } catch (IOException e) {
            throw new SQLException("Error writing to CSV file", e);
        }
    }
}