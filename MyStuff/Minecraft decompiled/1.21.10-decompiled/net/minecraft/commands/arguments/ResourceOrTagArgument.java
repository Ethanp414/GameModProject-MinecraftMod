package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagArgument<T> implements ArgumentType<ResourceOrTagArgument.Result<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   private static final Dynamic2CommandExceptionType ERROR_UNKNOWN_TAG = new Dynamic2CommandExceptionType(
      ($$0, $$1) -> Component.translatableEscape("argument.resource_tag.not_found", $$0, $$1)
   );
   private static final Dynamic3CommandExceptionType ERROR_INVALID_TAG_TYPE = new Dynamic3CommandExceptionType(
      ($$0, $$1, $$2) -> Component.translatableEscape("argument.resource_tag.invalid_type", $$0, $$1, $$2)
   );
   private final HolderLookup<T> registryLookup;
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceOrTagArgument(CommandBuildContext $$0, ResourceKey<? extends Registry<T>> $$1) {
      this.registryKey = $$1;
      this.registryLookup = $$0.lookupOrThrow($$1);
   }

   public static <T> ResourceOrTagArgument<T> resourceOrTag(CommandBuildContext $$0, ResourceKey<? extends Registry<T>> $$1) {
      return new ResourceOrTagArgument<>($$0, $$1);
   }

   public static <T> ResourceOrTagArgument.Result<T> getResourceOrTag(CommandContext<CommandSourceStack> $$0, String $$1, ResourceKey<Registry<T>> $$2) throws CommandSyntaxException {
      ResourceOrTagArgument.Result<?> $$3 = $$0.getArgument($$1, ResourceOrTagArgument.Result.class);
      Optional<ResourceOrTagArgument.Result<T>> $$4 = $$3.cast($$2);
      return (ResourceOrTagArgument.Result<T>)$$4.orElseThrow(() -> $$3.unwrap().map($$1xx -> {
            ResourceKey<?> $$2xx = $$1xx.key();
            return ResourceArgument.ERROR_INVALID_RESOURCE_TYPE.create($$2xx.location(), $$2xx.registry(), $$2.location());
         }, $$1xx -> {
            TagKey<?> $$2xx = $$1xx.key();
            return ERROR_INVALID_TAG_TYPE.create($$2xx.location(), $$2xx.registry(), $$2.location());
         }));
   }

   public ResourceOrTagArgument.Result<T> parse(StringReader $$0) throws CommandSyntaxException {
      if ($$0.canRead() && $$0.peek() == '#') {
         int $$1 = $$0.getCursor();

         try {
            $$0.skip();
            ResourceLocation $$2 = ResourceLocation.read($$0);
            TagKey<T> $$3 = TagKey.create(this.registryKey, $$2);
            HolderSet.Named<T> $$4 = (HolderSet.Named)this.registryLookup
               .get($$3)
               .orElseThrow(() -> ERROR_UNKNOWN_TAG.createWithContext($$0, $$2, this.registryKey.location()));
            return new ResourceOrTagArgument.TagResult<>($$4);
         } catch (CommandSyntaxException var6) {
            $$0.setCursor($$1);
            throw var6;
         }
      } else {
         ResourceLocation $$6 = ResourceLocation.read($$0);
         ResourceKey<T> $$7 = ResourceKey.create(this.registryKey, $$6);
         Holder.Reference<T> $$8 = (Holder.Reference)this.registryLookup
            .get($$7)
            .orElseThrow(() -> ResourceArgument.ERROR_UNKNOWN_RESOURCE.createWithContext($$0, $$6, this.registryKey.location()));
         return new ResourceOrTagArgument.ResourceResult<>($$8);
      }
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
      return SharedSuggestionProvider.listSuggestions($$0, $$1, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ALL);
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagArgument<T>, ResourceOrTagArgument.Info<T>.Template> {
      public void serializeToNetwork(ResourceOrTagArgument.Info<T>.Template $$0, FriendlyByteBuf $$1) {
         $$1.writeResourceKey($$0.registryKey);
      }

      public ResourceOrTagArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf $$0) {
         return new ResourceOrTagArgument.Info.Template($$0.readRegistryKey());
      }

      public void serializeToJson(ResourceOrTagArgument.Info<T>.Template $$0, JsonObject $$1) {
         $$1.addProperty("registry", $$0.registryKey.location().toString());
      }

      public ResourceOrTagArgument.Info<T>.Template unpack(ResourceOrTagArgument<T> $$0) {
         return new ResourceOrTagArgument.Info.Template($$0.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(final ResourceKey<? extends Registry<T>> param2) {
            this.registryKey = $$1;
         }

         public ResourceOrTagArgument<T> instantiate(CommandBuildContext $$0) {
            return new ResourceOrTagArgument<>($$0, this.registryKey);
         }

         @Override
         public ArgumentTypeInfo<ResourceOrTagArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }

   static record ResourceResult<T>(Holder.Reference<T> value) implements ResourceOrTagArgument.Result<T> {
      @Override
      public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
         return Either.left(this.value);
      }

      @Override
      public <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
         return this.value.key().isFor($$0) ? Optional.of(this) : Optional.empty();
      }

      public boolean test(Holder<T> $$0) {
         return $$0.equals(this.value);
      }

      @Override
      public String asPrintable() {
         return this.value.key().location().toString();
      }
   }

   public interface Result<T> extends Predicate<Holder<T>> {
      Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap();

      <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> var1);

      String asPrintable();
   }

   static record TagResult<T>(HolderSet.Named<T> tag) implements ResourceOrTagArgument.Result<T> {
      @Override
      public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
         return Either.right(this.tag);
      }

      @Override
      public <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
         return this.tag.key().isFor($$0) ? Optional.of(this) : Optional.empty();
      }

      public boolean test(Holder<T> $$0) {
         return this.tag.contains($$0);
      }

      @Override
      public String asPrintable() {
         return "#" + this.tag.key().location();
      }
   }
}
