package Tenzinn;

import Tenzinn.Commands.DEBUG.ActiveEventsCommand;
import Tenzinn.Commands.DEBUG.DeactivateUICommand;
import Tenzinn.Commands.DEBUG.ToggleModeCommand;
import Tenzinn.Events.BlockPlace;
import Tenzinn.Events.DamageBlocks;
import Tenzinn.Events.PreventItemDrop;
import Tenzinn.Events.DetectPlayerReady;
import Tenzinn.Events.Trackers.HealthTracker;
import Tenzinn.Systems.Camera.CameraThePrice;
import Tenzinn.Systems.Dungeon.DungeonSystem;
import Tenzinn.Commands.TestUI.TestUICommands;
import Tenzinn.Commands.Camera.CameraCommands;
import Tenzinn.Commands.Dungeons.DungeonCommands;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThePrice extends JavaPlugin {

    private final DungeonSystem dungeonSystem = new DungeonSystem();
    public AtomicBoolean activateEvents = new AtomicBoolean(true);
    private CameraThePrice cam;

    public ThePrice(@Nonnull JavaPluginInit init) { super(init); }

    @Override
    protected void setup() {
        cam = new CameraThePrice();

        getCommandRegistry().registerCommand(new TestUICommands("testui", "Test UI with this command"));
        getCommandRegistry().registerCommand(new CameraCommands("camera", "Test Camera with this command", cam));
        getCommandRegistry().registerCommand(new DungeonCommands("dungeon", "Dungeon Commands", dungeonSystem));

        getCommandRegistry().registerCommand(new ActiveEventsCommand("events", "Change state to event 'Block Place'", this));
        getCommandRegistry().registerCommand(new DeactivateUICommand("toggleui", "Toggle Custom UI to Debug"));
        getCommandRegistry().registerCommand(new ToggleModeCommand("togglemode", "Toggle Experience Mode The Price to Hytale Vanilla"));

        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> DetectPlayerReady.onPlayerReady(event, cam));

        this.getEntityStoreRegistry().registerSystem(new PreventItemDrop());
        this.getEntityStoreRegistry().registerSystem(new HealthTracker());
        this.getEntityStoreRegistry().registerSystem(new DamageBlocks(this));
        this.getEntityStoreRegistry().registerSystem(new BlockPlace(this));
    }
}