package byow.WorldBuilder;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Set;

public class DrawWorld {
    static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    static void drawRooms(Set<Rectangle> rooms, TETile[][] tiles) {
        for (Rectangle room: rooms) {
            drawRoom(room, tiles);
        }
    }

    private static void drawRoom(Rectangle room, TETile[][] tiles) {
        for (int x = room.left; x <= room.right; x++) {
            tiles[x][room.top] = Tileset.WALL;
            tiles[x][room.bottom] = Tileset.WALL;
        }
        for (int y = room.top; y <= room.bottom; y++) {
            tiles[room.left][y] = Tileset.WALL;
            tiles[room.right][y] = Tileset.WALL;
        }
        for (int y = room.top + 1; y <= room.bottom - 1; y++) {
            for (int x = room.left + 1; x <= room.right - 1; x++) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
    }
}
