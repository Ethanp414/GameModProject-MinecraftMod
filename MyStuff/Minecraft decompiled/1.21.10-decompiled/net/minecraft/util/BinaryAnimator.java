package net.minecraft.util;

public class BinaryAnimator {
   private final int animationLength;
   private final BinaryAnimator.EasingFunction easingFunction;
   private int ticks;
   private int ticksOld;

   public BinaryAnimator(int $$0, BinaryAnimator.EasingFunction $$1) {
      this.animationLength = $$0;
      this.easingFunction = $$1;
   }

   public BinaryAnimator(int $$0) {
      this($$0, $$0x -> $$0x);
   }

   public void tick(boolean $$0) {
      this.ticksOld = this.ticks;
      if ($$0) {
         if (this.ticks < this.animationLength) {
            ++this.ticks;
         }
      } else if (this.ticks > 0) {
         --this.ticks;
      }
   }

   public float getFactor(float $$0) {
      float $$1 = Mth.lerp($$0, (float)this.ticksOld, (float)this.ticks) / (float)this.animationLength;
      return this.easingFunction.apply($$1);
   }

   public interface EasingFunction {
      float apply(float var1);
   }
}
