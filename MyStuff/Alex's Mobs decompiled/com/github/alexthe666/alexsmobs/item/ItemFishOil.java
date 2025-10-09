package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemFishOil extends Item {
   public ItemFishOil(Properties p_i225737_1_) {
      super(p_i225737_1_);
   }

   public ItemStack m_5922_(ItemStack p_77654_1_, Level p_77654_2_, LivingEntity p_77654_3_) {
      super.m_5922_(p_77654_1_, p_77654_2_, p_77654_3_);
      if (AMConfig.fishOilMeme) {
         p_77654_3_.m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.OILED.get(), 1200, 0));
      }

      if (p_77654_3_ instanceof ServerPlayer lvt_4_1_) {
         CriteriaTriggers.f_10592_.m_23682_(lvt_4_1_, p_77654_1_);
         lvt_4_1_.m_36246_(Stats.f_12982_.m_12902_(this));
      }

      if (p_77654_1_.m_41619_()) {
         return new ItemStack(Items.f_42590_);
      } else {
         if (p_77654_3_ instanceof Player && !((Player)p_77654_3_).m_150110_().f_35937_) {
            ItemStack lvt_4_2_ = new ItemStack(Items.f_42590_);
            Player lvt_5_1_ = (Player)p_77654_3_;
            if (!lvt_5_1_.m_150109_().m_36054_(lvt_4_2_)) {
               lvt_5_1_.m_36176_(lvt_4_2_, false);
            }
         }

         return p_77654_1_;
      }
   }

   public int m_8105_(ItemStack p_77626_1_) {
      return 40;
   }

   public UseAnim m_6164_(ItemStack p_77661_1_) {
      return UseAnim.DRINK;
   }

   public SoundEvent m_6023_() {
      return SoundEvents.f_11970_;
   }

   public SoundEvent m_6061_() {
      return SoundEvents.f_11970_;
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand interactionHand) {
      return ItemUtils.m_150959_(level, player, interactionHand);
   }
}
