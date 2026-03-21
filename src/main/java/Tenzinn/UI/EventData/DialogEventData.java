package Tenzinn.UI.EventData;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class DialogEventData {

    private String action;

    public DialogEventData() { }

    public String getAction() { return action; }

    public static final BuilderCodec<DialogEventData> CODEC = BuilderCodec
            .builder(DialogEventData.class, DialogEventData::new).append(new KeyedCodec<>("Action", Codec.STRING), (data, value) -> data.action = value,
                    (data) -> data.action).add().build();
}