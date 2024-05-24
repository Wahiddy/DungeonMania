package dungeonmania.entities.playerState;

import dungeonmania.entities.Player;
import dungeonmania.battles.BattleStatistics;

public class InvisibleState extends PlayerState {
    public InvisibleState(Player player) {
        super(player, false, true);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, 0, 1, 1, false, false));
    }
}
