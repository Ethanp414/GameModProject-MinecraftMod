package net.minecraft.world.item;

import com.google.common.collect.ImmutableBiMap;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopperBlocks;

public record WeatheringCopperItems(
   Item unaffected, Item exposed, Item weathered, Item oxidized, Item waxed, Item waxedExposed, Item waxedWeathered, Item waxedOxidized
) {
   public static WeatheringCopperItems create(WeatheringCopperBlocks $$0, Function<Block, Item> $$1) {
      return new WeatheringCopperItems(
         (Item)$$1.apply($$0.unaffected()),
         (Item)$$1.apply($$0.exposed()),
         (Item)$$1.apply($$0.weathered()),
         (Item)$$1.apply($$0.oxidized()),
         (Item)$$1.apply($$0.waxed()),
         (Item)$$1.apply($$0.waxedExposed()),
         (Item)$$1.apply($$0.waxedWeathered()),
         (Item)$$1.apply($$0.waxedOxidized())
      );
   }

   public ImmutableBiMap<Item, Item> waxedMapping() {
      return ImmutableBiMap.of(
         this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered, this.oxidized, this.waxedOxidized
      );
   }

   public void forEach(Consumer<Item> $$0) {
      $$0.accept(this.unaffected);
      $$0.accept(this.exposed);
      $$0.accept(this.weathered);
      $$0.accept(this.oxidized);
      $$0.accept(this.waxed);
      $$0.accept(this.waxedExposed);
      $$0.accept(this.waxedWeathered);
      $$0.accept(this.waxedOxidized);
   }
}
