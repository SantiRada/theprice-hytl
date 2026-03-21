package Tenzinn.Systems.Dungeon;

import Tenzinn.ThePrice;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;

import java.util.UUID;
import java.util.logging.Level;
import it.unimi.dsi.fastutil.Pair;

public class DungeonGenerator {

    private ThePrice main;

    public DungeonGenerator (ThePrice main) { this.main = main; }

    public void createRoom(String key,int valueX, int valueY, int valueZ) {
        World world = Universe.get().getDefaultWorld();

        try {
            PrefabStore store = PrefabStore.get();
            BlockSelection prefab;

            prefab = store.getAssetPrefabFromAnyPack(key);

            if (prefab == null) { throw new RuntimeException(key + " - NO ENCONTRADO"); }

            BlockSelection cleanPrefab = new BlockSelection();
            cleanPrefab.setPosition(valueX, valueY, valueZ);

            prefab.forEachBlock((x, y, z, block) -> {
                cleanPrefab.addBlockAtLocalPos(x, y, z, block.blockId(), block.rotation(), block.filler(), block.supportValue());
            });

            prefab.forEachFluid(cleanPrefab::addFluidAtLocalPos);

            Vector3i pos = new Vector3i(valueX, valueY, valueZ);

            assert world != null;
            WorldConfig config = world.getWorldConfig();
            config.setBlockTicking(false);
            config.setTicking(false);

            cleanPrefab.placeNoReturn(world, pos, null);

            config.setBlockTicking(true);
            config.setTicking(true);

            main.setEnemies(8);
            spawnEnemies(8, 0, 0, 0);

        } catch (Exception e) {
            main.getLogger().at(Level.SEVERE).log("Error al colocar prefab: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void createRoom(TypeRoom typeRoom, boolean playerTP) {

        switch (typeRoom) {
            case TypeRoom.ROOM_STARTER:
                createRoom("Room/Terrenal/Starter.prefab.json", 0, 0, 0);

                if(playerTP) teleportPlayers();

                break;
        }
    }

    public void teleportPlayers() {
        World world = Universe.get().getDefaultWorld();
        if (world == null) return;

        Transform spawnPoint = new Transform(0, 20, 0);

        for (PlayerRef playerRef : Universe.get().getPlayers()) {
            UUID playerUUID = playerRef.getUuid();

            world.execute(() -> {
                try {
                    PlayerRef updatedRef = Universe.get().getPlayer(playerUUID);
                    if (updatedRef == null || updatedRef.getReference() == null) return;

                    Store<EntityStore> store = world.getEntityStore().getStore();
                    Teleport teleport = Teleport.createForPlayer(world, spawnPoint);
                    store.addComponent(updatedRef.getReference(), Teleport.getComponentType(), teleport);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void spawnEnemies(int amount, int roomX, int roomY, int roomZ) {
        World world = Universe.get().getDefaultWorld();
        if (world == null) return;

        Store<EntityStore> store = world.getEntityStore().getStore();

        world.execute(() -> {
            try {
                for (int i = 0; i < amount; i++) {
                    Vector3d position = new Vector3d(-2, 5, -18);
                    Vector3f rotation = new Vector3f(0, 0, 0);

                    Pair<Ref<EntityStore>, INonPlayerCharacter> result = NPCPlugin.get().spawnNPC(store, "Skeleton", null, position, rotation);
                }
            } catch (Exception e) {
                main.getLogger().at(Level.SEVERE).log("Error al spawnear NPCs: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}