package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.HexColorArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.WaypointArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import net.minecraft.world.waypoints.WaypointTransmitter;

public class WaypointCommand {
   public static void register(CommandDispatcher<CommandSourceStack> $$0, CommandBuildContext $$1) {
      $$0.register(
         Commands.literal("waypoint")
            .requires(Commands.hasPermission(2))
            .then(Commands.literal("list").executes($$0x -> listWaypoints((CommandSourceStack)$$0x.getSource())))
            .then(
               Commands.literal("modify")
                  .then(
                     Commands.argument("waypoint", EntityArgument.entity())
                        .then(
                           Commands.literal("color")
                              .then(
                                 Commands.argument("color", ColorArgument.color())
                                    .executes(
                                       $$0x -> setWaypointColor(
                                             (CommandSourceStack)$$0x.getSource(),
                                             WaypointArgument.getWaypoint($$0x, "waypoint"),
                                             ColorArgument.getColor($$0x, "color")
                                          )
                                    )
                              )
                              .then(
                                 Commands.literal("hex")
                                    .then(
                                       Commands.argument("color", HexColorArgument.hexColor())
                                          .executes(
                                             $$0x -> setWaypointColor(
                                                   (CommandSourceStack)$$0x.getSource(),
                                                   WaypointArgument.getWaypoint($$0x, "waypoint"),
                                                   HexColorArgument.getHexColor($$0x, "color")
                                                )
                                          )
                                    )
                              )
                              .then(
                                 Commands.literal("reset")
                                    .executes($$0x -> resetWaypointColor((CommandSourceStack)$$0x.getSource(), WaypointArgument.getWaypoint($$0x, "waypoint")))
                              )
                        )
                        .then(
                           Commands.literal("style")
                              .then(
                                 Commands.literal("reset")
                                    .executes(
                                       $$0x -> setWaypointStyle(
                                             (CommandSourceStack)$$0x.getSource(), WaypointArgument.getWaypoint($$0x, "waypoint"), WaypointStyleAssets.DEFAULT
                                          )
                                    )
                              )
                              .then(
                                 Commands.literal("set")
                                    .then(
                                       Commands.argument("style", ResourceLocationArgument.id())
                                          .executes(
                                             $$0x -> setWaypointStyle(
                                                   (CommandSourceStack)$$0x.getSource(),
                                                   WaypointArgument.getWaypoint($$0x, "waypoint"),
                                                   ResourceKey.create(WaypointStyleAssets.ROOT_ID, ResourceLocationArgument.getId($$0x, "style"))
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int setWaypointStyle(CommandSourceStack $$0, WaypointTransmitter $$1, ResourceKey<WaypointStyleAsset> $$2) {
      mutateIcon($$0, $$1, $$1x -> $$1x.style = $$2);
      $$0.sendSuccess(() -> Component.translatable("commands.waypoint.modify.style"), false);
      return 0;
   }

   private static int setWaypointColor(CommandSourceStack $$0, WaypointTransmitter $$1, ChatFormatting $$2) {
      mutateIcon($$0, $$1, $$1x -> $$1x.color = Optional.of($$2.getColor()));
      $$0.sendSuccess(() -> Component.translatable("commands.waypoint.modify.color", Component.literal($$2.getName()).withStyle($$2)), false);
      return 0;
   }

   private static int setWaypointColor(CommandSourceStack $$0, WaypointTransmitter $$1, Integer $$2) {
      mutateIcon($$0, $$1, $$1x -> $$1x.color = Optional.of($$2));
      $$0.sendSuccess(
         () -> Component.translatable("commands.waypoint.modify.color", Component.literal(String.format("%06X", ARGB.color(0, $$2))).withColor($$2)), false
      );
      return 0;
   }

   private static int resetWaypointColor(CommandSourceStack $$0, WaypointTransmitter $$1) {
      mutateIcon($$0, $$1, $$0x -> $$0x.color = Optional.empty());
      $$0.sendSuccess(() -> Component.translatable("commands.waypoint.modify.color.reset"), false);
      return 0;
   }

   private static int listWaypoints(CommandSourceStack $$0) {
      ServerLevel $$1 = $$0.getLevel();
      Set<WaypointTransmitter> $$2 = $$1.getWaypointManager().transmitters();
      String $$3 = $$1.dimension().location().toString();
      if ($$2.isEmpty()) {
         $$0.sendSuccess(() -> Component.translatable("commands.waypoint.list.empty", $$3), false);
         return 0;
      } else {
         Component $$4 = ComponentUtils.formatList(
            $$2.stream()
               .map(
                  $$1x -> {
                     if ($$1x instanceof LivingEntity $$2xx) {
                        BlockPos $$3xx = $$2xx.blockPosition();
                        return $$2xx.getFeedbackDisplayName()
                           .copy()
                           .withStyle(
                              $$3xx -> $$3xx.withClickEvent(
                                       new ClickEvent.SuggestCommand(
                                          "/execute in " + $$3 + " run tp @s " + $$3x.getX() + " " + $$3x.getY() + " " + $$3x.getZ()
                                       )
                                    )
                                    .withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip")))
                                    .withColor($$1x.waypointIcon().color.orElse(-1))
                           );
                     } else {
                        return Component.literal($$1x.toString());
                     }
                  }
               )
               .toList(),
            Function.identity()
         );
         $$0.sendSuccess(() -> Component.translatable("commands.waypoint.list.success", $$2.size(), $$3, $$4), false);
         return $$2.size();
      }
   }

   private static void mutateIcon(CommandSourceStack $$0, WaypointTransmitter $$1, Consumer<Waypoint.Icon> $$2) {
      ServerLevel $$3 = $$0.getLevel();
      $$3.getWaypointManager().untrackWaypoint($$1);
      $$2.accept($$1.waypointIcon());
      $$3.getWaypointManager().trackWaypoint($$1);
   }
}
