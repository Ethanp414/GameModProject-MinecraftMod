package net.minecraft.world.damagesource;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record FallLocation(String id) {
   public static final FallLocation GENERIC = new FallLocation("generic");
   public static final FallLocation LADDER = new FallLocation("ladder");
   public static final FallLocation VINES = new FallLocation("vines");
   public static final FallLocation WEEPING_VINES = new FallLocation("weeping_vines");
   public static final FallLocation TWISTING_VINES = new FallLocation("twisting_vines");
   public static final FallLocation SCAFFOLDING = new FallLocation("scaffolding");
   public static final FallLocation OTHER_CLIMBABLE = new FallLocation("other_climbable");
   public static final FallLocation WATER = new FallLocation("water");

   public static FallLocation blockToFallLocation(BlockState $$0) {
      if ($$0.is(Blocks.LADDER) || $$0.is(BlockTags.TRAPDOORS)) {
         return LADDER;
      } else if ($$0.is(Blocks.VINE)) {
         return VINES;
      } else if ($$0.is(Blocks.WEEPING_VINES) || $$0.is(Blocks.WEEPING_VINES_PLANT)) {
         return WEEPING_VINES;
      } else if ($$0.is(Blocks.TWISTING_VINES) || $$0.is(Blocks.TWISTING_VINES_PLANT)) {
         return TWISTING_VINES;
      } else {
         return $$0.is(Blocks.SCAFFOLDING) ? SCAFFOLDING : OTHER_CLIMBABLE;
      }
   }

   @Nullable
   public static FallLocation getCurrentFallLocation(LivingEntity $$0) {
      Optional<BlockPos> $$1 = $$0.getLastClimbablePos();
      if ($$1.isPresent()) {
         BlockState $$2 = $$0.level().getBlockState((BlockPos)$$1.get());
         return blockToFallLocation($$2);
      } else {
         return $$0.isInWater() ? WATER : null;
      }
   }

   public String languageKey() {
      return "death.fell.accident." + this.id;
   }
}
