package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class HoeItem extends Item {
   protected static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES = Maps.<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>>newHashMap(
      ImmutableMap.of(
         Blocks.GRASS_BLOCK,
         Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())),
         Blocks.DIRT_PATH,
         Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())),
         Blocks.DIRT,
         Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())),
         Blocks.COARSE_DIRT,
         Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.DIRT.defaultBlockState())),
         Blocks.ROOTED_DIRT,
         Pair.of((Predicate)$$0 -> true, changeIntoStateAndDropItem(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))
      )
   );

   public HoeItem(ToolMaterial $$0, float $$1, float $$2, Item.Properties $$3) {
      super($$3.hoe($$0, $$1, $$2));
   }

   @Override
   public InteractionResult useOn(UseOnContext $$0) {
      Level $$1 = $$0.getLevel();
      BlockPos $$2 = $$0.getClickedPos();
      Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> $$3 = (Pair)TILLABLES.get($$1.getBlockState($$2).getBlock());
      if ($$3 == null) {
         return InteractionResult.PASS;
      } else {
         Predicate<UseOnContext> $$4 = (Predicate)$$3.getFirst();
         Consumer<UseOnContext> $$5 = (Consumer)$$3.getSecond();
         if ($$4.test($$0)) {
            Player $$6 = $$0.getPlayer();
            $$1.playSound($$6, $$2, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!$$1.isClientSide()) {
               $$5.accept($$0);
               if ($$6 != null) {
                  $$0.getItemInHand().hurtAndBreak(1, $$6, $$0.getHand().asEquipmentSlot());
               }
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public static Consumer<UseOnContext> changeIntoState(BlockState $$0) {
      return $$1 -> {
         $$1.getLevel().setBlock($$1.getClickedPos(), $$0, 11);
         $$1.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, $$1.getClickedPos(), GameEvent.Context.of($$1.getPlayer(), $$0));
      };
   }

   public static Consumer<UseOnContext> changeIntoStateAndDropItem(BlockState $$0, ItemLike $$1) {
      return $$2 -> {
         $$2.getLevel().setBlock($$2.getClickedPos(), $$0, 11);
         $$2.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, $$2.getClickedPos(), GameEvent.Context.of($$2.getPlayer(), $$0));
         Block.popResourceFromFace($$2.getLevel(), $$2.getClickedPos(), $$2.getClickedFace(), new ItemStack($$1));
      };
   }

   public static boolean onlyIfAirAbove(UseOnContext $$0) {
      return $$0.getClickedFace() != Direction.DOWN && $$0.getLevel().getBlockState($$0.getClickedPos().above()).isAir();
   }
}
