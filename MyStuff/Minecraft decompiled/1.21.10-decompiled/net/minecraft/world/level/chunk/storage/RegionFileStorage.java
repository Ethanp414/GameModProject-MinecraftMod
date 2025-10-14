package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;

public final class RegionFileStorage implements AutoCloseable {
   public static final String ANVIL_EXTENSION = ".mca";
   private static final int MAX_CACHE_SIZE = 256;
   private final Long2ObjectLinkedOpenHashMap<RegionFile> regionCache = new Long2ObjectLinkedOpenHashMap<>();
   private final RegionStorageInfo info;
   private final Path folder;
   private final boolean sync;

   RegionFileStorage(RegionStorageInfo $$0, Path $$1, boolean $$2) {
      this.folder = $$1;
      this.sync = $$2;
      this.info = $$0;
   }

   private RegionFile getRegionFile(ChunkPos $$0) throws IOException {
      long $$1 = ChunkPos.asLong($$0.getRegionX(), $$0.getRegionZ());
      RegionFile $$2 = this.regionCache.getAndMoveToFirst($$1);
      if ($$2 != null) {
         return $$2;
      } else {
         if (this.regionCache.size() >= 256) {
            this.regionCache.removeLast().close();
         }

         FileUtil.createDirectoriesSafe(this.folder);
         Path $$3 = this.folder.resolve("r." + $$0.getRegionX() + "." + $$0.getRegionZ() + ".mca");
         RegionFile $$4 = new RegionFile(this.info, $$3, this.folder, this.sync);
         this.regionCache.putAndMoveToFirst($$1, $$4);
         return $$4;
      }
   }

   @Nullable
   public CompoundTag read(ChunkPos $$0) throws IOException {
      RegionFile $$1 = this.getRegionFile($$0);
      DataInputStream $$2 = $$1.getChunkDataInputStream($$0);

      CompoundTag var8;
      label43: {
         try {
            if ($$2 == null) {
               var8 = null;
               break label43;
            }

            var8 = NbtIo.read($$2);
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

         return var8;
      }

      if ($$2 != null) {
         $$2.close();
      }

      return var8;
   }

   public void scanChunk(ChunkPos $$0, StreamTagVisitor $$1) throws IOException {
      RegionFile $$2 = this.getRegionFile($$0);
      DataInputStream $$3 = $$2.getChunkDataInputStream($$0);

      try {
         if ($$3 != null) {
            NbtIo.parse($$3, $$1, NbtAccounter.unlimitedHeap());
         }
      } catch (Throwable var8) {
         if ($$3 != null) {
            try {
               $$3.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if ($$3 != null) {
         $$3.close();
      }
   }

   protected void write(ChunkPos $$0, @Nullable CompoundTag $$1) throws IOException {
      if (!SharedConstants.DEBUG_DONT_SAVE_WORLD) {
         RegionFile $$2 = this.getRegionFile($$0);
         if ($$1 == null) {
            $$2.clear($$0);
         } else {
            DataOutputStream $$3 = $$2.getChunkDataOutputStream($$0);

            try {
               NbtIo.write($$1, $$3);
            } catch (Throwable var8) {
               if ($$3 != null) {
                  try {
                     $$3.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if ($$3 != null) {
               $$3.close();
            }
         }
      }
   }

   public void close() throws IOException {
      ExceptionCollector<IOException> $$0 = new ExceptionCollector();

      for(RegionFile $$1 : this.regionCache.values()) {
         try {
            $$1.close();
         } catch (IOException var5) {
            $$0.add(var5);
         }
      }

      $$0.throwIfPresent();
   }

   public void flush() throws IOException {
      for(RegionFile $$0 : this.regionCache.values()) {
         $$0.flush();
      }
   }

   public RegionStorageInfo info() {
      return this.info;
   }
}
