package it.nlp.backend.migration.service;

import java.util.Arrays;
import java.util.function.Predicate;

public enum Emotion {
    ANGER, FEAR, JOY, LOVE, SADNESS, SURPRISE, UNSPECIFIABLE;

    public static boolean contains(String value) {
        return Arrays.stream(values()).map(Enum::name).anyMatch(Predicate.isEqual(value));
    }
}
