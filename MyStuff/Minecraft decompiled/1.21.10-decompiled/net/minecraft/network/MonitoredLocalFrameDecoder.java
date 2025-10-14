package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MonitoredLocalFrameDecoder extends ChannelInboundHandlerAdapter {
   private final BandwidthDebugMonitor monitor;

   public MonitoredLocalFrameDecoder(BandwidthDebugMonitor $$0) {
      this.monitor = $$0;
   }

   @Override
   public void channelRead(ChannelHandlerContext $$0, Object $$1) {
      $$1 = HiddenByteBuf.unpack($$1);
      if ($$1 instanceof ByteBuf $$2) {
         this.monitor.onReceive($$2.readableBytes());
      }

      $$0.fireChannelRead($$1);
   }
}
