package etec.com.tcc.palmslibras.models;

public class Course {
    private String title;
    private String description;
    private int iconResId;
    private int lessonId; // ID da lição que este card irá iniciar

    public Course(String title, String description, int iconResId, int lessonId) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.lessonId = lessonId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
    public int getLessonId() { return lessonId; }
}