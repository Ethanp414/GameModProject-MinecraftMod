package net.minecraft.server;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.SuppressForbidden;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class Main {
   private static final Logger LOGGER = LogUtils.getLogger();

   @SuppressForbidden(
      a = "System.out needed before bootstrap"
   )
   @DontObfuscate
   public static void main(String[] $$0) {
      SharedConstants.tryDetectVersion();
      OptionParser $$1 = new OptionParser();
      OptionSpec<Void> $$2 = $$1.accepts("nogui");
      OptionSpec<Void> $$3 = $$1.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
      OptionSpec<Void> $$4 = $$1.accepts("demo");
      OptionSpec<Void> $$5 = $$1.accepts("bonusChest");
      OptionSpec<Void> $$6 = $$1.accepts("forceUpgrade");
      OptionSpec<Void> $$7 = $$1.accepts("eraseCache");
      OptionSpec<Void> $$8 = $$1.accepts("recreateRegionFiles");
      OptionSpec<Void> $$9 = $$1.accepts("safeMode", "Loads level with vanilla datapack only");
      OptionSpec<Void> $$10 = $$1.accepts("help").forHelp();
      OptionSpec<String> $$11 = $$1.accepts("universe").withRequiredArg().defaultsTo(".");
      OptionSpec<String> $$12 = $$1.accepts("world").withRequiredArg();
      OptionSpec<Integer> $$13 = $$1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1);
      OptionSpec<String> $$14 = $$1.accepts("serverId").withRequiredArg();
      OptionSpec<Void> $$15 = $$1.accepts("jfrProfile");
      OptionSpec<Path> $$16 = $$1.accepts("pidFile").withRequiredArg().withValuesConvertedBy(new PathConverter());
      OptionSpec<String> $$17 = $$1.nonOptions();

      try {
         OptionSet $$18 = $$1.parse($$0);
         if ($$18.has($$10)) {
            $$1.printHelpOn(System.err);
            return;
         }

         Path $$19 = $$18.valueOf($$16);
         if ($$19 != null) {
            writePidFile($$19);
         }

         CrashReport.preload();
         if ($$18.has($$15)) {
            JvmProfiler.INSTANCE.start(Environment.SERVER);
         }

         Bootstrap.bootStrap();
         Bootstrap.validate();
         Util.startTimerHackThread();
         Path $$20 = Paths.get("server.properties");
         DedicatedServerSettings $$21 = new DedicatedServerSettings($$20);
         $$21.forceSave();
         RegionFileVersion.configure($$21.getProperties().regionFileComression);
         Path $$22 = Paths.get("eula.txt");
         Eula $$23 = new Eula($$22);
         if ($$18.has($$3)) {
            LOGGER.info("Initialized '{}' and '{}'", $$20.toAbsolutePath(), $$22.toAbsolutePath());
            return;
         }

         if (!$$23.hasAgreedToEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         File $$24 = new File($$18.valueOf($$11));
         Services $$25 = Services.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), $$24);
         String $$26 = (String)Optional.ofNullable((String)$$18.valueOf($$12)).orElse($$21.getProperties().levelName);
         LevelStorageSource $$27 = LevelStorageSource.createDefault($$24.toPath());
         LevelStorageSource.LevelStorageAccess $$28 = $$27.validateAndCreateAccess($$26);
         Dynamic<?> $$29;
         if ($$28.hasWorldData()) {
            LevelSummary $$30;
            try {
               $$29 = $$28.getDataTag();
               $$30 = $$28.getSummary($$29);
            } catch (NbtException | ReportedNbtException | IOException var41) {
               LevelStorageSource.LevelDirectory $$32 = $$28.getLevelDirectory();
               LOGGER.warn("Failed to load world data from {}", $$32.dataFile(), var41);
               LOGGER.info("Attempting to use fallback");

               try {
                  $$29 = $$28.getDataTagFallback();
                  $$30 = $$28.getSummary($$29);
               } catch (NbtException | ReportedNbtException | IOException var40) {
                  LOGGER.error("Failed to load world data from {}", $$32.oldDataFile(), var40);
                  LOGGER.error("Failed to load world data from {} and {}. World files may be corrupted. Shutting down.", $$32.dataFile(), $$32.oldDataFile());
                  return;
               }

               $$28.restoreLevelDataFromOld();
            }

            if ($$30.requiresManualConversion()) {
               LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
               return;
            }

            if (!$$30.isCompatible()) {
               LOGGER.info("This world was created by an incompatible version.");
               return;
            }
         } else {
            $$29 = null;
         }

         Dynamic<?> $$39 = $$29;
         boolean $$40 = $$18.has($$9);
         if ($$40) {
            LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
         }

         PackRepository $$41 = ServerPacksSource.createPackRepository($$28);

         WorldStem $$43;
         try {
            WorldLoader.InitConfig $$42 = loadOrCreateConfig($$21.getProperties(), $$39, $$40, $$41);
            $$43 = (WorldStem)Util.blockUntilDone(
                  $$6x -> WorldLoader.load(
                        $$42,
                        $$5xx -> {
                           Registry<LevelStem> $$6xxx = $$5xx.datapackDimensions().lookupOrThrow(Registries.LEVEL_STEM);
                           if ($$39 != null) {
                              LevelDataAndDimensions $$7xx = LevelStorageSource.getLevelDataAndDimensions(
                                 $$39, $$5xx.dataConfiguration(), $$6xxx, $$5xx.datapackWorldgen()
                              );
                              return new WorldLoader.DataLoadOutput<>($$7xx.worldData(), $$7xx.dimensions().dimensionsRegistryAccess());
                           } else {
                              LOGGER.info("No existing world data, creating new world");
                              return createNewWorldData($$21, $$5xx, $$6xxx, $$18.has($$4), $$18.has($$5));
                           }
                        },
                        WorldStem::new,
                        Util.backgroundExecutor(),
                        $$6x
                     )
               )
               .get();
         } catch (Exception var39) {
            LOGGER.warn(
               "Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", var39
            );
            return;
         }

         RegistryAccess.Frozen $$46 = $$43.registries().compositeAccess();
         WorldData $$47 = $$43.worldData();
         boolean $$48 = $$18.has($$8);
         if ($$18.has($$6) || $$48) {
            forceUpgrade($$28, $$47, DataFixers.getDataFixer(), $$18.has($$7), () -> true, $$46, $$48);
         }

         $$28.saveDataTag($$46, $$47);
         final DedicatedServer $$49 = MinecraftServer.spin($$11x -> {
            DedicatedServer $$12xx = new DedicatedServer($$11x, $$28, $$41, $$43, $$21, DataFixers.getDataFixer(), $$25);
            $$12xx.setPort($$18.valueOf($$13));
            $$12xx.setDemo($$18.has($$4));
            $$12xx.setId($$18.valueOf($$14));
            boolean $$13xx = !$$18.has($$2) && !$$18.valuesOf($$17).contains("nogui");
            if ($$13xx && !GraphicsEnvironment.isHeadless()) {
               $$12xx.showGui();
            }

            return $$12xx;
         });
         Thread $$50 = new Thread("Server Shutdown Thread") {
            public void run() {
               $$49.halt(true);
            }
         };
         $$50.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook($$50);
      } catch (Exception var42) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", var42);
      }
   }

   private static WorldLoader.DataLoadOutput<WorldData> createNewWorldData(
      DedicatedServerSettings $$0, WorldLoader.DataLoadContext $$1, Registry<LevelStem> $$2, boolean $$3, boolean $$4
   ) {
      LevelSettings $$5;
      WorldOptions $$6;
      WorldDimensions $$7;
      if ($$3) {
         $$5 = MinecraftServer.DEMO_SETTINGS;
         $$6 = WorldOptions.DEMO_OPTIONS;
         $$7 = WorldPresets.createNormalWorldDimensions($$1.datapackWorldgen());
      } else {
         DedicatedServerProperties $$8 = $$0.getProperties();
         $$5 = new LevelSettings(
            $$8.levelName,
            $$8.gameMode.get(),
            $$8.hardcore,
            $$8.difficulty.get(),
            false,
            new GameRules($$1.dataConfiguration().enabledFeatures()),
            $$1.dataConfiguration()
         );
         $$6 = $$4 ? $$8.worldOptions.withBonusChest(true) : $$8.worldOptions;
         $$7 = $$8.createDimensions($$1.datapackWorldgen());
      }

      WorldDimensions.Complete $$12 = $$7.bake($$2);
      Lifecycle $$13 = $$12.lifecycle().add($$1.datapackWorldgen().allRegistriesLifecycle());
      return new WorldLoader.DataLoadOutput<>(new PrimaryLevelData($$5, $$6, $$12.specialWorldProperty(), $$13), $$12.dimensionsRegistryAccess());
   }

   private static void writePidFile(Path $$0) {
      try {
         long $$1 = ProcessHandle.current().pid();
         Files.writeString($$0, Long.toString($$1));
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   private static WorldLoader.InitConfig loadOrCreateConfig(DedicatedServerProperties $$0, @Nullable Dynamic<?> $$1, boolean $$2, PackRepository $$3) {
      boolean $$5;
      WorldDataConfiguration $$6;
      if ($$1 != null) {
         WorldDataConfiguration $$4 = LevelStorageSource.readDataConfig($$1);
         $$5 = false;
         $$6 = $$4;
      } else {
         $$5 = true;
         $$6 = new WorldDataConfiguration($$0.initialDataPackConfiguration, FeatureFlags.DEFAULT_FLAGS);
      }

      WorldLoader.PackConfig $$9 = new WorldLoader.PackConfig($$3, $$6, $$2, $$5);
      return new WorldLoader.InitConfig($$9, Commands.CommandSelection.DEDICATED, $$0.functionPermissionLevel);
   }

   private static void forceUpgrade(
      LevelStorageSource.LevelStorageAccess $$0, WorldData $$1, DataFixer $$2, boolean $$3, BooleanSupplier $$4, RegistryAccess $$5, boolean $$6
   ) {
      LOGGER.info("Forcing world upgrade!");

      try (WorldUpgrader $$7 = new WorldUpgrader($$0, $$2, $$1, $$5, $$3, $$6)) {
         Component $$8 = null;

         while(!$$7.isFinished()) {
            Component $$9 = $$7.getStatus();
            if ($$8 != $$9) {
               $$8 = $$9;
               LOGGER.info($$7.getStatus().getString());
            }

            int $$10 = $$7.getTotalChunks();
            if ($$10 > 0) {
               int $$11 = $$7.getConverted() + $$7.getSkipped();
               LOGGER.info("{}% completed ({} / {} chunks)...", Mth.floor((float)$$11 / (float)$$10 * 100.0F), $$11, $$10);
            }

            if (!$$4.getAsBoolean()) {
               $$7.cancel();
            } else {
               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var13) {
               }
            }
         }
      }
   }
}
