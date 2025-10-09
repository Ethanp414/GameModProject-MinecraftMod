package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityFart;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ItemStinkRay extends Item {
   public static final Predicate<ItemStack> IS_FART_BOTTLE = stack -> stack.m_41720_() == AMItemRegistry.STINK_BOTTLE.get();

   public ItemStinkRay(Properties properties) {
      super(properties);
   }

   public int m_8105_(ItemStack stack) {
      return isUsable(stack) ? 72000 : 0;
   }

   public UseAnim m_6164_(ItemStack stack) {
      return UseAnim.BOW;
   }

   public static boolean isUsable(ItemStack stack) {
      return stack.m_41773_() < stack.m_41776_() - 1;
   }

   public boolean m_142522_(ItemStack itemStack) {
      return super.m_142522_(itemStack) && isUsable(itemStack);
   }

   public static float getPowerForTime(int i) {
      float f = i / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public void m_5551_(ItemStack itemStack, Level level, LivingEntity entity, int time) {
      if (entity instanceof Player player && isUsable(itemStack)) {
         int i = this.m_8105_(itemStack) - time;
         if (i >= 10) {
            boolean left = false;
            if (entity.m_7655_() == InteractionHand.OFF_HAND && entity.m_5737_() == HumanoidArm.RIGHT
               || entity.m_7655_() == InteractionHand.MAIN_HAND && entity.m_5737_() == HumanoidArm.LEFT) {
               left = true;
            }

            EntityFart blood = new EntityFart(level, entity, !left);
            Vec3 vector3d = entity.m_20252_(1.0F);
            RandomSource rand = level.m_213780_();
            entity.m_146850_(GameEvent.f_223698_);
            entity.m_5496_((SoundEvent)AMSoundRegistry.STINK_RAY.get(), 1.0F, 0.9F + (rand.m_188501_() - rand.m_188501_()) * 0.2F);
            blood.shoot(vector3d.m_7096_(), vector3d.m_7098_(), vector3d.m_7094_(), 0.2F + getPowerForTime(i) * 0.4F, 10.0F);
            if (!level.f_46443_) {
               level.m_7967_(blood);
            }

            itemStack.m_41622_(1, entity, breaker -> breaker.m_21190_(entity.m_7655_()));
         }
      }
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.m_21120_(handIn);
      playerIn.m_6672_(handIn);
      if (!isUsable(itemstack)) {
         ItemStack ammo = this.findAmmo(playerIn);
         boolean flag = playerIn.m_7500_();
         if (!ammo.m_41619_()) {
            ammo.m_41774_(1);
            ItemStack bottle = new ItemStack(Items.f_42590_);
            if (!playerIn.m_36356_(bottle)) {
               playerIn.m_36176_(bottle, false);
            }

            flag = true;
         }

         if (flag) {
            itemstack.m_41721_(0);
         }
      }

      return InteractionResultHolder.m_19096_(itemstack);
   }

   public ItemStack findAmmo(Player entity) {
      if (entity.m_7500_()) {
         return new ItemStack((ItemLike)AMItemRegistry.STINK_BOTTLE.get());
      } else {
         for (int i = 0; i < entity.m_150109_().m_6643_(); i++) {
            ItemStack itemstack1 = entity.m_150109_().m_8020_(i);
            if (IS_FART_BOTTLE.test(itemstack1)) {
               return itemstack1;
            }
         }

         return ItemStack.f_41583_;
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.m_41656_(oldStack, newStack);
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getISTERProperties());
   }
}
