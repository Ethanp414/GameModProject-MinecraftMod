package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class LeafcutterAntAIFollowCaravan extends Goal {
   public final EntityLeafcutterAnt ant;
   private double speedModifier;
   private int distCheckCounter;
   private int executionChance = 30;

   public LeafcutterAntAIFollowCaravan(EntityLeafcutterAnt llamaIn, double speedModifierIn) {
      this.ant = llamaIn;
      this.speedModifier = speedModifierIn;
      this.m_7021_(EnumSet.of(Flag.MOVE));
   }

   public boolean m_8036_() {
      long worldTime = this.ant.m_9236_().m_46467_() % 10L;
      if (this.ant.m_21216_() >= 100 && worldTime != 0L) {
         return false;
      } else if (this.ant.m_217043_().m_188503_(this.executionChance) != 0 && worldTime != 0L) {
         return false;
      } else if (!this.ant.shouldLeadCaravan() && !this.ant.m_6162_() && !this.ant.isQueen() && !this.ant.inCaravan() && !this.ant.hasLeaf()) {
         double dist = 15.0;
         List<EntityLeafcutterAnt> list = this.ant.m_9236_().m_45976_(EntityLeafcutterAnt.class, this.ant.m_20191_().m_82377_(dist, dist / 2.0, dist));
         EntityLeafcutterAnt LeafcutterAnt = null;
         double d0 = Double.MAX_VALUE;

         for (Entity entity : list) {
            EntityLeafcutterAnt LeafcutterAnt1 = (EntityLeafcutterAnt)entity;
            if (LeafcutterAnt1.inCaravan() && !LeafcutterAnt1.hasCaravanTrail()) {
               double d1 = this.ant.m_20280_(LeafcutterAnt1);
               if (!(d1 > d0)) {
                  d0 = d1;
                  LeafcutterAnt = LeafcutterAnt1;
               }
            }
         }

         if (LeafcutterAnt == null) {
            for (Entity entity1 : list) {
               EntityLeafcutterAnt llamaentity2 = (EntityLeafcutterAnt)entity1;
               if (llamaentity2.shouldLeadCaravan() && !llamaentity2.hasCaravanTrail()) {
                  double d2 = this.ant.m_20280_(llamaentity2);
                  if (!(d2 > d0)) {
                     d0 = d2;
                     LeafcutterAnt = llamaentity2;
                  }
               }
            }
         }

         if (LeafcutterAnt == null) {
            return false;
         } else if (d0 < 2.0) {
            return false;
         } else if (!LeafcutterAnt.shouldLeadCaravan() && !this.firstIsSilverback(LeafcutterAnt, 1)) {
            return false;
         } else {
            this.ant.joinCaravan(LeafcutterAnt);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean m_8045_() {
      if (this.ant.inCaravan() && this.ant.getCaravanHead().m_6084_() && this.firstIsSilverback(this.ant, 0)) {
         double d0 = this.ant.m_20280_(this.ant.getCaravanHead());
         if (d0 > 676.0) {
            if (this.speedModifier <= 1.5) {
               this.speedModifier *= 1.2;
               this.distCheckCounter = 40;
               return true;
            }

            if (this.distCheckCounter == 0) {
               return false;
            }
         }

         if (this.distCheckCounter > 0) {
            this.distCheckCounter--;
         }

         return true;
      } else {
         return false;
      }
   }

   public void m_8041_() {
      this.ant.leaveCaravan();
      this.speedModifier = 1.5;
   }

   public void m_8037_() {
      if (this.ant.inCaravan() && !this.ant.shouldLeadCaravan()) {
         EntityLeafcutterAnt llamaentity = this.ant.getCaravanHead();
         if (llamaentity != null) {
            double d0 = this.ant.m_20270_(llamaentity);
            Vec3 vector3d = new Vec3(
                  llamaentity.m_20185_() - this.ant.m_20185_(), llamaentity.m_20186_() - this.ant.m_20186_(), llamaentity.m_20189_() - this.ant.m_20189_()
               )
               .m_82541_()
               .m_82490_(Math.max(d0 - 2.0, 0.0));
            if (this.ant.m_21573_().m_26571_()) {
               try {
                  this.ant
                     .m_21573_()
                     .m_26519_(
                        this.ant.m_20185_() + vector3d.f_82479_,
                        this.ant.m_20186_() + vector3d.f_82480_,
                        this.ant.m_20189_() + vector3d.f_82481_,
                        this.speedModifier
                     );
               } catch (NullPointerException var6) {
                  AlexsMobs.LOGGER.warn("leafcutter ant encountered issue following caravan head");
               }
            }
         }
      }
   }

   private boolean firstIsSilverback(EntityLeafcutterAnt llama, int p_190858_2_) {
      if (p_190858_2_ > 8) {
         return false;
      } else if (llama.inCaravan()) {
         if (llama.getCaravanHead().shouldLeadCaravan()) {
            return true;
         } else {
            EntityLeafcutterAnt llamaentity = llama.getCaravanHead();
            return this.firstIsSilverback(llamaentity, ++p_190858_2_);
         }
      } else {
         return false;
      }
   }
}
