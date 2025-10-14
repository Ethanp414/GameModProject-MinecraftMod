package net.minecraft.world.scores;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;

public interface ReadOnlyScoreInfo {
   int value();

   boolean isLocked();

   @Nullable
   NumberFormat numberFormat();

   default MutableComponent formatValue(NumberFormat $$0) {
      return ((NumberFormat)Objects.requireNonNullElse(this.numberFormat(), $$0)).format(this.value());
   }

   static MutableComponent safeFormatValue(@Nullable ReadOnlyScoreInfo $$0, NumberFormat $$1) {
      return $$0 != null ? $$0.formatValue($$1) : $$1.format(0);
   }
}
