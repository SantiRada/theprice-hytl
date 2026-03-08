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

    private final OptionalArg<Float> rotationCamera;
    private final OptionalArg<Float> distance;

    @Nonnull
    public ActivateCameraCommand(String name, String description, CameraThePrice cam) {
        super(name, description);

        this.cam = cam;

        rotationCamera = withOptionalArg("rotate", "Rotation camera in Pitch", ArgTypes.FLOAT);
        distance = withOptionalArg("distance", "Distance camera in DEBUG", ArgTypes.FLOAT);
    }

    protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        if(distance.get(context) != null && distance.get(context) > 0.0f) cam.setDistance(distance.get(context));
        if(rotationCamera.get(context) != null && rotationCamera.get(context) != 0.0f) cam.activate(rotationCamera.get(context));

        cam.applyToPlayer(playerRef);
    }
}