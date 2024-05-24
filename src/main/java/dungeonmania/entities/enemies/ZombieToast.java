package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.enemies.strategy.Erratic;
import dungeonmania.entities.enemies.strategy.Invincibility;
import dungeonmania.entities.enemies.strategy.MoveStrategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Player;

public class ZombieToast extends Enemy {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;

    private MoveStrategy strategy;

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack);
    }

    @Override
    public void move(Game game) {
        Position nextPos = getPosition();
        GameMap map = game.getMap();
        Player player = map.getPlayer();
        if (player.getEffectivePotion() instanceof InvincibilityPotion) {
            strategy = new Invincibility();
            nextPos = strategy.move(player, map, this);
            map.moveTo(this, nextPos);
        } else {
            strategy = new Erratic();
            nextPos = strategy.move(player, map, this);
        }
        map.moveTo(this, nextPos);
    }

}
