package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TileEntityLeafcutterAnthill extends BlockEntity {
   private static final Direction[] DIRECTIONS_UP = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private final List<TileEntityLeafcutterAnthill.Ant> ants = Lists.newArrayList();
   private int leafFeedings = 0;

   public TileEntityLeafcutterAnthill(BlockPos pos, BlockState state) {
      super((BlockEntityType)AMTileEntityRegistry.LEAFCUTTER_ANTHILL.get(), pos, state);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityLeafcutterAnthill entity) {
      entity.tickAnts();
   }

   @Nullable
   public static Entity loadEntityAndExecute(CompoundTag compound, Level worldIn, Function<Entity, Entity> p_220335_2_) {
      return loadEntity(compound, worldIn).map(p_220335_2_).map(p_220346_3_ -> {
         if (compound.m_128425_("Passengers", 9)) {
            ListTag listnbt = compound.m_128437_("Passengers", 10);

            for (int i = 0; i < listnbt.size(); i++) {
               Entity entity = loadEntityAndExecute(listnbt.m_128728_(i), worldIn, p_220335_2_);
               if (entity != null) {
                  entity.m_7998_(p_220346_3_, true);
               }
            }
         }

         return (Entity)p_220346_3_;
      }).orElse(null);
   }

   private static Optional<Entity> loadEntity(CompoundTag compound, Level worldIn) {
      try {
         return loadEntityUnchecked(compound, worldIn);
      } catch (RuntimeException var3) {
         return Optional.empty();
      }
   }

   public static Optional<Entity> loadEntityUnchecked(CompoundTag compound, Level worldIn) {
      EntityLeafcutterAnt leafcutterAnt = (EntityLeafcutterAnt)((EntityType)AMEntityRegistry.LEAFCUTTER_ANT.get()).m_20615_(worldIn);
      leafcutterAnt.m_20258_(compound);
      return Optional.of(leafcutterAnt);
   }

   public boolean hasNoAnts() {
      return this.ants.isEmpty();
   }

   public boolean hasAtleastThisManyAnts(int antCount) {
      return this.ants.size() >= antCount;
   }

   public boolean isFullOfAnts() {
      return this.ants.size() == AMConfig.leafcutterAntColonySize;
   }

   public void angerAnts(@Nullable LivingEntity p_226963_1_, BlockState p_226963_2_, BeeReleaseStatus p_226963_3_) {
      List<Entity> list = this.tryReleaseAnt(p_226963_2_, p_226963_3_);
      if (p_226963_1_ != null) {
         for (Entity entity : list) {
            if (entity instanceof EntityLeafcutterAnt entityLeafcutterAnt) {
               if (p_226963_1_.m_20182_().m_82557_(entity.m_20182_()) <= 16.0) {
                  entityLeafcutterAnt.m_6710_(p_226963_1_);
               }

               entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
            }
         }
      }
   }

   public void angerAntsBecauseAnteater(@Nullable LivingEntity p_226963_1_, BlockState p_226963_2_, BeeReleaseStatus p_226963_3_) {
      List<Entity> list = this.tryReleaseAntAnteater(p_226963_2_, p_226963_3_);
      if (p_226963_1_ != null) {
         for (Entity entity : list) {
            if (entity instanceof EntityLeafcutterAnt entityLeafcutterAnt) {
               if (p_226963_1_.m_20182_().m_82557_(entity.m_20182_()) <= 16.0) {
                  entityLeafcutterAnt.m_6710_(p_226963_1_);
               }

               entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
            }
         }
      }
   }

   private List<Entity> tryReleaseAnt(BlockState p_226965_1_, BeeReleaseStatus p_226965_2_) {
      List<Entity> list = Lists.newArrayList();
      this.ants.removeIf(p_226966_4_ -> this.addAntToWorld(p_226965_1_, p_226966_4_, list, p_226965_2_));
      return list;
   }

   private List<Entity> tryReleaseAntAnteater(BlockState p_226965_1_, BeeReleaseStatus p_226965_2_) {
      List<Entity> list = Lists.newArrayList();
      this.ants.removeIf(ant -> !ant.queen && this.addAntToWorld(p_226965_1_, ant, list, p_226965_2_));
      return list;
   }

   private boolean addAntToWorld(
      BlockState p_235651_1_, TileEntityLeafcutterAnthill.Ant p_235651_2_, @Nullable List<Entity> p_235651_3_, BeeReleaseStatus p_235651_4_
   ) {
      BlockPos blockpos = this.m_58899_();
      CompoundTag compoundnbt = p_235651_2_.entityData;
      compoundnbt.m_128473_("Passengers");
      compoundnbt.m_128473_("Leash");
      compoundnbt.m_128473_("UUID");
      BlockPos blockpos1 = blockpos.m_7494_();
      boolean flag = !this.f_58857_.m_8055_(blockpos1).m_60816_(this.f_58857_, blockpos1).m_83281_();
      if (flag && p_235651_4_ != BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         Entity entity = loadEntityAndExecute(compoundnbt, this.f_58857_, p_226960_0_ -> p_226960_0_);
         if (entity != null) {
            if (entity instanceof EntityLeafcutterAnt entityLeafcutterAnt) {
               entityLeafcutterAnt.setLeaf(false);
               if (p_235651_4_ == BeeReleaseStatus.HONEY_DELIVERED) {
               }

               if (p_235651_3_ != null) {
                  p_235651_3_.add(entityLeafcutterAnt);
               }

               float f = entity.m_20205_();
               double d0 = blockpos.m_123341_() + 0.5;
               double d1 = blockpos.m_123342_() + 1.0;
               double d2 = blockpos.m_123343_() + 0.5;
               entity.m_7678_(d0, d1, d2, entity.m_146908_(), entity.m_146909_());
               if (((EntityLeafcutterAnt)entity).isQueen()) {
                  entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
               }
            }

            this.f_58857_.m_220407_(GameEvent.f_223702_, this.m_58899_(), Context.m_223722_(this.m_58900_()));
            this.f_58857_.m_5594_(null, blockpos, SoundEvents.f_11696_, SoundSource.BLOCKS, 1.0F, 1.0F);
            return this.f_58857_.m_7967_(entity);
         } else {
            return false;
         }
      }
   }

   public void tryEnterHive(EntityLeafcutterAnt p_226962_1_, boolean p_226962_2_, int p_226962_3_) {
      if (this.ants.size() < AMConfig.leafcutterAntColonySize) {
         p_226962_1_.m_8127_();
         p_226962_1_.m_20153_();
         CompoundTag compoundnbt = new CompoundTag();
         p_226962_1_.m_20223_(compoundnbt);
         if (p_226962_2_) {
            if (!this.f_58857_.f_46443_ && p_226962_1_.m_217043_().m_188501_() < AMConfig.leafcutterAntFungusGrowChance) {
               this.growFungus();
            }

            this.leafFeedings++;
            if (this.leafFeedings >= AMConfig.leafcutterAntRepopulateFeedings
               && this.getAntsInAreaCount(32.0) < Mth.m_14167_(AMConfig.leafcutterAntColonySize * 0.5F)
               && this.hasQueen()) {
               this.leafFeedings = 0;
               this.ants.add(new TileEntityLeafcutterAnthill.Ant(new CompoundTag(), 0, 100, false));
            }
         }

         this.ants.add(new TileEntityLeafcutterAnthill.Ant(compoundnbt, p_226962_3_, p_226962_2_ ? 100 : 200, p_226962_1_.isQueen()));
         if (this.f_58857_ != null) {
            BlockPos blockpos = this.m_58899_();
            this.f_58857_.m_220407_(GameEvent.f_223702_, this.m_58899_(), Context.m_223722_(this.m_58900_()));
            this.f_58857_.m_6263_(null, blockpos.m_123341_(), blockpos.m_123342_(), blockpos.m_123343_(), SoundEvents.f_11695_, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         p_226962_1_.m_142687_(RemovalReason.DISCARDED);
      }
   }

   private int getAntsInAreaCount(double size) {
      int ants = this.getAntCount();
      Vec3 vec = Vec3.m_82512_(this.m_58899_());
      AABB box = new AABB(vec.m_82520_(-size, -size, -size), vec.m_82520_(size, size, size));
      return ants + this.f_58857_.m_45976_(EntityLeafcutterAnt.class, box).size();
   }

   public boolean hasQueen() {
      for (TileEntityLeafcutterAnthill.Ant ant : this.ants) {
         if (ant.queen) {
            return true;
         }
      }

      return false;
   }

   public void releaseQueens() {
      this.ants.removeIf(p_226966_4_ -> p_226966_4_.queen && this.addAntToWorld(this.m_58900_(), p_226966_4_, null, BeeReleaseStatus.BEE_RELEASED));
   }

   public void tryEnterHive(EntityLeafcutterAnt p_226961_1_, boolean p_226961_2_) {
      this.tryEnterHive(p_226961_1_, p_226961_2_, 0);
   }

   public int getAntCount() {
      return this.ants.size();
   }

   public void m_6596_() {
      if (this.isNearFire()) {
         this.angerAnts(null, this.f_58857_.m_8055_(this.m_58899_()), BeeReleaseStatus.EMERGENCY);
      }

      super.m_6596_();
   }

   public boolean isNearFire() {
      if (this.f_58857_ == null) {
         return false;
      } else {
         for (BlockPos blockpos : BlockPos.m_121940_(this.f_58858_.m_7918_(-1, -1, -1), this.f_58858_.m_7918_(1, 1, 1))) {
            if (this.f_58857_.m_8055_(blockpos).m_60734_() instanceof FireBlock) {
               return true;
            }
         }

         return false;
      }
   }

   public BlockState shrinkFungus() {
      BlockPos bottomChamber = this.m_58899_().m_7495_();

      while (this.f_58857_.m_8055_(bottomChamber.m_7495_()).m_60734_() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get() && bottomChamber.m_123342_() > 0) {
         bottomChamber = bottomChamber.m_7495_();
      }

      BlockPos chamber = bottomChamber;
      if (!this.isUnfilledChamber(bottomChamber)) {
         BlockState prev = this.f_58857_.m_8055_(bottomChamber);
         if (prev.m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())) {
            int fungalLevel = (Integer)prev.m_61143_(BlockLeafcutterAntChamber.FUNGUS);
            this.f_58857_
               .m_46597_(
                  bottomChamber,
                  (BlockState)((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())
                     .m_49966_()
                     .m_61124_(BlockLeafcutterAntChamber.FUNGUS, Math.min(0, fungalLevel - 1))
               );
            return prev;
         }
      } else {
         boolean flag = false;
         List<BlockPos> possibleChambers = new ArrayList<>();

         while (!flag) {
            for (BlockPos blockpos : BlockPos.m_121940_(chamber.m_7918_(-4, 0, -4), chamber.m_7918_(4, 0, 4))) {
               if (this.isUnfilledChamber(blockpos)) {
                  possibleChambers.add(blockpos.m_7949_());
                  flag = true;
               }
            }

            if (!flag) {
               chamber = chamber.m_7494_();
               if (chamber.m_123342_() > this.f_58858_.m_123342_()) {
                  return null;
               }
            }
         }

         Collections.shuffle(possibleChambers);
         if (!possibleChambers.isEmpty()) {
            BlockPos newChamber = possibleChambers.get(0);
            if (newChamber != null && !this.isUnfilledChamber(newChamber)) {
               BlockState prev = this.f_58857_.m_8055_(newChamber);
               if (prev.m_60713_((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())) {
                  int fungalLevel = (Integer)prev.m_61143_(BlockLeafcutterAntChamber.FUNGUS);
                  this.f_58857_
                     .m_46597_(
                        newChamber,
                        (BlockState)((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())
                           .m_49966_()
                           .m_61124_(BlockLeafcutterAntChamber.FUNGUS, Math.min(fungalLevel - 1, 0))
                     );
                  return prev;
               }
            }
         }
      }

      return null;
   }

   public void growFungus() {
      if (!this.hasNoAnts()) {
         BlockPos bottomChamber = this.m_58899_().m_7495_();

         while (this.f_58857_.m_8055_(bottomChamber.m_7495_()).m_60734_() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get() && bottomChamber.m_123342_() > 0) {
            bottomChamber = bottomChamber.m_7495_();
         }

         BlockPos chamber = bottomChamber;
         if (this.isUnfilledChamber(bottomChamber)) {
            int fungalLevel = (Integer)this.f_58857_.m_8055_(bottomChamber).m_61143_(BlockLeafcutterAntChamber.FUNGUS);
            int fungalLevel2 = Mth.m_14045_(fungalLevel + 1 + this.f_58857_.m_213780_().m_188503_(1), 0, 5);
            this.f_58857_
               .m_46597_(
                  bottomChamber,
                  (BlockState)((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()).m_49966_().m_61124_(BlockLeafcutterAntChamber.FUNGUS, fungalLevel2)
               );
         } else {
            boolean flag = false;
            List<BlockPos> possibleChambers = new ArrayList<>();

            while (!flag) {
               for (BlockPos blockpos : BlockPos.m_121940_(chamber.m_7918_(-4, 0, -4), chamber.m_7918_(4, 0, 4))) {
                  if (this.isUnfilledChamber(blockpos)) {
                     possibleChambers.add(blockpos.m_7949_());
                     flag = true;
                  }
               }

               if (!flag) {
                  chamber = chamber.m_7494_();
                  if (chamber.m_123342_() > this.f_58858_.m_123342_()) {
                     return;
                  }
               }
            }

            Collections.shuffle(possibleChambers);
            if (!possibleChambers.isEmpty()) {
               BlockPos newChamber = possibleChambers.get(0);
               if (newChamber != null && this.isUnfilledChamber(newChamber)) {
                  int fungalLevel = (Integer)this.f_58857_.m_8055_(newChamber).m_61143_(BlockLeafcutterAntChamber.FUNGUS);
                  int fungalLevel2 = Mth.m_14045_(fungalLevel + 1 + this.f_58857_.m_213780_().m_188503_(1), 0, 5);
                  this.f_58857_
                     .m_46597_(
                        newChamber,
                        (BlockState)((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()).m_49966_().m_61124_(BlockLeafcutterAntChamber.FUNGUS, fungalLevel2)
                     );
               }
            }
         }
      }
   }

   private boolean isUnfilledChamber(BlockPos pos) {
      return this.f_58857_.m_8055_(pos).m_60734_() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()
         && (Integer)this.f_58857_.m_8055_(pos).m_61143_(BlockLeafcutterAntChamber.FUNGUS) < 5;
   }

   private void tickAnts() {
      Iterator<TileEntityLeafcutterAnthill.Ant> iterator = this.ants.iterator();
      BlockState blockstate = this.m_58900_();

      while (iterator.hasNext()) {
         TileEntityLeafcutterAnthill.Ant ant = iterator.next();
         if (ant.ticksInHive > ant.minOccupationTicks && !ant.queen) {
            BeeReleaseStatus beehivetileentity$state = ant.entityData.m_128471_("HasNectar") ? BeeReleaseStatus.HONEY_DELIVERED : BeeReleaseStatus.BEE_RELEASED;
            if (this.addAntToWorld(blockstate, ant, null, beehivetileentity$state)) {
               iterator.remove();
            }
         }

         ant.ticksInHive++;
      }
   }

   public void m_142466_(CompoundTag nbt) {
      super.m_142466_(nbt);
      this.ants.clear();
      this.leafFeedings = nbt.m_128451_("LeafFeedings");
      ListTag listnbt = nbt.m_128437_("Ants", 10);

      for (int i = 0; i < listnbt.size(); i++) {
         CompoundTag compoundnbt = listnbt.m_128728_(i);
         TileEntityLeafcutterAnthill.Ant beehiveTileEntity$ant = new TileEntityLeafcutterAnthill.Ant(
            compoundnbt.m_128469_("EntityData"),
            compoundnbt.m_128451_("TicksInHive"),
            compoundnbt.m_128451_("MinOccupationTicks"),
            compoundnbt.m_128471_("Queen")
         );
         this.ants.add(beehiveTileEntity$ant);
      }
   }

   public ListTag getAnts() {
      ListTag listnbt = new ListTag();

      for (TileEntityLeafcutterAnthill.Ant beehiveTileEntity$ant : this.ants) {
         beehiveTileEntity$ant.entityData.m_128473_("UUID");
         CompoundTag compoundnbt = new CompoundTag();
         compoundnbt.m_128365_("EntityData", beehiveTileEntity$ant.entityData);
         compoundnbt.m_128405_("TicksInHive", beehiveTileEntity$ant.ticksInHive);
         compoundnbt.m_128405_("MinOccupationTicks", beehiveTileEntity$ant.minOccupationTicks);
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   public void m_183515_(CompoundTag compound) {
      super.m_183515_(compound);
      compound.m_128365_("Ants", this.getAnts());
      compound.m_128405_("LeafFeedings", this.leafFeedings);
   }

   static class Ant {
      private final CompoundTag entityData;
      private final int minOccupationTicks;
      private int ticksInHive;
      private final boolean queen;

      private Ant(CompoundTag p_i225767_1_, int p_i225767_2_, int p_i225767_3_, boolean queen) {
         p_i225767_1_.m_128473_("UUID");
         this.entityData = p_i225767_1_;
         this.ticksInHive = p_i225767_2_;
         this.minOccupationTicks = p_i225767_3_;
         this.queen = queen;
      }
   }
}
