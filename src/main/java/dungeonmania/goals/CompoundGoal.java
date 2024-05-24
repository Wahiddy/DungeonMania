package dungeonmania.goals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CompoundGoal implements Goal {
    private final List<Goal> children = new ArrayList<>();

    public CompoundGoal(Goal... expressions) {
        children.addAll(Arrays.asList(expressions));
    }

    public List<Goal> getChildren() {
        return children;
    }

}
