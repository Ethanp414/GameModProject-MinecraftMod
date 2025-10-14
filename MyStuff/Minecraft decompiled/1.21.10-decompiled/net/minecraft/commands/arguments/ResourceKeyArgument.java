package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class ResourceKeyArgument<T> implements ArgumentType<ResourceKey<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_INVALID_FEATURE = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("commands.place.feature.invalid", $$0)
   );
   private static final DynamicCommandExceptionType ERROR_INVALID_STRUCTURE = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("commands.place.structure.invalid", $$0)
   );
   private static final DynamicCommandExceptionType ERROR_INVALID_TEMPLATE_POOL = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("commands.place.jigsaw.invalid", $$0)
   );
   private static final DynamicCommandExceptionType ERROR_INVALID_RECIPE = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("recipe.notFound", $$0)
   );
   private static final DynamicCommandExceptionType ERROR_INVALID_ADVANCEMENT = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("advancement.advancementNotFound", $$0)
   );
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceKeyArgument(ResourceKey<? extends Registry<T>> $$0) {
      this.registryKey = $$0;
   }

   public static <T> ResourceKeyArgument<T> key(ResourceKey<? extends Registry<T>> $$0) {
      return new ResourceKeyArgument<>($$0);
   }

   public static <T> ResourceKey<T> getRegistryKey(
      CommandContext<CommandSourceStack> $$0, String $$1, ResourceKey<Registry<T>> $$2, DynamicCommandExceptionType $$3
   ) throws CommandSyntaxException {
      ResourceKey<?> $$4 = $$0.getArgument($$1, ResourceKey.class);
      Optional<ResourceKey<T>> $$5 = $$4.cast($$2);
      return (ResourceKey<T>)$$5.orElseThrow(() -> $$3.create($$4.location()));
   }

   private static <T> Registry<T> getRegistry(CommandContext<CommandSourceStack> $$0, ResourceKey<? extends Registry<T>> $$1) {
      return $$0.getSource().getServer().registryAccess().lookupOrThrow($$1);
   }

   private static <T> Holder.Reference<T> resolveKey(
      CommandContext<CommandSourceStack> $$0, String $$1, ResourceKey<Registry<T>> $$2, DynamicCommandExceptionType $$3
   ) throws CommandSyntaxException {
      ResourceKey<T> $$4 = getRegistryKey($$0, $$1, $$2, $$3);
      return (Holder.Reference<T>)getRegistry($$0, $$2).get($$4).orElseThrow(() -> $$3.create($$4.location()));
   }

   public static Holder.Reference<ConfiguredFeature<?, ?>> getConfiguredFeature(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return resolveKey($$0, $$1, Registries.CONFIGURED_FEATURE, ERROR_INVALID_FEATURE);
   }

   public static Holder.Reference<Structure> getStructure(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return resolveKey($$0, $$1, Registries.STRUCTURE, ERROR_INVALID_STRUCTURE);
   }

   public static Holder.Reference<StructureTemplatePool> getStructureTemplatePool(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return resolveKey($$0, $$1, Registries.TEMPLATE_POOL, ERROR_INVALID_TEMPLATE_POOL);
   }

   public static RecipeHolder<?> getRecipe(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      RecipeManager $$2 = $$0.getSource().getServer().getRecipeManager();
      ResourceKey<Recipe<?>> $$3 = getRegistryKey($$0, $$1, Registries.RECIPE, ERROR_INVALID_RECIPE);
      return (RecipeHolder<?>)$$2.byKey($$3).orElseThrow(() -> ERROR_INVALID_RECIPE.create($$3.location()));
   }

   public static AdvancementHolder getAdvancement(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      ResourceKey<Advancement> $$2 = getRegistryKey($$0, $$1, Registries.ADVANCEMENT, ERROR_INVALID_ADVANCEMENT);
      AdvancementHolder $$3 = $$0.getSource().getServer().getAdvancements().get($$2.location());
      if ($$3 == null) {
         throw ERROR_INVALID_ADVANCEMENT.create($$2.location());
      } else {
         return $$3;
      }
   }

   public ResourceKey<T> parse(StringReader $$0) throws CommandSyntaxException {
      ResourceLocation $$1 = ResourceLocation.read($$0);
      return ResourceKey.create(this.registryKey, $$1);
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
      return SharedSuggestionProvider.listSuggestions($$0, $$1, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS);
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceKeyArgument<T>, ResourceKeyArgument.Info<T>.Template> {
      public void serializeToNetwork(ResourceKeyArgument.Info<T>.Template $$0, FriendlyByteBuf $$1) {
         $$1.writeResourceKey($$0.registryKey);
      }

      public ResourceKeyArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf $$0) {
         return new ResourceKeyArgument.Info.Template($$0.readRegistryKey());
      }

      public void serializeToJson(ResourceKeyArgument.Info<T>.Template $$0, JsonObject $$1) {
         $$1.addProperty("registry", $$0.registryKey.location().toString());
      }

      public ResourceKeyArgument.Info<T>.Template unpack(ResourceKeyArgument<T> $$0) {
         return new ResourceKeyArgument.Info.Template($$0.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceKeyArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(final ResourceKey<? extends Registry<T>> param2) {
            this.registryKey = $$1;
         }

         public ResourceKeyArgument<T> instantiate(CommandBuildContext $$0) {
            return new ResourceKeyArgument<>(this.registryKey);
         }

         @Override
         public ArgumentTypeInfo<ResourceKeyArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }
}
