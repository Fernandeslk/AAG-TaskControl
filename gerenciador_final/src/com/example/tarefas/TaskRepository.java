package com.example.tarefas;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class TaskRepository {
    private final Path file;

    public TaskRepository(String filename) {
        this.file = Paths.get(filename);
        try {
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar arquivo: " + e.getMessage(), e);
        }
    }

    public synchronized List<Task> loadAll() {
        try (BufferedReader br = Files.newBufferedReader(file)) {
            return br.lines()
                    .map(Task::fromCsvLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + e.getMessage(), e);
        }
    }

    public synchronized void saveAll(List<Task> tasks) {
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Task t : tasks) {
                bw.write(t.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage(), e);
        }
    }

    public Path getPath() {
        return file;
    }
}
