package utils;

import java.util.ArrayList;
import java.util.List;

public class Position {

    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public int chebyshevDistance(Position other) {
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }

    public int manhattanDistance(Position other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    /**
     * Returns all 8 neighboring positions (does not check bounds).
     */
    public List<Position> getNeighbors() {
        List<Position> neighbors = new ArrayList<>(8);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                neighbors.add(new Position(x + dx, y + dy));
            }
        }
        return neighbors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position pos = (Position) o;
        return x == pos.x && y == pos.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
