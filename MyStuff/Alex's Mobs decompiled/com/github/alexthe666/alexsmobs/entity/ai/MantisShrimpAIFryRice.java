package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MantisShrimpAIFryRice extends MoveToBlockGoal {
   private final EntityMantisShrimp mantisShrimp;
   private boolean wasLitPrior = false;
   private int cookingTicks = 0;

   public MantisShrimpAIFryRice(EntityMantisShrimp entityMantisShrimp) {
      super(entityMantisShrimp, 1.0, 8);
      this.mantisShrimp = entityMantisShrimp;
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public void m_8041_() {
      this.cookingTicks = 0;
      if (!this.wasLitPrior) {
         BlockPos blockpos = this.m_6669_().m_7495_();
         BlockState state = this.mantisShrimp.m_9236_().m_8055_(blockpos);
         if (state.m_60734_() instanceof AbstractFurnaceBlock && !this.wasLitPrior) {
            this.mantisShrimp.m_9236_().m_46597_(blockpos, (BlockState)state.m_61124_(AbstractFurnaceBlock.f_48684_, false));
         }
      }

      super.m_8041_();
   }

   public void m_8037_() {
      super.m_8037_();
      BlockPos blockpos = this.m_6669_().m_7495_();
      if (this.m_25625_()) {
         BlockState state = this.mantisShrimp.m_9236_().m_8055_(blockpos);
         if (this.mantisShrimp.punchProgress == 0.0F) {
            this.mantisShrimp.punch();
         }

         if (state.m_60734_() instanceof AbstractFurnaceBlock && !this.wasLitPrior) {
            this.mantisShrimp.m_9236_().m_46597_(blockpos, (BlockState)state.m_61124_(AbstractFurnaceBlock.f_48684_, true));
         }

         this.cookingTicks++;
         if (this.cookingTicks > 200) {
            this.cookingTicks = 0;
            ItemStack rice = new ItemStack((ItemLike)AMItemRegistry.SHRIMP_FRIED_RICE.get());
            rice.m_41764_(this.mantisShrimp.m_21205_().m_41613_());
            this.mantisShrimp.m_21008_(InteractionHand.MAIN_HAND, rice);
         }
      } else {
         this.cookingTicks = 0;
      }
   }

   public boolean m_8036_() {
      return this.mantisShrimp.m_21205_().m_204117_(AMTagRegistry.SHRIMP_RICE_FRYABLES) && !this.mantisShrimp.isSitting() && super.m_8036_();
   }

   public boolean m_8045_() {
      return this.mantisShrimp.m_21205_().m_204117_(AMTagRegistry.SHRIMP_RICE_FRYABLES) && !this.mantisShrimp.isSitting() && super.m_8045_();
   }

   public double m_8052_() {
      return 3.9F;
   }

   protected boolean m_6465_(LevelReader worldIn, BlockPos pos) {
      if (!worldIn.m_46859_(pos.m_7494_())) {
         return false;
      } else {
         BlockState blockstate = worldIn.m_8055_(pos);
         if (blockstate.m_60734_() instanceof AbstractFurnaceBlock) {
            this.wasLitPrior = (Boolean)blockstate.m_61143_(AbstractFurnaceBlock.f_48684_);
            return true;
         } else {
            return blockstate.m_204336_(BlockTags.f_13087_);
         }
      }
   }
}
