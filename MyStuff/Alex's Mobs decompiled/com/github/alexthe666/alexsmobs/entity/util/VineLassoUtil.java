package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class VineLassoUtil {
   private static final String LASSO_PACKET = "LassoSentPacketAlexsMobs";
   private static final String LASSO_REMOVED = "LassoRemovedAlexsMobs";
   private static final String LASSOED_TO_TAG = "LassoOwnerAlexsMobs";
   private static final String LASSOED_TO_ENTITY_ID_TAG = "LassoOwnerIDAlexsMobs";

   public static void lassoTo(@Nullable LivingEntity lassoer, LivingEntity lassoed) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
      if (lassoer == null) {
         lassoedTag.m_128362_("LassoOwnerAlexsMobs", UUID.randomUUID());
         lassoedTag.m_128405_("LassoOwnerIDAlexsMobs", -1);
         lassoedTag.m_128379_("LassoRemovedAlexsMobs", true);
      } else if (!lassoedTag.m_128441_("LassoOwnerIDAlexsMobs") || lassoedTag.m_128451_("LassoOwnerIDAlexsMobs") == -1) {
         lassoedTag.m_128362_("LassoOwnerAlexsMobs", lassoer.m_20148_());
         lassoedTag.m_128405_("LassoOwnerIDAlexsMobs", lassoer.m_19879_());
         lassoedTag.m_128379_("LassoRemovedAlexsMobs", false);
      }

      lassoedTag.m_128379_("LassoSentPacketAlexsMobs", true);
      CitadelEntityData.setCitadelTag(lassoed, lassoedTag);
      if (!lassoed.m_9236_().f_46443_) {
         Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", lassoedTag, lassoed.m_19879_()));
      }
   }

   public static boolean hasLassoData(LivingEntity lasso) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lasso);
      return lassoedTag.m_128441_("LassoOwnerIDAlexsMobs")
         && !lassoedTag.m_128471_("LassoRemovedAlexsMobs")
         && lassoedTag.m_128451_("LassoOwnerIDAlexsMobs") != -1;
   }

   public static Entity getLassoedTo(LivingEntity lassoed) {
      CompoundTag lassoedTag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
      if (lassoedTag.m_128471_("LassoRemovedAlexsMobs")) {
         return null;
      } else {
         if (hasLassoData(lassoed)) {
            if (lassoed.m_9236_().f_46443_ && lassoedTag.m_128441_("LassoOwnerIDAlexsMobs")) {
               int i = lassoedTag.m_128451_("LassoOwnerIDAlexsMobs");
               if (i != -1) {
                  Entity found = lassoed.m_9236_().m_6815_(i);
                  if (found != null) {
                     return found;
                  }

                  UUID uuid = lassoedTag.m_128342_("LassoOwnerAlexsMobs");
                  if (uuid != null) {
                     return lassoed.m_9236_().m_46003_(uuid);
                  }
               }
            } else if (lassoed.m_9236_() instanceof ServerLevel) {
               UUID uuid = lassoedTag.m_128342_("LassoOwnerAlexsMobs");
               if (uuid != null) {
                  Entity foundx = ((ServerLevel)lassoed.m_9236_()).m_8791_(uuid);
                  if (foundx != null) {
                     lassoedTag.m_128405_("LassoOwnerIDAlexsMobs", foundx.m_19879_());
                     return foundx;
                  }
               }
            }
         }

         return null;
      }
   }

   public static void tickLasso(LivingEntity lassoed) {
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(lassoed);
      if (!lassoed.m_9236_().f_46443_ && (tag.m_128441_("LassoSentPacketAlexsMobs") || tag.m_128471_("LassoRemovedAlexsMobs"))) {
         tag.m_128379_("LassoSentPacketAlexsMobs", false);
         CitadelEntityData.setCitadelTag(lassoed, tag);
         Citadel.sendMSGToAll(new PropertiesMessage("CitadelPatreonConfig", tag, lassoed.m_19879_()));
      }

      Entity lassoedOwner = getLassoedTo(lassoed);
      if (lassoedOwner != null) {
         double distance = lassoed.m_20270_(lassoedOwner);
         if (lassoed instanceof Mob mob) {
            if (distance > 3.0) {
               mob.m_21573_().m_5624_(lassoedOwner, 1.0);
            } else {
               mob.m_21573_().m_26573_();
            }
         }

         if (distance > 10.0) {
            double d0 = (lassoedOwner.m_20185_() - lassoed.m_20185_()) / distance;
            double d1 = (lassoedOwner.m_20186_() - lassoed.m_20186_()) / distance;
            double d2 = (lassoedOwner.m_20189_() - lassoed.m_20189_()) / distance;
            double yd = Math.copySign(d1 * d1 * 0.4, d1);
            if (lassoed instanceof Player) {
               yd = 0.0;
            }

            lassoed.m_20256_(lassoed.m_20184_().m_82520_(Math.copySign(d0 * d0 * 0.4, d0), yd, Math.copySign(d2 * d2 * 0.4, d2)));
         }
      }
   }
}
