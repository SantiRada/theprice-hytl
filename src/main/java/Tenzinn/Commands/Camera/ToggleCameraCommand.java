package Tenzinn.Commands.Camera;

import Tenzinn.Systems.Camera.CameraThePrice;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ToggleCameraCommand extends AbstractTargetPlayerCommand {

    private CameraThePrice cam;

    @Nonnull
    public ToggleCameraCommand(String name, String description, CameraThePrice cam) { super(name, description); this.cam = cam; }
    protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {

        if (cam.getState()) { cam.deactivate(); }
        else {
            cam.activate(5.4f);
            cam.applyToPlayer(playerRef);
        }
    }
}