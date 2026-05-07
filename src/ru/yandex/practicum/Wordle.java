package ru.yandex.practicum;

import java.io.IOException;
import java.util.NoSuchElementException;
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
                inputWord = scanner.nextLine();

                String helpWord = wordleGame.getHelp();
                // Если пользователь ввел пустое слово, подставляем подсказку
                if (inputWord.isEmpty()) {
                    inputWord = helpWord;
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
                    wordleGame.decrementSteps();

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

        } catch (IOException e) {
            logWriter.log("IOException: " + e.getMessage());
            System.out.println("Ошибка работы с файлом: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            logWriter.log("IllegalArgumentException: " + e.getMessage());
            System.out.println("Ошибка ввода данных: " + e.getMessage());

        } catch (IllegalStateException e) {
            logWriter.log("IllegalStateException: " + e.getMessage());
            System.out.println("Ошибка состояния игры: " + e.getMessage());

        } catch (NoSuchElementException e) {
            logWriter.log("NoSuchElementException: " + e.getMessage());
            System.out.println("Ошибка: недостаточно данных: " + e.getMessage());

        } catch (Exception e) {
            logWriter.log("Непредвиденная ошибка: " + e.getMessage());
            System.out.println("Непредвиденная ошибка: " + e.getMessage());
        }
    }

}
