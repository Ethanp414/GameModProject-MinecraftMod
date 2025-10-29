package dibs.bossfight.item.custom;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import Entity.custom.BasketballProjectileEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;

public class BasketballItem extends Item implements ProjectileItem {
    public static float PROJECTILE_SHOOT_POWER = 1.5F;

    public BasketballItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
                
        if (!pLevel.isClientSide) {
            BasketballProjectileEntity basketballProjectile = new BasketballProjectileEntity(pPlayer, pLevel);
            basketballProjectile.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 0F);
            pLevel.addFreshEntity(basketballProjectile);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level pLevel, Position pPosition, ItemStack pItemStack, Direction pDirection) {
        return new Snowball(pLevel, pPosition.x(), pPosition.y(), pPosition.z(), pItemStack);
    }



}