package etec.com.tcc.palmslibras.models;

public class Activity {
    private long id;
    private int lessonId;
    private String question;
    private String optionsJson; // Opções armazenadas como uma string JSON: "[\"A\",\"B\",\"C\"]"
    private String correctAnswer;
    private int xpReward;

    public Activity(long id, int lessonId, String question, String optionsJson, String correctAnswer, int xpReward) {
        this.id = id;
        this.lessonId = lessonId;
        this.question = question;
        this.optionsJson = optionsJson;
        this.correctAnswer = correctAnswer;
        this.xpReward = xpReward;
    }

    // Getters
    public long getId() { return id; }
    public int getLessonId() { return lessonId; }
    public String getQuestion() { return question; }
    public String getOptionsJson() { return optionsJson; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getXpReward() { return xpReward; }
}