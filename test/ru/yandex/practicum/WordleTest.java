package ru.yandex.practicum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    static WordleDictionary dictionary;
    static WordleGame wordleGame;
    static List<String> testWords;
    static LogWriter logWriter;
    @BeforeAll
    static void prepareTest() {
        logWriter = new LogWriter("TestLog.txt");
        testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        dictionary = new WordleDictionary(testWords, logWriter);
        wordleGame = new WordleGame("карта", dictionary, logWriter);
    }

    //Тесты WordleDictionary
    @Test
    //normalizeWord должен корректно нормализовать слово
    void shouldNormalizeWord() {
        Assertions.assertEquals("сосна", WordleDictionary.normalizeWord("СосНА"));
        Assertions.assertEquals("козел", WordleDictionary.normalizeWord("козёл"));
        Assertions.assertEquals("карта", WordleDictionary.normalizeWord(" КАРТА "));
    }

    //normalizeWord должен выбрасывать исключение если входное слово null
    @Test
    void normalizeWordShouldThrowIfNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> WordleDictionary.normalizeWord(null));
    }

    //checkWordInDictionary должен находить слово в словаре если оно есть
    @Test
    void checkWordInDictionaryShouldReturnTrueIfExists() {
        Assertions.assertTrue(dictionary.checkWordInDictionary("карта"));
    }

    //checkWordInDictionary должен выбрасывать WordNotFoundInDictionaryException
    @Test
    void shouldThrowWordNotFoundInDictionaryException() {
        Assertions.assertThrows(WordNotFoundInDictionaryException.class,
                () -> dictionary.checkWordInDictionary("zxcvb"));
    }

    //checkWordInDictionary не должен выбрасывать исключение для существующего слова
    @Test
    void shouldNotThrowExceptionForWordThatExists() {
        Assertions.assertDoesNotThrow(() -> dictionary.checkWordInDictionary("карта"));
    }

    //getRandomWord должен выбрасывать исключение если входное слово null
    @Test
    void getRandomWordShouldThrowIfNullList() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> dictionary.getRandomWord(null));
    }

    //getRandomWord должен выбрасывать исключение если словарь пуст
    @Test
    void getRandomWordShouldThrowIfEmptyList() {
        Assertions.assertThrows(NoSuchElementException.class,
                () -> dictionary.getRandomWord(Collections.emptyList()));
    }

    //getRandomWord должен корректно выдавать слово из списка
    @Test
    void getRandomWordShouldReturnElementFromList() {
        String testWord = dictionary.getRandomWord(testWords);
        Assertions.assertTrue(testWords.contains(testWord), "Слово должно быть из исходного списка");
    }

    //getRandomWord должен корректно выдавать слово из списка c 1 элементом
    @Test
    void getRandomWordShouldReturnSameWordIfSingleElementList() {
        List<String> words = List.of("карта");
        Assertions.assertEquals("карта", dictionary.getRandomWord(words));
    }

    //getCompareMask должен корректно выдавать маску, для обычного слова
    @Test
    void getCompareMaskAllMatchShouldBeUsualWord() {
        Assertions.assertEquals("--^^-", dictionary.getCompareMask("карта", "метро"));
    }

    //getCompareMask должен корректно выдавать маску, если все буквы совпадают
    @Test
    void getCompareMaskAllMatchShouldBeAllPluses() {
        Assertions.assertEquals("+++++", dictionary.getCompareMask("карта", "карта"));
    }

    //getCompareMask должен корректно выдавать маску, если все буквы не совпадают
    @Test
    void getCompareMaskNoLettersShouldBeAllDashes() {
        Assertions.assertEquals("-----", dictionary.getCompareMask("весна", "пирог")); // нет общих букв
    }

    //getCompareMask должен корректно выдавать маску, если все буквы находятся не на своих местах
    @Test
    void getCompareMaskShouldMarkMisplacedLettersWithCarets() {
        Assertions.assertEquals("^^^^^", dictionary.getCompareMask("абвгд", "бвгда"));
    }

    //getCompareMask должен корректно выдавать маску, если встречаются дубли букв
    @Test
    void getCompareMaskShouldHandleDuplicateLettersCorrectly() {
        // проверяем, что лишние повторы не помечаются '^' сверх количества в answer
        // answer: "аабба"
        // word : "aaaaa", маска должна быть "++--+". (позиции 0,1,4 совпали, на 2,3 'a' лишние)
        Assertions.assertEquals("++--+", dictionary.getCompareMask("ааааа", "аабба"));
    }

    //getCompareMask должен выбрасывать исключение если слово неправильной длины
    @Test
    void getCompareMaskShouldThrowIfWrongLength() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> dictionary.getCompareMask("кот", "карта"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> dictionary.getCompareMask("карта", "кот"));
    }

    //getCompareMask должен выбрасывать исключение если входные слова пусты
    @Test
    void getCompareMaskShouldThrowIfNullArgs() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> dictionary.getCompareMask(null, "карта"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> dictionary.getCompareMask("карта", null));
    }

    //getAllWords должен возвращать правильный список
    @Test
    void getAllWordsShouldReturnSameReferenceList() {
        List<String> list = dictionary.getAllWords();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(4, list.size());
    }


    //Тесты WordleDictionaryLoader
    //WordleDictionaryLoader должен выбрасывать исключение вместо файла null
    @Test
    void constructorShouldThrowWhenFilenameNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new WordleDictionaryLoader(null, logWriter));
    }

    //WordleDictionaryLoader должен выбрасывать исключение если имя файла пустое
    @Test
    void constructorShouldThrowWhenFilenameBlank() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new WordleDictionaryLoader("", logWriter));
    }

    //Тесты на WordleGame

    //checkAnswer должен выбрасывать исключение если имя файла null
    @Test
    void checkAnswerShouldThrowWhenNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> wordleGame.checkAnswer(null));
    }

    //checkAnswer должен выдавать true на правильное слово
    @Test
    void checkAnswerShouldReturnTrueForCorrectWord() {
        //загадано "карта"
        Assertions.assertTrue(wordleGame.checkAnswer("карта"));
    }

    //checkAnswer должен выдавать false на неправильное слово
    @Test
    void checkAnswerShouldReturnFalseForWrongWord() {
        //загадано "карта"
        Assertions.assertFalse(wordleGame.checkAnswer("пирог"));
    }

    //decrementSteps должен уменьшать число попыток на 1
    @Test
    void decrementStepsShouldDecreaseSteps() {
        //изначальное число выставлено 6
        wordleGame.decrementSteps();
        Assertions.assertEquals(5, wordleGame.getSteps());
    }

    //decrementSteps должен выбрасывать исключение, если  количество шагов <=0
    @Test
    void decrementStepsShouldThrowWhenStepsAlreadyZero() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame Game = new WordleGame("карта", dictionary, logWriter);
        // доводим до 0
        for (int i = 0; i < 6; i++) Game.decrementSteps();
        Assertions.assertEquals(0, Game.getSteps());

        Assertions.assertThrows(IllegalStateException.class, ()->Game.decrementSteps());
    }

    //setWordMask должен выбрасывать исключение, если  входное слово null
    @Test
    void setWordMaskShouldThrowWhenNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> wordleGame.setWordMask(null));
    }

    //setWordMask должен выбрасывать исключение, если  входное слово не соответствует длине 5
    @Test
    void setWordMaskShouldThrowWhenWrongLength() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> wordleGame.setWordMask("кот"));
    }

    //setWordMask должен изменять маску слова
    @Test
    void setWordMaskShouldUpdateMask() {
        wordleGame.setWordMask("карта");
        Assertions.assertEquals("+++++", wordleGame.getWordMask());
    }

    //setWordMask должен корректно обновить маску для слова
    @Test
    void setWordMaskShouldUpdateMaskForWord() {
        // answer = "карта", word = "парка"
        // должен обновить маску на "-++^+"
        wordleGame.setWordMask("парка");
        String mask = wordleGame.getWordMask();
        Assertions.assertEquals("-++^+",mask);
    }

    //addRepeatWordSet должен выбрасывать исключение, если  входное слово null
    @Test
    void addRepeatWordSetShouldThrowWhenNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> wordleGame.addRepeatWordSet(null));
    }

    //addRepeatWordSet должен выбрасывать исключение, если  входное слово пустое
    @Test
    void addRepeatWordSetShouldThrowWhenEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> wordleGame.addRepeatWordSet(""));
    }

    //wordHandler должен выбрасывать исключение, если  входное слово null
    @Test
    void wordHandlerShouldThrowWhenNullArgs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> wordleGame.wordHandler(null, "карта"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> wordleGame.wordHandler("+++++", null));
    }

    //wordHandler должен выбрасывать исключение, если  входное слово неправильной длины
    @Test
    void wordHandlerShouldThrowWhenDifferentLengths() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> wordleGame.wordHandler("+++++", "кар"));
    }

    //wordHandler должен добавлять несовпавшие буквы в множество серых букв
    @Test
    void wordHandlerShouldFillGreyCharsInSet() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame Game = new WordleGame("карта", dictionary, logWriter);
        // mask "-----" для слова "пирог" => все буквы серые
        Game.wordHandler("-----", "пирог");
        Set<Character> grey = Game.getGreyChars();

        Assertions.assertTrue(grey.contains('п'));
        Assertions.assertTrue(grey.contains('и'));
        Assertions.assertTrue(grey.contains('р'));
        Assertions.assertTrue(grey.contains('о'));
        Assertions.assertTrue(grey.contains('г'));
    }

    //wordHandler должен добавлять коллекцию буквы в множество зеленых букв
    @Test
    void wordHandlerShouldFillGreenCharsInMap() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame Game = new WordleGame("карта", dictionary, logWriter);
        // mask "+++++" для слова "пирог" => все буквы зеленые
        Game.wordHandler("+++++", "пирог");
        Map<Integer, Character> green = Game.getGreenChars();

        Assertions.assertTrue(green.containsValue('п'));
        Assertions.assertTrue(green.containsValue('и'));
        Assertions.assertTrue(green.containsValue('р'));
        Assertions.assertTrue(green.containsValue('о'));
        Assertions.assertTrue(green.containsValue('г'));
    }

    //wordHandler должен добавлять совпавшие желтые буквы в коллекцию желтых букв
    @Test
    void wordHandlerShouldFillYellowCharsInMap() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame Game = new WordleGame("карта", dictionary, logWriter);
        // mask "^^^^^" для слова "пирог" => все буквы желтые
        Game.wordHandler("^^^^^", "пирог");
        Map<Integer, Character> yellow = Game.getYellowChars();

        Assertions.assertTrue(yellow.containsValue('п'));
        Assertions.assertTrue(yellow.containsValue('и'));
        Assertions.assertTrue(yellow.containsValue('р'));
        Assertions.assertTrue(yellow.containsValue('о'));
        Assertions.assertTrue(yellow.containsValue('г'));
    }

    // wordHandler должен добавить каждую букву в свою коллекцию
    @Test
    void wordHandlerShouldFillAllTypes() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame Game = new WordleGame("карта", dictionary, logWriter);
        // mask "+^^--" для слова "пирог" => все буквы желтые
        Game.wordHandler("+^^--", "пирог");
        Map<Integer, Character> green = Game.getGreenChars();
        Map<Integer, Character> yellow = Game.getYellowChars();
        Set<Character> grey = Game.getGreyChars();

        Assertions.assertTrue(green.containsValue('п'));
        Assertions.assertTrue(yellow.containsValue('и'));
        Assertions.assertTrue(yellow.containsValue('р'));
        Assertions.assertTrue(grey.contains('о'));
        Assertions.assertTrue(grey.contains('г'));
    }

    // filterWords должен все слова и оставить только подходящие под маску
    @Test
    void filterWordsShouldKeepOnlyWordsMatchingGreenConstraints() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame game = new WordleGame("карта", dictionary, logWriter);
        // Закрепим зеленые: "карта" первые две буквы "к","а" правильные
        game.wordHandler("++---", "каxxx");

        game.filterWords();

        for (String w : game.getFilteredWords()) {
            Assertions.assertEquals('к', w.charAt(0));
            Assertions.assertEquals('а', w.charAt(1));
        }
    }

    // filterWords должен убрать слова с серыми буквами
    @Test
    void filterWordsShouldExcludeWordsContainingGreyLetters() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame game = new WordleGame("карта", dictionary, logWriter);
        // серые буквы из слова "пирог": п,и,р,о,г
        game.wordHandler("-----", "пирог");
        game.filterWords();

        List<String> filtered = game.getFilteredWords();

        // "пирог" точно должен исчезнуть, и любые слова с 'п','и','р','о','г' тоже
        assertFalse(filtered.contains("пирог"));
        for (String str : filtered) {
            assertFalse(str.contains("п"));
            assertFalse(str.contains("и"));
            assertFalse(str.contains("р"));
            assertFalse(str.contains("о"));
            assertFalse(str.contains("г"));
        }
    }

    @Test
    void filterWordsShouldRemoveRepeatedWordsEvenIfTheyMatchConstraints() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "каппа", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame game = new WordleGame("пирог", dictionary, logWriter);

        // добавим в repeat слово, которое при этом подходит
        game.addRepeatWordSet("каппа");

        game.filterWords();

        assertFalse(game.getFilteredWords().contains("каппа"),
                "Слово из repeatWordSet должно удаляться из подсказок/фильтра");
    }

    // getFilteredWords должен выдать исключение, если не осталось слов
    @Test
    void getHelpShouldThrowWhenNoWordsLeft() {
        // искусственно сужаем до пустого: добавим в повтор все слова и отфильтруем
        for (String w : new ArrayList<>(wordleGame.getFilteredWords())) {
            wordleGame.addRepeatWordSet(w);
        }
        wordleGame.filterWords();

        Assertions.assertThrows(NoSuchElementException.class,()-> wordleGame.getHelp());
    }

    // getHelp должен вернуть подсказку
    @Test
    void getHelpShouldReturnWordFromFilteredWords() {
        LogWriter logWriter = new LogWriter("TestLog.txt");
        List<String> testWords = new ArrayList<>(Arrays.asList("карта", "пирог", "метро", "весна"));
        WordleDictionary dictionary = new WordleDictionary(testWords, logWriter);
        WordleGame game = new WordleGame("карта", dictionary, logWriter);
        String help = game.getHelp();
        Assertions.assertNotNull(help);
        Assertions.assertTrue(game.getFilteredWords().contains(help));
    }

}

