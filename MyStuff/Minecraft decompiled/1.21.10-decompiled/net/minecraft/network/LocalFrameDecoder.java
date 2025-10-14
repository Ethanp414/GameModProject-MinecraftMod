package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LocalFrameDecoder extends ChannelInboundHandlerAdapter {
   @Override
   public void channelRead(ChannelHandlerContext $$0, Object $$1) {
      $$0.fireChannelRead(HiddenByteBuf.unpack($$1));
   }
}
