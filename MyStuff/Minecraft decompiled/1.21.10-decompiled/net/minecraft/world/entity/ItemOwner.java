package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface ItemOwner {
   Level level();

   Vec3 position();

   float getVisualRotationYInDegrees();

   @Nullable
   default LivingEntity asLivingEntity() {
      return null;
   }

   static ItemOwner offsetFromOwner(ItemOwner $$0, Vec3 $$1) {
      return new ItemOwner.OffsetFromOwner($$0, $$1);
   }

   public static record OffsetFromOwner(ItemOwner owner, Vec3 offset) implements ItemOwner {
      @Override
      public Level level() {
         return this.owner.level();
      }

      @Override
      public Vec3 position() {
         return this.owner.position().add(this.offset);
      }

      @Override
      public float getVisualRotationYInDegrees() {
         return this.owner.getVisualRotationYInDegrees();
      }

      @Nullable
      @Override
      public LivingEntity asLivingEntity() {
         return this.owner.asLivingEntity();
      }
   }
}
