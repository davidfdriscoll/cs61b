package byow.WorldBuilder;

public class Rectangle {
    public final int left;
    public final int right;
    public final int top;
    public final int bottom;

    public Rectangle(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public String toString() {
        return left + " " + right + " " + top + " " + bottom;
    }
}
