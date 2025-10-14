package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import org.slf4j.Logger;

public class EntityDataAccessor implements DataAccessor {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType(Component.translatable("commands.data.entity.invalid"));
   public static final Function<String, DataCommands.DataProvider> PROVIDER = $$0 -> new DataCommands.DataProvider() {
         @Override
         public DataAccessor access(CommandContext<CommandSourceStack> $$0x) throws CommandSyntaxException {
            return new EntityDataAccessor(EntityArgument.getEntity($$0, $$0));
         }

         @Override
         public ArgumentBuilder<CommandSourceStack, ?> wrap(
            ArgumentBuilder<CommandSourceStack, ?> $$0x, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> $$1
         ) {
            return $$0.then(
               Commands.literal("entity").then((ArgumentBuilder<CommandSourceStack, ?>)$$1.apply(Commands.argument($$0, EntityArgument.entity())))
            );
         }
      };
   private final Entity entity;

   public EntityDataAccessor(Entity $$0) {
      this.entity = $$0;
   }

   @Override
   public void setData(CompoundTag $$0) throws CommandSyntaxException {
      if (this.entity instanceof Player) {
         throw ERROR_NO_PLAYERS.create();
      } else {
         UUID $$1 = this.entity.getUUID();

         try (ProblemReporter.ScopedCollector $$2 = new ProblemReporter.ScopedCollector(this.entity.problemPath(), LOGGER)) {
            this.entity.load(TagValueInput.create($$2, this.entity.registryAccess(), $$0));
            this.entity.setUUID($$1);
         }
      }
   }

   @Override
   public CompoundTag getData() {
      return NbtPredicate.getEntityTagToCompare(this.entity);
   }

   @Override
   public Component getModifiedSuccess() {
      return Component.translatable("commands.data.entity.modified", this.entity.getDisplayName());
   }

   @Override
   public Component getPrintSuccess(Tag $$0) {
      return Component.translatable("commands.data.entity.query", this.entity.getDisplayName(), NbtUtils.toPrettyComponent($$0));
   }

   @Override
   public Component getPrintSuccess(NbtPathArgument.NbtPath $$0, double $$1, int $$2) {
      return Component.translatable("commands.data.entity.get", $$0.asString(), this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", $$1), $$2);
   }
}
