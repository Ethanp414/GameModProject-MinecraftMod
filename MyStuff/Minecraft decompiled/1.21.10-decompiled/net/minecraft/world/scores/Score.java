package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;

public class Score implements ReadOnlyScoreInfo {
   public static final MapCodec<Score> MAP_CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               Codec.INT.optionalFieldOf("Score", Integer.valueOf(0)).forGetter(Score::value),
               Codec.BOOL.optionalFieldOf("Locked", Boolean.valueOf(false)).forGetter(Score::isLocked),
               ComponentSerialization.CODEC.optionalFieldOf("display").forGetter($$0x -> Optional.ofNullable($$0x.display)),
               NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter($$0x -> Optional.ofNullable($$0x.numberFormat))
            )
            .apply($$0, Score::new)
   );
   private int value;
   private boolean locked = true;
   @Nullable
   private Component display;
   @Nullable
   private NumberFormat numberFormat;

   public Score() {
   }

   private Score(int $$0, boolean $$1, Optional<Component> $$2, Optional<NumberFormat> $$3) {
      this.value = $$0;
      this.locked = $$1;
      this.display = (Component)$$2.orElse(null);
      this.numberFormat = (NumberFormat)$$3.orElse(null);
   }

   @Override
   public int value() {
      return this.value;
   }

   public void value(int $$0) {
      this.value = $$0;
   }

   @Override
   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean $$0) {
      this.locked = $$0;
   }

   @Nullable
   public Component display() {
      return this.display;
   }

   public void display(@Nullable Component $$0) {
      this.display = $$0;
   }

   @Nullable
   @Override
   public NumberFormat numberFormat() {
      return this.numberFormat;
   }

   public void numberFormat(@Nullable NumberFormat $$0) {
      this.numberFormat = $$0;
   }
}
