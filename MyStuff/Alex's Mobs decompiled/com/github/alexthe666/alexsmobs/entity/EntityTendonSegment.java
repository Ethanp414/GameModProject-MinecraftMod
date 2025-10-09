package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.util.TendonWhipUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class EntityTendonSegment extends Entity {
   private static final EntityDataAccessor<Optional<UUID>> CREATOR_ID = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Integer> FROM_ID = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> TARGET_COUNT = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> CURRENT_TARGET_ID = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Float> PROGRESS = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Boolean> RETRACTING = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> HAS_CLAW = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> HAS_GLINT = SynchedEntityData.m_135353_(EntityTendonSegment.class, EntityDataSerializers.f_135035_);
   private List<Entity> previouslyTouched = new ArrayList<>();
   private boolean hasTouched = false;
   private boolean hasChained = false;
   public float prevProgress = 0.0F;
   public static final float MAX_EXTEND_TIME = 3.0F;

   public EntityTendonSegment(EntityType<?> type, Level level) {
      super(type, level);
   }

   public EntityTendonSegment(SpawnEntity spawnEntity, Level world) {
      this((EntityType<?>)AMEntityRegistry.TENDON_SEGMENT.get(), world);
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(CREATOR_ID, Optional.empty());
      this.f_19804_.m_135372_(FROM_ID, -1);
      this.f_19804_.m_135372_(TARGET_COUNT, 0);
      this.f_19804_.m_135372_(CURRENT_TARGET_ID, -1);
      this.f_19804_.m_135372_(PROGRESS, 0.0F);
      this.f_19804_.m_135372_(DAMAGE, 5.0F);
      this.f_19804_.m_135372_(RETRACTING, false);
      this.f_19804_.m_135372_(HAS_CLAW, true);
      this.f_19804_.m_135372_(HAS_GLINT, false);
   }

   public void m_8119_() {
      float progress = this.getProgress();
      this.prevProgress = progress;
      if (this.f_19797_ < 1) {
         this.onJoinWorld();
      } else if (this.f_19797_ == 1 && !this.m_9236_().f_46443_) {
         this.m_5496_((SoundEvent)AMSoundRegistry.TENDON_WHIP.get(), 1.0F, 0.8F + this.f_19796_.m_188501_() * 0.4F);
      }

      super.m_8119_();
      Entity creator = this.getCreatorEntity();
      Entity current = this.getToEntity();
      if (progress < 3.0F && !this.isRetracting()) {
         this.setProgress(progress + 1.0F);
      }

      if (progress > 0.0F && this.isRetracting()) {
         this.setProgress(progress - 1.0F);
      }

      if (progress == 0.0F && this.isRetracting()) {
         if (this.getFromEntity() instanceof EntityTendonSegment tendonSegment) {
            tendonSegment.setRetracting(true);
            this.updateLastTendon(tendonSegment);
         } else {
            this.updateLastTendon(null);
         }

         this.m_142687_(RemovalReason.DISCARDED);
      }

      if (creator instanceof LivingEntity && current != null) {
         Vec3 target = new Vec3(current.m_20185_(), current.m_20227_(0.4F), current.m_20189_());
         Vec3 lerp = target.m_82546_(this.m_20182_());
         this.m_20256_(lerp.m_82490_(0.5));
         if (!this.m_9236_().f_46443_ && !this.hasTouched && progress >= 3.0F) {
            this.hasTouched = true;
            Entity entity = this.getCreatorEntity();
            if (entity instanceof LivingEntity
               && current != creator
               && current.m_6469_(this.m_269291_().m_269299_(this, (LivingEntity)entity), (float)this.getDamageFor((LivingEntity)creator, (LivingEntity)entity))
               )
             {
               this.m_19970_((LivingEntity)creator, entity);
            }
         }
      }

      Vec3 vector3d = this.m_20184_();
      if (!this.m_9236_().f_46443_ && !this.hasChained) {
         if (this.getTargetsHit() > 3) {
            this.setRetracting(true);
         } else if (creator instanceof LivingEntity && this.getProgress() >= 3.0F) {
            Entity closestValid = null;

            for (Entity entity : this.m_9236_().m_45976_(LivingEntity.class, this.m_20191_().m_82400_(8.0))) {
               if (!entity.equals(creator)
                  && !this.previouslyTouched.contains(entity)
                  && this.isValidTarget((LivingEntity)creator, entity)
                  && this.hasLineOfSight(entity)
                  && (closestValid == null || this.m_20270_(entity) < this.m_20270_(closestValid))) {
                  closestValid = entity;
               }
            }

            if (closestValid != null) {
               this.createChain(closestValid);
               this.hasChained = true;
            } else {
               this.setRetracting(true);
            }
         }
      }

      double d0 = this.m_20185_() + vector3d.f_82479_;
      double d1 = this.m_20186_() + vector3d.f_82480_;
      double d2 = this.m_20189_() + vector3d.f_82481_;
      this.m_20256_(vector3d.m_82490_(0.99F));
      this.m_6034_(d0, d1, d2);
   }

   private boolean isValidTarget(LivingEntity creator, Entity entity) {
      return !creator.m_7307_(entity) && !entity.m_7307_(creator) && entity instanceof Mob
         ? true
         : creator.m_21214_() != null && creator.m_21214_().m_20148_().equals(entity.m_20148_())
            || creator.m_21188_() != null && creator.m_21188_().m_20148_().equals(entity.m_20148_());
   }

   private double getDamageFor(LivingEntity creator, LivingEntity entity) {
      ItemStack stack = creator.m_21120_(InteractionHand.MAIN_HAND).m_150930_((Item)AMItemRegistry.TENDON_WHIP.get())
         ? creator.m_21120_(InteractionHand.MAIN_HAND)
         : creator.m_21120_(InteractionHand.OFF_HAND);
      double dmg = this.getBaseDamage();
      if (stack.m_150930_((Item)AMItemRegistry.TENDON_WHIP.get())) {
         dmg += EnchantmentHelper.m_44833_(stack, entity.m_6336_());
      }

      return dmg;
   }

   private double getDamageForItem(ItemStack itemStack) {
      Multimap<Attribute, AttributeModifier> map = itemStack.m_41638_(EquipmentSlot.MAINHAND);
      if (map.isEmpty()) {
         return 0.0;
      } else {
         double d = 0.0;

         for (AttributeModifier mod : map.get(Attributes.f_22281_)) {
            d += mod.m_22218_();
         }

         return d;
      }
   }

   private boolean hasLineOfSight(Entity entity) {
      if (entity.m_9236_() != this.m_9236_()) {
         return false;
      } else {
         Vec3 vec3 = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
         Vec3 vec31 = new Vec3(entity.m_20185_(), entity.m_20188_(), entity.m_20189_());
         return vec31.m_82554_(vec3) > 128.0
            ? false
            : this.m_9236_().m_45547_(new ClipContext(vec3, vec31, Block.COLLIDER, Fluid.NONE, this)).m_6662_() == Type.MISS;
      }
   }

   private void updateLastTendon(EntityTendonSegment lastTendon) {
      Entity creator = this.getCreatorEntity();
      if (creator == null) {
         creator = this.m_9236_().m_46003_(this.getCreatorEntityUUID());
      }

      if (creator instanceof LivingEntity) {
         TendonWhipUtil.setLastTendon((LivingEntity)creator, lastTendon);
      }
   }

   private void createChain(Entity closestValid) {
      this.f_19804_.m_135381_(HAS_CLAW, false);
      EntityTendonSegment child = (EntityTendonSegment)((EntityType)AMEntityRegistry.TENDON_SEGMENT.get()).m_20615_(this.m_9236_());
      child.previouslyTouched = new ArrayList<>(this.previouslyTouched);
      child.previouslyTouched.add(closestValid);
      child.setCreatorEntityUUID(this.getCreatorEntityUUID());
      child.setFromEntityID(this.m_19879_());
      child.setToEntityID(closestValid.m_19879_());
      child.m_6034_(closestValid.m_20185_(), closestValid.m_20227_(0.4F), closestValid.m_20189_());
      child.setTargetsHit(this.getTargetsHit() + 1);
      this.updateLastTendon(child);
      child.setHasGlint(this.hasGlint());
      this.m_9236_().m_7967_(child);
   }

   private void onJoinWorld() {
      Entity creator = this.getCreatorEntity();
      if (creator == null) {
         creator = this.m_9236_().m_46003_(this.getCreatorEntityUUID());
      }

      Entity prior = this.getFromEntity();
      if (creator instanceof Player player) {
         ItemStack stack = player.m_21120_(InteractionHand.MAIN_HAND).m_150930_((Item)AMItemRegistry.TENDON_WHIP.get())
            ? player.m_21120_(InteractionHand.MAIN_HAND)
            : player.m_21120_(InteractionHand.OFF_HAND);
         if (stack.m_150930_((Item)AMItemRegistry.TENDON_WHIP.get())) {
            this.setHasGlint(stack.m_41790_());
         }

         float dmg = 2.0F;
         if (prior instanceof EntityTendonSegment) {
            dmg = Math.max(((EntityTendonSegment)prior).getBaseDamage() - 1.0F, 2.0F);
         } else {
            dmg = (float)this.getDamageForItem(stack);
         }

         this.f_19804_.m_135381_(DAMAGE, dmg);
      }
   }

   private float getBaseDamage() {
      return (Float)this.f_19804_.m_135370_(DAMAGE);
   }

   public UUID getCreatorEntityUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(CREATOR_ID)).orElse(null);
   }

   public void setCreatorEntityUUID(UUID id) {
      this.f_19804_.m_135381_(CREATOR_ID, Optional.ofNullable(id));
   }

   public Entity getCreatorEntity() {
      UUID uuid = this.getCreatorEntityUUID();
      return uuid != null && !this.m_9236_().f_46443_ ? ((ServerLevel)this.m_9236_()).m_8791_(uuid) : null;
   }

   public int getFromEntityID() {
      return (Integer)this.f_19804_.m_135370_(FROM_ID);
   }

   public void setFromEntityID(int id) {
      this.f_19804_.m_135381_(FROM_ID, id);
   }

   public Entity getFromEntity() {
      return this.getFromEntityID() == -1 ? null : this.m_9236_().m_6815_(this.getFromEntityID());
   }

   public int getToEntityID() {
      return (Integer)this.f_19804_.m_135370_(CURRENT_TARGET_ID);
   }

   public void setToEntityID(int id) {
      this.f_19804_.m_135381_(CURRENT_TARGET_ID, id);
   }

   public Entity getToEntity() {
      return this.getToEntityID() == -1 ? null : this.m_9236_().m_6815_(this.getToEntityID());
   }

   public int getTargetsHit() {
      return (Integer)this.f_19804_.m_135370_(TARGET_COUNT);
   }

   public void setTargetsHit(int i) {
      this.f_19804_.m_135381_(TARGET_COUNT, i);
   }

   public float getProgress() {
      return (Float)this.f_19804_.m_135370_(PROGRESS);
   }

   public void setProgress(float progress) {
      this.f_19804_.m_135381_(PROGRESS, progress);
   }

   public boolean isRetracting() {
      return (Boolean)this.f_19804_.m_135370_(RETRACTING);
   }

   public void setRetracting(boolean retract) {
      this.f_19804_.m_135381_(RETRACTING, retract);
   }

   public boolean hasGlint() {
      return (Boolean)this.f_19804_.m_135370_(HAS_GLINT);
   }

   public void setHasGlint(boolean glint) {
      this.f_19804_.m_135381_(HAS_GLINT, glint);
   }

   public boolean hasClaw() {
      return (Boolean)this.f_19804_.m_135370_(HAS_CLAW);
   }

   protected void m_7378_(CompoundTag p_20052_) {
   }

   protected void m_7380_(CompoundTag p_20139_) {
   }

   public boolean isCreator(Entity mob) {
      return this.getCreatorEntityUUID() != null && mob.m_20148_().equals(this.getCreatorEntityUUID());
   }
}
