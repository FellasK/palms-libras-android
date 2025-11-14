package etec.com.tcc.palmslibras.models;


import java.io.Serializable;

public class LessonProgress implements Serializable {
    final private long id;
    final private long userId;
    final private long lessonId;
    private int xpEarned;
    private boolean completed;

    public LessonProgress(long id, long userId, long lessonId, int xpEarned, boolean completed) {
        this.id = id;
        this.userId = userId;
        this.lessonId = lessonId;
        this.xpEarned = xpEarned;
        this.completed = completed;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getLessonId() {
        return lessonId;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public boolean isCompleted() {
        return completed;
    }

    // --- Setters (Útil para atualizar o objeto em memória) ---
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }
}
