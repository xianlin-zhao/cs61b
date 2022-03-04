package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.LinkedList;
import java.util.Random;

public class HallWay {
    public static void initializeHallWay(TETile[][] world) {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = Tileset.WALL;
            }
        }
        for (int i = 1; i <= world.length - 2; i += 2) {
            for (int j = 1; j <= world[0].length - 2; j += 2) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    public static Position RandomStart(TETile[][] world, long seed) {
        Random RANDOM = new Random(seed);
        while (true) {
            int startX = RANDOM.nextInt(world.length - 2);
            int startY = RANDOM.nextInt(world[0].length - 2);
            if (startX % 2 == 0) {
                startX += 1;
            }
            if (startY % 2 == 0) {
                startY += 1;
            }
            if (world[startX][startY] == Tileset.NOTHING) {
                return new Position(startX, startY);
            }
        }
    }

    private static void PositionsAvailableAroundAndConnectPath(Position position, TETile[][] world, LinkedList<Position>positionList, long seed) {
        int[][] nextDirection = {
                {2, 0},
                {0, -2},
                {-2, 0},
                {0, 2}
        };
        Random RANDOM = new Random(seed);
        boolean[] book = new boolean[4];
        boolean isConnected = false;
        while (book[0] == false || book[1] == false || book[2] == false || book[3] == false) {
            int next = RANDOM.nextInt(4);
            if (book[next]) {
                continue;
            }
            book[next] = true;
            int nextX = position.x + nextDirection[next][0];
            int nextY = position.y + nextDirection[next][1];
            if (nextX < 0 || nextX > world.length - 1 || nextY < 0 || nextY > world[0].length - 1) {
                continue;
            }
            if (Tileset.NOTHING.equals(world[nextX][nextY])) {
                world[nextX][nextY] = Tileset.FLOWER;
                positionList.add(new Position(nextX, nextY));
            }
            if (Tileset.FLOOR.equals(world[nextX][nextY]) && isConnected == false) {
                isConnected = true;
                int mx = (position.x + nextX) / 2;
                int my = (position.y + nextY) / 2;
                world[mx][my] = Tileset.FLOOR;
            }
        }
    }

    public static boolean generateHallWay(TETile[][] world, long seed) {
        Position start = RandomStart(world, seed);
        generateHallWay(world, start, seed);
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (Tileset.NOTHING.equals(world[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void generateHallWay(TETile[][] world, Position start, long seed) {
        LinkedList<Position> positionList = new LinkedList<>();
        positionList.add(start);
        Random RANDOM = new Random(seed);
        while (!positionList.isEmpty()) {
            int index = RANDOM.nextInt(positionList.size());
            Position curPos = positionList.get(index);
            int curX = curPos.x;
            int curY = curPos.y;
            world[curX][curY] = Tileset.FLOOR;
            PositionsAvailableAroundAndConnectPath(curPos, world, positionList, seed);
            positionList.remove(index);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(79, 29);
        TETile[][] world = new TETile[79][29];
        initializeHallWay(world);
        generateHallWay(world, 180084946);
        ter.renderFrame(world);
    }
}
