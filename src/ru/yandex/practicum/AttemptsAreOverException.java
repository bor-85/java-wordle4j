package ru.yandex.practicum;

public class AttemptsAreOverException extends Exception {
    public AttemptsAreOverException(String message) {
        super(message);
    }
}
