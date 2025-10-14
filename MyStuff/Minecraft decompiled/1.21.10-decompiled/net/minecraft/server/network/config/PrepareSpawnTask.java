package net.minecraft.server.network.config;

import com.mojang.logging.LogUtils;
import java.lang.runtime.SwitchBootstraps;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkLoadCounter;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class PrepareSpawnTask implements ConfigurationTask {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type("prepare_spawn");
   public static final int PREPARE_CHUNK_RADIUS = 3;
   final MinecraftServer server;
   final NameAndId nameAndId;
   final LevelLoadListener loadListener;
   @Nullable
   private PrepareSpawnTask.State state;

   public PrepareSpawnTask(MinecraftServer $$0, NameAndId $$1) {
      this.server = $$0;
      this.nameAndId = $$1;
      this.loadListener = $$0.getLevelLoadListener();
   }

   @Override
   public void start(Consumer<Packet<?>> $$0) {
      try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector(LOGGER)) {
         Optional<ValueInput> $$2 = this.server
            .getPlayerList()
            .loadPlayerData(this.nameAndId)
            .map($$1x -> TagValueInput.create($$1, this.server.registryAccess(), $$1x));
         ServerPlayer.SavedPosition $$3 = (ServerPlayer.SavedPosition)$$2.flatMap($$0x -> $$0x.read(ServerPlayer.SavedPosition.MAP_CODEC))
            .orElse(ServerPlayer.SavedPosition.EMPTY);
         LevelData.RespawnData $$4 = this.server.getWorldData().overworldData().getRespawnData();
         ServerLevel $$5 = (ServerLevel)$$3.dimension().map(this.server::getLevel).orElseGet(() -> {
            ServerLevel $$1xx = this.server.getLevel($$4.dimension());
            return $$1xx != null ? $$1xx : this.server.overworld();
         });
         CompletableFuture<Vec3> $$6 = (CompletableFuture)$$3.position()
            .map(CompletableFuture::completedFuture)
            .orElseGet(() -> PlayerSpawnFinder.findSpawn($$5, $$4.pos()));
         Vec2 $$7 = (Vec2)$$3.rotation().orElse(new Vec2($$4.yaw(), $$4.pitch()));
         this.state = new PrepareSpawnTask.Preparing($$5, $$6, $$7);
      }
   }

   @Override
   public boolean tick() {
      PrepareSpawnTask.State var1 = this.state;
      byte var2 = 0;
      boolean var10000;
      switch(SwitchBootstraps.typeSwitch<"typeSwitch",PrepareSpawnTask.Preparing,PrepareSpawnTask.Ready>(var1, var2)) {
         case -1:
            var10000 = false;
            break;
         case 0:
            PrepareSpawnTask.Preparing $$0 = (PrepareSpawnTask.Preparing)var1;
            PrepareSpawnTask.Ready $$1 = $$0.tick();
            if ($$1 != null) {
               this.state = $$1;
               var10000 = true;
            } else {
               var10000 = false;
            }
            break;
         case 1:
            PrepareSpawnTask.Ready $$2 = (PrepareSpawnTask.Ready)var1;
            var10000 = true;
            break;
         default:
            throw new MatchException(null, null);
      }

      return var10000;
   }

   public ServerPlayer spawnPlayer(Connection $$0, CommonListenerCookie $$1) {
      PrepareSpawnTask.State var4 = this.state;
      if (var4 instanceof PrepareSpawnTask.Ready $$2) {
         return $$2.spawn($$0, $$1);
      } else {
         throw new IllegalStateException("Player spawn was not ready");
      }
   }

   public void keepAlive() {
      PrepareSpawnTask.State var2 = this.state;
      if (var2 instanceof PrepareSpawnTask.Ready $$0) {
         $$0.keepAlive();
      }
   }

   public void close() {
      PrepareSpawnTask.State var2 = this.state;
      if (var2 instanceof PrepareSpawnTask.Preparing $$0) {
         $$0.cancel();
      }

      this.state = null;
   }

   @Override
   public ConfigurationTask.Type type() {
      return TYPE;
   }

   final class Preparing implements PrepareSpawnTask.State {
      private final ServerLevel spawnLevel;
      private final CompletableFuture<Vec3> spawnPosition;
      private final Vec2 spawnAngle;
      @Nullable
      private CompletableFuture<?> chunkLoadFuture;
      private final ChunkLoadCounter chunkLoadCounter = new ChunkLoadCounter();

      Preparing(final ServerLevel param2, final CompletableFuture<Vec3> param3, final Vec2 param4) {
         this.spawnLevel = $$0;
         this.spawnPosition = $$1;
         this.spawnAngle = $$2;
      }

      public void cancel() {
         this.spawnPosition.cancel(false);
      }

      @Nullable
      public PrepareSpawnTask.Ready tick() {
         if (!this.spawnPosition.isDone()) {
            return null;
         } else {
            Vec3 $$0 = (Vec3)this.spawnPosition.join();
            if (this.chunkLoadFuture == null) {
               ChunkPos $$1 = new ChunkPos(BlockPos.containing($$0));
               this.chunkLoadCounter
                  .track(
                     this.spawnLevel,
                     () -> this.chunkLoadFuture = this.spawnLevel.getChunkSource().addTicketAndLoadWithRadius(TicketType.PLAYER_SPAWN, $$1, 3)
                  );
               PrepareSpawnTask.this.loadListener.start(LevelLoadListener.Stage.LOAD_PLAYER_CHUNKS, this.chunkLoadCounter.totalChunks());
               PrepareSpawnTask.this.loadListener.updateFocus(this.spawnLevel.dimension(), $$1);
            }

            PrepareSpawnTask.this.loadListener
               .update(LevelLoadListener.Stage.LOAD_PLAYER_CHUNKS, this.chunkLoadCounter.readyChunks(), this.chunkLoadCounter.totalChunks());
            if (!this.chunkLoadFuture.isDone()) {
               return null;
            } else {
               PrepareSpawnTask.this.loadListener.finish(LevelLoadListener.Stage.LOAD_PLAYER_CHUNKS);
               return PrepareSpawnTask.this.new Ready(this.spawnLevel, $$0, this.spawnAngle);
            }
         }
      }
   }

   final class Ready implements PrepareSpawnTask.State {
      private final ServerLevel spawnLevel;
      private final Vec3 spawnPosition;
      private final Vec2 spawnAngle;

      Ready(final ServerLevel param2, final Vec3 param3, final Vec2 param4) {
         this.spawnLevel = $$0;
         this.spawnPosition = $$1;
         this.spawnAngle = $$2;
      }

      public void keepAlive() {
         this.spawnLevel.getChunkSource().addTicketWithRadius(TicketType.PLAYER_SPAWN, new ChunkPos(BlockPos.containing(this.spawnPosition)), 3);
      }

      public ServerPlayer spawn(Connection $$0, CommonListenerCookie $$1) {
         ChunkPos $$2 = new ChunkPos(BlockPos.containing(this.spawnPosition));
         this.spawnLevel.waitForEntities($$2, 3);
         ServerPlayer $$3 = new ServerPlayer(PrepareSpawnTask.this.server, this.spawnLevel, $$1.gameProfile(), $$1.clientInformation());

         ServerPlayer var7;
         try (ProblemReporter.ScopedCollector $$4 = new ProblemReporter.ScopedCollector($$3.problemPath(), PrepareSpawnTask.LOGGER)) {
            Optional<ValueInput> $$5 = PrepareSpawnTask.this.server
               .getPlayerList()
               .loadPlayerData(PrepareSpawnTask.this.nameAndId)
               .map($$1x -> TagValueInput.create($$4, PrepareSpawnTask.this.server.registryAccess(), $$1x));
            $$5.ifPresent($$3::load);
            $$3.snapTo(this.spawnPosition, this.spawnAngle.x, this.spawnAngle.y);
            PrepareSpawnTask.this.server.getPlayerList().placeNewPlayer($$0, $$3, $$1);
            $$5.ifPresent($$1x -> {
               $$3.loadAndSpawnEnderPearls($$1x);
               $$3.loadAndSpawnParentVehicle($$1x);
            });
            var7 = $$3;
         }

         return var7;
      }
   }

   sealed interface State permits PrepareSpawnTask.Preparing, PrepareSpawnTask.Ready {
   }
}
