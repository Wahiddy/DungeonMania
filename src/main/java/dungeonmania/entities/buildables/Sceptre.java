package dungeonmania.entities.buildables;

import dungeonmania.battles.BattleStatistics;

public class Sceptre extends Buildable {
    public static final int DEFAULT_DURATION = 6;
    public static final int DEFAULT_DURABILITY = 0;

    private int controlDuration;

    public Sceptre(int duration) {
        super(null, DEFAULT_DURABILITY);
        this.controlDuration = duration;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return origin;
    }

    public int getDuration() {
        return controlDuration;
    }

}
