package etec.com.tcc.palmslibras.database;

import android.provider.BaseColumns;

public final class PalmsLibrasContract {

    private PalmsLibrasContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_XP = "xp";
        public static final String COLUMN_NAME_LEVEL = "level";
        public static final String COLUMN_NAME_STREAK = "streak";
    }

    public static class ActivityEntry implements BaseColumns {
        public static final String TABLE_NAME = "activities";
        public static final String COLUMN_NAME_LESSON_ID = "lesson_id";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_OPTIONS = "options"; // JSON string ex: ["A","B","C","D"]
        public static final String COLUMN_NAME_CORRECT_ANSWER = "correct_answer";
        public static final String COLUMN_NAME_XP_REWARD = "xp_reward";
    }
}