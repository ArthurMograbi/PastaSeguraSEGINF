package dao.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import dao.MensagemDAO;
import model.Mensagem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MensagemDAOImpl implements MensagemDAO {
    private final String csvFile;

    public MensagemDAOImpl(String csvFile) {
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
                    String[] header = {"mid", "codigo", "mensagem"};
                    writer.writeNext(header);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error initializing CSV file", e);
        }
    }

    @Override
    public void create(Mensagem mensagem) throws SQLException {
        List<Mensagem> mensagens = readAllMensagens();
        int maxMid = mensagens.stream()
                .mapToInt(Mensagem::getMid)
                .max()
                .orElse(0);
        mensagem.setMid(maxMid + 1);
        mensagens.add(mensagem);
        writeAllMensagens(mensagens);
    }

    @Override
    public Optional<Mensagem> read(Integer mid) throws SQLException {
        return readAllMensagens().stream()
                .filter(m -> m.getMid().equals(mid))
                .findFirst();
    }

    @Override
    public void update(Mensagem mensagem) throws SQLException {
        List<Mensagem> mensagens = readAllMensagens();
        boolean found = false;
        for (int i = 0; i < mensagens.size(); i++) {
            if (mensagens.get(i).getMid().equals(mensagem.getMid())) {
                mensagens.set(i, mensagem);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("Mensagem not found with MID: " + mensagem.getMid());
        }
        writeAllMensagens(mensagens);
    }

    @Override
    public void delete(Integer mid) throws SQLException {
        List<Mensagem> mensagens = readAllMensagens();
        boolean removed = mensagens.removeIf(m -> m.getMid().equals(mid));
        if (!removed) {
            throw new SQLException("Mensagem not found with MID: " + mid);
        }
        writeAllMensagens(mensagens);
    }

    @Override
    public Optional<Mensagem> findByCodigo(Integer codigo) throws SQLException {
        return readAllMensagens().stream()
                .filter(m -> m.getCodigo().equals(codigo))
                .findFirst();
    }

    @Override
    public List<Mensagem> findAll() throws SQLException {
        return readAllMensagens();
    }

    private List<Mensagem> readAllMensagens() throws SQLException {
        List<Mensagem> mensagens = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            // Skip header
            if (!lines.isEmpty()) lines = lines.subList(1, lines.size());
            
            for (String[] line : lines) {
                if (line.length != 3) continue;
                
                Mensagem m = new Mensagem(
                    Integer.parseInt(line[0]),  // mid
                    Integer.parseInt(line[1]),  // codigo
                    line[2]                      // mensagem
                );
                mensagens.add(m);
            }
        } catch (IOException | CsvException e) {
            throw new SQLException("Error reading CSV file", e);
        } catch (NumberFormatException e) {
            throw new SQLException("Error parsing CSV data", e);
        }
        return mensagens;
    }

    private void writeAllMensagens(List<Mensagem> mensagens) throws SQLException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            List<String[]> lines = new ArrayList<>();
            // Write header
            lines.add(new String[]{"mid", "codigo", "mensagem"});
            
            for (Mensagem m : mensagens) {
                String[] line = {
                    String.valueOf(m.getMid()),
                    String.valueOf(m.getCodigo()),
                    m.getMensagem()
                };
                lines.add(line);
            }
            writer.writeAll(lines);
        } catch (IOException e) {
            throw new SQLException("Error writing to CSV file", e);
        }
    }
}