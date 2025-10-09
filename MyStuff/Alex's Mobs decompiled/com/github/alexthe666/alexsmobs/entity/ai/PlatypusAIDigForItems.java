package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class PlatypusAIDigForItems extends Goal {
   public static final ResourceLocation PLATYPUS_REWARD = new ResourceLocation("alexsmobs", "gameplay/platypus_reward");
   public static final ResourceLocation PLATYPUS_REWARD_CHARGED = new ResourceLocation("alexsmobs", "gameplay/platypus_supercharged_reward");
   private EntityPlatypus platypus;
   private BlockPos digPos;
   private int generatePosCooldown = 0;
   private int digTime = 0;
   private int maxDroppedItems = 3;

   public PlatypusAIDigForItems(EntityPlatypus platypus) {
      this.platypus = platypus;
   }

   private static List<ItemStack> getItemStacks(EntityPlatypus platypus) {
      LootTable loottable = platypus.m_9236_().m_7654_().m_278653_().m_278676_(platypus.superCharged ? PLATYPUS_REWARD_CHARGED : PLATYPUS_REWARD);
      return loottable.m_287195_(
         new Builder((ServerLevel)platypus.m_9236_()).m_287286_(LootContextParams.f_81455_, platypus).m_287235_(LootContextParamSets.f_81417_)
      );
   }

   public boolean m_8036_() {
      if (!this.platypus.isSensing()) {
         return false;
      } else if (this.generatePosCooldown == 0) {
         this.generatePosCooldown = 20 + this.platypus.m_217043_().m_188503_(20);
         this.digPos = this.genDigPos();
         this.maxDroppedItems = 2 + this.platypus.m_217043_().m_188503_(5);
         return this.digPos != null;
      } else {
         this.generatePosCooldown--;
         return false;
      }
   }

   public boolean m_8045_() {
      return this.platypus.m_5448_() == null
         && this.platypus.isSensing()
         && this.platypus.m_21188_() == null
         && this.digPos != null
         && this.platypus.m_9236_().m_8055_(this.digPos).m_204336_(AMTagRegistry.PLATYPUS_DIGABLES)
         && this.platypus.m_9236_().m_6425_(this.digPos.m_7494_()).m_205070_(FluidTags.f_13131_);
   }

   public void m_8037_() {
      double dist = this.platypus.m_20238_(Vec3.m_82512_(this.digPos.m_7494_()));
      double d0 = this.digPos.m_123341_() + 0.5 - this.platypus.m_20185_();
      double d1 = this.digPos.m_123342_() + 0.5 - this.platypus.m_20188_();
      double d2 = this.digPos.m_123343_() + 0.5 - this.platypus.m_20189_();
      float f = (float)(Mth.m_14136_(d2, d0) * 180.0F / (float)Math.PI) - 90.0F;
      if (dist < 2.0) {
         this.platypus.m_20256_(this.platypus.m_20184_().m_82520_(0.0, -0.01F, 0.0));
         this.platypus.m_21573_().m_26573_();
         this.digTime++;
         if (this.digTime % 5 == 0) {
            SoundEvent sound = this.platypus.m_9236_().m_8055_(this.digPos).m_60827_().m_56778_();
            this.platypus.m_146850_(GameEvent.f_223702_);
            this.platypus.m_5496_(sound, 1.0F, 0.5F + this.platypus.m_217043_().m_188501_() * 0.5F);
         }

         int itemDivis = (int)Math.floor(100.0F / this.maxDroppedItems);
         if (this.digTime % itemDivis == 0) {
            List<ItemStack> lootList = getItemStacks(this.platypus);
            if (lootList.size() > 0) {
               for (ItemStack stack : lootList) {
                  ItemEntity e = this.platypus.m_19983_(stack.m_41777_());
                  e.f_19812_ = true;
                  e.m_20256_(e.m_20184_().m_82542_(0.2, 0.2, 0.2));
               }
            }
         }

         if (this.digTime >= 100) {
            this.platypus.setSensing(false);
            this.platypus.setDigging(false);
            this.digTime = 0;
         } else {
            this.platypus.setDigging(true);
         }
      } else {
         this.platypus.setDigging(false);
         this.platypus.m_21573_().m_26519_(this.digPos.m_123341_(), this.digPos.m_123342_() + 1, this.digPos.m_123343_(), 1.0);
         this.platypus.m_146922_(f);
      }
   }

   public void m_8041_() {
      this.generatePosCooldown = 0;
      this.platypus.setSensing(false);
      this.platypus.setDigging(false);
      this.digPos = null;
      this.digTime = 0;
   }

   private BlockPos genSeafloorPos(BlockPos parent) {
      LevelAccessor world = this.platypus.m_9236_();
      RandomSource random = this.platypus.m_217043_();
      int range = 15;

      for (int i = 0; i < 15; i++) {
         BlockPos seafloor = parent.m_7918_(random.m_188503_(range) - range / 2, 0, random.m_188503_(range) - range / 2);

         while (world.m_6425_(seafloor).m_205070_(FluidTags.f_13131_) && seafloor.m_123342_() > 1) {
            seafloor = seafloor.m_7495_();
         }

         BlockState state = world.m_8055_(seafloor);
         if (state.m_204336_(AMTagRegistry.PLATYPUS_DIGABLES)) {
            return seafloor;
         }
      }

      return null;
   }

   private BlockPos genDigPos() {
      RandomSource random = this.platypus.m_217043_();
      int range = 15;
      if (this.platypus.m_20069_()) {
         return this.genSeafloorPos(this.platypus.m_20183_());
      } else {
         for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.platypus.m_20183_().m_7918_(random.m_188503_(range) - range / 2, 3, random.m_188503_(range) - range / 2);

            while (this.platypus.m_9236_().m_46859_(blockpos1) && blockpos1.m_123342_() > 1) {
               blockpos1 = blockpos1.m_7495_();
            }

            if (this.platypus.m_9236_().m_6425_(blockpos1).m_205070_(FluidTags.f_13131_)) {
               BlockPos pos3 = this.genSeafloorPos(blockpos1);
               if (pos3 != null) {
                  return pos3;
               }
            }
         }

         return null;
      }
   }
}
