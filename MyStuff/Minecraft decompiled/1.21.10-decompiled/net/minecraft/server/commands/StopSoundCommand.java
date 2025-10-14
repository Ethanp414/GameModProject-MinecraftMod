package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class StopSoundCommand {
   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      RequiredArgumentBuilder<CommandSourceStack, EntitySelector> $$1 = Commands.argument("targets", EntityArgument.players())
         .executes($$0x -> stopSound((CommandSourceStack)$$0x.getSource(), EntityArgument.getPlayers($$0x, "targets"), null, null))
         .then(
            Commands.literal("*")
               .then(
                  Commands.argument("sound", ResourceLocationArgument.id())
                     .suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS))
                     .executes(
                        $$0x -> stopSound(
                              (CommandSourceStack)$$0x.getSource(),
                              EntityArgument.getPlayers($$0x, "targets"),
                              null,
                              ResourceLocationArgument.getId($$0x, "sound")
                           )
                     )
               )
         );

      for(SoundSource $$2 : SoundSource.values()) {
         $$1.then(
            Commands.literal($$2.getName())
               .executes($$1x -> stopSound((CommandSourceStack)$$1x.getSource(), EntityArgument.getPlayers($$1x, "targets"), $$2, null))
               .then(
                  Commands.argument("sound", ResourceLocationArgument.id())
                     .suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS))
                     .executes(
                        $$1x -> stopSound(
                              (CommandSourceStack)$$1x.getSource(),
                              EntityArgument.getPlayers($$1x, "targets"),
                              $$2,
                              ResourceLocationArgument.getId($$1x, "sound")
                           )
                     )
               )
         );
      }

      $$0.register(Commands.literal("stopsound").requires(Commands.hasPermission(2)).then($$1));
   }

   private static int stopSound(CommandSourceStack $$0, Collection<ServerPlayer> $$1, @Nullable SoundSource $$2, @Nullable ResourceLocation $$3) {
      ClientboundStopSoundPacket $$4 = new ClientboundStopSoundPacket($$3, $$2);

      for(ServerPlayer $$5 : $$1) {
         $$5.connection.send($$4);
      }

      if ($$2 != null) {
         if ($$3 != null) {
            $$0.sendSuccess(() -> Component.translatable("commands.stopsound.success.source.sound", Component.translationArg($$3), $$2.getName()), true);
         } else {
            $$0.sendSuccess(() -> Component.translatable("commands.stopsound.success.source.any", $$2.getName()), true);
         }
      } else if ($$3 != null) {
         $$0.sendSuccess(() -> Component.translatable("commands.stopsound.success.sourceless.sound", Component.translationArg($$3)), true);
      } else {
         $$0.sendSuccess(() -> Component.translatable("commands.stopsound.success.sourceless.any"), true);
      }

      return $$1.size();
   }
}
