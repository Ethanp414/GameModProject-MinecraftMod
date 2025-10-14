package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldBorder extends SavedData {
   public static final double MAX_SIZE = 5.999997E7F;
   public static final double MAX_CENTER_COORDINATE = 2.9999984E7;
   public static final Codec<WorldBorder> CODEC = WorldBorder.Settings.CODEC.xmap(WorldBorder.Settings::toWorldBorder, WorldBorder.Settings::new);
   public static final SavedDataType<WorldBorder> TYPE = new SavedDataType<>(
      "world_border", $$0 -> WorldBorder.Settings.DEFAULT.toWorldBorder(), $$0 -> CODEC, DataFixTypes.SAVED_DATA_WORLD_BORDER
   );
   private final List<BorderChangeListener> listeners = Lists.<BorderChangeListener>newArrayList();
   double damagePerBlock = 0.2;
   double safeZone = 5.0;
   int warningTime = 15;
   int warningBlocks = 5;
   double centerX;
   double centerZ;
   int absoluteMaxSize = 29999984;
   WorldBorder.BorderExtent extent = new WorldBorder.StaticBorderExtent(5.999997E7F);

   public boolean isWithinBounds(BlockPos $$0) {
      return this.isWithinBounds((double)$$0.getX(), (double)$$0.getZ());
   }

   public boolean isWithinBounds(Vec3 $$0) {
      return this.isWithinBounds($$0.x, $$0.z);
   }

   public boolean isWithinBounds(ChunkPos $$0) {
      return this.isWithinBounds((double)$$0.getMinBlockX(), (double)$$0.getMinBlockZ())
         && this.isWithinBounds((double)$$0.getMaxBlockX(), (double)$$0.getMaxBlockZ());
   }

   public boolean isWithinBounds(AABB $$0) {
      return this.isWithinBounds($$0.minX, $$0.minZ, $$0.maxX - 1.0E-5F, $$0.maxZ - 1.0E-5F);
   }

   private boolean isWithinBounds(double $$0, double $$1, double $$2, double $$3) {
      return this.isWithinBounds($$0, $$1) && this.isWithinBounds($$2, $$3);
   }

   public boolean isWithinBounds(double $$0, double $$1) {
      return this.isWithinBounds($$0, $$1, 0.0);
   }

   public boolean isWithinBounds(double $$0, double $$1, double $$2) {
      return $$0 >= this.getMinX() - $$2 && $$0 < this.getMaxX() + $$2 && $$1 >= this.getMinZ() - $$2 && $$1 < this.getMaxZ() + $$2;
   }

   public BlockPos clampToBounds(BlockPos $$0) {
      return this.clampToBounds((double)$$0.getX(), (double)$$0.getY(), (double)$$0.getZ());
   }

   public BlockPos clampToBounds(Vec3 $$0) {
      return this.clampToBounds($$0.x(), $$0.y(), $$0.z());
   }

   public BlockPos clampToBounds(double $$0, double $$1, double $$2) {
      return BlockPos.containing(this.clampVec3ToBound($$0, $$1, $$2));
   }

   public Vec3 clampVec3ToBound(Vec3 $$0) {
      return this.clampVec3ToBound($$0.x, $$0.y, $$0.z);
   }

   public Vec3 clampVec3ToBound(double $$0, double $$1, double $$2) {
      return new Vec3(Mth.clamp($$0, this.getMinX(), this.getMaxX() - 1.0E-5F), $$1, Mth.clamp($$2, this.getMinZ(), this.getMaxZ() - 1.0E-5F));
   }

   public double getDistanceToBorder(Entity $$0) {
      return this.getDistanceToBorder($$0.getX(), $$0.getZ());
   }

   public VoxelShape getCollisionShape() {
      return this.extent.getCollisionShape();
   }

   public double getDistanceToBorder(double $$0, double $$1) {
      double $$2 = $$1 - this.getMinZ();
      double $$3 = this.getMaxZ() - $$1;
      double $$4 = $$0 - this.getMinX();
      double $$5 = this.getMaxX() - $$0;
      double $$6 = Math.min($$4, $$5);
      $$6 = Math.min($$6, $$2);
      return Math.min($$6, $$3);
   }

   public boolean isInsideCloseToBorder(Entity $$0, AABB $$1) {
      double $$2 = Math.max(Mth.absMax($$1.getXsize(), $$1.getZsize()), 1.0);
      return this.getDistanceToBorder($$0) < $$2 * 2.0 && this.isWithinBounds($$0.getX(), $$0.getZ(), $$2);
   }

   public BorderStatus getStatus() {
      return this.extent.getStatus();
   }

   public double getMinX() {
      return this.extent.getMinX();
   }

   public double getMinZ() {
      return this.extent.getMinZ();
   }

   public double getMaxX() {
      return this.extent.getMaxX();
   }

   public double getMaxZ() {
      return this.extent.getMaxZ();
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double $$0, double $$1) {
      this.centerX = $$0;
      this.centerZ = $$1;
      this.extent.onCenterChange();
      this.setDirty();

      for(BorderChangeListener $$2 : this.getListeners()) {
         $$2.onSetCenter(this, $$0, $$1);
      }
   }

   public double getSize() {
      return this.extent.getSize();
   }

   public long getLerpTime() {
      return this.extent.getLerpTime();
   }

   public double getLerpTarget() {
      return this.extent.getLerpTarget();
   }

   public void setSize(double $$0) {
      this.extent = new WorldBorder.StaticBorderExtent($$0);
      this.setDirty();

      for(BorderChangeListener $$1 : this.getListeners()) {
         $$1.onSetSize(this, $$0);
      }
   }

   public void lerpSizeBetween(double $$0, double $$1, long $$2) {
      this.extent = (WorldBorder.BorderExtent)($$0 == $$1 ? new WorldBorder.StaticBorderExtent($$1) : new WorldBorder.MovingBorderExtent($$0, $$1, $$2));
      this.setDirty();

      for(BorderChangeListener $$3 : this.getListeners()) {
         $$3.onLerpSize(this, $$0, $$1, $$2);
      }
   }

   protected List<BorderChangeListener> getListeners() {
      return Lists.<BorderChangeListener>newArrayList(this.listeners);
   }

   public void addListener(BorderChangeListener $$0) {
      this.listeners.add($$0);
   }

   public void removeListener(BorderChangeListener $$0) {
      this.listeners.remove($$0);
   }

   public void setAbsoluteMaxSize(int $$0) {
      this.absoluteMaxSize = $$0;
      this.extent.onAbsoluteMaxSizeChange();
   }

   public int getAbsoluteMaxSize() {
      return this.absoluteMaxSize;
   }

   public double getSafeZone() {
      return this.safeZone;
   }

   public void setSafeZone(double $$0) {
      this.safeZone = $$0;
      this.setDirty();

      for(BorderChangeListener $$1 : this.getListeners()) {
         $$1.onSetSafeZone(this, $$0);
      }
   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double $$0) {
      this.damagePerBlock = $$0;
      this.setDirty();

      for(BorderChangeListener $$1 : this.getListeners()) {
         $$1.onSetDamagePerBlock(this, $$0);
      }
   }

   public double getLerpSpeed() {
      return this.extent.getLerpSpeed();
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int $$0) {
      this.warningTime = $$0;
      this.setDirty();

      for(BorderChangeListener $$1 : this.getListeners()) {
         $$1.onSetWarningTime(this, $$0);
      }
   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }

   public void setWarningBlocks(int $$0) {
      this.warningBlocks = $$0;
      this.setDirty();

      for(BorderChangeListener $$1 : this.getListeners()) {
         $$1.onSetWarningBlocks(this, $$0);
      }
   }

   public void tick() {
      this.extent = this.extent.update();
   }

   public void applySettings(WorldBorder.Settings $$0) {
      this.setCenter($$0.centerX(), $$0.centerZ());
      this.setDamagePerBlock($$0.damagePerBlock());
      this.setSafeZone($$0.safeZone());
      this.setWarningBlocks($$0.warningBlocks());
      this.setWarningTime($$0.warningTime());
      if ($$0.lerpTime() > 0L) {
         this.lerpSizeBetween($$0.size(), $$0.lerpTarget(), $$0.lerpTime());
      } else {
         this.setSize($$0.size());
      }
   }

   interface BorderExtent {
      double getMinX();

      double getMaxX();

      double getMinZ();

      double getMaxZ();

      double getSize();

      double getLerpSpeed();

      long getLerpTime();

      double getLerpTarget();

      BorderStatus getStatus();

      void onAbsoluteMaxSizeChange();

      void onCenterChange();

      WorldBorder.BorderExtent update();

      VoxelShape getCollisionShape();
   }

   class MovingBorderExtent implements WorldBorder.BorderExtent {
      private final double from;
      private final double to;
      private final long lerpEnd;
      private final long lerpBegin;
      private final double lerpDuration;

      MovingBorderExtent(final double param2, final double param4, final long param6) {
         this.from = $$0;
         this.to = $$1;
         this.lerpDuration = (double)$$2;
         this.lerpBegin = Util.getMillis();
         this.lerpEnd = this.lerpBegin + $$2;
      }

      @Override
      public double getMinX() {
         return Mth.clamp(
            WorldBorder.this.getCenterX() - this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
      }

      @Override
      public double getMinZ() {
         return Mth.clamp(
            WorldBorder.this.getCenterZ() - this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
      }

      @Override
      public double getMaxX() {
         return Mth.clamp(
            WorldBorder.this.getCenterX() + this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
      }

      @Override
      public double getMaxZ() {
         return Mth.clamp(
            WorldBorder.this.getCenterZ() + this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
      }

      @Override
      public double getSize() {
         double $$0 = (double)(Util.getMillis() - this.lerpBegin) / this.lerpDuration;
         return $$0 < 1.0 ? Mth.lerp($$0, this.from, this.to) : this.to;
      }

      @Override
      public double getLerpSpeed() {
         return Math.abs(this.from - this.to) / (double)(this.lerpEnd - this.lerpBegin);
      }

      @Override
      public long getLerpTime() {
         return this.lerpEnd - Util.getMillis();
      }

      @Override
      public double getLerpTarget() {
         return this.to;
      }

      @Override
      public BorderStatus getStatus() {
         return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
      }

      @Override
      public void onCenterChange() {
      }

      @Override
      public void onAbsoluteMaxSizeChange() {
      }

      @Override
      public WorldBorder.BorderExtent update() {
         if (this.getLerpTime() <= 0L) {
            WorldBorder.this.setDirty();
            return WorldBorder.this.new StaticBorderExtent(this.to);
         } else {
            return this;
         }
      }

      @Override
      public VoxelShape getCollisionShape() {
         return Shapes.join(
            Shapes.INFINITY,
            Shapes.box(
               Math.floor(this.getMinX()),
               Double.NEGATIVE_INFINITY,
               Math.floor(this.getMinZ()),
               Math.ceil(this.getMaxX()),
               Double.POSITIVE_INFINITY,
               Math.ceil(this.getMaxZ())
            ),
            BooleanOp.ONLY_FIRST
         );
      }
   }

   public static record Settings(
      double centerX,
      double centerZ,
      double damagePerBlock,
      double safeZone,
      int warningBlocks,
      int warningTime,
      double size,
      long lerpTime,
      double lerpTarget
   ) {
      public static final WorldBorder.Settings DEFAULT = new WorldBorder.Settings(0.0, 0.0, 0.2, 5.0, 5, 15, 5.999997E7F, 0L, 0.0);
      public static final Codec<WorldBorder.Settings> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  Codec.doubleRange(-2.9999984E7, 2.9999984E7).fieldOf("center_x").forGetter(WorldBorder.Settings::centerX),
                  Codec.doubleRange(-2.9999984E7, 2.9999984E7).fieldOf("center_z").forGetter(WorldBorder.Settings::centerZ),
                  Codec.DOUBLE.fieldOf("damage_per_block").forGetter(WorldBorder.Settings::damagePerBlock),
                  Codec.DOUBLE.fieldOf("safe_zone").forGetter(WorldBorder.Settings::safeZone),
                  Codec.INT.fieldOf("warning_blocks").forGetter(WorldBorder.Settings::warningBlocks),
                  Codec.INT.fieldOf("warning_time").forGetter(WorldBorder.Settings::warningTime),
                  Codec.DOUBLE.fieldOf("size").forGetter(WorldBorder.Settings::size),
                  Codec.LONG.fieldOf("lerp_time").forGetter(WorldBorder.Settings::lerpTime),
                  Codec.DOUBLE.fieldOf("lerp_target").forGetter(WorldBorder.Settings::lerpTarget)
               )
               .apply($$0, WorldBorder.Settings::new)
      );

      public Settings(WorldBorder $$0) {
         this(
            $$0.centerX,
            $$0.centerZ,
            $$0.damagePerBlock,
            $$0.safeZone,
            $$0.warningBlocks,
            $$0.warningTime,
            $$0.extent.getSize(),
            $$0.extent.getLerpTime(),
            $$0.extent.getLerpTarget()
         );
      }

      public WorldBorder toWorldBorder() {
         WorldBorder $$0 = new WorldBorder();
         $$0.applySettings(this);
         return $$0;
      }
   }

   class StaticBorderExtent implements WorldBorder.BorderExtent {
      private final double size;
      private double minX;
      private double minZ;
      private double maxX;
      private double maxZ;
      private VoxelShape shape;

      public StaticBorderExtent(final double param2) {
         this.size = $$0;
         this.updateBox();
      }

      @Override
      public double getMinX() {
         return this.minX;
      }

      @Override
      public double getMaxX() {
         return this.maxX;
      }

      @Override
      public double getMinZ() {
         return this.minZ;
      }

      @Override
      public double getMaxZ() {
         return this.maxZ;
      }

      @Override
      public double getSize() {
         return this.size;
      }

      @Override
      public BorderStatus getStatus() {
         return BorderStatus.STATIONARY;
      }

      @Override
      public double getLerpSpeed() {
         return 0.0;
      }

      @Override
      public long getLerpTime() {
         return 0L;
      }

      @Override
      public double getLerpTarget() {
         return this.size;
      }

      private void updateBox() {
         this.minX = Mth.clamp(
            WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
         this.minZ = Mth.clamp(
            WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
         this.maxX = Mth.clamp(
            WorldBorder.this.getCenterX() + this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
         this.maxZ = Mth.clamp(
            WorldBorder.this.getCenterZ() + this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
         this.shape = Shapes.join(
            Shapes.INFINITY,
            Shapes.box(
               Math.floor(this.getMinX()),
               Double.NEGATIVE_INFINITY,
               Math.floor(this.getMinZ()),
               Math.ceil(this.getMaxX()),
               Double.POSITIVE_INFINITY,
               Math.ceil(this.getMaxZ())
            ),
            BooleanOp.ONLY_FIRST
         );
      }

      @Override
      public void onAbsoluteMaxSizeChange() {
         this.updateBox();
      }

      @Override
      public void onCenterChange() {
         this.updateBox();
      }

      @Override
      public WorldBorder.BorderExtent update() {
         return this;
      }

      @Override
      public VoxelShape getCollisionShape() {
         return this.shape;
      }
   }
}
