package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SimpleExplosionDamageCalculator extends ExplosionDamageCalculator {
   private final boolean explodesBlocks;
   private final boolean damagesEntities;
   private final Optional<Float> knockbackMultiplier;
   private final Optional<HolderSet<Block>> immuneBlocks;

   public SimpleExplosionDamageCalculator(boolean $$0, boolean $$1, Optional<Float> $$2, Optional<HolderSet<Block>> $$3) {
      this.explodesBlocks = $$0;
      this.damagesEntities = $$1;
      this.knockbackMultiplier = $$2;
      this.immuneBlocks = $$3;
   }

   @Override
   public Optional<Float> getBlockExplosionResistance(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, FluidState $$4) {
      if (this.immuneBlocks.isPresent()) {
         return $$3.is((HolderSet<Block>)this.immuneBlocks.get()) ? Optional.of(3600000.0F) : Optional.empty();
      } else {
         return super.getBlockExplosionResistance($$0, $$1, $$2, $$3, $$4);
      }
   }

   @Override
   public boolean shouldBlockExplode(Explosion $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, float $$4) {
      return this.explodesBlocks;
   }

   @Override
   public boolean shouldDamageEntity(Explosion $$0, Entity $$1) {
      return this.damagesEntities;
   }

   @Override
   public float getKnockbackMultiplier(Entity $$0) {
      boolean var10000;
      label17: {
         if ($$0 instanceof Player $$1 && $$1.getAbilities().flying) {
            var10000 = true;
            break label17;
         }

         var10000 = false;
      }

      boolean $$2 = var10000;
      return $$2 ? 0.0F : this.knockbackMultiplier.orElseGet(() -> super.getKnockbackMultiplier($$0));
   }
}
