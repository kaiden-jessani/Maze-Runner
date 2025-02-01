package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileMazeLoader implements MazeLoader {
    @Override
    public char[][] loadMaze(String fileName) throws IOException {
        StringBuilder mazeBuilder = new StringBuilder();
        int rows = 0, cols = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                mazeBuilder.append(line).append("\n");
                cols = line.length();
                rows++;
            }
        }

        char[][] maze = new char[rows][cols];
        String[] mazeLines = mazeBuilder.toString().split("\n");
        for (int i = 0; i < rows; i++) {
            maze[i] = mazeLines[i].toCharArray();
        }
        return maze;
    }
}
