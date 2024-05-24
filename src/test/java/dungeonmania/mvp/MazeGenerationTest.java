package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MazeGenerationTest {
    @Test
    @Tag("16-1")
    @DisplayName("Test if goal can be achieved in this maze")
    public void testGoalAchievableInMaze() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateGame(0, 0, 0, 1, "c_basicGoalsTest_exit");

        assertEquals(1, TestUtils.getEntities(res, "player").size());

        assertEquals(1, TestUtils.getEntities(res, "exit").size());

        // assert not goal met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // go to goal
        res = dmc.tick(Direction.DOWN);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("16-2")
    @DisplayName("Test appropriate positions of player and exit")
    public void testPlayerAndExitCorrectPos() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateGame(0, 0, 9, 9, "c_basicGoalsTest_exit");

        assertEquals(1, TestUtils.getEntities(res, "player").size());
        String playerId = TestUtils.getEntities(res, "player").get(0).getId();

        assertEquals(1, TestUtils.getEntities(res, "exit").size());
        String exitId = TestUtils.getEntities(res, "exit").get(0).getId();

        // check player and exit are in correct positions
        assertEquals(playerId, TestUtils.getEntityAtPos(res, "player", new Position(0, 0)).get().getId());
        assertEquals(exitId, TestUtils.getEntityAtPos(res, "exit", new Position(9, 9)).get().getId());

        // assert not goal met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

    }

}
