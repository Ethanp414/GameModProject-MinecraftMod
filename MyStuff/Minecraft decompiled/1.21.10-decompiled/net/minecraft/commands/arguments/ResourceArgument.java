package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ResourceArgument<T> implements ArgumentType<Holder.Reference<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_NOT_SUMMONABLE_ENTITY = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("entity.not_summonable", $$0)
   );
   public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_RESOURCE = new Dynamic2CommandExceptionType(
      ($$0, $$1) -> Component.translatableEscape("argument.resource.not_found", $$0, $$1)
   );
   public static final Dynamic3CommandExceptionType ERROR_INVALID_RESOURCE_TYPE = new Dynamic3CommandExceptionType(
      ($$0, $$1, $$2) -> Component.translatableEscape("argument.resource.invalid_type", $$0, $$1, $$2)
   );
   final ResourceKey<? extends Registry<T>> registryKey;
   private final HolderLookup<T> registryLookup;

   public ResourceArgument(CommandBuildContext $$0, ResourceKey<? extends Registry<T>> $$1) {
      this.registryKey = $$1;
      this.registryLookup = $$0.lookupOrThrow($$1);
   }

   public static <T> ResourceArgument<T> resource(CommandBuildContext $$0, ResourceKey<? extends Registry<T>> $$1) {
      return new ResourceArgument<>($$0, $$1);
   }

   public static <T> Holder.Reference<T> getResource(CommandContext<CommandSourceStack> $$0, String $$1, ResourceKey<Registry<T>> $$2) throws CommandSyntaxException {
      Holder.Reference<T> $$3 = $$0.getArgument($$1, Holder.Reference.class);
      ResourceKey<?> $$4 = $$3.key();
      if ($$4.isFor($$2)) {
         return $$3;
      } else {
         throw ERROR_INVALID_RESOURCE_TYPE.create($$4.location(), $$4.registry(), $$2.location());
      }
   }

   public static Holder.Reference<Attribute> getAttribute(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return getResource($$0, $$1, Registries.ATTRIBUTE);
   }

   public static Holder.Reference<ConfiguredFeature<?, ?>> getConfiguredFeature(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return getResource($$0, $$1, Registries.CONFIGURED_FEATURE);
   }

   public static Holder.Reference<Structure> getStructure(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return getResource($$0, $$1, Registries.STRUCTURE);
   }

   public static Holder.Reference<EntityType<?>> getEntityType(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return getResource($$0, $$1, Registries.ENTITY_TYPE);
   }

   public static Holder.Reference<EntityType<?>> getSummonableEntityType(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      Holder.Reference<EntityType<?>> $$2 = getResource($$0, $$1, Registries.ENTITY_TYPE);
      if (!$$2.value().canSummon()) {
         throw ERROR_NOT_SUMMONABLE_ENTITY.create($$2.key().location().toString());
      } else {
         return $$2;
      }
   }

   public static Holder.Reference<MobEffect> getMobEffect(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return getResource($$0, $$1, Registries.MOB_EFFECT);
   }

   public static Holder.Reference<Enchantment> getEnchantment(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return getResource($$0, $$1, Registries.ENCHANTMENT);
   }

   public Holder.Reference<T> parse(StringReader $$0) throws CommandSyntaxException {
      ResourceLocation $$1 = ResourceLocation.read($$0);
      ResourceKey<T> $$2 = ResourceKey.create(this.registryKey, $$1);
      return (Holder.Reference<T>)this.registryLookup
         .get($$2)
         .orElseThrow(() -> ERROR_UNKNOWN_RESOURCE.createWithContext($$0, $$1, this.registryKey.location()));
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
      return SharedSuggestionProvider.listSuggestions($$0, $$1, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS);
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceArgument<T>, ResourceArgument.Info<T>.Template> {
      public void serializeToNetwork(ResourceArgument.Info<T>.Template $$0, FriendlyByteBuf $$1) {
         $$1.writeResourceKey($$0.registryKey);
      }

      public ResourceArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf $$0) {
         return new ResourceArgument.Info.Template($$0.readRegistryKey());
      }

      public void serializeToJson(ResourceArgument.Info<T>.Template $$0, JsonObject $$1) {
         $$1.addProperty("registry", $$0.registryKey.location().toString());
      }

      public ResourceArgument.Info<T>.Template unpack(ResourceArgument<T> $$0) {
         return new ResourceArgument.Info.Template($$0.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(final ResourceKey<? extends Registry<T>> param2) {
            this.registryKey = $$1;
         }

         public ResourceArgument<T> instantiate(CommandBuildContext $$0) {
            return new ResourceArgument<>($$0, this.registryKey);
         }

         @Override
         public ArgumentTypeInfo<ResourceArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }
}
