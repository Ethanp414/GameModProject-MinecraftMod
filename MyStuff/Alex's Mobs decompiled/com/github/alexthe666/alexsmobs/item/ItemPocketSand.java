package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntitySandShot;
import java.util.function.Predicate;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ItemPocketSand extends Item {
   public static final Predicate<ItemStack> IS_SAND = stack -> stack.m_204117_(ItemTags.f_13137_);

   public ItemPocketSand(Properties properties) {
      super(properties);
   }

   public ItemStack findAmmo(Player entity) {
      if (entity.m_7500_()) {
         return ItemStack.f_41583_;
      } else {
         for (int i = 0; i < entity.m_150109_().m_6643_(); i++) {
            ItemStack itemstack1 = entity.m_150109_().m_8020_(i);
            if (IS_SAND.test(itemstack1)) {
               return itemstack1;
            }
         }

         return ItemStack.f_41583_;
      }
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player livingEntityIn, InteractionHand handIn) {
      ItemStack itemstack = livingEntityIn.m_21120_(handIn);
      ItemStack ammo = this.findAmmo(livingEntityIn);
      if (livingEntityIn.m_7500_()) {
         ammo = new ItemStack(Items.f_41830_);
      }

      if (!worldIn.f_46443_ && !ammo.m_41619_()) {
         livingEntityIn.m_146850_(GameEvent.f_223698_);
         worldIn.m_6263_(
            (Player)null,
            livingEntityIn.m_20185_(),
            livingEntityIn.m_20186_(),
            livingEntityIn.m_20189_(),
            SoundEvents.f_12331_,
            SoundSource.PLAYERS,
            0.5F,
            0.4F + (livingEntityIn.m_217043_().m_188501_() * 0.4F + 0.8F)
         );
         boolean left = false;
         if (livingEntityIn.m_7655_() == InteractionHand.OFF_HAND && livingEntityIn.m_5737_() == HumanoidArm.RIGHT
            || livingEntityIn.m_7655_() == InteractionHand.MAIN_HAND && livingEntityIn.m_5737_() == HumanoidArm.LEFT) {
            left = true;
         }

         EntitySandShot blood = new EntitySandShot(worldIn, livingEntityIn, !left);
         Vec3 vector3d = livingEntityIn.m_20252_(1.0F);
         blood.shoot(vector3d.m_7096_(), vector3d.m_7098_(), vector3d.m_7094_(), 1.2F, 11.0F);
         if (!worldIn.f_46443_) {
            worldIn.m_7967_(blood);
         }

         livingEntityIn.m_36335_().m_41524_(this, 2);
         ammo.m_41774_(1);
         itemstack.m_41622_(1, livingEntityIn, player -> player.m_21190_(livingEntityIn.m_7655_()));
      }

      livingEntityIn.m_36246_(Stats.f_12982_.m_12902_(this));
      return InteractionResultHolder.m_19092_(itemstack, worldIn.m_5776_());
   }
}
