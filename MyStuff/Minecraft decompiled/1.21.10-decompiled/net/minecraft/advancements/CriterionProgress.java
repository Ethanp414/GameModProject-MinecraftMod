package net.minecraft.advancements;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;

public class CriterionProgress {
   @Nullable
   private Instant obtained;

   public CriterionProgress() {
   }

   public CriterionProgress(Instant $$0) {
      this.obtained = $$0;
   }

   public boolean isDone() {
      return this.obtained != null;
   }

   public void grant() {
      this.obtained = Instant.now();
   }

   public void revoke() {
      this.obtained = null;
   }

   @Nullable
   public Instant getObtained() {
      return this.obtained;
   }

   public String toString() {
      return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + "}";
   }

   public void serializeToNetwork(FriendlyByteBuf $$0) {
      $$0.writeNullable(this.obtained, FriendlyByteBuf::writeInstant);
   }

   public static CriterionProgress fromNetwork(FriendlyByteBuf $$0) {
      CriterionProgress $$1 = new CriterionProgress();
      $$1.obtained = $$0.readNullable(FriendlyByteBuf::readInstant);
      return $$1;
   }
}
