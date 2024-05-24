package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MercenaryTest {
    @Test
    @Tag("12-1")
    @DisplayName("Test mercenary in line with Player moves towards them")
    public void simpleMovement() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_simpleMovement", "c_mercenaryTest_simpleMovement");

        assertEquals(new Position(8, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getMercPos(res));
    }

    @Test
    @Tag("12-2")
    @DisplayName("Test mercenary stops if they cannot move any closer to the player")
    public void stopMovement() {
        //                  Wall     Wall    Wall
        // P1       P2      Wall      M1     Wall
        //                  Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_stopMovement", "c_mercenaryTest_stopMovement");

        Position startingPos = getMercPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getMercPos(res));
    }

    @Test
    @Tag("12-3")
    @DisplayName("Test mercenaries can not move through closed doors")
    public void doorMovement() {
        //                  Wall     Door    Wall
        // P1       P2      Wall      M1     Wall
        // Key              Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_doorMovement", "c_mercenaryTest_doorMovement");

        Position startingPos = getMercPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getMercPos(res));
    }

    @Test
    @Tag("12-4")
    @DisplayName("Test mercenary moves around a wall to get to the player")
    public void evadeWall() {
        //                  Wall      M2
        // P1       P2      Wall      M1
        //                  Wall      M2
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_evadeWall", "c_mercenaryTest_evadeWall");

        res = dmc.tick(Direction.RIGHT);
        assertTrue(new Position(4, 1).equals(getMercPos(res)) || new Position(4, 3).equals(getMercPos(res)));
    }

    @Test
    @Tag("12-5")
    @DisplayName("Testing a mercenary can be bribed with a certain amount")
    public void bribeAmount() {
        //                                                          Wall     Wall     Wall    Wall    Wall
        // P1       P2/Treasure      P3/Treasure    P4/Treasure      M4       M3       M2     M1      Wall
        //                                                          Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_bribeAmount", "c_mercenaryTest_bribeAmount");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up second treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // pick up third treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 1), getMercPos(res));

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("12-6")
    @DisplayName("Testing a mercenary can be bribed within a radius")
    public void bribeRadius() {
        //                                         Wall     Wall    Wall    Wall  Wall
        // P1       P2/Treasure      P3    P4      M4       M3       M2     M1    Wall
        //                                         Wall     Wall    Wall    Wall  Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_bribeRadius", "c_mercenaryTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // fail bribe as player is 5 positions away from the mercenary and the bribe radius is 2
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(6, 1), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // bribe still fails as player is 3 positions away from the mercenary and the bribe radius is 2
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // bribe now successful as the player is only 1 position away from the mercenary
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("12-7")
    @DisplayName("Testing an allied mercenary does not battle the player")
    public void allyBattle() {
        //                                  Wall    Wall    Wall
        // P1       P2/Treasure      .      M2      M1      Wall
        //                                  Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyBattle", "c_mercenaryTest_allyBattle");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("12-8")
    @DisplayName("Testing a mercenary is bribed next to the player, then follow the player")
    public void allyMovementStick() {
        /**
         * W W W W W W E
         * W T P - - M -
         * W W W W W W -
         *
         * bribe_radius = 100
         * bribe_amount = 1
         */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMovementStick", "c_mercenaryTest_allyMovementStick");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(4, 1), getMercPos(res));

        // Wait until the mercenary is next to the player
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(3, 1), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(2, 1), getMercPos(res));

        // achieve bribe - success
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(2, 1), getMercPos(res));

        // Ally follows the player
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(2, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(new Position(1, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getPlayerPos(res));
        assertEquals(new Position(2, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), getPlayerPos(res));
        assertEquals(new Position(3, 1), getMercPos(res));
    }

    @Test
    @Tag("12-9")
    @DisplayName("Testing an allied mercenary finds the player, then follow the player")
    public void allyMovementFollow() {
        /**
         * W W W - W W W W W E
         * P T W - - - - M W -
         * - W W - W W W W W -
         *
         * bribe_radius = 100
         * bribe_amount = 1
         */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMovementFollow", "c_mercenaryTest_allyMovementFollow");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(6, 1), getMercPos(res));

        // achieve bribe - success
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 1), getMercPos(res));

        // Mercenary uses dijkstra to find the player
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(4, 1), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(3, 1), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(3, 2), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(3, 3), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(2, 3), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(1, 3), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(0, 3), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(0, 2), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(0, 1), getMercPos(res));

        // Ally follows the player
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(0, 1), getPlayerPos(res));
        assertEquals(new Position(1, 1), getMercPos(res));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 2), getPlayerPos(res));
        assertEquals(new Position(0, 1), getMercPos(res));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 3), getPlayerPos(res));
        assertEquals(new Position(0, 2), getMercPos(res));
    }

    @Test
    @Tag("12-10")
    @DisplayName("Testing a mercenary can't be bribed with sun stones")
    public void bribeSunstone() {
        //                                                               Wall    Wall     Wall     Wall    Wall    Wall
        // P1    P2/Treasure    P3/Treasure   P4/Sunstone    P5/Treasure  M5      M4       M3       M2      M1     Wall
        //                                                               Wall    Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_bribeSunstone",
                 "c_mercenaryTest_bribeSunstone");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(9, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up second treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(8, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // pick up third treasure being a sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // bribe attempt will fail as sunstones don't count
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // pick up fourth treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    @Test
    @Tag("12-11")
    @DisplayName("Testing a mercenary being mind controlled when a player uses a sceptre")
    public void mindControlSceptre() {
        //                                                           Wall    Wall     Wall     Wall    Wall    Wall
        // P1    P2/Wood    P3/Treasure   P4/Sunstone                 M5      M4       M3       M2      M1     Wall
        //                                                           Wall    Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_MindControlSceptre",
                "c_mercenaryTest_MindControlSceptre");

        // mind_control_duration = 5
        // bribe_amount = 3
        // bribe_radius = 1

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(new Position(9, 1), getMercPos(res));

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(8, 1), getMercPos(res));

        // pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // Mind control fails, as a sceptre hasn't been built
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // Achieve mind control now that the sceptre is made, mercenary is now allied
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(new Position(5, 1), getMercPos(res));

        // For the next 4 movements, the player shouldn't battle the ally despite walking into it
        // due to the value of mind_control_duration
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getPlayerPos(res));
        assertEquals(new Position(4, 1), getMercPos(res));
        assertEquals(0, res.getBattles().size());

        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(4, 1), getPlayerPos(res));
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(0, res.getBattles().size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getPlayerPos(res));
        assertEquals(new Position(4, 1), getMercPos(res));
        assertEquals(0, res.getBattles().size());

        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(4, 1), getPlayerPos(res));
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(0, res.getBattles().size());

        // The mind control duration will expire after the tick and walking into
        // the mercenary results in a battle
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getPlayerPos(res));
        assertEquals(new Position(5, 1), getPlayerPos(res));
        assertEquals(1, res.getBattles().size());
    }

    @Test
    @Tag("12-12")
    @DisplayName("When given the option to bribe or mind control, mind control the mercenary")
    public void mindControlInsteadOfBribe() {
        //                                                       Wall    Wall     Wall     Wall    Wall    Wall   Wall
        // P1  P2/Wood  P3/Treasure  P4/Treasure  P5/Sunstone             M5       M4       M3      M2      M1    Wall
        //                                                       Wall    Wall     Wall     Wall    Wall    Wall   Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_MindControlInsteadOfBribe",
                "c_mercenaryTest_MindControlInsteadOfBribe");

        // mind_control_duration = 5
        // bribe_amount = 1
        // bribe_radius = 3

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(new Position(10, 1), getMercPos(res));

        // pick up treasure x 2
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(8, 1), getMercPos(res));

        // pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // Mind control upon interaction, not bribe, based on the assumption that in this
        // case mind control will take the priority
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(new Position(4, 1), getMercPos(res));
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());

        // For the following 4 movements, the player shouldn't battle the ally despite walking into it
        // due to the the allied period being limited as the merc is mind controlled
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(4, 1), getPlayerPos(res));
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(0, res.getBattles().size());

        // Trying to interact will fail, as the mercenary is currently allied
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getPlayerPos(res));
        assertEquals(new Position(4, 1), getMercPos(res));
        assertEquals(0, res.getBattles().size());

        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(4, 1), getPlayerPos(res));
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(0, res.getBattles().size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getPlayerPos(res));
        assertEquals(new Position(4, 1), getMercPos(res));
        assertEquals(0, res.getBattles().size());

        // Trying to interact will fail again, as the mind control duration hasn't
        // expired still, meaning the mercenary is still allied
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        // This is the 5th (mind_control_duration = 5) and final movement under the mind control
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(5, 2), getPlayerPos(res));
        assertEquals(new Position(5, 1), getMercPos(res));

        // The mind control duration has now expired, the player can now interact
        // again via a bribe using the 1 treasure that remains, which will be gone
        // after the bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }
}
