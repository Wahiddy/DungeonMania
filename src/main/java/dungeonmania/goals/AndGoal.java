package dungeonmania.goals;

import dungeonmania.Game;

public class AndGoal extends CompoundGoal {
    public AndGoal(Goal... expressions) {
        super(expressions);
    }

    @Override
    public boolean achieved(Game game) {
        boolean result = true;
        for (Goal goal : getChildren()) {
            result = result && goal.achieved(game);
        }
        return result;
    }

    @Override
    public String toString(Game game) {
        if (this.achieved(game))
            return "";

        String result = "(";
        int numGoals = getChildren().size();
        for (int i = 0; i < numGoals; i++) {
            Goal goal = getChildren().get(i);
            result += goal.toString(game);
            if (i < numGoals - 1) {
                result += " AND ";
            }
        }
        result += ")";
        return result;
    }

}
