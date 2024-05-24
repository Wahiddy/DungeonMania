package dungeonmania.entities.buildables;

import dungeonmania.battles.BattleStatistics;

public class Shield extends Buildable {
    public static final int DEFAULT_HEALTH = 0;
    public static final int DEFAULT_ATTACK = 0;
    public static final int DEFAULT_ATTACK_MAGNIFIER = 1;
    public static final int DEFAULT_DAMAGE_REDUCTION = 1;

    private double defence;

    public Shield(int durability, double defence) {
        super(null, durability);
        this.defence = defence;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(DEFAULT_HEALTH, DEFAULT_ATTACK, defence,
                DEFAULT_ATTACK_MAGNIFIER, DEFAULT_DAMAGE_REDUCTION));
    }

}
