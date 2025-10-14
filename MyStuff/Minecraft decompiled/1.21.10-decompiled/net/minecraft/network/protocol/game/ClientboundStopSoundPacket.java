package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class ClientboundStopSoundPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundStopSoundPacket> STREAM_CODEC = Packet.codec(
      ClientboundStopSoundPacket::write, ClientboundStopSoundPacket::new
   );
   private static final int HAS_SOURCE = 1;
   private static final int HAS_SOUND = 2;
   @Nullable
   private final ResourceLocation name;
   @Nullable
   private final SoundSource source;

   public ClientboundStopSoundPacket(@Nullable ResourceLocation $$0, @Nullable SoundSource $$1) {
      this.name = $$0;
      this.source = $$1;
   }

   private ClientboundStopSoundPacket(FriendlyByteBuf $$0) {
      int $$1 = $$0.readByte();
      if (($$1 & 1) > 0) {
         this.source = $$0.readEnum(SoundSource.class);
      } else {
         this.source = null;
      }

      if (($$1 & 2) > 0) {
         this.name = $$0.readResourceLocation();
      } else {
         this.name = null;
      }
   }

   private void write(FriendlyByteBuf $$0) {
      if (this.source != null) {
         if (this.name != null) {
            $$0.writeByte(3);
            $$0.writeEnum(this.source);
            $$0.writeResourceLocation(this.name);
         } else {
            $$0.writeByte(1);
            $$0.writeEnum(this.source);
         }
      } else if (this.name != null) {
         $$0.writeByte(2);
         $$0.writeResourceLocation(this.name);
      } else {
         $$0.writeByte(0);
      }
   }

   @Override
   public PacketType<ClientboundStopSoundPacket> type() {
      return GamePacketTypes.CLIENTBOUND_STOP_SOUND;
   }

   public void handle(ClientGamePacketListener $$0) {
      $$0.handleStopSoundEvent(this);
   }

   @Nullable
   public ResourceLocation getName() {
      return this.name;
   }

   @Nullable
   public SoundSource getSource() {
      return this.source;
   }
}
