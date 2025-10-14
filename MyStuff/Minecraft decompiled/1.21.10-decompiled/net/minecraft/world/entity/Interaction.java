package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Interaction extends Entity implements Attackable, Targeting {
   private static final EntityDataAccessor<Float> DATA_WIDTH_ID = SynchedEntityData.defineId(Interaction.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_HEIGHT_ID = SynchedEntityData.defineId(Interaction.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DATA_RESPONSE_ID = SynchedEntityData.defineId(Interaction.class, EntityDataSerializers.BOOLEAN);
   private static final String TAG_WIDTH = "width";
   private static final String TAG_HEIGHT = "height";
   private static final String TAG_ATTACK = "attack";
   private static final String TAG_INTERACTION = "interaction";
   private static final String TAG_RESPONSE = "response";
   private static final float DEFAULT_WIDTH = 1.0F;
   private static final float DEFAULT_HEIGHT = 1.0F;
   private static final boolean DEFAULT_RESPONSE = false;
   @Nullable
   private Interaction.PlayerAction attack;
   @Nullable
   private Interaction.PlayerAction interaction;

   public Interaction(EntityType<?> $$0, Level $$1) {
      super($$0, $$1);
      this.noPhysics = true;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder $$0) {
      $$0.define(DATA_WIDTH_ID, 1.0F);
      $$0.define(DATA_HEIGHT_ID, 1.0F);
      $$0.define(DATA_RESPONSE_ID, false);
   }

   @Override
   protected void readAdditionalSaveData(ValueInput $$0) {
      this.setWidth($$0.getFloatOr("width", 1.0F));
      this.setHeight($$0.getFloatOr("height", 1.0F));
      this.attack = (Interaction.PlayerAction)$$0.read("attack", Interaction.PlayerAction.CODEC).orElse(null);
      this.interaction = (Interaction.PlayerAction)$$0.read("interaction", Interaction.PlayerAction.CODEC).orElse(null);
      this.setResponse($$0.getBooleanOr("response", false));
      this.setBoundingBox(this.makeBoundingBox());
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput $$0) {
      $$0.putFloat("width", this.getWidth());
      $$0.putFloat("height", this.getHeight());
      $$0.storeNullable("attack", Interaction.PlayerAction.CODEC, this.attack);
      $$0.storeNullable("interaction", Interaction.PlayerAction.CODEC, this.interaction);
      $$0.putBoolean("response", this.getResponse());
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
      super.onSyncedDataUpdated($$0);
      if (DATA_HEIGHT_ID.equals($$0) || DATA_WIDTH_ID.equals($$0)) {
         this.refreshDimensions();
      }
   }

   @Override
   public boolean canBeHitByProjectile() {
      return false;
   }

   @Override
   public boolean isPickable() {
      return true;
   }

   @Override
   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   @Override
   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   @Override
   public boolean skipAttackInteraction(Entity $$0) {
      if ($$0 instanceof Player $$1) {
         this.attack = new Interaction.PlayerAction($$1.getUUID(), this.level().getGameTime());
         if ($$1 instanceof ServerPlayer $$2) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger($$2, this, $$1.damageSources().generic(), 1.0F, 1.0F, false);
         }

         return !this.getResponse();
      } else {
         return false;
      }
   }

   @Override
   public final boolean hurtServer(ServerLevel $$0, DamageSource $$1, float $$2) {
      return false;
   }

   @Override
   public InteractionResult interact(Player $$0, InteractionHand $$1) {
      if (this.level().isClientSide()) {
         return this.getResponse() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      } else {
         this.interaction = new Interaction.PlayerAction($$0.getUUID(), this.level().getGameTime());
         return InteractionResult.CONSUME;
      }
   }

   @Override
   public void tick() {
   }

   @Nullable
   @Override
   public LivingEntity getLastAttacker() {
      return this.attack != null ? this.level().getPlayerByUUID(this.attack.player()) : null;
   }

   @Nullable
   @Override
   public LivingEntity getTarget() {
      return this.interaction != null ? this.level().getPlayerByUUID(this.interaction.player()) : null;
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

   private float getHeight() {
      return this.entityData.get(DATA_HEIGHT_ID);
   }

   private void setResponse(boolean $$0) {
      this.entityData.set(DATA_RESPONSE_ID, $$0);
   }

   private boolean getResponse() {
      return this.entityData.get(DATA_RESPONSE_ID);
   }

   private EntityDimensions getDimensions() {
      return EntityDimensions.scalable(this.getWidth(), this.getHeight());
   }

   @Override
   public EntityDimensions getDimensions(Pose $$0) {
      return this.getDimensions();
   }

   @Override
   protected AABB makeBoundingBox(Vec3 $$0) {
      return this.getDimensions().makeBoundingBox($$0);
   }

   static record PlayerAction(UUID player, long timestamp) {
      public static final Codec<Interaction.PlayerAction> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  UUIDUtil.CODEC.fieldOf("player").forGetter(Interaction.PlayerAction::player),
                  Codec.LONG.fieldOf("timestamp").forGetter(Interaction.PlayerAction::timestamp)
               )
               .apply($$0, Interaction.PlayerAction::new)
      );
   }
}
