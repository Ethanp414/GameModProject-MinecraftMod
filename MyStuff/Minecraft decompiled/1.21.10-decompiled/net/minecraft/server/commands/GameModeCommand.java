package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

public class GameModeCommand {
   public static final int PERMISSION_LEVEL = 2;

   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      $$0.register(
         Commands.literal("gamemode")
            .requires(Commands.hasPermission(2))
            .then(
               Commands.argument("gamemode", GameModeArgument.gameMode())
                  .executes(
                     $$0x -> setMode(
                           $$0x,
                           Collections.singleton(((CommandSourceStack)$$0x.getSource()).getPlayerOrException()),
                           GameModeArgument.getGameMode($$0x, "gamemode")
                        )
                  )
                  .then(
                     Commands.argument("target", EntityArgument.players())
                        .executes($$0x -> setMode($$0x, EntityArgument.getPlayers($$0x, "target"), GameModeArgument.getGameMode($$0x, "gamemode")))
                  )
            )
      );
   }

   private static void logGamemodeChange(CommandSourceStack $$0, ServerPlayer $$1, GameType $$2) {
      Component $$3 = Component.translatable("gameMode." + $$2.getName());
      if ($$0.getEntity() == $$1) {
         $$0.sendSuccess(() -> Component.translatable("commands.gamemode.success.self", $$3), true);
      } else {
         if ($$0.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            $$1.sendSystemMessage(Component.translatable("gameMode.changed", $$3));
         }

         $$0.sendSuccess(() -> Component.translatable("commands.gamemode.success.other", $$1.getDisplayName(), $$3), true);
      }
   }

   private static int setMode(CommandContext<CommandSourceStack> $$0, Collection<ServerPlayer> $$1, GameType $$2) {
      int $$3 = 0;

      for(ServerPlayer $$4 : $$1) {
         if (setGameMode($$0.getSource(), $$4, $$2)) {
            ++$$3;
         }
      }

      return $$3;
   }

   public static void setGameMode(ServerPlayer $$0, GameType $$1) {
      setGameMode($$0.createCommandSourceStack(), $$0, $$1);
   }

   private static boolean setGameMode(CommandSourceStack $$0, ServerPlayer $$1, GameType $$2) {
      if ($$1.setGameMode($$2)) {
         logGamemodeChange($$0, $$1, $$2);
         return true;
      } else {
         return false;
      }
   }
}
