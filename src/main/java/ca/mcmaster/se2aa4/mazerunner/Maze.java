package ca.mcmaster.se2aa4.mazerunner;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Maze {
    private char[][] maze;
    private static final Logger logger = LogManager.getLogger();

    public Maze(String fileName, MazeLoader loader) throws IOException {
        this.maze = loader.loadMaze(fileName);
    }

    public void printMaze() {
        for (char[] row : maze) {
            System.out.println(new String(row));
        }
    }

    public char[][] getMazeArray() {
        return maze;
    }

    public int findStart() {
        for (int row = 0; row < maze.length; row++) {
            if (maze[row].length > 0 && maze[row][0] == ' ') {
                return row;
            }
        }
        return -1;
    }
}
