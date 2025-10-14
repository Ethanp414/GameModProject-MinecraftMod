package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.waypoints.WaypointTransmitter;

public class WaypointArgument {
   public static final SimpleCommandExceptionType ERROR_NOT_A_WAYPOINT = new SimpleCommandExceptionType(Component.translatable("argument.waypoint.invalid"));

   public static WaypointTransmitter getWaypoint(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      Entity $$2 = $$0.<EntitySelector>getArgument($$1, EntitySelector.class).findSingleEntity($$0.getSource());
      if ($$2 instanceof WaypointTransmitter) {
         return (WaypointTransmitter)$$2;
      } else {
         throw ERROR_NOT_A_WAYPOINT.create();
      }
   }
}
