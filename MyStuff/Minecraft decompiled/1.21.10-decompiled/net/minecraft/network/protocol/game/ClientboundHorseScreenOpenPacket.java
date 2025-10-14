package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundHorseScreenOpenPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundHorseScreenOpenPacket> STREAM_CODEC = Packet.codec(
      ClientboundHorseScreenOpenPacket::write, ClientboundHorseScreenOpenPacket::new
   );
   private final int containerId;
   private final int inventoryColumns;
   private final int entityId;

   public ClientboundHorseScreenOpenPacket(int $$0, int $$1, int $$2) {
      this.containerId = $$0;
      this.inventoryColumns = $$1;
      this.entityId = $$2;
   }

   private ClientboundHorseScreenOpenPacket(FriendlyByteBuf $$0) {
      this.containerId = $$0.readContainerId();
      this.inventoryColumns = $$0.readVarInt();
      this.entityId = $$0.readInt();
   }

   private void write(FriendlyByteBuf $$0) {
      $$0.writeContainerId(this.containerId);
      $$0.writeVarInt(this.inventoryColumns);
      $$0.writeInt(this.entityId);
   }

   @Override
   public PacketType<ClientboundHorseScreenOpenPacket> type() {
      return GamePacketTypes.CLIENTBOUND_HORSE_SCREEN_OPEN;
   }

   public void handle(ClientGamePacketListener $$0) {
      $$0.handleHorseScreenOpen(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getInventoryColumns() {
      return this.inventoryColumns;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
