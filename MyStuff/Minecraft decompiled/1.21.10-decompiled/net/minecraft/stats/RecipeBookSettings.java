package net.minecraft.stats;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.function.UnaryOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.RecipeBookType;

public final class RecipeBookSettings {
   public static final StreamCodec<FriendlyByteBuf, RecipeBookSettings> STREAM_CODEC = StreamCodec.composite(
      RecipeBookSettings.TypeSettings.STREAM_CODEC,
      $$0 -> $$0.crafting,
      RecipeBookSettings.TypeSettings.STREAM_CODEC,
      $$0 -> $$0.furnace,
      RecipeBookSettings.TypeSettings.STREAM_CODEC,
      $$0 -> $$0.blastFurnace,
      RecipeBookSettings.TypeSettings.STREAM_CODEC,
      $$0 -> $$0.smoker,
      RecipeBookSettings::new
   );
   public static final MapCodec<RecipeBookSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               RecipeBookSettings.TypeSettings.CRAFTING_MAP_CODEC.forGetter($$0x -> $$0x.crafting),
               RecipeBookSettings.TypeSettings.FURNACE_MAP_CODEC.forGetter($$0x -> $$0x.furnace),
               RecipeBookSettings.TypeSettings.BLAST_FURNACE_MAP_CODEC.forGetter($$0x -> $$0x.blastFurnace),
               RecipeBookSettings.TypeSettings.SMOKER_MAP_CODEC.forGetter($$0x -> $$0x.smoker)
            )
            .apply($$0, RecipeBookSettings::new)
   );
   private RecipeBookSettings.TypeSettings crafting;
   private RecipeBookSettings.TypeSettings furnace;
   private RecipeBookSettings.TypeSettings blastFurnace;
   private RecipeBookSettings.TypeSettings smoker;

   public RecipeBookSettings() {
      this(
         RecipeBookSettings.TypeSettings.DEFAULT,
         RecipeBookSettings.TypeSettings.DEFAULT,
         RecipeBookSettings.TypeSettings.DEFAULT,
         RecipeBookSettings.TypeSettings.DEFAULT
      );
   }

   private RecipeBookSettings(
      RecipeBookSettings.TypeSettings $$0, RecipeBookSettings.TypeSettings $$1, RecipeBookSettings.TypeSettings $$2, RecipeBookSettings.TypeSettings $$3
   ) {
      this.crafting = $$0;
      this.furnace = $$1;
      this.blastFurnace = $$2;
      this.smoker = $$3;
   }

   @VisibleForTesting
   public RecipeBookSettings.TypeSettings getSettings(RecipeBookType $$0) {
      return switch($$0) {
         case CRAFTING -> this.crafting;
         case FURNACE -> this.furnace;
         case BLAST_FURNACE -> this.blastFurnace;
         case SMOKER -> this.smoker;
         default -> throw new MatchException(null, null);
      };
   }

   private void updateSettings(RecipeBookType $$0, UnaryOperator<RecipeBookSettings.TypeSettings> $$1) {
      switch($$0) {
         case CRAFTING:
            this.crafting = (RecipeBookSettings.TypeSettings)$$1.apply(this.crafting);
            break;
         case FURNACE:
            this.furnace = (RecipeBookSettings.TypeSettings)$$1.apply(this.furnace);
            break;
         case BLAST_FURNACE:
            this.blastFurnace = (RecipeBookSettings.TypeSettings)$$1.apply(this.blastFurnace);
            break;
         case SMOKER:
            this.smoker = (RecipeBookSettings.TypeSettings)$$1.apply(this.smoker);
      }
   }

   public boolean isOpen(RecipeBookType $$0) {
      return this.getSettings($$0).open;
   }

   public void setOpen(RecipeBookType $$0, boolean $$1) {
      this.updateSettings($$0, $$1x -> $$1x.setOpen($$1));
   }

   public boolean isFiltering(RecipeBookType $$0) {
      return this.getSettings($$0).filtering;
   }

   public void setFiltering(RecipeBookType $$0, boolean $$1) {
      this.updateSettings($$0, $$1x -> $$1x.setFiltering($$1));
   }

   public RecipeBookSettings copy() {
      return new RecipeBookSettings(this.crafting, this.furnace, this.blastFurnace, this.smoker);
   }

   public void replaceFrom(RecipeBookSettings $$0) {
      this.crafting = $$0.crafting;
      this.furnace = $$0.furnace;
      this.blastFurnace = $$0.blastFurnace;
      this.smoker = $$0.smoker;
   }

   public static record TypeSettings(boolean open, boolean filtering) {
      final boolean open;
      final boolean filtering;
      public static final RecipeBookSettings.TypeSettings DEFAULT = new RecipeBookSettings.TypeSettings(false, false);
      public static final MapCodec<RecipeBookSettings.TypeSettings> CRAFTING_MAP_CODEC = codec("isGuiOpen", "isFilteringCraftable");
      public static final MapCodec<RecipeBookSettings.TypeSettings> FURNACE_MAP_CODEC = codec("isFurnaceGuiOpen", "isFurnaceFilteringCraftable");
      public static final MapCodec<RecipeBookSettings.TypeSettings> BLAST_FURNACE_MAP_CODEC = codec(
         "isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"
      );
      public static final MapCodec<RecipeBookSettings.TypeSettings> SMOKER_MAP_CODEC = codec("isSmokerGuiOpen", "isSmokerFilteringCraftable");
      public static final StreamCodec<ByteBuf, RecipeBookSettings.TypeSettings> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.BOOL,
         RecipeBookSettings.TypeSettings::open,
         ByteBufCodecs.BOOL,
         RecipeBookSettings.TypeSettings::filtering,
         RecipeBookSettings.TypeSettings::new
      );

      public String toString() {
         return "[open=" + this.open + ", filtering=" + this.filtering + "]";
      }

      public RecipeBookSettings.TypeSettings setOpen(boolean $$0) {
         return new RecipeBookSettings.TypeSettings($$0, this.filtering);
      }

      public RecipeBookSettings.TypeSettings setFiltering(boolean $$0) {
         return new RecipeBookSettings.TypeSettings(this.open, $$0);
      }

      private static MapCodec<RecipeBookSettings.TypeSettings> codec(String $$0, String $$1) {
         return RecordCodecBuilder.mapCodec(
            $$2 -> $$2.group(
                     Codec.BOOL.optionalFieldOf($$0, Boolean.valueOf(false)).forGetter(RecipeBookSettings.TypeSettings::open),
                     Codec.BOOL.optionalFieldOf($$1, Boolean.valueOf(false)).forGetter(RecipeBookSettings.TypeSettings::filtering)
                  )
                  .apply($$2, RecipeBookSettings.TypeSettings::new)
         );
      }
   }
}
