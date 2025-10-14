package net.minecraft.network;

import com.google.common.base.Suppliers;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.HandshakeProtocols;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.StatusProtocols;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.LocalSampleLogger;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
   private static final float AVERAGE_PACKETS_SMOOTHING = 0.75F;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Marker ROOT_MARKER = MarkerFactory.getMarker("NETWORK");
   public static final Marker PACKET_MARKER = Util.make(MarkerFactory.getMarker("NETWORK_PACKETS"), $$0 -> $$0.add(ROOT_MARKER));
   public static final Marker PACKET_RECEIVED_MARKER = Util.make(MarkerFactory.getMarker("PACKET_RECEIVED"), $$0 -> $$0.add(PACKET_MARKER));
   public static final Marker PACKET_SENT_MARKER = Util.make(MarkerFactory.getMarker("PACKET_SENT"), $$0 -> $$0.add(PACKET_MARKER));
   public static final Supplier<NioEventLoopGroup> NETWORK_WORKER_GROUP = Suppliers.memoize(
      () -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
   );
   public static final Supplier<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = Suppliers.memoize(
      () -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
   );
   public static final Supplier<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = Suppliers.memoize(
      () -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
   );
   private static final ProtocolInfo<ServerHandshakePacketListener> INITIAL_PROTOCOL = HandshakeProtocols.SERVERBOUND;
   private final PacketFlow receiving;
   private volatile boolean sendLoginDisconnect = true;
   private final Queue<Consumer<Connection>> pendingActions = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   @Nullable
   private volatile PacketListener disconnectListener;
   @Nullable
   private volatile PacketListener packetListener;
   @Nullable
   private DisconnectionDetails disconnectionDetails;
   private boolean encrypted;
   private boolean disconnectionHandled;
   private int receivedPackets;
   private int sentPackets;
   private float averageReceivedPackets;
   private float averageSentPackets;
   private int tickCount;
   private boolean handlingFault;
   @Nullable
   private volatile DisconnectionDetails delayedDisconnect;
   @Nullable
   BandwidthDebugMonitor bandwidthDebugMonitor;

   public Connection(PacketFlow $$0) {
      this.receiving = $$0;
   }

   @Override
   public void channelActive(ChannelHandlerContext $$0) throws Exception {
      super.channelActive($$0);
      this.channel = $$0.channel();
      this.address = this.channel.remoteAddress();
      if (this.delayedDisconnect != null) {
         this.disconnect(this.delayedDisconnect);
      }
   }

   @Override
   public void channelInactive(ChannelHandlerContext $$0) {
      this.disconnect(Component.translatable("disconnect.endOfStream"));
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext $$0, Throwable $$1) {
      if ($$1 instanceof SkipPacketException) {
         LOGGER.debug("Skipping packet due to errors", $$1.getCause());
      } else {
         boolean $$2 = !this.handlingFault;
         this.handlingFault = true;
         if (this.channel.isOpen()) {
            if ($$1 instanceof TimeoutException) {
               LOGGER.debug("Timeout", $$1);
               this.disconnect(Component.translatable("disconnect.timeout"));
            } else {
               Component $$3 = Component.translatable("disconnect.genericReason", "Internal Exception: " + $$1);
               PacketListener $$4 = this.packetListener;
               DisconnectionDetails $$5;
               if ($$4 != null) {
                  $$5 = $$4.createDisconnectionInfo($$3, $$1);
               } else {
                  $$5 = new DisconnectionDetails($$3);
               }

               if ($$2) {
                  LOGGER.debug("Failed to sent packet", $$1);
                  if (this.getSending() == PacketFlow.CLIENTBOUND) {
                     Packet<?> $$7 = (Packet<?>)(this.sendLoginDisconnect ? new ClientboundLoginDisconnectPacket($$3) : new ClientboundDisconnectPacket($$3));
                     this.send($$7, PacketSendListener.thenRun(() -> this.disconnect($$5)));
                  } else {
                     this.disconnect($$5);
                  }

                  this.setReadOnly();
               } else {
                  LOGGER.debug("Double fault", $$1);
                  this.disconnect($$5);
               }
            }
         }
      }
   }

   protected void channelRead0(ChannelHandlerContext $$0, Packet<?> $$1) {
      if (this.channel.isOpen()) {
         PacketListener $$2 = this.packetListener;
         if ($$2 == null) {
            throw new IllegalStateException("Received a packet before the packet listener was initialized");
         } else {
            if ($$2.shouldHandleMessage($$1)) {
               try {
                  genericsFtw($$1, $$2);
               } catch (RunningOnDifferentThreadException var5) {
               } catch (RejectedExecutionException var6) {
                  this.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
               } catch (ClassCastException var7) {
                  LOGGER.error("Received {} that couldn't be processed", $$1.getClass(), var7);
                  this.disconnect(Component.translatable("multiplayer.disconnect.invalid_packet"));
               }

               ++this.receivedPackets;
            }
         }
      }
   }

   private static <T extends PacketListener> void genericsFtw(Packet<T> $$0, PacketListener $$1) {
      $$0.handle((T)$$1);
   }

   private void validateListener(ProtocolInfo<?> $$0, PacketListener $$1) {
      Objects.requireNonNull($$1, "packetListener");
      PacketFlow $$2 = $$1.flow();
      if ($$2 != this.receiving) {
         throw new IllegalStateException("Trying to set listener for wrong side: connection is " + this.receiving + ", but listener is " + $$2);
      } else {
         ConnectionProtocol $$3 = $$1.protocol();
         if ($$0.id() != $$3) {
            throw new IllegalStateException("Listener protocol (" + $$3 + ") does not match requested one " + $$0);
         }
      }
   }

   private static void syncAfterConfigurationChange(ChannelFuture $$0) {
      try {
         $$0.syncUninterruptibly();
      } catch (Exception var2) {
         if (var2 instanceof ClosedChannelException) {
            LOGGER.info("Connection closed during protocol change");
         } else {
            throw var2;
         }
      }
   }

   public <T extends PacketListener> void setupInboundProtocol(ProtocolInfo<T> $$0, T $$1) {
      this.validateListener($$0, $$1);
      if ($$0.flow() != this.getReceiving()) {
         throw new IllegalStateException("Invalid inbound protocol: " + $$0.id());
      } else {
         this.packetListener = $$1;
         this.disconnectListener = null;
         UnconfiguredPipelineHandler.InboundConfigurationTask $$2 = UnconfiguredPipelineHandler.setupInboundProtocol($$0);
         BundlerInfo $$3 = $$0.bundlerInfo();
         if ($$3 != null) {
            PacketBundlePacker $$4 = new PacketBundlePacker($$3);
            $$2 = $$2.andThen($$1x -> $$1x.pipeline().addAfter("decoder", "bundler", $$4));
         }

         syncAfterConfigurationChange(this.channel.writeAndFlush($$2));
      }
   }

   public void setupOutboundProtocol(ProtocolInfo<?> $$0) {
      if ($$0.flow() != this.getSending()) {
         throw new IllegalStateException("Invalid outbound protocol: " + $$0.id());
      } else {
         UnconfiguredPipelineHandler.OutboundConfigurationTask $$1 = UnconfiguredPipelineHandler.setupOutboundProtocol($$0);
         BundlerInfo $$2 = $$0.bundlerInfo();
         if ($$2 != null) {
            PacketBundleUnpacker $$3 = new PacketBundleUnpacker($$2);
            $$1 = $$1.andThen($$1x -> $$1x.pipeline().addAfter("encoder", "unbundler", $$3));
         }

         boolean $$4 = $$0.id() == ConnectionProtocol.LOGIN;
         syncAfterConfigurationChange(this.channel.writeAndFlush($$1.andThen($$1x -> this.sendLoginDisconnect = $$4)));
      }
   }

   public void setListenerForServerboundHandshake(PacketListener $$0) {
      if (this.packetListener != null) {
         throw new IllegalStateException("Listener already set");
      } else if (this.receiving == PacketFlow.SERVERBOUND && $$0.flow() == PacketFlow.SERVERBOUND && $$0.protocol() == INITIAL_PROTOCOL.id()) {
         this.packetListener = $$0;
      } else {
         throw new IllegalStateException("Invalid initial listener");
      }
   }

   public void initiateServerboundStatusConnection(String $$0, int $$1, ClientStatusPacketListener $$2) {
      this.initiateServerboundConnection($$0, $$1, StatusProtocols.SERVERBOUND, StatusProtocols.CLIENTBOUND, $$2, ClientIntent.STATUS);
   }

   public void initiateServerboundPlayConnection(String $$0, int $$1, ClientLoginPacketListener $$2) {
      this.initiateServerboundConnection($$0, $$1, LoginProtocols.SERVERBOUND, LoginProtocols.CLIENTBOUND, $$2, ClientIntent.LOGIN);
   }

   public <S extends ServerboundPacketListener, C extends ClientboundPacketListener> void initiateServerboundPlayConnection(
      String $$0, int $$1, ProtocolInfo<S> $$2, ProtocolInfo<C> $$3, C $$4, boolean $$5
   ) {
      this.initiateServerboundConnection($$0, $$1, $$2, $$3, $$4, $$5 ? ClientIntent.TRANSFER : ClientIntent.LOGIN);
   }

   private <S extends ServerboundPacketListener, C extends ClientboundPacketListener> void initiateServerboundConnection(
      String $$0, int $$1, ProtocolInfo<S> $$2, ProtocolInfo<C> $$3, C $$4, ClientIntent $$5
   ) {
      if ($$2.id() != $$3.id()) {
         throw new IllegalStateException("Mismatched initial protocols");
      } else {
         this.disconnectListener = $$4;
         this.runOnceConnected($$6 -> {
            this.setupInboundProtocol($$3, $$4);
            $$6.sendPacket(new ClientIntentionPacket(SharedConstants.getCurrentVersion().protocolVersion(), $$0, $$1, $$5), null, true);
            this.setupOutboundProtocol($$2);
         });
      }
   }

   public void send(Packet<?> $$0) {
      this.send($$0, null);
   }

   public void send(Packet<?> $$0, @Nullable ChannelFutureListener $$1) {
      this.send($$0, $$1, true);
   }

   public void send(Packet<?> $$0, @Nullable ChannelFutureListener $$1, boolean $$2) {
      if (this.isConnected()) {
         this.flushQueue();
         this.sendPacket($$0, $$1, $$2);
      } else {
         this.pendingActions.add((Consumer)$$3 -> $$3.sendPacket($$0, $$1, $$2));
      }
   }

   public void runOnceConnected(Consumer<Connection> $$0) {
      if (this.isConnected()) {
         this.flushQueue();
         $$0.accept(this);
      } else {
         this.pendingActions.add($$0);
      }
   }

   private void sendPacket(Packet<?> $$0, @Nullable ChannelFutureListener $$1, boolean $$2) {
      ++this.sentPackets;
      if (this.channel.eventLoop().inEventLoop()) {
         this.doSendPacket($$0, $$1, $$2);
      } else {
         this.channel.eventLoop().execute(() -> this.doSendPacket($$0, $$1, $$2));
      }
   }

   private void doSendPacket(Packet<?> $$0, @Nullable ChannelFutureListener $$1, boolean $$2) {
      if ($$1 != null) {
         ChannelFuture $$3 = $$2 ? this.channel.writeAndFlush($$0) : this.channel.write($$0);
         $$3.addListener($$1);
      } else if ($$2) {
         this.channel.writeAndFlush($$0, this.channel.voidPromise());
      } else {
         this.channel.write($$0, this.channel.voidPromise());
      }
   }

   public void flushChannel() {
      if (this.isConnected()) {
         this.flush();
      } else {
         this.pendingActions.add(Connection::flush);
      }
   }

   private void flush() {
      if (this.channel.eventLoop().inEventLoop()) {
         this.channel.flush();
      } else {
         this.channel.eventLoop().execute(() -> this.channel.flush());
      }
   }

   private void flushQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         synchronized(this.pendingActions) {
            Consumer<Connection> $$0;
            while(($$0 = (Consumer)this.pendingActions.poll()) != null) {
               $$0.accept(this);
            }
         }
      }
   }

   public void tick() {
      this.flushQueue();
      PacketListener var2 = this.packetListener;
      if (var2 instanceof TickablePacketListener $$0) {
         $$0.tick();
      }

      if (!this.isConnected() && !this.disconnectionHandled) {
         this.handleDisconnection();
      }

      if (this.channel != null) {
         this.channel.flush();
      }

      if (this.tickCount++ % 20 == 0) {
         this.tickSecond();
      }

      if (this.bandwidthDebugMonitor != null) {
         this.bandwidthDebugMonitor.tick();
      }
   }

   protected void tickSecond() {
      this.averageSentPackets = Mth.lerp(0.75F, (float)this.sentPackets, this.averageSentPackets);
      this.averageReceivedPackets = Mth.lerp(0.75F, (float)this.receivedPackets, this.averageReceivedPackets);
      this.sentPackets = 0;
      this.receivedPackets = 0;
   }

   public SocketAddress getRemoteAddress() {
      return this.address;
   }

   public String getLoggableAddress(boolean $$0) {
      if (this.address == null) {
         return "local";
      } else {
         return $$0 ? this.address.toString() : "IP hidden";
      }
   }

   public void disconnect(Component $$0) {
      this.disconnect(new DisconnectionDetails($$0));
   }

   public void disconnect(DisconnectionDetails $$0) {
      if (this.channel == null) {
         this.delayedDisconnect = $$0;
      }

      if (this.isConnected()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectionDetails = $$0;
      }
   }

   public boolean isMemoryConnection() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public PacketFlow getReceiving() {
      return this.receiving;
   }

   public PacketFlow getSending() {
      return this.receiving.getOpposite();
   }

   public static Connection connectToServer(InetSocketAddress $$0, boolean $$1, @Nullable LocalSampleLogger $$2) {
      Connection $$3 = new Connection(PacketFlow.CLIENTBOUND);
      if ($$2 != null) {
         $$3.setBandwidthLogger($$2);
      }

      ChannelFuture $$4 = connect($$0, $$1, $$3);
      $$4.syncUninterruptibly();
      return $$3;
   }

   public static ChannelFuture connect(InetSocketAddress $$0, boolean $$1, final Connection $$2) {
      Class<? extends SocketChannel> $$3;
      EventLoopGroup $$4;
      if (Epoll.isAvailable() && $$1) {
         $$3 = EpollSocketChannel.class;
         $$4 = (EventLoopGroup)NETWORK_EPOLL_WORKER_GROUP.get();
      } else {
         $$3 = NioSocketChannel.class;
         $$4 = (EventLoopGroup)NETWORK_WORKER_GROUP.get();
      }

      return new Bootstrap().group($$4).handler(new ChannelInitializer<Channel>() {
         @Override
         protected void initChannel(Channel $$0) {
            try {
               $$0.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
            }

            ChannelPipeline $$1 = $$0.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
            Connection.configureSerialization($$1, PacketFlow.CLIENTBOUND, false, $$2.bandwidthDebugMonitor);
            $$2.configurePacketHandler($$1);
         }
      }).channel($$3).connect($$0.getAddress(), $$0.getPort());
   }

   private static String outboundHandlerName(boolean $$0) {
      return $$0 ? "encoder" : "outbound_config";
   }

   private static String inboundHandlerName(boolean $$0) {
      return $$0 ? "decoder" : "inbound_config";
   }

   public void configurePacketHandler(ChannelPipeline $$0) {
      $$0.addLast("hackfix", new ChannelOutboundHandlerAdapter(this) {
         @Override
         public void write(ChannelHandlerContext $$0, Object $$1, ChannelPromise $$2) throws Exception {
            super.write($$0, $$1, $$2);
         }
      }).addLast("packet_handler", this);
   }

   public static void configureSerialization(ChannelPipeline $$0, PacketFlow $$1, boolean $$2, @Nullable BandwidthDebugMonitor $$3) {
      PacketFlow $$4 = $$1.getOpposite();
      boolean $$5 = $$1 == PacketFlow.SERVERBOUND;
      boolean $$6 = $$4 == PacketFlow.SERVERBOUND;
      $$0.addLast("splitter", createFrameDecoder($$3, $$2))
         .addLast(new FlowControlHandler())
         .addLast(inboundHandlerName($$5), (ChannelHandler)($$5 ? new PacketDecoder<>(INITIAL_PROTOCOL) : new UnconfiguredPipelineHandler.Inbound()))
         .addLast("prepender", createFrameEncoder($$2))
         .addLast(outboundHandlerName($$6), (ChannelHandler)($$6 ? new PacketEncoder<>(INITIAL_PROTOCOL) : new UnconfiguredPipelineHandler.Outbound()));
   }

   private static ChannelOutboundHandler createFrameEncoder(boolean $$0) {
      return (ChannelOutboundHandler)($$0 ? new LocalFrameEncoder() : new Varint21LengthFieldPrepender());
   }

   private static ChannelInboundHandler createFrameDecoder(@Nullable BandwidthDebugMonitor $$0, boolean $$1) {
      if (!$$1) {
         return new Varint21FrameDecoder($$0);
      } else {
         return (ChannelInboundHandler)($$0 != null ? new MonitoredLocalFrameDecoder($$0) : new LocalFrameDecoder());
      }
   }

   public static void configureInMemoryPipeline(ChannelPipeline $$0, PacketFlow $$1) {
      configureSerialization($$0, $$1, true, null);
   }

   public static Connection connectToLocalServer(SocketAddress $$0) {
      final Connection $$1 = new Connection(PacketFlow.CLIENTBOUND);
      new Bootstrap().group((EventLoopGroup)LOCAL_WORKER_GROUP.get()).handler(new ChannelInitializer<Channel>() {
         @Override
         protected void initChannel(Channel $$0) {
            ChannelPipeline $$1x = $$0.pipeline();
            Connection.configureInMemoryPipeline($$1x, PacketFlow.CLIENTBOUND);
            $$1.configurePacketHandler($$1x);
         }
      }).channel(LocalChannel.class).connect($$0).syncUninterruptibly();
      return $$1;
   }

   public void setEncryptionKey(Cipher $$0, Cipher $$1) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new CipherDecoder($$0));
      this.channel.pipeline().addBefore("prepender", "encrypt", new CipherEncoder($$1));
   }

   public boolean isEncrypted() {
      return this.encrypted;
   }

   public boolean isConnected() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean isConnecting() {
      return this.channel == null;
   }

   @Nullable
   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   @Nullable
   public DisconnectionDetails getDisconnectionDetails() {
      return this.disconnectionDetails;
   }

   public void setReadOnly() {
      if (this.channel != null) {
         this.channel.config().setAutoRead(false);
      }
   }

   public void setupCompression(int $$0, boolean $$1) {
      if ($$0 >= 0) {
         ChannelHandler var4 = this.channel.pipeline().get("decompress");
         if (var4 instanceof CompressionDecoder $$2) {
            $$2.setThreshold($$0, $$1);
         } else {
            this.channel.pipeline().addAfter("splitter", "decompress", new CompressionDecoder($$0, $$1));
         }

         var4 = this.channel.pipeline().get("compress");
         if (var4 instanceof CompressionEncoder $$3) {
            $$3.setThreshold($$0);
         } else {
            this.channel.pipeline().addAfter("prepender", "compress", new CompressionEncoder($$0));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   public void handleDisconnection() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (this.disconnectionHandled) {
            LOGGER.warn("handleDisconnection() called twice");
         } else {
            this.disconnectionHandled = true;
            PacketListener $$0 = this.getPacketListener();
            PacketListener $$1 = $$0 != null ? $$0 : this.disconnectListener;
            if ($$1 != null) {
               DisconnectionDetails $$2 = (DisconnectionDetails)Objects.requireNonNullElseGet(
                  this.getDisconnectionDetails(), () -> new DisconnectionDetails(Component.translatable("multiplayer.disconnect.generic"))
               );
               $$1.onDisconnect($$2);
            }
         }
      }
   }

   public float getAverageReceivedPackets() {
      return this.averageReceivedPackets;
   }

   public float getAverageSentPackets() {
      return this.averageSentPackets;
   }

   public void setBandwidthLogger(LocalSampleLogger $$0) {
      this.bandwidthDebugMonitor = new BandwidthDebugMonitor($$0);
   }
}
