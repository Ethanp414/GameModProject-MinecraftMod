package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ColumnPos;

public class ColumnPosArgument implements ArgumentType<Coordinates> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.pos2d.incomplete"));

   public static ColumnPosArgument columnPos() {
      return new ColumnPosArgument();
   }

   public static ColumnPos getColumnPos(CommandContext<CommandSourceStack> $$0, String $$1) {
      BlockPos $$2 = $$0.<Coordinates>getArgument($$1, Coordinates.class).getBlockPos($$0.getSource());
      return new ColumnPos($$2.getX(), $$2.getZ());
   }

   public Coordinates parse(StringReader $$0) throws CommandSyntaxException {
      int $$1 = $$0.getCursor();
      if (!$$0.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext($$0);
      } else {
         WorldCoordinate $$2 = WorldCoordinate.parseInt($$0);
         if ($$0.canRead() && $$0.peek() == ' ') {
            $$0.skip();
            WorldCoordinate $$3 = WorldCoordinate.parseInt($$0);
            return new WorldCoordinates($$2, new WorldCoordinate(true, 0.0), $$3);
         } else {
            $$0.setCursor($$1);
            throw ERROR_NOT_COMPLETE.createWithContext($$0);
         }
      }
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
      if (!($$0.getSource() instanceof SharedSuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String $$2 = $$1.getRemaining();
         Collection<SharedSuggestionProvider.TextCoordinates> $$3;
         if (!$$2.isEmpty() && $$2.charAt(0) == '^') {
            $$3 = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
         } else {
            $$3 = ((SharedSuggestionProvider)$$0.getSource()).getRelevantCoordinates();
         }

         return SharedSuggestionProvider.suggest2DCoordinates($$2, $$3, $$1, Commands.createValidator(this::parse));
      }
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
