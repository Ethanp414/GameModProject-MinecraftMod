// package dibs.bossfight.entity.custom;

// import Entity.ModEntities;
// import dibs.bossfight.ModItems;
// import net.minecraft.core.Direction;
// import net.minecraft.sounds.SoundEvents;
// import net.minecraft.sounds.SoundSource;
// import net.minecraft.world.entity.EntityType;
// import net.minecraft.world.entity.LivingEntity;
// import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
// import net.minecraft.world.item.Item;
// import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.level.Level;
// import net.minecraft.world.phys.BlockHitResult;
// import net.minecraft.world.phys.EntityHitResult;
// import net.minecraft.world.phys.Vec3;

// public class BasketballProjectileEntity extends ThrowableItemProjectile {
//     private float rotation;
    
//     public BasketballProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
//         super(type, level);
//     }

//     public BasketballProjectileEntity(Level level, LivingEntity shooter) {
//         super((EntityType<? extends ThrowableItemProjectile>) ModEntities.BASKETBALL.get(), level);
//         setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
//         setOwner(shooter);
//     }

//     public BasketballProjectileEntity(Level level, double x, double y, double z) {
//         super((EntityType<? extends ThrowableItemProjectile>) ModEntities.BASKETBALL.get(), level);
//         setPos(x, y, z);
//     }

//     @Override
//     protected Item getDefaultItem() {
//         return ModItems.BASKETBALL.get();
//     }

//     public float getRenderingRotation() {
//         rotation += 0.5f;
//         if(rotation >= 360) {
//             rotation = 0;
//         }
//         return rotation;
//     }

//     @Override
//     protected void onHitEntity(EntityHitResult result) {
//         super.onHitEntity(result);
//         if (!this.level().isClientSide) {
//             var entity = result.getEntity();
//             // Calculate momentum-based damage (more speed = more damage)
//             Vec3 motion = this.getDeltaMovement();
//             float damage = (float) (4.0f * motion.length());  // Base damage multiplied by speed
            
//             // Apply damage using non-deprecated method
//             entity.invulnerableTime = 0; // Allow immediate damage
//             if (entity instanceof LivingEntity) {
//                 ((LivingEntity) entity).knockback(0.4D, 
//                     this.getX() - entity.getX(),
//                     this.getZ() - entity.getZ());
//             }
            
//             // Apply damage using DamageSource
//             entity.addDeltaMovement(motion.scale(0.1)); // Add some knockback
//             entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.1, 0)); // Slight upward bounce
//             if (entity instanceof LivingEntity livingEntity) {
//                 livingEntity.setHealth(livingEntity.getHealth() - damage);
//             }
            
//             // Bounce off with reduced velocity
//             Vec3 bounceDirection = this.position().subtract(entity.position()).normalize();
//             this.setDeltaMovement(bounceDirection.scale(motion.length() * 0.6)); // 60% of original speed
            
//             // Add sound effect
//             this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
//                     SoundEvents.SLIME_BLOCK_HIT, 
//                     SoundSource.NEUTRAL,
//                     1.0F, 1.0F);
//         }
//     }

//     @Override
//     protected void onHitBlock(BlockHitResult result) {
//         super.onHitBlock(result);
//         if (!this.level().isClientSide) {
//             Vec3 motion = this.getDeltaMovement();
//             Direction dir = result.getDirection();

//             Vec3 normal = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
            
//             // Reflect the motion vector off the surface (with energy loss)
//             double bounceFactor = 0.6; // 60% energy retention
//             Vec3 reflection = motion.subtract(normal.scale(2.0D * motion.dot(normal))).scale(bounceFactor);
            
//             // Only bounce if moving fast enough, otherwise come to rest
//             if (reflection.length() > 0.1) {
//                 this.setDeltaMovement(reflection);
//                 // Play bounce sound
//                 this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
//                         SoundEvents.SLIME_BLOCK_HIT,
//                         SoundSource.NEUTRAL,
//                         1.0F, 1.2F);
//             } else {
//                 this.discard(); // Remove if basically stopped
//                 // Drop as item
//                 // Drop a new basketball item
//                 net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
//                     this.level(), this.getX(), this.getY(), this.getZ(),
//                     new ItemStack(this.getDefaultItem()));
//                 this.level().addFreshEntity(itemEntity);
//             }
//         }
//     }

//     @Override
//     public void tick() {
//         super.tick();
        
//         // Add drag/air resistance
//         Vec3 motion = this.getDeltaMovement();
//         double drag = 0.99; // Slight air resistance
//         this.setDeltaMovement(motion.scale(drag));
        
//         // Despawn after 15 seconds
//         if (!this.level().isClientSide && this.tickCount > 300) {
//             this.discard();
//         }
//     }
// }