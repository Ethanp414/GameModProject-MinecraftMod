package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class GameRules {
   public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<GameRules.Key<?>, GameRules.Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing($$0 -> $$0.id));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOFIRETICK = register(
      "doFireTick", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ALLOWFIRETICKAWAYFROMPLAYERS = register(
      "allowFireTicksAwayFromPlayer", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_MOBGRIEFING = register(
      "mobGriefing", GameRules.Category.MOBS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEPINVENTORY = register(
      "keepInventory", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBSPAWNING = register(
      "doMobSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBLOOT = register(
      "doMobLoot", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_PROJECTILESCANBREAKBLOCKS = register(
      "projectilesCanBreakBlocks", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOBLOCKDROPS = register(
      "doTileDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOENTITYDROPS = register(
      "doEntityDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_COMMANDBLOCKOUTPUT = register(
      "commandBlockOutput", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_NATURAL_REGENERATION = register(
      "naturalRegeneration", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DAYLIGHT = register(
      "doDaylightCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(!SharedConstants.DEBUG_WORLD_RECREATE)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LOGADMINCOMMANDS = register(
      "logAdminCommands", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SHOWDEATHMESSAGES = register(
      "showDeathMessages", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_RANDOMTICKING = register(
      "randomTickSpeed", GameRules.Category.UPDATES, GameRules.IntegerValue.create(3)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SENDCOMMANDFEEDBACK = register(
      "sendCommandFeedback", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_REDUCEDDEBUGINFO = register(
      "reducedDebugInfo", GameRules.Category.MISC, GameRules.BooleanValue.create(false, ($$0, $$1) -> {
         byte $$2 = (byte)($$1.get() ? 22 : 23);
   
         for(ServerPlayer $$3 : $$0.getPlayerList().getPlayers()) {
            $$3.connection.send(new ClientboundEntityEventPacket($$3, $$2));
         }
      })
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SPECTATORSGENERATECHUNKS = register(
      "spectatorsGenerateChunks", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_SPAWN_RADIUS = register(
      "spawnRadius", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_PLAYER_MOVEMENT_CHECK = register(
      "disablePlayerMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = register(
      "disableElytraMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_ENTITY_CRAMMING = register(
      "maxEntityCramming", GameRules.Category.MOBS, GameRules.IntegerValue.create(24)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_WEATHER_CYCLE = register(
      "doWeatherCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(!SharedConstants.DEBUG_WORLD_RECREATE)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LIMITED_CRAFTING = register(
      "doLimitedCrafting", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false, ($$0, $$1) -> {
         for(ServerPlayer $$2 : $$0.getPlayerList().getPlayers()) {
            $$2.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.LIMITED_CRAFTING, $$1.get() ? 1.0F : 0.0F));
         }
      })
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH = register(
      "maxCommandChainLength", GameRules.Category.MISC, GameRules.IntegerValue.create(65536)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_COMMAND_FORK_COUNT = register(
      "maxCommandForkCount", GameRules.Category.MISC, GameRules.IntegerValue.create(65536)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_COMMAND_MODIFICATION_BLOCK_LIMIT = register(
      "commandModificationBlockLimit", GameRules.Category.MISC, GameRules.IntegerValue.create(32768)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS = register(
      "announceAdvancements", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_RAIDS = register(
      "disableRaids", GameRules.Category.MOBS, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOINSOMNIA = register(
      "doInsomnia", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_IMMEDIATE_RESPAWN = register(
      "doImmediateRespawn", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false, ($$0, $$1) -> {
         for(ServerPlayer $$2 : $$0.getPlayerList().getPlayers()) {
            $$2.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, $$1.get() ? 1.0F : 0.0F));
         }
      })
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY = register(
      "playersNetherPortalDefaultDelay", GameRules.Category.PLAYER, GameRules.IntegerValue.create(80)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY = register(
      "playersNetherPortalCreativeDelay", GameRules.Category.PLAYER, GameRules.IntegerValue.create(0)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DROWNING_DAMAGE = register(
      "drowningDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FALL_DAMAGE = register(
      "fallDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FIRE_DAMAGE = register(
      "fireDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FREEZE_DAMAGE = register(
      "freezeDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_PATROL_SPAWNING = register(
      "doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_TRADER_SPAWNING = register(
      "doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_WARDEN_SPAWNING = register(
      "doWardenSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FORGIVE_DEAD_PLAYERS = register(
      "forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_UNIVERSAL_ANGER = register(
      "universalAnger", GameRules.Category.MOBS, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_PLAYERS_SLEEPING_PERCENTAGE = register(
      "playersSleepingPercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_BLOCK_EXPLOSION_DROP_DECAY = register(
      "blockExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_MOB_EXPLOSION_DROP_DECAY = register(
      "mobExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_TNT_EXPLOSION_DROP_DECAY = register(
      "tntExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_SNOW_ACCUMULATION_HEIGHT = register(
      "snowAccumulationHeight", GameRules.Category.UPDATES, GameRules.IntegerValue.create(1)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_WATER_SOURCE_CONVERSION = register(
      "waterSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LAVA_SOURCE_CONVERSION = register(
      "lavaSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_GLOBAL_SOUND_EVENTS = register(
      "globalSoundEvents", GameRules.Category.MISC, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_VINES_SPREAD = register(
      "doVinesSpread", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ENDER_PEARLS_VANISH_ON_DEATH = register(
      "enderPearlsVanishOnDeath", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MINECART_MAX_SPEED = register(
      "minecartMaxSpeed",
      GameRules.Category.MISC,
      GameRules.IntegerValue.create(8, 1, 1000, FeatureFlagSet.of(FeatureFlags.MINECART_IMPROVEMENTS), ($$0, $$1) -> {
      })
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_TNT_EXPLODES = register(
      "tntExplodes", GameRules.Category.MISC, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LOCATOR_BAR = register(
      "locatorBar", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true, ($$0, $$1) -> $$0.getAllLevels().forEach($$1x -> {
            ServerWaypointManager $$2 = $$1x.getWaypointManager();
            if ($$1.get()) {
               $$1x.players().forEach($$2::updatePlayer);
            } else {
               $$2.breakAllConnections();
            }
         }))
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_PVP = register("pvp", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ALLOW_NETHER = register(
      "allowEnteringNetherUsingPortals", GameRules.Category.MISC, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SPAWN_MONSTERS = register(
      "spawnMonsters", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true, ($$0, $$1) -> $$0.updateMobSpawningFlags())
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_COMMAND_BLOCKS_ENABLED = register(
      "commandBlocksEnabled", GameRules.Category.MISC, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SPAWNER_BLOCKS_ENABLED = register(
      "spawnerBlocksEnabled", GameRules.Category.MISC, GameRules.BooleanValue.create(true)
   );
   private final Map<GameRules.Key<?>, GameRules.Value<?>> rules;
   private final FeatureFlagSet enabledFeatures;

   public static <T extends GameRules.Value<T>> GameRules.Type<T> getType(GameRules.Key<T> $$0) {
      return (GameRules.Type<T>)GAME_RULE_TYPES.get($$0);
   }

   public static <T extends GameRules.Value<T>> Codec<GameRules.Key<T>> keyCodec(Class<T> $$0) {
      return Codec.STRING
         .comapFlatMap(
            $$1 -> (DataResult)GAME_RULE_TYPES.entrySet()
                  .stream()
                  .filter($$1x -> ((GameRules.Type)$$1x.getValue()).valueClass == $$0)
                  .map(Entry::getKey)
                  .filter($$1x -> $$1x.getId().equals($$1))
                  .map($$0xx -> $$0xx)
                  .findFirst()
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Invalid game rule ID for type: " + $$1)),
            GameRules.Key::getId
         );
   }

   private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String $$0, GameRules.Category $$1, GameRules.Type<T> $$2) {
      GameRules.Key<T> $$3 = new GameRules.Key<>($$0, $$1);
      GameRules.Type<?> $$4 = (GameRules.Type)GAME_RULE_TYPES.put($$3, $$2);
      if ($$4 != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + $$0);
      } else {
         return $$3;
      }
   }

   public GameRules(FeatureFlagSet $$0, DynamicLike<?> $$1) {
      this($$0);
      this.loadFromTag($$1);
   }

   public GameRules(FeatureFlagSet $$0) {
      this(
         (Map<GameRules.Key<?>, GameRules.Value<?>>)availableRules($$0)
            .collect(ImmutableMap.toImmutableMap(Entry::getKey, $$0x -> ((GameRules.Type)$$0x.getValue()).createRule())),
         $$0
      );
   }

   public static Stream<Entry<GameRules.Key<?>, GameRules.Type<?>>> availableRules(FeatureFlagSet $$0) {
      return GAME_RULE_TYPES.entrySet().stream().filter($$1 -> ((GameRules.Type)$$1.getValue()).requiredFeatures.isSubsetOf($$0));
   }

   private GameRules(Map<GameRules.Key<?>, GameRules.Value<?>> $$0, FeatureFlagSet $$1) {
      this.rules = $$0;
      this.enabledFeatures = $$1;
   }

   public <T extends GameRules.Value<T>> T getRule(GameRules.Key<T> $$0) {
      T $$1 = (T)this.rules.get($$0);
      if ($$1 == null) {
         throw new IllegalArgumentException("Tried to access invalid game rule");
      } else {
         return $$1;
      }
   }

   public CompoundTag createTag() {
      CompoundTag $$0 = new CompoundTag();
      this.rules.forEach(($$1, $$2) -> $$0.putString($$1.id, $$2.serialize()));
      return $$0;
   }

   private void loadFromTag(DynamicLike<?> $$0) {
      this.rules.forEach(($$1, $$2) -> $$0.get($$1.id).asString().ifSuccess($$2::deserialize));
   }

   public GameRules copy(FeatureFlagSet $$0) {
      return new GameRules(
         (Map<GameRules.Key<?>, GameRules.Value<?>>)availableRules($$0)
            .collect(
               ImmutableMap.toImmutableMap(
                  Entry::getKey,
                  $$0x -> this.rules.containsKey($$0x.getKey())
                        ? ((GameRules.Value)this.rules.get($$0x.getKey())).copy()
                        : ((GameRules.Type)$$0x.getValue()).createRule()
               )
            ),
         $$0
      );
   }

   public void visitGameRuleTypes(GameRules.GameRuleTypeVisitor $$0) {
      GAME_RULE_TYPES.forEach(($$1, $$2) -> this.callVisitorCap($$0, $$1, $$2));
   }

   private <T extends GameRules.Value<T>> void callVisitorCap(GameRules.GameRuleTypeVisitor $$0, GameRules.Key<?> $$1, GameRules.Type<?> $$2) {
      if ($$2.requiredFeatures.isSubsetOf(this.enabledFeatures)) {
         $$0.visit($$1, $$2);
         $$2.callVisitor($$0, $$1);
      }
   }

   public void assignFrom(GameRules $$0, @Nullable MinecraftServer $$1) {
      $$0.rules.keySet().forEach($$2 -> this.assignCap($$2, $$0, $$1));
   }

   private <T extends GameRules.Value<T>> void assignCap(GameRules.Key<T> $$0, GameRules $$1, @Nullable MinecraftServer $$2) {
      T $$3 = $$1.getRule($$0);
      this.<T>getRule($$0).setFrom($$3, $$2);
   }

   public boolean getBoolean(GameRules.Key<GameRules.BooleanValue> $$0) {
      return this.getRule($$0).get();
   }

   public int getInt(GameRules.Key<GameRules.IntegerValue> $$0) {
      return this.getRule($$0).get();
   }

   public static class BooleanValue extends GameRules.Value<GameRules.BooleanValue> {
      private boolean value;

      private static GameRules.Type<GameRules.BooleanValue> create(boolean $$0, BiConsumer<MinecraftServer, GameRules.BooleanValue> $$1, FeatureFlagSet $$2) {
         return new GameRules.Type<>(
            BoolArgumentType::bool,
            $$1x -> new GameRules.BooleanValue($$1x, $$0),
            $$1,
            GameRules.GameRuleTypeVisitor::visitBoolean,
            GameRules.BooleanValue.class,
            $$2
         );
      }

      static GameRules.Type<GameRules.BooleanValue> create(boolean $$0, BiConsumer<MinecraftServer, GameRules.BooleanValue> $$1) {
         return new GameRules.Type<>(
            BoolArgumentType::bool,
            $$1x -> new GameRules.BooleanValue($$1x, $$0),
            $$1,
            GameRules.GameRuleTypeVisitor::visitBoolean,
            GameRules.BooleanValue.class,
            FeatureFlagSet.of()
         );
      }

      public static GameRules.Type<GameRules.BooleanValue> create(boolean $$0) {
         return create($$0, ($$0x, $$1) -> {
         });
      }

      public BooleanValue(GameRules.Type<GameRules.BooleanValue> $$0, boolean $$1) {
         super($$0);
         this.value = $$1;
      }

      @Override
      protected void updateFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
         this.value = BoolArgumentType.getBool($$0, $$1);
      }

      public boolean get() {
         return this.value;
      }

      public void set(boolean $$0, @Nullable MinecraftServer $$1) {
         this.value = $$0;
         this.onChanged($$1);
      }

      @Override
      public String serialize() {
         return Boolean.toString(this.value);
      }

      @Override
      protected void deserialize(String $$0) {
         this.value = Boolean.parseBoolean($$0);
      }

      @Override
      public int getCommandResult() {
         return this.value ? 1 : 0;
      }

      protected GameRules.BooleanValue getSelf() {
         return this;
      }

      protected GameRules.BooleanValue copy() {
         return new GameRules.BooleanValue(this.type, this.value);
      }

      public void setFrom(GameRules.BooleanValue $$0, @Nullable MinecraftServer $$1) {
         this.value = $$0.value;
         this.onChanged($$1);
      }
   }

   public static enum Category {
      PLAYER("gamerule.category.player"),
      MOBS("gamerule.category.mobs"),
      SPAWNING("gamerule.category.spawning"),
      DROPS("gamerule.category.drops"),
      UPDATES("gamerule.category.updates"),
      CHAT("gamerule.category.chat"),
      MISC("gamerule.category.misc");

      private final String descriptionId;

      private Category(final String param3) {
         this.descriptionId = $$0;
      }

      public String getDescriptionId() {
         return this.descriptionId;
      }
   }

   public interface GameRuleTypeVisitor {
      default <T extends GameRules.Value<T>> void visit(GameRules.Key<T> $$0, GameRules.Type<T> $$1) {
      }

      default void visitBoolean(GameRules.Key<GameRules.BooleanValue> $$0, GameRules.Type<GameRules.BooleanValue> $$1) {
      }

      default void visitInteger(GameRules.Key<GameRules.IntegerValue> $$0, GameRules.Type<GameRules.IntegerValue> $$1) {
      }
   }

   public static class IntegerValue extends GameRules.Value<GameRules.IntegerValue> {
      private int value;

      private static GameRules.Type<GameRules.IntegerValue> create(int $$0, BiConsumer<MinecraftServer, GameRules.IntegerValue> $$1) {
         return new GameRules.Type<>(
            IntegerArgumentType::integer,
            $$1x -> new GameRules.IntegerValue($$1x, $$0),
            $$1,
            GameRules.GameRuleTypeVisitor::visitInteger,
            GameRules.IntegerValue.class,
            FeatureFlagSet.of()
         );
      }

      static GameRules.Type<GameRules.IntegerValue> create(
         int $$0, int $$1, int $$2, FeatureFlagSet $$3, BiConsumer<MinecraftServer, GameRules.IntegerValue> $$4
      ) {
         return new GameRules.Type<>(
            () -> IntegerArgumentType.integer($$1, $$2),
            $$1x -> new GameRules.IntegerValue($$1x, $$0),
            $$4,
            GameRules.GameRuleTypeVisitor::visitInteger,
            GameRules.IntegerValue.class,
            $$3
         );
      }

      public static GameRules.Type<GameRules.IntegerValue> create(int $$0) {
         return create($$0, ($$0x, $$1) -> {
         });
      }

      public IntegerValue(GameRules.Type<GameRules.IntegerValue> $$0, int $$1) {
         super($$0);
         this.value = $$1;
      }

      @Override
      protected void updateFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
         this.value = IntegerArgumentType.getInteger($$0, $$1);
      }

      public int get() {
         return this.value;
      }

      public void set(int $$0, @Nullable MinecraftServer $$1) {
         this.value = $$0;
         this.onChanged($$1);
      }

      @Override
      public String serialize() {
         return Integer.toString(this.value);
      }

      @Override
      protected void deserialize(String $$0) {
         this.value = safeParse($$0);
      }

      public boolean tryDeserialize(String $$0) {
         try {
            StringReader $$1 = new StringReader($$0);
            this.value = ((ArgumentType)this.type.argument.get()).parse($$1);
            return !$$1.canRead();
         } catch (CommandSyntaxException var3) {
            return false;
         }
      }

      private static int safeParse(String $$0) {
         if (!$$0.isEmpty()) {
            try {
               return Integer.parseInt($$0);
            } catch (NumberFormatException var2) {
               GameRules.LOGGER.warn("Failed to parse integer {}", $$0);
            }
         }

         return 0;
      }

      @Override
      public int getCommandResult() {
         return this.value;
      }

      protected GameRules.IntegerValue getSelf() {
         return this;
      }

      protected GameRules.IntegerValue copy() {
         return new GameRules.IntegerValue(this.type, this.value);
      }

      public void setFrom(GameRules.IntegerValue $$0, @Nullable MinecraftServer $$1) {
         this.value = $$0.value;
         this.onChanged($$1);
      }
   }

   public static final class Key<T extends GameRules.Value<T>> {
      final String id;
      private final GameRules.Category category;

      public Key(String $$0, GameRules.Category $$1) {
         this.id = $$0;
         this.category = $$1;
      }

      public String toString() {
         return this.id;
      }

      public boolean equals(Object $$0) {
         if (this == $$0) {
            return true;
         } else {
            return $$0 instanceof GameRules.Key && ((GameRules.Key)$$0).id.equals(this.id);
         }
      }

      public int hashCode() {
         return this.id.hashCode();
      }

      public String getId() {
         return this.id;
      }

      public String getDescriptionId() {
         return "gamerule." + this.id;
      }

      public GameRules.Category getCategory() {
         return this.category;
      }
   }

   public static class Type<T extends GameRules.Value<T>> {
      final Supplier<ArgumentType<?>> argument;
      private final Function<GameRules.Type<T>, T> constructor;
      final BiConsumer<MinecraftServer, T> callback;
      private final GameRules.VisitorCaller<T> visitorCaller;
      final Class<T> valueClass;
      final FeatureFlagSet requiredFeatures;

      Type(
         Supplier<ArgumentType<?>> $$0,
         Function<GameRules.Type<T>, T> $$1,
         BiConsumer<MinecraftServer, T> $$2,
         GameRules.VisitorCaller<T> $$3,
         Class<T> $$4,
         FeatureFlagSet $$5
      ) {
         this.argument = $$0;
         this.constructor = $$1;
         this.callback = $$2;
         this.visitorCaller = $$3;
         this.valueClass = $$4;
         this.requiredFeatures = $$5;
      }

      public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String $$0) {
         return Commands.argument($$0, (ArgumentType<T>)this.argument.get());
      }

      public T createRule() {
         return (T)this.constructor.apply(this);
      }

      public void callVisitor(GameRules.GameRuleTypeVisitor $$0, GameRules.Key<T> $$1) {
         this.visitorCaller.call($$0, $$1, this);
      }

      public FeatureFlagSet requiredFeatures() {
         return this.requiredFeatures;
      }
   }

   public abstract static class Value<T extends GameRules.Value<T>> {
      protected final GameRules.Type<T> type;

      public Value(GameRules.Type<T> $$0) {
         this.type = $$0;
      }

      protected abstract void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2);

      public void setFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
         this.updateFromArgument($$0, $$1);
         this.onChanged($$0.getSource().getServer());
      }

      protected void onChanged(@Nullable MinecraftServer $$0) {
         if ($$0 != null) {
            this.type.callback.accept($$0, this.getSelf());
         }
      }

      protected abstract void deserialize(String var1);

      public abstract String serialize();

      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract T getSelf();

      protected abstract T copy();

      public abstract void setFrom(T var1, @Nullable MinecraftServer var2);
   }

   interface VisitorCaller<T extends GameRules.Value<T>> {
      void call(GameRules.GameRuleTypeVisitor var1, GameRules.Key<T> var2, GameRules.Type<T> var3);
   }
}
