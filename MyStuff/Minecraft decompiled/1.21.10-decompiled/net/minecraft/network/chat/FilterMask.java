package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.BitSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.apache.commons.lang3.StringUtils;

public class FilterMask {
   public static final Codec<FilterMask> CODEC = StringRepresentable.fromEnum(FilterMask.Type::values).dispatch(FilterMask::type, FilterMask.Type::codec);
   public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMask.Type.FULLY_FILTERED);
   public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterMask.Type.PASS_THROUGH);
   public static final Style FILTERED_STYLE = Style.EMPTY
      .withColor(ChatFormatting.DARK_GRAY)
      .withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.filtered")));
   static final MapCodec<FilterMask> PASS_THROUGH_CODEC = MapCodec.unit(PASS_THROUGH);
   static final MapCodec<FilterMask> FULLY_FILTERED_CODEC = MapCodec.unit(FULLY_FILTERED);
   static final MapCodec<FilterMask> PARTIALLY_FILTERED_CODEC = ExtraCodecs.BIT_SET.<FilterMask>xmap(FilterMask::new, FilterMask::mask).fieldOf("value");
   private static final char HASH = '#';
   private final BitSet mask;
   private final FilterMask.Type type;

   private FilterMask(BitSet $$0, FilterMask.Type $$1) {
      this.mask = $$0;
      this.type = $$1;
   }

   private FilterMask(BitSet $$0) {
      this.mask = $$0;
      this.type = FilterMask.Type.PARTIALLY_FILTERED;
   }

   public FilterMask(int $$0) {
      this(new BitSet($$0), FilterMask.Type.PARTIALLY_FILTERED);
   }

   private FilterMask.Type type() {
      return this.type;
   }

   private BitSet mask() {
      return this.mask;
   }

   public static FilterMask read(FriendlyByteBuf $$0) {
      FilterMask.Type $$1 = $$0.readEnum(FilterMask.Type.class);

      return switch($$1.ordinal()) {
         case 0 -> PASS_THROUGH;
         case 1 -> FULLY_FILTERED;
         case 2 -> new FilterMask($$0.readBitSet(), FilterMask.Type.PARTIALLY_FILTERED);
         default -> throw new MatchException(null, null);
      };
   }

   public static void write(FriendlyByteBuf $$0, FilterMask $$1) {
      $$0.writeEnum($$1.type);
      if ($$1.type == FilterMask.Type.PARTIALLY_FILTERED) {
         $$0.writeBitSet($$1.mask);
      }
   }

   public void setFiltered(int $$0) {
      this.mask.set($$0);
   }

   @Nullable
   public String apply(String $$0) {
      return switch(this.type.ordinal()) {
         case 0 -> $$0;
         case 1 -> null;
         case 2 -> {
            char[] $$1 = $$0.toCharArray();

            for(int $$2 = 0; $$2 < $$1.length && $$2 < this.mask.length(); ++$$2) {
               if (this.mask.get($$2)) {
                  $$1[$$2] = '#';
               }
            }

            yield new String($$1);
         }
         default -> throw new MatchException(null, null);
      };
   }

   @Nullable
   public Component applyWithFormatting(String $$0) {
      return switch(this.type.ordinal()) {
         case 0 -> Component.literal($$0);
         case 1 -> null;
         case 2 -> {
            MutableComponent $$1 = Component.empty();
            int $$2 = 0;
            boolean $$3 = this.mask.get(0);

            while(true) {
               int $$4 = $$3 ? this.mask.nextClearBit($$2) : this.mask.nextSetBit($$2);
               $$4 = $$4 < 0 ? $$0.length() : $$4;
               if ($$4 == $$2) {
                  yield $$1;
               }

               if ($$3) {
                  $$1.append(Component.literal(StringUtils.repeat('#', $$4 - $$2)).withStyle(FILTERED_STYLE));
               } else {
                  $$1.append($$0.substring($$2, $$4));
               }

               $$3 = !$$3;
               $$2 = $$4;
            }
         }
         default -> throw new MatchException(null, null);
      };
   }

   public boolean isEmpty() {
      return this.type == FilterMask.Type.PASS_THROUGH;
   }

   public boolean isFullyFiltered() {
      return this.type == FilterMask.Type.FULLY_FILTERED;
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else if ($$0 != null && this.getClass() == $$0.getClass()) {
         FilterMask $$1 = (FilterMask)$$0;
         return this.mask.equals($$1.mask) && this.type == $$1.type;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int $$0 = this.mask.hashCode();
      return 31 * $$0 + this.type.hashCode();
   }

   static enum Type implements StringRepresentable {
      PASS_THROUGH("pass_through", () -> FilterMask.PASS_THROUGH_CODEC),
      FULLY_FILTERED("fully_filtered", () -> FilterMask.FULLY_FILTERED_CODEC),
      PARTIALLY_FILTERED("partially_filtered", () -> FilterMask.PARTIALLY_FILTERED_CODEC);

      private final String serializedName;
      private final Supplier<MapCodec<FilterMask>> codec;

      private Type(final String param3, final Supplier<MapCodec<FilterMask>> param4) {
         this.serializedName = $$0;
         this.codec = $$1;
      }

      @Override
      public String getSerializedName() {
         return this.serializedName;
      }

      private MapCodec<FilterMask> codec() {
         return (MapCodec<FilterMask>)this.codec.get();
      }
   }
}
