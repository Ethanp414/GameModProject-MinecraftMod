package net.minecraft.server.dialog.input;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class InputControlTypes {
   public static MapCodec<? extends InputControl> bootstrap(Registry<MapCodec<? extends InputControl>> $$0) {
      Registry.register($$0, ResourceLocation.withDefaultNamespace("boolean"), BooleanInput.MAP_CODEC);
      Registry.register($$0, ResourceLocation.withDefaultNamespace("number_range"), NumberRangeInput.MAP_CODEC);
      Registry.register($$0, ResourceLocation.withDefaultNamespace("single_option"), SingleOptionInput.MAP_CODEC);
      return Registry.register($$0, ResourceLocation.withDefaultNamespace("text"), TextInput.MAP_CODEC);
   }
}
