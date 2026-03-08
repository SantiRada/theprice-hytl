package Tenzinn.Commands.DEBUG;

import Tenzinn.UI.NewHUD;
import Tenzinn.Events.Trackers.HealthTracker;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DeactivateUICommand extends CommandBase {

    public DeactivateUICommand(@NonNullDecl String name, @NonNullDecl String description) { super(name, description); }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        Player player = commandContext.senderAs(Player.class);
        PlayerRef playerRef = Universe.get().getPlayerByUsername(player.getDisplayName(), NameMatching.EXACT);
        assert playerRef != null;

        CustomUIHud customHUD = player.getHudManager().getCustomHud();

        if (customHUD instanceof NewHUD) {
            player.getHudManager().resetHud(playerRef);

            player.getHudManager().showHudComponents(playerRef, HudComponent.Mana);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Sleep);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Oxygen);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Hotbar);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Health);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Compass);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Reticle);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Stamina);
            player.getHudManager().showHudComponents(playerRef, HudComponent.KillFeed);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Requests);
            player.getHudManager().showHudComponents(playerRef, HudComponent.PlayerList);
            player.getHudManager().showHudComponents(playerRef, HudComponent.EventTitle);
            player.getHudManager().showHudComponents(playerRef, HudComponent.PortalPanel);
            player.getHudManager().showHudComponents(playerRef, HudComponent.Speedometer);
            player.getHudManager().showHudComponents(playerRef, HudComponent.AmmoIndicator);
            player.getHudManager().showHudComponents(playerRef, HudComponent.InputBindings);
            player.getHudManager().showHudComponents(playerRef, HudComponent.BuilderToolsLegend);
            player.getHudManager().showHudComponents(playerRef, HudComponent.UtilitySlotSelector);
            player.getHudManager().showHudComponents(playerRef, HudComponent.BlockVariantSelector);
            player.getHudManager().showHudComponents(playerRef, HudComponent.BuilderToolsMaterialSlotSelector);

            player.sendMessage(Message.raw("UI desactivada"));
        }
        else {
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

            player.sendMessage(Message.raw("Se activó la UI nuevamente"));
        }
    }
}