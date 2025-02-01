package ca.mcmaster.se2aa4.mazerunner;

import java.io.IOException;

public interface MazeLoader {
    char[][] loadMaze(String source) throws IOException;
}
