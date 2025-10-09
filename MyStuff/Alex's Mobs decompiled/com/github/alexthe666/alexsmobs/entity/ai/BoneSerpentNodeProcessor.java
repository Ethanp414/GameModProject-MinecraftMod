package com.github.alexthe666.alexsmobs.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.Target;

public class BoneSerpentNodeProcessor extends NodeEvaluator {
   public Node m_7171_() {
      return super.m_5676_(
         Mth.m_14107_(this.f_77313_.m_20191_().f_82288_),
         Mth.m_14107_(this.f_77313_.m_20191_().f_82289_ + 0.5),
         Mth.m_14107_(this.f_77313_.m_20191_().f_82290_)
      );
   }

   public Target m_7568_(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new Target(
         super.m_5676_(
            Mth.m_14107_(p_224768_1_ - this.f_77313_.m_20205_() / 2.0F),
            Mth.m_14107_(p_224768_3_ + 0.5),
            Mth.m_14107_(p_224768_5_ - this.f_77313_.m_20205_() / 2.0F)
         )
      );
   }

   public int m_6065_(Node[] p_222859_1_, Node p_222859_2_) {
      int i = 0;

      for (Direction direction : Direction.values()) {
         Node pathpoint = this.getWaterNode(
            p_222859_2_.f_77271_ + direction.m_122429_(), p_222859_2_.f_77272_ + direction.m_122430_(), p_222859_2_.f_77273_ + direction.m_122431_()
         );
         if (pathpoint != null && !pathpoint.f_77279_) {
            p_222859_1_[i++] = pathpoint;
         }
      }

      return i;
   }

   public BlockPathTypes m_7209_(BlockGetter blockaccessIn, int x, int y, int z, Mob entitylivingIn) {
      return this.m_8086_(blockaccessIn, x, y, z);
   }

   public BlockPathTypes m_8086_(BlockGetter blockaccessIn, int x, int y, int z) {
      BlockPos blockpos = new BlockPos(x, y, z);
      FluidState fluidstate = blockaccessIn.m_6425_(blockpos);
      BlockState blockstate = blockaccessIn.m_8055_(blockpos);
      if (fluidstate.m_76178_() && blockstate.m_60647_(blockaccessIn, blockpos.m_7495_(), PathComputationType.WATER) && blockstate.m_60795_()) {
         return BlockPathTypes.BREACH;
      } else {
         return !fluidstate.m_205070_(FluidTags.f_13132_)
               && (!fluidstate.m_205070_(FluidTags.f_13131_) || !blockstate.m_60647_(blockaccessIn, blockpos, PathComputationType.WATER))
            ? BlockPathTypes.BLOCKED
            : BlockPathTypes.WATER;
      }
   }

   @Nullable
   private Node getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
      BlockPathTypes pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
      return pathnodetype != BlockPathTypes.BREACH && pathnodetype != BlockPathTypes.WATER && pathnodetype != BlockPathTypes.LAVA
         ? null
         : this.m_5676_(p_186328_1_, p_186328_2_, p_186328_3_);
   }

   @Nullable
   protected Node m_5676_(int x, int y, int z) {
      Node pathpoint = null;
      BlockPathTypes pathnodetype = this.m_8086_(this.f_77313_.m_9236_(), x, y, z);
      float f = this.f_77313_.m_21439_(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.m_5676_(x, y, z);
         pathpoint.f_77282_ = pathnodetype;
         pathpoint.f_77281_ = Math.max(pathpoint.f_77281_, f);
         if (this.f_77312_.m_6425_(new BlockPos(x, y, z)).m_76178_()) {
            pathpoint.f_77281_ += 8.0F;
         }
      }

      return pathnodetype == BlockPathTypes.OPEN ? pathpoint : pathpoint;
   }

   private BlockPathTypes isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int i = p_186327_1_; i < p_186327_1_ + this.f_77315_; i++) {
         for (int j = p_186327_2_; j < p_186327_2_ + this.f_77316_; j++) {
            for (int k = p_186327_3_; k < p_186327_3_ + this.f_77317_; k++) {
               FluidState fluidstate = this.f_77312_.m_6425_(blockpos$mutable.m_122178_(i, j, k));
               BlockState blockstate = this.f_77312_.m_8055_(blockpos$mutable.m_122178_(i, j, k));
               if (fluidstate.m_76178_() && blockstate.m_60647_(this.f_77312_, blockpos$mutable.m_7495_(), PathComputationType.WATER) && blockstate.m_60795_()) {
                  return BlockPathTypes.BREACH;
               }

               if (!fluidstate.m_205070_(FluidTags.f_13131_) && !fluidstate.m_205070_(FluidTags.f_13132_)) {
                  return BlockPathTypes.BLOCKED;
               }
            }
         }
      }

      BlockState blockstate1 = this.f_77312_.m_8055_(blockpos$mutable);
      return !blockstate1.m_60819_().m_205070_(FluidTags.f_13132_) && !blockstate1.m_60647_(this.f_77312_, blockpos$mutable, PathComputationType.WATER)
         ? BlockPathTypes.BLOCKED
         : BlockPathTypes.WATER;
   }
}
