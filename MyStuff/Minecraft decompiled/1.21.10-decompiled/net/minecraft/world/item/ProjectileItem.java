package net.minecraft.world.item;

import java.util.OptionalInt;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public interface ProjectileItem {
   Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4);

   default ProjectileItem.DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.DEFAULT;
   }

   default void shoot(Projectile $$0, double $$1, double $$2, double $$3, float $$4, float $$5) {
      $$0.shoot($$1, $$2, $$3, $$4, $$5);
   }

   public static record DispenseConfig(ProjectileItem.PositionFunction positionFunction, float uncertainty, float power, OptionalInt overrideDispenseEvent) {
      public static final ProjectileItem.DispenseConfig DEFAULT = builder().build();

      public static ProjectileItem.DispenseConfig.Builder builder() {
         return new ProjectileItem.DispenseConfig.Builder();
      }

      public static class Builder {
         private ProjectileItem.PositionFunction positionFunction = ($$0, $$1) -> DispenserBlock.getDispensePosition($$0, 0.7, new Vec3(0.0, 0.1, 0.0));
         private float uncertainty = 6.0F;
         private float power = 1.1F;
         private OptionalInt overrideDispenseEvent = OptionalInt.empty();

         public ProjectileItem.DispenseConfig.Builder positionFunction(ProjectileItem.PositionFunction $$0) {
            this.positionFunction = $$0;
            return this;
         }

         public ProjectileItem.DispenseConfig.Builder uncertainty(float $$0) {
            this.uncertainty = $$0;
            return this;
         }

         public ProjectileItem.DispenseConfig.Builder power(float $$0) {
            this.power = $$0;
            return this;
         }

         public ProjectileItem.DispenseConfig.Builder overrideDispenseEvent(int $$0) {
            this.overrideDispenseEvent = OptionalInt.of($$0);
            return this;
         }

         public ProjectileItem.DispenseConfig build() {
            return new ProjectileItem.DispenseConfig(this.positionFunction, this.uncertainty, this.power, this.overrideDispenseEvent);
         }
      }
   }

   @FunctionalInterface
   public interface PositionFunction {
      Position getDispensePosition(BlockSource var1, Direction var2);
   }
}
