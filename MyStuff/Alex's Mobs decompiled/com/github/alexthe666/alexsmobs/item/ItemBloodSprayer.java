package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import java.util.function.Predicate;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ItemBloodSprayer extends Item {
   public static final Predicate<ItemStack> IS_BLOOD = stack -> stack.m_41720_() == AMItemRegistry.BLOOD_SAC.get();

   public ItemBloodSprayer(Properties properties) {
      super(properties);
   }

   public int m_8105_(ItemStack stack) {
      return isUsable(stack) ? Integer.MAX_VALUE : 0;
   }

   public UseAnim m_6164_(ItemStack stack) {
      return UseAnim.BOW;
   }

   public static boolean isUsable(ItemStack stack) {
      return stack.m_41773_() < stack.m_41776_() - 1;
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.m_21120_(handIn);
      playerIn.m_6672_(handIn);
      if (!isUsable(itemstack)) {
         ItemStack ammo = this.findAmmo(playerIn);
         boolean flag = playerIn.m_7500_();
         if (!ammo.m_41619_()) {
            ammo.m_41774_(1);
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
         return ItemStack.f_41583_;
      } else {
         for (int i = 0; i < entity.m_150109_().m_6643_(); i++) {
            ItemStack itemstack1 = entity.m_150109_().m_8020_(i);
            if (IS_BLOOD.test(itemstack1)) {
               return itemstack1;
            }
         }

         return ItemStack.f_41583_;
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.m_41656_(oldStack, newStack);
   }

   public boolean m_142522_(ItemStack itemStack) {
      return super.m_142522_(itemStack) && isUsable(itemStack);
   }

   public void m_5929_(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
      if (isUsable(stack)) {
         if (count % 2 == 0) {
            boolean left = false;
            if (livingEntityIn.m_7655_() == InteractionHand.OFF_HAND && livingEntityIn.m_5737_() == HumanoidArm.RIGHT
               || livingEntityIn.m_7655_() == InteractionHand.MAIN_HAND && livingEntityIn.m_5737_() == HumanoidArm.LEFT) {
               left = true;
            }

            EntityMosquitoSpit blood = new EntityMosquitoSpit(worldIn, livingEntityIn, !left);
            Vec3 vector3d = livingEntityIn.m_20252_(1.0F);
            RandomSource rand = worldIn.m_213780_();
            livingEntityIn.m_146850_(GameEvent.f_223698_);
            livingEntityIn.m_5496_(SoundEvents.f_12032_, 1.0F, 1.2F + (rand.m_188501_() - rand.m_188501_()) * 0.2F);
            blood.shoot(vector3d.m_7096_(), vector3d.m_7098_(), vector3d.m_7094_(), 1.0F, 10.0F);
            if (!worldIn.f_46443_) {
               worldIn.m_7967_(blood);
            }

            stack.m_41622_(1, livingEntityIn, player -> player.m_21190_(livingEntityIn.m_7655_()));
         }
      } else if (livingEntityIn instanceof Player) {
         ItemStack ammo = this.findAmmo((Player)livingEntityIn);
         boolean flag = ((Player)livingEntityIn).m_7500_();
         if (!ammo.m_41619_()) {
            ammo.m_41774_(1);
            flag = true;
         }

         if (flag) {
            ((Player)livingEntityIn).m_36335_().m_41524_(this, 20);
            stack.m_41721_(0);
         }

         livingEntityIn.m_5810_();
      }
   }
}
