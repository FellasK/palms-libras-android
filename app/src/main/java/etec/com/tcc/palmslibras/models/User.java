package etec.com.tcc.palmslibras.models;

public class User {
    private long id;
    private String name;
    private String email;
    private int xp;
    private int level;
    private int streak;

    public User(long id, String name, String email, int xp, int level, int streak) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.xp = xp;
        this.level = level;
        this.streak = streak;
    }

    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getXp() { return xp; }
    public int getLevel() { return level; }
    public int getStreak() { return streak; }
}