package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public enum SymmetricGroup3 {
   P123(0, 1, 2),
   P213(1, 0, 2),
   P132(0, 2, 1),
   P231(1, 2, 0),
   P312(2, 0, 1),
   P321(2, 1, 0);

   private final int[] permutation;
   private final Matrix3fc transformation;
   private static final int ORDER = 3;
   private static final SymmetricGroup3[][] CAYLEY_TABLE = Util.make(new SymmetricGroup3[values().length][values().length], $$0 -> {
      for(SymmetricGroup3 $$1 : values()) {
         for(SymmetricGroup3 $$2 : values()) {
            int[] $$3 = new int[3];

            for(int $$4 = 0; $$4 < 3; ++$$4) {
               $$3[$$4] = $$1.permutation[$$2.permutation[$$4]];
            }

            SymmetricGroup3 $$5 = (SymmetricGroup3)Arrays.stream(values()).filter($$1x -> Arrays.equals($$1x.permutation, $$3)).findFirst().get();
            $$0[$$1.ordinal()][$$2.ordinal()] = $$5;
         }
      }
   });

   private SymmetricGroup3(final int param3, final int param4, final int param5) {
      this.permutation = new int[]{$$0, $$1, $$2};
      Matrix3f $$3 = new Matrix3f().zero();
      $$3.set(this.permutation(0), 0, 1.0F);
      $$3.set(this.permutation(1), 1, 1.0F);
      $$3.set(this.permutation(2), 2, 1.0F);
      this.transformation = $$3;
   }

   public SymmetricGroup3 compose(SymmetricGroup3 $$0) {
      return CAYLEY_TABLE[this.ordinal()][$$0.ordinal()];
   }

   public int permutation(int $$0) {
      return this.permutation[$$0];
   }

   public Matrix3fc transformation() {
      return this.transformation;
   }
}
