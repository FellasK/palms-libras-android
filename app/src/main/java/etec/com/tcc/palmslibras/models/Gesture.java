package etec.com.tcc.palmslibras.models;

import java.io.Serializable;
import java.util.Objects;

public class Gesture implements Serializable {
    private String letter;
    private int drawableId;

    public Gesture(String letter, int drawableId) {
        this.letter = letter;
        this.drawableId = drawableId;
    }

    public String getLetter() {
        return letter;
    }

    public int getDrawableId() {
        return drawableId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gesture gesture = (Gesture) o;
        return drawableId == gesture.drawableId && Objects.equals(letter, gesture.letter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, drawableId);
    }
}