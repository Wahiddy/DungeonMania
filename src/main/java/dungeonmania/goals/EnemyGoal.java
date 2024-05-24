package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.enemies.ZombieToastSpawner;

public class EnemyGoal implements Goal {
    private int enemyGoal;

    public EnemyGoal(int enemyGoal) {
        this.enemyGoal = enemyGoal;
    }

    @Override
    public boolean achieved(Game game) {
        return (game.getMap().getEntities(ZombieToastSpawner.class).isEmpty())
                && game.getEnemiesDestroyed() >= enemyGoal;
    }

    @Override
    public String toString(Game game) {
        if (this.achieved(game))
            return "";

        return ":enemies";
    }

}
