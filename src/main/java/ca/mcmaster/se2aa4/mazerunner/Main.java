package ca.mcmaster.se2aa4.mazerunner;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.*;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("** Starting Maze Runner");

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

            // Pass FileMazeLoader to Maze
            Maze maze = new Maze(fileName, new FileMazeLoader());
            maze.printMaze();

            int startY = maze.findStart();
            if (startY == -1) {
                logger.error("No valid starting position found on the left side of the maze!");
                return;
            }

            User user = new User(0, startY, maze.getMazeArray());
            StringBuilder directions = new StringBuilder();

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

                logger.info("\nMaze State:");
                user.printMazeWithUser();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("Sleep interrupted", e);
                }
            }

            logger.info("User has reached the exit!");
            logger.info("**** Maze Navigation Complete");
            logger.info("Directions: " + compress(directions.toString()));

        } catch (ParseException | IOException e) {
            logger.error("Error processing the maze", e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
        }

        logger.info("** End of Maze Runner");
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
