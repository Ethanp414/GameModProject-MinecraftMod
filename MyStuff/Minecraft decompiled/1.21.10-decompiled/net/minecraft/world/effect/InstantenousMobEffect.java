package net.minecraft.world.effect;

public class InstantenousMobEffect extends MobEffect {
   public InstantenousMobEffect(MobEffectCategory $$0, int $$1) {
      super($$0, $$1);
   }

   @Override
   public boolean isInstantenous() {
      return true;
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int $$0, int $$1) {
      return $$0 >= 1;
   }
}
