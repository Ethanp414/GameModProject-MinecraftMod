package net.minecraft.world.waypoints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.scores.PlayerTeam;

public interface Waypoint {
   int MAX_RANGE = 60000000;
   AttributeModifier WAYPOINT_TRANSMIT_RANGE_HIDE_MODIFIER = new AttributeModifier(
      ResourceLocation.withDefaultNamespace("waypoint_transmit_range_hide"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
   );

   static Item.Properties addHideAttribute(Item.Properties $$0) {
      return $$0.component(
         DataComponents.ATTRIBUTE_MODIFIERS,
         ItemAttributeModifiers.builder()
            .add(Attributes.WAYPOINT_TRANSMIT_RANGE, WAYPOINT_TRANSMIT_RANGE_HIDE_MODIFIER, EquipmentSlotGroup.HEAD, ItemAttributeModifiers.Display.hidden())
            .build()
      );
   }

   public static class Icon {
      public static final Codec<Waypoint.Icon> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  ResourceKey.codec(WaypointStyleAssets.ROOT_ID).fieldOf("style").forGetter($$0x -> $$0x.style),
                  ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color").forGetter($$0x -> $$0x.color)
               )
               .apply($$0, Waypoint.Icon::new)
      );
      public static final StreamCodec<ByteBuf, Waypoint.Icon> STREAM_CODEC = StreamCodec.composite(
         ResourceKey.streamCodec(WaypointStyleAssets.ROOT_ID),
         $$0 -> $$0.style,
         ByteBufCodecs.optional(ByteBufCodecs.RGB_COLOR),
         $$0 -> $$0.color,
         Waypoint.Icon::new
      );
      public static final Waypoint.Icon NULL = new Waypoint.Icon();
      public ResourceKey<WaypointStyleAsset> style = WaypointStyleAssets.DEFAULT;
      public Optional<Integer> color = Optional.empty();

      public Icon() {
      }

      private Icon(ResourceKey<WaypointStyleAsset> $$0, Optional<Integer> $$1) {
         this.style = $$0;
         this.color = $$1;
      }

      public boolean hasData() {
         return this.style != WaypointStyleAssets.DEFAULT || this.color.isPresent();
      }

      public Waypoint.Icon cloneAndAssignStyle(LivingEntity $$0) {
         ResourceKey<WaypointStyleAsset> $$1 = this.getOverrideStyle();
         Optional<Integer> $$2 = this.color
            .or(() -> Optional.ofNullable($$0.getTeam()).map($$0xx -> $$0xx.getColor().getColor()).map($$0xx -> $$0xx == 0 ? -13619152 : $$0xx));
         return $$1 == this.style && $$2.isEmpty() ? this : new Waypoint.Icon($$1, $$2);
      }

      private ResourceKey<WaypointStyleAsset> getOverrideStyle() {
         return this.style != WaypointStyleAssets.DEFAULT ? this.style : WaypointStyleAssets.DEFAULT;
      }
   }
}
