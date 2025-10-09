package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAnthill;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.List;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class AnteaterAIRaidNest extends MoveToBlockGoal {
   public static final ResourceLocation ANTEATER_REWARD = new ResourceLocation("alexsmobs", "gameplay/anteater_reward");
   private final EntityAnteater anteater;
   private int idleAtHiveTime = 0;
   private boolean isAboveDestinationAnteater;
   private boolean shootTongue;
   private int maxEatingTime = 0;

   public AnteaterAIRaidNest(EntityAnteater anteater) {
      super(anteater, 1.0, 32, 8);
      this.anteater = anteater;
   }

   private static List<ItemStack> getItemStacks(EntityAnteater anteater) {
      LootTable loottable = anteater.m_9236_().m_7654_().m_278653_().m_278676_(ANTEATER_REWARD);
      return loottable.m_287195_(
         new Builder((ServerLevel)anteater.m_9236_()).m_287286_(LootContextParams.f_81455_, anteater).m_287235_(LootContextParamSets.f_81417_)
      );
   }

   private void dropDigItems() {
      List<ItemStack> lootList = getItemStacks(this.anteater);
      if (lootList.size() > 0) {
         for (ItemStack stack : lootList) {
            ItemEntity e = this.anteater.m_19983_(stack.m_41777_());
            e.f_19812_ = true;
            e.m_20256_(e.m_20184_().m_82542_(0.2, 0.2, 0.2));
         }
      }
   }

   public boolean m_8036_() {
      return !this.anteater.m_6162_() && super.m_8036_() && this.anteater.eatAntCooldown <= 0;
   }

   public boolean m_8045_() {
      return super.m_8045_() && this.anteater.eatAntCooldown <= 0;
   }

   public void m_8056_() {
      super.m_8056_();
      this.maxEatingTime = 150 + this.anteater.m_217043_().m_188503_(200);
   }

   public void m_8041_() {
      super.m_8041_();
      this.idleAtHiveTime = 0;
      this.maxEatingTime = 150 + this.anteater.m_217043_().m_188503_(200);
      this.anteater.setLeaning(false);
      this.anteater.resetAntCooldown();
   }

   public double m_8052_() {
      return 1.2;
   }

   public void m_8037_() {
      super.m_8037_();
      BlockPos blockpos = this.m_6669_();
      if (!this.isWithinXZDist(blockpos, this.f_25598_.m_20182_(), this.m_8052_())) {
         this.isAboveDestinationAnteater = false;
         this.f_25601_++;
         if (this.m_8064_()) {
            this.f_25598_.m_21573_().m_26519_(blockpos.m_123341_() + 0.5, blockpos.m_123342_(), blockpos.m_123343_() + 0.5, this.f_25599_);
         }
      } else {
         this.isAboveDestinationAnteater = true;
         this.f_25601_--;
      }

      if (this.m_25625_()) {
         this.anteater.m_7618_(Anchor.EYES, new Vec3(this.f_25602_.m_123341_() + 0.5, this.f_25602_.m_123342_() - 1, this.f_25602_.m_123343_() + 0.5));
         if (this.idleAtHiveTime >= 20 && this.idleAtHiveTime % 20 == 0) {
            this.shootTongue = this.anteater.m_217043_().m_188503_(2) == 0;
            if (this.shootTongue) {
               this.eatHive();
            } else {
               this.breakHiveEffect();
            }
         }

         this.idleAtHiveTime++;
         if (this.shootTongue && this.anteater.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.anteater.setLeaning(false);
            this.anteater.setAnimation(EntityAnteater.ANIMATION_TOUNGE_IDLE);
         } else if (this.anteater.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.anteater.setLeaning(true);
            this.anteater.setAnimation(this.anteater.m_217043_().m_188499_() ? EntityAnteater.ANIMATION_SLASH_L : EntityAnteater.ANIMATION_SLASH_R);
         }

         if (this.idleAtHiveTime > this.maxEatingTime) {
            this.m_8041_();
         }
      }
   }

   private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
      return blockpos.m_123331_(AMBlockPos.fromCoords(positionVec.m_7096_(), blockpos.m_123342_(), positionVec.m_7094_())) < distance * distance;
   }

   protected boolean m_25625_() {
      return this.isAboveDestinationAnteater;
   }

   private void breakHiveEffect() {
      if (ForgeEventFactory.getMobGriefingEvent(this.anteater.m_9236_(), this.anteater)) {
         BlockState blockstate = this.anteater.m_9236_().m_8055_(this.f_25602_);
         if (blockstate.m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANTHILL.get())) {
            if (this.anteater.m_9236_().m_7702_(this.f_25602_) instanceof TileEntityLeafcutterAnthill) {
               TileEntityLeafcutterAnthill anthill = (TileEntityLeafcutterAnthill)this.anteater.m_9236_().m_7702_(this.f_25602_);
               anthill.angerAntsBecauseAnteater(this.anteater, blockstate, BeeReleaseStatus.EMERGENCY);
               this.anteater.m_9236_().m_46961_(this.f_25602_, false);
               if (blockstate.m_60734_() instanceof BlockLeafcutterAnthill) {
                  this.anteater.m_9236_().m_46597_(this.f_25602_, blockstate);
               }

               this.dropDigItems();
            }
         } else if (blockstate.m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())) {
            this.anteater.m_9236_().m_46961_(this.f_25602_, false);
            this.anteater.m_9236_().m_46597_(this.f_25602_, blockstate);
         }
      }
   }

   private void eatHive() {
      if (ForgeEventFactory.getMobGriefingEvent(this.anteater.m_9236_(), this.anteater)) {
         BlockState blockstate = this.anteater.m_9236_().m_8055_(this.f_25602_);
         if (blockstate.m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANTHILL.get())) {
            if (this.anteater.m_9236_().m_7702_(this.f_25602_) instanceof TileEntityLeafcutterAnthill) {
               RandomSource rand = this.anteater.m_217043_();
               TileEntityLeafcutterAnthill anthill = (TileEntityLeafcutterAnthill)this.anteater.m_9236_().m_7702_(this.f_25602_);
               anthill.angerAntsBecauseAnteater(this.anteater, blockstate, BeeReleaseStatus.EMERGENCY);
               this.anteater.m_9236_().m_46717_(this.f_25602_, blockstate.m_60734_());
               if (!anthill.hasNoAnts()) {
                  BlockState state = anthill.shrinkFungus();
                  if (state != null
                     && state.m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())
                     && (Integer)state.m_61143_(BlockLeafcutterAntChamber.FUNGUS) >= 5) {
                     ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.GONGYLIDIA.get());
                     ItemEntity itementity = new ItemEntity(
                        this.anteater.m_9236_(),
                        this.f_25602_.m_123341_() + rand.m_188501_(),
                        this.f_25602_.m_123342_() + rand.m_188501_(),
                        this.f_25602_.m_123343_() + rand.m_188501_(),
                        stack
                     );
                     itementity.m_32060_();
                     this.anteater.m_9236_().m_7967_(itementity);
                  }

                  this.anteater.setAntOnTongue(true);
               }
            }
         } else if (blockstate.m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())) {
            this.anteater.m_9236_().m_46961_(this.f_25602_, false);
            if ((Integer)blockstate.m_61143_(BlockLeafcutterAntChamber.FUNGUS) >= 5) {
               RandomSource rand = this.anteater.m_217043_();
               ItemStack stack = new ItemStack((ItemLike)AMItemRegistry.GONGYLIDIA.get());
               ItemEntity itementity = new ItemEntity(
                  this.anteater.m_9236_(),
                  this.f_25602_.m_123341_() + rand.m_188501_(),
                  this.f_25602_.m_123342_() + rand.m_188501_(),
                  this.f_25602_.m_123343_() + rand.m_188501_(),
                  stack
               );
               itementity.m_32060_();
               this.anteater.m_9236_().m_7967_(itementity);
            }

            this.anteater.m_9236_().m_46597_(this.f_25602_, Blocks.f_50546_.m_49966_());
            this.anteater.setAntOnTongue(true);
         }

         double d0 = 15.0;

         for (EntityLeafcutterAnt leafcutter : this.anteater
            .m_9236_()
            .m_45976_(
               EntityLeafcutterAnt.class,
               new AABB(
                  this.f_25602_.m_123341_() - d0,
                  this.f_25602_.m_123342_() - d0,
                  this.f_25602_.m_123343_() - d0,
                  this.f_25602_.m_123341_() + d0,
                  this.f_25602_.m_123342_() + d0,
                  this.f_25602_.m_123343_() + d0
               )
            )) {
            leafcutter.m_7870_(100);
            leafcutter.m_6710_(this.anteater);
            leafcutter.setStayOutOfHiveCountdown(400);
         }
      }
   }

   protected boolean m_6465_(LevelReader worldIn, BlockPos pos) {
      return worldIn.m_8055_(pos).m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())
         || worldIn.m_8055_(pos).m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANTHILL.get())
            && worldIn.m_7702_(pos) instanceof TileEntityLeafcutterAnthill
            && this.isValidAnthill(pos, (TileEntityLeafcutterAnthill)worldIn.m_7702_(pos));
   }

   private boolean isValidAnthill(BlockPos pos, TileEntityLeafcutterAnthill blockEntity) {
      return blockEntity.hasAtleastThisManyAnts(2);
   }
}
