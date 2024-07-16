package byow.WorldBuilder;

public class RoomBox {
    public final Rectangle rectangle;
    public final Rectangle room;
    private final RoomBox leftChild;
    private final RoomBox rightChild;
    public final boolean isHorizontalSplit;

    public RoomBox(Rectangle wrapper, Rectangle room, RoomBox leftChild, RoomBox rightChild, boolean isHorizontalSplit) {
        this.rectangle = wrapper;
        this.room = room;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.isHorizontalSplit = isHorizontalSplit;
    }

    public RoomBox getLeftChild() {
        return this.leftChild;
    }

    public RoomBox getRightChild() {
        return this.rightChild;
    }
}
