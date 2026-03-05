package Tenzinn.UI.Pages;

import Tenzinn.UI.Data.DialogEventData;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;

public class DialogUI extends InteractiveCustomUIPage<DialogEventData> {

    private UICommandBuilder uiBuilder;

    public DialogUI(PlayerRef playerRef) { super(playerRef, CustomPageLifetime.CanDismiss, Tenzinn.UI.Data.DialogEventData.CODEC); }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref,@NonNullDecl UICommandBuilder uiCommandBuilder,@NonNullDecl UIEventBuilder uiEventBuilder,@NonNullDecl Store<EntityStore> store) {
        playerRef.sendMessage(Message.raw("TestUI: Dialog").color(Color.yellow));

        uiCommandBuilder.append("Dialog.ui");
        uiBuilder = uiCommandBuilder;

        sendUpdate();
    }
}