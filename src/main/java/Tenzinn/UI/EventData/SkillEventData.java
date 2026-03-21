package Tenzinn.UI.EventData;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class SkillEventData {

    private String action;

    public SkillEventData() { }

    public String getAction() { return action; }

    public static final BuilderCodec<SkillEventData> CODEC = BuilderCodec
            .builder(SkillEventData.class, SkillEventData::new).append(new KeyedCodec<>("Action", Codec.STRING), (data, value) -> data.action = value,
                    (data) -> data.action).add().build();
}