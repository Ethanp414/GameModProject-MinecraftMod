package net.minecraft.world.flag;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;

public final class FeatureFlagSet {
   private static final FeatureFlagSet EMPTY = new FeatureFlagSet(null, 0L);
   public static final int MAX_CONTAINER_SIZE = 64;
   @Nullable
   private final FeatureFlagUniverse universe;
   private final long mask;

   private FeatureFlagSet(@Nullable FeatureFlagUniverse $$0, long $$1) {
      this.universe = $$0;
      this.mask = $$1;
   }

   static FeatureFlagSet create(FeatureFlagUniverse $$0, Collection<FeatureFlag> $$1) {
      if ($$1.isEmpty()) {
         return EMPTY;
      } else {
         long $$2 = computeMask($$0, 0L, $$1);
         return new FeatureFlagSet($$0, $$2);
      }
   }

   public static FeatureFlagSet of() {
      return EMPTY;
   }

   public static FeatureFlagSet of(FeatureFlag $$0) {
      return new FeatureFlagSet($$0.universe, $$0.mask);
   }

   public static FeatureFlagSet of(FeatureFlag $$0, FeatureFlag... $$1) {
      long $$2 = $$1.length == 0 ? $$0.mask : computeMask($$0.universe, $$0.mask, Arrays.asList($$1));
      return new FeatureFlagSet($$0.universe, $$2);
   }

   private static long computeMask(FeatureFlagUniverse $$0, long $$1, Iterable<FeatureFlag> $$2) {
      for(FeatureFlag $$3 : $$2) {
         if ($$0 != $$3.universe) {
            throw new IllegalStateException("Mismatched feature universe, expected '" + $$0 + "', but got '" + $$3.universe + "'");
         }

         $$1 |= $$3.mask;
      }

      return $$1;
   }

   public boolean contains(FeatureFlag $$0) {
      if (this.universe != $$0.universe) {
         return false;
      } else {
         return (this.mask & $$0.mask) != 0L;
      }
   }

   public boolean isEmpty() {
      return this.equals(EMPTY);
   }

   public boolean isSubsetOf(FeatureFlagSet $$0) {
      if (this.universe == null) {
         return true;
      } else if (this.universe != $$0.universe) {
         return false;
      } else {
         return (this.mask & ~$$0.mask) == 0L;
      }
   }

   public boolean intersects(FeatureFlagSet $$0) {
      if (this.universe != null && $$0.universe != null && this.universe == $$0.universe) {
         return (this.mask & $$0.mask) != 0L;
      } else {
         return false;
      }
   }

   public FeatureFlagSet join(FeatureFlagSet $$0) {
      if (this.universe == null) {
         return $$0;
      } else if ($$0.universe == null) {
         return this;
      } else if (this.universe != $$0.universe) {
         throw new IllegalArgumentException("Mismatched set elements: '" + this.universe + "' != '" + $$0.universe + "'");
      } else {
         return new FeatureFlagSet(this.universe, this.mask | $$0.mask);
      }
   }

   public FeatureFlagSet subtract(FeatureFlagSet $$0) {
      if (this.universe == null || $$0.universe == null) {
         return this;
      } else if (this.universe != $$0.universe) {
         throw new IllegalArgumentException("Mismatched set elements: '" + this.universe + "' != '" + $$0.universe + "'");
      } else {
         long $$1 = this.mask & ~$$0.mask;
         return $$1 == 0L ? EMPTY : new FeatureFlagSet(this.universe, $$1);
      }
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else {
         if ($$0 instanceof FeatureFlagSet $$1 && this.universe == $$1.universe && this.mask == $$1.mask) {
            return true;
         }

         return false;
      }
   }

   public int hashCode() {
      return (int)HashCommon.mix(this.mask);
   }
}
