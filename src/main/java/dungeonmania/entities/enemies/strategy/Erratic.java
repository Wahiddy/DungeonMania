package dungeonmania.entities.enemies.strategy;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;

public class Erratic implements MoveStrategy {
    @Override
    public Position move(Player player, GameMap map, Entity e) {
        Random randGen = new Random();
        List<Position> pos = e.getPosition().getCardinallyAdjacentPositions();
        pos = pos.stream().filter(p -> map.canMoveTo(e, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            return e.getPosition();
        } else {
            return pos.get(randGen.nextInt(pos.size()));
        }
    }
}
