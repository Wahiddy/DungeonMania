package dungeonmania.entities.enemies.strategy;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.util.Direction;

public class Invincibility implements MoveStrategy {
    @Override
    public Position move(Player player, GameMap map, Entity e) {
        Position plrDiff = Position.calculatePositionBetween(player.getPosition(), e.getPosition());

        Position moveX = e.getPosition();
        Position moveY = e.getPosition();

        // Checking if the enemy is stuck in the corner
        if (!map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.RIGHT)) && plrDiff.getX() > 0) {
            moveY = moveInVertical(player, map, e);
        }

        // Checking if the enemy is stuck in the corner
        if (!map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.LEFT)) && plrDiff.getX() < 0) {
            moveY = moveInVertical(player, map, e);
        }

        // Checking if the enemy is stuck in the corner
        if (!map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.UP)) && plrDiff.getY() > 0) {
            moveX = moveInHorizontal(player, map, e);
        }

        // Checking if the enemy is stuck in the corner
        if (!map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.DOWN)) && plrDiff.getY() < 0) {
            moveX = moveInHorizontal(player, map, e);
        }

        // Enemy can move away freely
        if (plrDiff.getX() >= 0 && map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.RIGHT))) {
            moveX = Position.translateBy(e.getPosition(), Direction.RIGHT);
        } else if (plrDiff.getX() < 0 && map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.LEFT))) {
            moveX = Position.translateBy(e.getPosition(), Direction.LEFT);
        }

        // Enemy can move away freely
        if (plrDiff.getY() >= 0 && map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.UP))) {
            moveY = Position.translateBy(e.getPosition(), Direction.UP);
        } else if (plrDiff.getY() < 0 && map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.DOWN))) {
            moveY = Position.translateBy(e.getPosition(), Direction.DOWN);
        }

        Position offset = e.getPosition();

        if (Math.abs(plrDiff.getX()) >= Math.abs(plrDiff.getY())) {
            if (map.canMoveTo(e, moveX))
                offset = moveX;
            else if (map.canMoveTo(e, moveY))
                offset = moveY;
        } else {
            if (map.canMoveTo(e, moveY))
                offset = moveY;
            else if (map.canMoveTo(e, moveX))
                offset = moveX;
        }

        return offset;
    }

    /**
     * Helper method to move the enemy in the vertical direction
     * @param player
     * @param map
     * @param e
     * @return
     */
    private Position moveInVertical(Player player, GameMap map, Entity e) {
        if (!map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.UP))) {
            return Position.translateBy(e.getPosition(), Direction.DOWN);
        } else {
            return Position.translateBy(e.getPosition(), Direction.UP);
        }
    }

    /**
     * Helper method to move the enemy in the horizontal direction
     * @param player
     * @param map
     * @param e
     * @return
     */
    private Position moveInHorizontal(Player player, GameMap map, Entity e) {
        if (!map.canMoveTo(e, Position.translateBy(e.getPosition(), Direction.LEFT))) {
            return Position.translateBy(e.getPosition(), Direction.RIGHT);
        } else {
            return Position.translateBy(e.getPosition(), Direction.LEFT);
        }
    }

}
