package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.event.ServerEvents;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.antlr.v4.runtime.misc.Triple;

public class EntityVoidPortal extends Entity {
   protected static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.m_135353_(EntityVoidPortal.class, EntityDataSerializers.f_135040_);
   protected static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.m_135353_(EntityVoidPortal.class, EntityDataSerializers.f_135028_);
   protected static final EntityDataAccessor<Boolean> SHATTERED = SynchedEntityData.m_135353_(EntityVoidPortal.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Optional<BlockPos>> DESTINATION = SynchedEntityData.m_135353_(
      EntityVoidPortal.class, EntityDataSerializers.f_135039_
   );
   private static final EntityDataAccessor<Optional<UUID>> SISTER_UUID = SynchedEntityData.m_135353_(EntityVoidPortal.class, EntityDataSerializers.f_135041_);
   public ResourceKey<Level> exitDimension;
   private boolean madeOpenNoise = false;
   private boolean madeCloseNoise = false;
   private boolean isDummy = false;
   private boolean hasClearedObstructions;

   public EntityVoidPortal(EntityType<?> entityTypeIn, Level worldIn) {
      super(entityTypeIn, worldIn);
   }

   public EntityVoidPortal(SpawnEntity spawnEntity, Level world) {
      this((EntityType<?>)AMEntityRegistry.VOID_PORTAL.get(), world);
   }

   public EntityVoidPortal(Level world, ItemDimensionalCarver item) {
      this((EntityType<?>)AMEntityRegistry.VOID_PORTAL.get(), world);
      if (item == AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER.get()) {
         this.setShattered(true);
         this.setLifespan(2000);
      } else {
         this.setShattered(false);
         this.setLifespan(1200);
      }
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.f_19797_ == 1 && this.getLifespan() == 0) {
         this.setLifespan(2000);
      }

      if (!this.madeOpenNoise) {
         this.m_146850_(GameEvent.f_157810_);
         this.m_5496_((SoundEvent)AMSoundRegistry.VOID_PORTAL_OPEN.get(), 1.0F, 1.0F + this.f_19796_.m_188501_() * 0.2F);
         this.madeOpenNoise = true;
      }

      Direction direction2 = this.getAttachmentFacing().m_122424_();
      float minX = -0.15F;
      float minY = -0.15F;
      float minZ = -0.15F;
      float maxX = 0.15F;
      float maxY = 0.15F;
      float maxZ = 0.15F;
      switch (direction2) {
         case NORTH:
         case SOUTH:
            minX = -1.5F;
            maxX = 1.5F;
            minY = -1.5F;
            maxY = 1.5F;
            break;
         case EAST:
         case WEST:
            minZ = -1.5F;
            maxZ = 1.5F;
            minY = -1.5F;
            maxY = 1.5F;
            break;
         case UP:
         case DOWN:
            minX = -1.5F;
            maxX = 1.5F;
            minZ = -1.5F;
            maxZ = 1.5F;
      }

      AABB bb = new AABB(
         this.m_20185_() + minX, this.m_20186_() + minY, this.m_20189_() + minZ, this.m_20185_() + maxX, this.m_20186_() + maxY, this.m_20189_() + maxZ
      );
      this.m_20011_(bb);
      if (this.m_9236_().f_46443_ && this.f_19796_.m_188501_() < 0.5F && Math.min(this.f_19797_, this.getLifespan()) >= 20) {
         double particleX = this.m_20191_().f_82288_ + this.f_19796_.m_188501_() * (this.m_20191_().f_82291_ - this.m_20191_().f_82288_);
         double particleY = this.m_20191_().f_82289_ + this.f_19796_.m_188501_() * (this.m_20191_().f_82292_ - this.m_20191_().f_82289_);
         double particleZ = this.m_20191_().f_82290_ + this.f_19796_.m_188501_() * (this.m_20191_().f_82293_ - this.m_20191_().f_82290_);
         this.m_9236_()
            .m_7106_(
               (ParticleOptions)AMParticleRegistry.WORM_PORTAL.get(),
               particleX,
               particleY,
               particleZ,
               0.1 * this.f_19796_.m_188583_(),
               0.1 * this.f_19796_.m_188583_(),
               0.1 * this.f_19796_.m_188583_()
            );
      }

      List<Entity> entities = new ArrayList<>();
      entities.addAll(this.m_9236_().m_45933_(this, bb.m_82406_(0.2F)));
      entities.addAll(this.m_9236_().m_45976_(EntityVoidWorm.class, bb.m_82400_(1.5)));
      if (!this.m_9236_().f_46443_) {
         MinecraftServer server = this.m_9236_().m_7654_();
         if (this.getDestination() != null && this.getLifespan() > 20 && this.f_19797_ > 20) {
            BlockPos offsetPos = this.getDestination().m_5484_(this.getAttachmentFacing().m_122424_(), 2);

            for (Entity e : entities) {
               if (!e.m_20092_()
                  && !e.m_6144_()
                  && !(e instanceof EntityVoidPortal)
                  && e.getParts() == null
                  && !(e instanceof PartEntity)
                  && !e.m_6095_().m_204039_(AMTagRegistry.VOID_PORTAL_IGNORES)) {
                  if (e instanceof EntityVoidWormPart) {
                     if (this.getLifespan() < 22) {
                        this.setLifespan(this.getLifespan() + 1);
                     }
                  } else if (e instanceof EntityVoidWorm) {
                     ((EntityVoidWorm)e).teleportTo(Vec3.m_82512_(this.getDestination()));
                     e.m_20091_();
                     ((EntityVoidWorm)e).resetPortalLogic();
                  } else {
                     boolean flag = true;
                     if (this.exitDimension != null) {
                        ServerLevel dimWorld = server.m_129880_(this.exitDimension);
                        if (dimWorld != null && this.m_9236_().m_46472_() != this.exitDimension) {
                           this.teleportEntityFromDimension(e, dimWorld, offsetPos, true);
                           flag = false;
                        }
                     }

                     if (flag) {
                        e.m_20324_(offsetPos.m_123341_() + 0.5F, offsetPos.m_123342_() + 0.5F, offsetPos.m_123343_() + 0.5F);
                        e.m_20091_();
                     }
                  }
               }
            }
         }
      }

      this.setLifespan(this.getLifespan() - 1);
      if (this.getLifespan() <= 20 && !this.madeCloseNoise) {
         this.m_146850_(GameEvent.f_157810_);
         this.m_5496_((SoundEvent)AMSoundRegistry.VOID_PORTAL_CLOSE.get(), 1.0F, 1.0F + this.f_19796_.m_188501_() * 0.2F);
         this.madeCloseNoise = true;
      }

      if (this.getLifespan() <= 0) {
         this.m_142687_(RemovalReason.DISCARDED);
      }

      if (this.f_19797_ > 1) {
         this.clearObstructions();
      }
   }

   private void teleportEntityFromDimension(Entity entity, ServerLevel endpointWorld, BlockPos endpoint, boolean b) {
      if (entity instanceof ServerPlayer) {
         ServerEvents.teleportPlayers.add(new Triple((ServerPlayer)entity, endpointWorld, endpoint));
         if (this.getSisterId() == null) {
            this.createAndSetSister(endpointWorld, Direction.DOWN);
         }
      } else {
         entity.m_19877_();
         entity.m_284535_(endpointWorld);
         Entity teleportedEntity = entity.m_6095_().m_20615_(endpointWorld);
         if (teleportedEntity != null) {
            teleportedEntity.m_20361_(entity);
            teleportedEntity.m_7678_(endpoint.m_123341_() + 0.5, endpoint.m_123342_() + 0.5, endpoint.m_123343_() + 0.5, entity.m_146908_(), entity.m_146909_());
            teleportedEntity.m_5616_(entity.m_6080_());
            teleportedEntity.m_20091_();
            endpointWorld.m_7967_(teleportedEntity);
         }

         entity.m_142687_(RemovalReason.DISCARDED);
      }
   }

   public void clearObstructions() {
      if (!this.hasClearedObstructions && this.isShattered() && this.getDestination() != null) {
         this.hasClearedObstructions = true;

         for (int i = -1; i <= -1; i++) {
            for (int j = -1; j <= -1; j++) {
               for (int k = -1; k <= -1; k++) {
                  BlockPos toAir = this.getDestination().m_7918_(i, j, k);
                  this.m_9236_().m_46961_(toAir, true);
               }
            }
         }
      }
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.f_19804_.m_135370_(ATTACHED_FACE);
   }

   public void setAttachmentFacing(Direction facing) {
      this.f_19804_.m_135381_(ATTACHED_FACE, facing);
   }

   public int getLifespan() {
      return (Integer)this.f_19804_.m_135370_(LIFESPAN);
   }

   public void setLifespan(int i) {
      this.f_19804_.m_135381_(LIFESPAN, i);
   }

   public boolean isShattered() {
      return (Boolean)this.f_19804_.m_135370_(SHATTERED);
   }

   public void setShattered(boolean set) {
      this.f_19804_.m_135381_(SHATTERED, set);
   }

   public BlockPos getDestination() {
      return (BlockPos)((Optional)this.f_19804_.m_135370_(DESTINATION)).orElse(null);
   }

   public void setDestination(BlockPos destination) {
      this.f_19804_.m_135381_(DESTINATION, Optional.ofNullable(destination));
      if (this.getSisterId() == null && (this.exitDimension == null || this.exitDimension == this.m_9236_().m_46472_())) {
         this.createAndSetSister(this.m_9236_(), null);
      }
   }

   public void createAndSetSister(Level world, Direction dir) {
      EntityVoidPortal portal = (EntityVoidPortal)((EntityType)AMEntityRegistry.VOID_PORTAL.get()).m_20615_(world);
      portal.setAttachmentFacing(dir != null ? dir : this.getAttachmentFacing().m_122424_());
      BlockPos safeDestination = this.getDestination();
      portal.m_20324_(safeDestination.m_123341_() + 0.5F, safeDestination.m_123342_() + 0.5F, safeDestination.m_123343_() + 0.5F);
      portal.link(this);
      portal.exitDimension = this.m_9236_().m_46472_();
      world.m_7967_(portal);
      portal.setShattered(this.isShattered());
   }

   public void setDestination(BlockPos destination, Direction dir) {
      this.f_19804_.m_135381_(DESTINATION, Optional.ofNullable(destination));
      if (this.getSisterId() == null && (this.exitDimension == null || this.exitDimension == this.m_9236_().m_46472_())) {
         this.createAndSetSister(this.m_9236_(), dir);
      }
   }

   public void link(EntityVoidPortal portal) {
      this.setSisterId(portal.m_20148_());
      portal.setSisterId(this.m_20148_());
      portal.setLifespan(this.getLifespan());
      this.setDestination(portal.m_20183_());
      portal.setDestination(this.m_20183_());
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(ATTACHED_FACE, Direction.DOWN);
      this.f_19804_.m_135372_(LIFESPAN, 300);
      this.f_19804_.m_135372_(SHATTERED, false);
      this.f_19804_.m_135372_(SISTER_UUID, Optional.empty());
      this.f_19804_.m_135372_(DESTINATION, Optional.empty());
   }

   protected void m_7378_(CompoundTag compound) {
      this.f_19804_.m_135381_(ATTACHED_FACE, Direction.m_122376_(compound.m_128445_("AttachFace")));
      this.setLifespan(compound.m_128451_("Lifespan"));
      if (compound.m_128441_("Shattered")) {
         this.setShattered(compound.m_128471_("Shattered"));
      }

      if (compound.m_128441_("DX")) {
         int i = compound.m_128451_("DX");
         int j = compound.m_128451_("DY");
         int k = compound.m_128451_("DZ");
         this.f_19804_.m_135381_(DESTINATION, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.f_19804_.m_135381_(DESTINATION, Optional.empty());
      }

      if (compound.m_128403_("SisterUUID")) {
         this.setSisterId(compound.m_128342_("SisterUUID"));
      }

      if (compound.m_128441_("ExitDimension")) {
         this.exitDimension = Level.f_46427_
            .parse(NbtOps.f_128958_, compound.m_128423_("ExitDimension"))
            .resultOrPartial(AlexsMobs.LOGGER::error)
            .orElse(Level.f_46428_);
      }
   }

   protected void m_7380_(CompoundTag compound) {
      compound.m_128344_("AttachFace", (byte)((Direction)this.f_19804_.m_135370_(ATTACHED_FACE)).m_122411_());
      compound.m_128405_("Lifespan", this.getLifespan());
      compound.m_128379_("Shattered", this.isShattered());
      BlockPos blockpos = this.getDestination();
      if (blockpos != null) {
         compound.m_128405_("DX", blockpos.m_123341_());
         compound.m_128405_("DY", blockpos.m_123342_());
         compound.m_128405_("DZ", blockpos.m_123343_());
      }

      if (this.getSisterId() != null) {
         compound.m_128362_("SisterUUID", this.getSisterId());
      }

      if (this.exitDimension != null) {
         ResourceLocation.f_135803_
            .encodeStart(NbtOps.f_128958_, this.exitDimension.m_135782_())
            .resultOrPartial(AlexsMobs.LOGGER::error)
            .ifPresent(p_241148_1_ -> compound.m_128365_("ExitDimension", p_241148_1_));
      }
   }

   public Entity getSister() {
      UUID id = this.getSisterId();
      return id != null && !this.m_9236_().f_46443_ ? ((ServerLevel)this.m_9236_()).m_8791_(id) : null;
   }

   @Nullable
   public UUID getSisterId() {
      return (UUID)((Optional)this.f_19804_.m_135370_(SISTER_UUID)).orElse(null);
   }

   public void setSisterId(@Nullable UUID uniqueId) {
      this.f_19804_.m_135381_(SISTER_UUID, Optional.ofNullable(uniqueId));
   }
}
