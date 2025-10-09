package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;

public class EntityCentipedeTail extends EntityCentipedeBody {
   protected EntityCentipedeTail(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   @Override
   public MobType m_6336_() {
      return MobType.f_21642_;
   }
}
