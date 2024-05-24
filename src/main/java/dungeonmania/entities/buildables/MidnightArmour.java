package dungeonmania.entities.buildables;

import dungeonmania.battles.BattleStatistics;

public class MidnightArmour extends Buildable {
    public static final int DEFAULT_HEALTH = 0;
    public static final int DEFAULT_ATTACK_MAGNIFIER = 1;
    public static final int DEFAULT_DAMAGE_REDUCTION = 1;
    public static final int DEFAULT_DURABILITY = 0;

    private int attack;
    private int defence;

    public MidnightArmour(int attack, int defence) {
        super(null, DEFAULT_DURABILITY);
        this.attack = attack;
        this.defence = defence;
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(DEFAULT_HEALTH, attack, defence,
                DEFAULT_ATTACK_MAGNIFIER, DEFAULT_DAMAGE_REDUCTION));
    }

}
