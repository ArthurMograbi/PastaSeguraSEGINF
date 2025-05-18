package dao.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import dao.ChaveiroDAO;
import model.Chaveiro;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChaveiroDAOImpl implements ChaveiroDAO {
    private final String csvFile;

    public ChaveiroDAOImpl(String csvFile) {
        this.csvFile = csvFile;
        initializeCSV();
    }

    private void initializeCSV() {
        File file = new File(csvFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error creating CSV file", e);
            }
        }
    }

    @Override
    public void create(Chaveiro chaveiro) throws SQLException {
        List<Chaveiro> chaveiros = readAllChaveiros();
        int maxKid = chaveiros.stream()
                .mapToInt(Chaveiro::getKid)
                .max()
                .orElse(0);
        int newKid = maxKid + 1;
        LocalDateTime now = LocalDateTime.now();
        Chaveiro newChaveiro = new Chaveiro(newKid, chaveiro.getUid(), chaveiro.getChavePrivada(), chaveiro.getCertificado(), now);
        chaveiros.add(newChaveiro);
        writeAllChaveiros(chaveiros);
    }

    @Override
    public Optional<Chaveiro> read(Integer kid) throws SQLException {
        List<Chaveiro> chaveiros = readAllChaveiros();
        return chaveiros.stream()
                .filter(ch -> ch.getKid().equals(kid))
                .findFirst();
    }

    @Override
    public void update(Chaveiro chaveiro) throws SQLException {
        List<Chaveiro> chaveiros = readAllChaveiros();
        boolean found = false;
        for (int i = 0; i < chaveiros.size(); i++) {
            Chaveiro ch = chaveiros.get(i);
            if (ch.getKid().equals(chaveiro.getKid())) {
                chaveiros.set(i, chaveiro);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SQLException("Chaveiro not found with KID: " + chaveiro.getKid());
        }
        writeAllChaveiros(chaveiros);
    }

    @Override
    public void delete(Integer kid) throws SQLException {
        List<Chaveiro> chaveiros = readAllChaveiros();
        boolean removed = chaveiros.removeIf(ch -> ch.getKid().equals(kid));
        if (!removed) {
            throw new SQLException("Chaveiro not found with KID: " + kid);
        }
        writeAllChaveiros(chaveiros);
    }

    @Override
    public List<Chaveiro> findByUid(Integer uid) throws SQLException {
        List<Chaveiro> chaveiros = readAllChaveiros();
        return chaveiros.stream()
                .filter(ch -> ch.getUid().equals(uid))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Chaveiro> findLatestByUid(Integer uid) throws SQLException {
        List<Chaveiro> chaveiros = readAllChaveiros();
        return chaveiros.stream()
                .filter(ch -> ch.getUid().equals(uid))
                .max(Comparator.comparing(Chaveiro::getDataCriacao));
    }

    @Override
    public void updateCertificado(Integer kid, String novoCertificado) throws SQLException {
        List<Chaveiro> chaveiros = readAllChaveiros();
        for (int i = 0; i < chaveiros.size(); i++) {
            Chaveiro ch = chaveiros.get(i);
            if (ch.getKid().equals(kid)) {
                ch.setCertificado(novoCertificado);
                writeAllChaveiros(chaveiros);
                return;
            }
        }
        throw new SQLException("Chaveiro not found with KID: " + kid);
    }

    @Override
    public void updateChavePrivada(Integer kid, byte[] novaChavePrivada) throws SQLException {
        List<Chaveiro> chaveiros = readAllChaveiros();
        for (int i = 0; i < chaveiros.size(); i++) {
            Chaveiro ch = chaveiros.get(i);
            if (ch.getKid().equals(kid)) {
                ch.setChavePrivada(novaChavePrivada);
                writeAllChaveiros(chaveiros);
                return;
            }
        }
        throw new SQLException("Chaveiro not found with KID: " + kid);
    }

    private List<Chaveiro> readAllChaveiros() throws SQLException {
        List<Chaveiro> chaveiros = new ArrayList<>();
        File file = new File(csvFile);
        if (!file.exists() || file.length() == 0) {
            return chaveiros;
        }
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                if (line.length != 5) {
                    continue; // Skip invalid lines
                }
                Integer kid = Integer.parseInt(line[0]);
                Integer uid = Integer.parseInt(line[1]);
                byte[] chavePrivada = Base64.getDecoder().decode(line[2]);
                String certificado = line[3];
                LocalDateTime dataCriacao = LocalDateTime.parse(line[4]);
                Chaveiro chaveiro = new Chaveiro(kid, uid, chavePrivada, certificado, dataCriacao);
                chaveiros.add(chaveiro);
            }
        } catch (IOException | CsvException e) {
            throw new SQLException("Error reading CSV file", e);
        } catch ( IllegalArgumentException e) {
            throw new SQLException("Error parsing CSV data", e);
        }
        return chaveiros;
    }

    private void writeAllChaveiros(List<Chaveiro> chaveiros) throws SQLException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            List<String[]> lines = new ArrayList<>();
            for (Chaveiro ch : chaveiros) {
                String kid = String.valueOf(ch.getKid());
                String uid = String.valueOf(ch.getUid());
                String chavePrivada = Base64.getEncoder().encodeToString(ch.getChavePrivada());
                String certificado = ch.getCertificado();
                String dataCriacao = ch.getDataCriacao().toString();
                String[] line = {kid, uid, chavePrivada, certificado, dataCriacao};
                lines.add(line);
            }
            writer.writeAll(lines);
        } catch (IOException e) {
            throw new SQLException("Error writing to CSV file", e);
        }
    }
}