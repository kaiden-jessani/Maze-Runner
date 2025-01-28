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
                // Print the user's current position and direction
    
                // Right-hand rule: if there's no wall on the right, turn right and move forward
                if (!user.check_right_wall()) {
                    user.turn_right();
                    directions.append("R");
                }
                // Try to move forward, and if blocked, turn left
                if (!user.move_forward()) {
                    user.turn_left();
                    directions.append("L");
                } else {
                    directions.append("F");
                }

                // Print the maze with the user's current position
                System.out.println("\nMaze State:");
                user.printMazeWithUser();

                // Pause for 500 milliseconds before the next step
                try {
                    Thread.sleep(500); // Sleep for half a second
                } catch (InterruptedException e) {
                    logger.error("Sleep interrupted", e);
                }
            }

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
            if (maze[row].length > 0 && maze[row][0] == ' ') {
                return row;
            }
        }
        return -1;
    }
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

    // Turn right (clockwise)
    public void turn_right() {
        if (direction == 'E') {
            direction = 'S';
        } else if (direction == 'S') {
            direction = 'W';
        } else if (direction == 'W') {
            direction = 'N';
        } else if (direction == 'N') {
            direction = 'E';
        }
    }

    // Turn left (counterclockwise)
    public void turn_left() {
        if (direction == 'E') {
            direction = 'N';
        } else if (direction == 'N') {
            direction = 'W';
        } else if (direction == 'W') {
            direction = 'S';
        } else if (direction == 'S') {
            direction = 'E';
        }
    }

    /**
     * Check if there is a wall to the right of the current position.
     *
     * @return true if there is a wall to the right, false otherwise.
     */
    public boolean check_right_wall() {
        // Determine the position to the right based on the current direction
        int rightX = x_coor;
        int rightY = y_coor;

        if (direction == 'E') {
            rightY = y_coor + 1; // Right is to the south
        } else if (direction == 'S') {
            rightX = x_coor - 1; // Right is to the west
        } else if (direction == 'W') {
            rightY = y_coor - 1; // Right is to the north
        } else if (direction == 'N') {
            rightX = x_coor + 1; // Right is to the east
        }

        // Ensure the right position is within bounds and check if it's a wall
        if (rightY >= 0 && rightY < maze.length && rightX >= 0 && rightX < maze[0].length) {
            return maze[rightY][rightX] == '#'; // '#' indicates a wall
        }

        // If out of bounds, treat it as a wall
        return true;
    }

    public void printMazeWithUser() {
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[y].length; x++) {
                if (y == y_coor && x == x_coor) {
                    // Add an arrow emoji based on the user's current direction
                    switch (direction) {
                        case 'E':
                            System.out.print("→");
                            break;
                        case 'S':
                            System.out.print("↓");
                            break;
                        case 'W':
                            System.out.print("←");
                            break;
                        case 'N':
                            System.out.print("↑");
                            break;
                    }
                } else {
                    System.out.print(maze[y][x]);
                }
            }
            System.out.println();
        }
    }

    public boolean move_forward() {
        // Determine the new position based on the current direction
        int newX = x_coor;
        int newY = y_coor;

        if (direction == 'E') {
            newX++;
        } else if (direction == 'S') {
            newY++;
        } else if (direction == 'W') {
            newX--;
        } else if (direction == 'N') {
            newY--;
        }

        // Check if the new position is within bounds and not a wall
        if (newY >= 0 && newY < maze.length && newX >= 0 && newX < maze[0].length && maze[newY][newX] == ' ') {
            // Update position
            x_coor = newX;
            y_coor = newY;
            return true; // Move successful
        }

        return false; // Move blocked by a wall or out of bounds
    }
}