package etec.com.tcc.palmslibras.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "PalmsLibrasSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_UNIT1_COMPLETED = "unit1Completed";
    private static final String KEY_UNIT2_UNLOCKED = "unit2Unlocked";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(long userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, userId);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return pref.getLong(KEY_USER_ID, -1); // Retorna -1 se não encontrar
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public void setUnit1Completed(boolean completed) {
        editor.putBoolean(KEY_UNIT1_COMPLETED, completed);
        editor.commit();
    }

    public boolean isUnit1Completed() {
        return pref.getBoolean(KEY_UNIT1_COMPLETED, false);
    }

    public void setUnit2Unlocked(boolean unlocked) {
        editor.putBoolean(KEY_UNIT2_UNLOCKED, unlocked);
        editor.commit();
    }

    public boolean isUnit2Unlocked() {
        return pref.getBoolean(KEY_UNIT2_UNLOCKED, false);
    }
}