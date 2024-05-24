package dungeonmania.entities.playerState;

import dungeonmania.entities.Player;
import dungeonmania.battles.BattleStatistics;

public class InvincibleState extends PlayerState {
    public InvincibleState(Player player) {
        super(player, true, false);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, 0, 1, 1, true, true));
    }
}
