package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;

public class SimpleRegionStorage implements AutoCloseable {
   private final IOWorker worker;
   private final DataFixer fixerUpper;
   private final DataFixTypes dataFixType;

   public SimpleRegionStorage(RegionStorageInfo $$0, Path $$1, DataFixer $$2, boolean $$3, DataFixTypes $$4) {
      this.fixerUpper = $$2;
      this.dataFixType = $$4;
      this.worker = new IOWorker($$0, $$1, $$3);
   }

   public CompletableFuture<Optional<CompoundTag>> read(ChunkPos $$0) {
      return this.worker.loadAsync($$0);
   }

   public CompletableFuture<Void> write(ChunkPos $$0, @Nullable CompoundTag $$1) {
      return this.worker.store($$0, $$1);
   }

   public CompoundTag upgradeChunkTag(CompoundTag $$0, int $$1) {
      int $$2 = NbtUtils.getDataVersion($$0, $$1);
      CompoundTag $$3 = this.dataFixType.updateToCurrentVersion(this.fixerUpper, $$0, $$2);
      return NbtUtils.addCurrentDataVersion($$3);
   }

   public Dynamic<Tag> upgradeChunkTag(Dynamic<Tag> $$0, int $$1) {
      int $$2 = NbtUtils.getDataVersion($$0, $$1);
      Dynamic<Tag> $$3 = this.dataFixType.updateToCurrentVersion(this.fixerUpper, $$0, $$2);
      return NbtUtils.addCurrentDataVersion($$3);
   }

   public CompletableFuture<Void> synchronize(boolean $$0) {
      return this.worker.synchronize($$0);
   }

   public void close() throws IOException {
      this.worker.close();
   }

   public RegionStorageInfo storageInfo() {
      return this.worker.storageInfo();
   }
}
