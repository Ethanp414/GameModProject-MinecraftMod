package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record MethodInfo(String description, Optional<ParamInfo> params, Optional<ResultInfo> result) {
   public static final Codec<Optional<ParamInfo>> PARAMS_CODEC = ParamInfo.CODEC
      .codec()
      .listOf()
      .xmap($$0 -> $$0.stream().findAny(), $$0 -> (List)$$0.map(List::of).orElse(List.of()));
   public static final MapCodec<MethodInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               Codec.STRING.fieldOf("description").forGetter(MethodInfo::description),
               PARAMS_CODEC.fieldOf("params").forGetter(MethodInfo::params),
               ResultInfo.CODEC.codec().optionalFieldOf("result").forGetter(MethodInfo::result)
            )
            .apply($$0, MethodInfo::new)
   );

   public MethodInfo(String $$0, @Nullable ParamInfo $$1, @Nullable ResultInfo $$2) {
      this($$0, Optional.ofNullable($$1), Optional.ofNullable($$2));
   }

   public MethodInfo.Named named(ResourceLocation $$0) {
      return new MethodInfo.Named($$0, this);
   }

   public static record Named(ResourceLocation name, MethodInfo contents) {
      public static final Codec<MethodInfo.Named> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(ResourceLocation.CODEC.fieldOf("name").forGetter(MethodInfo.Named::name), MethodInfo.MAP_CODEC.forGetter(MethodInfo.Named::contents))
               .apply($$0, MethodInfo.Named::new)
      );
   }
}
