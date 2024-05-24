package dungeonmania.entities.enemies.strategy;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;

public interface MoveStrategy {
    public Position move(Player player, GameMap map, Entity e);
}
