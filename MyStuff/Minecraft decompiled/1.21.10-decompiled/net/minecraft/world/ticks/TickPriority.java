package net.minecraft.world.ticks;

import com.mojang.serialization.Codec;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   public static final Codec<TickPriority> CODEC = Codec.INT.xmap(TickPriority::byValue, TickPriority::getValue);
   private final int value;

   private TickPriority(final int param3) {
      this.value = $$0;
   }

   public static TickPriority byValue(int $$0) {
      for(TickPriority $$1 : values()) {
         if ($$1.value == $$0) {
            return $$1;
         }
      }

      return $$0 < EXTREMELY_HIGH.value ? EXTREMELY_HIGH : EXTREMELY_LOW;
   }

   public int getValue() {
      return this.value;
   }
}
