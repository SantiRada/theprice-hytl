package Tenzinn.Systems.Dungeon;

import com.google.gson.Gson;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.math.vector.Transform;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jline.utils.InputStreamReader;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DungeonSystem {

    private static final String CONFIG_PATH = "Server/Worlds/terrenal.json";

    private WorldConfig worldConfig;

    private final Set<String> generatedForPlayers = new HashSet<>();
    private Vector3i          dungeonSpawnPoint   = null;
    private UUID              dungeonWorldUuid    = null;

    public DungeonSystem() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH);
            if (stream == null) return;
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(stream);
            worldConfig = gson.fromJson(reader, WorldConfig.class);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPlayerJoin(Player player) {
        if (worldConfig == null) {
            player.sendMessage(Message.raw("No carga la configuración del mundo"));
            return;
        }

        String username = player.getDisplayName();
        PlayerRef playerRef = Universe.get().getPlayerByUsername(username, NameMatching.EXACT);

        if (dungeonSpawnPoint != null) {
            if (!generatedForPlayers.contains(username)) {
                generatedForPlayers.add(username);
                teleportToSpawn(playerRef);
                player.sendMessage(Message.raw("Teletransportado al dungeon existente"));
            }
            return;
        }

        generatedForPlayers.add(username);

        World world = Universe.get().getWorld(playerRef.getWorldUuid());
        Transform transform = playerRef.getTransform();

        dungeonWorldUuid = playerRef.getWorldUuid();

        int avgRoomHalfX = (worldConfig.size.min.x + worldConfig.size.max.x) / 4;
        int avgRoomHalfZ = (worldConfig.size.min.z + worldConfig.size.max.z) / 4;

        Vector3i startOrigin = new Vector3i(
                (int) transform.getPosition().x - avgRoomHalfX,
                (int) transform.getPosition().y,
                (int) transform.getPosition().z - avgRoomHalfZ
        );

        dungeonSpawnPoint = new Vector3i(
                (int) transform.getPosition().x,
                (int) transform.getPosition().y + 1,
                (int) transform.getPosition().z
        );

        world.execute(() -> generateDungeon(world, startOrigin));
    }

    private void teleportToSpawn(PlayerRef playerRef) {
        if (dungeonSpawnPoint == null || dungeonWorldUuid == null) return;
        World world = Universe.get().getWorld(dungeonWorldUuid);
        world.execute(() -> {
            try {
                Ref<EntityStore> ref = playerRef.getReference();
                Store<EntityStore> store = ref.getStore();
                Vector3f rotation = new Vector3f(0f, 0f, 0f);
                Vector3f pos = new Vector3f(dungeonSpawnPoint.x, dungeonSpawnPoint.y, dungeonSpawnPoint.z);
                Teleport teleport = new Teleport(pos.toVector3d(), rotation);
                store.putComponent(ref, Teleport.getComponentType(), teleport);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void generateDungeon(World world, Vector3i startOrigin) {
        DungeonLayout layout  = new DungeonLayout(worldConfig);
        RoomBuilder   builder = new RoomBuilder();
        TunnelBuilder tunnels = new TunnelBuilder();

        List<RoomNode> rooms = layout.generate(startOrigin);

        world.sendMessage(Message.raw("Generando Dungeon... (" + rooms.size() + " salas)"));

        for (RoomNode room : rooms) {
            builder.build(world, room);
        }

        world.sendMessage(Message.raw("Salas construidas, conectando..."));

        Set<String> builtTunnels = new HashSet<>();

        for (RoomNode room : rooms) {
            for (var entry : room.connections.entrySet()) {
                Direction dir   = entry.getKey();
                RoomNode  other = entry.getValue();
                String    key   = Math.min(room.id, other.id) + "_" + Math.max(room.id, other.id);

                if (!builtTunnels.contains(key)) {
                    builtTunnels.add(key);
                    tunnels.buildTunnel(world, room, other, dir, room.selectedWalls, room.selectedGrounds);
                }
            }
        }

        world.sendMessage(Message.raw("Dungeon generado completamente."));
    }
}