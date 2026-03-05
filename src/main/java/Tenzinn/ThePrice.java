package Tenzinn;

import Tenzinn.Events.BlockPlace;
import Tenzinn.Events.DamageBlocks;
import Tenzinn.Events.PreventItemDrop;
import Tenzinn.Events.DetectPlayerReady;
import Tenzinn.Events.Trackers.HealthTracker;
import Tenzinn.Commands.TestUI.TestUICommands;

import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseMotionEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;

import javax.annotation.Nonnull;

public class ThePrice extends JavaPlugin {

    public ThePrice(@Nonnull JavaPluginInit init) { super(init); }

    @Override
    protected void setup() {
        // Commands
        getCommandRegistry().registerCommand(new TestUICommands("testui", "Test UI with this command"));

        // Events
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, DetectPlayerReady::onPlayerReady);
        this.getEventRegistry().registerGlobal(PlayerMouseMotionEvent.class, event -> {
            Ref<EntityStore> ref = event.getPlayerRef();
            Store<EntityStore> store = ref.getStore();

            Vector3i targetBlock = event.getTargetBlock();
            if (targetBlock == null) return;

            TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (transform == null) return;

            Vector3d playerPos = transform.getPosition();

            float dx = targetBlock.x - (float) playerPos.x;
            float dz = targetBlock.z - (float) playerPos.z;

            if (dx == 0 && dz == 0) return;

            float yaw = (float) Math.atan2(dx, dz);

            transform.getRotation().assign(yaw, 0.0f, 0.0f);
        });

        // ------
        this.getEntityStoreRegistry().registerSystem(new HealthTracker());
        this.getEntityStoreRegistry().registerSystem(new PreventItemDrop());
        this.getEntityStoreRegistry().registerSystem(new DamageBlocks());
        this.getEntityStoreRegistry().registerSystem(new BlockPlace());
    }
}