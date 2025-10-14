package net.minecraft.server.dedicated;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.net.HostAndPort;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import io.netty.handler.ssl.SslContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.jsonrpc.JsonRpcNotificationService;
import net.minecraft.server.jsonrpc.ManagementServer;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.security.AuthenticationHandler;
import net.minecraft.server.jsonrpc.security.JsonRpcSslContextProvider;
import net.minecraft.server.jsonrpc.security.SecurityConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.LoggingLevelLoadListener;
import net.minecraft.server.network.ServerTextFilter;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.RemoteSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.monitoring.jmx.MinecraftServerStatistics;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class DedicatedServer extends MinecraftServer implements ServerInterface {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int CONVERSION_RETRY_DELAY_MS = 5000;
   private static final int CONVERSION_RETRIES = 2;
   private final List<ConsoleInput> consoleInput = Collections.synchronizedList(Lists.newArrayList());
   @Nullable
   private QueryThreadGs4 queryThreadGs4;
   private final RconConsoleSource rconConsoleSource;
   @Nullable
   private RconThread rconThread;
   private final DedicatedServerSettings settings;
   @Nullable
   private MinecraftServerGui gui;
   @Nullable
   private final ServerTextFilter serverTextFilter;
   @Nullable
   private RemoteSampleLogger tickTimeLogger;
   private boolean isTickTimeLoggingEnabled;
   private final ServerLinks serverLinks;
   private final Map<String, String> codeOfConductTexts;
   @Nullable
   private ManagementServer jsonRpcServer;
   private long lastHeartbeat;

   public DedicatedServer(
      Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, DedicatedServerSettings $$4, DataFixer $$5, Services $$6
   ) {
      super($$0, $$1, $$2, $$3, Proxy.NO_PROXY, $$5, $$6, LoggingLevelLoadListener.forDedicatedServer());
      this.settings = $$4;
      this.rconConsoleSource = new RconConsoleSource(this);
      this.serverTextFilter = ServerTextFilter.createFromConfig($$4.getProperties());
      this.serverLinks = createServerLinks($$4);
      if ($$4.getProperties().codeOfConduct) {
         this.codeOfConductTexts = readCodeOfConducts();
      } else {
         this.codeOfConductTexts = Map.of();
      }
   }

   private static Map<String, String> readCodeOfConducts() {
      Path $$0 = Path.of("codeofconduct");
      if (!Files.isDirectory($$0, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
         throw new IllegalArgumentException("Code of Conduct folder does not exist: " + $$0);
      } else {
         try {
            Builder<String, String> $$1 = ImmutableMap.builder();
            Stream<Path> $$2 = Files.list($$0);

            try {
               for(Path $$3 : $$2.toList()) {
                  String $$4 = $$3.getFileName().toString();
                  if ($$4.endsWith(".txt")) {
                     String $$5 = $$4.substring(0, $$4.length() - 4).toLowerCase(Locale.ROOT);
                     if (!$$3.toRealPath().getParent().equals($$0.toAbsolutePath())) {
                        throw new IllegalArgumentException(
                           "Failed to read Code of Conduct file \"" + $$4 + "\" because it links to a file outside the allowed directory"
                        );
                     }

                     try {
                        String $$6 = String.join("\n", Files.readAllLines($$3, StandardCharsets.UTF_8));
                        $$1.put($$5, StringUtil.stripColor($$6));
                     } catch (IOException var9) {
                        throw new IllegalArgumentException("Failed to read Code of Conduct file " + $$4, var9);
                     }
                  }
               }
            } catch (Throwable var10) {
               if ($$2 != null) {
                  try {
                     $$2.close();
                  } catch (Throwable var8) {
                     var10.addSuppressed(var8);
                  }
               }

               throw var10;
            }

            if ($$2 != null) {
               $$2.close();
            }

            return $$1.build();
         } catch (IOException var11) {
            throw new IllegalArgumentException("Failed to read Code of Conduct folder", var11);
         }
      }
   }

   private SslContext createSslContext() {
      try {
         return JsonRpcSslContextProvider.createFrom(
            this.getProperties().managementServerTlsKeystore, this.getProperties().managementServerTlsKeystorePassword
         );
      } catch (Exception var2) {
         JsonRpcSslContextProvider.printInstructions();
         throw new IllegalStateException("Failed to configure TLS for the server management protocol", var2);
      }
   }

   @Override
   public boolean initServer() throws IOException {
      int $$0 = this.getProperties().managementServerPort;
      if (this.getProperties().managementServerEnabled) {
         String $$1 = this.settings.getProperties().managementServerSecret;
         if (!SecurityConfig.isValid($$1)) {
            throw new IllegalStateException("Invalid management server secret, must be 40 alphanumeric characters");
         }

         String $$2 = this.getProperties().managementServerHost;
         HostAndPort $$3 = HostAndPort.fromParts($$2, $$0);
         SecurityConfig $$4 = new SecurityConfig($$1);
         AuthenticationHandler $$5 = new AuthenticationHandler($$4);
         LOGGER.info("Starting json RPC server on {}", $$3);
         this.jsonRpcServer = new ManagementServer($$3, $$5);
         MinecraftApi $$6 = MinecraftApi.of(this);
         $$6.notificationManager().registerService(new JsonRpcNotificationService($$6, this.jsonRpcServer));
         if (this.getProperties().managementServerTlsEnabled) {
            SslContext $$7 = this.createSslContext();
            this.jsonRpcServer.startWithTls($$6, $$7);
         } else {
            this.jsonRpcServer.startWithoutTls($$6);
         }
      }

      Thread $$8 = new Thread("Server console handler") {
         public void run() {
            BufferedReader $$0 = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            String $$1;
            try {
               while(!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && ($$1 = $$0.readLine()) != null) {
                  DedicatedServer.this.handleConsoleInput($$1, DedicatedServer.this.createCommandSourceStack());
               }
            } catch (IOException var4) {
               DedicatedServer.LOGGER.error("Exception handling console input", var4);
            }
         }
      };
      $$8.setDaemon(true);
      $$8.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      $$8.start();
      LOGGER.info("Starting minecraft server version {}", SharedConstants.getCurrentVersion().name());
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      LOGGER.info("Loading properties");
      DedicatedServerProperties $$9 = this.settings.getProperties();
      if (this.isSingleplayer()) {
         this.setLocalIp("127.0.0.1");
      } else {
         this.setUsesAuthentication($$9.onlineMode);
         this.setPreventProxyConnections($$9.preventProxyConnections);
         this.setLocalIp($$9.serverIp);
      }

      this.worldData.setGameType($$9.gameMode.get());
      LOGGER.info("Default game type: {}", $$9.gameMode.get());
      InetAddress $$10 = null;
      if (!this.getLocalIp().isEmpty()) {
         $$10 = InetAddress.getByName(this.getLocalIp());
      }

      if (this.getPort() < 0) {
         this.setPort($$9.serverPort);
      }

      this.initializeKeyPair();
      LOGGER.info("Starting Minecraft server on {}:{}", this.getLocalIp().isEmpty() ? "*" : this.getLocalIp(), this.getPort());

      try {
         this.getConnection().startTcpServerListener($$10, this.getPort());
      } catch (IOException var11) {
         LOGGER.warn("**** FAILED TO BIND TO PORT!");
         LOGGER.warn("The exception was: {}", var11.toString());
         LOGGER.warn("Perhaps a server is already running on that port?");
         return false;
      }

      if (!this.usesAuthentication()) {
         LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
         LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
         LOGGER.warn(
            "While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
         );
         LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
      }

      if (this.convertOldUsers()) {
         this.services.nameToIdCache().save();
      }

      if (!OldUsersConverter.serverReadyAfterUserconversion(this)) {
         return false;
      } else {
         this.setPlayerList(new DedicatedPlayerList(this, this.registries(), this.playerDataStorage));
         this.tickTimeLogger = new RemoteSampleLogger(TpsDebugDimensions.values().length, this.debugSubscribers(), RemoteDebugSampleType.TICK_TIME);
         long $$12 = Util.getNanos();
         this.services.nameToIdCache().resolveOfflineUsers(!this.usesAuthentication());
         LOGGER.info("Preparing level \"{}\"", this.getLevelIdName());
         this.loadLevel();
         long $$13 = Util.getNanos() - $$12;
         String $$14 = String.format(Locale.ROOT, "%.3fs", (double)$$13 / 1.0E9);
         LOGGER.info("Done ({})! For help, type \"help\"", $$14);
         if ($$9.announcePlayerAchievements != null) {
            this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set($$9.announcePlayerAchievements, this);
         }

         if ($$9.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryThreadGs4 = QueryThreadGs4.create(this);
         }

         if ($$9.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconThread = RconThread.create(this);
         }

         if (this.getMaxTickLength() > 0L) {
            Thread $$15 = new Thread(new ServerWatchdog(this));
            $$15.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
            $$15.setName("Server Watchdog");
            $$15.setDaemon(true);
            $$15.start();
         }

         if ($$9.enableJmxMonitoring) {
            MinecraftServerStatistics.registerJmxMonitoring(this);
            LOGGER.info("JMX monitoring enabled");
         }

         this.notificationManager().serverStarted();
         return true;
      }
   }

   @Override
   public boolean isEnforceWhitelist() {
      return this.settings.getProperties().enforceWhitelist.get();
   }

   @Override
   public void setEnforceWhitelist(boolean $$0) {
      this.settings.update($$1 -> $$1.enforceWhitelist.update(this.registryAccess(), $$0));
   }

   @Override
   public boolean isUsingWhitelist() {
      return this.settings.getProperties().whiteList.get();
   }

   @Override
   public void setUsingWhitelist(boolean $$0) {
      this.settings.update($$1 -> $$1.whiteList.update(this.registryAccess(), $$0));
   }

   @Override
   public void tickServer(BooleanSupplier $$0) {
      super.tickServer($$0);
      if (this.jsonRpcServer != null) {
         this.jsonRpcServer.tick();
      }

      long $$1 = Util.getMillis();
      int $$2 = this.statusHeartbeatInterval();
      if ($$2 > 0) {
         long $$3 = (long)$$2 * TimeUtil.MILLISECONDS_PER_SECOND;
         if ($$1 - this.lastHeartbeat >= $$3) {
            this.lastHeartbeat = $$1;
            this.notificationManager().statusHeartbeat();
         }
      }
   }

   @Override
   public boolean saveAllChunks(boolean $$0, boolean $$1, boolean $$2) {
      this.notificationManager().serverSaveStarted();
      boolean $$3 = super.saveAllChunks($$0, $$1, $$2);
      this.notificationManager().serverSaveCompleted();
      return $$3;
   }

   @Override
   public boolean allowFlight() {
      return this.settings.getProperties().allowFlight.get();
   }

   public void setAllowFlight(boolean $$0) {
      this.settings.update($$1 -> $$1.allowFlight.update(this.registryAccess(), $$0));
   }

   @Override
   public DedicatedServerProperties getProperties() {
      return this.settings.getProperties();
   }

   public void setDifficulty(Difficulty $$0) {
      this.settings.update($$1 -> $$1.difficulty.update(this.registryAccess(), $$0));
      this.forceDifficulty();
   }

   @Override
   public void forceDifficulty() {
      this.setDifficulty(this.getProperties().difficulty.get(), true);
   }

   public int viewDistance() {
      return this.settings.getProperties().viewDistance.get();
   }

   public void setViewDistance(int $$0) {
      this.settings.update($$1 -> $$1.viewDistance.update(this.registryAccess(), $$0));
      this.getPlayerList().setViewDistance($$0);
   }

   public int simulationDistance() {
      return this.settings.getProperties().simulationDistance.get();
   }

   public void setSimulationDistance(int $$0) {
      this.settings.update($$1 -> $$1.simulationDistance.update(this.registryAccess(), $$0));
      this.getPlayerList().setSimulationDistance($$0);
   }

   @Override
   public SystemReport fillServerSystemReport(SystemReport $$0) {
      $$0.setDetail("Is Modded", (Supplier<String>)(() -> this.getModdedStatus().fullDescription()));
      $$0.setDetail("Type", (Supplier<String>)(() -> "Dedicated Server (map_server.txt)"));
      return $$0;
   }

   @Override
   public void dumpServerProperties(Path $$0) throws IOException {
      DedicatedServerProperties $$1 = this.getProperties();
      Writer $$2 = Files.newBufferedWriter($$0);

      try {
         $$2.write(String.format(Locale.ROOT, "sync-chunk-writes=%s%n", $$1.syncChunkWrites));
         $$2.write(String.format(Locale.ROOT, "gamemode=%s%n", $$1.gameMode.get()));
         $$2.write(String.format(Locale.ROOT, "entity-broadcast-range-percentage=%d%n", $$1.entityBroadcastRangePercentage.get()));
         $$2.write(String.format(Locale.ROOT, "max-world-size=%d%n", $$1.maxWorldSize));
         $$2.write(String.format(Locale.ROOT, "view-distance=%d%n", $$1.viewDistance.get()));
         $$2.write(String.format(Locale.ROOT, "simulation-distance=%d%n", $$1.simulationDistance.get()));
         $$2.write(String.format(Locale.ROOT, "generate-structures=%s%n", $$1.worldOptions.generateStructures()));
         $$2.write(String.format(Locale.ROOT, "use-native=%s%n", $$1.useNativeTransport));
         $$2.write(String.format(Locale.ROOT, "rate-limit=%d%n", $$1.rateLimitPacketsPerSecond));
      } catch (Throwable var7) {
         if ($$2 != null) {
            try {
               $$2.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if ($$2 != null) {
         $$2.close();
      }
   }

   @Override
   public void onServerExit() {
      if (this.serverTextFilter != null) {
         this.serverTextFilter.close();
      }

      if (this.gui != null) {
         this.gui.close();
      }

      if (this.rconThread != null) {
         this.rconThread.stop();
      }

      if (this.queryThreadGs4 != null) {
         this.queryThreadGs4.stop();
      }

      if (this.jsonRpcServer != null) {
         try {
            this.jsonRpcServer.stop(true);
         } catch (InterruptedException var2) {
            LOGGER.error("Interrupted while stopping the management server", var2);
         }
      }
   }

   @Override
   public void tickConnection() {
      super.tickConnection();
      this.handleConsoleInputs();
   }

   public void handleConsoleInput(String $$0, CommandSourceStack $$1) {
      this.consoleInput.add(new ConsoleInput($$0, $$1));
   }

   public void handleConsoleInputs() {
      while(!this.consoleInput.isEmpty()) {
         ConsoleInput $$0 = (ConsoleInput)this.consoleInput.remove(0);
         this.getCommands().performPrefixedCommand($$0.source, $$0.msg);
      }
   }

   @Override
   public boolean isDedicatedServer() {
      return true;
   }

   @Override
   public int getRateLimitPacketsPerSecond() {
      return this.getProperties().rateLimitPacketsPerSecond;
   }

   @Override
   public boolean isEpollEnabled() {
      return this.getProperties().useNativeTransport;
   }

   public DedicatedPlayerList getPlayerList() {
      return (DedicatedPlayerList)super.getPlayerList();
   }

   @Override
   public int getMaxPlayers() {
      return this.settings.getProperties().maxPlayers.get();
   }

   public void setMaxPlayers(int $$0) {
      this.settings.update($$1 -> $$1.maxPlayers.update(this.registryAccess(), $$0));
   }

   @Override
   public boolean isPublished() {
      return true;
   }

   @Override
   public String getServerIp() {
      return this.getLocalIp();
   }

   @Override
   public int getServerPort() {
      return this.getPort();
   }

   @Override
   public String getServerName() {
      return this.getMotd();
   }

   public void showGui() {
      if (this.gui == null) {
         this.gui = MinecraftServerGui.showFrameFor(this);
      }
   }

   @Override
   public boolean hasGui() {
      return this.gui != null;
   }

   public int spawnProtectionRadius() {
      return this.getProperties().spawnProtection.get();
   }

   public void setSpawnProtectionRadius(int $$0) {
      this.settings.update($$1 -> $$1.spawnProtection.update(this.registryAccess(), $$0));
   }

   @Override
   public boolean isUnderSpawnProtection(ServerLevel $$0, BlockPos $$1, Player $$2) {
      LevelData.RespawnData $$3 = $$0.getRespawnData();
      if ($$0.dimension() != $$3.dimension()) {
         return false;
      } else if (this.getPlayerList().getOps().isEmpty()) {
         return false;
      } else if (this.getPlayerList().isOp($$2.nameAndId())) {
         return false;
      } else if (this.spawnProtectionRadius() <= 0) {
         return false;
      } else {
         BlockPos $$4 = $$3.pos();
         int $$5 = Mth.abs($$1.getX() - $$4.getX());
         int $$6 = Mth.abs($$1.getZ() - $$4.getZ());
         int $$7 = Math.max($$5, $$6);
         return $$7 <= this.spawnProtectionRadius();
      }
   }

   @Override
   public boolean repliesToStatus() {
      return this.getProperties().enableStatus.get();
   }

   public void setRepliesToStatus(boolean $$0) {
      this.settings.update($$1 -> $$1.enableStatus.update(this.registryAccess(), $$0));
   }

   @Override
   public boolean hidesOnlinePlayers() {
      return this.getProperties().hideOnlinePlayers.get();
   }

   public void setHidesOnlinePlayers(boolean $$0) {
      this.settings.update($$1 -> $$1.hideOnlinePlayers.update(this.registryAccess(), $$0));
   }

   @Override
   public int operatorUserPermissionLevel() {
      return this.getProperties().opPermissionLevel.get();
   }

   public void setOperatorUserPermissionLevel(int $$0) {
      this.settings.update($$1 -> $$1.opPermissionLevel.update(this.registryAccess(), $$0));
   }

   @Override
   public int getFunctionCompilationLevel() {
      return this.getProperties().functionPermissionLevel;
   }

   @Override
   public int playerIdleTimeout() {
      return this.settings.getProperties().playerIdleTimeout.get();
   }

   @Override
   public void setPlayerIdleTimeout(int $$0) {
      this.settings.update($$1 -> $$1.playerIdleTimeout.update(this.registryAccess(), $$0));
   }

   public int statusHeartbeatInterval() {
      return this.settings.getProperties().statusHeartbeatInterval.get();
   }

   public void setStatusHeartbeatInterval(int $$0) {
      this.settings.update($$1 -> $$1.statusHeartbeatInterval.update(this.registryAccess(), $$0));
   }

   @Override
   public String getMotd() {
      return (String)this.settings.getProperties().motd.get();
   }

   @Override
   public void setMotd(String $$0) {
      this.settings.update($$1 -> $$1.motd.update(this.registryAccess(), $$0));
   }

   @Override
   public boolean shouldRconBroadcast() {
      return this.getProperties().broadcastRconToOps;
   }

   @Override
   public boolean shouldInformAdmins() {
      return this.getProperties().broadcastConsoleToOps;
   }

   @Override
   public int getAbsoluteMaxWorldSize() {
      return this.getProperties().maxWorldSize;
   }

   @Override
   public int getCompressionThreshold() {
      return this.getProperties().networkCompressionThreshold;
   }

   @Override
   public boolean enforceSecureProfile() {
      DedicatedServerProperties $$0 = this.getProperties();
      return $$0.enforceSecureProfile && $$0.onlineMode && this.services.canValidateProfileKeys();
   }

   @Override
   public boolean logIPs() {
      return this.getProperties().logIPs;
   }

   protected boolean convertOldUsers() {
      boolean $$0 = false;

      for(int $$1 = 0; !$$0 && $$1 <= 2; ++$$1) {
         if ($$1 > 0) {
            LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         $$0 = OldUsersConverter.convertUserBanlist(this);
      }

      boolean $$2 = false;

      for(int var7 = 0; !$$2 && var7 <= 2; ++var7) {
         if (var7 > 0) {
            LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         $$2 = OldUsersConverter.convertIpBanlist(this);
      }

      boolean $$3 = false;

      for(int var8 = 0; !$$3 && var8 <= 2; ++var8) {
         if (var8 > 0) {
            LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
            this.waitForRetry();
         }

         $$3 = OldUsersConverter.convertOpsList(this);
      }

      boolean $$4 = false;

      for(int var9 = 0; !$$4 && var9 <= 2; ++var9) {
         if (var9 > 0) {
            LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
            this.waitForRetry();
         }

         $$4 = OldUsersConverter.convertWhiteList(this);
      }

      boolean $$5 = false;

      for(int var10 = 0; !$$5 && var10 <= 2; ++var10) {
         if (var10 > 0) {
            LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
            this.waitForRetry();
         }

         $$5 = OldUsersConverter.convertPlayers(this);
      }

      return $$0 || $$2 || $$3 || $$4 || $$5;
   }

   private void waitForRetry() {
      try {
         Thread.sleep(5000L);
      } catch (InterruptedException var2) {
      }
   }

   public long getMaxTickLength() {
      return this.getProperties().maxTickTime;
   }

   @Override
   public int getMaxChainedNeighborUpdates() {
      return this.getProperties().maxChainedNeighborUpdates;
   }

   @Override
   public String getPluginNames() {
      return "";
   }

   @Override
   public String runCommand(String $$0) {
      this.rconConsoleSource.prepareForCommand();
      this.executeBlocking(() -> this.getCommands().performPrefixedCommand(this.rconConsoleSource.createCommandSourceStack(), $$0));
      return this.rconConsoleSource.getCommandResponse();
   }

   @Override
   public void stopServer() {
      this.notificationManager().serverShuttingDown();
      super.stopServer();
      Util.shutdownExecutors();
   }

   @Override
   public boolean isSingleplayerOwner(NameAndId $$0) {
      return false;
   }

   @Override
   public int getScaledTrackingDistance(int $$0) {
      return this.entityBroadcastRangePercentage() * $$0 / 100;
   }

   public int entityBroadcastRangePercentage() {
      return this.getProperties().entityBroadcastRangePercentage.get();
   }

   public void setEntityBroadcastRangePercentage(int $$0) {
      this.settings.update($$1 -> $$1.entityBroadcastRangePercentage.update(this.registryAccess(), $$0));
   }

   @Override
   public String getLevelIdName() {
      return this.storageSource.getLevelId();
   }

   @Override
   public boolean forceSynchronousWrites() {
      return this.settings.getProperties().syncChunkWrites;
   }

   @Override
   public TextFilter createTextFilterForPlayer(ServerPlayer $$0) {
      return this.serverTextFilter != null ? this.serverTextFilter.createContext($$0.getGameProfile()) : TextFilter.DUMMY;
   }

   @Nullable
   @Override
   public GameType getForcedGameType() {
      return this.forceGameMode() ? this.worldData.getGameType() : null;
   }

   public boolean forceGameMode() {
      return this.settings.getProperties().forceGameMode.get();
   }

   public void setForceGameMode(boolean $$0) {
      this.settings.update($$1 -> $$1.forceGameMode.update(this.registryAccess(), $$0));
      this.enforceGameTypeForPlayers(this.getForcedGameType());
   }

   public GameType gameMode() {
      return this.getProperties().gameMode.get();
   }

   public void setGameMode(GameType $$0) {
      this.settings.update($$1 -> $$1.gameMode.update(this.registryAccess(), $$0));
      this.worldData.setGameType(this.gameMode());
      this.enforceGameTypeForPlayers(this.getForcedGameType());
   }

   @Override
   public Optional<MinecraftServer.ServerResourcePackInfo> getServerResourcePack() {
      return this.settings.getProperties().serverResourcePackInfo;
   }

   @Override
   public void endMetricsRecordingTick() {
      super.endMetricsRecordingTick();
      this.isTickTimeLoggingEnabled = this.debugSubscribers().hasAnySubscriberFor(DebugSubscriptions.DEDICATED_SERVER_TICK_TIME);
   }

   @Override
   public SampleLogger getTickTimeLogger() {
      return this.tickTimeLogger;
   }

   @Override
   public boolean isTickTimeLoggingEnabled() {
      return this.isTickTimeLoggingEnabled;
   }

   @Override
   public boolean acceptsTransfers() {
      return this.settings.getProperties().acceptsTransfers.get();
   }

   public void setAcceptsTransfers(boolean $$0) {
      this.settings.update($$1 -> $$1.acceptsTransfers.update(this.registryAccess(), $$0));
   }

   @Override
   public ServerLinks serverLinks() {
      return this.serverLinks;
   }

   @Override
   public int pauseWhenEmptySeconds() {
      return this.settings.getProperties().pauseWhenEmptySeconds.get();
   }

   public void setPauseWhenEmptySeconds(int $$0) {
      this.settings.update($$1 -> $$1.pauseWhenEmptySeconds.update(this.registryAccess(), $$0));
   }

   private static ServerLinks createServerLinks(DedicatedServerSettings $$0) {
      Optional<URI> $$1 = parseBugReportLink($$0.getProperties());
      return (ServerLinks)$$1.map($$0x -> new ServerLinks(List.of(ServerLinks.KnownLinkType.BUG_REPORT.create($$0x)))).orElse(ServerLinks.EMPTY);
   }

   private static Optional<URI> parseBugReportLink(DedicatedServerProperties $$0) {
      String $$1 = $$0.bugReportLink;
      if ($$1.isEmpty()) {
         return Optional.empty();
      } else {
         try {
            return Optional.of(Util.parseAndValidateUntrustedUri($$1));
         } catch (Exception var3) {
            LOGGER.warn("Failed to parse bug link {}", $$1, var3);
            return Optional.empty();
         }
      }
   }

   @Override
   public Map<String, String> getCodeOfConducts() {
      return this.codeOfConductTexts;
   }
}
