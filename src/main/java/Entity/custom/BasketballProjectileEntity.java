package Entity.custom;

import Entity.ModEntities;
import dibs.bossfight.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;

public class BasketballProjectileEntity extends ThrowableItemProjectile {


    public BasketballProjectileEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public BasketballProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.BASKETBALL.get(), shooter, level);
    }

    public BasketballProjectileEntity(Level level, double x, double y, double z) {
        super(ModEntities.BASKETBALL.get(), x, y, z, level);
    }
    /*
    public BasketballProjectileEntity(EntityType<? extends Snowball> entityType, Level level) {
        super(entityType, level);
    }

    public BasketballProjectileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.BASKETBALL.get(), shooter, level, new ItemStack(ModItems.BASKETBALL.get()), null);
    }

    public BasketballProjectileEntity(Level level, LivingEntity owner, ItemStack item) {
        super(EntityType.SNOWBALL, owner, level, item);
    }

    public BasketballProjectileEntity(Level level, double x, double y, double z, ItemStack item) {
        super(EntityType.SNOWBALL, x, y, z, level, item);
    }
    */


    /*
    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.BASKETBALL.get());
    }
    */
    
    @Override
    protected Item getDefaultItem() {
        return ModItems.BASKETBALL.get();
    }


    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 4);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    /*
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if(result.getDirection() == Direction.SOUTH) {
            groundedOffset = new Vec2(215f,180f);
        }
        if(result.getDirection() == Direction.NORTH) {
            groundedOffset = new Vec2(215f, 0f);
        }
        if(result.getDirection() == Direction.EAST) {
            groundedOffset = new Vec2(215f,-90f);
        }
        if(result.getDirection() == Direction.WEST) {
            groundedOffset = new Vec2(215f,90f);
        }

        if(result.getDirection() == Direction.DOWN) {
            groundedOffset = new Vec2(115f,180f);
        }
        if(result.getDirection() == Direction.UP) {
            groundedOffset = new Vec2(285f,180f);
        }
    }
    */
}