package net.minecraft.stats;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.slf4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   public static final String RECIPE_BOOK_TAG = "recipeBook";
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ServerRecipeBook.DisplayResolver displayResolver;
   @VisibleForTesting
   protected final Set<ResourceKey<Recipe<?>>> known = Sets.newIdentityHashSet();
   @VisibleForTesting
   protected final Set<ResourceKey<Recipe<?>>> highlight = Sets.newIdentityHashSet();

   public ServerRecipeBook(ServerRecipeBook.DisplayResolver $$0) {
      this.displayResolver = $$0;
   }

   public void add(ResourceKey<Recipe<?>> $$0) {
      this.known.add($$0);
   }

   public boolean contains(ResourceKey<Recipe<?>> $$0) {
      return this.known.contains($$0);
   }

   public void remove(ResourceKey<Recipe<?>> $$0) {
      this.known.remove($$0);
      this.highlight.remove($$0);
   }

   public void removeHighlight(ResourceKey<Recipe<?>> $$0) {
      this.highlight.remove($$0);
   }

   private void addHighlight(ResourceKey<Recipe<?>> $$0) {
      this.highlight.add($$0);
   }

   public int addRecipes(Collection<RecipeHolder<?>> $$0, ServerPlayer $$1) {
      List<ClientboundRecipeBookAddPacket.Entry> $$2 = new ArrayList();

      for(RecipeHolder<?> $$3 : $$0) {
         ResourceKey<Recipe<?>> $$4 = $$3.id();
         if (!this.known.contains($$4) && !$$3.value().isSpecial()) {
            this.add($$4);
            this.addHighlight($$4);
            this.displayResolver.displaysForRecipe($$4, $$2x -> $$2.add(new ClientboundRecipeBookAddPacket.Entry($$2x, $$3.value().showNotification(), true)));
            CriteriaTriggers.RECIPE_UNLOCKED.trigger($$1, $$3);
         }
      }

      if (!$$2.isEmpty()) {
         $$1.connection.send(new ClientboundRecipeBookAddPacket($$2, false));
      }

      return $$2.size();
   }

   public int removeRecipes(Collection<RecipeHolder<?>> $$0, ServerPlayer $$1) {
      List<RecipeDisplayId> $$2 = Lists.newArrayList();

      for(RecipeHolder<?> $$3 : $$0) {
         ResourceKey<Recipe<?>> $$4 = $$3.id();
         if (this.known.contains($$4)) {
            this.remove($$4);
            this.displayResolver.displaysForRecipe($$4, $$1x -> $$2.add($$1x.id()));
         }
      }

      if (!$$2.isEmpty()) {
         $$1.connection.send(new ClientboundRecipeBookRemovePacket($$2));
      }

      return $$2.size();
   }

   private void loadRecipes(List<ResourceKey<Recipe<?>>> $$0, Consumer<ResourceKey<Recipe<?>>> $$1, Predicate<ResourceKey<Recipe<?>>> $$2) {
      for(ResourceKey<Recipe<?>> $$3 : $$0) {
         if (!$$2.test($$3)) {
            LOGGER.error("Tried to load unrecognized recipe: {} removed now.", $$3);
         } else {
            $$1.accept($$3);
         }
      }
   }

   public void sendInitialRecipeBook(ServerPlayer $$0) {
      $$0.connection.send(new ClientboundRecipeBookSettingsPacket(this.getBookSettings().copy()));
      List<ClientboundRecipeBookAddPacket.Entry> $$1 = new ArrayList(this.known.size());

      for(ResourceKey<Recipe<?>> $$2 : this.known) {
         this.displayResolver.displaysForRecipe($$2, $$2x -> $$1.add(new ClientboundRecipeBookAddPacket.Entry($$2x, false, this.highlight.contains($$2))));
      }

      $$0.connection.send(new ClientboundRecipeBookAddPacket($$1, true));
   }

   public void copyOverData(ServerRecipeBook $$0) {
      this.apply($$0.pack());
   }

   public ServerRecipeBook.Packed pack() {
      return new ServerRecipeBook.Packed(this.bookSettings.copy(), List.copyOf(this.known), List.copyOf(this.highlight));
   }

   private void apply(ServerRecipeBook.Packed $$0) {
      this.known.clear();
      this.highlight.clear();
      this.bookSettings.replaceFrom($$0.settings);
      this.known.addAll($$0.known);
      this.highlight.addAll($$0.highlight);
   }

   public void loadUntrusted(ServerRecipeBook.Packed $$0, Predicate<ResourceKey<Recipe<?>>> $$1) {
      this.bookSettings.replaceFrom($$0.settings);
      this.loadRecipes($$0.known, this.known::add, $$1);
      this.loadRecipes($$0.highlight, this.highlight::add, $$1);
   }

   @FunctionalInterface
   public interface DisplayResolver {
      void displaysForRecipe(ResourceKey<Recipe<?>> var1, Consumer<RecipeDisplayEntry> var2);
   }

   public static record Packed(RecipeBookSettings settings, List<ResourceKey<Recipe<?>>> known, List<ResourceKey<Recipe<?>>> highlight) {
      final RecipeBookSettings settings;
      final List<ResourceKey<Recipe<?>>> known;
      final List<ResourceKey<Recipe<?>>> highlight;
      public static final Codec<ServerRecipeBook.Packed> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  RecipeBookSettings.MAP_CODEC.forGetter(ServerRecipeBook.Packed::settings),
                  Recipe.KEY_CODEC.listOf().fieldOf("recipes").forGetter(ServerRecipeBook.Packed::known),
                  Recipe.KEY_CODEC.listOf().fieldOf("toBeDisplayed").forGetter(ServerRecipeBook.Packed::highlight)
               )
               .apply($$0, ServerRecipeBook.Packed::new)
      );
   }
}
