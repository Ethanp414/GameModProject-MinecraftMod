package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.brigadier.StringReader;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportType;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceSelectorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggingLevelLoadListener;
import net.minecraft.server.notifications.EmptyNotificationService;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.server.players.UserNameToIdResolver;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.slf4j.Logger;

public class GameTestServer extends MinecraftServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int PROGRESS_REPORT_INTERVAL = 20;
   private static final int TEST_POSITION_RANGE = 14999992;
   private static final Services NO_SERVICES = new Services(
      null, ServicesKeySet.EMPTY, null, new GameTestServer.MockUserNameToIdResolver(), new GameTestServer.MockProfileResolver()
   );
   private static final FeatureFlagSet ENABLED_FEATURES = FeatureFlags.REGISTRY
      .allFlags()
      .subtract(FeatureFlagSet.of(FeatureFlags.REDSTONE_EXPERIMENTS, FeatureFlags.MINECART_IMPROVEMENTS));
   private final LocalSampleLogger sampleLogger = new LocalSampleLogger(4);
   private final Optional<String> testSelection;
   private final boolean verify;
   private List<GameTestBatch> testBatches = new ArrayList();
   private final Stopwatch stopwatch = Stopwatch.createUnstarted();
   private static final WorldOptions WORLD_OPTIONS = new WorldOptions(0L, false, false);
   @Nullable
   private MultipleTestTracker testTracker;

   public static GameTestServer create(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, Optional<String> $$3, boolean $$4) {
      $$2.reload();
      ArrayList<String> $$5 = new ArrayList($$2.getAvailableIds());
      $$5.remove("vanilla");
      $$5.addFirst("vanilla");
      WorldDataConfiguration $$6 = new WorldDataConfiguration(new DataPackConfig($$5, List.of()), ENABLED_FEATURES);
      LevelSettings $$7 = new LevelSettings("Test Level", GameType.CREATIVE, false, Difficulty.NORMAL, true, new GameRules(ENABLED_FEATURES), $$6);
      WorldLoader.PackConfig $$8 = new WorldLoader.PackConfig($$2, $$6, false, true);
      WorldLoader.InitConfig $$9 = new WorldLoader.InitConfig($$8, Commands.CommandSelection.DEDICATED, 4);

      try {
         LOGGER.debug("Starting resource loading");
         Stopwatch $$10 = Stopwatch.createStarted();
         WorldStem $$11 = (WorldStem)Util.blockUntilDone(
               $$2x -> WorldLoader.load(
                     $$9,
                     $$1xx -> {
                        Registry<LevelStem> $$2xxx = new MappedRegistry(Registries.LEVEL_STEM, Lifecycle.stable()).freeze();
                        WorldDimensions.Complete $$3xx = $$1xx.datapackWorldgen()
                           .<WorldPreset>lookupOrThrow(Registries.WORLD_PRESET)
                           .getOrThrow(WorldPresets.FLAT)
                           .value()
                           .createWorldDimensions()
                           .bake($$2xxx);
                        return new WorldLoader.DataLoadOutput<>(
                           new PrimaryLevelData($$7, WORLD_OPTIONS, $$3xx.specialWorldProperty(), $$3xx.lifecycle()), $$3xx.dimensionsRegistryAccess()
                        );
                     },
                     WorldStem::new,
                     Util.backgroundExecutor(),
                     $$2x
                  )
            )
            .get();
         $$10.stop();
         LOGGER.debug("Finished resource loading after {} ms", $$10.elapsed(TimeUnit.MILLISECONDS));
         return new GameTestServer($$0, $$1, $$2, $$11, $$3, $$4);
      } catch (Exception var12) {
         LOGGER.warn("Failed to load vanilla datapack, bit oops", var12);
         System.exit(-1);
         throw new IllegalStateException();
      }
   }

   private GameTestServer(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Optional<String> $$4, boolean $$5) {
      super($$0, $$1, $$2, $$3, Proxy.NO_PROXY, DataFixers.getDataFixer(), NO_SERVICES, LoggingLevelLoadListener.forDedicatedServer());
      this.testSelection = $$4;
      this.verify = $$5;
   }

   @Override
   public boolean initServer() {
      this.setPlayerList(new PlayerList(this, this, this.registries(), this.playerDataStorage, new EmptyNotificationService()) {
      });
      this.loadLevel();
      ServerLevel $$0 = this.overworld();
      this.testBatches = this.evaluateTestsToRun($$0);
      LOGGER.info("Started game test server");
      return true;
   }

   private List<GameTestBatch> evaluateTestsToRun(ServerLevel $$0) {
      Registry<GameTestInstance> $$1 = $$0.registryAccess().lookupOrThrow(Registries.TEST_INSTANCE);
      Collection<Holder.Reference<GameTestInstance>> $$2;
      GameTestBatchFactory.TestDecorator $$3;
      if (this.testSelection.isPresent()) {
         $$2 = getTestsForSelection($$0.registryAccess(), (String)this.testSelection.get())
            .filter($$0x -> !((GameTestInstance)$$0x.value()).manualOnly())
            .toList();
         if (this.verify) {
            $$3 = GameTestServer::rotateAndMultiply;
            LOGGER.info("Verify requested. Will run each test that matches {} {} times", this.testSelection.get(), 100 * Rotation.values().length);
         } else {
            $$3 = GameTestBatchFactory.DIRECT;
            LOGGER.info("Will run tests matching {} ({} tests)", this.testSelection.get(), $$2.size());
         }
      } else {
         $$2 = $$1.listElements().filter($$0x -> !((GameTestInstance)$$0x.value()).manualOnly()).toList();
         $$3 = GameTestBatchFactory.DIRECT;
      }

      return GameTestBatchFactory.divideIntoBatches($$2, $$3, $$0);
   }

   private static Stream<GameTestInfo> rotateAndMultiply(Holder.Reference<GameTestInstance> $$0, ServerLevel $$1) {
      Builder<GameTestInfo> $$2 = Stream.builder();

      for(Rotation $$3 : Rotation.values()) {
         for(int $$4 = 0; $$4 < 100; ++$$4) {
            $$2.add(new GameTestInfo($$0, $$3, $$1, RetryOptions.noRetries()));
         }
      }

      return $$2.build();
   }

   public static Stream<Holder.Reference<GameTestInstance>> getTestsForSelection(RegistryAccess $$0, String $$1) {
      return ResourceSelectorArgument.parse(new StringReader($$1), $$0.lookupOrThrow(Registries.TEST_INSTANCE)).stream();
   }

   @Override
   public void tickServer(BooleanSupplier $$0) {
      super.tickServer($$0);
      ServerLevel $$1 = this.overworld();
      if (!this.haveTestsStarted()) {
         this.startTests($$1);
      }

      if ($$1.getGameTime() % 20L == 0L) {
         LOGGER.info(this.testTracker.getProgressBar());
      }

      if (this.testTracker.isDone()) {
         this.halt(false);
         LOGGER.info(this.testTracker.getProgressBar());
         GlobalTestReporter.finish();
         LOGGER.info("========= {} GAME TESTS COMPLETE IN {} ======================", this.testTracker.getTotalCount(), this.stopwatch.stop());
         if (this.testTracker.hasFailedRequired()) {
            LOGGER.info("{} required tests failed :(", this.testTracker.getFailedRequiredCount());
            this.testTracker.getFailedRequired().forEach(GameTestServer::logFailedTest);
         } else {
            LOGGER.info("All {} required tests passed :)", this.testTracker.getTotalCount());
         }

         if (this.testTracker.hasFailedOptional()) {
            LOGGER.info("{} optional tests failed", this.testTracker.getFailedOptionalCount());
            this.testTracker.getFailedOptional().forEach(GameTestServer::logFailedTest);
         }

         LOGGER.info("====================================================");
      }
   }

   private static void logFailedTest(GameTestInfo $$0) {
      if ($$0.getRotation() != Rotation.NONE) {
         LOGGER.info("   - {} with rotation {}: {}", $$0.id(), $$0.getRotation().getSerializedName(), $$0.getError().getDescription().getString());
      } else {
         LOGGER.info("   - {}: {}", $$0.id(), $$0.getError().getDescription().getString());
      }
   }

   @Override
   public SampleLogger getTickTimeLogger() {
      return this.sampleLogger;
   }

   @Override
   public boolean isTickTimeLoggingEnabled() {
      return false;
   }

   @Override
   public void waitUntilNextTick() {
      this.runAllTasks();
   }

   @Override
   public SystemReport fillServerSystemReport(SystemReport $$0) {
      $$0.setDetail("Type", "Game test server");
      return $$0;
   }

   @Override
   public void onServerExit() {
      super.onServerExit();
      LOGGER.info("Game test server shutting down");
      System.exit(this.testTracker != null ? this.testTracker.getFailedRequiredCount() : -1);
   }

   @Override
   public void onServerCrash(CrashReport $$0) {
      super.onServerCrash($$0);
      LOGGER.error("Game test server crashed\n{}", $$0.getFriendlyReport(ReportType.CRASH));
      System.exit(1);
   }

   private void startTests(ServerLevel $$0) {
      BlockPos $$1 = new BlockPos($$0.random.nextIntBetweenInclusive(-14999992, 14999992), -59, $$0.random.nextIntBetweenInclusive(-14999992, 14999992));
      $$0.setRespawnData(LevelData.RespawnData.of($$0.dimension(), $$1, 0.0F, 0.0F));
      GameTestRunner $$2 = GameTestRunner.Builder.fromBatches(this.testBatches, $$0).newStructureSpawner(new StructureGridSpawner($$1, 8, false)).build();
      Collection<GameTestInfo> $$3 = $$2.getTestInfos();
      this.testTracker = new MultipleTestTracker($$3);
      LOGGER.info("{} tests are now running at position {}!", this.testTracker.getTotalCount(), $$1.toShortString());
      this.stopwatch.reset();
      this.stopwatch.start();
      $$2.start();
   }

   private boolean haveTestsStarted() {
      return this.testTracker != null;
   }

   @Override
   public boolean isHardcore() {
      return false;
   }

   @Override
   public int operatorUserPermissionLevel() {
      return 0;
   }

   @Override
   public int getFunctionCompilationLevel() {
      return 4;
   }

   @Override
   public boolean shouldRconBroadcast() {
      return false;
   }

   @Override
   public boolean isDedicatedServer() {
      return false;
   }

   @Override
   public int getRateLimitPacketsPerSecond() {
      return 0;
   }

   @Override
   public boolean isEpollEnabled() {
      return false;
   }

   @Override
   public boolean isCommandBlockEnabled() {
      return true;
   }

   @Override
   public boolean isSpawnerBlockEnabled() {
      return true;
   }

   @Override
   public boolean isPublished() {
      return false;
   }

   @Override
   public boolean shouldInformAdmins() {
      return false;
   }

   @Override
   public boolean isSingleplayerOwner(NameAndId $$0) {
      return false;
   }

   @Override
   public int getMaxPlayers() {
      return 1;
   }

   static class MockProfileResolver implements ProfileResolver {
      @Override
      public Optional<GameProfile> fetchByName(String $$0) {
         return Optional.empty();
      }

      @Override
      public Optional<GameProfile> fetchById(UUID $$0) {
         return Optional.empty();
      }
   }

   static class MockUserNameToIdResolver implements UserNameToIdResolver {
      private final Set<NameAndId> savedIds = new HashSet();

      @Override
      public void add(NameAndId $$0) {
         this.savedIds.add($$0);
      }

      @Override
      public Optional<NameAndId> get(String $$0) {
         return this.savedIds.stream().filter($$1 -> $$1.name().equals($$0)).findFirst().or(() -> Optional.of(NameAndId.createOffline($$0)));
      }

      @Override
      public Optional<NameAndId> get(UUID $$0) {
         return this.savedIds.stream().filter($$1 -> $$1.id().equals($$0)).findFirst();
      }

      @Override
      public void resolveOfflineUsers(boolean $$0) {
      }

      @Override
      public void save() {
      }
   }
}
