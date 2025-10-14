package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument implements ArgumentType<ParticleOptions> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle{foo:bar}");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("particle.notFound", $$0)
   );
   public static final DynamicCommandExceptionType ERROR_INVALID_OPTIONS = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("particle.invalidOptions", $$0)
   );
   private final HolderLookup.Provider registries;
   private static final TagParser<?> VALUE_PARSER = TagParser.create(NbtOps.INSTANCE);

   public ParticleArgument(CommandBuildContext $$0) {
      this.registries = $$0;
   }

   public static ParticleArgument particle(CommandBuildContext $$0) {
      return new ParticleArgument($$0);
   }

   public static ParticleOptions getParticle(CommandContext<CommandSourceStack> $$0, String $$1) {
      return $$0.getArgument($$1, ParticleOptions.class);
   }

   public ParticleOptions parse(StringReader $$0) throws CommandSyntaxException {
      return readParticle($$0, this.registries);
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static ParticleOptions readParticle(StringReader $$0, HolderLookup.Provider $$1) throws CommandSyntaxException {
      ParticleType<?> $$2 = readParticleType($$0, $$1.lookupOrThrow(Registries.PARTICLE_TYPE));
      return readParticle(VALUE_PARSER, $$0, $$2, $$1);
   }

   private static ParticleType<?> readParticleType(StringReader $$0, HolderLookup<ParticleType<?>> $$1) throws CommandSyntaxException {
      ResourceLocation $$2 = ResourceLocation.read($$0);
      ResourceKey<ParticleType<?>> $$3 = ResourceKey.create(Registries.PARTICLE_TYPE, $$2);
      return (ParticleType<?>)((Holder.Reference)$$1.get($$3).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.createWithContext($$0, $$2))).value();
   }

   private static <T extends ParticleOptions, O> T readParticle(TagParser<O> $$0, StringReader $$1, ParticleType<T> $$2, HolderLookup.Provider $$3) throws CommandSyntaxException {
      RegistryOps<O> $$4 = $$3.createSerializationContext($$0.getOps());
      O $$5;
      if ($$1.canRead() && $$1.peek() == '{') {
         $$5 = $$0.parseAsArgument($$1);
      } else {
         $$5 = $$4.emptyMap();
      }

      return $$2.codec().codec().parse($$4, $$5).getOrThrow(ERROR_INVALID_OPTIONS::create);
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
      HolderLookup.RegistryLookup<ParticleType<?>> $$2 = this.registries.lookupOrThrow(Registries.PARTICLE_TYPE);
      return SharedSuggestionProvider.suggestResource($$2.listElementIds().map(ResourceKey::location), $$1);
   }
}
