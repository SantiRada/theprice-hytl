package Tenzinn.Commands.Camera;

import Tenzinn.Systems.Camera.CameraThePrice;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class CameraCommands extends AbstractCommandCollection {

    public CameraCommands(@NonNullDecl String name, @NonNullDecl String description, CameraThePrice cam) {
        super(name, description);

        addSubCommand(new ActivateCameraCommand("activate", "activate camera for The Price System", cam));
        addSubCommand(new DesactivateCameraCommand("desactivate", "desactivate camera for The Price System", cam));
        addSubCommand(new ToggleCameraCommand("toggle", "Toggle camera state between activate or desactivate", cam));
    }
}