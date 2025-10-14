package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.ResourceLocation;

public record CustomAll(ResourceLocation id, Optional<CompoundTag> additions) implements Action {
   public static final MapCodec<CustomAll> MAP_CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               ResourceLocation.CODEC.fieldOf("id").forGetter(CustomAll::id), CompoundTag.CODEC.optionalFieldOf("additions").forGetter(CustomAll::additions)
            )
            .apply($$0, CustomAll::new)
   );

   @Override
   public MapCodec<CustomAll> codec() {
      return MAP_CODEC;
   }

   @Override
   public Optional<ClickEvent> createAction(Map<String, Action.ValueGetter> $$0) {
      CompoundTag $$1 = (CompoundTag)this.additions.map(CompoundTag::copy).orElseGet(CompoundTag::new);
      $$0.forEach(($$1x, $$2) -> $$1.put($$1x, $$2.asTag()));
      return Optional.of(new ClickEvent.Custom(this.id, Optional.of($$1)));
   }
}
