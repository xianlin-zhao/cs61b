package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.algs4.Transaction;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.Locale;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;
    private static final int ENTRYX = 40;
    private static final int ENTRYY = 5;
    private TETile[][] world;
    private int playerX;
    private int playerY;
    private String seedString = "";
    private static final String PATH = "world.txt";

    private boolean setupMode = true;
    private boolean newGameMode = false;
    private boolean quitMode = false;

    private static final String NORTH = "w";
    private static final String EAST = "d";
    private static final String SOUTH = "s";
    private static final String WEST = "a";



    private void processInput(String input) {
        if (input == null) {
            System.out.println("No input given.");
            System.exit(0);
        }
        String first = Character.toString(input.charAt(0));
        first = first.toLowerCase();
        processInputString(first);
        if (input.length() > 1) {
            String rest = input.substring(1);
            processInput(rest);
        }
    }

    private void processInputString(String first) {
        if (setupMode) {
            switch (first) {
                case "n":
                    newGameMode = !newGameMode;
                    break;
                case "s":
                    setupNewGame();
                    break;
                case "l":
                    load();
                    break;
                case "q":
                    System.exit(0);
                    break;
                default:
                    try {
                        Long.parseLong(first);
                        seedString += first;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input given: " + first);
                        System.exit(0);
                    }

                    break;
            }
        } else {
            switch (first) {
                case NORTH:
                case EAST:
                case SOUTH:
                case WEST:
                    move(first);
                    break;
                case ":":
                    quitMode = !quitMode;
                    break;
                case "q":
                    saveAndQuit();
                    System.exit(0);
                    break;
                default:
            }
        }
    }

    private void move(String input) {
        switch (input) {
            case NORTH:
                if (world[playerX][playerY + 1].equals(Tileset.FLOOR)) {
                    world[playerX][playerY + 1] = Tileset.PLAYER;
                    world[playerX][playerY] = Tileset.FLOOR;
                    playerY += 1;
                }
                return;
            case EAST:
                if (world[playerX + 1][playerY].equals(Tileset.FLOOR)) {
                    world[playerX + 1][playerY] = Tileset.PLAYER;
                    world[playerX][playerY] = Tileset.FLOOR;
                    playerX += 1;
                }
                return;
            case SOUTH:
                if (world[playerX][playerY - 1].equals(Tileset.FLOOR)) {
                    world[playerX][playerY - 1] = Tileset.PLAYER;
                    world[playerX][playerY] = Tileset.FLOOR;
                    playerY -= 1;
                }
                return;
            case WEST:
                if (world[playerX - 1][playerY].equals(Tileset.FLOOR)) {
                    world[playerX - 1][playerY] = Tileset.PLAYER;
                    world[playerX][playerY] = Tileset.FLOOR;
                    playerX -= 1;
                }
                return;
            default:
        }
    }

    private void setupNewGame() {
        if (!newGameMode) {
            String error = "Input string " + "\"S\" given, but no game has been initialized.\n"
                    + "Please initialize game first by input string \"N\" and following random seed"
                    + "numbers";
            System.out.println(error);
            System.exit(0);
        }
        newGameMode = !newGameMode;
        MapGenerator wg;
        if (seedString.equals("")) {
            wg = new MapGenerator(WIDTH, HEIGHT, 0);
        } else {
            long seed = Long.parseLong(seedString);
            wg = new MapGenerator(WIDTH, HEIGHT, seed);
        }
        world = wg.mapGenerator();
        playerX = wg.player.x;
        playerY = wg.player.y;
        setupMode = !setupMode;
    }

    private void saveAndQuit() {
        if (!quitMode) {
            return;
        }
        quitMode = !quitMode;

        File f = new File(PATH);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(world);
            oos.close();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        rewritePlayerLocation();
    }

    private void rewritePlayerLocation() {
        for (int w = 0; w < WIDTH; w++) {
            for (int h = 0; h < HEIGHT; h++) {
                if (world[w][h].equals(Tileset.PLAYER)) {
                    playerX = w;
                    playerY = h;
                }
            }
        }
    }

    private void load() {
        File f = new File(PATH);
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            world = (TETile[][]) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            System.out.println("No previously saved world found.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.out.println("Class TETile[][] not found.");
            System.exit(1);
        }
        setupMode = !setupMode;

    }

    private void processWelcome() {
        StdDraw.setCanvasSize();
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                String typed = Character.toString(key);
                processInput(typed);
            }
            renderWelcomeBoard();
            if (!setupMode) {
                break;
            }
        }
        processGame();
    }

    private void renderWelcomeBoard() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
        StdDraw.text(0.5, 0.8, "CS61B: BYoG");

        StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
        StdDraw.text(0.5, 0.5, "New Game: N");
        StdDraw.text(0.5, 0.45, "Load Game: L");
        StdDraw.text(0.5, 0.4, "Quit: Q");

        if (newGameMode) {
            StdDraw.text(0.5, 0.25, "Seed: " + seedString);
            StdDraw.text(0.5, 0.225, "(Press S to start the game)");
        }

        StdDraw.show();
        StdDraw.pause(100);
    }

    private void processGame() {
        ter.initialize(WIDTH, HEIGHT);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                String typed = Character.toString(key);
                processInput(typed);
            }
            renderGame();
        }
    }

    private void renderGame() {
        renderWorld();
        showTileOnHover();
        StdDraw.pause(10);
    }

    private void renderWorld() {
        StdDraw.setFont();
        StdDraw.setPenColor();
        ter.renderFrame(world);
    }

    private void showTileOnHover() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        TETile mouseTile = world[mouseX][mouseY];

        StdDraw.setFont(new Font("Arial", Font.PLAIN, 15));
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(1, HEIGHT - 1, mouseTile.description());
        StdDraw.show();
    }

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        processWelcome();
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // TODO: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().

        processInput(input);
        return world;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.playWithKeyboard();
    }
}
