package net.minecraft.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.ResourceLocation;

public interface CustomPacketPayload {
   CustomPacketPayload.Type<? extends CustomPacketPayload> type();

   static <B extends ByteBuf, T extends CustomPacketPayload> StreamCodec<B, T> codec(StreamMemberEncoder<B, T> $$0, StreamDecoder<B, T> $$1) {
      return StreamCodec.ofMember($$0, $$1);
   }

   static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String $$0) {
      return new CustomPacketPayload.Type<>(ResourceLocation.withDefaultNamespace($$0));
   }

   static <B extends FriendlyByteBuf> StreamCodec<B, CustomPacketPayload> codec(
      final CustomPacketPayload.FallbackProvider<B> $$0, List<CustomPacketPayload.TypeAndCodec<? super B, ?>> $$1
   ) {
      final Map<ResourceLocation, StreamCodec<? super B, ? extends CustomPacketPayload>> $$2 = (Map)$$1.stream()
         .collect(Collectors.toUnmodifiableMap($$0x -> $$0x.type().id(), CustomPacketPayload.TypeAndCodec::codec));
      return new StreamCodec<B, CustomPacketPayload>() {
         private StreamCodec<? super B, ? extends CustomPacketPayload> findCodec(ResourceLocation $$0x) {
            StreamCodec<? super B, ? extends CustomPacketPayload> $$1 = (StreamCodec)$$2.get($$0);
            return $$1 != null ? $$1 : $$0.create($$0);
         }

         private <T extends CustomPacketPayload> void writeCap(B $$0x, CustomPacketPayload.Type<T> $$1, CustomPacketPayload $$2x) {
            $$0.writeResourceLocation($$1.id());
            StreamCodec<B, T> $$3 = this.findCodec($$1.id);
            $$3.encode($$0, $$2);
         }

         public void encode(B $$0x, CustomPacketPayload $$1) {
            this.writeCap($$0, $$1.type(), $$1);
         }

         public CustomPacketPayload decode(B $$0x) {
            ResourceLocation $$1 = $$0.readResourceLocation();
            return (CustomPacketPayload)this.findCodec($$1).decode($$0);
         }
      };
   }

   public interface FallbackProvider<B extends FriendlyByteBuf> {
      StreamCodec<B, ? extends CustomPacketPayload> create(ResourceLocation var1);
   }

   public static record Type<T extends CustomPacketPayload>(ResourceLocation id) {
      final ResourceLocation id;
   }

   public static record TypeAndCodec<B extends FriendlyByteBuf, T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<B, T> codec) {
   }
}
