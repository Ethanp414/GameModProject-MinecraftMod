package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;

public record ResultInfo(String name, Schema schema) {
   public static final MapCodec<ResultInfo> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(Codec.STRING.fieldOf("name").forGetter(ResultInfo::name), Schema.CODEC.fieldOf("schema").forGetter(ResultInfo::schema))
            .apply($$0, ResultInfo::new)
   );
}
