package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;

public class SeedCommand {
   public static void register(CommandDispatcher<CommandSourceStack> $$0, boolean $$1) {
      $$0.register(Commands.literal("seed").requires(Commands.hasPermission($$1 ? 2 : 0)).executes($$0x -> {
         long $$1xx = ((CommandSourceStack)$$0x.getSource()).getLevel().getSeed();
         Component $$2 = ComponentUtils.copyOnClickText(String.valueOf($$1xx));
         ((CommandSourceStack)$$0x.getSource()).sendSuccess(() -> Component.translatable("commands.seed.success", $$2), false);
         return (int)$$1xx;
      }));
   }
}
