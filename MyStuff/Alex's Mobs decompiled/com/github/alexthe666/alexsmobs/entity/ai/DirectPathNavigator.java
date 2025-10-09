package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;

public class DirectPathNavigator extends GroundPathNavigation {
   private final Mob mob;
   private float yMobOffset = 0.0F;

   public DirectPathNavigator(Mob mob, Level world) {
      this(mob, world, 0.0F);
   }

   public DirectPathNavigator(Mob mob, Level world, float yMobOffset) {
      super(mob, world);
      this.mob = mob;
      this.yMobOffset = yMobOffset;
   }

   public void m_7638_() {
      this.f_26498_++;
   }

   public boolean m_26519_(double x, double y, double z, double speedIn) {
      this.mob.m_21566_().m_6849_(x, y, z, speedIn);
      return true;
   }

   public boolean m_5624_(Entity entityIn, double speedIn) {
      this.mob.m_21566_().m_6849_(entityIn.m_20185_(), entityIn.m_20186_() + this.yMobOffset, entityIn.m_20189_(), speedIn);
      return true;
   }
}
