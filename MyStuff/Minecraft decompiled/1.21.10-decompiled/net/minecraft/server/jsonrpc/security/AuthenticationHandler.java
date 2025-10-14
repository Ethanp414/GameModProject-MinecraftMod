package net.minecraft.server.jsonrpc.security;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;

@Sharable
public class AuthenticationHandler extends ChannelInboundHandlerAdapter {
   private final Logger LOGGER = LogUtils.getLogger();
   private static final AttributeKey<Boolean> AUTHENTICATED_KEY = AttributeKey.valueOf("authenticated");
   public static final String AUTH_HEADER = "Authorization";
   public static final String BEARER_PREFIX = "Bearer ";
   private final SecurityConfig securityConfig;

   public AuthenticationHandler(SecurityConfig $$0) {
      this.securityConfig = $$0;
   }

   @Override
   public void channelRead(ChannelHandlerContext $$0, Object $$1) throws Exception {
      String $$2 = this.getClientIp($$0);
      if ($$1 instanceof HttpRequest $$3) {
         AuthenticationHandler.SecurityCheckResult $$4 = this.performSecurityChecks($$3);
         if (!$$4.isAllowed()) {
            this.LOGGER.debug("Authentication rejected for connection with ip {}: {}", $$2, $$4.getReason());
            $$0.channel().attr(AUTHENTICATED_KEY).set(false);
            this.sendUnauthorizedResponse($$0, $$4.getReason());
            return;
         }

         $$0.channel().attr(AUTHENTICATED_KEY).set(true);
      }

      Boolean $$5 = (Boolean)$$0.channel().attr(AUTHENTICATED_KEY).get();
      if (Boolean.TRUE.equals($$5)) {
         super.channelRead($$0, $$1);
      } else {
         this.LOGGER.debug("Dropping unauthenticated connection with ip {}", $$2);
         $$0.close();
      }
   }

   private AuthenticationHandler.SecurityCheckResult performSecurityChecks(HttpRequest $$0) {
      return !this.validateAuthentication($$0)
         ? AuthenticationHandler.SecurityCheckResult.denied("Invalid or missing API key")
         : AuthenticationHandler.SecurityCheckResult.allowed();
   }

   private boolean validateAuthentication(HttpRequest $$0) {
      String $$1 = $$0.headers().get("Authorization");
      if ($$1 == null || $$1.trim().isEmpty()) {
         return false;
      } else if ($$1.startsWith("Bearer ")) {
         String $$2 = $$1.substring("Bearer ".length()).trim();
         return this.isValidApiKey($$2);
      } else {
         return false;
      }
   }

   public boolean isValidApiKey(String $$0) {
      if ($$0 != null && !$$0.isEmpty()) {
         byte[] $$1 = $$0.getBytes(StandardCharsets.UTF_8);
         byte[] $$2 = this.securityConfig.secretKey().getBytes(StandardCharsets.UTF_8);
         return MessageDigest.isEqual($$1, $$2);
      } else {
         return false;
      }
   }

   private String getClientIp(ChannelHandlerContext $$0) {
      InetSocketAddress $$1 = (InetSocketAddress)$$0.channel().remoteAddress();
      return $$1.getAddress().getHostAddress();
   }

   private void sendUnauthorizedResponse(ChannelHandlerContext $$0, String $$1) {
      String $$2 = "{\"error\":\"Unauthorized\",\"message\":\"" + $$1 + "\"}";
      byte[] $$3 = $$2.getBytes(StandardCharsets.UTF_8);
      DefaultFullHttpResponse $$4 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.wrappedBuffer($$3));
      $$4.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
      $$4.headers().set(HttpHeaderNames.CONTENT_LENGTH, $$3.length);
      $$4.headers().set(HttpHeaderNames.CONNECTION, "close");
      $$0.writeAndFlush($$4).addListener($$1x -> $$0.close());
   }

   static class SecurityCheckResult {
      private final boolean allowed;
      private final String reason;

      private SecurityCheckResult(boolean $$0, String $$1) {
         this.allowed = $$0;
         this.reason = $$1;
      }

      public static AuthenticationHandler.SecurityCheckResult allowed() {
         return new AuthenticationHandler.SecurityCheckResult(true, null);
      }

      public static AuthenticationHandler.SecurityCheckResult denied(String $$0) {
         return new AuthenticationHandler.SecurityCheckResult(false, $$0);
      }

      public boolean isAllowed() {
         return this.allowed;
      }

      public String getReason() {
         return this.reason;
      }
   }
}
