package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;

public class GameRuleCommand {
   public static void register(CommandDispatcher<CommandSourceStack> $$0, CommandBuildContext $$1) {
      final LiteralArgumentBuilder<CommandSourceStack> $$2 = Commands.literal("gamerule").requires(Commands.hasPermission(2));
      new GameRules($$1.enabledFeatures())
         .visitGameRuleTypes(
            new GameRules.GameRuleTypeVisitor() {
               @Override
               public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> $$0, GameRules.Type<T> $$1) {
                  LiteralArgumentBuilder<CommandSourceStack> $$2x = Commands.literal($$0.getId());
                  $$2.then(
                     $$2x.executes($$1x -> GameRuleCommand.queryRule((CommandSourceStack)$$1x.getSource(), $$0))
                        .then($$1.createArgument("value").executes($$1x -> GameRuleCommand.setRule($$1x, $$0)))
                  );
               }
            }
         );
      $$0.register($$2);
   }

   static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> $$0, GameRules.Key<T> $$1) {
      CommandSourceStack $$2 = $$0.getSource();
      T $$3 = $$2.getServer().getGameRules().getRule($$1);
      $$3.setFromArgument($$0, "value");
      $$2.getServer().onGameRuleChanged($$1.getId(), $$3);
      $$2.sendSuccess(() -> Component.translatable("commands.gamerule.set", $$1.getId(), $$3.toString()), true);
      return $$3.getCommandResult();
   }

   static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack $$0, GameRules.Key<T> $$1) {
      T $$2 = $$0.getServer().getGameRules().getRule($$1);
      $$0.sendSuccess(() -> Component.translatable("commands.gamerule.query", $$1.getId(), $$2.toString()), false);
      return $$2.getCommandResult();
   }
}
