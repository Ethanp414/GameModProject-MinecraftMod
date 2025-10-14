package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCounted;

public record HiddenByteBuf(ByteBuf contents) implements ReferenceCounted {
   public HiddenByteBuf(final ByteBuf param1) {
      this.contents = ByteBufUtil.ensureAccessible($$0);
   }

   public static Object pack(Object $$0) {
      return $$0 instanceof ByteBuf $$1 ? new HiddenByteBuf($$1) : $$0;
   }

   public static Object unpack(Object $$0) {
      return $$0 instanceof HiddenByteBuf $$1 ? ByteBufUtil.ensureAccessible($$1.contents) : $$0;
   }

   @Override
   public int refCnt() {
      return this.contents.refCnt();
   }

   public HiddenByteBuf retain() {
      this.contents.retain();
      return this;
   }

   public HiddenByteBuf retain(int $$0) {
      this.contents.retain($$0);
      return this;
   }

   public HiddenByteBuf touch() {
      this.contents.touch();
      return this;
   }

   public HiddenByteBuf touch(Object $$0) {
      this.contents.touch($$0);
      return this;
   }

   @Override
   public boolean release() {
      return this.contents.release();
   }

   @Override
   public boolean release(int $$0) {
      return this.contents.release($$0);
   }
}
