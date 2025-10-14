package net.minecraft.world.level.chunk.storage;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.world.level.ChunkPos;

public interface ChunkIOErrorReporter {
   void reportChunkLoadFailure(Throwable var1, RegionStorageInfo var2, ChunkPos var3);

   void reportChunkSaveFailure(Throwable var1, RegionStorageInfo var2, ChunkPos var3);

   static ReportedException createMisplacedChunkReport(ChunkPos $$0, ChunkPos $$1) {
      CrashReport $$2 = CrashReport.forThrowable(
         new IllegalStateException("Retrieved chunk position " + $$0 + " does not match requested " + $$1), "Chunk found in invalid location"
      );
      CrashReportCategory $$3 = $$2.addCategory("Misplaced Chunk");
      $$3.setDetail("Stored Position", $$0::toString);
      return new ReportedException($$2);
   }

   default void reportMisplacedChunk(ChunkPos $$0, ChunkPos $$1, RegionStorageInfo $$2) {
      this.reportChunkLoadFailure(createMisplacedChunkReport($$0, $$1), $$2, $$1);
   }
}
