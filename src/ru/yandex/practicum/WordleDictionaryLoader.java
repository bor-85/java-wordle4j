package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private final String filename;
    private final LogWriter logWriter;

    public WordleDictionaryLoader(String filename, LogWriter logWriter) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Конструктор WordleDictionaryLoader: Имя файла не должно быть пустым");
        }
        if (logWriter == null) {
            throw new IllegalArgumentException("Конструктор WordleDictionaryLoader: LogWriter не должен быть null");
        }
        File file = new File(filename);
        if (!file.exists()) {
            logWriter.log("Конструктор WordleDictionaryLoader: Файл словаря не найден " + filename);
            throw new IllegalArgumentException("Файл словаря не найден: " + filename);
        }
        if (!file.isFile()) {
            logWriter.log("Конструктор WordleDictionaryLoader: По указанному пути находится не файл " + filename);
            throw new IllegalArgumentException("По указанному пути находится не файл: " + filename);
        }
        this.filename = filename;
        this.logWriter = logWriter;
        logWriter.log("Загрузчик словаря создан.");
    }

    public List<String> getWords() throws IOException {
        List<String> list = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                line = WordleDictionary.normalizeWord(line);
                if (line.length() == WordleDictionary.WORD_LENGTH) {
                    list.add(line);
                }
            }
        }
        logWriter.log("Словарь успешно прогружен, всего слов: " + list.size());
        return list;

    }

}
