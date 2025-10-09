package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import java.util.EnumSet;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class AnimalAILeaveWaterLava extends Goal {
   private final PathfinderMob creature;
   private BlockPos targetPos;
   private final int executionChance = 30;

   public AnimalAILeaveWaterLava(PathfinderMob creature) {
      this.creature = creature;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      if ((
            this.creature.m_9236_().m_6425_(this.creature.m_20183_()).m_205070_(FluidTags.f_13131_)
               || this.creature.m_9236_().m_6425_(this.creature.m_20183_()).m_205070_(FluidTags.f_13132_)
         )
         && this.creature instanceof ISemiAquatic
         && ((ISemiAquatic)this.creature).shouldLeaveWater()
         && (this.creature.m_5448_() != null || this.creature.m_217043_().m_188503_(30) == 0)) {
         this.targetPos = this.generateTarget();
         return this.targetPos != null;
      } else {
         return false;
      }
   }

   public void m_8056_() {
      if (this.targetPos != null) {
         this.creature.m_21573_().m_26519_(this.targetPos.m_123341_(), this.targetPos.m_123342_(), this.targetPos.m_123343_(), 1.0);
      }
   }

   public void m_8037_() {
      if (this.targetPos != null) {
         this.creature.m_21573_().m_26519_(this.targetPos.m_123341_(), this.targetPos.m_123342_(), this.targetPos.m_123343_(), 1.0);
      }

      if (this.creature.f_19862_ && (this.creature.m_20069_() || this.creature.m_20077_())) {
         float f1 = this.creature.m_146908_() * (float) (Math.PI / 180.0);
         this.creature.m_20256_(this.creature.m_20184_().m_82520_(-Mth.m_14031_(f1) * 0.2F, 0.1, Mth.m_14089_(f1) * 0.2F));
      }
   }

   public boolean m_8045_() {
      if (this.creature instanceof ISemiAquatic && !((ISemiAquatic)this.creature).shouldLeaveWater()) {
         this.creature.m_21573_().m_26573_();
         return false;
      } else {
         return !this.creature.m_21573_().m_26571_()
            && this.targetPos != null
            && !this.creature.m_9236_().m_6425_(this.targetPos).m_205070_(FluidTags.f_13131_)
            && !this.creature.m_9236_().m_6425_(this.targetPos).m_205070_(FluidTags.f_13132_);
      }
   }

   public BlockPos generateTarget() {
      Vec3 vector3d = LandRandomPos.m_148488_(this.creature, 23, 7);

      for (int tries = 0; vector3d != null && tries < 8; tries++) {
         boolean waterDetected = false;
         Iterator var4 = BlockPos.m_121976_(
               Mth.m_14107_(vector3d.f_82479_ - 2.0),
               Mth.m_14107_(vector3d.f_82480_ - 1.0),
               Mth.m_14107_(vector3d.f_82481_ - 2.0),
               Mth.m_14107_(vector3d.f_82479_ + 2.0),
               Mth.m_14107_(vector3d.f_82480_),
               Mth.m_14107_(vector3d.f_82481_ + 2.0)
            )
            .iterator();

         while (true) {
            if (var4.hasNext()) {
               BlockPos blockpos1 = (BlockPos)var4.next();
               if (!this.creature.m_9236_().m_6425_(blockpos1).m_205070_(FluidTags.f_13131_)
                  && !this.creature.m_9236_().m_6425_(blockpos1).m_205070_(FluidTags.f_13132_)) {
                  continue;
               }

               waterDetected = true;
            }

            if (!waterDetected) {
               return AMBlockPos.fromVec3(vector3d);
            }

            vector3d = LandRandomPos.m_148488_(this.creature, 23, 7);
            break;
         }
      }

      return null;
   }
}
