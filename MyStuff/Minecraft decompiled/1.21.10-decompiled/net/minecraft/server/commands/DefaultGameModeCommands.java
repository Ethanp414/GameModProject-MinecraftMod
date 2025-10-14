package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      $$0.register(
         Commands.literal("defaultgamemode")
            .requires(Commands.hasPermission(2))
            .then(
               Commands.argument("gamemode", GameModeArgument.gameMode())
                  .executes($$0x -> setMode((CommandSourceStack)$$0x.getSource(), GameModeArgument.getGameMode($$0x, "gamemode")))
            )
      );
   }

   private static int setMode(CommandSourceStack $$0, GameType $$1) {
      MinecraftServer $$2 = $$0.getServer();
      $$2.setDefaultGameType($$1);
      int $$3 = $$2.enforceGameTypeForPlayers($$2.getForcedGameType());
      $$0.sendSuccess(() -> Component.translatable("commands.defaultgamemode.success", $$1.getLongDisplayName()), true);
      return $$3;
   }
}
