package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record GlobalPos(ResourceKey<Level> dimension, BlockPos pos) {
   public static final MapCodec<GlobalPos> MAP_CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos))
            .apply($$0, GlobalPos::of)
   );
   public static final Codec<GlobalPos> CODEC = MAP_CODEC.codec();
   public static final StreamCodec<ByteBuf, GlobalPos> STREAM_CODEC = StreamCodec.composite(
      ResourceKey.streamCodec(Registries.DIMENSION), GlobalPos::dimension, BlockPos.STREAM_CODEC, GlobalPos::pos, GlobalPos::of
   );

   public static GlobalPos of(ResourceKey<Level> $$0, BlockPos $$1) {
      return new GlobalPos($$0, $$1);
   }

   public String toString() {
      return this.dimension + " " + this.pos;
   }

   public boolean isCloseEnough(ResourceKey<Level> $$0, BlockPos $$1, int $$2) {
      return this.dimension.equals($$0) && this.pos.distChessboard($$1) <= $$2;
   }
}
