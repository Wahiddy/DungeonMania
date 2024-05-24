package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoorsKeysTest {
    @Test
    @Tag("4-1")
    @DisplayName("Test player cannot walk through a closed door")
    public void cannotWalkClosedDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_cannotWalkClosedDoor",
                "c_DoorsKeysTest_cannotWalkClosedDoor");
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();

        // try to walk through door and fail
        res = dmc.tick(Direction.RIGHT);
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("4-2")
    @DisplayName("Test player can pick up a key and add to inventory")
    public void pickUpKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_pickUpKey", "c_DoorsKeysTest_pickUpKey");

        assertEquals(1, TestUtils.getEntities(res, "key").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getEntities(res, "key").size());

    }

    @Test
    @Tag("4-3")
    @DisplayName("Test player can use a key to open and walk through a door")
    public void useKeyWalkThroughOpenDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useKeyWalkThroughOpenDoor",
                "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // walk through door and check key is gone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("4-4")
    @DisplayName("Test player cannot pickup two keys at the same time")
    public void cannotPickupTwoKeys() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_cannotPickupTwoKeys", "c_DoorsKeysTest_cannotPickupTwoKeys");

        assertEquals(2, TestUtils.getEntities(res, "key").size());

        // pick up key_1
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getEntities(res, "key").size());

        // pick up key_2
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getEntities(res, "key").size());
    }

    @Test
    @Tag("4-5")
    @DisplayName("Test player can pick up a second key after using the first")
    public void canPickupSecondKeyAfterFirstUse() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_canPickupSecondKeyAfterFirstUse",
                "c_DoorsKeysTest_canPickupSecondKeyAfterFirstUse");

        assertEquals(2, TestUtils.getEntities(res, "key").size());

        // pick up key_1
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getEntities(res, "key").size());

        // walk through door_1
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getEntities(res, "key").size());

        // pick up key_2
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(0, TestUtils.getEntities(res, "key").size());
    }

    @Test
    @Tag("4-6")
    @DisplayName("Test player cannot open a door with the wrong key")
    public void cannotOpenDoorWithWrongKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_cannotOpenDoorWithWrongKey",
                "c_DoorsKeysTest_cannotOpenDoorWithWrongKey");

        // pick up key_1
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // cannot walk through door_2
        res = dmc.tick(Direction.RIGHT);
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
    }

    @Test
    @Tag("4-7")
    @DisplayName("Test doors remain open and the player can move through the door without a key")
    public void doorRemainsOpen() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_doorRemainsOpen", "c_DoorsKeysTest_doorRemainsOpen");

        // pick up key
        res = dmc.tick(Direction.RIGHT);

        // open door
        res = dmc.tick(Direction.RIGHT);

        // player no longer has a key but can move freely through door
        assertEquals(0, TestUtils.getInventory(res, "key").size());

        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
        pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.LEFT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
        pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.LEFT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("4-8")
    @DisplayName("Test player can pick up a Sunstone and add to inventory")
    public void pickUpSunstone() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_pickUpSunstone", "c_DoorsKeysTest_pickUpSunstone");

        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
    }

    @Test
    @Tag("4-9")
    @DisplayName("Test player can use a Sun Stone to open and walk through a door")
    public void useSunstoneWalkThroughOpenDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useSunstoneWalkThroughOpenDoor",
                "c_DoorsKeysTest_useSunstoneWalkThroughOpenDoor");

        // pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // walk through door and check Sun Stone is still there
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("4-10")
    @DisplayName("Test player prioritises keys over sun stones to open doors")
    public void prioritiseKeyOverSunstone() {
        // This behaviour is based on an assumption that keys take the priority over sun stones when
        // opening doors
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_prioritiseKeyOverSunstone",
                "c_DoorsKeysTest_prioritiseKeyOverSunstone");

        // pick up Sun Stone
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // pick up key
        res = dmc.tick(Direction.DOWN);
        Position pos1 = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());

        // walk through door and check that the key has been used, not the sun stone
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertNotEquals(pos1, TestUtils.getEntities(res, "player").get(0).getPosition());
    }
}
