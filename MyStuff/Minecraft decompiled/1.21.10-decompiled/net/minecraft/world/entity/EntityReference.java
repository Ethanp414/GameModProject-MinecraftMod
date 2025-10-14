package net.minecraft.world.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.UUIDLookup;
import net.minecraft.world.level.entity.UniquelyIdentifyable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class EntityReference<StoredEntityType extends UniquelyIdentifyable> {
   private static final Codec<? extends EntityReference<?>> CODEC = UUIDUtil.CODEC.xmap(EntityReference::new, EntityReference::getUUID);
   private static final StreamCodec<ByteBuf, ? extends EntityReference<?>> STREAM_CODEC = UUIDUtil.STREAM_CODEC
      .map(EntityReference::new, EntityReference::getUUID);
   private Either<UUID, StoredEntityType> entity;

   public static <Type extends UniquelyIdentifyable> Codec<EntityReference<Type>> codec() {
      return CODEC;
   }

   public static <Type extends UniquelyIdentifyable> StreamCodec<ByteBuf, EntityReference<Type>> streamCodec() {
      return STREAM_CODEC;
   }

   private EntityReference(StoredEntityType $$0) {
      this.entity = Either.right($$0);
   }

   private EntityReference(UUID $$0) {
      this.entity = Either.left($$0);
   }

   @Nullable
   public static <T extends UniquelyIdentifyable> EntityReference<T> of(@Nullable T $$0) {
      return $$0 != null ? new EntityReference<>($$0) : null;
   }

   public static <T extends UniquelyIdentifyable> EntityReference<T> of(UUID $$0) {
      return new EntityReference<>($$0);
   }

   public UUID getUUID() {
      return this.entity.map($$0 -> $$0, UniquelyIdentifyable::getUUID);
   }

   @Nullable
   public StoredEntityType getEntity(UUIDLookup<? extends UniquelyIdentifyable> $$0, Class<StoredEntityType> $$1) {
      Optional<StoredEntityType> $$2 = this.entity.right();
      if ($$2.isPresent()) {
         StoredEntityType $$3 = (StoredEntityType)$$2.get();
         if (!$$3.isRemoved()) {
            return $$3;
         }

         this.entity = Either.left($$3.getUUID());
      }

      Optional<UUID> $$4 = this.entity.left();
      if ($$4.isPresent()) {
         StoredEntityType $$5 = this.resolve($$0.lookup((UUID)$$4.get()), $$1);
         if ($$5 != null && !$$5.isRemoved()) {
            this.entity = Either.right($$5);
            return $$5;
         }
      }

      return null;
   }

   @Nullable
   public StoredEntityType getEntity(Level $$0, Class<StoredEntityType> $$1) {
      return (StoredEntityType)(Player.class.isAssignableFrom($$1)
         ? this.getEntity($$0::getPlayerInAnyDimension, $$1)
         : this.getEntity($$0::getEntityInAnyDimension, $$1));
   }

   @Nullable
   private StoredEntityType resolve(@Nullable UniquelyIdentifyable $$0, Class<StoredEntityType> $$1) {
      return (StoredEntityType)($$0 != null && $$1.isAssignableFrom($$0.getClass()) ? $$1.cast($$0) : null);
   }

   public boolean matches(StoredEntityType $$0) {
      return this.getUUID().equals($$0.getUUID());
   }

   public void store(ValueOutput $$0, String $$1) {
      $$0.store($$1, UUIDUtil.CODEC, this.getUUID());
   }

   public static void store(@Nullable EntityReference<?> $$0, ValueOutput $$1, String $$2) {
      if ($$0 != null) {
         $$0.store($$1, $$2);
      }
   }

   @Nullable
   public static <StoredEntityType extends UniquelyIdentifyable> StoredEntityType get(
      @Nullable EntityReference<StoredEntityType> $$0, Level $$1, Class<StoredEntityType> $$2
   ) {
      return $$0 != null ? $$0.getEntity($$1, $$2) : null;
   }

   @Nullable
   public static Entity getEntity(@Nullable EntityReference<Entity> $$0, Level $$1) {
      return get($$0, $$1, Entity.class);
   }

   @Nullable
   public static LivingEntity getLivingEntity(@Nullable EntityReference<LivingEntity> $$0, Level $$1) {
      return get($$0, $$1, LivingEntity.class);
   }

   @Nullable
   public static Player getPlayer(@Nullable EntityReference<Player> $$0, Level $$1) {
      return get($$0, $$1, Player.class);
   }

   @Nullable
   public static <StoredEntityType extends UniquelyIdentifyable> EntityReference<StoredEntityType> read(ValueInput $$0, String $$1) {
      return (EntityReference<StoredEntityType>)$$0.read($$1, codec()).orElse(null);
   }

   @Nullable
   public static <StoredEntityType extends UniquelyIdentifyable> EntityReference<StoredEntityType> readWithOldOwnerConversion(
      ValueInput $$0, String $$1, Level $$2
   ) {
      Optional<UUID> $$3 = $$0.read($$1, UUIDUtil.CODEC);
      return $$3.isPresent()
         ? of((UUID)$$3.get())
         : (EntityReference)$$0.getString($$1)
            .map($$1x -> OldUsersConverter.convertMobOwnerIfNecessary($$2.getServer(), $$1x))
            .map(EntityReference::new)
            .orElse(null);
   }

   public boolean equals(Object $$0) {
      if ($$0 == this) {
         return true;
      } else {
         if ($$0 instanceof EntityReference $$1 && this.getUUID().equals($$1.getUUID())) {
            return true;
         }

         return false;
      }
   }

   public int hashCode() {
      return this.getUUID().hashCode();
   }
}
