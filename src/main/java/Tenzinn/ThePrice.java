package Tenzinn;

import Tenzinn.Events.BlockPlace;
import Tenzinn.Events.DamageBlocks;
import Tenzinn.Events.PreventItemDrop;
import Tenzinn.Events.DetectPlayerReady;
import Tenzinn.Events.Trackers.KillTracker;
import Tenzinn.Systems.Camera.CameraThePrice;
import Tenzinn.Events.Trackers.HealthTracker;
import Tenzinn.Commands.TestUI.TestUICommands;
import Tenzinn.Commands.Camera.CameraCommands;
import Tenzinn.Systems.Dungeon.DungeonGenerator;
import Tenzinn.Commands.Dungeons.DungeonCommand;

import Tenzinn.Commands.DEBUG.ToggleModeCommand;
import Tenzinn.Commands.DEBUG.ActiveEventsCommand;
import Tenzinn.Commands.DEBUG.DeactivateUICommand;

import Tenzinn.UI.Data.FlairsData;
import Tenzinn.Systems.Dungeon.TypeRoom;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThePrice extends JavaPlugin {

    public AtomicBoolean activateEvents = new AtomicBoolean(true);
    private CameraThePrice cam;

    private DungeonGenerator dungeonGenerator;
    private KillTracker killTracker;

    public ThePrice(@Nonnull JavaPluginInit init) { super(init); }
    //////////////////////////////////////////////////
    @Override
    protected void start() { StaticSystems systems = new StaticSystems(); }
    @Override
    protected void setup() {
        FlairsData.loadContent();

        cam = new CameraThePrice();
        dungeonGenerator = new DungeonGenerator(this);

        getCommandRegistry().registerCommand(new TestUICommands("testui", "Test UI with this command"));
        getCommandRegistry().registerCommand(new CameraCommands("camera", "Test Camera with this command", cam));

        getCommandRegistry().registerCommand(new DungeonCommand(dungeonGenerator));

        getCommandRegistry().registerCommand(new ActiveEventsCommand("events", "Change state to event 'Block Place'", this));
        getCommandRegistry().registerCommand(new DeactivateUICommand("toggleui", "Toggle Custom UI to Debug"));
        getCommandRegistry().registerCommand(new ToggleModeCommand("togglemode", "Toggle Experience Mode The Price to Hytale Vanilla"));

        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> DetectPlayerReady.onPlayerReady(event, cam, dungeonGenerator));

        this.getEntityStoreRegistry().registerSystem(new PreventItemDrop());
        this.getEntityStoreRegistry().registerSystem(new HealthTracker());
        this.getEntityStoreRegistry().registerSystem(new DamageBlocks(this));
        this.getEntityStoreRegistry().registerSystem(new BlockPlace(this));

        killTracker = new KillTracker(this);
        this.getEntityStoreRegistry().registerSystem(killTracker);
    }
    //////////////////////////////////////////////////
    public void setEnemies(int amount) {
        killTracker.setKillsRequired(amount);
    }
    public void recreateRoom() { dungeonGenerator.createRoom(TypeRoom.ROOM_STARTER, false); }
    //////////////////////////////////////////////////
}