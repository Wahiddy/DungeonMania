package dungeonmania.entities.playerState;

import dungeonmania.entities.Player;
import dungeonmania.battles.BattleStatistics;

public class BaseState extends PlayerState {
    public BaseState(Player player) {
        super(player, false, false);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return origin;
    }
}
