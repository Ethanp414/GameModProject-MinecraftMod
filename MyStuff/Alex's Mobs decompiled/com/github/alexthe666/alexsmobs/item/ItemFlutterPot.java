package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;

public class ItemFlutterPot extends Item implements DispensibleContainerItem {
   public ItemFlutterPot(Properties builder) {
      super(builder.m_41487_(1));
   }

   public InteractionResult m_6225_(UseOnContext context) {
      Level world = context.m_43725_();
      BlockPos blockpos = context.m_8083_();
      if (world.f_46443_) {
         return InteractionResult.PASS;
      } else {
         if (this.placeFish((ServerLevel)world, context.m_43722_(), blockpos) && (context.m_43723_() == null || !context.m_43723_().m_7500_())) {
            context.m_43722_().m_41774_(1);
         }

         return InteractionResult.m_19078_(world.f_46443_);
      }
   }

   protected void playEmptySound(@Nullable Player player, LevelAccessor worldIn, BlockPos pos) {
      worldIn.m_5594_(player, pos, SoundEvents.f_11779_, SoundSource.NEUTRAL, 1.0F, 1.0F);
   }

   private boolean placeFish(ServerLevel worldIn, ItemStack stack, BlockPos pos) {
      Entity entity = ((EntityType)AMEntityRegistry.FLUTTER.get()).m_20592_(worldIn, stack, (Player)null, pos, MobSpawnType.BUCKET, true, false);
      if (entity != null && entity instanceof EntityFlutter) {
         CompoundTag compoundnbt = stack.m_41784_();
         if (compoundnbt.m_128441_("FlutterData")) {
            ((EntityFlutter)entity).m_7378_(compoundnbt.m_128469_("FlutterData"));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean m_142073_(
      @org.jetbrains.annotations.Nullable Player p_150821_, Level p_150822_, BlockPos p_150823_, @org.jetbrains.annotations.Nullable BlockHitResult p_150824_
   ) {
      return false;
   }
}
