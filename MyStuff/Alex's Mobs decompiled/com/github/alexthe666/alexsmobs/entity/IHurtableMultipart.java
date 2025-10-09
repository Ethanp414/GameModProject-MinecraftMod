package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IHurtableMultipart {
   void onAttackedFromServer(LivingEntity var1, float var2, DamageSource var3);

   default Vec3 calcOffsetVec(float offsetZ, float xRot, float yRot) {
      return new Vec3(0.0, 0.0, offsetZ).m_82496_(xRot * (float) (Math.PI / 180.0)).m_82524_(-yRot * (float) (Math.PI / 180.0));
   }

   default float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
      float f = Mth.m_14177_(targetAngle - sourceAngle);
      if (f > maximumChange) {
         f = maximumChange;
      }

      if (f < -maximumChange) {
         f = -maximumChange;
      }

      float f1 = sourceAngle + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }
}
