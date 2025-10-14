package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class ChunkPos {
   public static final Codec<ChunkPos> CODEC = Codec.INT_STREAM
      .<ChunkPos>comapFlatMap($$0 -> Util.fixedSize($$0, 2).map($$0x -> new ChunkPos($$0x[0], $$0x[1])), $$0 -> IntStream.of(new int[]{$$0.x, $$0.z}))
      .stable();
   public static final StreamCodec<ByteBuf, ChunkPos> STREAM_CODEC = new StreamCodec<ByteBuf, ChunkPos>() {
      public ChunkPos decode(ByteBuf $$0) {
         return FriendlyByteBuf.readChunkPos($$0);
      }

      public void encode(ByteBuf $$0, ChunkPos $$1) {
         FriendlyByteBuf.writeChunkPos($$0, $$1);
      }
   };
   private static final int SAFETY_MARGIN = 1056;
   public static final long INVALID_CHUNK_POS = asLong(1875066, 1875066);
   private static final int SAFETY_MARGIN_CHUNKS = (32 + ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().size() + 1) * 2;
   public static final int MAX_COORDINATE_VALUE = SectionPos.blockToSectionCoord(BlockPos.MAX_HORIZONTAL_COORDINATE) - SAFETY_MARGIN_CHUNKS;
   public static final ChunkPos ZERO = new ChunkPos(0, 0);
   private static final long COORD_BITS = 32L;
   private static final long COORD_MASK = 4294967295L;
   private static final int REGION_BITS = 5;
   public static final int REGION_SIZE = 32;
   private static final int REGION_MASK = 31;
   public static final int REGION_MAX_INDEX = 31;
   public final int x;
   public final int z;
   private static final int HASH_A = 1664525;
   private static final int HASH_C = 1013904223;
   private static final int HASH_Z_XOR = -559038737;

   public ChunkPos(int $$0, int $$1) {
      this.x = $$0;
      this.z = $$1;
   }

   public ChunkPos(BlockPos $$0) {
      this.x = SectionPos.blockToSectionCoord($$0.getX());
      this.z = SectionPos.blockToSectionCoord($$0.getZ());
   }

   public ChunkPos(long $$0) {
      this.x = (int)$$0;
      this.z = (int)($$0 >> 32);
   }

   public static ChunkPos minFromRegion(int $$0, int $$1) {
      return new ChunkPos($$0 << 5, $$1 << 5);
   }

   public static ChunkPos maxFromRegion(int $$0, int $$1) {
      return new ChunkPos(($$0 << 5) + 31, ($$1 << 5) + 31);
   }

   public long toLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int $$0, int $$1) {
      return (long)$$0 & 4294967295L | ((long)$$1 & 4294967295L) << 32;
   }

   public static long asLong(BlockPos $$0) {
      return asLong(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
   }

   public static int getX(long $$0) {
      return (int)($$0 & 4294967295L);
   }

   public static int getZ(long $$0) {
      return (int)($$0 >>> 32 & 4294967295L);
   }

   public int hashCode() {
      return hash(this.x, this.z);
   }

   public static int hash(int $$0, int $$1) {
      int $$2 = 1664525 * $$0 + 1013904223;
      int $$3 = 1664525 * ($$1 ^ -559038737) + 1013904223;
      return $$2 ^ $$3;
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else if (!($$0 instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos $$1 = (ChunkPos)$$0;
         return this.x == $$1.x && this.z == $$1.z;
      }
   }

   public int getMiddleBlockX() {
      return this.getBlockX(8);
   }

   public int getMiddleBlockZ() {
      return this.getBlockZ(8);
   }

   public int getMinBlockX() {
      return SectionPos.sectionToBlockCoord(this.x);
   }

   public int getMinBlockZ() {
      return SectionPos.sectionToBlockCoord(this.z);
   }

   public int getMaxBlockX() {
      return this.getBlockX(15);
   }

   public int getMaxBlockZ() {
      return this.getBlockZ(15);
   }

   public int getRegionX() {
      return this.x >> 5;
   }

   public int getRegionZ() {
      return this.z >> 5;
   }

   public int getRegionLocalX() {
      return this.x & 31;
   }

   public int getRegionLocalZ() {
      return this.z & 31;
   }

   public BlockPos getBlockAt(int $$0, int $$1, int $$2) {
      return new BlockPos(this.getBlockX($$0), $$1, this.getBlockZ($$2));
   }

   public int getBlockX(int $$0) {
      return SectionPos.sectionToBlockCoord(this.x, $$0);
   }

   public int getBlockZ(int $$0) {
      return SectionPos.sectionToBlockCoord(this.z, $$0);
   }

   public BlockPos getMiddleBlockPosition(int $$0) {
      return new BlockPos(this.getMiddleBlockX(), $$0, this.getMiddleBlockZ());
   }

   public boolean contains(BlockPos $$0) {
      return $$0.getX() >= this.getMinBlockX() && $$0.getZ() >= this.getMinBlockZ() && $$0.getX() <= this.getMaxBlockX() && $$0.getZ() <= this.getMaxBlockZ();
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public BlockPos getWorldPosition() {
      return new BlockPos(this.getMinBlockX(), 0, this.getMinBlockZ());
   }

   public int getChessboardDistance(ChunkPos $$0) {
      return this.getChessboardDistance($$0.x, $$0.z);
   }

   public int getChessboardDistance(int $$0, int $$1) {
      return Math.max(Math.abs(this.x - $$0), Math.abs(this.z - $$1));
   }

   public int distanceSquared(ChunkPos $$0) {
      return this.distanceSquared($$0.x, $$0.z);
   }

   public int distanceSquared(long $$0) {
      return this.distanceSquared(getX($$0), getZ($$0));
   }

   private int distanceSquared(int $$0, int $$1) {
      int $$2 = $$0 - this.x;
      int $$3 = $$1 - this.z;
      return $$2 * $$2 + $$3 * $$3;
   }

   public static Stream<ChunkPos> rangeClosed(ChunkPos $$0, int $$1) {
      return rangeClosed(new ChunkPos($$0.x - $$1, $$0.z - $$1), new ChunkPos($$0.x + $$1, $$0.z + $$1));
   }

   public static Stream<ChunkPos> rangeClosed(final ChunkPos $$0, final ChunkPos $$1) {
      int $$2 = Math.abs($$0.x - $$1.x) + 1;
      int $$3 = Math.abs($$0.z - $$1.z) + 1;
      final int $$4 = $$0.x < $$1.x ? 1 : -1;
      final int $$5 = $$0.z < $$1.z ? 1 : -1;
      return StreamSupport.stream(new AbstractSpliterator<ChunkPos>((long)($$2 * $$3), 64) {
         @Nullable
         private ChunkPos pos;

         public boolean tryAdvance(Consumer<? super ChunkPos> $$0x) {
            if (this.pos == null) {
               this.pos = $$0;
            } else {
               int $$1x = this.pos.x;
               int $$2 = this.pos.z;
               if ($$1x == $$1.x) {
                  if ($$2 == $$1.z) {
                     return false;
                  }

                  this.pos = new ChunkPos($$0.x, $$2 + $$5);
               } else {
                  this.pos = new ChunkPos($$1x + $$4, $$2);
               }
            }

            $$0.accept(this.pos);
            return true;
         }
      }, false);
   }
}
