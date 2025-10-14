package net.minecraft.world.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class ProjectileUtil {
   public static final float DEFAULT_ENTITY_HIT_RESULT_MARGIN = 0.3F;

   public static HitResult getHitResultOnMoveVector(Entity $$0, Predicate<Entity> $$1) {
      Vec3 $$2 = $$0.getDeltaMovement();
      Level $$3 = $$0.level();
      Vec3 $$4 = $$0.position();
      return getHitResult($$4, $$0, $$1, $$2, $$3, computeMargin($$0), ClipContext.Block.COLLIDER);
   }

   public static HitResult getHitResultOnMoveVector(Entity $$0, Predicate<Entity> $$1, ClipContext.Block $$2) {
      Vec3 $$3 = $$0.getDeltaMovement();
      Level $$4 = $$0.level();
      Vec3 $$5 = $$0.position();
      return getHitResult($$5, $$0, $$1, $$3, $$4, computeMargin($$0), $$2);
   }

   public static HitResult getHitResultOnViewVector(Entity $$0, Predicate<Entity> $$1, double $$2) {
      Vec3 $$3 = $$0.getViewVector(0.0F).scale($$2);
      Level $$4 = $$0.level();
      Vec3 $$5 = $$0.getEyePosition();
      return getHitResult($$5, $$0, $$1, $$3, $$4, 0.0F, ClipContext.Block.COLLIDER);
   }

   private static HitResult getHitResult(Vec3 $$0, Entity $$1, Predicate<Entity> $$2, Vec3 $$3, Level $$4, float $$5, ClipContext.Block $$6) {
      Vec3 $$7 = $$0.add($$3);
      HitResult $$8 = $$4.clipIncludingBorder(new ClipContext($$0, $$7, $$6, ClipContext.Fluid.NONE, $$1));
      if ($$8.getType() != HitResult.Type.MISS) {
         $$7 = $$8.getLocation();
      }

      HitResult $$9 = getEntityHitResult($$4, $$1, $$0, $$7, $$1.getBoundingBox().expandTowards($$3).inflate(1.0), $$2, $$5);
      if ($$9 != null) {
         $$8 = $$9;
      }

      return $$8;
   }

   @Nullable
   public static EntityHitResult getEntityHitResult(Entity $$0, Vec3 $$1, Vec3 $$2, AABB $$3, Predicate<Entity> $$4, double $$5) {
      Level $$6 = $$0.level();
      double $$7 = $$5;
      Entity $$8 = null;
      Vec3 $$9 = null;

      for(Entity $$10 : $$6.getEntities($$0, $$3, $$4)) {
         AABB $$11 = $$10.getBoundingBox().inflate((double)$$10.getPickRadius());
         Optional<Vec3> $$12 = $$11.clip($$1, $$2);
         if ($$11.contains($$1)) {
            if ($$7 >= 0.0) {
               $$8 = $$10;
               $$9 = (Vec3)$$12.orElse($$1);
               $$7 = 0.0;
            }
         } else if ($$12.isPresent()) {
            Vec3 $$13 = (Vec3)$$12.get();
            double $$14 = $$1.distanceToSqr($$13);
            if ($$14 < $$7 || $$7 == 0.0) {
               if ($$10.getRootVehicle() == $$0.getRootVehicle()) {
                  if ($$7 == 0.0) {
                     $$8 = $$10;
                     $$9 = $$13;
                  }
               } else {
                  $$8 = $$10;
                  $$9 = $$13;
                  $$7 = $$14;
               }
            }
         }
      }

      return $$8 == null ? null : new EntityHitResult($$8, $$9);
   }

   @Nullable
   public static EntityHitResult getEntityHitResult(Level $$0, Projectile $$1, Vec3 $$2, Vec3 $$3, AABB $$4, Predicate<Entity> $$5) {
      return getEntityHitResult($$0, $$1, $$2, $$3, $$4, $$5, computeMargin($$1));
   }

   public static float computeMargin(Entity $$0) {
      return Math.max(0.0F, Math.min(0.3F, (float)($$0.tickCount - 2) / 20.0F));
   }

   @Nullable
   public static EntityHitResult getEntityHitResult(Level $$0, Entity $$1, Vec3 $$2, Vec3 $$3, AABB $$4, Predicate<Entity> $$5, float $$6) {
      double $$7 = Double.MAX_VALUE;
      Optional<Vec3> $$8 = Optional.empty();
      Entity $$9 = null;

      for(Entity $$10 : $$0.getEntities($$1, $$4, $$5)) {
         AABB $$11 = $$10.getBoundingBox().inflate((double)$$6);
         Optional<Vec3> $$12 = $$11.clip($$2, $$3);
         if ($$12.isPresent()) {
            double $$13 = $$2.distanceToSqr((Vec3)$$12.get());
            if ($$13 < $$7) {
               $$9 = $$10;
               $$7 = $$13;
               $$8 = $$12;
            }
         }
      }

      return $$9 == null ? null : new EntityHitResult($$9, (Vec3)$$8.get());
   }

   public static void rotateTowardsMovement(Entity $$0, float $$1) {
      Vec3 $$2 = $$0.getDeltaMovement();
      if ($$2.lengthSqr() != 0.0) {
         double $$3 = $$2.horizontalDistance();
         $$0.setYRot((float)(Mth.atan2($$2.z, $$2.x) * 180.0F / (float)Math.PI) + 90.0F);
         $$0.setXRot((float)(Mth.atan2($$3, $$2.y) * 180.0F / (float)Math.PI) - 90.0F);

         while($$0.getXRot() - $$0.xRotO < -180.0F) {
            $$0.xRotO -= 360.0F;
         }

         while($$0.getXRot() - $$0.xRotO >= 180.0F) {
            $$0.xRotO += 360.0F;
         }

         while($$0.getYRot() - $$0.yRotO < -180.0F) {
            $$0.yRotO -= 360.0F;
         }

         while($$0.getYRot() - $$0.yRotO >= 180.0F) {
            $$0.yRotO += 360.0F;
         }

         $$0.setXRot(Mth.lerp($$1, $$0.xRotO, $$0.getXRot()));
         $$0.setYRot(Mth.lerp($$1, $$0.yRotO, $$0.getYRot()));
      }
   }

   public static InteractionHand getWeaponHoldingHand(LivingEntity $$0, Item $$1) {
      return $$0.getMainHandItem().is($$1) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
   }

   public static AbstractArrow getMobArrow(LivingEntity $$0, ItemStack $$1, float $$2, @Nullable ItemStack $$3) {
      ArrowItem $$4 = (ArrowItem)($$1.getItem() instanceof ArrowItem ? $$1.getItem() : Items.ARROW);
      AbstractArrow $$5 = $$4.createArrow($$0.level(), $$1, $$0, $$3);
      $$5.setBaseDamageFromMob($$2);
      return $$5;
   }
}
