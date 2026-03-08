package Tenzinn.Commands.Dungeons;

import Tenzinn.Systems.Dungeon.DungeonSystem;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DungeonCommands extends AbstractCommandCollection {

    public DungeonCommands(@NonNullDecl String name, @NonNullDecl String description, DungeonSystem dungeonSystem) {
        super(name, description);

        addSubCommand(new CreateDungeonCommand("create", "Create Dungeon in position of this player.", dungeonSystem));
    }
}