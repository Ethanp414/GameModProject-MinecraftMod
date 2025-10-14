package net.minecraft.world.ticks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;

public record SavedTick<T>(T type, BlockPos pos, int delay, TickPriority priority) {
   public static final Strategy<SavedTick<?>> UNIQUE_TICK_HASH = new Strategy<SavedTick<?>>() {
      public int hashCode(SavedTick<?> $$0) {
         return 31 * $$0.pos().hashCode() + $$0.type().hashCode();
      }

      public boolean equals(@Nullable SavedTick<?> $$0, @Nullable SavedTick<?> $$1) {
         if ($$0 == $$1) {
            return true;
         } else if ($$0 != null && $$1 != null) {
            return $$0.type() == $$1.type() && $$0.pos().equals($$1.pos());
         } else {
            return false;
         }
      }
   };

   public static <T> Codec<SavedTick<T>> codec(Codec<T> $$0) {
      MapCodec<BlockPos> $$1 = RecordCodecBuilder.mapCodec(
         $$0x -> $$0x.group(
                  Codec.INT.fieldOf("x").forGetter(Vec3i::getX), Codec.INT.fieldOf("y").forGetter(Vec3i::getY), Codec.INT.fieldOf("z").forGetter(Vec3i::getZ)
               )
               .apply($$0x, BlockPos::new)
      );
      return RecordCodecBuilder.create(
         $$2 -> $$2.group(
                  $$0.fieldOf("i").forGetter(SavedTick::type),
                  $$1.forGetter(SavedTick::pos),
                  Codec.INT.fieldOf("t").forGetter(SavedTick::delay),
                  TickPriority.CODEC.fieldOf("p").forGetter(SavedTick::priority)
               )
               .apply($$2, SavedTick::new)
      );
   }

   public static <T> List<SavedTick<T>> filterTickListForChunk(List<SavedTick<T>> $$0, ChunkPos $$1) {
      long $$2 = $$1.toLong();
      return $$0.stream().filter($$1x -> ChunkPos.asLong($$1x.pos()) == $$2).toList();
   }

   public ScheduledTick<T> unpack(long $$0, long $$1) {
      return new ScheduledTick<>(this.type, this.pos, $$0 + (long)this.delay, this.priority, $$1);
   }

   public static <T> SavedTick<T> probe(T $$0, BlockPos $$1) {
      return new SavedTick<>($$0, $$1, 0, TickPriority.NORMAL);
   }
}
