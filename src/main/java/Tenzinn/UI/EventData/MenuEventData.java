package Tenzinn.UI.EventData;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class MenuEventData {

    private String action;

    public MenuEventData() { }

    public String getAction() { return action; }

    public static final BuilderCodec<MenuEventData> CODEC = BuilderCodec
            .builder(MenuEventData.class, MenuEventData::new).append(new KeyedCodec<>("Action", Codec.STRING), (data, value) -> data.action = value,
                    (data) -> data.action).add().build();
}