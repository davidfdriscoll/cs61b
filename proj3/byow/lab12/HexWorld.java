package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     *
     * @param x x coordinate for far left of top middle row
     * @param y y coordinate for far left of top middle row
     * @param size length of each size of hexagon
     */
    private static void addHexagon(TETile[][] tiles, int x, int y, int size, TETile tile) {
        int bigRowSize = getHexagonDiameter(size);
        for (int i = 0; i < size; i++) {
            int rowX = x + i;
            int topY = y - i;
            int bottomY = y + 1 + i;
            int rowSize = bigRowSize - 2 * i;
            addRow(tiles, rowX, topY, rowSize, tile);
            addRow(tiles, rowX, bottomY, rowSize, tile);
        }
    }

    /**
     *
     * @param hexagonSize length of a side of the hexagon
     * @return hexagon diameter
     * e.g. 2 -> 4, 3 -> 7, etc.
     */
    private static int getHexagonDiameter(int hexagonSize) {
        return 3 * hexagonSize - 2;
    }

    private static void addRow(TETile[][] tiles, int x, int y, int length, TETile tile) {
        for (int i = 0; i < length; i++) {
            tiles[x + i][y] = tile;
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(9);
        return switch (tileNum) {
            case 0 -> Tileset.WALL;
            case 1 -> Tileset.FLOWER;
            case 2 -> Tileset.GRASS;
            case 3 -> Tileset.FLOOR;
            case 4 -> Tileset.MOUNTAIN;
            case 5 -> Tileset.SAND;
            case 6 -> Tileset.WATER;
            case 7 -> Tileset.AVATAR;
            case 8 -> Tileset.LOCKED_DOOR;
            default -> Tileset.NOTHING;
        };
    }

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void drawTesselationRow(TETile[][] tiles, int x, int y, int hexagonSize, int numberOfHexagons) {
        int hexagonDiameter = getHexagonDiameter(hexagonSize);
        for (int hexagonIdx = 0; hexagonIdx < numberOfHexagons; hexagonIdx++) {
            int hexagonX = x + hexagonIdx * (hexagonDiameter + hexagonSize);
            addHexagon(tiles, hexagonX, y, hexagonSize, randomTile());
        }
    }

    public static void drawTesselationColumn(TETile[][] tiles, int x, int y, int hexagonSize, int rows, int hexagonsPerRow) {
        int hexagonDiameter = getHexagonDiameter(hexagonSize);
        int yOffset = hexagonDiameter - hexagonSize + 2;
        for (int i = 0; i < rows; i++) {
            int rowY = y + yOffset * i;
            drawTesselationRow(tiles, x, rowY, hexagonSize, hexagonsPerRow);
        }
    }


    public static void drawTesselation(TETile[][] tiles, int x, int y, int hexagonSize, int tesselationSize) {
        int xOffset = 2 * hexagonSize - 1;
        for (int i = 0; i < tesselationSize; i++) {
            int tesselationX = x + xOffset * i;
            int tesselationY = y - i * hexagonSize;
            int tesselationRows = tesselationSize + i;
            int tesselationColumns = tesselationSize - i;
            drawTesselationColumn(tiles, tesselationX, tesselationY, hexagonSize, tesselationRows, tesselationColumns);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        fillWithNothing(tiles);
        drawTesselation(tiles, 5, 30, 4, 3);

        ter.renderFrame(tiles);
    }
}
