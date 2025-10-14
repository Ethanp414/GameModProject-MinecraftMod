package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;

public record ParamInfo(String name, Schema schema, boolean required) {
   public static final MapCodec<ParamInfo> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               Codec.STRING.fieldOf("name").forGetter(ParamInfo::name),
               Schema.CODEC.fieldOf("schema").forGetter(ParamInfo::schema),
               Codec.BOOL.fieldOf("required").forGetter(ParamInfo::required)
            )
            .apply($$0, ParamInfo::new)
   );

   public ParamInfo(String $$0, Schema $$1) {
      this($$0, $$1, true);
   }
}
