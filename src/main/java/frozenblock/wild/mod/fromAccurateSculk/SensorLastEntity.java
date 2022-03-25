package frozenblock.wild.mod.fromAccurateSculk;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;

public class SensorLastEntity {

    public static final IntArrayList entityList = new IntArrayList();
    public static final ArrayList<BlockPos> sensors = new ArrayList<>();
    public static final ArrayList<BlockPos> eventPos = new ArrayList<>();
    public static final ArrayList<GameEvent> gameEvents = new ArrayList<>();

    public static void addEntity(Entity entity, BlockPos pos, BlockPos event, GameEvent gameEvent) {
        if (!sensors.isEmpty()) {
            if (sensors.contains(pos)) {
                int slot = sensors.indexOf(pos);
                entityList.set(slot, entity.getId());
                eventPos.set(slot, event);
                gameEvents.set(slot, gameEvent);
            } else {
                sensors.add(pos);
                entityList.add(entity.getId());
                eventPos.add(event);
                gameEvents.add(gameEvent);
            }
        } else {
            sensors.add(pos);
            entityList.add(entity.getId());
            eventPos.add(event);
            gameEvents.add(gameEvent);
        }
    }

    public static int getLastEntity(BlockPos pos) {
        if (!sensors.isEmpty()) {
            if (sensors.contains(pos)) {
                int slot = sensors.indexOf(pos);
                return entityList.getInt(slot);
            }
        }
        return -1;
    }

    public static BlockPos getLastPos(BlockPos pos) {
        if (!sensors.isEmpty()) {
            if (sensors.contains(pos)) {
                int slot = sensors.indexOf(pos);
                return eventPos.get(slot);
            }
        }
        return null;
    }

    public static GameEvent getLastEvent(BlockPos pos) {
        if (!sensors.isEmpty()) {
            if (sensors.contains(pos)) {
                int slot = sensors.indexOf(pos);
                return gameEvents.get(slot);
            }
        }
        return null;
    }

}
