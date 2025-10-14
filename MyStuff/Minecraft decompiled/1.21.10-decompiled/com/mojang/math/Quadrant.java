package com.mojang.math;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Mth;

public enum Quadrant {
   R0(0),
   R90(1),
   R180(2),
   R270(3);

   public static final Codec<Quadrant> CODEC = Codec.INT.comapFlatMap($$0 -> {
      return switch(Mth.positiveModulo($$0, 360)) {
         case 0 -> DataResult.success(R0);
         case 90 -> DataResult.success(R90);
         case 180 -> DataResult.success(R180);
         case 270 -> DataResult.success(R270);
         default -> DataResult.error(() -> "Invalid rotation " + $$0 + " found, only 0/90/180/270 allowed");
      };
   }, $$0 -> {
      return switch($$0.ordinal()) {
         case 0 -> 0;
         case 1 -> 90;
         case 2 -> 180;
         case 3 -> 270;
         default -> throw new MatchException(null, null);
      };
   });
   public final int shift;

   private Quadrant(final int param3) {
      this.shift = $$0;
   }

   @Deprecated
   public static Quadrant parseJson(int $$0) {
      return switch(Mth.positiveModulo($$0, 360)) {
         case 0 -> R0;
         case 90 -> R90;
         case 180 -> R180;
         case 270 -> R270;
         default -> throw new JsonParseException("Invalid rotation " + $$0 + " found, only 0/90/180/270 allowed");
      };
   }

   public int rotateVertexIndex(int $$0) {
      return ($$0 + this.shift) % 4;
   }
}
