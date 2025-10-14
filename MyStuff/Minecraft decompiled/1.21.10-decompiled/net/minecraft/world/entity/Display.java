package net.minecraft.world.entity;

import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Brightness;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public abstract class Display extends Entity {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final int NO_BRIGHTNESS_OVERRIDE = -1;
   private static final EntityDataAccessor<Integer> DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID = SynchedEntityData.defineId(
      Display.class, EntityDataSerializers.INT
   );
   private static final EntityDataAccessor<Integer> DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID = SynchedEntityData.defineId(
      Display.class, EntityDataSerializers.INT
   );
   private static final EntityDataAccessor<Integer> DATA_POS_ROT_INTERPOLATION_DURATION_ID = SynchedEntityData.defineId(
      Display.class, EntityDataSerializers.INT
   );
   private static final EntityDataAccessor<Vector3f> DATA_TRANSLATION_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Vector3f> DATA_SCALE_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Quaternionf> DATA_LEFT_ROTATION_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.QUATERNION);
   private static final EntityDataAccessor<Quaternionf> DATA_RIGHT_ROTATION_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.QUATERNION);
   private static final EntityDataAccessor<Byte> DATA_BILLBOARD_RENDER_CONSTRAINTS_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Integer> DATA_BRIGHTNESS_OVERRIDE_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_VIEW_RANGE_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_SHADOW_RADIUS_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_SHADOW_STRENGTH_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_WIDTH_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_HEIGHT_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_GLOW_COLOR_OVERRIDE_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.INT);
   private static final IntSet RENDER_STATE_IDS = IntSet.of(
      DATA_TRANSLATION_ID.id(),
      DATA_SCALE_ID.id(),
      DATA_LEFT_ROTATION_ID.id(),
      DATA_RIGHT_ROTATION_ID.id(),
      DATA_BILLBOARD_RENDER_CONSTRAINTS_ID.id(),
      DATA_BRIGHTNESS_OVERRIDE_ID.id(),
      DATA_SHADOW_RADIUS_ID.id(),
      DATA_SHADOW_STRENGTH_ID.id()
   );
   private static final int INITIAL_TRANSFORMATION_INTERPOLATION_DURATION = 0;
   private static final int INITIAL_TRANSFORMATION_START_INTERPOLATION = 0;
   private static final int INITIAL_POS_ROT_INTERPOLATION_DURATION = 0;
   private static final float INITIAL_SHADOW_RADIUS = 0.0F;
   private static final float INITIAL_SHADOW_STRENGTH = 1.0F;
   private static final float INITIAL_VIEW_RANGE = 1.0F;
   private static final float INITIAL_WIDTH = 0.0F;
   private static final float INITIAL_HEIGHT = 0.0F;
   private static final int NO_GLOW_COLOR_OVERRIDE = -1;
   public static final String TAG_POS_ROT_INTERPOLATION_DURATION = "teleport_duration";
   public static final String TAG_TRANSFORMATION_INTERPOLATION_DURATION = "interpolation_duration";
   public static final String TAG_TRANSFORMATION_START_INTERPOLATION = "start_interpolation";
   public static final String TAG_TRANSFORMATION = "transformation";
   public static final String TAG_BILLBOARD = "billboard";
   public static final String TAG_BRIGHTNESS = "brightness";
   public static final String TAG_VIEW_RANGE = "view_range";
   public static final String TAG_SHADOW_RADIUS = "shadow_radius";
   public static final String TAG_SHADOW_STRENGTH = "shadow_strength";
   public static final String TAG_WIDTH = "width";
   public static final String TAG_HEIGHT = "height";
   public static final String TAG_GLOW_COLOR_OVERRIDE = "glow_color_override";
   private long interpolationStartClientTick = -2147483648L;
   private int interpolationDuration;
   private float lastProgress;
   private AABB cullingBoundingBox;
   private boolean noCulling = true;
   protected boolean updateRenderState;
   private boolean updateStartTick;
   private boolean updateInterpolationDuration;
   @Nullable
   private Display.RenderState renderState;
   private final InterpolationHandler interpolation = new InterpolationHandler(this, 0);

   public Display(EntityType<?> $$0, Level $$1) {
      super($$0, $$1);
      this.noPhysics = true;
      this.cullingBoundingBox = this.getBoundingBox();
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
      super.onSyncedDataUpdated($$0);
      if (DATA_HEIGHT_ID.equals($$0) || DATA_WIDTH_ID.equals($$0)) {
         this.updateCulling();
      }

      if (DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID.equals($$0)) {
         this.updateStartTick = true;
      }

      if (DATA_POS_ROT_INTERPOLATION_DURATION_ID.equals($$0)) {
         this.interpolation.setInterpolationLength(this.getPosRotInterpolationDuration());
      }

      if (DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID.equals($$0)) {
         this.updateInterpolationDuration = true;
      }

      if (RENDER_STATE_IDS.contains($$0.id())) {
         this.updateRenderState = true;
      }
   }

   @Override
   public final boolean hurtServer(ServerLevel $$0, DamageSource $$1, float $$2) {
      return false;
   }

   private static Transformation createTransformation(SynchedEntityData $$0) {
      Vector3f $$1 = $$0.get(DATA_TRANSLATION_ID);
      Quaternionf $$2 = $$0.get(DATA_LEFT_ROTATION_ID);
      Vector3f $$3 = $$0.get(DATA_SCALE_ID);
      Quaternionf $$4 = $$0.get(DATA_RIGHT_ROTATION_ID);
      return new Transformation($$1, $$2, $$3, $$4);
   }

   @Override
   public void tick() {
      Entity $$0 = this.getVehicle();
      if ($$0 != null && $$0.isRemoved()) {
         this.stopRiding();
      }

      if (this.level().isClientSide()) {
         if (this.updateStartTick) {
            this.updateStartTick = false;
            int $$1 = this.getTransformationInterpolationDelay();
            this.interpolationStartClientTick = (long)(this.tickCount + $$1);
         }

         if (this.updateInterpolationDuration) {
            this.updateInterpolationDuration = false;
            this.interpolationDuration = this.getTransformationInterpolationDuration();
         }

         if (this.updateRenderState) {
            this.updateRenderState = false;
            boolean $$2 = this.interpolationDuration != 0;
            if ($$2 && this.renderState != null) {
               this.renderState = this.createInterpolatedRenderState(this.renderState, this.lastProgress);
            } else {
               this.renderState = this.createFreshRenderState();
            }

            this.updateRenderSubState($$2, this.lastProgress);
         }

         this.interpolation.interpolate();
      }
   }

   @Override
   public InterpolationHandler getInterpolation() {
      return this.interpolation;
   }

   protected abstract void updateRenderSubState(boolean var1, float var2);

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder $$0) {
      $$0.define(DATA_POS_ROT_INTERPOLATION_DURATION_ID, 0);
      $$0.define(DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID, 0);
      $$0.define(DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID, 0);
      $$0.define(DATA_TRANSLATION_ID, new Vector3f());
      $$0.define(DATA_SCALE_ID, new Vector3f(1.0F, 1.0F, 1.0F));
      $$0.define(DATA_RIGHT_ROTATION_ID, new Quaternionf());
      $$0.define(DATA_LEFT_ROTATION_ID, new Quaternionf());
      $$0.define(DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, Display.BillboardConstraints.FIXED.getId());
      $$0.define(DATA_BRIGHTNESS_OVERRIDE_ID, -1);
      $$0.define(DATA_VIEW_RANGE_ID, 1.0F);
      $$0.define(DATA_SHADOW_RADIUS_ID, 0.0F);
      $$0.define(DATA_SHADOW_STRENGTH_ID, 1.0F);
      $$0.define(DATA_WIDTH_ID, 0.0F);
      $$0.define(DATA_HEIGHT_ID, 0.0F);
      $$0.define(DATA_GLOW_COLOR_OVERRIDE_ID, -1);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput $$0) {
      this.setTransformation((Transformation)$$0.read("transformation", Transformation.EXTENDED_CODEC).orElse(Transformation.identity()));
      this.setTransformationInterpolationDuration($$0.getIntOr("interpolation_duration", 0));
      this.setTransformationInterpolationDelay($$0.getIntOr("start_interpolation", 0));
      int $$1 = $$0.getIntOr("teleport_duration", 0);
      this.setPosRotInterpolationDuration(Mth.clamp($$1, 0, 59));
      this.setBillboardConstraints(
         (Display.BillboardConstraints)$$0.read("billboard", Display.BillboardConstraints.CODEC).orElse(Display.BillboardConstraints.FIXED)
      );
      this.setViewRange($$0.getFloatOr("view_range", 1.0F));
      this.setShadowRadius($$0.getFloatOr("shadow_radius", 0.0F));
      this.setShadowStrength($$0.getFloatOr("shadow_strength", 1.0F));
      this.setWidth($$0.getFloatOr("width", 0.0F));
      this.setHeight($$0.getFloatOr("height", 0.0F));
      this.setGlowColorOverride($$0.getIntOr("glow_color_override", -1));
      this.setBrightnessOverride((Brightness)$$0.read("brightness", Brightness.CODEC).orElse(null));
   }

   private void setTransformation(Transformation $$0) {
      this.entityData.set(DATA_TRANSLATION_ID, $$0.getTranslation());
      this.entityData.set(DATA_LEFT_ROTATION_ID, $$0.getLeftRotation());
      this.entityData.set(DATA_SCALE_ID, $$0.getScale());
      this.entityData.set(DATA_RIGHT_ROTATION_ID, $$0.getRightRotation());
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput $$0) {
      $$0.store("transformation", Transformation.EXTENDED_CODEC, createTransformation(this.entityData));
      $$0.store("billboard", Display.BillboardConstraints.CODEC, this.getBillboardConstraints());
      $$0.putInt("interpolation_duration", this.getTransformationInterpolationDuration());
      $$0.putInt("teleport_duration", this.getPosRotInterpolationDuration());
      $$0.putFloat("view_range", this.getViewRange());
      $$0.putFloat("shadow_radius", this.getShadowRadius());
      $$0.putFloat("shadow_strength", this.getShadowStrength());
      $$0.putFloat("width", this.getWidth());
      $$0.putFloat("height", this.getHeight());
      $$0.putInt("glow_color_override", this.getGlowColorOverride());
      $$0.storeNullable("brightness", Brightness.CODEC, this.getBrightnessOverride());
   }

   public AABB getBoundingBoxForCulling() {
      return this.cullingBoundingBox;
   }

   public boolean affectedByCulling() {
      return !this.noCulling;
   }

   @Override
   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   @Override
   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   @Nullable
   public Display.RenderState renderState() {
      return this.renderState;
   }

   private void setTransformationInterpolationDuration(int $$0) {
      this.entityData.set(DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID, $$0);
   }

   private int getTransformationInterpolationDuration() {
      return this.entityData.get(DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID);
   }

   private void setTransformationInterpolationDelay(int $$0) {
      this.entityData.set(DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID, $$0, true);
   }

   private int getTransformationInterpolationDelay() {
      return this.entityData.get(DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID);
   }

   private void setPosRotInterpolationDuration(int $$0) {
      this.entityData.set(DATA_POS_ROT_INTERPOLATION_DURATION_ID, $$0);
   }

   private int getPosRotInterpolationDuration() {
      return this.entityData.get(DATA_POS_ROT_INTERPOLATION_DURATION_ID);
   }

   private void setBillboardConstraints(Display.BillboardConstraints $$0) {
      this.entityData.set(DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, $$0.getId());
   }

   private Display.BillboardConstraints getBillboardConstraints() {
      return (Display.BillboardConstraints)Display.BillboardConstraints.BY_ID.apply(this.entityData.get(DATA_BILLBOARD_RENDER_CONSTRAINTS_ID));
   }

   private void setBrightnessOverride(@Nullable Brightness $$0) {
      this.entityData.set(DATA_BRIGHTNESS_OVERRIDE_ID, $$0 != null ? $$0.pack() : -1);
   }

   @Nullable
   private Brightness getBrightnessOverride() {
      int $$0 = this.entityData.get(DATA_BRIGHTNESS_OVERRIDE_ID);
      return $$0 != -1 ? Brightness.unpack($$0) : null;
   }

   private int getPackedBrightnessOverride() {
      return this.entityData.get(DATA_BRIGHTNESS_OVERRIDE_ID);
   }

   private void setViewRange(float $$0) {
      this.entityData.set(DATA_VIEW_RANGE_ID, $$0);
   }

   private float getViewRange() {
      return this.entityData.get(DATA_VIEW_RANGE_ID);
   }

   private void setShadowRadius(float $$0) {
      this.entityData.set(DATA_SHADOW_RADIUS_ID, $$0);
   }

   private float getShadowRadius() {
      return this.entityData.get(DATA_SHADOW_RADIUS_ID);
   }

   private void setShadowStrength(float $$0) {
      this.entityData.set(DATA_SHADOW_STRENGTH_ID, $$0);
   }

   private float getShadowStrength() {
      return this.entityData.get(DATA_SHADOW_STRENGTH_ID);
   }

   private void setWidth(float $$0) {
      this.entityData.set(DATA_WIDTH_ID, $$0);
   }

   private float getWidth() {
      return this.entityData.get(DATA_WIDTH_ID);
   }

   private void setHeight(float $$0) {
      this.entityData.set(DATA_HEIGHT_ID, $$0);
   }

   private int getGlowColorOverride() {
      return this.entityData.get(DATA_GLOW_COLOR_OVERRIDE_ID);
   }

   private void setGlowColorOverride(int $$0) {
      this.entityData.set(DATA_GLOW_COLOR_OVERRIDE_ID, $$0);
   }

   public float calculateInterpolationProgress(float $$0) {
      int $$1 = this.interpolationDuration;
      if ($$1 <= 0) {
         return 1.0F;
      } else {
         float $$2 = (float)((long)this.tickCount - this.interpolationStartClientTick);
         float $$3 = $$2 + $$0;
         float $$4 = Mth.clamp(Mth.inverseLerp($$3, 0.0F, (float)$$1), 0.0F, 1.0F);
         this.lastProgress = $$4;
         return $$4;
      }
   }

   private float getHeight() {
      return this.entityData.get(DATA_HEIGHT_ID);
   }

   @Override
   public void setPos(double $$0, double $$1, double $$2) {
      super.setPos($$0, $$1, $$2);
      this.updateCulling();
   }

   private void updateCulling() {
      float $$0 = this.getWidth();
      float $$1 = this.getHeight();
      this.noCulling = $$0 == 0.0F || $$1 == 0.0F;
      float $$2 = $$0 / 2.0F;
      double $$3 = this.getX();
      double $$4 = this.getY();
      double $$5 = this.getZ();
      this.cullingBoundingBox = new AABB($$3 - (double)$$2, $$4, $$5 - (double)$$2, $$3 + (double)$$2, $$4 + (double)$$1, $$5 + (double)$$2);
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double $$0) {
      return $$0 < Mth.square((double)this.getViewRange() * 64.0 * getViewScale());
   }

   @Override
   public int getTeamColor() {
      int $$0 = this.getGlowColorOverride();
      return $$0 != -1 ? $$0 : super.getTeamColor();
   }

   private Display.RenderState createFreshRenderState() {
      return new Display.RenderState(
         Display.GenericInterpolator.constant(createTransformation(this.entityData)),
         this.getBillboardConstraints(),
         this.getPackedBrightnessOverride(),
         Display.FloatInterpolator.constant(this.getShadowRadius()),
         Display.FloatInterpolator.constant(this.getShadowStrength()),
         this.getGlowColorOverride()
      );
   }

   private Display.RenderState createInterpolatedRenderState(Display.RenderState $$0, float $$1) {
      Transformation $$2 = $$0.transformation.get($$1);
      float $$3 = $$0.shadowRadius.get($$1);
      float $$4 = $$0.shadowStrength.get($$1);
      return new Display.RenderState(
         new Display.TransformationInterpolator($$2, createTransformation(this.entityData)),
         this.getBillboardConstraints(),
         this.getPackedBrightnessOverride(),
         new Display.LinearFloatInterpolator($$3, this.getShadowRadius()),
         new Display.LinearFloatInterpolator($$4, this.getShadowStrength()),
         this.getGlowColorOverride()
      );
   }

   public static enum BillboardConstraints implements StringRepresentable {
      FIXED((byte)0, "fixed"),
      VERTICAL((byte)1, "vertical"),
      HORIZONTAL((byte)2, "horizontal"),
      CENTER((byte)3, "center");

      public static final Codec<Display.BillboardConstraints> CODEC = StringRepresentable.fromEnum(Display.BillboardConstraints::values);
      public static final IntFunction<Display.BillboardConstraints> BY_ID = ByIdMap.continuous(
         Display.BillboardConstraints::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO
      );
      private final byte id;
      private final String name;

      private BillboardConstraints(final byte param3, final String param4) {
         this.name = $$1;
         this.id = $$0;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      byte getId() {
         return this.id;
      }
   }

   public static class BlockDisplay extends Display {
      public static final String TAG_BLOCK_STATE = "block_state";
      private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(
         Display.BlockDisplay.class, EntityDataSerializers.BLOCK_STATE
      );
      @Nullable
      private Display.BlockDisplay.BlockRenderState blockRenderState;

      public BlockDisplay(EntityType<?> $$0, Level $$1) {
         super($$0, $$1);
      }

      @Override
      protected void defineSynchedData(SynchedEntityData.Builder $$0) {
         super.defineSynchedData($$0);
         $$0.define(DATA_BLOCK_STATE_ID, Blocks.AIR.defaultBlockState());
      }

      @Override
      public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
         super.onSyncedDataUpdated($$0);
         if ($$0.equals(DATA_BLOCK_STATE_ID)) {
            this.updateRenderState = true;
         }
      }

      private BlockState getBlockState() {
         return this.entityData.get(DATA_BLOCK_STATE_ID);
      }

      private void setBlockState(BlockState $$0) {
         this.entityData.set(DATA_BLOCK_STATE_ID, $$0);
      }

      @Override
      protected void readAdditionalSaveData(ValueInput $$0) {
         super.readAdditionalSaveData($$0);
         this.setBlockState((BlockState)$$0.read("block_state", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState()));
      }

      @Override
      protected void addAdditionalSaveData(ValueOutput $$0) {
         super.addAdditionalSaveData($$0);
         $$0.store("block_state", BlockState.CODEC, this.getBlockState());
      }

      @Nullable
      public Display.BlockDisplay.BlockRenderState blockRenderState() {
         return this.blockRenderState;
      }

      @Override
      protected void updateRenderSubState(boolean $$0, float $$1) {
         this.blockRenderState = new Display.BlockDisplay.BlockRenderState(this.getBlockState());
      }

      public static record BlockRenderState(BlockState blockState) {
      }
   }

   static record ColorInterpolator(int previous, int current) implements Display.IntInterpolator {
      @Override
      public int get(float $$0) {
         return ARGB.lerp($$0, this.previous, this.current);
      }
   }

   @FunctionalInterface
   public interface FloatInterpolator {
      static Display.FloatInterpolator constant(float $$0) {
         return $$1 -> $$0;
      }

      float get(float var1);
   }

   @FunctionalInterface
   public interface GenericInterpolator<T> {
      static <T> Display.GenericInterpolator<T> constant(T $$0) {
         return $$1 -> $$0;
      }

      T get(float var1);
   }

   @FunctionalInterface
   public interface IntInterpolator {
      static Display.IntInterpolator constant(int $$0) {
         return $$1 -> $$0;
      }

      int get(float var1);
   }

   public static class ItemDisplay extends Display {
      private static final String TAG_ITEM = "item";
      private static final String TAG_ITEM_DISPLAY = "item_display";
      private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK_ID = SynchedEntityData.defineId(
         Display.ItemDisplay.class, EntityDataSerializers.ITEM_STACK
      );
      private static final EntityDataAccessor<Byte> DATA_ITEM_DISPLAY_ID = SynchedEntityData.defineId(Display.ItemDisplay.class, EntityDataSerializers.BYTE);
      private final SlotAccess slot = SlotAccess.of(this::getItemStack, this::setItemStack);
      @Nullable
      private Display.ItemDisplay.ItemRenderState itemRenderState;

      public ItemDisplay(EntityType<?> $$0, Level $$1) {
         super($$0, $$1);
      }

      @Override
      protected void defineSynchedData(SynchedEntityData.Builder $$0) {
         super.defineSynchedData($$0);
         $$0.define(DATA_ITEM_STACK_ID, ItemStack.EMPTY);
         $$0.define(DATA_ITEM_DISPLAY_ID, ItemDisplayContext.NONE.getId());
      }

      @Override
      public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
         super.onSyncedDataUpdated($$0);
         if (DATA_ITEM_STACK_ID.equals($$0) || DATA_ITEM_DISPLAY_ID.equals($$0)) {
            this.updateRenderState = true;
         }
      }

      private ItemStack getItemStack() {
         return this.entityData.get(DATA_ITEM_STACK_ID);
      }

      private void setItemStack(ItemStack $$0) {
         this.entityData.set(DATA_ITEM_STACK_ID, $$0);
      }

      private void setItemTransform(ItemDisplayContext $$0) {
         this.entityData.set(DATA_ITEM_DISPLAY_ID, $$0.getId());
      }

      private ItemDisplayContext getItemTransform() {
         return (ItemDisplayContext)ItemDisplayContext.BY_ID.apply(this.entityData.get(DATA_ITEM_DISPLAY_ID));
      }

      @Override
      protected void readAdditionalSaveData(ValueInput $$0) {
         super.readAdditionalSaveData($$0);
         this.setItemStack((ItemStack)$$0.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY));
         this.setItemTransform((ItemDisplayContext)$$0.read("item_display", ItemDisplayContext.CODEC).orElse(ItemDisplayContext.NONE));
      }

      @Override
      protected void addAdditionalSaveData(ValueOutput $$0) {
         super.addAdditionalSaveData($$0);
         ItemStack $$1 = this.getItemStack();
         if (!$$1.isEmpty()) {
            $$0.store("item", ItemStack.CODEC, $$1);
         }

         $$0.store("item_display", ItemDisplayContext.CODEC, this.getItemTransform());
      }

      @Override
      public SlotAccess getSlot(int $$0) {
         return $$0 == 0 ? this.slot : SlotAccess.NULL;
      }

      @Nullable
      public Display.ItemDisplay.ItemRenderState itemRenderState() {
         return this.itemRenderState;
      }

      @Override
      protected void updateRenderSubState(boolean $$0, float $$1) {
         ItemStack $$2 = this.getItemStack();
         $$2.setEntityRepresentation(this);
         this.itemRenderState = new Display.ItemDisplay.ItemRenderState($$2, this.getItemTransform());
      }

      public static record ItemRenderState(ItemStack itemStack, ItemDisplayContext itemTransform) {
      }
   }

   static record LinearFloatInterpolator(float previous, float current) implements Display.FloatInterpolator {
      @Override
      public float get(float $$0) {
         return Mth.lerp($$0, this.previous, this.current);
      }
   }

   static record LinearIntInterpolator(int previous, int current) implements Display.IntInterpolator {
      @Override
      public int get(float $$0) {
         return Mth.lerpInt($$0, this.previous, this.current);
      }
   }

   public static record RenderState(
      Display.GenericInterpolator<Transformation> transformation,
      Display.BillboardConstraints billboardConstraints,
      int brightnessOverride,
      Display.FloatInterpolator shadowRadius,
      Display.FloatInterpolator shadowStrength,
      int glowColorOverride
   ) {
      final Display.GenericInterpolator<Transformation> transformation;
      final Display.FloatInterpolator shadowRadius;
      final Display.FloatInterpolator shadowStrength;
   }

   public static class TextDisplay extends Display {
      public static final String TAG_TEXT = "text";
      private static final String TAG_LINE_WIDTH = "line_width";
      private static final String TAG_TEXT_OPACITY = "text_opacity";
      private static final String TAG_BACKGROUND_COLOR = "background";
      private static final String TAG_SHADOW = "shadow";
      private static final String TAG_SEE_THROUGH = "see_through";
      private static final String TAG_USE_DEFAULT_BACKGROUND = "default_background";
      private static final String TAG_ALIGNMENT = "alignment";
      public static final byte FLAG_SHADOW = 1;
      public static final byte FLAG_SEE_THROUGH = 2;
      public static final byte FLAG_USE_DEFAULT_BACKGROUND = 4;
      public static final byte FLAG_ALIGN_LEFT = 8;
      public static final byte FLAG_ALIGN_RIGHT = 16;
      private static final byte INITIAL_TEXT_OPACITY = -1;
      public static final int INITIAL_BACKGROUND = 1073741824;
      private static final int INITIAL_LINE_WIDTH = 200;
      private static final EntityDataAccessor<Component> DATA_TEXT_ID = SynchedEntityData.defineId(Display.TextDisplay.class, EntityDataSerializers.COMPONENT);
      private static final EntityDataAccessor<Integer> DATA_LINE_WIDTH_ID = SynchedEntityData.defineId(Display.TextDisplay.class, EntityDataSerializers.INT);
      private static final EntityDataAccessor<Integer> DATA_BACKGROUND_COLOR_ID = SynchedEntityData.defineId(
         Display.TextDisplay.class, EntityDataSerializers.INT
      );
      private static final EntityDataAccessor<Byte> DATA_TEXT_OPACITY_ID = SynchedEntityData.defineId(Display.TextDisplay.class, EntityDataSerializers.BYTE);
      private static final EntityDataAccessor<Byte> DATA_STYLE_FLAGS_ID = SynchedEntityData.defineId(Display.TextDisplay.class, EntityDataSerializers.BYTE);
      private static final IntSet TEXT_RENDER_STATE_IDS = IntSet.of(
         DATA_TEXT_ID.id(), DATA_LINE_WIDTH_ID.id(), DATA_BACKGROUND_COLOR_ID.id(), DATA_TEXT_OPACITY_ID.id(), DATA_STYLE_FLAGS_ID.id()
      );
      @Nullable
      private Display.TextDisplay.CachedInfo clientDisplayCache;
      @Nullable
      private Display.TextDisplay.TextRenderState textRenderState;

      public TextDisplay(EntityType<?> $$0, Level $$1) {
         super($$0, $$1);
      }

      @Override
      protected void defineSynchedData(SynchedEntityData.Builder $$0) {
         super.defineSynchedData($$0);
         $$0.define(DATA_TEXT_ID, Component.empty());
         $$0.define(DATA_LINE_WIDTH_ID, 200);
         $$0.define(DATA_BACKGROUND_COLOR_ID, 1073741824);
         $$0.define(DATA_TEXT_OPACITY_ID, (byte)-1);
         $$0.define(DATA_STYLE_FLAGS_ID, (byte)0);
      }

      @Override
      public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
         super.onSyncedDataUpdated($$0);
         if (TEXT_RENDER_STATE_IDS.contains($$0.id())) {
            this.updateRenderState = true;
         }
      }

      private Component getText() {
         return this.entityData.get(DATA_TEXT_ID);
      }

      private void setText(Component $$0) {
         this.entityData.set(DATA_TEXT_ID, $$0);
      }

      private int getLineWidth() {
         return this.entityData.get(DATA_LINE_WIDTH_ID);
      }

      private void setLineWidth(int $$0) {
         this.entityData.set(DATA_LINE_WIDTH_ID, $$0);
      }

      private byte getTextOpacity() {
         return this.entityData.get(DATA_TEXT_OPACITY_ID);
      }

      private void setTextOpacity(byte $$0) {
         this.entityData.set(DATA_TEXT_OPACITY_ID, $$0);
      }

      private int getBackgroundColor() {
         return this.entityData.get(DATA_BACKGROUND_COLOR_ID);
      }

      private void setBackgroundColor(int $$0) {
         this.entityData.set(DATA_BACKGROUND_COLOR_ID, $$0);
      }

      private byte getFlags() {
         return this.entityData.get(DATA_STYLE_FLAGS_ID);
      }

      private void setFlags(byte $$0) {
         this.entityData.set(DATA_STYLE_FLAGS_ID, $$0);
      }

      private static byte loadFlag(byte $$0, ValueInput $$1, String $$2, byte $$3) {
         return $$1.getBooleanOr($$2, false) ? (byte)($$0 | $$3) : $$0;
      }

      @Override
      protected void readAdditionalSaveData(ValueInput $$0) {
         super.readAdditionalSaveData($$0);
         this.setLineWidth($$0.getIntOr("line_width", 200));
         this.setTextOpacity($$0.getByteOr("text_opacity", (byte)-1));
         this.setBackgroundColor($$0.getIntOr("background", 1073741824));
         byte $$1 = loadFlag((byte)0, $$0, "shadow", (byte)1);
         $$1 = loadFlag($$1, $$0, "see_through", (byte)2);
         $$1 = loadFlag($$1, $$0, "default_background", (byte)4);
         Optional<Display.TextDisplay.Align> $$2 = $$0.read("alignment", Display.TextDisplay.Align.CODEC);
         if ($$2.isPresent()) {
            $$1 = switch(((Display.TextDisplay.Align)$$2.get()).ordinal()) {
               case 0 -> $$1;
               case 1 -> (byte)($$1 | 8);
               case 2 -> (byte)($$1 | 16);
               default -> throw new MatchException(null, null);
            };
         }

         this.setFlags($$1);
         Optional<Component> $$3 = $$0.read("text", ComponentSerialization.CODEC);
         if ($$3.isPresent()) {
            try {
               Level $$5 = this.level();
               if ($$5 instanceof ServerLevel $$4) {
                  CommandSourceStack $$5x = this.createCommandSourceStackForNameResolution($$4).withPermission(2);
                  Component $$6 = ComponentUtils.updateForEntity($$5x, (Component)$$3.get(), this, 0);
                  this.setText($$6);
               } else {
                  this.setText(Component.empty());
               }
            } catch (Exception var8) {
               Display.LOGGER.warn("Failed to parse display entity text {}", $$3, var8);
            }
         }
      }

      private static void storeFlag(byte $$0, ValueOutput $$1, String $$2, byte $$3) {
         $$1.putBoolean($$2, ($$0 & $$3) != 0);
      }

      @Override
      protected void addAdditionalSaveData(ValueOutput $$0) {
         super.addAdditionalSaveData($$0);
         $$0.store("text", ComponentSerialization.CODEC, this.getText());
         $$0.putInt("line_width", this.getLineWidth());
         $$0.putInt("background", this.getBackgroundColor());
         $$0.putByte("text_opacity", this.getTextOpacity());
         byte $$1 = this.getFlags();
         storeFlag($$1, $$0, "shadow", (byte)1);
         storeFlag($$1, $$0, "see_through", (byte)2);
         storeFlag($$1, $$0, "default_background", (byte)4);
         $$0.store("alignment", Display.TextDisplay.Align.CODEC, getAlign($$1));
      }

      @Override
      protected void updateRenderSubState(boolean $$0, float $$1) {
         if ($$0 && this.textRenderState != null) {
            this.textRenderState = this.createInterpolatedTextRenderState(this.textRenderState, $$1);
         } else {
            this.textRenderState = this.createFreshTextRenderState();
         }

         this.clientDisplayCache = null;
      }

      @Nullable
      public Display.TextDisplay.TextRenderState textRenderState() {
         return this.textRenderState;
      }

      private Display.TextDisplay.TextRenderState createFreshTextRenderState() {
         return new Display.TextDisplay.TextRenderState(
            this.getText(),
            this.getLineWidth(),
            Display.IntInterpolator.constant(this.getTextOpacity()),
            Display.IntInterpolator.constant(this.getBackgroundColor()),
            this.getFlags()
         );
      }

      private Display.TextDisplay.TextRenderState createInterpolatedTextRenderState(Display.TextDisplay.TextRenderState $$0, float $$1) {
         int $$2 = $$0.backgroundColor.get($$1);
         int $$3 = $$0.textOpacity.get($$1);
         return new Display.TextDisplay.TextRenderState(
            this.getText(),
            this.getLineWidth(),
            new Display.LinearIntInterpolator($$3, this.getTextOpacity()),
            new Display.ColorInterpolator($$2, this.getBackgroundColor()),
            this.getFlags()
         );
      }

      public Display.TextDisplay.CachedInfo cacheDisplay(Display.TextDisplay.LineSplitter $$0) {
         if (this.clientDisplayCache == null) {
            if (this.textRenderState != null) {
               this.clientDisplayCache = $$0.split(this.textRenderState.text(), this.textRenderState.lineWidth());
            } else {
               this.clientDisplayCache = new Display.TextDisplay.CachedInfo(List.of(), 0);
            }
         }

         return this.clientDisplayCache;
      }

      public static Display.TextDisplay.Align getAlign(byte $$0) {
         if (($$0 & 8) != 0) {
            return Display.TextDisplay.Align.LEFT;
         } else {
            return ($$0 & 16) != 0 ? Display.TextDisplay.Align.RIGHT : Display.TextDisplay.Align.CENTER;
         }
      }

      public static enum Align implements StringRepresentable {
         CENTER("center"),
         LEFT("left"),
         RIGHT("right");

         public static final Codec<Display.TextDisplay.Align> CODEC = StringRepresentable.fromEnum(Display.TextDisplay.Align::values);
         private final String name;

         private Align(final String param3) {
            this.name = $$0;
         }

         @Override
         public String getSerializedName() {
            return this.name;
         }
      }

      public static record CachedInfo(List<Display.TextDisplay.CachedLine> lines, int width) {
      }

      public static record CachedLine(FormattedCharSequence contents, int width) {
      }

      @FunctionalInterface
      public interface LineSplitter {
         Display.TextDisplay.CachedInfo split(Component var1, int var2);
      }

      public static record TextRenderState(
         Component text, int lineWidth, Display.IntInterpolator textOpacity, Display.IntInterpolator backgroundColor, byte flags
      ) {
         final Display.IntInterpolator textOpacity;
         final Display.IntInterpolator backgroundColor;
      }
   }

   static record TransformationInterpolator(Transformation previous, Transformation current) implements Display.GenericInterpolator<Transformation> {
      public Transformation get(float $$0) {
         return (double)$$0 >= 1.0 ? this.current : this.previous.slerp(this.current, $$0);
      }
   }
}
