package Tenzinn.Systems.Dungeon;

import com.hypixel.hytale.math.vector.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomNode {

    public final int id;
    public Vector3i origin;
    public int sizeX, sizeY, sizeZ;

    public Direction entryDirection;
    public final Map<Direction, RoomNode> connections   = new HashMap<>();
    public final Map<Direction, Integer>  doorAlignAxis = new HashMap<>();

    public List<String> selectedGrounds;
    public List<String> selectedWalls;
    public List<String> selectedAccents;
    public List<String> selectedDecorations;
    public List<String> selectedRoofs;
    public List<String> selectedRoots;
    public List<String> selectedPlants;
    public List<String> selectedRubbles;
    public List<String> selectedLanternsFloor;
    public List<String> selectedPillarBases;
    public List<String> selectedPillarMiddles;

    public RoomNode(int id) { this.id = id; }

    public boolean hasExit(Direction dir) { return connections.containsKey(dir); }

    public Vector3i getCenter() {
        return new Vector3i(origin.x + sizeX / 2, origin.y, origin.z + sizeZ / 2);
    }

    public boolean containsXZ(int px, int pz) {
        return px >= origin.x && px < origin.x + sizeX
            && pz >= origin.z && pz < origin.z + sizeZ;
    }

    public int getDoorWallCoord(Direction dir) {
        switch (dir) {
            case NORTH: return origin.z;
            case SOUTH: return origin.z + sizeZ - 1;
            case WEST:  return origin.x;
            case EAST:  return origin.x + sizeX - 1;
            default:    return 0;
        }
    }

    public int getDoorBackingCoord(Direction dir) {
        switch (dir) {
            case NORTH: return origin.z - 1;
            case SOUTH: return origin.z + sizeZ;
            case WEST:  return origin.x - 1;
            case EAST:  return origin.x + sizeX;
            default:    return 0;
        }
    }
}
