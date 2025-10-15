package com.gustavo.finansync.service;

import com.gustavo.finansync.dto.TransactionDTO;
import com.gustavo.finansync.entity.TransactionType;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParsingService {
    /*public List<TransactionDTO> parseCsv(InputStream csvInputStream) throws Exception {
        List<TransactionDTO> transactions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(csvInputStream, "UTF-8"))) {
            String[] line;
            int lineNumber = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            // Pular as 5 primeiras linhas (4 metadados + 1 cabeçalho)
            while (lineNumber < 5 && (line = reader.readNext()) != null) {
                lineNumber++;
            }
            // Agora, processar as transações
            while ((line = reader.readNext()) != null) {
                if (line.length < 5) continue; // Linha inválida
                // Campos: Data Lançamento;Histórico;Descrição;Valor;Saldo
                String dataStr = line[0].trim();
                String historico = line[1].trim();
                String descricao = line[2].trim();
                String valorStr = line[3].replace(".", "").replace(",", ".").trim(); // Corrige separador decimal
                // Monta a descrição final
                String fullDescription = historico + " - " + descricao;
                // Data
                LocalDate date = LocalDate.parse(dataStr, formatter);
                // Valor
                BigDecimal amount = new BigDecimal(valorStr);
                // Tipo: crédito se valor > 0, débito se < 0
                TransactionType type = amount.signum() >= 0 ? TransactionType.RECEITA : TransactionType.DESPESA;
                transactions.add(new TransactionDTO(
                        null, // id
                        fullDescription,
                        amount,
                        date,
                        type
                ));
            }
        }
        return transactions;
    }*/
    public List<TransactionDTO> parseCsv(InputStream csvInputStream) throws Exception {
        List<TransactionDTO> transactions = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream, StandardCharsets.UTF_8));
        String line;
        int lineNumber = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            // Pule as 5 primeiras linhas (4 metadados + 1 cabeçalho)
            if (lineNumber <= 6) continue;
            if (line.trim().isEmpty()) continue;
            String[] fields = line.split(";");
            if (fields.length < 5) continue;
            try {
                String dataStr = fields[0].trim();
                String historico = fields[1].trim();
                String descricao = fields[2].trim();
                String valorStr = fields[3].replace(".", "").replace(",", ".").trim();
                String fullDescription = historico + " - " + descricao;
                LocalDate date = LocalDate.parse(dataStr, formatter);
                BigDecimal amount = new BigDecimal(valorStr);
                TransactionType type = amount.signum() >= 0 ? TransactionType.RECEITA : TransactionType.DESPESA;
                transactions.add(new TransactionDTO(
                        null, // id
                        fullDescription,
                        amount,
                        date,
                        type
                ));
            } catch (Exception e) {
                System.out.println("Erro ao processar linha: " + line);
                e.printStackTrace();
            }
        }
        System.out.println("Total de transações parseadas: " + transactions.size());
        return transactions;
    }
}
