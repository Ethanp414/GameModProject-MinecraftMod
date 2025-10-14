package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("commands.difficulty.failure", $$0)
   );

   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      LiteralArgumentBuilder<CommandSourceStack> $$1 = Commands.literal("difficulty");

      for(Difficulty $$2 : Difficulty.values()) {
         $$1.then(Commands.literal($$2.getKey()).executes($$1x -> setDifficulty((CommandSourceStack)$$1x.getSource(), $$2)));
      }

      $$0.register($$1.requires(Commands.hasPermission(2)).executes($$0x -> {
         Difficulty $$1xx = ((CommandSourceStack)$$0x.getSource()).getLevel().getDifficulty();
         ((CommandSourceStack)$$0x.getSource()).sendSuccess(() -> Component.translatable("commands.difficulty.query", $$1x.getDisplayName()), false);
         return $$1xx.getId();
      }));
   }

   public static int setDifficulty(CommandSourceStack $$0, Difficulty $$1) throws CommandSyntaxException {
      MinecraftServer $$2 = $$0.getServer();
      if ($$2.getWorldData().getDifficulty() == $$1) {
         throw ERROR_ALREADY_DIFFICULT.create($$1.getKey());
      } else {
         $$2.setDifficulty($$1, true);
         $$0.sendSuccess(() -> Component.translatable("commands.difficulty.success", $$1.getDisplayName()), true);
         return 0;
      }
   }
}
