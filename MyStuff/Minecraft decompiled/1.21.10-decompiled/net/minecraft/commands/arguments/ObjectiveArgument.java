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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class ObjectiveArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("arguments.objective.notFound", $$0)
   );
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("arguments.objective.readonly", $$0)
   );

   public static ObjectiveArgument objective() {
      return new ObjectiveArgument();
   }

   public static Objective getObjective(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      String $$2 = $$0.getArgument($$1, String.class);
      Scoreboard $$3 = $$0.getSource().getServer().getScoreboard();
      Objective $$4 = $$3.getObjective($$2);
      if ($$4 == null) {
         throw ERROR_OBJECTIVE_NOT_FOUND.create($$2);
      } else {
         return $$4;
      }
   }

   public static Objective getWritableObjective(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      Objective $$2 = getObjective($$0, $$1);
      if ($$2.getCriteria().isReadOnly()) {
         throw ERROR_OBJECTIVE_READ_ONLY.create($$2.getName());
      } else {
         return $$2;
      }
   }

   public String parse(StringReader $$0) throws CommandSyntaxException {
      return $$0.readUnquotedString();
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
      S $$2 = $$0.getSource();
      if ($$2 instanceof CommandSourceStack $$3) {
         return SharedSuggestionProvider.suggest($$3.getServer().getScoreboard().getObjectiveNames(), $$1);
      } else {
         return $$2 instanceof SharedSuggestionProvider $$4 ? $$4.customSuggestion($$0) : Suggestions.empty();
      }
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
