package Tenzinn.Commands.Dungeons;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import Tenzinn.Systems.Dungeon.DungeonSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class CreateDungeonCommand extends AbstractPlayerCommand {

    private DungeonSystem dungeonSystem;

    public CreateDungeonCommand(String name, String description, DungeonSystem dungeonSystem) { super(name, description); this.dungeonSystem = dungeonSystem; }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        Player player = commandContext.senderAs(Player.class);
        dungeonSystem.onPlayerJoin(player);
    }
}