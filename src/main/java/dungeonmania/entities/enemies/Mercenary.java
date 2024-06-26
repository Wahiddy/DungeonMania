package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.*;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.entities.enemies.strategy.Erratic;
import dungeonmania.entities.enemies.strategy.Invincibility;
import dungeonmania.entities.enemies.strategy.MoveStrategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;

    private double allyAttack;
    private double allyDefence;
    private boolean allied = false;
    private boolean isAdjacentToPlayer = false;

    private int allyExpiry = 0;

    private MoveStrategy strategy;

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius,
            double allyAttack, double allyDefence) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.allyAttack = allyAttack;
        this.allyDefence = allyDefence;
    }

    public boolean isAllied() {
        return allied;
    }

    public int getAllyExpiry() {
        return this.allyExpiry;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied)
            return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        int bribables = player.countEntityOfType(Treasure.class) - player.countEntityOfType(SunStone.class);
        int playerDistance = playerDistanceFromMerc(player.getPosition(), getPosition());
        return bribeRadius >= 0 && playerDistance <= bribeRadius && bribables >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    private void bribe(Player player) {
        Inventory inventory = player.getInventory();
        int i = 0;
        while (i < bribeAmount) {
            for (Entity item : inventory.getEntities()) {
                if (item instanceof Treasure && !(item instanceof SunStone)) {
                    player.useForBribe((Treasure) item);
                    i++;
                }
            }
        }
    }

    private void mindControl(Player player, Game game) {
        Sceptre s = playerSceptre(player);
        setAllyExpiry(s.getDuration() + game.getTick());
        player.remove(s);
    }

    @Override
    public void interact(Player player, Game game) {
        allied = true;
        if (playerSceptre(player) != null) {
            mindControl(player, game);
        } else {
            bribe(player);
        }

        if (!isAdjacentToPlayer && Position.isAdjacent(player.getPosition(), getPosition()))
            isAdjacentToPlayer = true;
    }

    @Override
    public void move(Game game) {
        Position nextPos = getPosition();
        GameMap map = game.getMap();
        Player player = game.getPlayer();
        if (allied) {
            nextPos = isAdjacentToPlayer ? player.getPreviousDistinctPosition()
                    : map.dijkstraPathFind(getPosition(), player.getPosition(), this);
            if (!isAdjacentToPlayer && Position.isAdjacent(player.getPosition(), nextPos))
                isAdjacentToPlayer = true;
        } else if (map.getPlayer().getEffectivePotion() instanceof InvisibilityPotion) {
            // Move random
            strategy = new Erratic();
            nextPos = strategy.move(player, map, this);
        } else if (map.getPlayer().getEffectivePotion() instanceof InvincibilityPotion) {
            strategy = new Invincibility();
            nextPos = strategy.move(player, map, this);
        } else {
            // Follow hostile
            nextPos = map.dijkstraPathFind(getPosition(), player.getPosition(), this);
        }
        map.moveTo(this, nextPos);
    }

    @Override
    public boolean isInteractable(Player player) {
        return !allied && (canBeBribed(player) || playerSceptre(player) != null);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        if (!allied)
            return super.getBattleStatistics();
        return new BattleStatistics(0, allyAttack, allyDefence, 1, 1);
    }

    private Sceptre playerSceptre(Player player) {
        Inventory inventory = player.getInventory();
        Sceptre s = inventory.getFirst(Sceptre.class);
        return s;
    }

    /*
     * Calculates the distance between the player and mercenary using
     * manhattan distance
     */
    private int playerDistanceFromMerc(Position player, Position merc) {
        return Math.abs(player.getX() - merc.getX()) + Math.abs(player.getY() - merc.getY());
    }

    public void setAllyExpiry(int duration) {
        allyExpiry = duration;
    }

    public void alliedStatus(Boolean isAllied) {
        allied = isAllied;
    }

}
