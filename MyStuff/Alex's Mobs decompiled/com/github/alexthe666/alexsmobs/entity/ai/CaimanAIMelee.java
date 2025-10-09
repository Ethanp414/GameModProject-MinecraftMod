package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import java.util.EnumSet;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class CaimanAIMelee extends Goal {
   private final EntityCaiman caiman;
   private int grabTime = 0;

   public CaimanAIMelee(EntityCaiman caiman) {
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.caiman = caiman;
   }

   public boolean m_8036_() {
      LivingEntity target = this.caiman.m_5448_();
      return target != null && target.m_6084_();
   }

   public void m_8041_() {
      this.caiman.setHeldMobId(-1);
      this.grabTime = 0;
   }

   public void m_8037_() {
      if (this.grabTime < 0) {
         this.grabTime++;
      }

      LivingEntity target = this.caiman.m_5448_();
      if (target != null) {
         double bbWidth = (this.caiman.m_20205_() + target.m_20205_()) / 2.0;
         double dist = this.caiman.m_20270_(target);
         boolean flag = false;
         if (dist < bbWidth + 2.0 && this.grabTime >= 0) {
            if (this.grabTime % 25 == 0) {
               target.m_6469_(
                  this.caiman.m_21824_() ? this.caiman.m_269291_().m_269063_() : this.caiman.m_269291_().m_269333_(this.caiman),
                  (float)this.caiman.m_21133_(Attributes.f_22281_)
               );
            }

            this.grabTime++;
            Vec3 shakePreyPos = this.caiman.getShakePreyPos();
            Vec3 minus = new Vec3(shakePreyPos.f_82479_ - target.m_20185_(), 0.0, shakePreyPos.f_82481_ - target.m_20189_()).m_82541_();
            target.m_20256_(target.m_20184_().m_82542_(0.6F, 0.6F, 0.6F).m_82549_(minus.m_82490_(0.35F)));
            flag = true;
            if (this.grabTime > this.getGrabDuration()) {
               this.grabTime = -10;
            }
         }

         this.caiman.setHeldMobId(flag ? target.m_19879_() : -1);
         if (dist > bbWidth && !flag) {
            this.caiman.m_7618_(Anchor.EYES, target.m_146892_());
            this.caiman.m_21573_().m_5624_(target, 1.2F);
         }
      }
   }

   private int getGrabDuration() {
      return this.caiman.m_21824_() && this.caiman.tameAttackFlag ? 300 : 2;
   }
}
