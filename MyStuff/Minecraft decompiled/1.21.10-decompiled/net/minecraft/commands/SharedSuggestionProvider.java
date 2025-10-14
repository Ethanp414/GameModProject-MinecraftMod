package net.minecraft.commands;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;

public interface SharedSuggestionProvider {
   CharMatcher MATCH_SPLITTER = CharMatcher.anyOf("._/");

   Collection<String> getOnlinePlayerNames();

   default Collection<String> getCustomTabSugggestions() {
      return this.getOnlinePlayerNames();
   }

   default Collection<String> getSelectedEntities() {
      return Collections.emptyList();
   }

   Collection<String> getAllTeams();

   Stream<ResourceLocation> getAvailableSounds();

   CompletableFuture<Suggestions> customSuggestion(CommandContext<?> var1);

   default Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   default Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   Set<ResourceKey<Level>> levels();

   RegistryAccess registryAccess();

   FeatureFlagSet enabledFeatures();

   default void suggestRegistryElements(HolderLookup<?> $$0, SharedSuggestionProvider.ElementSuggestionType $$1, SuggestionsBuilder $$2) {
      if ($$1.shouldSuggestTags()) {
         suggestResource($$0.listTagIds().map(TagKey::location), $$2, "#");
      }

      if ($$1.shouldSuggestElements()) {
         suggestResource($$0.listElementIds().map(ResourceKey::location), $$2);
      }
   }

   static <S> CompletableFuture<Suggestions> listSuggestions(
      CommandContext<S> $$0, SuggestionsBuilder $$1, ResourceKey<? extends Registry<?>> $$2, SharedSuggestionProvider.ElementSuggestionType $$3
   ) {
      Object var5 = $$0.getSource();
      return var5 instanceof SharedSuggestionProvider $$4 ? $$4.suggestRegistryElements($$2, $$3, $$1, $$0) : $$1.buildFuture();
   }

   CompletableFuture<Suggestions> suggestRegistryElements(
      ResourceKey<? extends Registry<?>> var1, SharedSuggestionProvider.ElementSuggestionType var2, SuggestionsBuilder var3, CommandContext<?> var4
   );

   static <T> void filterResources(Iterable<T> $$0, String $$1, Function<T, ResourceLocation> $$2, Consumer<T> $$3) {
      boolean $$4 = $$1.indexOf(58) > -1;

      for(T $$5 : $$0) {
         ResourceLocation $$6 = (ResourceLocation)$$2.apply($$5);
         if ($$4) {
            String $$7 = $$6.toString();
            if (matchesSubStr($$1, $$7)) {
               $$3.accept($$5);
            }
         } else if (matchesSubStr($$1, $$6.getNamespace()) || $$6.getNamespace().equals("minecraft") && matchesSubStr($$1, $$6.getPath())) {
            $$3.accept($$5);
         }
      }
   }

   static <T> void filterResources(Iterable<T> $$0, String $$1, String $$2, Function<T, ResourceLocation> $$3, Consumer<T> $$4) {
      if ($$1.isEmpty()) {
         $$0.forEach($$4);
      } else {
         String $$5 = Strings.commonPrefix($$1, $$2);
         if (!$$5.isEmpty()) {
            String $$6 = $$1.substring($$5.length());
            filterResources($$0, $$6, $$3, $$4);
         }
      }
   }

   static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> $$0, SuggestionsBuilder $$1, String $$2) {
      String $$3 = $$1.getRemaining().toLowerCase(Locale.ROOT);
      filterResources($$0, $$3, $$2, $$0x -> $$0x, $$2x -> $$1.suggest($$2 + $$2x));
      return $$1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> $$0, SuggestionsBuilder $$1, String $$2) {
      return suggestResource($$0::iterator, $$1, $$2);
   }

   static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> $$0, SuggestionsBuilder $$1) {
      String $$2 = $$1.getRemaining().toLowerCase(Locale.ROOT);
      filterResources($$0, $$2, $$0x -> $$0x, $$1x -> $$1.suggest($$1x.toString()));
      return $$1.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> suggestResource(
      Iterable<T> $$0, SuggestionsBuilder $$1, Function<T, ResourceLocation> $$2, Function<T, Message> $$3
   ) {
      String $$4 = $$1.getRemaining().toLowerCase(Locale.ROOT);
      filterResources($$0, $$4, $$2, $$3x -> $$1.suggest(((ResourceLocation)$$2.apply($$3x)).toString(), (Message)$$3.apply($$3x)));
      return $$1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> $$0, SuggestionsBuilder $$1) {
      return suggestResource($$0::iterator, $$1);
   }

   static <T> CompletableFuture<Suggestions> suggestResource(
      Stream<T> $$0, SuggestionsBuilder $$1, Function<T, ResourceLocation> $$2, Function<T, Message> $$3
   ) {
      return suggestResource($$0::iterator, $$1, $$2, $$3);
   }

   static CompletableFuture<Suggestions> suggestCoordinates(
      String $$0, Collection<SharedSuggestionProvider.TextCoordinates> $$1, SuggestionsBuilder $$2, Predicate<String> $$3
   ) {
      List<String> $$4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty($$0)) {
         for(SharedSuggestionProvider.TextCoordinates $$5 : $$1) {
            String $$6 = $$5.x + " " + $$5.y + " " + $$5.z;
            if ($$3.test($$6)) {
               $$4.add($$5.x);
               $$4.add($$5.x + " " + $$5.y);
               $$4.add($$6);
            }
         }
      } else {
         String[] $$7 = $$0.split(" ");
         if ($$7.length == 1) {
            for(SharedSuggestionProvider.TextCoordinates $$8 : $$1) {
               String $$9 = $$7[0] + " " + $$8.y + " " + $$8.z;
               if ($$3.test($$9)) {
                  $$4.add($$7[0] + " " + $$8.y);
                  $$4.add($$9);
               }
            }
         } else if ($$7.length == 2) {
            for(SharedSuggestionProvider.TextCoordinates $$10 : $$1) {
               String $$11 = $$7[0] + " " + $$7[1] + " " + $$10.z;
               if ($$3.test($$11)) {
                  $$4.add($$11);
               }
            }
         }
      }

      return suggest($$4, $$2);
   }

   static CompletableFuture<Suggestions> suggest2DCoordinates(
      String $$0, Collection<SharedSuggestionProvider.TextCoordinates> $$1, SuggestionsBuilder $$2, Predicate<String> $$3
   ) {
      List<String> $$4 = Lists.newArrayList();
      if (Strings.isNullOrEmpty($$0)) {
         for(SharedSuggestionProvider.TextCoordinates $$5 : $$1) {
            String $$6 = $$5.x + " " + $$5.z;
            if ($$3.test($$6)) {
               $$4.add($$5.x);
               $$4.add($$6);
            }
         }
      } else {
         String[] $$7 = $$0.split(" ");
         if ($$7.length == 1) {
            for(SharedSuggestionProvider.TextCoordinates $$8 : $$1) {
               String $$9 = $$7[0] + " " + $$8.z;
               if ($$3.test($$9)) {
                  $$4.add($$9);
               }
            }
         }
      }

      return suggest($$4, $$2);
   }

   static CompletableFuture<Suggestions> suggest(Iterable<String> $$0, SuggestionsBuilder $$1) {
      String $$2 = $$1.getRemaining().toLowerCase(Locale.ROOT);

      for(String $$3 : $$0) {
         if (matchesSubStr($$2, $$3.toLowerCase(Locale.ROOT))) {
            $$1.suggest($$3);
         }
      }

      return $$1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(Stream<String> $$0, SuggestionsBuilder $$1) {
      String $$2 = $$1.getRemaining().toLowerCase(Locale.ROOT);
      $$0.filter($$1x -> matchesSubStr($$2, $$1x.toLowerCase(Locale.ROOT))).forEach($$1::suggest);
      return $$1.buildFuture();
   }

   static CompletableFuture<Suggestions> suggest(String[] $$0, SuggestionsBuilder $$1) {
      String $$2 = $$1.getRemaining().toLowerCase(Locale.ROOT);

      for(String $$3 : $$0) {
         if (matchesSubStr($$2, $$3.toLowerCase(Locale.ROOT))) {
            $$1.suggest($$3);
         }
      }

      return $$1.buildFuture();
   }

   static <T> CompletableFuture<Suggestions> suggest(Iterable<T> $$0, SuggestionsBuilder $$1, Function<T, String> $$2, Function<T, Message> $$3) {
      String $$4 = $$1.getRemaining().toLowerCase(Locale.ROOT);

      for(T $$5 : $$0) {
         String $$6 = (String)$$2.apply($$5);
         if (matchesSubStr($$4, $$6.toLowerCase(Locale.ROOT))) {
            $$1.suggest($$6, (Message)$$3.apply($$5));
         }
      }

      return $$1.buildFuture();
   }

   static boolean matchesSubStr(String $$0, String $$1) {
      int $$3;
      for(int $$2 = 0; !$$1.startsWith($$0, $$2); $$2 = $$3 + 1) {
         $$3 = MATCH_SPLITTER.indexIn($$1, $$2);
         if ($$3 < 0) {
            return false;
         }
      }

      return true;
   }

   public static enum ElementSuggestionType {
      TAGS,
      ELEMENTS,
      ALL;

      public boolean shouldSuggestTags() {
         return this == TAGS || this == ALL;
      }

      public boolean shouldSuggestElements() {
         return this == ELEMENTS || this == ALL;
      }
   }

   public static class TextCoordinates {
      public static final SharedSuggestionProvider.TextCoordinates DEFAULT_LOCAL = new SharedSuggestionProvider.TextCoordinates("^", "^", "^");
      public static final SharedSuggestionProvider.TextCoordinates DEFAULT_GLOBAL = new SharedSuggestionProvider.TextCoordinates("~", "~", "~");
      public final String x;
      public final String y;
      public final String z;

      public TextCoordinates(String $$0, String $$1, String $$2) {
         this.x = $$0;
         this.y = $$1;
         this.z = $$2;
      }
   }
}
