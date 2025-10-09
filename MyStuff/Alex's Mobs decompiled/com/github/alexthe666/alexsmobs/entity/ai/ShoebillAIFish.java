package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.Vec3;

public class ShoebillAIFish extends Goal {
   private final EntityShoebill bird;
   private BlockPos waterPos = null;
   private BlockPos targetPos = null;
   private int executionChance = 0;
   private final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private int idleTime = 0;
   private int navigateTime = 0;

   public ShoebillAIFish(EntityShoebill bird) {
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.bird = bird;
   }

   public void m_8041_() {
      this.targetPos = null;
      this.waterPos = null;
      this.idleTime = 0;
      this.navigateTime = 0;
      this.bird.m_21573_().m_26573_();
   }

   public void m_8037_() {
      if (this.targetPos != null && this.waterPos != null) {
         double dist = this.bird.m_20238_(Vec3.m_82512_(this.waterPos));
         if (dist <= 1.0) {
            this.navigateTime = 0;
            double d0 = this.waterPos.m_123341_() + 0.5 - this.bird.m_20185_();
            double d2 = this.waterPos.m_123343_() + 0.5 - this.bird.m_20189_();
            float yaw = (float)(Mth.m_14136_(d2, d0) * 180.0F / (float)Math.PI) - 90.0F;
            this.bird.m_146922_(yaw);
            this.bird.f_20885_ = yaw;
            this.bird.f_20883_ = yaw;
            this.bird.m_21573_().m_26573_();
            this.idleTime++;
            if (this.idleTime > 25) {
               this.bird.setAnimation(EntityShoebill.ANIMATION_FISH);
            }

            if (this.idleTime > 45 && this.bird.getAnimation() == EntityShoebill.ANIMATION_FISH) {
               this.bird.m_146850_(GameEvent.f_223698_);
               this.bird.m_5496_(SoundEvents.f_11917_, 0.7F, 0.5F + this.bird.m_217043_().m_188501_());
               this.bird.resetFishingCooldown();
               this.spawnFishingLoot();
               this.m_8041_();
            }
         } else {
            this.navigateTime++;
            this.bird.m_21573_().m_26519_(this.waterPos.m_123341_(), this.waterPos.m_123342_(), this.waterPos.m_123343_(), 1.2);
         }

         if (this.navigateTime > 3600) {
            this.m_8041_();
         }
      }
   }

   public boolean m_8045_() {
      return this.targetPos != null && this.bird.fishingCooldown == 0 && this.bird.revengeCooldown == 0 && !this.bird.isFlying();
   }

   public void spawnFishingLoot() {
      double luck = 0.0 + this.bird.luckLevel * 0.5F;
      Builder lootcontext$builder = new Builder((ServerLevel)this.bird.m_9236_());
      lootcontext$builder.m_287239_((float)luck);
      net.minecraft.world.level.storage.loot.parameters.LootContextParamSet.Builder lootparameterset$builder = new net.minecraft.world.level.storage.loot.parameters.LootContextParamSet.Builder();
      LootTable loottable = this.bird.m_9236_().m_7654_().m_278653_().m_278676_(BuiltInLootTables.f_78720_);

      for (ItemStack itemstack : loottable.m_287195_(lootcontext$builder.m_287235_(lootparameterset$builder.m_81405_()))) {
         ItemEntity item = new ItemEntity(this.bird.m_9236_(), this.bird.m_20185_() + 0.5, this.bird.m_20186_(), this.bird.m_20189_(), itemstack);
         if (!this.bird.m_9236_().f_46443_) {
            this.bird.m_9236_().m_7967_(item);
         }
      }
   }

   public boolean m_8036_() {
      if (!this.bird.isFlying() && this.bird.fishingCooldown == 0 && this.bird.m_217043_().m_188503_(30) == 0) {
         if (this.bird.m_20069_()) {
            this.waterPos = this.bird.m_20183_();
            this.targetPos = this.waterPos;
            return true;
         }

         this.waterPos = this.generateTarget();
         if (this.waterPos != null) {
            this.targetPos = this.getLandPos(this.waterPos);
            return this.targetPos != null;
         }
      }

      return false;
   }

   public BlockPos generateTarget() {
      BlockPos blockpos = null;
      RandomSource random = this.bird.m_217043_();
      int range = 32;

      for (int i = 0; i < 15; i++) {
         BlockPos blockpos1 = this.bird.m_20183_().m_7918_(random.m_188503_(range) - range / 2, 3, random.m_188503_(range) - range / 2);

         while (this.bird.m_9236_().m_46859_(blockpos1) && blockpos1.m_123342_() > 1) {
            blockpos1 = blockpos1.m_7495_();
         }

         if (this.isConnectedToLand(blockpos1)) {
            blockpos = blockpos1;
         }
      }

      return blockpos;
   }

   public boolean isConnectedToLand(BlockPos pos) {
      if (this.bird.m_9236_().m_6425_(pos).m_205070_(FluidTags.f_13131_)) {
         for (Direction dir : this.HORIZONTALS) {
            BlockPos offsetPos = pos.m_121945_(dir);
            if (this.bird.m_9236_().m_6425_(offsetPos).m_76178_() && this.bird.m_9236_().m_6425_(offsetPos.m_7494_()).m_76178_()) {
               return true;
            }
         }
      }

      return false;
   }

   public BlockPos getLandPos(BlockPos pos) {
      if (this.bird.m_9236_().m_6425_(pos).m_205070_(FluidTags.f_13131_)) {
         for (Direction dir : this.HORIZONTALS) {
            BlockPos offsetPos = pos.m_121945_(dir);
            if (this.bird.m_9236_().m_6425_(offsetPos).m_76178_() && this.bird.m_9236_().m_6425_(offsetPos.m_7494_()).m_76178_()) {
               return offsetPos;
            }
         }
      }

      return null;
   }
}
