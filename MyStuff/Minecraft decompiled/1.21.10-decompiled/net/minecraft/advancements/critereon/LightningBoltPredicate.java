package net.minecraft.advancements.critereon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public record LightningBoltPredicate(MinMaxBounds.Ints blocksSetOnFire, Optional<EntityPredicate> entityStruck) implements EntitySubPredicate {
   public static final MapCodec<LightningBoltPredicate> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               MinMaxBounds.Ints.CODEC.optionalFieldOf("blocks_set_on_fire", MinMaxBounds.Ints.ANY).forGetter(LightningBoltPredicate::blocksSetOnFire),
               EntityPredicate.CODEC.optionalFieldOf("entity_struck").forGetter(LightningBoltPredicate::entityStruck)
            )
            .apply($$0, LightningBoltPredicate::new)
   );

   public static LightningBoltPredicate blockSetOnFire(MinMaxBounds.Ints $$0) {
      return new LightningBoltPredicate($$0, Optional.empty());
   }

   @Override
   public MapCodec<LightningBoltPredicate> codec() {
      return EntitySubPredicates.LIGHTNING;
   }

   @Override
   public boolean matches(Entity $$0, ServerLevel $$1, @Nullable Vec3 $$2) {
      if (!($$0 instanceof LightningBolt)) {
         return false;
      } else {
         LightningBolt $$3 = (LightningBolt)$$0;
         return this.blocksSetOnFire.matches($$3.getBlocksSetOnFire())
            && (this.entityStruck.isEmpty() || $$3.getHitEntities().anyMatch($$2x -> ((EntityPredicate)this.entityStruck.get()).matches($$1, $$2, $$2x)));
      }
   }
}
