package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.*;

public class User {

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