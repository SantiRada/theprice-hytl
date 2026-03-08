package Tenzinn.Commands.TestUI;

import Tenzinn.Commands.Camera.ActivateCameraCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class TestUICommands extends AbstractCommandCollection {

    public TestUICommands(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, description);

        addSubCommand(new MenuCommand("menu", "Test UI: Menu"));
        addSubCommand(new DialogCommand("dialog", "Test UI: Dialog"));
        addSubCommand(new SkillCommand("skill", "Test UI: Skill"));
        addSubCommand(new FlairCommand("flair", "Test UI: Flair"));
    }
}