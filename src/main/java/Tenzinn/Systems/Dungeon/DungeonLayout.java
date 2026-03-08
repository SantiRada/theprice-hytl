package Tenzinn.Systems.Dungeon;

import com.hypixel.hytale.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DungeonLayout {

    private static final int TOTAL_ROOMS  = 10;
    private static final int ROOM_SPACING = 8;
    private static final int MAX_ATTEMPTS = 1000;

    private final WorldConfig config;

    public DungeonLayout(WorldConfig config) { this.config = config; }

    public List<RoomNode> generate(Vector3i startOrigin) {
        List<RoomNode> rooms = new ArrayList<>();
        rooms.add(buildRoom(0, startOrigin, null));

        int attempts = 0;
        while (rooms.size() < TOTAL_ROOMS && attempts < MAX_ATTEMPTS) {
            attempts++;
            RoomNode parent = rooms.get(ThreadLocalRandom.current().nextInt(rooms.size()));
            List<Direction> candidates = availableDirections(parent);
            if (candidates.isEmpty()) continue;

            Direction dir      = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
            int       childSizeX = randomBetween(config.size.min.x, config.size.max.x);
            int       childSizeZ = randomBetween(config.size.min.z, config.size.max.z);
            Vector3i  newOrigin  = computeOrigin(parent, dir, childSizeX, childSizeZ);

            if (overlapsAny(newOrigin, childSizeX, childSizeZ, rooms)) continue;

            RoomNode child = buildRoom(rooms.size(), newOrigin, dir.opposite());
            child.sizeX = childSizeX;
            child.sizeZ = childSizeZ;
            connectRooms(parent, child, dir);
            rooms.add(child);
        }
        return rooms;
    }

    private void connectRooms(RoomNode parent, RoomNode child, Direction dir) {
        parent.connections.put(dir, child);
        child.connections.put(dir.opposite(), parent);

        int alignAxis;
        if (dir == Direction.NORTH || dir == Direction.SOUTH) {
            int overlapMin = Math.max(parent.origin.x + 2, child.origin.x + 2);
            int overlapMax = Math.min(parent.origin.x + parent.sizeX - 3, child.origin.x + child.sizeX - 3);
            alignAxis = (overlapMin > overlapMax)
                ? parent.origin.x + parent.sizeX / 2
                : (overlapMin + overlapMax) / 2;
        } else {
            int overlapMin = Math.max(parent.origin.z + 2, child.origin.z + 2);
            int overlapMax = Math.min(parent.origin.z + parent.sizeZ - 3, child.origin.z + child.sizeZ - 3);
            alignAxis = (overlapMin > overlapMax)
                ? parent.origin.z + parent.sizeZ / 2
                : (overlapMin + overlapMax) / 2;
        }

        parent.doorAlignAxis.put(dir, alignAxis);
        child.doorAlignAxis.put(dir.opposite(), alignAxis);
    }

    private RoomNode buildRoom(int id, Vector3i origin, Direction entryDir) {
        RoomNode room       = new RoomNode(id);
        room.origin         = origin;
        room.sizeX          = randomBetween(config.size.min.x, config.size.max.x);
        room.sizeY          = randomBetween(config.size.min.y, config.size.max.y);
        room.sizeZ          = randomBetween(config.size.min.z, config.size.max.z);
        room.entryDirection = entryDir;

        room.selectedGrounds       = pickRandom(config.content.grounds,       3);
        room.selectedWalls         = pickRandom(config.content.walls,         3);
        room.selectedAccents       = pickRandom(config.content.accents,       2);
        room.selectedDecorations   = pickRandom(config.content.decoratives,   3);
        room.selectedRoofs         = pickRandom(config.content.roofs,         2);
        room.selectedRoots         = pickRandom(config.content.roots,         3);
        room.selectedPlants        = pickRandom(config.content.plants,        3);
        room.selectedRubbles       = pickRandom(config.content.rubbles,       2);
        room.selectedLanternsFloor = pickRandom(config.content.lanterns_floor, 1);
        room.selectedPillarBases   = pickRandom(config.content.pillar_bases,   1);
        room.selectedPillarMiddles = pickRandom(config.content.pillar_middles, 1);

        return room;
    }

    private List<Direction> availableDirections(RoomNode room) {
        List<Direction> available = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (!room.connections.containsKey(dir)) available.add(dir);
        }
        return available;
    }

    private Vector3i computeOrigin(RoomNode parent, Direction dir, int csX, int csZ) {
        int gap = ROOM_SPACING;
        switch (dir) {
            case NORTH: return new Vector3i(parent.origin.x, parent.origin.y, parent.origin.z - csZ - gap);
            case SOUTH: return new Vector3i(parent.origin.x, parent.origin.y, parent.origin.z + parent.sizeZ + gap);
            case WEST:  return new Vector3i(parent.origin.x - csX - gap, parent.origin.y, parent.origin.z);
            case EAST:  return new Vector3i(parent.origin.x + parent.sizeX + gap, parent.origin.y, parent.origin.z);
            default:    return parent.origin;
        }
    }

    private boolean overlapsAny(Vector3i origin, int sizeX, int sizeZ, List<RoomNode> rooms) {
        int pad = ROOM_SPACING;
        for (RoomNode r : rooms) {
            boolean ox = (r.origin.x - pad) < (origin.x + sizeX) && (r.origin.x + r.sizeX + pad) > origin.x;
            boolean oz = (r.origin.z - pad) < (origin.z + sizeZ) && (r.origin.z + r.sizeZ + pad) > origin.z;
            if (ox && oz) return true;
        }
        return false;
    }

    private int randomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private List<String> pickRandom(List<String> source, int max) {
        if (source == null || source.isEmpty()) return Collections.emptyList();
        List<String> copy = new ArrayList<>(source);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, Math.min(max, copy.size())));
    }
}
