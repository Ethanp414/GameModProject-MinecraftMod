package net.minecraft.world.entity;

import java.util.List;
import net.minecraft.world.phys.Vec3;

public enum EntityAttachment {
   PASSENGER(EntityAttachment.Fallback.AT_HEIGHT),
   VEHICLE(EntityAttachment.Fallback.AT_FEET),
   NAME_TAG(EntityAttachment.Fallback.AT_HEIGHT),
   WARDEN_CHEST(EntityAttachment.Fallback.AT_CENTER);

   private final EntityAttachment.Fallback fallback;

   private EntityAttachment(final EntityAttachment.Fallback param3) {
      this.fallback = $$0;
   }

   public List<Vec3> createFallbackPoints(float $$0, float $$1) {
      return this.fallback.create($$0, $$1);
   }

   public interface Fallback {
      List<Vec3> ZERO = List.of(Vec3.ZERO);
      EntityAttachment.Fallback AT_FEET = ($$0, $$1) -> ZERO;
      EntityAttachment.Fallback AT_HEIGHT = ($$0, $$1) -> List.of(new Vec3(0.0, (double)$$1, 0.0));
      EntityAttachment.Fallback AT_CENTER = ($$0, $$1) -> List.of(new Vec3(0.0, (double)$$1 / 2.0, 0.0));

      List<Vec3> create(float var1, float var2);
   }
}
