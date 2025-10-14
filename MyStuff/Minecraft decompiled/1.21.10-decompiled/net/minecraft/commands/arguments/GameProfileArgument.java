package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

public class GameProfileArgument implements ArgumentType<GameProfileArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
   public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.unknown"));

   public static Collection<NameAndId> getGameProfiles(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return $$0.<GameProfileArgument.Result>getArgument($$1, GameProfileArgument.Result.class).getNames($$0.getSource());
   }

   public static GameProfileArgument gameProfile() {
      return new GameProfileArgument();
   }

   public <S> GameProfileArgument.Result parse(StringReader $$0, S $$1) throws CommandSyntaxException {
      return parse($$0, EntitySelectorParser.allowSelectors($$1));
   }

   public GameProfileArgument.Result parse(StringReader $$0) throws CommandSyntaxException {
      return parse($$0, true);
   }

   private static GameProfileArgument.Result parse(StringReader $$0, boolean $$1) throws CommandSyntaxException {
      if ($$0.canRead() && $$0.peek() == '@') {
         EntitySelectorParser $$2 = new EntitySelectorParser($$0, $$1);
         EntitySelector $$3 = $$2.parse();
         if ($$3.includesEntities()) {
            throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.createWithContext($$0);
         } else {
            return new GameProfileArgument.SelectorResult($$3);
         }
      } else {
         int $$4 = $$0.getCursor();

         while($$0.canRead() && $$0.peek() != ' ') {
            $$0.skip();
         }

         String $$5 = $$0.getString().substring($$4, $$0.getCursor());
         return $$1x -> {
            Optional<NameAndId> $$2 = $$1x.getServer().services().nameToIdCache().get($$5);
            return Collections.singleton((NameAndId)$$2.orElseThrow(ERROR_UNKNOWN_PLAYER::create));
         };
      }
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
      StringReader $$3 = $$0.getSource();
      if ($$3 instanceof SharedSuggestionProvider $$2) {
         $$3 = new StringReader($$1.getInput());
         $$3.setCursor($$1.getStart());
         EntitySelectorParser $$4 = new EntitySelectorParser($$3, EntitySelectorParser.allowSelectors($$2));

         try {
            $$4.parse();
         } catch (CommandSyntaxException var7) {
         }

         return $$4.fillSuggestions($$1, $$1x -> SharedSuggestionProvider.suggest($$2.getOnlinePlayerNames(), $$1x));
      } else {
         return Suggestions.empty();
      }
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   @FunctionalInterface
   public interface Result {
      Collection<NameAndId> getNames(CommandSourceStack var1) throws CommandSyntaxException;
   }

   public static class SelectorResult implements GameProfileArgument.Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector $$0) {
         this.selector = $$0;
      }

      @Override
      public Collection<NameAndId> getNames(CommandSourceStack $$0) throws CommandSyntaxException {
         List<ServerPlayer> $$1 = this.selector.findPlayers($$0);
         if ($$1.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
         } else {
            List<NameAndId> $$2 = new ArrayList();

            for(ServerPlayer $$3 : $$1) {
               $$2.add($$3.nameAndId());
            }

            return $$2;
         }
      }
   }
}
