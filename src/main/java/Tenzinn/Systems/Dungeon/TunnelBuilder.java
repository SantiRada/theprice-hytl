package Tenzinn.Systems.Dungeon;

import com.hypixel.hytale.server.core.universe.world.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TunnelBuilder {

    private static final int    HALF             = 1;
    private static final int    TUNNEL_HEIGHT    = 3;
    private static final int    INVISIBLE_LAYERS = 2;
    private static final String INV              = "Invisible_Block";

    public void buildTunnel(World world, RoomNode from, RoomNode to, Direction dir,
                            List<String> wallBlocks, List<String> floorBlocks) {
        String wall  = pickOrDefault(wallBlocks,  "Rock_Stone");
        String floor = pickOrDefault(floorBlocks, "Rock_Stone_Cobble");

        if (dir == Direction.NORTH || dir == Direction.SOUTH) {
            buildNSTunnel(world, from, to, dir, wall, floor);
        } else {
            buildEWTunnel(world, from, to, dir, wall, floor);
        }
    }

    private void buildNSTunnel(World world, RoomNode from, RoomNode to, Direction dir,
                               String wall, String floor) {
        int cx     = from.doorAlignAxis.containsKey(dir)
                     ? from.doorAlignAxis.get(dir)
                     : from.doorAlignAxis.get(dir.opposite());
        int baseY  = from.origin.y;
        int totalH = TUNNEL_HEIGHT + INVISIBLE_LAYERS;

        // Gap = the space strictly between the two room outer walls (backing layers)
        int fromSouth = from.origin.z + from.sizeZ;   // first z outside 'from' south wall
        int toNorth   = to.origin.z - 1;               // last z before 'to' north wall backing
        int fromNorth = from.origin.z - 1;             // last z before 'from' north wall backing
        int toSouth   = to.origin.z + to.sizeZ;        // first z outside 'to' south wall

        int startZ, endZ;
        if (dir == Direction.SOUTH) {
            startZ = fromSouth;
            endZ   = toNorth;
        } else {
            startZ = toSouth;
            endZ   = fromNorth;
        }
        if (startZ > endZ) { int t = startZ; startZ = endZ; endZ = t; }
        if (startZ > endZ) return; // no gap, rooms touch — nothing to build

        for (int z = startZ; z <= endZ; z++) {
            for (int dx = -HALF; dx <= HALF; dx++) {
                world.setBlock(cx + dx, baseY - 1, z, floor);
            }
            world.setBlock(cx - (HALF + 1), baseY - 1, z, wall);
            world.setBlock(cx + (HALF + 1), baseY - 1, z, wall);

            for (int dy = 0; dy < totalH; dy++) {
                boolean vis = dy < TUNNEL_HEIGHT;
                world.setBlock(cx - (HALF + 1), baseY + dy, z, vis ? wall : INV);
                world.setBlock(cx + (HALF + 1), baseY + dy, z, vis ? wall : INV);
                for (int dx = -HALF; dx <= HALF; dx++) {
                    world.setBlock(cx + dx, baseY + dy, z, "Empty");
                }
            }
            for (int dx = -(HALF + 1); dx <= HALF + 1; dx++) {
                world.setBlock(cx + dx, baseY + totalH, z, INV);
            }
        }
    }

    private void buildEWTunnel(World world, RoomNode from, RoomNode to, Direction dir,
                               String wall, String floor) {
        int cz     = from.doorAlignAxis.containsKey(dir)
                     ? from.doorAlignAxis.get(dir)
                     : from.doorAlignAxis.get(dir.opposite());
        int baseY  = from.origin.y;
        int totalH = TUNNEL_HEIGHT + INVISIBLE_LAYERS;

        int fromEast  = from.origin.x + from.sizeX;
        int toWest    = to.origin.x - 1;
        int fromWest  = from.origin.x - 1;
        int toEast    = to.origin.x + to.sizeX;

        int startX, endX;
        if (dir == Direction.EAST) {
            startX = fromEast;
            endX   = toWest;
        } else {
            startX = toEast;
            endX   = fromWest;
        }
        if (startX > endX) { int t = startX; startX = endX; endX = t; }
        if (startX > endX) return;

        for (int x = startX; x <= endX; x++) {
            for (int dz = -HALF; dz <= HALF; dz++) {
                world.setBlock(x, baseY - 1, cz + dz, floor);
            }
            world.setBlock(x, baseY - 1, cz - (HALF + 1), wall);
            world.setBlock(x, baseY - 1, cz + (HALF + 1), wall);

            for (int dy = 0; dy < totalH; dy++) {
                boolean vis = dy < TUNNEL_HEIGHT;
                world.setBlock(x, baseY + dy, cz - (HALF + 1), vis ? wall : INV);
                world.setBlock(x, baseY + dy, cz + (HALF + 1), INV);
                for (int dz = -HALF; dz <= HALF; dz++) {
                    world.setBlock(x, baseY + dy, cz + dz, "Empty");
                }
            }
            for (int dz = -(HALF + 1); dz <= HALF + 1; dz++) {
                world.setBlock(x, baseY + totalH, cz + dz, INV);
            }
        }
    }

    private String pickOrDefault(List<String> list, String fallback) {
        if (list == null || list.isEmpty()) return fallback;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
