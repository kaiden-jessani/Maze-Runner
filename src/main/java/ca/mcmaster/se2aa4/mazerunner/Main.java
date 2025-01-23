package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.*;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("** Starting Maze Runner");

        // Define CLI options
        Options options = new Options();
        options.addOption("i", true, "Input file");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (!cmd.hasOption("i")) {
                logger.error("Input file not specified. Use -i <file>");
                System.exit(1);
            }

            String fileName = "./examples/" + cmd.getOptionValue("i");
            logger.info("**** Reading the maze from file: " + fileName);

            // Read and construct the maze
            char[][] maze = readMaze(fileName);

            // Print the maze
            logger.info("**** Maze structure:");
            printMaze(maze);

            // Initialize the user at the starting position
            int startY = find_start(maze);
            if (startY == -1) {
                logger.error("No valid starting position found on the left side of the maze!");
                return;
            }
            User user = new User(0, startY, maze);

            // StringBuilder to store the directions
            StringBuilder directions = new StringBuilder();

            // Navigate through the maze
            while (user.x_coor != maze[0].length - 1 || maze[user.y_coor][user.x_coor] != ' ') {
                System.out.println("User position: (" + user.x_coor + ", " + user.y_coor + "), direction: " + user.direction);
                System.out.println("\nMaze State:");
                user.printMazeWithUser();
                user.move_forward();
                directions.append("F");
            }
            user.printMazeWithUser();
            System.out.println("User has reached the exit!");
            logger.info("**** Maze Navigation Complete");
            // Print the directions
            System.out.println("Directions: " + compress(directions.toString()));

        } catch (ParseException e) {
            logger.error("Failed to parse command line arguments", e);
        } catch (IOException e) {
            logger.error("Error reading the maze file", e);
        } catch (Exception e) {
            logger.error("/!\\ An unexpected error has occurred /!\\", e);
        }

        logger.info("** End of Maze Runner");
    }

    /**
     * Reads the maze from a file and returns it as a 2D character array.
     */
    private static char[][] readMaze(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder mazeBuilder = new StringBuilder();
        int rows = 0;
        int cols = 0;

        // Read the maze to determine its size
        String line;
        while ((line = reader.readLine()) != null) {
            mazeBuilder.append(line).append("\n");
            cols = line.length();
            rows++;
        }
        reader.close();

        // Construct the maze array
        char[][] maze = new char[rows][cols];
        String[] mazeLines = mazeBuilder.toString().split("\n");
        for (int i = 0; i < rows; i++) {
            maze[i] = mazeLines[i].toCharArray();
        }
        return maze;
    }

    /**
     * Prints the maze to the console.
     */
    private static void printMaze(char[][] maze) {
        for (char[] row : maze) {
            for (char cell : row) {
                System.out.print(cell);
            }
            System.out.println();
        }
    }

    /**
     * Finds the starting row of the maze (left side).
     */
    private static int find_start(char[][] maze) {
        for (int row = 0; row < maze.length; row++) {
            if (maze[row].length > 0 && maze[row][0] != '#') {
                return row;
            }
        }
        return -1;
    }

    /**
     * Finds the exit row of the maze (right side).
     */

    public static String compress(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        char currentChar = input.charAt(0);
        int count = 1;

        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == currentChar) {
                count++;
            } else {
                result.append(count).append(" ").append(currentChar).append(" ");
                currentChar = input.charAt(i);
                count = 1;
            }
        }
        result.append(count).append(" ").append(currentChar);

        return result.toString().trim();
    }
}

class User {

    public char direction = 'E';
    public int x_coor;
    public int y_coor;
    private char[][] maze;

    public User(int startX, int startY, char[][] maze) {
        this.x_coor = startX;
        this.y_coor = startY;
        this.maze = maze;
    }

    public void printMazeWithUser() {
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[y].length; x++) {
                if (y == y_coor && x == x_coor) {
                    System.out.print("→");
                } else {
                    System.out.print(maze[y][x]);
                }
            }
            System.out.println();
        }
    }

    public void move_forward() {
        x_coor++;
    }
}