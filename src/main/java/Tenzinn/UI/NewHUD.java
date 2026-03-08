package Tenzinn.UI;

import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;

public class NewHUD extends CustomUIHud {

    private UICommandBuilder uiBuilder;

    public NewHUD(@NonNullDecl PlayerRef playerRef) { super(playerRef); }

    @Override
    protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("HUD.ui");
        uiBuilder = uiCommandBuilder;
    }
    public void setHealth(int current, int max) {
        if (uiBuilder == null) return;

        int newHealth = current * 262 / max;

        Anchor anchor = new Anchor();
        anchor.setWidth(Value.of(newHealth));
        anchor.setHeight(Value.of(26));

        uiBuilder.setObject("#HealthbarValue.Anchor", anchor);

        String color = "#AD5A36";

        if (newHealth < 200 && newHealth > 100) { color = "#A6351C"; }
        else if (newHealth <= 100) { color = "#AB0C0C"; }

        uiBuilder.set("#HealthbarValue.Background", color);

        update(true, uiBuilder);
    }
}