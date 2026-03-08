package Tenzinn.Systems.Dungeon;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RoomBuilder {

    private static final int    DOOR_WIDTH       = 3;
    private static final int    VISIBLE_HEIGHT   = 4;
    private static final int    INVISIBLE_LAYERS = 2;
    private static final String INV              = "Invisible_Block";

    public void build(World world, RoomNode room) {
        buildFloor(world, room);
        buildWalls(world, room);
        buildInvisibleCeiling(world, room);
        fillInterior(world, room);
        carveConnections(world, room);
        addCornerPillars(world, room);
        addLanterns(world, room);
        addRoomFeature(world, room);
        addDecorations(world, room);
    }

    // ── Structure ────────────────────────────────────────────────────────────

    private void buildFloor(World world, RoomNode room) {
        String primary = pickSolid(room.selectedGrounds);
        String accent  = pickSolid(room.selectedAccents);
        for (int x = 0; x < room.sizeX; x++) {
            for (int z = 0; z < room.sizeZ; z++) {
                boolean border = x == 1 || x == room.sizeX - 2 || z == 1 || z == room.sizeZ - 2;
                world.setBlock(room.origin.x + x, room.origin.y - 1, room.origin.z + z, border ? accent : primary);
            }
        }
    }

    private void buildWalls(World world, RoomNode room) {
        String solid  = pickSolid(room.selectedGrounds);
        String accent = pickSolid(room.selectedAccents);
        int totalH    = room.sizeY + INVISIBLE_LAYERS;

        for (int y = 0; y < totalH; y++) {
            boolean visible = y < VISIBLE_HEIGHT;
            String deco    = visible ? (y % 3 == 1 ? pick(room.selectedAccents) : pick(room.selectedWalls)) : INV;
            String backing = visible ? (y % 3 == 1 ? accent : solid) : INV;

            for (int x = 0; x < room.sizeX; x++) {
                world.setBlock(room.origin.x + x, room.origin.y + y, room.origin.z - 1,             backing);
                world.setBlock(room.origin.x + x, room.origin.y + y, room.origin.z,                 deco);
                world.setBlock(room.origin.x + x, room.origin.y + y, room.origin.z + room.sizeZ,     INV);
                world.setBlock(room.origin.x + x, room.origin.y + y, room.origin.z + room.sizeZ - 1, INV);
            }
            for (int z = 0; z < room.sizeZ; z++) {
                world.setBlock(room.origin.x - 1,              room.origin.y + y, room.origin.z + z, backing);
                world.setBlock(room.origin.x,                  room.origin.y + y, room.origin.z + z, deco);
                world.setBlock(room.origin.x + room.sizeX,     room.origin.y + y, room.origin.z + z, backing);
                world.setBlock(room.origin.x + room.sizeX - 1, room.origin.y + y, room.origin.z + z, deco);
            }
        }
    }

    private void buildInvisibleCeiling(World world, RoomNode room) {
        int topY = room.origin.y + room.sizeY + INVISIBLE_LAYERS;
        for (int x = 0; x < room.sizeX; x++) {
            for (int z = 0; z < room.sizeZ; z++) {
                world.setBlock(room.origin.x + x, topY, room.origin.z + z, INV);
            }
        }
        addRoofRoots(world, room);
    }

    private void addRoofRoots(World world, RoomNode room) {
        if (room.selectedRoots.isEmpty()) return;
        int qty = ThreadLocalRandom.current().nextInt(4, 10);
        for (int i = 0; i < qty; i++) {
            int x = ThreadLocalRandom.current().nextInt(1, room.sizeX - 1);
            int z = ThreadLocalRandom.current().nextInt(1, room.sizeZ - 1);
            world.setBlock(room.origin.x + x, room.origin.y + room.sizeY - 1, room.origin.z + z, pick(room.selectedRoots));
        }
    }

    private void fillInterior(World world, RoomNode room) {
        int totalH = room.sizeY + INVISIBLE_LAYERS;
        for (int y = 0; y < totalH; y++) {
            for (int x = 1; x < room.sizeX - 1; x++) {
                for (int z = 1; z < room.sizeZ - 1; z++) {
                    world.setBlock(room.origin.x + x, room.origin.y + y, room.origin.z + z, "Empty");
                }
            }
        }
    }

    // ── Connections ──────────────────────────────────────────────────────────

    private void carveConnections(World world, RoomNode room) {
        for (Direction dir : room.connections.keySet()) {
            carveDoor(world, room, dir);
        }
    }

    private void carveDoor(World world, RoomNode room, Direction dir) {
        int align  = room.doorAlignAxis.get(dir);
        int baseY  = room.origin.y;
        int half   = DOOR_WIDTH / 2;
        int totalH = room.sizeY + INVISIBLE_LAYERS;
        int wall   = room.getDoorWallCoord(dir);
        int back   = room.getDoorBackingCoord(dir);

        switch (dir) {
            case NORTH: case SOUTH:
                for (int dx = -half; dx <= half; dx++) {
                    for (int dy = 0; dy < totalH; dy++) {
                        world.setBlock(align + dx, baseY + dy, wall, "Empty");
                        world.setBlock(align + dx, baseY + dy, back, "Empty");
                    }
                }
                break;
            case WEST: case EAST:
                for (int dz = -half; dz <= half; dz++) {
                    for (int dy = 0; dy < totalH; dy++) {
                        world.setBlock(wall, baseY + dy, align + dz, "Empty");
                        world.setBlock(back, baseY + dy, align + dz, "Empty");
                    }
                }
                break;
        }
    }

    // ── Pillars & Lanterns ───────────────────────────────────────────────────

    private void buildPillar(World world, RoomNode room, int wx, int wz) {
        String base   = room.selectedPillarBases.isEmpty()   ? pickSolid(room.selectedWalls) : pick(room.selectedPillarBases);
        String middle = room.selectedPillarMiddles.isEmpty() ? pickSolid(room.selectedWalls) : pick(room.selectedPillarMiddles);
        int baseY = room.origin.y;

        world.setBlock(wx, baseY,     wz, base);
        for (int y = 1; y < VISIBLE_HEIGHT - 1; y++) {
            world.setBlock(wx, baseY + y, wz, middle);
        }
        world.setBlock(wx, baseY + VISIBLE_HEIGHT - 1, wz, base);
    }

    private void addCornerPillars(World world, RoomNode room) {
        if (room.selectedPillarBases.isEmpty()) return;
        int[][] corners = {
            {room.origin.x + 1, room.origin.z + 1},
            {room.origin.x + room.sizeX - 2, room.origin.z + 1},
            {room.origin.x + 1, room.origin.z + room.sizeZ - 2},
            {room.origin.x + room.sizeX - 2, room.origin.z + room.sizeZ - 2}
        };
        for (int[] c : corners) {
            if (!isNearDoor(room, c[0], c[1])) {
                buildPillar(world, room, c[0], c[1]);
            }
        }
    }

    private void addLanterns(World world, RoomNode room) {
        if (room.selectedLanternsFloor.isEmpty()) return;
        String lantern = pick(room.selectedLanternsFloor);
        int margin   = 3;
        int baseY    = room.origin.y;

        int intervalX = Math.max(4, room.sizeX / 3);
        for (int x = room.origin.x + margin; x < room.origin.x + room.sizeX - margin; x += intervalX) {
            if (!isNearDoor(room, x, room.origin.z + 1)) {
                world.setBlock(x, baseY, room.origin.z + 2, lantern);
            }
        }
        int intervalZ = Math.max(4, room.sizeZ / 3);
        for (int z = room.origin.z + margin; z < room.origin.z + room.sizeZ - margin; z += intervalZ) {
            if (!isNearDoor(room, room.origin.x + 1, z)) {
                world.setBlock(room.origin.x + 2, baseY, z, lantern);
            }
        }
    }

    private boolean isNearDoor(RoomNode room, int wx, int wz) {
        for (Direction dir : room.connections.keySet()) {
            int align = room.doorAlignAxis.get(dir);
            int half  = DOOR_WIDTH / 2 + 2;
            if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                if (Math.abs(wx - align) <= half) return true;
            } else {
                if (Math.abs(wz - align) <= half) return true;
            }
        }
        return false;
    }

    // ── Room Features ────────────────────────────────────────────────────────

    private void addRoomFeature(World world, RoomNode room) {
        int roll = ThreadLocalRandom.current().nextInt(6);
        switch (roll) {
            case 0: buildRaisedPlatform(world, room);  break;
            case 1: buildStaircaseCorner(world, room); break;
            case 2: buildSunkenPit(world, room);       break;
            case 3: buildCentralPillar(world, room);   break;
            case 4: buildMultiLevel(world, room);      break;
            case 5: buildRuinedColumns(world, room);   break;
        }
    }

    private void buildRaisedPlatform(World world, RoomNode room) {
        if (room.sizeX < 8 || room.sizeZ < 8) return;
        String solid  = pickSolid(room.selectedGrounds);
        String accent = pickSolid(room.selectedAccents);
        int margin = 3;
        int pw = ThreadLocalRandom.current().nextInt(3, room.sizeX / 2);
        int pd = ThreadLocalRandom.current().nextInt(3, room.sizeZ / 2);
        int px = room.origin.x + ThreadLocalRandom.current().nextInt(margin, room.sizeX - pw - margin);
        int pz = room.origin.z + ThreadLocalRandom.current().nextInt(margin, room.sizeZ - pd - margin);

        for (int x = px; x < px + pw; x++) {
            for (int z = pz; z < pz + pd; z++) {
                boolean edge = x == px || x == px + pw - 1 || z == pz || z == pz + pd - 1;
                world.setBlock(x, room.origin.y, z, edge ? accent : solid);
                world.setBlock(x, room.origin.y + 1, z, "Empty");
            }
        }
        world.setBlock(px + pw / 2,     room.origin.y - 1, pz - 1, solid);
        world.setBlock(px + pw / 2 + 1, room.origin.y - 1, pz - 1, solid);
        if (!room.selectedLanternsFloor.isEmpty()) {
            world.setBlock(px + pw / 2, room.origin.y + 1, pz + pd / 2, pick(room.selectedLanternsFloor));
        }
    }

    private void buildStaircaseCorner(World world, RoomNode room) {
        if (room.sizeX < 6 || room.sizeZ < 6) return;
        String solid = pickSolid(room.selectedGrounds);
        int baseX = room.origin.x + 2;
        int baseZ = room.origin.z + 2;
        int baseY = room.origin.y - 1;
        int steps = Math.min(3, VISIBLE_HEIGHT - 1);

        for (int s = 0; s < steps; s++) {
            int wy = baseY + s;
            for (int fill = baseY; fill <= wy; fill++) {
                world.setBlock(baseX + s, fill,     baseZ,     solid);
                world.setBlock(baseX + s, fill,     baseZ + 1, solid);
                world.setBlock(baseX + s, fill + 1, baseZ,     "Empty");
                world.setBlock(baseX + s, fill + 1, baseZ + 1, "Empty");
            }
        }
        int topX = baseX + steps;
        int topY = baseY + steps;
        for (int x = topX; x < topX + 3 && x < room.origin.x + room.sizeX - 2; x++) {
            for (int z = baseZ; z < baseZ + 3 && z < room.origin.z + room.sizeZ - 2; z++) {
                world.setBlock(x, topY, z, solid);
                world.setBlock(x, topY + 1, z, "Empty");
            }
        }
        if (!room.selectedLanternsFloor.isEmpty()) {
            world.setBlock(topX, topY + 1, baseZ, pick(room.selectedLanternsFloor));
        }
    }

    private void buildSunkenPit(World world, RoomNode room) {
        if (room.sizeX < 8 || room.sizeZ < 8) return;
        String solid  = pickSolid(room.selectedGrounds);
        String accent = pickSolid(room.selectedAccents);
        int margin = 3;
        int pw = ThreadLocalRandom.current().nextInt(3, room.sizeX / 2);
        int pd = ThreadLocalRandom.current().nextInt(3, room.sizeZ / 2);
        int px = room.origin.x + ThreadLocalRandom.current().nextInt(margin, room.sizeX - pw - margin);
        int pz = room.origin.z + ThreadLocalRandom.current().nextInt(margin, room.sizeZ - pd - margin);

        for (int x = px; x < px + pw; x++) {
            for (int z = pz; z < pz + pd; z++) {
                world.setBlock(x, room.origin.y - 2, z, accent);
                world.setBlock(x, room.origin.y - 1, z, "Empty");
                world.setBlock(x, room.origin.y,     z, "Empty");
            }
        }
        for (int x = px - 1; x <= px + pw; x++) {
            world.setBlock(x, room.origin.y - 1, pz - 1,  solid);
            world.setBlock(x, room.origin.y - 1, pz + pd, solid);
        }
        for (int z = pz; z < pz + pd; z++) {
            world.setBlock(px - 1,  room.origin.y - 1, z, solid);
            world.setBlock(px + pw, room.origin.y - 1, z, solid);
        }
        if (!room.selectedPlants.isEmpty()) {
            world.setBlock(px + pw / 2, room.origin.y - 1, pz + pd / 2, pick(room.selectedPlants));
        }
    }

    private void buildCentralPillar(World world, RoomNode room) {
        int cx    = room.origin.x + room.sizeX / 2;
        int cz    = room.origin.z + room.sizeZ / 2;
        String accent = pickSolid(room.selectedAccents);

        buildPillar(world, room, cx,     cz);
        buildPillar(world, room, cx + 1, cz);
        buildPillar(world, room, cx,     cz + 1);
        buildPillar(world, room, cx + 1, cz + 1);

        for (int dx = -1; dx <= 2; dx++) {
            for (int dz = -1; dz <= 2; dz++) {
                if (dx == -1 || dx == 2 || dz == -1 || dz == 2) {
                    world.setBlock(cx + dx, room.origin.y - 1, cz + dz, accent);
                }
            }
        }
        if (!room.selectedLanternsFloor.isEmpty()) {
            world.setBlock(cx - 1, room.origin.y, cz,     pick(room.selectedLanternsFloor));
            world.setBlock(cx + 2, room.origin.y, cz + 1, pick(room.selectedLanternsFloor));
        }
    }

    private void buildMultiLevel(World world, RoomNode room) {
        if (room.sizeX < 10 || room.sizeZ < 10) return;
        String solid  = pickSolid(room.selectedGrounds);
        String accent = pickSolid(room.selectedAccents);
        int raiseX = room.origin.x + room.sizeX / 2;

        for (int x = raiseX; x < room.origin.x + room.sizeX - 1; x++) {
            for (int z = room.origin.z + 1; z < room.origin.z + room.sizeZ - 1; z++) {
                boolean edge = z == room.origin.z + 1 || z == room.origin.z + room.sizeZ - 2;
                world.setBlock(x, room.origin.y + 1, z, edge ? accent : solid);
                world.setBlock(x, room.origin.y + 2, z, "Empty");
            }
        }
        int bridgeZ = room.origin.z + room.sizeZ / 2;
        world.setBlock(raiseX - 1, room.origin.y,     bridgeZ, solid);
        world.setBlock(raiseX,     room.origin.y + 1, bridgeZ, solid);
        world.setBlock(raiseX + 1, room.origin.y + 2, bridgeZ, "Empty");
        for (int z = room.origin.z + 1; z < room.origin.z + room.sizeZ - 1; z++) {
            if (z == bridgeZ) continue;
            world.setBlock(raiseX - 1, room.origin.y,     z, solid);
            world.setBlock(raiseX - 1, room.origin.y + 1, z, solid);
        }
        if (!room.selectedLanternsFloor.isEmpty()) {
            world.setBlock(raiseX + 2, room.origin.y + 2, bridgeZ, pick(room.selectedLanternsFloor));
        }
    }

    private void buildRuinedColumns(World world, RoomNode room) {
        if (room.sizeX < 10 || room.sizeZ < 10) return;
        int[][] positions = {
            {room.origin.x + room.sizeX / 4,         room.origin.z + room.sizeZ / 4},
            {room.origin.x + 3 * room.sizeX / 4 - 1, room.origin.z + room.sizeZ / 4},
            {room.origin.x + room.sizeX / 4,         room.origin.z + 3 * room.sizeZ / 4 - 1},
            {room.origin.x + 3 * room.sizeX / 4 - 1, room.origin.z + 3 * room.sizeZ / 4 - 1}
        };
        for (int[] p : positions) {
            if (isNearDoor(room, p[0], p[1])) continue;
            int height = ThreadLocalRandom.current().nextInt(2, VISIBLE_HEIGHT);
            String base   = room.selectedPillarBases.isEmpty()   ? pickSolid(room.selectedWalls) : pick(room.selectedPillarBases);
            String middle = room.selectedPillarMiddles.isEmpty() ? pickSolid(room.selectedWalls) : pick(room.selectedPillarMiddles);
            for (int y = 0; y < height; y++) {
                world.setBlock(p[0], room.origin.y + y, p[1], y == 0 ? base : middle);
            }
            if (!room.selectedRubbles.isEmpty()) {
                world.setBlock(p[0] + 1, room.origin.y, p[1],     pick(room.selectedRubbles));
                world.setBlock(p[0],     room.origin.y, p[1] + 1, pick(room.selectedRubbles));
            }
        }
    }

    // ── Decorations ──────────────────────────────────────────────────────────

    private void addDecorations(World world, RoomNode room) {
        addRubbles(world, room);
        addPlants(world, room);
        addWallDecoratives(world, room);
    }

    private void addRubbles(World world, RoomNode room) {
        if (room.selectedRubbles.isEmpty()) return;
        int qty = ThreadLocalRandom.current().nextInt(3, 9);
        for (int i = 0; i < qty; i++) {
            int x = ThreadLocalRandom.current().nextInt(2, room.sizeX - 2);
            int z = ThreadLocalRandom.current().nextInt(2, room.sizeZ - 2);
            world.setBlock(room.origin.x + x, room.origin.y, room.origin.z + z, pick(room.selectedRubbles));
        }
    }

    private void addPlants(World world, RoomNode room) {
        if (room.selectedPlants.isEmpty()) return;
        int qty = ThreadLocalRandom.current().nextInt(2, 7);
        for (int i = 0; i < qty; i++) {
            int x = ThreadLocalRandom.current().nextInt(2, room.sizeX - 2);
            int z = ThreadLocalRandom.current().nextInt(2, room.sizeZ - 2);
            world.setBlock(room.origin.x + x, room.origin.y, room.origin.z + z, pick(room.selectedPlants));
        }
    }

    private void addWallDecoratives(World world, RoomNode room) {
        if (room.selectedDecorations.isEmpty()) return;
        int qty = ThreadLocalRandom.current().nextInt(3, 8);
        for (int i = 0; i < qty; i++) {
            Direction wall = Direction.values()[ThreadLocalRandom.current().nextInt(Direction.values().length)];
            if (wall == Direction.SOUTH) continue;
            Vector3i pos = getWallDecoPosition(room, wall);
            world.setBlock(pos.x, pos.y, pos.z, pick(room.selectedDecorations));
        }
    }

    private Vector3i getWallDecoPosition(RoomNode room, Direction wall) {
        int y = room.origin.y + ThreadLocalRandom.current().nextInt(0, Math.min(VISIBLE_HEIGHT - 1, room.sizeY - 1));
        switch (wall) {
            case NORTH: return new Vector3i(room.origin.x + ThreadLocalRandom.current().nextInt(1, room.sizeX - 1), y, room.origin.z + 1);
            case WEST:  return new Vector3i(room.origin.x + 1, y, room.origin.z + ThreadLocalRandom.current().nextInt(1, room.sizeZ - 1));
            case EAST:  return new Vector3i(room.origin.x + room.sizeX - 2, y, room.origin.z + ThreadLocalRandom.current().nextInt(1, room.sizeZ - 1));
            default:    return room.getCenter();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String pickSolid(List<String> list) {
        if (list == null || list.isEmpty()) return "Rock_Stone";
        List<String> solids = new ArrayList<>();
        for (String s : list) {
            if (!s.toLowerCase().contains("half")) solids.add(s);
        }
        if (solids.isEmpty()) return "Rock_Stone";
        return solids.get(ThreadLocalRandom.current().nextInt(solids.size()));
    }

    private String pick(List<String> list) {
        if (list == null || list.isEmpty()) return "Rock_Stone";
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
