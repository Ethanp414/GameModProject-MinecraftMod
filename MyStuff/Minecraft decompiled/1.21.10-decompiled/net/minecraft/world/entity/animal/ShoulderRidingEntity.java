package net.minecraft.world.entity.animal;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public abstract class ShoulderRidingEntity extends TamableAnimal {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int RIDE_COOLDOWN = 100;
   private int rideCooldownCounter;

   protected ShoulderRidingEntity(EntityType<? extends ShoulderRidingEntity> $$0, Level $$1) {
      super($$0, $$1);
   }

   public boolean setEntityOnShoulder(ServerPlayer $$0) {
      try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput $$2 = TagValueOutput.createWithContext($$1, this.registryAccess());
         this.saveWithoutId($$2);
         $$2.putString("id", this.getEncodeId());
         if ($$0.setEntityOnShoulder($$2.buildResult())) {
            this.discard();
            return true;
         }
      }

      return false;
   }

   @Override
   public void tick() {
      ++this.rideCooldownCounter;
      super.tick();
   }

   public boolean canSitOnShoulder() {
      return this.rideCooldownCounter > 100;
   }
}
