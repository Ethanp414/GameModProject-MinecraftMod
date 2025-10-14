package net.minecraft.core;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.network.codec.StreamCodec;

public record Rotations(float x, float y, float z) {
   final float x;
   final float y;
   final float z;
   public static final Codec<Rotations> CODEC = Codec.FLOAT
      .listOf()
      .comapFlatMap($$0 -> Util.fixedSize($$0, 3).map($$0x -> new Rotations($$0x.get(0), $$0x.get(1), $$0x.get(2))), $$0 -> List.of($$0.x(), $$0.y(), $$0.z()));
   public static final StreamCodec<ByteBuf, Rotations> STREAM_CODEC = new StreamCodec<ByteBuf, Rotations>() {
      public Rotations decode(ByteBuf $$0) {
         return new Rotations($$0.readFloat(), $$0.readFloat(), $$0.readFloat());
      }

      public void encode(ByteBuf $$0, Rotations $$1) {
         $$0.writeFloat($$1.x);
         $$0.writeFloat($$1.y);
         $$0.writeFloat($$1.z);
      }
   };

   public Rotations(float param1, float param2, float param3) {
      $$0 = !Float.isInfinite($$0) && !Float.isNaN($$0) ? $$0 % 360.0F : 0.0F;
      $$1 = !Float.isInfinite($$1) && !Float.isNaN($$1) ? $$1 % 360.0F : 0.0F;
      $$2 = !Float.isInfinite($$2) && !Float.isNaN($$2) ? $$2 % 360.0F : 0.0F;
      this.x = $$0;
      this.y = $$1;
      this.z = $$2;
   }
}
