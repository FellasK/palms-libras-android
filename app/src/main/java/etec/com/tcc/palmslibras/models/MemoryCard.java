package etec.com.tcc.palmslibras.models;

public class MemoryCard {
    private Gesture gesture;
    private boolean isImage;
    private boolean isFlipped;
    private boolean isMatched;

    public MemoryCard(Gesture gesture, boolean isImage) {
        this.gesture = gesture;
        this.isImage = isImage;
        this.isFlipped = false;
        this.isMatched = false;
    }

    public Gesture getGesture() {
        return gesture;
    }

    public boolean isImage() {
        return isImage;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}