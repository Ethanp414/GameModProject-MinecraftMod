package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BucketItem extends Item implements DispensibleContainerItem {
   private final Fluid content;

   public BucketItem(Fluid $$0, Item.Properties $$1) {
      super($$1);
      this.content = $$0;
   }

   @Override
   public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
      ItemStack $$3 = $$1.getItemInHand($$2);
      BlockHitResult $$4 = getPlayerPOVHitResult($$0, $$1, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
      if ($$4.getType() == HitResult.Type.MISS) {
         return InteractionResult.PASS;
      } else if ($$4.getType() != HitResult.Type.BLOCK) {
         return InteractionResult.PASS;
      } else {
         BlockPos $$5 = $$4.getBlockPos();
         Direction $$6 = $$4.getDirection();
         BlockPos $$7 = $$5.relative($$6);
         if (!$$0.mayInteract($$1, $$5) || !$$1.mayUseItemAt($$7, $$6, $$3)) {
            return InteractionResult.FAIL;
         } else if (this.content == Fluids.EMPTY) {
            BlockState $$8 = $$0.getBlockState($$5);
            Block var15 = $$8.getBlock();
            if (var15 instanceof BucketPickup $$9) {
               ItemStack $$10 = $$9.pickupBlock($$1, $$0, $$5, $$8);
               if (!$$10.isEmpty()) {
                  $$1.awardStat(Stats.ITEM_USED.get(this));
                  $$9.getPickupSound().ifPresent($$1x -> $$1.playSound($$1x, 1.0F, 1.0F));
                  $$0.gameEvent($$1, GameEvent.FLUID_PICKUP, $$5);
                  ItemStack $$11 = ItemUtils.createFilledResult($$3, $$1, $$10);
                  if (!$$0.isClientSide()) {
                     CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)$$1, $$10);
                  }

                  return InteractionResult.SUCCESS.heldItemTransformedTo($$11);
               }
            }

            return InteractionResult.FAIL;
         } else {
            BlockState $$12 = $$0.getBlockState($$5);
            BlockPos $$13 = $$12.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? $$5 : $$7;
            if (this.emptyContents($$1, $$0, $$13, $$4)) {
               this.checkExtraContent($$1, $$0, $$3, $$13);
               if ($$1 instanceof ServerPlayer) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)$$1, $$13, $$3);
               }

               $$1.awardStat(Stats.ITEM_USED.get(this));
               ItemStack $$14 = ItemUtils.createFilledResult($$3, $$1, getEmptySuccessItem($$3, $$1));
               return InteractionResult.SUCCESS.heldItemTransformedTo($$14);
            } else {
               return InteractionResult.FAIL;
            }
         }
      }
   }

   public static ItemStack getEmptySuccessItem(ItemStack $$0, Player $$1) {
      return !$$1.hasInfiniteMaterials() ? new ItemStack(Items.BUCKET) : $$0;
   }

   @Override
   public void checkExtraContent(@Nullable LivingEntity $$0, Level $$1, ItemStack $$2, BlockPos $$3) {
   }

   @Override
   public boolean emptyContents(@Nullable LivingEntity $$0, Level $$1, BlockPos $$2, @Nullable BlockHitResult $$3) {
      Fluid $$6 = this.content;
      if (!($$6 instanceof FlowingFluid)) {
         return false;
      } else {
         FlowingFluid $$4;
         Block $$7;
         boolean $$8;
         boolean $$9;
         boolean var10000;
         label106: {
            $$4 = (FlowingFluid)$$6;
            $$6 = $$1.getBlockState($$2);
            $$7 = $$6.getBlock();
            $$8 = $$6.canBeReplaced(this.content);
            $$9 = $$0 != null && $$0.isShiftKeyDown();
            label88:
            if (!$$8) {
               if ($$7 instanceof LiquidBlockContainer $$10 && $$10.canPlaceLiquid($$0, $$1, $$2, $$6, this.content)) {
                  break label88;
               }

               var10000 = false;
               break label106;
            }

            var10000 = true;
         }

         boolean $$11 = var10000;
         boolean $$12 = $$6.isAir() || $$11 && (!$$9 || $$3 == null);
         if (!$$12) {
            return $$3 != null && this.emptyContents($$0, $$1, $$3.getBlockPos().relative($$3.getDirection()), null);
         } else if ($$1.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
            int $$13 = $$2.getX();
            int $$14 = $$2.getY();
            int $$15 = $$2.getZ();
            $$1.playSound($$0, $$2, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + ($$1.random.nextFloat() - $$1.random.nextFloat()) * 0.8F);

            for(int $$16 = 0; $$16 < 8; ++$$16) {
               $$1.addParticle(
                  ParticleTypes.LARGE_SMOKE, (double)$$13 + Math.random(), (double)$$14 + Math.random(), (double)$$15 + Math.random(), 0.0, 0.0, 0.0
               );
            }

            return true;
         } else {
            if ($$7 instanceof LiquidBlockContainer $$17 && this.content == Fluids.WATER) {
               $$17.placeLiquid($$1, $$2, $$6, $$4.getSource(false));
               this.playEmptySound($$0, $$1, $$2);
               return true;
            }

            if (!$$1.isClientSide() && $$8 && !$$6.liquid()) {
               $$1.destroyBlock($$2, true);
            }

            if (!$$1.setBlock($$2, this.content.defaultFluidState().createLegacyBlock(), 11) && !$$6.getFluidState().isSource()) {
               return false;
            } else {
               this.playEmptySound($$0, $$1, $$2);
               return true;
            }
         }
      }
   }

   protected void playEmptySound(@Nullable LivingEntity $$0, LevelAccessor $$1, BlockPos $$2) {
      SoundEvent $$3 = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
      $$1.playSound($$0, $$2, $$3, SoundSource.BLOCKS, 1.0F, 1.0F);
      $$1.gameEvent($$0, GameEvent.FLUID_PLACE, $$2);
   }

   public Fluid getContent() {
      return this.content;
   }
}
