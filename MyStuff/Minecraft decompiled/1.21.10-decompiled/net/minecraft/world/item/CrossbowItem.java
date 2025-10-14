package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrossbowItem extends ProjectileWeaponItem {
   private static final float MAX_CHARGE_DURATION = 1.25F;
   public static final int DEFAULT_RANGE = 8;
   private boolean startSoundPlayed = false;
   private boolean midLoadSoundPlayed = false;
   private static final float START_SOUND_PERCENT = 0.2F;
   private static final float MID_SOUND_PERCENT = 0.5F;
   private static final float ARROW_POWER = 3.15F;
   private static final float FIREWORK_POWER = 1.6F;
   public static final float MOB_ARROW_POWER = 1.6F;
   private static final CrossbowItem.ChargingSounds DEFAULT_SOUNDS = new CrossbowItem.ChargingSounds(
      Optional.of(SoundEvents.CROSSBOW_LOADING_START), Optional.of(SoundEvents.CROSSBOW_LOADING_MIDDLE), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
   );

   public CrossbowItem(Item.Properties $$0) {
      super($$0);
   }

   @Override
   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return ARROW_OR_FIREWORK;
   }

   @Override
   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }

   @Override
   public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
      ItemStack $$3 = $$1.getItemInHand($$2);
      ChargedProjectiles $$4 = $$3.get(DataComponents.CHARGED_PROJECTILES);
      if ($$4 != null && !$$4.isEmpty()) {
         this.performShooting($$0, $$1, $$2, $$3, getShootingPower($$4), 1.0F, null);
         return InteractionResult.CONSUME;
      } else if (!$$1.getProjectile($$3).isEmpty()) {
         this.startSoundPlayed = false;
         this.midLoadSoundPlayed = false;
         $$1.startUsingItem($$2);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.FAIL;
      }
   }

   private static float getShootingPower(ChargedProjectiles $$0) {
      return $$0.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
   }

   @Override
   public boolean releaseUsing(ItemStack $$0, Level $$1, LivingEntity $$2, int $$3) {
      int $$4 = this.getUseDuration($$0, $$2) - $$3;
      return getPowerForTime($$4, $$0, $$2) >= 1.0F && isCharged($$0);
   }

   private static boolean tryLoadProjectiles(LivingEntity $$0, ItemStack $$1) {
      List<ItemStack> $$2 = draw($$1, $$0.getProjectile($$1), $$0);
      if (!$$2.isEmpty()) {
         $$1.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of($$2));
         return true;
      } else {
         return false;
      }
   }

   public static boolean isCharged(ItemStack $$0) {
      ChargedProjectiles $$1 = $$0.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
      return !$$1.isEmpty();
   }

   @Override
   protected void shootProjectile(LivingEntity $$0, Projectile $$1, int $$2, float $$3, float $$4, float $$5, @Nullable LivingEntity $$6) {
      Vector3f $$11;
      if ($$6 != null) {
         double $$7 = $$6.getX() - $$0.getX();
         double $$8 = $$6.getZ() - $$0.getZ();
         double $$9 = Math.sqrt($$7 * $$7 + $$8 * $$8);
         double $$10 = $$6.getY(0.3333333333333333) - $$1.getY() + $$9 * 0.2F;
         $$11 = getProjectileShotVector($$0, new Vec3($$7, $$10, $$8), $$5);
      } else {
         Vec3 $$12 = $$0.getUpVector(1.0F);
         Quaternionf $$13 = new Quaternionf().setAngleAxis((double)($$5 * (float) (Math.PI / 180.0)), $$12.x, $$12.y, $$12.z);
         Vec3 $$14 = $$0.getViewVector(1.0F);
         $$11 = $$14.toVector3f().rotate($$13);
      }

      $$1.shoot((double)$$11.x(), (double)$$11.y(), (double)$$11.z(), $$3, $$4);
      float $$16 = getShotPitch($$0.getRandom(), $$2);
      $$0.level().playSound(null, $$0.getX(), $$0.getY(), $$0.getZ(), SoundEvents.CROSSBOW_SHOOT, $$0.getSoundSource(), 1.0F, $$16);
   }

   private static Vector3f getProjectileShotVector(LivingEntity $$0, Vec3 $$1, float $$2) {
      Vector3f $$3 = $$1.toVector3f().normalize();
      Vector3f $$4 = new Vector3f($$3).cross(new Vector3f(0.0F, 1.0F, 0.0F));
      if ((double)$$4.lengthSquared() <= 1.0E-7) {
         Vec3 $$5 = $$0.getUpVector(1.0F);
         $$4 = new Vector3f($$3).cross($$5.toVector3f());
      }

      Vector3f $$6 = new Vector3f($$3).rotateAxis((float) (Math.PI / 2), $$4.x, $$4.y, $$4.z);
      return new Vector3f($$3).rotateAxis($$2 * (float) (Math.PI / 180.0), $$6.x, $$6.y, $$6.z);
   }

   @Override
   protected Projectile createProjectile(Level $$0, LivingEntity $$1, ItemStack $$2, ItemStack $$3, boolean $$4) {
      if ($$3.is(Items.FIREWORK_ROCKET)) {
         return new FireworkRocketEntity($$0, $$3, $$1, $$1.getX(), $$1.getEyeY() - 0.15F, $$1.getZ(), true);
      } else {
         Projectile $$5 = super.createProjectile($$0, $$1, $$2, $$3, $$4);
         if ($$5 instanceof AbstractArrow $$6) {
            $$6.setSoundEvent(SoundEvents.CROSSBOW_HIT);
         }

         return $$5;
      }
   }

   @Override
   protected int getDurabilityUse(ItemStack $$0) {
      return $$0.is(Items.FIREWORK_ROCKET) ? 3 : 1;
   }

   public void performShooting(Level $$0, LivingEntity $$1, InteractionHand $$2, ItemStack $$3, float $$4, float $$5, @Nullable LivingEntity $$6) {
      if ($$0 instanceof ServerLevel $$7) {
         ChargedProjectiles $$9 = $$3.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
         if ($$9 != null && !$$9.isEmpty()) {
            this.shoot($$7, $$1, $$2, $$3, $$9.getItems(), $$4, $$5, $$1 instanceof Player, $$6);
            if ($$1 instanceof ServerPlayer $$10) {
               CriteriaTriggers.SHOT_CROSSBOW.trigger($$10, $$3);
               $$10.awardStat(Stats.ITEM_USED.get($$3.getItem()));
            }
         }
      }
   }

   private static float getShotPitch(RandomSource $$0, int $$1) {
      return $$1 == 0 ? 1.0F : getRandomShotPitch(($$1 & 1) == 1, $$0);
   }

   private static float getRandomShotPitch(boolean $$0, RandomSource $$1) {
      float $$2 = $$0 ? 0.63F : 0.43F;
      return 1.0F / ($$1.nextFloat() * 0.5F + 1.8F) + $$2;
   }

   @Override
   public void onUseTick(Level $$0, LivingEntity $$1, ItemStack $$2, int $$3) {
      if (!$$0.isClientSide()) {
         CrossbowItem.ChargingSounds $$4 = this.getChargingSounds($$2);
         float $$5 = (float)($$2.getUseDuration($$1) - $$3) / (float)getChargeDuration($$2, $$1);
         if ($$5 < 0.2F) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
         }

         if ($$5 >= 0.2F && !this.startSoundPlayed) {
            this.startSoundPlayed = true;
            $$4.start().ifPresent($$2x -> $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), (SoundEvent)$$2x.value(), SoundSource.PLAYERS, 0.5F, 1.0F));
         }

         if ($$5 >= 0.5F && !this.midLoadSoundPlayed) {
            this.midLoadSoundPlayed = true;
            $$4.mid().ifPresent($$2x -> $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), (SoundEvent)$$2x.value(), SoundSource.PLAYERS, 0.5F, 1.0F));
         }

         if ($$5 >= 1.0F && !isCharged($$2) && tryLoadProjectiles($$1, $$2)) {
            $$4.end()
               .ifPresent(
                  $$2x -> $$0.playSound(
                        null,
                        $$1.getX(),
                        $$1.getY(),
                        $$1.getZ(),
                        (SoundEvent)$$2x.value(),
                        $$1.getSoundSource(),
                        1.0F,
                        1.0F / ($$0.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
                     )
               );
         }
      }
   }

   @Override
   public int getUseDuration(ItemStack $$0, LivingEntity $$1) {
      return 72000;
   }

   public static int getChargeDuration(ItemStack $$0, LivingEntity $$1) {
      float $$2 = EnchantmentHelper.modifyCrossbowChargingTime($$0, $$1, 1.25F);
      return Mth.floor($$2 * 20.0F);
   }

   @Override
   public ItemUseAnimation getUseAnimation(ItemStack $$0) {
      return ItemUseAnimation.CROSSBOW;
   }

   CrossbowItem.ChargingSounds getChargingSounds(ItemStack $$0) {
      return (CrossbowItem.ChargingSounds)EnchantmentHelper.pickHighestLevel($$0, EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS).orElse(DEFAULT_SOUNDS);
   }

   private static float getPowerForTime(int $$0, ItemStack $$1, LivingEntity $$2) {
      float $$3 = (float)$$0 / (float)getChargeDuration($$1, $$2);
      if ($$3 > 1.0F) {
         $$3 = 1.0F;
      }

      return $$3;
   }

   @Override
   public boolean useOnRelease(ItemStack $$0) {
      return $$0.is(this);
   }

   @Override
   public int getDefaultProjectileRange() {
      return 8;
   }

   public static enum ChargeType implements StringRepresentable {
      NONE("none"),
      ARROW("arrow"),
      ROCKET("rocket");

      public static final Codec<CrossbowItem.ChargeType> CODEC = StringRepresentable.fromEnum(CrossbowItem.ChargeType::values);
      private final String name;

      private ChargeType(final String param3) {
         this.name = $$0;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }

   public static record ChargingSounds(Optional<Holder<SoundEvent>> start, Optional<Holder<SoundEvent>> mid, Optional<Holder<SoundEvent>> end) {
      public static final Codec<CrossbowItem.ChargingSounds> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  SoundEvent.CODEC.optionalFieldOf("start").forGetter(CrossbowItem.ChargingSounds::start),
                  SoundEvent.CODEC.optionalFieldOf("mid").forGetter(CrossbowItem.ChargingSounds::mid),
                  SoundEvent.CODEC.optionalFieldOf("end").forGetter(CrossbowItem.ChargingSounds::end)
               )
               .apply($$0, CrossbowItem.ChargingSounds::new)
      );
   }
}
