package dungeonmania.battles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.NameConverter;

public class BattleFacade {
    private List<BattleResponse> battleResponses = new ArrayList<>();

    public void battle(Game game, Player player, Enemy enemy) {
        // 0. init
        double initialPlayerHealth = player.getHealth();
        double initialEnemyHealth = enemy.getHealth();
        String enemyString = NameConverter.toSnakeCase(enemy);

        // 1. apply buff provided by the game and player's inventory
        // getting buffing amount
        List<BattleItem> battleItems = new ArrayList<>();
        BattleStatistics playerBuff = player.doBuffs(game, battleItems);

        // 2. Battle the two stats
        BattleStatistics playerBaseStatistics = player.getBattleStatistics();
        BattleStatistics enemyBaseStatistics = enemy.getBattleStatistics();
        BattleStatistics playerBattleStatistics = BattleStatistics.applyBuff(playerBaseStatistics, playerBuff);
        BattleStatistics enemyBattleStatistics = enemyBaseStatistics;
        if (!playerBattleStatistics.isEnabled() || !enemyBaseStatistics.isEnabled())
            return;
        List<BattleRound> rounds = BattleStatistics.battle(playerBattleStatistics, enemyBattleStatistics);

        // 3. update health to the actual statistics
        updateHealth(player, playerBattleStatistics);
        updateHealth(enemy, enemyBattleStatistics);

        // 4. call to decrease durability of items
        decreaseDurabilityOfItems(game, battleItems);

        // 5. Log the battle - solidate it to be a battle response
        battleResponses.add(new BattleResponse(enemyString,
                rounds.stream().map(ResponseBuilder::getRoundResponse).collect(Collectors.toList()),
                battleItems.stream().map(Entity.class::cast).map(ResponseBuilder::getItemResponse)
                        .collect(Collectors.toList()),
                initialPlayerHealth, initialEnemyHealth));
    }

    /**
     * Helper function to update health
     * @param entity
     * @param statistics
     */
    private void updateHealth(Battleable entity, BattleStatistics statistics) {
        entity.setHealth(statistics.getHealth());
    }

    /**
     * Helper function to reduce durability
     * @param game
     * @param battleItems
     */
    private void decreaseDurabilityOfItems(Game game, List<BattleItem> battleItems) {
        for (BattleItem item : battleItems) {
            if (item instanceof InventoryItem) {
                item.use(game);
            }
        }
    }

    public List<BattleResponse> getBattleResponses() {
        return battleResponses;
    }
}
