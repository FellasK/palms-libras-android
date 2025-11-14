package etec.com.tcc.palmslibras.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

import etec.com.tcc.palmslibras.models.Activity;
import etec.com.tcc.palmslibras.models.Lesson;
import etec.com.tcc.palmslibras.models.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION =2;
    public static final String DATABASE_NAME = "PalmsLibras.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL para criar a tabela de usuários
        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + PalmsLibrasContract.UserEntry.TABLE_NAME + " (" +
                PalmsLibrasContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL + " TEXT NOT NULL UNIQUE," +
                PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD_HASH + " TEXT NOT NULL," +
                PalmsLibrasContract.UserEntry.COLUMN_NAME_XP + " INTEGER DEFAULT 0," +
                PalmsLibrasContract.UserEntry.COLUMN_NAME_LEVEL + " INTEGER DEFAULT 1," +
                PalmsLibrasContract.UserEntry.COLUMN_NAME_STREAK + " INTEGER DEFAULT 0)";

        // SQL para criar a tabela de atividades
        final String SQL_CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + PalmsLibrasContract.ActivityEntry.TABLE_NAME + " (" +
                PalmsLibrasContract.ActivityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PalmsLibrasContract.ActivityEntry.COLUMN_NAME_LESSON_ID + " INTEGER NOT NULL," +
                PalmsLibrasContract.ActivityEntry.COLUMN_NAME_QUESTION + " TEXT NOT NULL," +
                PalmsLibrasContract.ActivityEntry.COLUMN_NAME_OPTIONS + " TEXT NOT NULL," +
                PalmsLibrasContract.ActivityEntry.COLUMN_NAME_CORRECT_ANSWER + " TEXT NOT NULL," +
                PalmsLibrasContract.ActivityEntry.COLUMN_NAME_XP_REWARD + " INTEGER NOT NULL)";

        final String SQL_CREATE_LESSONS_TABLE = "CREATE TABLE " + PalmsLibrasContract.LessonEntry.TABLE_NAME + " (" +
                PalmsLibrasContract.LessonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PalmsLibrasContract.LessonEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                PalmsLibrasContract.LessonEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                PalmsLibrasContract.LessonEntry.COLUMN_NAME_ORDER_INDEX + " INTEGER UNIQUE)";

        // SQL para criar a tabela de progresso das lições (LessonProgress)
        // Adicionando chaves estrangeiras (FOREIGN KEY) para User e Lesson para integridade referencial.
        final String SQL_CREATE_LESSON_PROGRESS_TABLE = "CREATE TABLE " + PalmsLibrasContract.LessonProgressEntry.TABLE_NAME + " (" +
                PalmsLibrasContract.LessonProgressEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PalmsLibrasContract.LessonProgressEntry.COLUMN_NAME_USER_ID + " INTEGER NOT NULL," +
                PalmsLibrasContract.LessonProgressEntry.COLUMN_NAME_LESSON_ID + " INTEGER NOT NULL," +
                PalmsLibrasContract.LessonProgressEntry.COLUMN_NAME_XP_EARNED + " INTEGER DEFAULT 0," +
                PalmsLibrasContract.LessonProgressEntry.COLUMN_NAME_COMPLETED + " INTEGER DEFAULT 0," + // 0=false, 1=true
                " UNIQUE (" + PalmsLibrasContract.LessonProgressEntry.COLUMN_NAME_USER_ID + ", " + PalmsLibrasContract.LessonProgressEntry.COLUMN_NAME_LESSON_ID + ")," +
                " FOREIGN KEY (" + PalmsLibrasContract.LessonProgressEntry.COLUMN_NAME_USER_ID + ") REFERENCES " +
                PalmsLibrasContract.UserEntry.TABLE_NAME + " (" + PalmsLibrasContract.UserEntry._ID + ")," +
                " FOREIGN KEY (" + PalmsLibrasContract.LessonProgressEntry.COLUMN_NAME_LESSON_ID + ") REFERENCES " +
                PalmsLibrasContract.LessonEntry.TABLE_NAME + " (" + PalmsLibrasContract.LessonEntry._ID + "))";


        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_ACTIVITIES_TABLE);
        db.execSQL(SQL_CREATE_LESSONS_TABLE);
        db.execSQL(SQL_CREATE_LESSON_PROGRESS_TABLE);

        populateInitialData(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Adiciona a nova coluna 'activity_type' com valor padrão 'multiple_choice'
            db.execSQL("ALTER TABLE " + PalmsLibrasContract.ActivityEntry.TABLE_NAME +
                    " ADD COLUMN " + PalmsLibrasContract.ActivityEntry.COLUMN_NAME_TYPE +
                    " TEXT DEFAULT 'multiple_choice'");

            // Atualiza todas as atividades já existentes para terem tipo 'multiple_choice'
            db.execSQL("UPDATE " + PalmsLibrasContract.ActivityEntry.TABLE_NAME +
                    " SET " + PalmsLibrasContract.ActivityEntry.COLUMN_NAME_TYPE + " = 'multiple_choice'");
        }
    }



    private void populateInitialData(SQLiteDatabase db) {
        // Lição 1: Alfabeto
        // Atualizado para incluir o campo 'activity_type' nas atividades.
        // Esse campo permite distinguir diferentes tipos de questões no futuro (ex: múltipla escolha, vídeo, texto).
        // Todas as atividades atuais são do tipo 'multiple_choice.

        addActivity(db, 1, "Qual é a forma correta de fazer \"A\" em Libras?", "[\"A\",\"B\",\"C\",\"D\"]", "A", 10, "multiple_choice");
        addActivity(db, 1, "Qual é a forma correta de fazer \"E\" em Libras?", "[\"E\",\"I\",\"O\",\"U\"]", "E", 10, "multiple_choice");
        addActivity(db, 1, "Qual é a forma correta de fazer \"I\" em Libras?", "[\"Y\",\"I\",\"J\",\"K\"]", "I", 15, "multiple_choice");

// Lição 2: Números
        addActivity(db, 2, "Qual sinal representa o número \"1\"?", "[\"1\",\"2\",\"3\",\"4\"]", "1", 10, "multiple_choice");
        addActivity(db, 2, "Qual sinal representa o número \"5\"?", "[\"3\",\"4\",\"5\",\"6\"]", "5", 10, "multiple_choice");


        // Adicione usuários de exemplo para o ranking
        addUser(db, "Maria", "maria@email.com", "123", 1250);
        addUser(db, "João", "joao@email.com", "123", 980);
        addUser(db, "Ana", "ana@email.com", "123", 750);
    }

    private void addActivity(SQLiteDatabase db, int lessonId, String question, String options, String answer, int xp, String type) {
        ContentValues values = new ContentValues();
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_LESSON_ID, lessonId);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_QUESTION, question);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_OPTIONS, options);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_CORRECT_ANSWER, answer);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_XP_REWARD, xp);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_TYPE, type);
        db.insert(PalmsLibrasContract.ActivityEntry.TABLE_NAME, null, values);
    }

    private void addUser(SQLiteDatabase db, String name, String email, String password, int xp) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);
        ContentValues values = new ContentValues();
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME, name);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL, email);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD_HASH, hashedPassword);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_SALT, salt);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_XP, xp);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_LEVEL, 1);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_STREAK, 0);
        db.insert(PalmsLibrasContract.UserEntry.TABLE_NAME, null, values);
    }

    public long addUser(String name, String email, String password) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME, name);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL, email);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD_HASH, hashedPassword);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_SALT, salt);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_LEVEL, 1);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_STREAK, 0);
        long id = db.insert(PalmsLibrasContract.UserEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(PalmsLibrasContract.UserEntry.TABLE_NAME,
                new String[]{
                        PalmsLibrasContract.UserEntry._ID,
                        PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME,
                        PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL,
                        PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD_HASH, // Novo
                        PalmsLibrasContract.UserEntry.COLUMN_NAME_SALT,           // Novo
                        PalmsLibrasContract.UserEntry.COLUMN_NAME_XP,
                        PalmsLibrasContract.UserEntry.COLUMN_NAME_LEVEL,
                        PalmsLibrasContract.UserEntry.COLUMN_NAME_STREAK
                },
                PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Extrai o Hash e o Salt do banco
            String storedHash = cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD_HASH));
            // Nota: BCrypt combina o salt e o hash em uma única string, mas ter o salt na coluna pode ser redundante se você usar BCrypt.checkpw

            // Verifica a senha fornecida (password) com o Hash armazenado (storedHash)
            // BCrypt.checkpw : extrai o salt do storedHash, hasheia a senha fornecida com ele e compara os resultados.
            if (BCrypt.checkpw(password, storedHash)) {
                // Senha correta: constrói e retorna o objeto User
                user = new User(
                        cursor.getLong(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_XP)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_LEVEL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_STREAK))
                );
            }
        }

        if (cursor != null) cursor.close();
        db.close();
        return user; // Retorna User se a senha estiver correta, ou null se não estiver ou o email não existir.
    }

    public User getUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PalmsLibrasContract.UserEntry.TABLE_NAME, null,
                PalmsLibrasContract.UserEntry._ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_XP)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_LEVEL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_STREAK))
            );
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    public List<User> getAllUsersSortedByXP() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PalmsLibrasContract.UserEntry.TABLE_NAME, null, null, null, null, null, PalmsLibrasContract.UserEntry.COLUMN_NAME_XP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                userList.add(new User(
                        cursor.getLong(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_XP)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_LEVEL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.UserEntry.COLUMN_NAME_STREAK))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    public void updateUserXp(long userId, int xpToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();
        User currentUser = getUserById(userId);
        if (currentUser != null) {
            ContentValues values = new ContentValues();
            values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_XP, currentUser.getXp() + xpToAdd);
            db.update(PalmsLibrasContract.UserEntry.TABLE_NAME, values, PalmsLibrasContract.UserEntry._ID + " = ?", new String[]{String.valueOf(userId)});
        }
        db.close();
    }

    public List<Activity> getActivitiesForLesson(int lessonId) {
        List<Activity> activityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PalmsLibrasContract.ActivityEntry.TABLE_NAME, null,
                PalmsLibrasContract.ActivityEntry.COLUMN_NAME_LESSON_ID + " = ?",
                new String[]{String.valueOf(lessonId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                activityList.add(new Activity(
                        cursor.getLong(cursor.getColumnIndexOrThrow(PalmsLibrasContract.ActivityEntry._ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_LESSON_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_QUESTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_OPTIONS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_CORRECT_ANSWER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_XP_REWARD))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activityList;
    }


    // Funções de consulta
    private long addLesson(SQLiteDatabase db, String title, String description, int orderIndex) {
        ContentValues values = new ContentValues();
        values.put(PalmsLibrasContract.LessonEntry.COLUMN_NAME_TITLE, title);
        values.put(PalmsLibrasContract.LessonEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(PalmsLibrasContract.LessonEntry.COLUMN_NAME_ORDER_INDEX, orderIndex);
        return db.insert(PalmsLibrasContract.LessonEntry.TABLE_NAME, null, values);
    }

    // --- Funções de Consulta Faltantes ---

    public List<Lesson> getAllLessons() {
        List<Lesson> lessonList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Ordena pelo ORDER_INDEX para exibir na sequência correta
        Cursor cursor = db.query(PalmsLibrasContract.LessonEntry.TABLE_NAME, null, null, null, null, null, PalmsLibrasContract.LessonEntry.COLUMN_NAME_ORDER_INDEX + " ASC");

        if (cursor.moveToFirst()) {
            do {
                lessonList.add(new Lesson(
                        cursor.getLong(cursor.getColumnIndexOrThrow(PalmsLibrasContract.LessonEntry._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.LessonEntry.COLUMN_NAME_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PalmsLibrasContract.LessonEntry.COLUMN_NAME_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PalmsLibrasContract.LessonEntry.COLUMN_NAME_ORDER_INDEX))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lessonList;
    }
}