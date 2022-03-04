package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MapGenerator {
    private int WIDTH;
    private int HEIGHT;
    private Random RANDOM;
    private long SEED;
    private static final int TIMES = 100;
    private static TETile[][] world;
    private static TETile[][] positionOfRoom;
    private static ArrayList<Room> Rooms;
    public Position player;
    public Position door;

    public MapGenerator(int width, int height, long seed) {
        WIDTH = width;
        HEIGHT = height;
        SEED = seed;
        RANDOM = new Random(seed);
        isPassed = new boolean[WIDTH][HEIGHT];
    }

    public TETile[][] mapGenerator() {
        do {
            world = new TETile[WIDTH][HEIGHT];
            positionOfRoom = new TETile[WIDTH][HEIGHT];
            initializeWorldArray();
            HallWay.initializeHallWay(world);
            Rooms = new ArrayList<>();
            fillWithRoom();
        } while (HallWay.generateHallWay(world, SEED) == false);
        connectRoomWithHallWay();
        removeDeadends();
        destroyWall();
        GenerateDoor();
        GeneratePlayer();
        return world;
    }

    private void GenerateDoor() {
        while (true) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            if (Tileset.FLOOR.equals(world[x][y])) {
                door = new Position(x, y);
                world[x][y] = Tileset.LOCKED_DOOR;
                return;
            }
        }
    }

    private void GeneratePlayer() {
        while (true) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            if (Tileset.FLOOR.equals(world[x][y])) {
                player = new Position(x, y);
                world[x][y] = Tileset.PLAYER;
                return;
            }
        }
    }

    private void initializeWorldArray() {
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++){
                positionOfRoom[x][y] = Tileset.NOTHING;
                world[x][y] = Tileset.NOTHING;
            }
    }

    private void connectRoomWithHallWay() {
        for (int i = 0; i < Rooms.size(); i++) {
            Room curRoom = Rooms.get(i);
            removeWall(curRoom);
        }
    }

    private void removeWall(Room curRoom) {
        for (int i = 0; i < 100; i++) {
            int index = RANDOM.nextInt(4);
            int mx, my;
            switch (index) {
                case 0:
                    mx = curRoom.x;
                    my = RANDOM.nextInt(curRoom.HEIGHT) + curRoom.y + 1;
                    if (!canBeRemoved(mx, my, index)) {
                        continue;
                    }
                    world[mx][my] = Tileset.FLOOR;
                    if (Tileset.FLOOR.equals(world[mx - 1][my])) {
                        return;
                    }
                    world[mx - 1][my] = Tileset.FLOOR;
                    if (Tileset.GRASS.equals(world[mx - 2][my])) {
                        continue;
                    }
                    return;
                case 1:
                    mx = curRoom.x + curRoom.WIDTH + 1;
                    my = RANDOM.nextInt(curRoom.HEIGHT) + curRoom.y + 1;
                    if (!canBeRemoved(mx, my, index)) {
                        continue;
                    }
                    world[mx][my] = Tileset.FLOOR;
                    if (Tileset.FLOOR.equals(world[mx + 1][my])) {
                        return;
                    }
                    world[mx + 1][my] = Tileset.FLOOR;
                    if (Tileset.GRASS.equals(world[mx + 2][my])) {
                        continue;
                    }
                    return;
                case 2:
                    mx = RANDOM.nextInt(curRoom.WIDTH) + curRoom.x + 1;
                    my = curRoom.y;
                    if (!canBeRemoved(mx, my, index)) {
                        continue;
                    }
                    world[mx][my] = Tileset.FLOOR;
                    if (Tileset.FLOOR.equals(world[mx][my - 1])) {
                        return;
                    }
                    world[mx][my - 1] = Tileset.FLOOR;
                    if (Tileset.GRASS.equals(world[mx][my - 2])) {
                        continue;
                    }
                    return;
                case 3:
                    mx = RANDOM.nextInt(curRoom.WIDTH) + curRoom.x + 1;
                    my = curRoom.y + curRoom.HEIGHT + 1;
                    if (!canBeRemoved(mx, my, index)) {
                        continue;
                    }
                    world[mx][my] = Tileset.FLOOR;
                    if (Tileset.FLOOR.equals(world[mx][my + 1])) {
                        return;
                    }
                    world[mx][my + 1] = Tileset.FLOOR;
                    if (Tileset.GRASS.equals(world[mx][my + 2])) {
                        continue;
                    }
                    return;
            }
        }
    }

    private boolean canBeRemoved(int x, int y, int direction) {
        if (Tileset.FLOOR.equals(world[x][y])) {
            return false;
        }
        switch (direction) {
            case 0:
                if (x <= 1) {
                    return false;
                }
                if (Tileset.NOTHING.equals(world[x - 1][y]) || (Tileset.WALL.equals(world[x - 2][y]) && Tileset.WALL.equals(world[x - 1][y]))) {
                    return false;
                }
                return true;
            case 1:
                if (x >= WIDTH - 2) {
                    return false;
                }
                if (Tileset.NOTHING.equals(world[x + 1][y]) || (Tileset.WALL.equals(world[x + 2][y]) && Tileset.WALL.equals(world[x + 1][y]))) {
                    return false;
                }
                return true;
            case 2:
                if (y <= 1) {
                    return false;
                }
                if (Tileset.NOTHING.equals(world[x][y - 1]) || (Tileset.WALL.equals(world[x][y - 2]) && Tileset.WALL.equals(world[x][y - 1]))) {
                    return false;
                }
                return true;
            case 3:
                if (y >= HEIGHT - 2) {
                    return false;
                }
                if (Tileset.NOTHING.equals(world[x][y + 1]) || (Tileset.WALL.equals(world[x][y + 2]) && Tileset.WALL.equals(world[x][y + 1]))) {
                    return false;
                }
                return true;
        }
        return false;
    }

    private void removeDeadends() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (Tileset.GRASS.equals(world[i][j]) || Tileset.FLOOR.equals(world[i][j])) {
                    world[i][j] = Tileset.FLOOR;
                    int cnt = 0;
                    if (Tileset.WALL.equals(world[i - 1][j])) {
                        cnt++;
                    }
                    if (Tileset.WALL.equals(world[i + 1][j])) {
                        cnt++;
                    }
                    if (Tileset.WALL.equals(world[i][j - 1])) {
                        cnt++;
                    }
                    if (Tileset.WALL.equals(world[i][j + 1])) {
                        cnt++;
                    }
                    if (cnt >= 3) {
                        DFS(i, j);
                    }
                }
            }
        }
    }

    private static int[][] next = {
            {1, 0},
            {0, -1},
            {-1, 0},
            {0, 1}
    };

    private boolean[][] isPassed;

    private boolean DFS(int x, int y) {
        int cnt = 0;
        Queue<Position> accessiblePath = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            int mx = x + next[i][0];
            int my = y + next[i][1];
            if (mx < 0 || mx >= WIDTH || my < 0 || my >= HEIGHT) {
                continue;
            }
            if (Tileset.WALL.equals(world[mx][my])) {
                cnt++;
                continue;
            }
            if (isPassed[mx][my] == true) {
                continue;
            }
            if (Tileset.GRASS.equals(world[mx][my])) {
                world[mx][my] = Tileset.FLOOR;
            }
            if (Tileset.FLOOR.equals(world[mx][my])) {
                accessiblePath.offer(new Position(mx, my));
            }
        }
        if (cnt >= 3) {
            world[x][y] = Tileset.WALL;
        }
        while (!accessiblePath.isEmpty()) {
            Position pos = accessiblePath.peek();
            isPassed[pos.x][pos.y] = true;
            if (DFS(pos.x, pos.y)) {
                cnt++;
            }
            if (cnt >= 3) {
                world[x][y] = Tileset.WALL;
            }
            accessiblePath.poll();
        }
        return cnt >= 3;
    }

    private void destroyWall() {
        int[][] next2 = {
                {1, 1},
                {1, -1},
                {-1, -1},
                {-1, 1}
        };
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (Tileset.WALL.equals(world[i][j])) {
                    boolean isDestroy = true;
                    for (int k = 0; k < 4; k++) {
                        int mx = i + next2[k][0];
                        int my = j + next2[k][1];
                        if (mx < 0 || mx >= WIDTH || my < 0 || my >= HEIGHT) {
                            continue;
                        }
                        if (Tileset.FLOOR.equals(world[mx][my])) {
                            isDestroy = false;
                        }
                    }
                    if (isDestroy == true) {
                        world[i][j] = Tileset.NOTHING;
                    }
                }
            }
        }
    }

    private void fillWithRoom() {
        for (int i = 0; i < TIMES; i++) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            int width = RANDOM.nextInt(WIDTH / 10) + 2;
            int height = RANDOM.nextInt(HEIGHT / 5) + 2;
            if (y + height + 1 >= HEIGHT || x + width + 1 >= WIDTH) {
                continue;
            }
            if (isOverlap(x, y, width, height)) {
                continue;
            }
            buildRoom(x, y, width, height);
            Rooms.add(new Room(x, y, width, height));
        }
    }

    private void buildRoom(int x, int y, int width, int height) {
        for (int i = x; i <= x + width + 1; i++) {
            for (int j = y; j <= y + height + 1; j++) {
                if (i == x || i == x + width + 1 || j == y || j == y + height + 1) {
                    positionOfRoom[i][j] = Tileset.WALL;
                    world[i][j] = Tileset.WALL;
                    continue;
                }
                positionOfRoom[i][j] = Tileset.GRASS;
                world[i][j] = Tileset.GRASS;
            }
        }
    }

    private boolean isOverlap(int x, int y, int width, int height) {
        for (int i = x; i <= x + width + 1; i++) {
            for (int j = y; j <= y + height + 1; j++) {
                if (positionOfRoom[i][j] == Tileset.WALL || positionOfRoom[i][j] == Tileset.GRASS) {
                    return true;
                }
            }
        }
        return false;
    }
}
