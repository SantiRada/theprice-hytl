package Tenzinn.Events;

import Tenzinn.ThePrice;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockPlace extends EntityEventSystem<EntityStore, PlaceBlockEvent> {

    private ThePrice main;

    public BlockPlace(ThePrice main) { super(PlaceBlockEvent.class); this.main = main; }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull PlaceBlockEvent event) {
        if(main.activateEvents.get()) event.setCancelled(true);
    }

    @Nullable @Override
    public Query<EntityStore> getQuery() { return PlayerRef.getComponentType(); }
}