package etec.com.tcc.palmslibras.models;

import java.io.Serializable;
import java.util.List;

public class Exercices implements Serializable {

    public enum ActivityDataType {
        QUESTION_ANSWER,
        MEMORY_GAME,
        CONNECT_GAME,
        INSTRUCTION,
        CAMERA_EXERCISE
    }

    private ActivityDataType type;
    private List<Gesture> options;
    private Gesture correctAnswer;
    private List<Gesture> memoryPairs;

    public ActivityDataType getType() {
        return type;
    }

    public List<Gesture> getOptions() {
        return options;
    }

    public Gesture getCorrectAnswer() {
        return correctAnswer;
    }

    public List<Gesture> getMemoryPairs() {
        return memoryPairs;
    }

    public static Exercices createQaLesson(List<Gesture> options, Gesture correctAnswer) {
        Exercices exercices = new Exercices();
        exercices.type = ActivityDataType.QUESTION_ANSWER;
        exercices.options = options;
        exercices.correctAnswer = correctAnswer;
        return exercices;
    }

    public static Exercices createMemoryLesson(List<Gesture> pairs) {
        Exercices exercices = new Exercices();
        exercices.type = ActivityDataType.MEMORY_GAME;
        exercices.memoryPairs = pairs;
        return exercices;
    }

    public static Exercices createConnectLesson(List<Gesture> options) {
        Exercices exercices = new Exercices();
        exercices.type = ActivityDataType.CONNECT_GAME;
        exercices.options = options; // O jogo de conectar usa as opções como pares
        return exercices;
    }

    public static Exercices createInstruction(Gesture gesture) {
        Exercices exercices = new Exercices();
        exercices.type = ActivityDataType.INSTRUCTION;
        exercices.correctAnswer = gesture;
        return exercices;
    }

    public static Exercices createCameraExercise(Gesture gesture) {
        Exercices exercices = new Exercices();
        exercices.type = ActivityDataType.CAMERA_EXERCISE;
        exercices.correctAnswer = gesture;
        return exercices;
    }
}