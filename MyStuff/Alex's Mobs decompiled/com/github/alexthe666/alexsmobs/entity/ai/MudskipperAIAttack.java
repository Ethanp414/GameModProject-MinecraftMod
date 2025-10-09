package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMudBall;
import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.gameevent.GameEvent;

public class MudskipperAIAttack extends Goal {
   private final EntityMudskipper entity;
   private int shootCooldown = 0;
   private boolean strafed = false;

   public MudskipperAIAttack(EntityMudskipper mob) {
      this.entity = mob;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      return this.entity.m_5448_() != null && this.entity.m_5448_().m_6084_();
   }

   public void m_8041_() {
      this.shootCooldown = 0;
      this.strafed = false;
   }

   public void m_8037_() {
      Entity target = this.entity.m_5448_();
      boolean keepFollowing = true;
      if (this.shootCooldown > 0) {
         this.shootCooldown--;
      }

      if (this.entity.m_21574_().m_148306_(target)) {
         float dist = this.entity.m_20270_(target);
         if (dist < this.entity.m_20205_() + target.m_20205_() + 3.0F) {
            keepFollowing = false;
            this.entity.m_21391_(target, 360.0F, 360.0F);
            this.entity.m_21566_().m_24988_(-3.0F, 0.0F);
            this.strafed = true;
         }

         if (dist < 8.0F && this.shootCooldown == 0) {
            EntityMudBall mudball = new EntityMudBall(this.entity.m_9236_(), this.entity);
            double d0 = target.m_20185_() - mudball.m_20185_();
            double d1 = target.m_20227_(0.3F) - mudball.m_20186_();
            double d2 = target.m_20189_() - mudball.m_20189_();
            float f = Mth.m_14116_((float)(d0 * d0 + d2 * d2)) * 0.4F;
            mudball.shoot(d0, d1 + f, d2, 1.0F, 10.0F);
            if (!this.entity.m_20067_()) {
               this.entity.m_146850_(GameEvent.f_157778_);
               this.entity
                  .m_9236_()
                  .m_6263_(
                     null,
                     this.entity.m_20185_(),
                     this.entity.m_20186_(),
                     this.entity.m_20189_(),
                     (SoundEvent)AMSoundRegistry.MUDSKIPPER_SPIT.get(),
                     this.entity.m_5720_(),
                     1.0F,
                     1.0F + (this.entity.m_217043_().m_188501_() - this.entity.m_217043_().m_188501_()) * 0.2F
                  );
            }

            this.entity.m_9236_().m_7967_(mudball);
            this.shootCooldown = 10 + this.entity.m_217043_().m_188503_(10);
            this.entity.openMouth(10);
         }
      }

      if (keepFollowing) {
         if (this.strafed) {
            this.entity.m_21566_().m_24988_(0.0F, 0.0F);
            this.strafed = false;
         }

         this.entity.m_21573_().m_5624_(target, 1.5);
      } else {
         this.entity.m_21573_().m_26573_();
      }
   }
}
