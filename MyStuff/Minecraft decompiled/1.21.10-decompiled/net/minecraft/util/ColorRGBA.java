package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;

public record ColorRGBA(int rgba) {
   private static final String CUSTOM_COLOR_PREFIX = "#";
   public static final Codec<ColorRGBA> CODEC = Codec.STRING.comapFlatMap($$0 -> {
      if (!$$0.startsWith("#")) {
         return DataResult.error(() -> "Not a color code: " + $$0);
      } else {
         try {
            int $$1 = (int)Long.parseLong($$0.substring(1), 16);
            return DataResult.success(new ColorRGBA($$1));
         } catch (NumberFormatException var2) {
            return DataResult.error(() -> "Exception parsing color code: " + var2.getMessage());
         }
      }
   }, ColorRGBA::formatValue);

   private String formatValue() {
      return String.format(Locale.ROOT, "#%08X", this.rgba);
   }

   public String toString() {
      return this.formatValue();
   }
}
