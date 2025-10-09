package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RockyChestplateUtil {
   private static final String ROCKY_ROLL_TICKS = "RockyRollTicksAlexsMobs";
   private static final String ROCKY_ROLL_TIMESTAMP = "RockyRollTimestampAlexsMobs";
   private static final String ROCKY_X = "RockyRollXAlexsMobs";
   private static final String ROCKY_Y = "RockyRollYAlexsMobs";
   private static final String ROCKY_Z = "RockyRollZAlexsMobs";
   private static final int MAX_ROLL_TICKS = 30;

   public static void rollFor(LivingEntity roller, int ticks) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(roller);
      lassoedTag.m_128405_("RockyRollTicksAlexsMobs", ticks);
      if (ticks == 30) {
         lassoedTag.m_128405_("RockyRollTimestampAlexsMobs", roller.f_19797_);
      }

      CitadelEntityData.setCitadelTag(roller, lassoedTag);
      if (!roller.m_9236_().f_46443_) {
         Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", lassoedTag, roller.m_19879_()));
      } else {
         Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", lassoedTag, roller.m_19879_()));
      }
   }

   public static int getRollingTicksLeft(LivingEntity entity) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(entity);
      return lassoedTag.m_128441_("RockyRollTicksAlexsMobs") ? lassoedTag.m_128451_("RockyRollTicksAlexsMobs") : 0;
   }

   public static int getRollingTimestamp(LivingEntity entity) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(entity);
      return lassoedTag.m_128441_("RockyRollTimestampAlexsMobs") ? lassoedTag.m_128451_("RockyRollTimestampAlexsMobs") : 0;
   }

   public static boolean isWearing(LivingEntity entity) {
      return entity.m_6844_(EquipmentSlot.CHEST).m_41720_() == AMItemRegistry.ROCKY_CHESTPLATE.get();
   }

   public static boolean isRockyRolling(LivingEntity entity) {
      return isWearing(entity) && getRollingTicksLeft(entity) > 0;
   }

   public static void tickRockyRolling(LivingEntity roller) {
      if (roller.m_20072_()) {
         roller.m_20256_(roller.m_20184_().m_82520_(0.0, -0.015F, 0.0));
      }

      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(roller);
      boolean update = false;
      int rollCounter = getRollingTicksLeft(roller);
      if (rollCounter == 0) {
         if (roller.m_20142_()
            && !roller.m_6144_()
            && (!(roller instanceof Player) || !((Player)roller).m_150110_().f_35935_)
            && canRollAgain(roller)
            && !roller.m_20159_()) {
            update = true;
            rollFor(roller, 30);
         }

         if (roller instanceof Player && ((Player)roller).getForcedPose() == Pose.SWIMMING) {
            ((Player)roller).setForcedPose(null);
         }
      } else {
         if (roller instanceof Player) {
            ((Player)roller).setForcedPose(Pose.SWIMMING);
         }

         if (!roller.m_9236_().f_46443_) {
            for (Entity entity : roller.m_9236_().m_45976_(LivingEntity.class, roller.m_20191_().m_82400_(1.0))) {
               if (!roller.m_7307_(entity) && !entity.m_7307_(roller) && entity != roller) {
                  entity.m_6469_(entity.m_269291_().m_269333_(roller), 2.0F + roller.m_217043_().m_188501_() * 1.0F);
               }
            }
         }

         if (roller.f_19789_ > 3.0F) {
            roller.f_19789_ -= 0.5F;
         }

         roller.m_6210_();
         Vec3 vec3 = roller.m_20096_() ? roller.m_20184_() : roller.m_20184_().m_82542_(0.9, 1.0, 0.9);
         float f = roller.m_146908_() * (float) (Math.PI / 180.0);
         float f1 = roller.m_20072_() ? 0.05F : 0.15F;
         Vec3 rollDelta = new Vec3(vec3.f_82479_ + -Mth.m_14031_(f) * f1, 0.0, vec3.f_82481_ + Mth.m_14089_(f) * f1);
         double rollY = !roller.m_20072_() && !roller.m_6144_() ? (rollCounter >= 30 ? 0.27 : vec3.f_82480_) : -0.1F;
         roller.m_20256_(rollDelta.m_82520_(0.0, rollY, 0.0));
         if (rollCounter > 1 || !roller.m_20142_()) {
            rollFor(roller, rollCounter - 1);
         }

         if ((roller instanceof Player && ((Player)roller).m_150110_().f_35935_ || roller.m_6144_()) && canRollAgain(roller)) {
            rollCounter = 0;
            rollFor(roller, 0);
         }

         if (rollCounter == 0) {
            update = true;
         }
      }

      if (!roller.m_9236_().f_46443_ && update) {
         CitadelEntityData.setCitadelTag(roller, tag);
         Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", tag, roller.m_19879_()));
      }
   }

   private static boolean canRollAgain(LivingEntity roller) {
      return roller.f_19797_ - getRollingTimestamp(roller) >= 20 || Math.abs(roller.f_19797_ - getRollingTimestamp(roller)) > 100;
   }
}
