package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;

public class DebugPathCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_MOB = new SimpleCommandExceptionType(Component.literal("Source is not a mob"));
   private static final SimpleCommandExceptionType ERROR_NO_PATH = new SimpleCommandExceptionType(Component.literal("Path not found"));
   private static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.literal("Target not reached"));

   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      $$0.register(
         Commands.literal("debugpath")
            .requires(Commands.hasPermission(2))
            .then(
               Commands.argument("to", BlockPosArgument.blockPos())
                  .executes($$0x -> fillBlocks((CommandSourceStack)$$0x.getSource(), BlockPosArgument.getLoadedBlockPos($$0x, "to")))
            )
      );
   }

   private static int fillBlocks(CommandSourceStack $$0, BlockPos $$1) throws CommandSyntaxException {
      Entity $$2 = $$0.getEntity();
      if (!($$2 instanceof Mob)) {
         throw ERROR_NOT_MOB.create();
      } else {
         Mob $$3 = (Mob)$$2;
         PathNavigation $$4 = new GroundPathNavigation($$3, $$0.getLevel());
         Path $$5 = $$4.createPath($$1, 0);
         if ($$5 == null) {
            throw ERROR_NO_PATH.create();
         } else if (!$$5.canReach()) {
            throw ERROR_NOT_COMPLETE.create();
         } else {
            $$0.sendSuccess(() -> Component.literal("Made path"), true);
            return 1;
         }
      }
   }
}
