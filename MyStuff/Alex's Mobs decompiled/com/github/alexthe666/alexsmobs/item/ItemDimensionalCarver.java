package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ItemDimensionalCarver extends Item {
   public static final int MAX_TIME = 200;

   public ItemDimensionalCarver(Properties props) {
      super(props);
   }

   protected static BlockHitResult rayTracePortal(Level worldIn, Player player, Fluid fluidMode) {
      float f = player.m_146909_();
      float f1 = player.m_146908_();
      Vec3 vector3d = player.m_20299_(1.0F);
      float f11 = -f1 * (float) (Math.PI / 180.0) - (float) Math.PI;
      float f12 = -f * (float) (Math.PI / 180.0);
      float f2 = Mth.m_14089_(f11);
      float f3 = Mth.m_14031_(f11);
      float f4 = -Mth.m_14089_(f12);
      float f5 = Mth.m_14031_(f12);
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d0 = 1.5;
      Vec3 vector3d1 = vector3d.m_82520_(f6 * 1.5, f5 * 1.5, f7 * 1.5);
      return worldIn.m_45547_(new ClipContext(vector3d, vector3d1, Block.OUTLINE, fluidMode, player));
   }

   public int getItemStackLimit(ItemStack stack) {
      return 1;
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.m_21120_(handIn);
      if (itemstack.m_41773_() >= itemstack.m_41776_()) {
         return InteractionResultHolder.m_19100_(itemstack);
      } else {
         playerIn.m_6672_(handIn);
         HitResult raytraceresult = rayTracePortal(worldIn, playerIn, Fluid.ANY);
         Direction dir = Direction.m_122382_(playerIn)[0];
         double x = raytraceresult.m_82450_().f_82479_ - dir.m_122436_().m_123341_() * 0.1F;
         double y = raytraceresult.m_82450_().f_82480_ - dir.m_122436_().m_123342_() * 0.1F;
         double z = raytraceresult.m_82450_().f_82481_ - dir.m_122436_().m_123343_() * 0.1F;
         if (itemstack.m_41784_().m_128471_("HASBLOCK")) {
            x = itemstack.m_41784_().m_128459_("BLOCKX");
            y = itemstack.m_41784_().m_128459_("BLOCKY");
            z = itemstack.m_41784_().m_128459_("BLOCKZ");
         } else {
            itemstack.m_41784_().m_128379_("HASBLOCK", true);
            itemstack.m_41784_().m_128347_("BLOCKX", x);
            itemstack.m_41784_().m_128347_("BLOCKY", y);
            itemstack.m_41784_().m_128347_("BLOCKZ", z);
            itemstack.m_41751_(itemstack.m_41784_());
         }

         worldIn.m_7106_((ParticleOptions)AMParticleRegistry.INVERT_DIG.get(), x, y, z, playerIn.m_19879_(), 0.0, 0.0);
         return InteractionResultHolder.m_19096_(itemstack);
      }
   }

   public int m_8105_(ItemStack stack) {
      return 200;
   }

   public float getXpRepairRatio(ItemStack stack) {
      return 100.0F;
   }

   public void m_5929_(Level level, LivingEntity player, ItemStack itemstack, int count) {
      player.m_6674_(player.m_7655_());
      RandomSource random = player.m_217043_();
      if (count % 5 == 0) {
         player.m_146850_(GameEvent.f_223698_);
         player.m_5496_(SoundEvents.f_12201_, 1.0F, 0.5F + random.m_188501_());
      }

      boolean flag = false;
      if (itemstack.m_41784_().m_128471_("HASBLOCK")) {
         double x = itemstack.m_41784_().m_128459_("BLOCKX");
         double y = itemstack.m_41784_().m_128459_("BLOCKY");
         double z = itemstack.m_41784_().m_128459_("BLOCKZ");
         if (random.m_188501_() < 0.2) {
            player.m_9236_()
               .m_7106_(
                  (ParticleOptions)AMParticleRegistry.WORM_PORTAL.get(),
                  x + random.m_188583_() * 0.1F,
                  y + random.m_188583_() * 0.1F,
                  z + random.m_188583_() * 0.1F,
                  random.m_188583_() * 0.1F,
                  -0.1F,
                  random.m_188583_() * 0.1F
               );
         }

         if (player.m_20275_(x, y, z) > 9.0) {
            flag = true;
            if (player instanceof Player) {
               ((Player)player).m_36335_().m_41524_(this, 40);
            }
         }

         if (count == 1 && !player.m_9236_().f_46443_) {
            player.m_146850_(GameEvent.f_223698_);
            player.m_5496_(SoundEvents.f_11983_, 1.0F, 0.5F);
            EntityVoidPortal portal = new EntityVoidPortal(player.m_9236_(), this);
            portal.m_6034_(x, y, z);
            Direction dir = Direction.m_122382_(player)[0].m_122424_();
            if (dir == Direction.UP) {
               dir = Direction.DOWN;
            }

            portal.setAttachmentFacing(dir);
            player.m_9236_().m_7967_(portal);
            this.onPortalOpen(player.m_9236_(), player, portal, dir);
            itemstack.m_41622_(1, player, playerIn -> player.m_21190_(playerIn.m_7655_()));
            flag = true;
            if (player instanceof Player) {
               ((Player)player).m_36335_().m_41524_(this, 200);
            }
         }
      }

      if (flag) {
         player.m_5810_();
         itemstack.m_41784_().m_128379_("HASBLOCK", false);
         itemstack.m_41784_().m_128347_("BLOCKX", 0.0);
         itemstack.m_41784_().m_128347_("BLOCKY", 0.0);
         itemstack.m_41784_().m_128347_("BLOCKZ", 0.0);
         itemstack.m_41751_(itemstack.m_41784_());
      }
   }

   public void m_5551_(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
      stack.m_41784_().m_128379_("HASBLOCK", false);
      stack.m_41784_().m_128347_("BLOCKX", 0.0);
      stack.m_41784_().m_128347_("BLOCKY", 0.0);
      stack.m_41784_().m_128347_("BLOCKZ", 0.0);
      stack.m_41751_(stack.m_41784_());
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.m_41656_(oldStack, newStack);
   }

   public void onPortalOpen(Level worldIn, LivingEntity player, EntityVoidPortal portal, Direction dir) {
      portal.setLifespan(1200);
      ResourceKey<Level> respawnDimension = Level.f_46428_;
      BlockPos respawnPosition = player.m_21257_().isPresent()
         ? (BlockPos)player.m_21257_().get()
         : player.m_9236_().m_5452_(Types.MOTION_BLOCKING, BlockPos.f_121853_);
      if (player instanceof ServerPlayer serverPlayer) {
         respawnDimension = serverPlayer.m_8963_();
         if (serverPlayer.m_8961_() != null) {
            respawnPosition = serverPlayer.m_8961_();
         }
      }

      portal.exitDimension = respawnDimension;
      portal.setDestination(respawnPosition.m_6630_(2));
   }
}
