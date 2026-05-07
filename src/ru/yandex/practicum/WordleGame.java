package ru.yandex.practicum;

import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {
    //Константа - количество попыток
    private final int COUNT_OF_ATTEMPTS = 6;
    //переменная для загаданного слова
    private final String answer;
    //переменная для попыток
    private int steps;
    //исходный словарь
    private final WordleDictionary dictionary;
    //переменная для хранения позиции и буквы, которая была угадана(зеленые буквы "+")
    private Map<Integer, Character> greenChars = new HashMap<>();
    //множество для хранения позиции и буквы, которые присутствуют в слове, но на другой позиции(желтые буквы "^")
    private Map<Integer, Character> yellowChars = new HashMap<>();
    //множество для хранения букв, которые осутствуют в слове(серые буквы "-")
    private Set<Character> greyChars = new HashSet<>();
    //множество для хранения слов, введенных игроком или выданных в качестве подсказки
    private Set<String> repeatWordSet = new HashSet<>();
    //переменная для хранения маски слова
    private String wordMask = "-----";
    //отфильтрованный словарь, состоящий из слов, подходящих под маску и корректируемый по ходу игры
    private List<String> filteredWords;

    private final LogWriter logWriter;

    public WordleGame(String answer, WordleDictionary dictionary, LogWriter logWriter) {
        if (answer == null || answer.length() != WordleDictionary.WORD_LENGTH) {
            throw new IllegalArgumentException("Конструктор WordleGame: Некорректное загаданное слово");
        }
        if (dictionary == null) {
            throw new IllegalArgumentException("Конструктор WordleGame: Словарь не должен быть null");
        }
        if (logWriter == null) {
            throw new IllegalArgumentException("Конструктор WordleGame: LogWriter не должен быть null");
        }
        this.answer = answer;
        this.steps = COUNT_OF_ATTEMPTS;
        this.dictionary = dictionary;
        this.filteredWords = new ArrayList<>(dictionary.getAllWords());
        this.logWriter = logWriter;
        logWriter.log("Игровая сессия создана");
        logWriter.log("Загадано слово: '" + answer + "'");
        logWriter.log("Количество попыток - " + COUNT_OF_ATTEMPTS);
        logWriter.log("Количество слов в словаре - " + dictionary.getAllWords().size());
    }

    public List<String> getFilteredWords() {
        return filteredWords;
    }

    public Set<Character> getGreyChars() {
        return greyChars;
    }

    //метод, который распределяет буквы по множествам для дальнейшей фильтрации слов
    public void wordHandler(String mask, String word) {
        if (mask == null || word == null) {
            throw new IllegalArgumentException("Метод wordHandler: mask или word == null");
        }
        if (mask.length() != word.length()) {
            throw new IllegalArgumentException("Метод wordHandler: длины mask и word не совпадают");
        }

        logWriter.log("Метод wordHandler обрабатывает слово '" + word + "' по маске " + mask);

        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            char maskChar = mask.charAt(i);
            char letter = word.charAt(i);

            switch (maskChar) {
                case '+':
                    // Зеленая буква - правильная позиция
                    greenChars.put(i, letter);
                    logWriter.log("Зафиксирована зеленая буква(+) '" + letter + "' на позиции " + i);
                    break;

                case '^':
                    // Желтая буква - есть в слове, но на другом месте
                    // Записываем позицию (где не должна находиться буква) и саму букву
                    yellowChars.put(i, letter);
                    logWriter.log("Зафиксирована желтая буква(^) '" + letter + "' на позиции " + i);
                    break;

                case '-':
                    // Буква отсутствует
                    if (!greenChars.containsValue(letter) && !yellowChars.containsValue(letter)) {
                        greyChars.add(letter);
                        logWriter.log("Зафиксирована серая буква(-) '" + letter + "' на позиции " + i);
                    }
                    break;
            }
        }
        logWriter.log("Список зеленых букв(+):" + greenChars.toString());
        logWriter.log("Список желтых букв(^):" + yellowChars.toString());
        logWriter.log("Список серых букв(-):" + greyChars.toString());
        logWriter.log("Метод wordHandler завершил работу");
    }

    //метод фильтрует список слов по данным из исходной коллекции словаря по мере заполнения коллекций букв
    public void filterWords() {
        if (filteredWords == null) {
            throw new IllegalStateException("Метод filterWords: Список слов не инициализирован");
        }
        logWriter.log("Метод filterWords начинает работу. Количество слов в словаре до фильтрации: "
                + filteredWords.size());

        List<String> newfilteredWords = new ArrayList<>();
        for (String word : filteredWords) {
            boolean isValid = true;
            //System.out.println(word);
            // Проверка зеленых букв
            for (Map.Entry<Integer, Character> entry : greenChars.entrySet()) {
                int pos = entry.getKey();
                char ch = entry.getValue();
                if (pos >= word.length() || word.charAt(pos) != ch) {
                    //System.out.println(" - Не подходит по зеленой букве: " + ch + " на позиции " + pos);
                    isValid = false;
                    break;
                }
            }
            if (!isValid) continue;

            // Проверка желтых букв — они должны быть в слове, но не на указанных позициях
            for (Map.Entry<Integer, Character> entry : yellowChars.entrySet()) {
                int badPos = entry.getKey();
                char c = entry.getValue();
                // Буква должна быть в слове
                if (!word.contains(String.valueOf(c))) {
                    isValid = false;
                    break;
                }
                // Буква не должна быть на "запрещенной" позиции
                if (word.charAt(badPos) == c) {
                    isValid = false;
                    break;
                }
            }
            if (!isValid) continue;

            // Проверка серых букв
            for (char c : greyChars) {
                if (word.indexOf(c) != -1) {
                    isValid = false;
                    break;
                }
            }

            if (isValid) {
                newfilteredWords.add(word);
            }
        }

        // Убираем слова, уже использованные
        newfilteredWords.removeAll(repeatWordSet);
        filteredWords = newfilteredWords;
        logWriter.log("Метод filterWords завершает работу. Количество слов в словаре после фильтрации: "
                + filteredWords.size());
    }

    //получаем подсказку из словаря отфильтрованнных слов
    public String getHelp() {
        if (filteredWords == null || filteredWords.isEmpty()) {
            throw new NoSuchElementException("Метод getHelp: Нет доступных слов для подсказки");
        }
        String advise = dictionary.getRandomWord(filteredWords);
        logWriter.log("Метод getHelp выдает слово-подсказку '" + advise + "' из " + filteredWords.size() + " слов.");
        return advise;
    }

    public int getSteps() {
        return steps;
    }

    //уменьшение счетчика попыток
    public void decrementSteps() {
        if (steps <= 0) {
            throw new IllegalStateException("Попытки закончились. Количество меньше 0.");
        }
        steps--;
        logWriter.log("Метод decrementSteps уменьшает количество попыток до " + steps);
    }

    //проверка слова на совпадение с ответом
    public boolean checkAnswer(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Метод checkAnswer. Слово не должно быть null");
        }
        boolean check = word.equals(answer);
        logWriter.log("Метод checkAnswer проверил слово '" + word + "' при загаданном слове '" + answer
                + "' - " + check);
        return check;
    }

    public String getWordMask() {
        return wordMask;
    }

    //метод обновляет маску после ввода слова
    public void setWordMask(String word) {
        if (word == null || word.length() != WordleDictionary.WORD_LENGTH) {
            throw new IllegalArgumentException("Метод setWordMask: Введено пустое слово или длина слова не равна 5");
        }
        wordMask = dictionary.getCompareMask(word, this.answer);
        logWriter.log("Метод setWordMask обновил маску для слова '" + word + "' при загаданном слове '"
                + answer + "' - " + wordMask);
    }

    //добавление слова в исключение, чтобы оно не участвовало в дальнейших ходах в виде подсказки
    public void addRepeatWordSet(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("Метод addRepeatWordSet: Cлово не должно быть пустым");
        }
        logWriter.log("Метод addRepeatWordSet добавил в исключение слово '" + word + "'.");
        repeatWordSet.add(word);
    }

    public Map<Integer, Character> getYellowChars() {
        return yellowChars;
    }

    public Map<Integer, Character> getGreenChars() {
        return greenChars;
    }
}
