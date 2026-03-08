package Tenzinn.Commands.Camera;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import Tenzinn.Systems.Camera.CameraThePrice;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DesactivateCameraCommand extends AbstractTargetPlayerCommand {

    private CameraThePrice cam;

    @Nonnull
    public DesactivateCameraCommand(String name, String description, CameraThePrice cam) { super(name, description); this.cam = cam; }

    protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        cam.deactivate();
    }
}