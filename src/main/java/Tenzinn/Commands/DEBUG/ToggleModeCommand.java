package Tenzinn.Commands.DEBUG;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ToggleModeCommand extends AbstractPlayerCommand {

    public ToggleModeCommand(@NonNullDecl String name, @NonNullDecl String description) { super(name, description); }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        CommandManager.get().handleCommand(playerRef, "toggleui");
        CommandManager.get().handleCommand(playerRef, "camera toggle");
        CommandManager.get().handleCommand(playerRef, "events");
    }
}