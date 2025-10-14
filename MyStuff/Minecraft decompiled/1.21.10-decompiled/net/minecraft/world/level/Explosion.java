package net.minecraft.world.level;

import java.lang.runtime.SwitchBootstraps;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public interface Explosion {
   static DamageSource getDefaultDamageSource(Level $$0, @Nullable Entity $$1) {
      return $$0.damageSources().explosion($$1, getIndirectSourceEntity($$1));
   }

   @Nullable
   static LivingEntity getIndirectSourceEntity(@Nullable Entity $$0) {
      Entity var1 = $$0;
      byte var2 = 0;

      while(true) {
         switch(SwitchBootstraps.typeSwitch<"typeSwitch",PrimedTnt,LivingEntity,Projectile>(var1, var2)) {
            case -1:
            default:
               return null;
            case 0:
               PrimedTnt $$1 = (PrimedTnt)var1;
               return $$1.getOwner();
            case 1:
               return (LivingEntity)var1;
            case 2:
               Projectile $$3 = (Projectile)var1;
               Entity var7 = $$3.getOwner();
               if (var7 instanceof LivingEntity $$4) {
                  return $$4;
               }

               var2 = 3;
         }
      }
   }

   ServerLevel level();

   Explosion.BlockInteraction getBlockInteraction();

   @Nullable
   LivingEntity getIndirectSourceEntity();

   @Nullable
   Entity getDirectSourceEntity();

   float radius();

   Vec3 center();

   boolean canTriggerBlocks();

   boolean shouldAffectBlocklikeEntities();

   public static enum BlockInteraction {
      KEEP(false),
      DESTROY(true),
      DESTROY_WITH_DECAY(true),
      TRIGGER_BLOCK(false);

      private final boolean shouldAffectBlocklikeEntities;

      private BlockInteraction(final boolean param3) {
         this.shouldAffectBlocklikeEntities = $$0;
      }

      public boolean shouldAffectBlocklikeEntities() {
         return this.shouldAffectBlocklikeEntities;
      }
   }
}
