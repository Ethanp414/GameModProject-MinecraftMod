package net.minecraft.world.entity.schedule;

public class Keyframe {
   private final int timeStamp;
   private final float value;

   public Keyframe(int $$0, float $$1) {
      this.timeStamp = $$0;
      this.value = $$1;
   }

   public int getTimeStamp() {
      return this.timeStamp;
   }

   public float getValue() {
      return this.value;
   }
}
