package etec.com.tcc.palmslibras.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import etec.com.tcc.palmslibras.models.Activity;
import etec.com.tcc.palmslibras.models.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
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
                PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT NOT NULL," +
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

        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_ACTIVITIES_TABLE);

        populateInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PalmsLibrasContract.UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PalmsLibrasContract.ActivityEntry.TABLE_NAME);
        onCreate(db);
    }

    private void populateInitialData(SQLiteDatabase db) {
        // Lição 1: Alfabeto
        addActivity(db, 1, "Qual é a forma correta de fazer \"A\" em Libras?", "[\"A\",\"B\",\"C\",\"D\"]", "A", 10);
        addActivity(db, 1, "Qual é a forma correta de fazer \"E\" em Libras?", "[\"E\",\"I\",\"O\",\"U\"]", "E", 10);
        addActivity(db, 1, "Qual é a forma correta de fazer \"I\" em Libras?", "[\"Y\",\"I\",\"J\",\"K\"]", "I", 15);

        // Lição 2: Números
        addActivity(db, 2, "Qual sinal representa o número \"1\"?", "[\"1\",\"2\",\"3\",\"4\"]", "1", 10);
        addActivity(db, 2, "Qual sinal representa o número \"5\"?", "[\"3\",\"4\",\"5\",\"6\"]", "5", 10);

        // Adicione usuários de exemplo para o ranking
        addUser(db, "Maria", "maria@email.com", "123", 1250);
        addUser(db, "João", "joao@email.com", "123", 980);
        addUser(db, "Ana", "ana@email.com", "123", 750);
    }

    private void addActivity(SQLiteDatabase db, int lessonId, String question, String options, String answer, int xp) {
        ContentValues values = new ContentValues();
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_LESSON_ID, lessonId);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_QUESTION, question);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_OPTIONS, options);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_CORRECT_ANSWER, answer);
        values.put(PalmsLibrasContract.ActivityEntry.COLUMN_NAME_XP_REWARD, xp);
        db.insert(PalmsLibrasContract.ActivityEntry.TABLE_NAME, null, values);
    }

    private void addUser(SQLiteDatabase db, String name, String email, String password, int xp) {
        ContentValues values = new ContentValues();
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME, name);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL, email);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD, password);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_XP, xp);
        db.insert(PalmsLibrasContract.UserEntry.TABLE_NAME, null, values);
    }

    public long addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_NAME, name);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL, email);
        values.put(PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD, password);
        long id = db.insert(PalmsLibrasContract.UserEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PalmsLibrasContract.UserEntry.TABLE_NAME, null,
                PalmsLibrasContract.UserEntry.COLUMN_NAME_EMAIL + "=? AND " + PalmsLibrasContract.UserEntry.COLUMN_NAME_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
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
}