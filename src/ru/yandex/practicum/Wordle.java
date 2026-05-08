package ru.yandex.practicum;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

/*
     главный класс, содержит метод main().
     В нём мы готовим всё, что нужно для игры, создаём лог-файл,
     создаём загрузчик словарей, загружаем словарь.
     Здесь же мы создаём класс игры WordleGame и опрашиваем пользователя.
     В общем, главный класс задаёт общий ход работы программы.
 */
public class Wordle {

    public static void main(String[] args) {

        LogWriter logWriter = new LogWriter("Gamelog.txt");
        try {
            logWriter.log("Игровая сессия начинается:");
            WordleDictionaryLoader wdl = new WordleDictionaryLoader("words_ru.txt", logWriter);
            WordleDictionary wordDictionary = new WordleDictionary(wdl.getWords(), logWriter);

            WordleGame wordleGame = new WordleGame(wordDictionary.getRandomWord(wordDictionary.getAllWords()), wordDictionary, logWriter);

            boolean match = false;
            Scanner scanner = new Scanner(System.in);
            String inputWord;

            System.out.println("Игра начинается, загадано слово из 5 букв. Попробуй угадай!");

            while (!match && wordleGame.getSteps() > 0) {
                System.out.println("Введите слово из 5 букв (или просто нажмите Enter для подсказки):");
                inputWord = WordleDictionary.normalizeWord(scanner.nextLine());

                // Если пользователь ввел пустое слово, подставляем подсказку
                if (inputWord.isEmpty()) {
                    inputWord = wordleGame.getHelp();
                    System.out.println(inputWord + " - Выбран подсказочный вариант");
                }
                try {
                    wordDictionary.checkWordInDictionary(WordleDictionary.normalizeWord(inputWord));
                } catch (WordNotFoundInDictionaryException e) {
                    // Записываем в лог и на консоль, но не уменьшаем шаги и продолжаем цикл!
                    logWriter.log("Игрок ввёл некорректное слово: " + e.getMessage());
                    System.out.println("Слово должно состоять из 5 букв кириллицы, попробуйте ввести другое слово");
                    continue; // Переходим на следующую итерацию цикла, запрашиваем ввод заново
                }

                if (wordleGame.checkAnswer(inputWord)) {
                    match = true;
                    System.out.println("Поздравляю! Вы угадали слово: " + inputWord);
                } else {
                    // уменьшаем попытки
                    try {
                        wordleGame.decrementSteps();
                    } catch (AttemptsAreOverException e) {
                        logWriter.log("AttemptsAreOverException: " + e.getMessage());
                        System.out.println("Попытки закончились.");
                        break; // выходим из while
                    }
                    // добавляем слово во множество использованных
                    wordleGame.addRepeatWordSet(inputWord);
                    // обновляем маску по введенному слову
                    wordleGame.setWordMask(inputWord);
                    String mask = wordleGame.getWordMask();

                    // обрабатываем слово по маске и фильтруем словари
                    wordleGame.wordHandler(mask, inputWord);
                    wordleGame.filterWords();
                    System.out.println(mask);
                    System.out.println("Слово не угадано. Осталось попыток: " + wordleGame.getSteps());
                    System.out.println("В слове есть но в других местах: " + wordleGame.getYellowChars() + ". В слове нет букв: " + wordleGame.getGreyChars());
                }
            }
            if (!match) System.out.println("проиграно");
            logWriter.log("Игровая сессия закончилась.");
            logWriter.log("-----------------------------------------------------------------------------");

        } catch (EmptyDictionaryException e) {
            logWriter.log("EmptyDictionaryException: " + e.getMessage());
            System.out.println("Словарь пуст или не содержит подходящих слов: " + e.getMessage());

        } catch (InvalidGameConfigException e) {
            logWriter.log("InvalidGameConfigException: " + e.getMessage());
            System.out.println("Ошибка конфигурации игры: " + e.getMessage());

        } catch (InvalidWordFormatException e) {
            logWriter.log("InvalidWordFormatException: " + e.getMessage());
            System.out.println("Ошибка формата слова: " + e.getMessage());

        } catch (DictionaryFileException e) {
            logWriter.log("DictionaryFileException: " + e.getMessage());
            System.out.println("Ошибка файла: " + e.getMessage());

        } catch (DictionaryReadException e) {
            logWriter.log("DictionaryReadException: " + e.getMessage());
            System.out.println("Ошибка чтения файла: " + e.getMessage());

        } catch (Exception e) {
            logWriter.log("Exception: " + e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());

        } catch (Throwable t) {
            StringWriter stringWriter = new StringWriter();
            t.printStackTrace(new PrintWriter(stringWriter));
            logWriter.log("Непредвиденная ошибка:\n" + stringWriter);
            System.out.println("Непредвиденная ошибка");
        }
    }

}
