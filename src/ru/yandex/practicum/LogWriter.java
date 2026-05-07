package ru.yandex.practicum;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class LogWriter {

    private final String filename;

    public LogWriter(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Конструктор LogWriter: Имя файла не должно быть пустым");
        }
        this.filename = filename;
    }

    public void log(String message) {
        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(
                        new FileWriter(filename, true)))) {

            String timestamp = LocalDateTime.now().withNano(0).toString();
            writer.println(timestamp + " - " + message);

        } catch (IOException e) {
            System.err.println("Ошибка записи в лог: " + e.getMessage());
        }
    }
}