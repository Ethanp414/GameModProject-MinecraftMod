package net.minecraft.server.commands;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.Entity;

public class TagCommand {
   private static final SimpleCommandExceptionType ERROR_ADD_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.tag.add.failed"));
   private static final SimpleCommandExceptionType ERROR_REMOVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.tag.remove.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> $$0) {
      $$0.register(
         Commands.literal("tag")
            .requires(Commands.hasPermission(2))
            .then(
               Commands.argument("targets", EntityArgument.entities())
                  .then(
                     Commands.literal("add")
                        .then(
                           Commands.argument("name", StringArgumentType.word())
                              .executes(
                                 $$0x -> addTag(
                                       (CommandSourceStack)$$0x.getSource(),
                                       EntityArgument.getEntities($$0x, "targets"),
                                       StringArgumentType.getString($$0x, "name")
                                    )
                              )
                        )
                  )
                  .then(
                     Commands.literal("remove")
                        .then(
                           Commands.argument("name", StringArgumentType.word())
                              .suggests(($$0x, $$1) -> SharedSuggestionProvider.suggest(getTags(EntityArgument.getEntities($$0x, "targets")), $$1))
                              .executes(
                                 $$0x -> removeTag(
                                       (CommandSourceStack)$$0x.getSource(),
                                       EntityArgument.getEntities($$0x, "targets"),
                                       StringArgumentType.getString($$0x, "name")
                                    )
                              )
                        )
                  )
                  .then(Commands.literal("list").executes($$0x -> listTags((CommandSourceStack)$$0x.getSource(), EntityArgument.getEntities($$0x, "targets"))))
            )
      );
   }

   private static Collection<String> getTags(Collection<? extends Entity> $$0) {
      Set<String> $$1 = Sets.newHashSet();

      for(Entity $$2 : $$0) {
         $$1.addAll($$2.getTags());
      }

      return $$1;
   }

   private static int addTag(CommandSourceStack $$0, Collection<? extends Entity> $$1, String $$2) throws CommandSyntaxException {
      int $$3 = 0;

      for(Entity $$4 : $$1) {
         if ($$4.addTag($$2)) {
            ++$$3;
         }
      }

      if ($$3 == 0) {
         throw ERROR_ADD_FAILED.create();
      } else {
         if ($$1.size() == 1) {
            $$0.sendSuccess(() -> Component.translatable("commands.tag.add.success.single", $$2, ((Entity)$$1.iterator().next()).getDisplayName()), true);
         } else {
            $$0.sendSuccess(() -> Component.translatable("commands.tag.add.success.multiple", $$2, $$1.size()), true);
         }

         return $$3;
      }
   }

   private static int removeTag(CommandSourceStack $$0, Collection<? extends Entity> $$1, String $$2) throws CommandSyntaxException {
      int $$3 = 0;

      for(Entity $$4 : $$1) {
         if ($$4.removeTag($$2)) {
            ++$$3;
         }
      }

      if ($$3 == 0) {
         throw ERROR_REMOVE_FAILED.create();
      } else {
         if ($$1.size() == 1) {
            $$0.sendSuccess(() -> Component.translatable("commands.tag.remove.success.single", $$2, ((Entity)$$1.iterator().next()).getDisplayName()), true);
         } else {
            $$0.sendSuccess(() -> Component.translatable("commands.tag.remove.success.multiple", $$2, $$1.size()), true);
         }

         return $$3;
      }
   }

   private static int listTags(CommandSourceStack $$0, Collection<? extends Entity> $$1) {
      Set<String> $$2 = Sets.newHashSet();

      for(Entity $$3 : $$1) {
         $$2.addAll($$3.getTags());
      }

      if ($$1.size() == 1) {
         Entity $$4 = (Entity)$$1.iterator().next();
         if ($$2.isEmpty()) {
            $$0.sendSuccess(() -> Component.translatable("commands.tag.list.single.empty", $$4.getDisplayName()), false);
         } else {
            $$0.sendSuccess(
               () -> Component.translatable("commands.tag.list.single.success", $$4.getDisplayName(), $$2.size(), ComponentUtils.formatList($$2)), false
            );
         }
      } else if ($$2.isEmpty()) {
         $$0.sendSuccess(() -> Component.translatable("commands.tag.list.multiple.empty", $$1.size()), false);
      } else {
         $$0.sendSuccess(() -> Component.translatable("commands.tag.list.multiple.success", $$1.size(), $$2.size(), ComponentUtils.formatList($$2)), false);
      }

      return $$2.size();
   }
}
