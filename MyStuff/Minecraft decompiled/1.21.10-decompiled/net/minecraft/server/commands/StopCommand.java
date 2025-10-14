package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class StopCommand {
   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      $$0.register(Commands.literal("stop").requires(Commands.hasPermission(4)).executes($$0x -> {
         ((CommandSourceStack)$$0x.getSource()).sendSuccess(() -> Component.translatable("commands.stop.stopping"), true);
         ((CommandSourceStack)$$0x.getSource()).getServer().halt(false);
         return 1;
      }));
   }
}
