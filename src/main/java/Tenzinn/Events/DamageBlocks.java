package Tenzinn.Events;

import Tenzinn.ThePrice;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;

import javax.annotation.Nullable;

public class DamageBlocks extends EntityEventSystem<EntityStore, DamageBlockEvent> {

    private ThePrice main;

    public DamageBlocks(ThePrice main) { super(DamageBlockEvent.class); this.main = main; }

    @Override
    public void handle(int i, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store, CommandBuffer<EntityStore> buffer, DamageBlockEvent event) {
        if (event.getBlockType() == BlockType.EMPTY) return;

        if (main.activateEvents.get()) event.setCancelled(true);
    }

    @Nullable @Override
    public Query<EntityStore> getQuery() { return PlayerRef.getComponentType(); }
}