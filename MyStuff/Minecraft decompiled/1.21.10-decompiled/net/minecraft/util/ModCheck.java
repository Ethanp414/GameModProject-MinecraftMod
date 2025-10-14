package net.minecraft.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public record ModCheck(ModCheck.Confidence confidence, String description) {
   public static ModCheck identify(String $$0, Supplier<String> $$1, String $$2, Class<?> $$3) {
      String $$4 = (String)$$1.get();
      if (!$$0.equals($$4)) {
         return new ModCheck(ModCheck.Confidence.DEFINITELY, $$2 + " brand changed to '" + $$4 + "'");
      } else {
         return $$3.getSigners() == null
            ? new ModCheck(ModCheck.Confidence.VERY_LIKELY, $$2 + " jar signature invalidated")
            : new ModCheck(ModCheck.Confidence.PROBABLY_NOT, $$2 + " jar signature and brand is untouched");
      }
   }

   public boolean shouldReportAsModified() {
      return this.confidence.shouldReportAsModified;
   }

   public ModCheck merge(ModCheck $$0) {
      return new ModCheck(ObjectUtils.max(this.confidence, $$0.confidence), this.description + "; " + $$0.description);
   }

   public String fullDescription() {
      return this.confidence.description + " " + this.description;
   }

   public static enum Confidence {
      PROBABLY_NOT("Probably not.", false),
      VERY_LIKELY("Very likely;", true),
      DEFINITELY("Definitely;", true);

      final String description;
      final boolean shouldReportAsModified;

      private Confidence(final String param3, final boolean param4) {
         this.description = $$0;
         this.shouldReportAsModified = $$1;
      }
   }
}
