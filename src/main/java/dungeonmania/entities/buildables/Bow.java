package dungeonmania.entities.buildables;

import dungeonmania.battles.BattleStatistics;

public class Bow extends Buildable {
    public static final int DEFAULT_HEALTH = 0;
    public static final int DEFAULT_ATTACK = 0;
    public static final int DEFAULT_DEFENCE = 0;
    public static final int DEFAULT_ATTACK_MAGNIFIER = 2;
    public static final int DEFAULT_DAMAGE_REDUCTION = 1;

    public Bow(int durability) {
        super(null, durability);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(DEFAULT_HEALTH, DEFAULT_ATTACK, DEFAULT_DEFENCE,
                DEFAULT_ATTACK_MAGNIFIER, DEFAULT_DAMAGE_REDUCTION));
    }

}
