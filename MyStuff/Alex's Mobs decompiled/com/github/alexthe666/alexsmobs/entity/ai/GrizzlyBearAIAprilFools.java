package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.message.MessageSendVisualFlagFromServer;
import com.github.alexthe666.alexsmobs.misc.AMDamageTypes;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.EnumSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;

public class GrizzlyBearAIAprilFools extends Goal {
   private final EntityGrizzlyBear bear;
   private Player target;
   private int runDelay = 0;
   private final double maxDistance = 13.0;
   private int powerOutTimer = 0;
   private int musicBoxTimer = 0;
   private int maxMusicBoxTime = 0;
   private int leapTimer = 0;

   public GrizzlyBearAIAprilFools(EntityGrizzlyBear bear) {
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.bear = bear;
   }

   public boolean m_8036_() {
      if (!this.bear.m_6162_() && AlexsMobs.isAprilFools() && this.runDelay-- <= 0 && this.bear.m_217043_().m_188503_(30) == 0) {
         this.runDelay = 400 + this.bear.m_217043_().m_188503_(350);
         Player nearestPlayer = this.bear
            .m_9236_()
            .m_5788_(
               this.bear.m_20185_(),
               this.bear.m_20186_(),
               this.bear.m_20189_(),
               13.0,
               entity -> this.bear.m_142582_(entity)
                  && (!(entity instanceof Player) || !((Player)entity).m_21023_((MobEffect)AMEffectRegistry.POWER_DOWN.get()))
            );
         if (nearestPlayer != null) {
            this.target = nearestPlayer;
            return true;
         }
      }

      return false;
   }

   public boolean m_8045_() {
      return this.target != null && this.bear.m_20270_(this.target) < 26.0;
   }

   public void m_8056_() {
      this.maxMusicBoxTime = 100 + this.bear.m_217043_().m_188503_(130);
   }

   public void m_8037_() {
      super.m_8037_();
      double dist = this.bear.m_20270_(this.target);
      this.bear.m_21563_().m_24946_(this.target.m_20185_(), this.target.m_20188_(), this.target.m_20189_());
      if (dist <= 6.0 && this.bear.m_142582_(this.target)) {
         this.bear.m_21573_().m_26573_();
         if (this.bear.getAprilFoolsFlag() == 5) {
            this.leapTimer++;
            if (this.leapTimer == 7) {
               AlexsMobs.sendMSGToAll(new MessageSendVisualFlagFromServer(this.target.m_19879_(), 87));
            }

            if (this.leapTimer >= 10) {
               this.bear.setAprilFoolsFlag(0);
               if (this.bear.m_9236_().m_6106_().m_5466_()) {
                  this.target.m_6469_(AMDamageTypes.causeFreddyBearDamage(this.bear), this.target.m_21233_() - 1.0F);
                  this.target.m_21153_(1.0F);
               } else {
                  this.target.m_6469_(AMDamageTypes.causeFreddyBearDamage(this.bear), this.target.m_21233_() + 1000.0F);
               }

               this.m_8041_();
               return;
            }
         } else if (this.bear.getAprilFoolsFlag() < 4) {
            if (this.powerOutTimer == 0) {
               this.target
                  .m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.POWER_DOWN.get(), 2 * (this.maxMusicBoxTime + 100), 0, false, false, true));
            }

            this.powerOutTimer++;
            if (this.powerOutTimer >= 60) {
               this.bear.setAprilFoolsFlag(4);
               this.powerOutTimer = 0;
            } else {
               this.bear.setAprilFoolsFlag(3);
            }
         } else {
            if (this.musicBoxTimer == 0) {
               this.bear.m_9236_().m_7605_(this.bear, (byte)67);
            }

            this.musicBoxTimer++;
            if (this.musicBoxTimer >= this.maxMusicBoxTime && this.bear.getAprilFoolsFlag() != 5) {
               this.bear.m_9236_().m_7605_(this.bear, (byte)68);
               this.bear.setAprilFoolsFlag(5);
               this.bear.m_146850_(GameEvent.f_223709_);
               this.bear.m_5496_((SoundEvent)AMSoundRegistry.APRIL_FOOLS_SCREAM.get(), 3.0F, 1.0F);
               this.musicBoxTimer = 0;
            }
         }

         if (this.bear.getAprilFoolsFlag() < 2) {
            this.bear.setAprilFoolsFlag(2);
         }
      } else {
         this.bear.m_21573_().m_5624_(this.target, 1.2F);
         if (this.bear.getAprilFoolsFlag() < 1) {
            this.bear.setAprilFoolsFlag(1);
         }
      }
   }

   public void m_8041_() {
      this.target = null;
      this.runDelay = 100 + this.bear.m_217043_().m_188503_(100);
      this.bear.setAprilFoolsFlag(0);
      this.powerOutTimer = 0;
      this.musicBoxTimer = 0;
      this.leapTimer = 0;
   }
}
