package etec.com.tcc.palmslibras.utils;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import etec.com.tcc.palmslibras.models.Gesture;

public class GestureManager {

    private static List<Gesture> alphabetGestures;

    public static void loadGestures(Context context) {
        if (alphabetGestures == null) {
            alphabetGestures = new ArrayList<>();
            Resources res = context.getResources();
            String packageName = context.getPackageName();

            for (char c = 'a'; c <= 'z'; c++) {
                int resId = res.getIdentifier("gesto_" + c, "drawable", packageName);
                if (resId != 0) {
                    alphabetGestures.add(new Gesture(String.valueOf(c).toUpperCase(), resId));
                }
            }
        }
    }

    public static List<Gesture> getAlphabetGestures() {
        return alphabetGestures;
    }

    public static List<Gesture> getGesturesForUnit(int unitNumber) {
        if (alphabetGestures == null || alphabetGestures.isEmpty()) {
            return new ArrayList<>();
        }

        if (unitNumber == 1) { // A-M
            return new ArrayList<>(alphabetGestures.subList(0, 13));
        } else if (unitNumber == 2) { // N-Z
            return new ArrayList<>(alphabetGestures.subList(13, alphabetGestures.size()));
        }
        return new ArrayList<>();
    }

    public static List<Gesture> getRandomGestures(Gesture exclude, int count) {
        List<Gesture> randomGestures = new ArrayList<>(alphabetGestures);
        randomGestures.remove(exclude);
        Collections.shuffle(randomGestures);
        return new ArrayList<>(randomGestures.subList(0, count));
    }
}