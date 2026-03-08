package Tenzinn.Events;

import Tenzinn.Systems.Camera.CameraThePrice;
import Tenzinn.UI.NewHUD;
import Tenzinn.Events.Trackers.HealthTracker;
import Tenzinn.Systems.Dungeon.DungeonSystem;

import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

public class DetectPlayerReady {

    private static CameraThePrice cam;

    public static void onPlayerReady(PlayerReadyEvent event, CameraThePrice camera) {
        cam = camera;
        Player player = event.getPlayer();

        PlayerRef playerRef = Universe.get().getPlayerByUsername(player.getDisplayName(), NameMatching.EXACT);
        assert playerRef != null;

        openHUD(playerRef, player);

        cam.activate(5.4f);
        cam.applyToPlayer(playerRef);
    }
    private static void openHUD(PlayerRef playerRef, Player player) {

        // player.getHudManager().hideHudComponents(playerRef, HudComponent.Chat);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Mana);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Sleep);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Oxygen);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Hotbar);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Health);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Compass);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Reticle);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Stamina);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.KillFeed);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Requests);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.PlayerList);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.EventTitle);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.PortalPanel);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.Speedometer);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.AmmoIndicator);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.InputBindings);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.BuilderToolsLegend);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.UtilitySlotSelector);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.BlockVariantSelector);
        player.getHudManager().hideHudComponents(playerRef, HudComponent.BuilderToolsMaterialSlotSelector);

        NewHUD newHUD = new NewHUD(playerRef);
        player.getHudManager().setCustomHud(playerRef, newHUD);

        float current = HealthTracker.getCurrentHealth(playerRef.getUuid());
        float max = HealthTracker.getMaxHealth(playerRef.getUuid());

        newHUD.setHealth((int)current, (int)max);
    }
}