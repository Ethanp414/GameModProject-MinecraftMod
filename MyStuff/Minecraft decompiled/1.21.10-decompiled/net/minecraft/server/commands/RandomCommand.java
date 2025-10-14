package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.RandomSequences;

public class RandomCommand {
   private static final SimpleCommandExceptionType ERROR_RANGE_TOO_LARGE = new SimpleCommandExceptionType(
      Component.translatable("commands.random.error.range_too_large")
   );
   private static final SimpleCommandExceptionType ERROR_RANGE_TOO_SMALL = new SimpleCommandExceptionType(
      Component.translatable("commands.random.error.range_too_small")
   );

   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      $$0.register(
         Commands.literal("random")
            .then(drawRandomValueTree("value", false))
            .then(drawRandomValueTree("roll", true))
            .then(
               Commands.literal("reset")
                  .requires(Commands.hasPermission(2))
                  .then(
                     Commands.literal("*")
                        .executes($$0x -> resetAllSequences((CommandSourceStack)$$0x.getSource()))
                        .then(
                           Commands.argument("seed", IntegerArgumentType.integer())
                              .executes(
                                 $$0x -> resetAllSequencesAndSetNewDefaults(
                                       (CommandSourceStack)$$0x.getSource(), IntegerArgumentType.getInteger($$0x, "seed"), true, true
                                    )
                              )
                              .then(
                                 Commands.argument("includeWorldSeed", BoolArgumentType.bool())
                                    .executes(
                                       $$0x -> resetAllSequencesAndSetNewDefaults(
                                             (CommandSourceStack)$$0x.getSource(),
                                             IntegerArgumentType.getInteger($$0x, "seed"),
                                             BoolArgumentType.getBool($$0x, "includeWorldSeed"),
                                             true
                                          )
                                    )
                                    .then(
                                       Commands.argument("includeSequenceId", BoolArgumentType.bool())
                                          .executes(
                                             $$0x -> resetAllSequencesAndSetNewDefaults(
                                                   (CommandSourceStack)$$0x.getSource(),
                                                   IntegerArgumentType.getInteger($$0x, "seed"),
                                                   BoolArgumentType.getBool($$0x, "includeWorldSeed"),
                                                   BoolArgumentType.getBool($$0x, "includeSequenceId")
                                                )
                                          )
                                    )
                              )
                        )
                  )
                  .then(
                     Commands.argument("sequence", ResourceLocationArgument.id())
                        .suggests(RandomCommand::suggestRandomSequence)
                        .executes($$0x -> resetSequence((CommandSourceStack)$$0x.getSource(), ResourceLocationArgument.getId($$0x, "sequence")))
                        .then(
                           Commands.argument("seed", IntegerArgumentType.integer())
                              .executes(
                                 $$0x -> resetSequence(
                                       (CommandSourceStack)$$0x.getSource(),
                                       ResourceLocationArgument.getId($$0x, "sequence"),
                                       IntegerArgumentType.getInteger($$0x, "seed"),
                                       true,
                                       true
                                    )
                              )
                              .then(
                                 Commands.argument("includeWorldSeed", BoolArgumentType.bool())
                                    .executes(
                                       $$0x -> resetSequence(
                                             (CommandSourceStack)$$0x.getSource(),
                                             ResourceLocationArgument.getId($$0x, "sequence"),
                                             IntegerArgumentType.getInteger($$0x, "seed"),
                                             BoolArgumentType.getBool($$0x, "includeWorldSeed"),
                                             true
                                          )
                                    )
                                    .then(
                                       Commands.argument("includeSequenceId", BoolArgumentType.bool())
                                          .executes(
                                             $$0x -> resetSequence(
                                                   (CommandSourceStack)$$0x.getSource(),
                                                   ResourceLocationArgument.getId($$0x, "sequence"),
                                                   IntegerArgumentType.getInteger($$0x, "seed"),
                                                   BoolArgumentType.getBool($$0x, "includeWorldSeed"),
                                                   BoolArgumentType.getBool($$0x, "includeSequenceId")
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static LiteralArgumentBuilder<CommandSourceStack> drawRandomValueTree(String $$0, boolean $$1) {
      return Commands.literal($$0)
         .then(
            Commands.argument("range", RangeArgument.intRange())
               .executes($$1x -> randomSample((CommandSourceStack)$$1x.getSource(), RangeArgument.Ints.getRange($$1x, "range"), null, $$1))
               .then(
                  Commands.argument("sequence", ResourceLocationArgument.id())
                     .suggests(RandomCommand::suggestRandomSequence)
                     .requires(Commands.hasPermission(2))
                     .executes(
                        $$1x -> randomSample(
                              (CommandSourceStack)$$1x.getSource(),
                              RangeArgument.Ints.getRange($$1x, "range"),
                              ResourceLocationArgument.getId($$1x, "sequence"),
                              $$1
                           )
                     )
               )
         );
   }

   private static CompletableFuture<Suggestions> suggestRandomSequence(CommandContext<CommandSourceStack> $$0, SuggestionsBuilder $$1) {
      List<String> $$2 = Lists.newArrayList();
      $$0.getSource().getLevel().getRandomSequences().forAllSequences(($$1x, $$2x) -> $$2.add($$1x.toString()));
      return SharedSuggestionProvider.suggest($$2, $$1);
   }

   private static int randomSample(CommandSourceStack $$0, MinMaxBounds.Ints $$1, @Nullable ResourceLocation $$2, boolean $$3) throws CommandSyntaxException {
      RandomSource $$4;
      if ($$2 != null) {
         $$4 = $$0.getLevel().getRandomSequence($$2);
      } else {
         $$4 = $$0.getLevel().getRandom();
      }

      int $$6 = $$1.min().orElse(Integer.MIN_VALUE);
      int $$7 = $$1.max().orElse(Integer.MAX_VALUE);
      long $$8 = (long)$$7 - (long)$$6;
      if ($$8 == 0L) {
         throw ERROR_RANGE_TOO_SMALL.create();
      } else if ($$8 >= 2147483647L) {
         throw ERROR_RANGE_TOO_LARGE.create();
      } else {
         int $$9 = Mth.randomBetweenInclusive($$4, $$6, $$7);
         if ($$3) {
            $$0.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("commands.random.roll", $$0.getDisplayName(), $$9, $$6, $$7), false);
         } else {
            $$0.sendSuccess(() -> Component.translatable("commands.random.sample.success", $$9), false);
         }

         return $$9;
      }
   }

   private static int resetSequence(CommandSourceStack $$0, ResourceLocation $$1) throws CommandSyntaxException {
      $$0.getLevel().getRandomSequences().reset($$1);
      $$0.sendSuccess(() -> Component.translatable("commands.random.reset.success", Component.translationArg($$1)), false);
      return 1;
   }

   private static int resetSequence(CommandSourceStack $$0, ResourceLocation $$1, int $$2, boolean $$3, boolean $$4) throws CommandSyntaxException {
      $$0.getLevel().getRandomSequences().reset($$1, $$2, $$3, $$4);
      $$0.sendSuccess(() -> Component.translatable("commands.random.reset.success", Component.translationArg($$1)), false);
      return 1;
   }

   private static int resetAllSequences(CommandSourceStack $$0) {
      int $$1 = $$0.getLevel().getRandomSequences().clear();
      $$0.sendSuccess(() -> Component.translatable("commands.random.reset.all.success", $$1), false);
      return $$1;
   }

   private static int resetAllSequencesAndSetNewDefaults(CommandSourceStack $$0, int $$1, boolean $$2, boolean $$3) {
      RandomSequences $$4 = $$0.getLevel().getRandomSequences();
      $$4.setSeedDefaults($$1, $$2, $$3);
      int $$5 = $$4.clear();
      $$0.sendSuccess(() -> Component.translatable("commands.random.reset.all.success", $$5), false);
      return $$5;
   }
}
