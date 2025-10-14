package net.minecraft.world.entity;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;

public interface OwnableEntity {
   @Nullable
   EntityReference<LivingEntity> getOwnerReference();

   Level level();

   @Nullable
   default LivingEntity getOwner() {
      return EntityReference.getLivingEntity(this.getOwnerReference(), this.level());
   }

   @Nullable
   default LivingEntity getRootOwner() {
      Set<Object> $$0 = new ObjectArraySet<>();
      LivingEntity $$1 = this.getOwner();
      $$0.add(this);

      while($$1 instanceof OwnableEntity) {
         OwnableEntity $$2 = (OwnableEntity)$$1;
         LivingEntity $$3 = $$2.getOwner();
         if ($$0.contains($$3)) {
            return null;
         }

         $$0.add($$1);
         $$1 = $$2.getOwner();
      }

      return $$1;
   }
}
