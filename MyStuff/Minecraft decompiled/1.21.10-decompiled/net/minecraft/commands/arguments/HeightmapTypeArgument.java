package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap;

public class HeightmapTypeArgument extends StringRepresentableArgument<Heightmap.Types> {
   private static final Codec<Heightmap.Types> LOWER_CASE_CODEC = StringRepresentable.fromEnumWithMapping(
      HeightmapTypeArgument::keptTypes, $$0 -> $$0.toLowerCase(Locale.ROOT)
   );

   private static Heightmap.Types[] keptTypes() {
      return (Heightmap.Types[])Arrays.stream(Heightmap.Types.values()).filter(Heightmap.Types::keepAfterWorldgen).toArray($$0 -> new Heightmap.Types[$$0]);
   }

   private HeightmapTypeArgument() {
      super(LOWER_CASE_CODEC, HeightmapTypeArgument::keptTypes);
   }

   public static HeightmapTypeArgument heightmap() {
      return new HeightmapTypeArgument();
   }

   public static Heightmap.Types getHeightmap(CommandContext<CommandSourceStack> $$0, String $$1) {
      return $$0.getArgument($$1, Heightmap.Types.class);
   }

   @Override
   protected String convertId(String $$0) {
      return $$0.toLowerCase(Locale.ROOT);
   }
}
