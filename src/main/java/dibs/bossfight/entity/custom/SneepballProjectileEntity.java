package dibs.bossfight.entity.custom;

import Entity.custom.DibsEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SneepballProjectileEntity extends ThrowableItemProjectile {
   public SneepballProjectileEntity(EntityType<? extends SneepballProjectileEntity> $$0, Level $$1) {
      super($$0, $$1);
   }

   public SneepballProjectileEntity(Level $$0, LivingEntity $$1, ItemStack $$2) {
      super(EntityType.SNOWBALL, $$1, $$0, $$2);
   }

   public SneepballProjectileEntity(Level $$0, double $$1, double $$2, double $$3, ItemStack $$4) {
      super(EntityType.SNOWBALL, $$1, $$2, $$3, $$0, $$4);
   }

   @Override
   protected Item getDefaultItem() {
      return Items.SNOWBALL;
   }

   private ParticleOptions getParticle() {
      ItemStack $$0 = this.getItem();
      return (ParticleOptions)($$0.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, $$0));
   }

   @Override
   public void handleEntityEvent(byte $$0) {
      if ($$0 == 3) {
         ParticleOptions $$1 = this.getParticle();

         for(int $$2 = 0; $$2 < 8; ++$$2) {
            this.level().addParticle($$1, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   protected void onHitEntity(EntityHitResult $$0) {
      super.onHitEntity($$0);
      Entity entity = $$0.getEntity();
      //int $$2 = entity instanceof Blaze ? 3 : 0;
      float dmgAmount = entity instanceof DibsEntity ? 12 : 1.75f;
      entity.hurt(this.damageSources().thrown(this, this.getOwner()), dmgAmount);
   }

/*
   @Override
   protected void onHit(HitResult $$0) {
      super.onHit($$0);
      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)3);
         this.discard();
      }
   }
*/


   // use fast inverse square root to calculate bounce physics more quickly? need to look that shit up again lol

   // onHitBlock, sneepball bounces off surfaces

   @Override
   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      Direction direction = result.getDirection();
      Vec3 motion = this.getDeltaMovement();

      // Bounce logic: invert and dampen velocity based on hit face
      double bounceFactor = 0.45;
      double retainFactor = 0.65;
      double x = motion.x;
      double y = motion.y;
      double z = motion.z;

      if (motion.length() < 0.1) {
         this.discard();
         }

      switch (direction) {
         case EAST:
         case WEST:
            x = -x * bounceFactor;
            y = y * retainFactor;
            z = z * retainFactor;
            break;
         case NORTH:
         case SOUTH:
            x = x * retainFactor;
            y = y * retainFactor;
            z = -z * bounceFactor;
            break;
         case UP:
         case DOWN:
            x = x * retainFactor;
            y = -y * bounceFactor;
            z = z * retainFactor;
            break;
         default:
            break;


      }

      this.setDeltaMovement(x, y, z);

      // Play bounce sound
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.SLIME_BLOCK_STEP, SoundSource.NEUTRAL, 0.5f, 1.0f);
   }

/*
   @Override
   protected void onHitBlock(BlockHitResult $$0) {
      super.onHitBlock($$0);
      Direction direction = $$0.getDirection();
      Vec3 motion = this.getDeltaMovement();

      if (direction == Direction.EAST || direction == Direction.WEST) {
         this.setDeltaMovement(-motion.x * 0.7, motion.y * 0.9, motion.z * 0.9);
      } else if (direction == Direction.NORTH || direction == Direction.SOUTH) {
         this.setDeltaMovement(motion.x * 0.9, motion.y * 0.9, -motion.z * 0.7);
      } else if (direction == Direction.UP || direction == Direction.DOWN) {
         this.setDeltaMovement(motion.x * 0.9, -motion.y * 0.7, motion.z * 0.9);
      }

      // Play bounce sound
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.SLIME_BLOCK_STEP, SoundSource.NEUTRAL, 0.5f, 1.0f);
   }
*/

/*

    @Override
    public void tick() {
        super.tick();
        
        // Add drag/air resistance
        Vec3 motion = this.getDeltaMovement();
        double drag = 0.97; // Slight air resistance
        this.setDeltaMovement(motion.scale(drag));
        
        // Despawn after 15 seconds
        if (!this.level().isClientSide && this.tickCount > 300) {
            this.discard();
        }
    }
*/


}
