package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.world.scores.PlayerTeam;

public record ConversionParams(ConversionType type, boolean keepEquipment, boolean preserveCanPickUpLoot, @Nullable PlayerTeam team) {
   public static ConversionParams single(Mob $$0, boolean $$1, boolean $$2) {
      return new ConversionParams(ConversionType.SINGLE, $$1, $$2, $$0.getTeam());
   }

   @FunctionalInterface
   public interface AfterConversion<T extends Mob> {
      void finalizeConversion(T var1);
   }
}
