package ru.yandex.practicum;

import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */

public class WordleDictionary {
    protected static final int WORD_LENGTH = 5;
    protected static final String DEFAULT_MASK = "-----";
    private final LogWriter logWriter;
    private final List<String> allWords;
    private final Random random = new Random();


    public WordleDictionary(List<String> words, LogWriter logWriter) {
        if (words == null) {
            throw new InvalidGameConfigException("Конструктор WordleDictionary: Словарь не должен быть null");
        }
        if (logWriter == null) {
            throw new InvalidGameConfigException("Конструктор WordleDictionary: LogWriter не должен быть null");
        }
        this.allWords = words;
        this.logWriter = logWriter;
        logWriter.log("Словарь WordleDictionary создан: " + allWords.size() + " слов.");
    }

    //получение слова случайным образом из списка
    public String getRandomWord(List<String> words) throws EmptyDictionaryException {

        if (words == null) {
            throw new InvalidWordFormatException("Список не должен быть null");
        }
        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Невозможно сгенерировать слово, пустой список.");
        }

        String word = words.get(random.nextInt(words.size()));
        logWriter.log("Метод getRandomWord выбрал слово '" + word + "' из " + words.size() + " слов.");

        return word;

    }

    //Метод, приводящий слово к нужному виду
    public static String normalizeWord(String word) {
        if (word == null) {
            throw new InvalidWordFormatException("Метод normalizeWord: введено пустое слово");
        }
        return word.trim().toLowerCase().replace("ё", "е");
    }

    //метод, проверяющий присутствие слова в базовом словаре игры
    public boolean checkWordInDictionary(String word) throws WordNotFoundInDictionaryException {
        if (word == null) {
            throw new InvalidWordFormatException("Метод checkWordInDictionary: введено пустое слово");
        }
        boolean check = allWords.contains(word);
        if (!check) {
            throw new WordNotFoundInDictionaryException("Слово '" + word + "' отсутствует в словаре игры");
        }
        logWriter.log("Метод checkWordInDictionary проверил слово '" + word + "' - " + check);
        return check;
    }

    public String getCompareMask(String word, String answer) {
        if (word == null || answer == null || word.length() != WORD_LENGTH || answer.length() != WORD_LENGTH) {
            throw new InvalidWordFormatException("Параметры метода getCompareMask должны иметь длину " + WORD_LENGTH
                    + " символов");
        }

        StringBuilder compareMask = new StringBuilder(DEFAULT_MASK);
        char[] answerChars = answer.toCharArray();
        char[] wordChars = word.toCharArray();

        boolean[] used = new boolean[WORD_LENGTH]; // для учета использованных букв в answer

        // 1. Сначала отмечаем точные совпадения
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (wordChars[i] == answerChars[i]) {
                compareMask.setCharAt(i, '+');
                used[i] = true;
            }
        }

        // 2. Потом ищем совпадения по букве, но на другой позиции
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (compareMask.charAt(i) == '+') {
                continue;
            }

            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!used[j] && wordChars[i] == answerChars[j]) {
                    compareMask.setCharAt(i, '^');
                    used[j] = true;
                    break;
                }
            }
        }
        logWriter.log("Метод getCompareMask сформировал маску " +
                compareMask + " для слова '" + word +
                "' при загаданном слове '" + answer + "'");
        return compareMask.toString();

    }

    public List<String> getAllWords() {
        return allWords;
    }
}
