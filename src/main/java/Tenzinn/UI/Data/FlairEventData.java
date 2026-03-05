package Tenzinn.UI.Data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class FlairEventData {

    private String action;

    public FlairEventData() { }

    public String getAction() { return action; }

    public static final BuilderCodec<FlairEventData> CODEC = BuilderCodec
            .builder(FlairEventData.class, FlairEventData::new).append(new KeyedCodec<>("Action", Codec.STRING), (data, value) -> data.action = value,
                    (data) -> data.action).add().build();
}