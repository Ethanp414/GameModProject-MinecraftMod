package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult.Error;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.item.component.ResolvableProfile;

public class FetchProfileCommand {
   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      $$0.register(
         Commands.literal("fetchprofile")
            .requires(Commands.hasPermission(2))
            .then(
               Commands.literal("name")
                  .then(
                     Commands.argument("name", StringArgumentType.greedyString())
                        .executes($$0x -> resolveName((CommandSourceStack)$$0x.getSource(), StringArgumentType.getString($$0x, "name")))
                  )
            )
            .then(
               Commands.literal("id")
                  .then(
                     Commands.argument("id", UuidArgument.uuid())
                        .executes($$0x -> resolveId((CommandSourceStack)$$0x.getSource(), UuidArgument.getUuid($$0x, "id")))
                  )
            )
      );
   }

   private static void reportResolvedProfile(CommandSourceStack $$0, GameProfile $$1, String $$2, Component $$3) {
      ResolvableProfile $$4 = ResolvableProfile.createResolved($$1);
      ResolvableProfile.CODEC
         .encodeStart(NbtOps.INSTANCE, $$4)
         .ifSuccess(
            $$4x -> {
               String $$5 = $$4x.toString();
               MutableComponent $$6 = Component.object(new PlayerSprite($$4, true));
               ComponentSerialization.CODEC
                  .encodeStart(NbtOps.INSTANCE, $$6)
                  .ifSuccess(
                     $$5x -> {
                        String $$6xx = $$5x.toString();
                        $$0.sendSuccess(
                           () -> {
                              Component $$5xxx = ComponentUtils.formatList(
                                 List.of(
                                    Component.translatable("commands.fetchprofile.copy_component")
                                       .withStyle($$1xxxx -> $$1xxxx.withClickEvent(new ClickEvent.CopyToClipboard($$5))),
                                    Component.translatable("commands.fetchprofile.give_item")
                                       .withStyle(
                                          $$1xxxx -> $$1xxxx.withClickEvent(new ClickEvent.RunCommand("give @s minecraft:player_head[profile=" + $$5 + "]"))
                                       ),
                                    Component.translatable("commands.fetchprofile.summon_mannequin")
                                       .withStyle(
                                          $$1xxxx -> $$1xxxx.withClickEvent(
                                                new ClickEvent.RunCommand("summon minecraft:mannequin ~ ~ ~ {profile:" + $$5 + "}")
                                             )
                                       ),
                                    Component.translatable("commands.fetchprofile.copy_text", $$6.withStyle(ChatFormatting.WHITE))
                                       .withStyle($$1xxxx -> $$1xxxx.withClickEvent(new ClickEvent.CopyToClipboard($$6x)))
                                 ),
                                 CommonComponents.SPACE,
                                 $$0xxxx -> ComponentUtils.wrapInSquareBrackets($$0xxxx.withStyle(ChatFormatting.GREEN))
                              );
                              return Component.translatable($$2, $$3, $$5xxx);
                           },
                           false
                        );
                     }
                  )
                  .ifError($$1xx -> $$0.sendFailure(Component.translatable("commands.fetchprofile.failed_to_serialize", $$1xx.message())));
            }
         )
         .ifError($$1x -> $$0.sendFailure(Component.translatable("commands.fetchprofile.failed_to_serialize", $$1x.message())));
   }

   private static int resolveName(CommandSourceStack $$0, String $$1) {
      MinecraftServer $$2 = $$0.getServer();
      ProfileResolver $$3 = $$2.services().profileResolver();
      Util.nonCriticalIoPool()
         .execute(
            () -> {
               Component $$4 = Component.literal($$1);
               Optional<GameProfile> $$5 = $$3.fetchByName($$1);
               $$2.execute(
                  () -> $$5.ifPresentOrElse(
                        $$2xxx -> reportResolvedProfile($$0, $$2xxx, "commands.fetchprofile.name.success", $$4),
                        () -> $$0.sendFailure(Component.translatable("commands.fetchprofile.name.failure", $$4))
                     )
               );
            }
         );
      return 1;
   }

   private static int resolveId(CommandSourceStack $$0, UUID $$1) {
      MinecraftServer $$2 = $$0.getServer();
      ProfileResolver $$3 = $$2.services().profileResolver();
      Util.nonCriticalIoPool()
         .execute(
            () -> {
               Component $$4 = Component.translationArg($$1);
               Optional<GameProfile> $$5 = $$3.fetchById($$1);
               $$2.execute(
                  () -> $$5.ifPresentOrElse(
                        $$2xxx -> reportResolvedProfile($$0, $$2xxx, "commands.fetchprofile.id.success", $$4),
                        () -> $$0.sendFailure(Component.translatable("commands.fetchprofile.id.failure", $$4))
                     )
               );
            }
         );
      return 1;
   }
}
