package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FeatureLeafcutterAnthill extends Feature<NoneFeatureConfiguration> {
   public FeatureLeafcutterAnthill(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean m_142674_(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      if (context.m_159774_().m_213780_().m_188501_() > 0.0175F) {
         return false;
      } else {
         int x = 8;
         int z = 8;
         BlockPos pos = context.m_159777_();
         int y = context.m_159774_().m_6924_(Types.WORLD_SURFACE_WG, pos.m_123341_() + x, pos.m_123343_() + z);
         BlockPos heightPos = new BlockPos(pos.m_123341_() + x, y, pos.m_123343_() + z);
         if (!context.m_159774_().m_6425_(heightPos.m_7495_()).m_76178_()) {
            return false;
         } else {
            int outOfGround = 2 + context.m_159774_().m_213780_().m_188503_(2);

            for (int i = 0; i < outOfGround; i++) {
               float size = outOfGround - i;
               int lvt_8_1_ = (int)(Math.floor(size) * context.m_159774_().m_213780_().m_188501_()) + 2;
               int lvt_10_1_ = (int)(Math.floor(size) * context.m_159774_().m_213780_().m_188501_()) + 2;
               float radius = (lvt_8_1_ + lvt_10_1_) * 0.333F;

               for (BlockPos lvt_13_1_ : BlockPos.m_121940_(heightPos.m_7918_(-lvt_8_1_, 0, -lvt_10_1_), heightPos.m_7918_(lvt_8_1_, 3, lvt_10_1_))) {
                  if (lvt_13_1_.m_123331_(heightPos) <= radius * radius) {
                     BlockState block = Blocks.f_50546_.m_49966_();
                     if (context.m_159774_().m_213780_().m_188501_() < 0.2F) {
                        block = Blocks.f_50493_.m_49966_();
                     }

                     context.m_159774_().m_7731_(lvt_13_1_, block, 4);
                  }
               }
            }

            Random chunkSeedRandom = new Random(pos.m_121878_());
            outOfGround -= chunkSeedRandom.nextInt(1) + 1;
            heightPos = heightPos.m_7918_(-chunkSeedRandom.nextInt(2), 0, -chunkSeedRandom.nextInt(2));
            if (context.m_159774_().m_8055_(heightPos.m_6630_(outOfGround + 1)).m_60734_() != AMBlockRegistry.LEAFCUTTER_ANTHILL.get()
               && context.m_159774_().m_8055_(heightPos.m_6630_(outOfGround - 1)).m_60734_() != AMBlockRegistry.LEAFCUTTER_ANTHILL.get()) {
               context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround), ((Block)AMBlockRegistry.LEAFCUTTER_ANTHILL.get()).m_49966_(), 4);
               if (context.m_159774_().m_7702_(heightPos.m_6630_(outOfGround)) instanceof TileEntityLeafcutterAnthill beehivetileentity) {
                  int j = 3 + chunkSeedRandom.nextInt(3);
                  if (beehivetileentity.hasNoAnts()) {
                     for (int k = 0; k < j; k++) {
                        EntityLeafcutterAnt beeentity = new EntityLeafcutterAnt(
                           (EntityType)AMEntityRegistry.LEAFCUTTER_ANT.get(), context.m_159774_().m_6018_()
                        );
                        beeentity.setQueen(k == 0);
                        beehivetileentity.tryEnterHive(beeentity, false, context.m_159774_().m_213780_().m_188503_(599));
                     }
                  }
               }

               if (context.m_159774_().m_213780_().m_188499_()) {
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround).m_122012_(), Blocks.f_50546_.m_49966_(), 4);
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround - 1).m_122012_(), Blocks.f_50546_.m_49966_(), 4);
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround - 2).m_122012_(), Blocks.f_50546_.m_49966_(), 4);
               }

               if (context.m_159774_().m_213780_().m_188499_()) {
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround).m_122029_(), Blocks.f_50546_.m_49966_(), 4);
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround - 1).m_122029_(), Blocks.f_50546_.m_49966_(), 4);
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround - 2).m_122029_(), Blocks.f_50546_.m_49966_(), 4);
               }

               if (context.m_159774_().m_213780_().m_188499_()) {
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround).m_122019_(), Blocks.f_50546_.m_49966_(), 4);
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround - 1).m_122019_(), Blocks.f_50546_.m_49966_(), 4);
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround - 2).m_122019_(), Blocks.f_50546_.m_49966_(), 4);
               }

               if (context.m_159774_().m_213780_().m_188499_()) {
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround).m_122024_(), Blocks.f_50546_.m_49966_(), 4);
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround - 1).m_122024_(), Blocks.f_50546_.m_49966_(), 4);
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround - 2).m_122024_(), Blocks.f_50546_.m_49966_(), 4);
               }

               for (int airs = 1; airs < 3; airs++) {
                  context.m_159774_().m_7731_(heightPos.m_6630_(outOfGround + airs), Blocks.f_50016_.m_49966_(), 4);
               }
            }

            int i = outOfGround;
            int down = context.m_159774_().m_213780_().m_188503_(2) + 1;

            while (i > -down) {
               context.m_159774_().m_7731_(heightPos.m_6630_(--i), ((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()).m_49966_(), 4);
            }

            float size = chunkSeedRandom.nextInt(1) + 1;
            int lvt_8_1_ = (int)(Math.floor(size) * context.m_159774_().m_213780_().m_188501_()) + 1;
            int lvt_9_1_ = (int)(Math.floor(size) * context.m_159774_().m_213780_().m_188501_()) + 1;
            int lvt_10_1_ = (int)(Math.floor(size) * context.m_159774_().m_213780_().m_188501_()) + 1;
            float radius = (lvt_8_1_ + lvt_9_1_ + lvt_10_1_) * 0.333F + 0.5F;
            heightPos = heightPos.m_6625_(down + lvt_9_1_).m_7918_(chunkSeedRandom.nextInt(2), 0, chunkSeedRandom.nextInt(2));

            for (BlockPos lvt_13_1_x : BlockPos.m_121940_(heightPos.m_7918_(-lvt_8_1_, -lvt_9_1_, -lvt_10_1_), heightPos.m_7918_(lvt_8_1_, lvt_9_1_, lvt_10_1_))) {
               if (lvt_13_1_x.m_123331_(heightPos) < radius * radius) {
                  BlockState block = ((Block)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()).m_49966_();
                  context.m_159774_().m_7731_(lvt_13_1_x, block, 4);
               }
            }

            return true;
         }
      }
   }
}
