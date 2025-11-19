package etec.com.tcc.palmslibras.models;

public class Course {
    private String title;
    private String description;
    private int iconResId;
    private int lessonId; // ID da lição que este card irá iniciar
    private boolean enabled = true;
    private int backgroundColorResId;

    public Course(String title, String description, int iconResId, int lessonId) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.lessonId = lessonId;
    }

    public Course(String title, String description, int iconResId, int lessonId, boolean enabled) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.lessonId = lessonId;
        this.enabled = enabled;
    }

    public Course(String title, String description, int iconResId, int lessonId, boolean enabled, int backgroundColorResId) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.lessonId = lessonId;
        this.enabled = enabled;
        this.backgroundColorResId = backgroundColorResId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
    public int getLessonId() { return lessonId; }
    public boolean isEnabled() { return enabled; }
    public int getBackgroundColorResId() { return backgroundColorResId; }
}