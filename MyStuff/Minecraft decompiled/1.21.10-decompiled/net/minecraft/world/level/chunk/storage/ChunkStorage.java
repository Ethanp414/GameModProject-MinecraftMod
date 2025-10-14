package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class ChunkStorage implements AutoCloseable {
   public static final int LAST_MONOLYTH_STRUCTURE_DATA_VERSION = 1493;
   private final IOWorker worker;
   protected final DataFixer fixerUpper;
   @Nullable
   private volatile LegacyStructureDataHandler legacyStructureHandler;

   public ChunkStorage(RegionStorageInfo $$0, Path $$1, DataFixer $$2, boolean $$3) {
      this.fixerUpper = $$2;
      this.worker = new IOWorker($$0, $$1, $$3);
   }

   public boolean isOldChunkAround(ChunkPos $$0, int $$1) {
      return this.worker.isOldChunkAround($$0, $$1);
   }

   public CompoundTag upgradeChunkTag(
      ResourceKey<Level> $$0, Supplier<DimensionDataStorage> $$1, CompoundTag $$2, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> $$3
   ) {
      int $$4 = getVersion($$2);
      if ($$4 == SharedConstants.getCurrentVersion().dataVersion().version()) {
         return $$2;
      } else {
         try {
            if ($$4 < 1493) {
               $$2 = DataFixTypes.CHUNK.update(this.fixerUpper, $$2, $$4, 1493);
               if ($$2.getCompound("Level").flatMap($$0x -> $$0x.getBoolean("hasLegacyStructureData")).orElse(false)) {
                  LegacyStructureDataHandler $$5 = this.getLegacyStructureHandler($$0, $$1);
                  $$2 = $$5.updateFromLegacy($$2);
               }
            }

            injectDatafixingContext($$2, $$0, $$3);
            $$2 = DataFixTypes.CHUNK.updateToCurrentVersion(this.fixerUpper, $$2, Math.max(1493, $$4));
            removeDatafixingContext($$2);
            NbtUtils.addCurrentDataVersion($$2);
            return $$2;
         } catch (Exception var9) {
            CrashReport $$7 = CrashReport.forThrowable(var9, "Updated chunk");
            CrashReportCategory $$8 = $$7.addCategory("Updated chunk details");
            $$8.setDetail("Data version", $$4);
            throw new ReportedException($$7);
         }
      }
   }

   private LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> $$0, Supplier<DimensionDataStorage> $$1) {
      LegacyStructureDataHandler $$2 = this.legacyStructureHandler;
      if ($$2 == null) {
         synchronized(this) {
            $$2 = this.legacyStructureHandler;
            if ($$2 == null) {
               this.legacyStructureHandler = $$2 = LegacyStructureDataHandler.getLegacyStructureHandler($$0, (DimensionDataStorage)$$1.get());
            }
         }
      }

      return $$2;
   }

   public static void injectDatafixingContext(CompoundTag $$0, ResourceKey<Level> $$1, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> $$2) {
      CompoundTag $$3 = new CompoundTag();
      $$3.putString("dimension", $$1.location().toString());
      $$2.ifPresent($$1x -> $$3.putString("generator", $$1x.location().toString()));
      $$0.put("__context", $$3);
   }

   private static void removeDatafixingContext(CompoundTag $$0) {
      $$0.remove("__context");
   }

   public static int getVersion(CompoundTag $$0) {
      return NbtUtils.getDataVersion($$0, -1);
   }

   public CompletableFuture<Optional<CompoundTag>> read(ChunkPos $$0) {
      return this.worker.loadAsync($$0);
   }

   public CompletableFuture<Void> write(ChunkPos $$0, Supplier<CompoundTag> $$1) {
      this.handleLegacyStructureIndex($$0);
      return this.worker.store($$0, $$1);
   }

   protected void handleLegacyStructureIndex(ChunkPos $$0) {
      if (this.legacyStructureHandler != null) {
         this.legacyStructureHandler.removeIndex($$0.toLong());
      }
   }

   public void flushWorker() {
      this.worker.synchronize(true).join();
   }

   public void close() throws IOException {
      this.worker.close();
   }

   public ChunkScanAccess chunkScanner() {
      return this.worker;
   }

   protected RegionStorageInfo storageInfo() {
      return this.worker.storageInfo();
   }
}
