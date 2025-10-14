package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandSourceStack implements ExecutionCommandSource<CommandSourceStack>, PermissionSource, SharedSuggestionProvider {
   public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(Component.translatable("permissions.requires.player"));
   public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType(Component.translatable("permissions.requires.entity"));
   private final CommandSource source;
   private final Vec3 worldPosition;
   private final ServerLevel level;
   private final int permissionLevel;
   private final String textName;
   private final Component displayName;
   private final MinecraftServer server;
   private final boolean silent;
   @Nullable
   private final Entity entity;
   private final CommandResultCallback resultCallback;
   private final EntityAnchorArgument.Anchor anchor;
   private final Vec2 rotation;
   private final CommandSigningContext signingContext;
   private final TaskChainer chatMessageChainer;

   public CommandSourceStack(
      CommandSource $$0, Vec3 $$1, Vec2 $$2, ServerLevel $$3, int $$4, String $$5, Component $$6, MinecraftServer $$7, @Nullable Entity $$8
   ) {
      this(
         $$0,
         $$1,
         $$2,
         $$3,
         $$4,
         $$5,
         $$6,
         $$7,
         $$8,
         false,
         CommandResultCallback.EMPTY,
         EntityAnchorArgument.Anchor.FEET,
         CommandSigningContext.ANONYMOUS,
         TaskChainer.immediate($$7)
      );
   }

   protected CommandSourceStack(
      CommandSource $$0,
      Vec3 $$1,
      Vec2 $$2,
      ServerLevel $$3,
      int $$4,
      String $$5,
      Component $$6,
      MinecraftServer $$7,
      @Nullable Entity $$8,
      boolean $$9,
      CommandResultCallback $$10,
      EntityAnchorArgument.Anchor $$11,
      CommandSigningContext $$12,
      TaskChainer $$13
   ) {
      this.source = $$0;
      this.worldPosition = $$1;
      this.level = $$3;
      this.silent = $$9;
      this.entity = $$8;
      this.permissionLevel = $$4;
      this.textName = $$5;
      this.displayName = $$6;
      this.server = $$7;
      this.resultCallback = $$10;
      this.anchor = $$11;
      this.rotation = $$2;
      this.signingContext = $$12;
      this.chatMessageChainer = $$13;
   }

   public CommandSourceStack withSource(CommandSource $$0) {
      return this.source == $$0
         ? this
         : new CommandSourceStack(
            $$0,
            this.worldPosition,
            this.rotation,
            this.level,
            this.permissionLevel,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            this.resultCallback,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         );
   }

   public CommandSourceStack withEntity(Entity $$0) {
      return this.entity == $$0
         ? this
         : new CommandSourceStack(
            this.source,
            this.worldPosition,
            this.rotation,
            this.level,
            this.permissionLevel,
            $$0.getPlainTextName(),
            $$0.getDisplayName(),
            this.server,
            $$0,
            this.silent,
            this.resultCallback,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         );
   }

   public CommandSourceStack withPosition(Vec3 $$0) {
      return this.worldPosition.equals($$0)
         ? this
         : new CommandSourceStack(
            this.source,
            $$0,
            this.rotation,
            this.level,
            this.permissionLevel,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            this.resultCallback,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         );
   }

   public CommandSourceStack withRotation(Vec2 $$0) {
      return this.rotation.equals($$0)
         ? this
         : new CommandSourceStack(
            this.source,
            this.worldPosition,
            $$0,
            this.level,
            this.permissionLevel,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            this.resultCallback,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         );
   }

   public CommandSourceStack withCallback(CommandResultCallback $$0) {
      return Objects.equals(this.resultCallback, $$0)
         ? this
         : new CommandSourceStack(
            this.source,
            this.worldPosition,
            this.rotation,
            this.level,
            this.permissionLevel,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            $$0,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         );
   }

   public CommandSourceStack withCallback(CommandResultCallback $$0, BinaryOperator<CommandResultCallback> $$1) {
      CommandResultCallback $$2 = (CommandResultCallback)$$1.apply(this.resultCallback, $$0);
      return this.withCallback($$2);
   }

   public CommandSourceStack withSuppressedOutput() {
      return !this.silent && !this.source.alwaysAccepts()
         ? new CommandSourceStack(
            this.source,
            this.worldPosition,
            this.rotation,
            this.level,
            this.permissionLevel,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            true,
            this.resultCallback,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         )
         : this;
   }

   public CommandSourceStack withPermission(int $$0) {
      return $$0 == this.permissionLevel
         ? this
         : new CommandSourceStack(
            this.source,
            this.worldPosition,
            this.rotation,
            this.level,
            $$0,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            this.resultCallback,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         );
   }

   public CommandSourceStack withMaximumPermission(int $$0) {
      return $$0 <= this.permissionLevel
         ? this
         : new CommandSourceStack(
            this.source,
            this.worldPosition,
            this.rotation,
            this.level,
            $$0,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            this.resultCallback,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         );
   }

   public CommandSourceStack withAnchor(EntityAnchorArgument.Anchor $$0) {
      return $$0 == this.anchor
         ? this
         : new CommandSourceStack(
            this.source,
            this.worldPosition,
            this.rotation,
            this.level,
            this.permissionLevel,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            this.resultCallback,
            $$0,
            this.signingContext,
            this.chatMessageChainer
         );
   }

   public CommandSourceStack withLevel(ServerLevel $$0) {
      if ($$0 == this.level) {
         return this;
      } else {
         double $$1 = DimensionType.getTeleportationScale(this.level.dimensionType(), $$0.dimensionType());
         Vec3 $$2 = new Vec3(this.worldPosition.x * $$1, this.worldPosition.y, this.worldPosition.z * $$1);
         return new CommandSourceStack(
            this.source,
            $$2,
            this.rotation,
            $$0,
            this.permissionLevel,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            this.resultCallback,
            this.anchor,
            this.signingContext,
            this.chatMessageChainer
         );
      }
   }

   public CommandSourceStack facing(Entity $$0, EntityAnchorArgument.Anchor $$1) {
      return this.facing($$1.apply($$0));
   }

   public CommandSourceStack facing(Vec3 $$0) {
      Vec3 $$1 = this.anchor.apply(this);
      double $$2 = $$0.x - $$1.x;
      double $$3 = $$0.y - $$1.y;
      double $$4 = $$0.z - $$1.z;
      double $$5 = Math.sqrt($$2 * $$2 + $$4 * $$4);
      float $$6 = Mth.wrapDegrees((float)(-(Mth.atan2($$3, $$5) * 180.0F / (float)Math.PI)));
      float $$7 = Mth.wrapDegrees((float)(Mth.atan2($$4, $$2) * 180.0F / (float)Math.PI) - 90.0F);
      return this.withRotation(new Vec2($$6, $$7));
   }

   public CommandSourceStack withSigningContext(CommandSigningContext $$0, TaskChainer $$1) {
      return $$0 == this.signingContext && $$1 == this.chatMessageChainer
         ? this
         : new CommandSourceStack(
            this.source,
            this.worldPosition,
            this.rotation,
            this.level,
            this.permissionLevel,
            this.textName,
            this.displayName,
            this.server,
            this.entity,
            this.silent,
            this.resultCallback,
            this.anchor,
            $$0,
            $$1
         );
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public String getTextName() {
      return this.textName;
   }

   @Override
   public boolean hasPermission(int $$0) {
      return this.permissionLevel >= $$0;
   }

   public Vec3 getPosition() {
      return this.worldPosition;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity getEntityOrException() throws CommandSyntaxException {
      if (this.entity == null) {
         throw ERROR_NOT_ENTITY.create();
      } else {
         return this.entity;
      }
   }

   public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
      Entity var2 = this.entity;
      if (var2 instanceof ServerPlayer) {
         return (ServerPlayer)var2;
      } else {
         throw ERROR_NOT_PLAYER.create();
      }
   }

   @Nullable
   public ServerPlayer getPlayer() {
      Entity var2 = this.entity;
      return var2 instanceof ServerPlayer $$0 ? $$0 : null;
   }

   public boolean isPlayer() {
      return this.entity instanceof ServerPlayer;
   }

   public Vec2 getRotation() {
      return this.rotation;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Anchor getAnchor() {
      return this.anchor;
   }

   public CommandSigningContext getSigningContext() {
      return this.signingContext;
   }

   public TaskChainer getChatMessageChainer() {
      return this.chatMessageChainer;
   }

   public boolean shouldFilterMessageTo(ServerPlayer $$0) {
      ServerPlayer $$1 = this.getPlayer();
      if ($$0 == $$1) {
         return false;
      } else {
         return $$1 != null && $$1.isTextFilteringEnabled() || $$0.isTextFilteringEnabled();
      }
   }

   public void sendChatMessage(OutgoingChatMessage $$0, boolean $$1, ChatType.Bound $$2) {
      if (!this.silent) {
         ServerPlayer $$3 = this.getPlayer();
         if ($$3 != null) {
            $$3.sendChatMessage($$0, $$1, $$2);
         } else {
            this.source.sendSystemMessage($$2.decorate($$0.content()));
         }
      }
   }

   public void sendSystemMessage(Component $$0) {
      if (!this.silent) {
         ServerPlayer $$1 = this.getPlayer();
         if ($$1 != null) {
            $$1.sendSystemMessage($$0);
         } else {
            this.source.sendSystemMessage($$0);
         }
      }
   }

   public void sendSuccess(Supplier<Component> $$0, boolean $$1) {
      boolean $$2 = this.source.acceptsSuccess() && !this.silent;
      boolean $$3 = $$1 && this.source.shouldInformAdmins() && !this.silent;
      if ($$2 || $$3) {
         Component $$4 = (Component)$$0.get();
         if ($$2) {
            this.source.sendSystemMessage($$4);
         }

         if ($$3) {
            this.broadcastToAdmins($$4);
         }
      }
   }

   private void broadcastToAdmins(Component $$0) {
      Component $$1 = Component.translatable("chat.type.admin", this.getDisplayName(), $$0).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
      if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
         for(ServerPlayer $$2 : this.server.getPlayerList().getPlayers()) {
            if ($$2.commandSource() != this.source && this.server.getPlayerList().isOp($$2.nameAndId())) {
               $$2.sendSystemMessage($$1);
            }
         }
      }

      if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
         this.server.sendSystemMessage($$1);
      }
   }

   public void sendFailure(Component $$0) {
      if (this.source.acceptsFailure() && !this.silent) {
         this.source.sendSystemMessage(Component.empty().append($$0).withStyle(ChatFormatting.RED));
      }
   }

   @Override
   public CommandResultCallback callback() {
      return this.resultCallback;
   }

   @Override
   public Collection<String> getOnlinePlayerNames() {
      return Lists.newArrayList(this.server.getPlayerNames());
   }

   @Override
   public Collection<String> getAllTeams() {
      return this.server.getScoreboard().getTeamNames();
   }

   @Override
   public Stream<ResourceLocation> getAvailableSounds() {
      return BuiltInRegistries.SOUND_EVENT.stream().map(SoundEvent::location);
   }

   @Override
   public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> $$0) {
      return Suggestions.empty();
   }

   @Override
   public CompletableFuture<Suggestions> suggestRegistryElements(
      ResourceKey<? extends Registry<?>> $$0, SharedSuggestionProvider.ElementSuggestionType $$1, SuggestionsBuilder $$2, CommandContext<?> $$3
   ) {
      if ($$0 == Registries.RECIPE) {
         return SharedSuggestionProvider.suggestResource(this.server.getRecipeManager().getRecipes().stream().map($$0x -> $$0x.id().location()), $$2);
      } else if ($$0 == Registries.ADVANCEMENT) {
         Collection<AdvancementHolder> $$4 = this.server.getAdvancements().getAllAdvancements();
         return SharedSuggestionProvider.suggestResource($$4.stream().map(AdvancementHolder::id), $$2);
      } else {
         return (CompletableFuture<Suggestions>)this.getLookup($$0).map($$2x -> {
            this.suggestRegistryElements($$2x, $$1, $$2);
            return $$2.buildFuture();
         }).orElseGet(Suggestions::empty);
      }
   }

   private Optional<? extends HolderLookup<?>> getLookup(ResourceKey<? extends Registry<?>> $$0) {
      Optional<? extends Registry<?>> $$1 = this.registryAccess().lookup($$0);
      return $$1.isPresent() ? $$1 : this.server.reloadableRegistries().lookup().lookup($$0);
   }

   @Override
   public Set<ResourceKey<Level>> levels() {
      return this.server.levelKeys();
   }

   @Override
   public RegistryAccess registryAccess() {
      return this.server.registryAccess();
   }

   @Override
   public FeatureFlagSet enabledFeatures() {
      return this.level.enabledFeatures();
   }

   @Override
   public CommandDispatcher<CommandSourceStack> dispatcher() {
      return this.getServer().getFunctions().getDispatcher();
   }

   @Override
   public void handleError(CommandExceptionType $$0, Message $$1, boolean $$2, @Nullable TraceCallbacks $$3) {
      if ($$3 != null) {
         $$3.onError($$1.getString());
      }

      if (!$$2) {
         this.sendFailure(ComponentUtils.fromMessage($$1));
      }
   }

   @Override
   public boolean isSilent() {
      return this.silent;
   }
}
