package Tenzinn.Commands.Dungeons;

import Tenzinn.Systems.Dungeon.TypeRoom;
import Tenzinn.Systems.Dungeon.DungeonGenerator;

import Tenzinn.ThePrice;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DungeonCommand extends AbstractPlayerCommand {

    private DungeonGenerator main;

    public DungeonCommand(DungeonGenerator main) {
        super("dungeon", "Genera un dungeon procedural en tu posición");
        this.main = main;
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        main.createRoom(TypeRoom.ROOM_STARTER, true);
    }
}