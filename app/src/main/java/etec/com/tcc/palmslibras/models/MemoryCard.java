package etec.com.tcc.palmslibras.models;

public class MemoryCard {
    private Gesture gesture;
    private boolean isImage;
    private boolean isFlipped;
    private boolean isMatched;
    private boolean isError;
    private int variant; // 0 = default, 1..3 tons de pele

    public MemoryCard(Gesture gesture, boolean isImage) {
        this.gesture = gesture;
        this.isImage = isImage;
        this.isFlipped = false;
        this.isMatched = false;
        this.isError = false;
        this.variant = 0;
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

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public int getVariant() { return variant; }
    public void setVariant(int variant) { this.variant = variant; }
}