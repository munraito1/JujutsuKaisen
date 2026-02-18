package models;

import enums.TileType;

public class Tile {

    private TileType type;
    private boolean walkable;

    public Tile(TileType type) {
        this.type = type;
        this.walkable = (type != TileType.OBSTACLE);
    }

    public TileType getType() { return type; }
    public boolean isWalkable() { return walkable; }
}
