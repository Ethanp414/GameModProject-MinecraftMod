package net.minecraft.commands.synchronization;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class SuggestionProviders {
   private static final Map<ResourceLocation, SuggestionProvider<SharedSuggestionProvider>> PROVIDERS_BY_NAME = new HashMap();
   private static final ResourceLocation ID_ASK_SERVER = ResourceLocation.withDefaultNamespace("ask_server");
   public static final SuggestionProvider<SharedSuggestionProvider> ASK_SERVER = register(ID_ASK_SERVER, ($$0, $$1) -> $$0.getSource().customSuggestion($$0));
   public static final SuggestionProvider<SharedSuggestionProvider> AVAILABLE_SOUNDS = register(
      ResourceLocation.withDefaultNamespace("available_sounds"),
      ($$0, $$1) -> SharedSuggestionProvider.suggestResource($$0.getSource().getAvailableSounds(), $$1)
   );
   public static final SuggestionProvider<SharedSuggestionProvider> SUMMONABLE_ENTITIES = register(
      ResourceLocation.withDefaultNamespace("summonable_entities"),
      ($$0, $$1) -> SharedSuggestionProvider.suggestResource(
            BuiltInRegistries.ENTITY_TYPE.stream().filter($$1x -> $$1x.isEnabled($$0.getSource().enabledFeatures()) && $$1x.canSummon()),
            $$1,
            EntityType::getKey,
            EntityType::getDescription
         )
   );

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> register(ResourceLocation $$0, SuggestionProvider<SharedSuggestionProvider> $$1) {
      SuggestionProvider<SharedSuggestionProvider> $$2 = (SuggestionProvider)PROVIDERS_BY_NAME.putIfAbsent($$0, $$1);
      if ($$2 != null) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name '" + $$0 + "'");
      } else {
         return new SuggestionProviders.RegisteredSuggestion($$0, $$1);
      }
   }

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> cast(SuggestionProvider<SharedSuggestionProvider> $$0) {
      return $$0;
   }

   public static <S extends SharedSuggestionProvider> SuggestionProvider<S> getProvider(ResourceLocation $$0) {
      return cast((SuggestionProvider<SharedSuggestionProvider>)PROVIDERS_BY_NAME.getOrDefault($$0, ASK_SERVER));
   }

   public static ResourceLocation getName(SuggestionProvider<?> $$0) {
      return $$0 instanceof SuggestionProviders.RegisteredSuggestion $$1 ? $$1.name : ID_ASK_SERVER;
   }

   static record RegisteredSuggestion(ResourceLocation name, SuggestionProvider<SharedSuggestionProvider> delegate)
      implements SuggestionProvider<SharedSuggestionProvider> {
      final ResourceLocation name;

      @Override
      public CompletableFuture<Suggestions> getSuggestions(CommandContext<SharedSuggestionProvider> $$0, SuggestionsBuilder $$1) throws CommandSyntaxException {
         return this.delegate.getSuggestions($$0, $$1);
      }
   }
}
