package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public record BlockItemStateProperties(Map<String, String> properties) implements TooltipProvider {
   public static final BlockItemStateProperties EMPTY = new BlockItemStateProperties(Map.of());
   public static final Codec<BlockItemStateProperties> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING)
      .xmap(BlockItemStateProperties::new, BlockItemStateProperties::properties);
   private static final StreamCodec<ByteBuf, Map<String, String>> PROPERTIES_STREAM_CODEC = ByteBufCodecs.map(
      Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8
   );
   public static final StreamCodec<ByteBuf, BlockItemStateProperties> STREAM_CODEC = PROPERTIES_STREAM_CODEC.map(
      BlockItemStateProperties::new, BlockItemStateProperties::properties
   );

   public <T extends Comparable<T>> BlockItemStateProperties with(Property<T> $$0, T $$1) {
      return new BlockItemStateProperties(Util.copyAndPut(this.properties, $$0.getName(), $$0.getName($$1)));
   }

   public <T extends Comparable<T>> BlockItemStateProperties with(Property<T> $$0, BlockState $$1) {
      return this.with($$0, $$1.getValue($$0));
   }

   @Nullable
   public <T extends Comparable<T>> T get(Property<T> $$0) {
      String $$1 = (String)this.properties.get($$0.getName());
      return (T)($$1 == null ? null : $$0.getValue($$1).orElse(null));
   }

   public BlockState apply(BlockState $$0) {
      StateDefinition<Block, BlockState> $$1 = $$0.getBlock().getStateDefinition();

      for(Entry<String, String> $$2 : this.properties.entrySet()) {
         Property<?> $$3 = $$1.getProperty((String)$$2.getKey());
         if ($$3 != null) {
            $$0 = updateState($$0, $$3, (String)$$2.getValue());
         }
      }

      return $$0;
   }

   private static <T extends Comparable<T>> BlockState updateState(BlockState $$0, Property<T> $$1, String $$2) {
      return (BlockState)$$1.getValue($$2).map($$2x -> $$0.setValue($$1, $$2x)).orElse($$0);
   }

   public boolean isEmpty() {
      return this.properties.isEmpty();
   }

   @Override
   public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
      Integer $$4 = this.get(BeehiveBlock.HONEY_LEVEL);
      if ($$4 != null) {
         $$1.accept(Component.translatable("container.beehive.honey", $$4, 5).withStyle(ChatFormatting.GRAY));
      }
   }
}
