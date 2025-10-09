package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class EffectDebilitatingSting extends MobEffect {
   private int lastDuration = -1;

   protected EffectDebilitatingSting() {
      super(MobEffectCategory.NEUTRAL, 16774021);
      this.m_19472_(Attributes.f_22279_, "7107DE5E-7CE8-4030-940E-514C1F160890", -1.0, Operation.MULTIPLY_BASE);
   }

   public void m_6386_(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
      if (entityLivingBaseIn.m_6336_() == MobType.f_21642_) {
         super.m_6386_(entityLivingBaseIn, attributeMapIn, amplifier);
      }
   }

   public void m_6385_(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
      if (entityLivingBaseIn.m_6336_() == MobType.f_21642_) {
         super.m_6385_(entityLivingBaseIn, attributeMapIn, amplifier);
      }
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
      if (entity.m_6336_() != MobType.f_21642_) {
         if (entity.m_21223_() > entity.m_21233_() * 0.5F) {
            entity.m_6469_(entity.m_269291_().m_269425_(), 1.0F);
         }
      } else {
         boolean suf = this.isEntityInsideOpaqueBlock(entity);
         if (suf) {
            entity.m_20256_(Vec3.f_82478_);
            entity.f_19794_ = true;
         }

         entity.m_20242_(suf);
         entity.m_6862_(false);
         if (!entity.m_20159_() && entity instanceof Mob && ((Mob)entity).m_21566_().getClass() != MoveControl.class) {
            entity.m_20256_(new Vec3(0.0, -1.0, 0.0));
         }

         if (this.lastDuration == 1) {
            entity.m_6469_(entity.m_269291_().m_269425_(), (amplifier + 1) * 30);
            if (amplifier > 0) {
               BlockPos surface = entity.m_20183_();

               while (!entity.m_9236_().m_46859_(surface) && surface.m_123342_() < 256) {
                  surface = surface.m_7494_();
               }

               EntityTarantulaHawk baby = (EntityTarantulaHawk)((EntityType)AMEntityRegistry.TARANTULA_HAWK.get()).m_20615_(entity.m_9236_());
               baby.m_6863_(true);
               baby.m_6034_(entity.m_20185_(), surface.m_123342_() + 0.1F, entity.m_20189_());
               if (!entity.m_9236_().f_46443_) {
                  baby.m_6518_((ServerLevelAccessor)entity.m_9236_(), entity.m_9236_().m_6436_(entity.m_20183_()), MobSpawnType.BREEDING, null, null);
                  entity.m_9236_().m_7967_(baby);
               }
            }

            entity.m_20242_(false);
            entity.f_19794_ = false;
         }
      }
   }

   public boolean isEntityInsideOpaqueBlock(Entity entity) {
      Vec3 vec3 = entity.m_146892_();
      float f = entity.m_6972_(entity.m_20089_()).f_20377_ * 0.8F;
      AABB axisalignedbb = AABB.m_165882_(vec3, f, 1.0E-6, f);
      return entity.m_9236_()
         .m_45556_(axisalignedbb)
         .filter(Predicate.not(BlockStateBase::m_60795_))
         .anyMatch(
            p_185969_ -> {
               BlockPos blockpos = AMBlockPos.fromVec3(vec3);
               return p_185969_.m_60828_(entity.m_9236_(), blockpos)
                  && Shapes.m_83157_(
                     p_185969_.m_60812_(entity.m_9236_(), blockpos).m_83216_(vec3.f_82479_, vec3.f_82480_, vec3.f_82481_),
                     Shapes.m_83064_(axisalignedbb),
                     BooleanOp.f_82689_
                  );
            }
         );
   }

   public boolean m_6584_(int duration, int amplifier) {
      this.lastDuration = duration;
      return duration > 0;
   }

   public String m_19481_() {
      return "alexsmobs.potion.debilitating_sting";
   }
}
