package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class EntityAttachments {
   private final Map<EntityAttachment, List<Vec3>> attachments;

   EntityAttachments(Map<EntityAttachment, List<Vec3>> $$0) {
      this.attachments = $$0;
   }

   public static EntityAttachments createDefault(float $$0, float $$1) {
      return builder().build($$0, $$1);
   }

   public static EntityAttachments.Builder builder() {
      return new EntityAttachments.Builder();
   }

   public EntityAttachments scale(float $$0, float $$1, float $$2) {
      return new EntityAttachments(Util.makeEnumMap(EntityAttachment.class, $$3 -> {
         List<Vec3> $$4 = new ArrayList();

         for(Vec3 $$5 : (List)this.attachments.get($$3)) {
            $$4.add($$5.multiply((double)$$0, (double)$$1, (double)$$2));
         }

         return $$4;
      }));
   }

   @Nullable
   public Vec3 getNullable(EntityAttachment $$0, int $$1, float $$2) {
      List<Vec3> $$3 = (List)this.attachments.get($$0);
      return $$1 >= 0 && $$1 < $$3.size() ? transformPoint((Vec3)$$3.get($$1), $$2) : null;
   }

   public Vec3 get(EntityAttachment $$0, int $$1, float $$2) {
      Vec3 $$3 = this.getNullable($$0, $$1, $$2);
      if ($$3 == null) {
         throw new IllegalStateException("Had no attachment point of type: " + $$0 + " for index: " + $$1);
      } else {
         return $$3;
      }
   }

   public Vec3 getAverage(EntityAttachment $$0) {
      List<Vec3> $$1 = (List)this.attachments.get($$0);
      if ($$1 != null && !$$1.isEmpty()) {
         Vec3 $$2 = Vec3.ZERO;

         for(Vec3 $$3 : $$1) {
            $$2 = $$2.add($$3);
         }

         return $$2.scale((double)(1.0F / (float)$$1.size()));
      } else {
         throw new IllegalStateException("No attachment points of type: PASSENGER");
      }
   }

   public Vec3 getClamped(EntityAttachment $$0, int $$1, float $$2) {
      List<Vec3> $$3 = (List)this.attachments.get($$0);
      if ($$3.isEmpty()) {
         throw new IllegalStateException("Had no attachment points of type: " + $$0);
      } else {
         Vec3 $$4 = (Vec3)$$3.get(Mth.clamp($$1, 0, $$3.size() - 1));
         return transformPoint($$4, $$2);
      }
   }

   private static Vec3 transformPoint(Vec3 $$0, float $$1) {
      return $$0.yRot(-$$1 * (float) (Math.PI / 180.0));
   }

   public static class Builder {
      private final Map<EntityAttachment, List<Vec3>> attachments = new EnumMap(EntityAttachment.class);

      Builder() {
      }

      public EntityAttachments.Builder attach(EntityAttachment $$0, float $$1, float $$2, float $$3) {
         return this.attach($$0, new Vec3((double)$$1, (double)$$2, (double)$$3));
      }

      public EntityAttachments.Builder attach(EntityAttachment $$0, Vec3 $$1) {
         ((List)this.attachments.computeIfAbsent($$0, $$0x -> new ArrayList(1))).add($$1);
         return this;
      }

      public EntityAttachments build(float $$0, float $$1) {
         Map<EntityAttachment, List<Vec3>> $$2 = Util.makeEnumMap(EntityAttachment.class, $$2x -> {
            List<Vec3> $$3 = (List)this.attachments.get($$2x);
            return $$3 == null ? $$2x.createFallbackPoints($$0, $$1) : List.copyOf($$3);
         });
         return new EntityAttachments($$2);
      }
   }
}
