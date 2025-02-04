package ca.mcmaster.se2aa4.mazerunner;

import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.*;

public class Main {
    // private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        // logger.info("** Starting Maze Runner");

        // Define command-line options
        Options options = new Options();
        options.addOption("i", true, "Input file");
        options.addOption("p", true, "Path sequence for verification");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (!cmd.hasOption("i")) {
                System.out.println("Input file not specified. Use -i <file>");
                System.exit(1);
            }

            // Construct file path
            String fileName = "./examples/" + cmd.getOptionValue("i");
            // logger.info("**** Reading the maze from file: " + fileName);

            // Load the maze from the file
            Maze maze = new Maze(fileName, new FileMazeLoader());
            // maze.printMaze();

            // Find the starting position on the left side of the maze
            int startY = maze.findStart();
            if (startY == -1) {
                System.out.println("No valid starting position found on the left side of the maze!");
                return;
            }

            // Initialize user position in the maze
            User user = new User(0, startY, maze.getMazeArray());
            StringBuilder directions = new StringBuilder();

            // Navigate the maze using right-hand rule
            while (user.x_coor != maze.getMazeArray()[0].length - 1 || maze.getMazeArray()[user.y_coor][user.x_coor] != ' ') {
                if (!user.check_right_wall()) {
                    user.turn_right();
                    directions.append("R");
                }
                if (!user.move_forward()) {
                    user.turn_left();
                    directions.append("L");
                } else {
                    directions.append("F");
                }

                // logger.info("\nMaze State:");
                // user.printMazeWithUser();
            }

            // logger.info("User has reached the exit!");
            // logger.info("**** Maze Navigation Complete");

            // Compress the computed path sequence
            String computedPath = compress(directions.toString());

            // Check if a path verification flag was provided
            if (cmd.hasOption("p")) {
                String userPath = cmd.getOptionValue("p");
                if (computedPath.equals(userPath)) {
                    System.out.println("correct path");
                } else {
                    System.out.println("incorrect path.");
                }
            } else {
                // Output the computed path if no verification flag was used
                System.out.println(computedPath);
            }
        } catch (ParseException | IOException e) {
            System.out.println("Error processing the maze");
        } catch (Exception e) {
            System.out.println("Unexpected error occurred");
        }

        // System.out.println("** End of Maze Runner");
    }

    // Compresses a path sequence by counting consecutive movements
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
                result.append(count).append("").append(currentChar).append("");
                currentChar = input.charAt(i);
                count = 1;
            }
        }
        result.append(count).append("").append(currentChar);
        return result.toString().trim();
    }
}
