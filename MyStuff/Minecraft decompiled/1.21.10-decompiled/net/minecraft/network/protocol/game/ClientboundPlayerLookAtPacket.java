package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundPlayerLookAtPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerLookAtPacket> STREAM_CODEC = Packet.codec(
      ClientboundPlayerLookAtPacket::write, ClientboundPlayerLookAtPacket::new
   );
   private final double x;
   private final double y;
   private final double z;
   private final int entity;
   private final EntityAnchorArgument.Anchor fromAnchor;
   private final EntityAnchorArgument.Anchor toAnchor;
   private final boolean atEntity;

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor $$0, double $$1, double $$2, double $$3) {
      this.fromAnchor = $$0;
      this.x = $$1;
      this.y = $$2;
      this.z = $$3;
      this.entity = 0;
      this.atEntity = false;
      this.toAnchor = null;
   }

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor $$0, Entity $$1, EntityAnchorArgument.Anchor $$2) {
      this.fromAnchor = $$0;
      this.entity = $$1.getId();
      this.toAnchor = $$2;
      Vec3 $$3 = $$2.apply($$1);
      this.x = $$3.x;
      this.y = $$3.y;
      this.z = $$3.z;
      this.atEntity = true;
   }

   private ClientboundPlayerLookAtPacket(FriendlyByteBuf $$0) {
      this.fromAnchor = $$0.readEnum(EntityAnchorArgument.Anchor.class);
      this.x = $$0.readDouble();
      this.y = $$0.readDouble();
      this.z = $$0.readDouble();
      this.atEntity = $$0.readBoolean();
      if (this.atEntity) {
         this.entity = $$0.readVarInt();
         this.toAnchor = $$0.readEnum(EntityAnchorArgument.Anchor.class);
      } else {
         this.entity = 0;
         this.toAnchor = null;
      }
   }

   private void write(FriendlyByteBuf $$0) {
      $$0.writeEnum(this.fromAnchor);
      $$0.writeDouble(this.x);
      $$0.writeDouble(this.y);
      $$0.writeDouble(this.z);
      $$0.writeBoolean(this.atEntity);
      if (this.atEntity) {
         $$0.writeVarInt(this.entity);
         $$0.writeEnum(this.toAnchor);
      }
   }

   @Override
   public PacketType<ClientboundPlayerLookAtPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_LOOK_AT;
   }

   public void handle(ClientGamePacketListener $$0) {
      $$0.handleLookAt(this);
   }

   public EntityAnchorArgument.Anchor getFromAnchor() {
      return this.fromAnchor;
   }

   @Nullable
   public Vec3 getPosition(Level $$0) {
      if (this.atEntity) {
         Entity $$1 = $$0.getEntity(this.entity);
         return $$1 == null ? new Vec3(this.x, this.y, this.z) : this.toAnchor.apply($$1);
      } else {
         return new Vec3(this.x, this.y, this.z);
      }
   }
}
