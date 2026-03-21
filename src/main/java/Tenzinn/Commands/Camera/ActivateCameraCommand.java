package Tenzinn.Commands.Camera;

import Tenzinn.Systems.Camera.CameraThePrice;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActivateCameraCommand extends AbstractTargetPlayerCommand {

    private CameraThePrice cam;

    private final OptionalArg<String> plane;

    @Nonnull
    public ActivateCameraCommand(String name, String description, CameraThePrice cam) {
        super(name, description);

        this.cam = cam;

        plane = withOptionalArg("world", "Type camera based in World System", ArgTypes.STRING);
    }

    protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        cam.activate(plane.get(context));
        cam.applyToPlayer(playerRef);
    }
}