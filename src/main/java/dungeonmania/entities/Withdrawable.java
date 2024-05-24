package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface Withdrawable {
    void onMovedAway(GameMap map, Entity entity);
}
