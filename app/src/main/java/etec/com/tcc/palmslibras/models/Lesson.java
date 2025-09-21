package etec.com.tcc.palmslibras.models;

import java.io.Serializable;
import java.util.List;

public class Lesson implements Serializable {

    public enum LessonType {
        QUESTION_ANSWER,
        MEMORY_GAME
    }

    private LessonType type;
    private List<Gesture> options;
    private Gesture correctAnswer;
    private List<Gesture> memoryPairs;

    public LessonType getType() {
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

    public static Lesson createQaLesson(List<Gesture> options, Gesture correctAnswer) {
        Lesson lesson = new Lesson();
        lesson.type = LessonType.QUESTION_ANSWER;
        lesson.options = options;
        lesson.correctAnswer = correctAnswer;
        return lesson;
    }

    public static Lesson createMemoryLesson(List<Gesture> pairs) {
        Lesson lesson = new Lesson();
        lesson.type = LessonType.MEMORY_GAME;
        lesson.memoryPairs = pairs;
        return lesson;
    }
}