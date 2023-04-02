package it.winter2223.bachelor.ak.backend.commentEmotionAssignment.model;

import java.util.Arrays;
import java.util.function.Predicate;

public enum Emotion {
    ANGER, FEAR, JOY, LOVE, SADNESS, SURPRISE, UNSPECIFIABLE;

    public static boolean contains(String value) {
        return Arrays.stream(values()).map(Enum::name).anyMatch(Predicate.isEqual(value));
    }
}
