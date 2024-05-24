package dungeonmania.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.CollectableEntity;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.BaseState;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable, Overlappable {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;

    private int collectedTreasureCount = 0;

    private PlayerState state;

    public Player(Position position, double health, double attack) {
        super(position);
        battleStatistics = new BattleStatistics(health, attack, 0, BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        state = new BaseState(this);
    }

    public int getCollectedTreasureCount() {
        return collectedTreasureCount;
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public BattleItem getWeapon() {
        return inventory.getWeapon();
    }

    public List<String> getBuildables() {
        return inventory.getBuildables();
    }

    public boolean build(String entity, EntityFactory factory) {
        InventoryItem item = inventory.checkBuildCriteria(this, true, entity, factory);

        if (item == null)
            return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                if (((Mercenary) entity).isAllied())
                    return;
            }
            map.getGame().battle(this, (Enemy) entity);
        } else if (entity instanceof CollectableEntity) {
            ((CollectableEntity) entity).onOverlap(map, this);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(Entity item) {
        if (item instanceof Treasure)
            collectedTreasureCount++;
        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return inventory.getEntities(clz);
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null && !(item instanceof SunStone))
            inventory.remove(item);
    }

    public void useForBribe(Treasure t) {
        inventory.remove(t);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNext(int currentTick) {
        if (queue.isEmpty()) {
            inEffective = null;
            state.transitionBase();
            return;
        }
        inEffective = queue.remove();
        if (inEffective instanceof InvincibilityPotion) {
            state.transitionInvincible();
        } else {
            state.transitionInvisible();
        }
        nextTrigger = currentTick + inEffective.getDuration();
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void onTick(Game game, int tick) {
        List<Mercenary> mercs = game.getEntities(Mercenary.class);
        for (Mercenary merc : mercs) {
            if (tick == merc.getAllyExpiry()) {
                merc.alliedStatus(false);
            }
        }
        if (inEffective == null || tick == nextTrigger) {
            triggerNext(tick);
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public double getHealth() {
        return getBattleStatistics().getHealth();
    }

    @Override
    public void setHealth(double health) {
        getBattleStatistics().setHealth(health);
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    /**
     * Goes through each entity and applies specific buffs to player
     * @param game
     * @param battleItems
     * @return
     */
    public BattleStatistics doBuffs(Game game, List<BattleItem> battleItems) {
        BattleStatistics playerBuff = new BattleStatistics(0, 0, 0, 1, 1);

        Potion effectivePotion = getEffectivePotion();
        if (effectivePotion != null) {
            playerBuff = applyBuff(playerBuff);
        }
        for (BattleItem item : getEntities(BattleItem.class)) {
            if (item instanceof Potion)
                continue;
            playerBuff = item.applyBuff(playerBuff);
            battleItems.add(item);
        }

        List<Mercenary> mercs = game.getEntities(Mercenary.class);
        for (Mercenary merc : mercs) {
            if (!merc.isAllied())
                continue;
            playerBuff = BattleStatistics.applyBuff(playerBuff, merc.getBattleStatistics());
        }

        return playerBuff;
    }

    private BattleStatistics applyBuff(BattleStatistics origin) {
        return state.applyBuff(origin);
    }
}
