package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.players.PlayerList;

public class SayCommand {
   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      $$0.register(Commands.literal("say").requires(Commands.hasPermission(2)).then(Commands.argument("message", MessageArgument.message()).executes($$0x -> {
         MessageArgument.resolveChatMessage($$0x, "message", $$1 -> {
            CommandSourceStack $$2 = (CommandSourceStack)$$0x.getSource();
            PlayerList $$3 = $$2.getServer().getPlayerList();
            $$3.broadcastChatMessage($$1, $$2, ChatType.bind(ChatType.SAY_COMMAND, $$2));
         });
         return 1;
      })));
   }
}
