package byow.WorldBuilder;

import byow.Core.Engine;
import byow.TileEngine.TETile;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;

public class WorldBuilder {
    private static final int MIN_ROOMBOX_SIZE = 10;
    private static final double ROOMBOX_SPLIT_MIN = 0.45;
    private static final double ROOMBOX_SPLIT_MAX = 0.55;
    private static final double ROOM_SIZE_FIXED_PROPORTION = 0.5;
    private static final double ROOM_SIZE_VARIABLE_PROPORTION = 0.5;

    public static void main(String[] args) {
        Engine engine = new Engine();
        TETile[][] tiles = engine.interactWithInputString("N14543S");
        engine.renderTiles(tiles);
    }

    public static TETile[][] generateWorld(Random random) {
        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        DrawWorld.fillWithNothing(tiles);
        RoomBox head = createRoomBoxTree(random);
        Set<Rectangle> rooms = getRooms(head);
        DrawWorld.drawRooms(rooms, tiles);
        return tiles;
    }

    private static Rectangle createRoomFromRoomBoxRectangle(Random random, Rectangle rectangle) {
        int roomHeight =
                (int) ((random.nextDouble() * ROOM_SIZE_VARIABLE_PROPORTION + ROOM_SIZE_FIXED_PROPORTION)
                        * (rectangle.bottom - rectangle.top)) - 1;
        int roomWidth =
                (int) ((random.nextDouble() * ROOM_SIZE_VARIABLE_PROPORTION + ROOM_SIZE_FIXED_PROPORTION)
                        * (rectangle.right - rectangle.left)) - 1;

        int roomTop = random.nextInt(rectangle.bottom - rectangle.top - roomHeight) + rectangle.top + 1;
        int roomBottom = roomTop + roomHeight;
        int roomLeft = random.nextInt(rectangle.right - rectangle.left - roomWidth) + rectangle.left + 1;
        int roomRight = roomLeft + roomWidth;

        return new Rectangle(roomLeft, roomRight, roomTop, roomBottom);
    }

    private static Set<Rectangle> getRooms(RoomBox head) {
        Set<Rectangle> rooms = new HashSet<>();
        traverseRoomBoxTree(head, rooms);
        return rooms;
    }

    private static void traverseRoomBoxTree(RoomBox roomBox, Set<Rectangle> rooms) {
        if (roomBox.getLeftChild() == null || roomBox.getRightChild() == null) {
            rooms.add(roomBox.room);
        } else {
            traverseRoomBoxTree(roomBox.getLeftChild(), rooms);
            traverseRoomBoxTree(roomBox.getRightChild(), rooms);
        }
    }

    private static RoomBox createRoomBoxTree(Random random) {
        int treeHeight = 2 * Math.min(HEIGHT / MIN_ROOMBOX_SIZE, WIDTH / MIN_ROOMBOX_SIZE);
        Rectangle world = new Rectangle(1, WIDTH - 2, 1, HEIGHT - 2);
        return createRoomBox(random, world, treeHeight);
    }

    private static int createRoomBoxSplit(Random random, int start, int end) {
        int interval = end - start;
        int cutStart = (int) (interval * ROOMBOX_SPLIT_MIN);
        int cutEnd = (int) (interval * ROOMBOX_SPLIT_MAX);
        int cut = random.nextInt(cutEnd - cutStart);
        return cut + cutStart + start;
    }

    private static RoomBox createRoomBox(Random random, Rectangle rectangle, int remainingHeight) {
        if (remainingHeight == 0) {
            return null;
        }
        Rectangle room = createRoomFromRoomBoxRectangle(random, rectangle);

        // horizontal split = two new rectangles are stacked vertically
        boolean isHorizontalSplit = random.nextBoolean();

        // if the room is too narrow for the split, abort
        int start = isHorizontalSplit ? rectangle.top : rectangle.left;
        int end = isHorizontalSplit ? rectangle.bottom : rectangle.right;
        boolean isBadSplit = end - start <= MIN_ROOMBOX_SIZE;
        if (isBadSplit) {
            return new RoomBox(rectangle, room, null, null, isHorizontalSplit);
        }

        int cut = createRoomBoxSplit(random, start, end);

        Rectangle leftChildRectangle;
        Rectangle rightChildRectangle;
        if (isHorizontalSplit) {
            leftChildRectangle =
                new Rectangle(rectangle.left, rectangle.right, rectangle.top, cut);
            rightChildRectangle =
                new Rectangle(rectangle.left, rectangle.right, cut + 1, rectangle.bottom);
        } else {
            leftChildRectangle =
                new Rectangle(rectangle.left, cut, rectangle.top, rectangle.bottom);
            rightChildRectangle =
                new Rectangle(cut + 1, rectangle.right, rectangle.top, rectangle.bottom);
        }

        RoomBox leftChild = createRoomBox(random, leftChildRectangle, remainingHeight - 1);
        RoomBox rightChild = createRoomBox(random, rightChildRectangle, remainingHeight - 1);
        return new RoomBox(rectangle, room, leftChild, rightChild, isHorizontalSplit);
    }
}
