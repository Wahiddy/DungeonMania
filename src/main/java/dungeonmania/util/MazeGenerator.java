package dungeonmania.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class MazeGenerator {
    private final int width;
    private final int height;
    private final int xMin;
    private final int yMin;
    private final int xStart;
    private final int yStart;
    private final int xEnd;
    private final int yEnd;

    private final boolean[][] maze;
    private final Random random;
    private static final int[] START = {
            1, 1
    };

    public MazeGenerator(int xStart, int yStart, int xEnd, int yEnd) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;

        this.width = Math.abs(xStart - xEnd) + 3;
        this.height = Math.abs(yStart - yEnd) + 3;

        this.xMin = Math.min(xStart, xEnd) - 1;
        this.yMin = Math.min(yStart, yEnd) - 1;
        this.maze = new boolean[width][height];
        this.random = new Random();
    }

    /**
     * Uses Prims algorithm to generate a maze
     * that is random each time
     */
    public void generateMaze() {
        int[] end = {
                width - 2, height - 2
        };
        maze[START[0]][START[1]] = true;

        List<int[]> options = new ArrayList<>();
        addDistanceWalls(START[0], START[1], 2, options);

        while (!options.isEmpty()) {
            int[] next = options.remove(random.nextInt(options.size()));

            List<int[]> neighbours = new ArrayList<>();
            addDistanceEmpty(next[0], next[1], 2, neighbours);

            if (!neighbours.isEmpty()) {
                int[] neighbour = neighbours.get(random.nextInt(neighbours.size()));
                maze[next[0]][next[1]] = true;

                // add empty spaces inbetween
                maze[(next[0] + neighbour[0]) / 2][(next[1] + neighbour[1]) / 2] = true;
                maze[neighbour[0]][neighbour[1]] = true;
            }

            addDistanceWalls(next[0], next[1], 2, options);
        }

        makeEndConnected(end);
    }

    /**
     * Checks whether the given coordinates are within the maze boundaries
     * @param x
     * @param y
     * @return
     */
    private boolean withinBoundary(int x, int y) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1;
    }

    /**
     * Adds the coordinates of the cardianlly adjacent cells that are empty
     * with the given distance to list
     * @param x
     * @param y
     * @param distance
     * @param neighbours
     */
    private void addDistanceEmpty(int x, int y, int distance, List<int[]> neighbours) {
        if (withinBoundary(x, y + distance) && maze[x][y + distance]) {
            neighbours.add(new int[] {
                    x, y + distance
            });
        }
        if (withinBoundary(x + distance, y) && maze[x + distance][y]) {
            neighbours.add(new int[] {
                    x + distance, y
            });
        }
        if (withinBoundary(x, y - distance) && maze[x][y - distance]) {
            neighbours.add(new int[] {
                    x, y - distance
            });
        }
        if (withinBoundary(x - distance, y) && maze[x - distance][y]) {
            neighbours.add(new int[] {
                    x - distance, y
            });
        }
    }

    /**
     * Adds the coordinates of the cardianlly adjacent cells that are walls
     * with the given distance to list
     * @param x
     * @param y
     * @param distance
     * @param options
     */
    private void addDistanceWalls(int x, int y, int distance, List<int[]> options) {
        if (withinBoundary(x, y + distance) && !maze[x][y + distance]) {
            options.add(new int[] {
                    x, y + distance
            });
        }
        if (withinBoundary(x + distance, y) && !maze[x + distance][y]) {
            options.add(new int[] {
                    x + distance, y
            });
        }
        if (withinBoundary(x, y - distance) && !maze[x][y - distance]) {
            options.add(new int[] {
                    x, y - distance
            });
        }
        if (withinBoundary(x - distance, y) && !maze[x - distance][y]) {
            options.add(new int[] {
                    x - distance, y
            });
        }
    }

    private void makeEndConnected(int[] end) {
        if (!maze[end[0]][end[1]]) {
            maze[end[0]][end[1]] = true;

            List<int[]> neighbours = new ArrayList<>(); // resuse neighbours list
            addDistanceEmpty(end[0], end[1], 1, neighbours);
            addDistanceWalls(end[0], end[1], 1, neighbours);

            boolean allWalls = true;
            for (int[] neighbour : neighbours) {
                if (maze[neighbour[0]][neighbour[1]]) {
                    allWalls = false;
                    break;
                }
            }

            if (allWalls) {
                int[] neighbour = neighbours.get(random.nextInt(neighbours.size()));
                maze[neighbour[0]][neighbour[1]] = true; // make sure the end is connected
            }
        }
    }

    public JSONObject generateJSONDungeon() {
        JSONArray entities = new JSONArray();

        // generates dungeon using xMin and yMin offset
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!maze[i][j])
                    entities.put(new JSONObject().put("x", xMin + i).put("y", yMin + j).put("type", "wall"));
            }
        }

        entities.put(new JSONObject().put("x", xStart).put("y", yStart).put("type", "player"));
        entities.put(new JSONObject().put("x", xEnd).put("y", yEnd).put("type", "exit"));

        // create the goal-condition object
        JSONObject goalCondition = new JSONObject().put("goal", "exit");
        JSONObject json = new JSONObject();

        json.put("entities", entities);
        json.put("goal-condition", goalCondition);

        return json;
    }

    public boolean[][] getMaze() {
        return maze;
    }

}
