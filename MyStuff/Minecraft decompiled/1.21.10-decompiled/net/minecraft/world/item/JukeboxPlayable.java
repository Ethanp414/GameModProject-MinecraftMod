package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public record JukeboxPlayable(EitherHolder<JukeboxSong> song) implements TooltipProvider {
   public static final Codec<JukeboxPlayable> CODEC = EitherHolder.codec(Registries.JUKEBOX_SONG, JukeboxSong.CODEC)
      .xmap(JukeboxPlayable::new, JukeboxPlayable::song);
   public static final StreamCodec<RegistryFriendlyByteBuf, JukeboxPlayable> STREAM_CODEC = StreamCodec.composite(
      EitherHolder.streamCodec(Registries.JUKEBOX_SONG, JukeboxSong.STREAM_CODEC), JukeboxPlayable::song, JukeboxPlayable::new
   );

   @Override
   public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
      HolderLookup.Provider $$4 = $$0.registries();
      if ($$4 != null) {
         this.song.unwrap($$4).ifPresent($$1x -> {
            MutableComponent $$2xx = ((JukeboxSong)$$1x.value()).description().copy();
            ComponentUtils.mergeStyles($$2xx, Style.EMPTY.withColor(ChatFormatting.GRAY));
            $$1.accept($$2xx);
         });
      }
   }

   public static InteractionResult tryInsertIntoJukebox(Level $$0, BlockPos $$1, ItemStack $$2, Player $$3) {
      JukeboxPlayable $$4 = $$2.get(DataComponents.JUKEBOX_PLAYABLE);
      if ($$4 == null) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         BlockState $$5 = $$0.getBlockState($$1);
         if ($$5.is(Blocks.JUKEBOX) && !$$5.getValue(JukeboxBlock.HAS_RECORD)) {
            if (!$$0.isClientSide()) {
               ItemStack $$6 = $$2.consumeAndReturn(1, $$3);
               BlockEntity var8 = $$0.getBlockEntity($$1);
               if (var8 instanceof JukeboxBlockEntity $$7) {
                  $$7.setTheItem($$6);
                  $$0.gameEvent(GameEvent.BLOCK_CHANGE, $$1, GameEvent.Context.of($$3, $$5));
               }

               $$3.awardStat(Stats.PLAY_RECORD);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         }
      }
   }
}
