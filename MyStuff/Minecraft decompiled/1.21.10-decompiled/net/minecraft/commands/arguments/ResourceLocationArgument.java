package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");

   public static ResourceLocationArgument id() {
      return new ResourceLocationArgument();
   }

   public static ResourceLocation getId(CommandContext<CommandSourceStack> $$0, String $$1) {
      return $$0.getArgument($$1, ResourceLocation.class);
   }

   public ResourceLocation parse(StringReader $$0) throws CommandSyntaxException {
      return ResourceLocation.read($$0);
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
