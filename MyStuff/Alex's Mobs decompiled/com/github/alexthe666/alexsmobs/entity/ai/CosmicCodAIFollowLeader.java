package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCosmicCod;
import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.Goal;

public class CosmicCodAIFollowLeader extends Goal {
   private static final int INTERVAL_TICKS = 200;
   private final EntityCosmicCod mob;
   private int timeToRecalcPath;
   private int nextStartTick;

   public CosmicCodAIFollowLeader(EntityCosmicCod cod) {
      this.mob = cod;
      this.nextStartTick = this.nextStartTick(cod);
   }

   protected int nextStartTick(EntityCosmicCod p_25252_) {
      return m_186073_(100 + p_25252_.m_217043_().m_188503_(100) % 20);
   }

   public boolean m_8036_() {
      if (this.mob.isGroupLeader() || this.mob.isCircling()) {
         return false;
      } else if (this.mob.hasGroupLeader()) {
         return true;
      } else if (this.nextStartTick > 0) {
         this.nextStartTick--;
         return false;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         Predicate<EntityCosmicCod> predicate = p_25258_ -> p_25258_.canGroupGrow() || !p_25258_.hasGroupLeader();
         List<EntityCosmicCod> list = this.mob.m_9236_().m_6443_(EntityCosmicCod.class, this.mob.m_20191_().m_82377_(8.0, 8.0, 8.0), predicate);
         EntityCosmicCod cc = (EntityCosmicCod)DataFixUtils.orElse(list.stream().filter(EntityCosmicCod::canGroupGrow).findAny(), this.mob);
         cc.createFromStream(list.stream().filter(p_25255_ -> !p_25255_.hasGroupLeader()));
         return this.mob.hasGroupLeader();
      }
   }

   public boolean m_8045_() {
      return this.mob.hasGroupLeader() && this.mob.inRangeOfGroupLeader() && !this.mob.isCircling();
   }

   public void m_8056_() {
      this.timeToRecalcPath = 0;
   }

   public void m_8041_() {
      this.mob.leaveGroup();
   }

   public void m_8037_() {
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.m_183277_(10);
         this.mob.moveToGroupLeader();
      }
   }
}
