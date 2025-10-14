package com.mojang.math;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public enum OctahedralGroup implements StringRepresentable {
   IDENTITY("identity", SymmetricGroup3.P123, false, false, false),
   ROT_180_FACE_XY("rot_180_face_xy", SymmetricGroup3.P123, true, true, false),
   ROT_180_FACE_XZ("rot_180_face_xz", SymmetricGroup3.P123, true, false, true),
   ROT_180_FACE_YZ("rot_180_face_yz", SymmetricGroup3.P123, false, true, true),
   ROT_120_NNN("rot_120_nnn", SymmetricGroup3.P231, false, false, false),
   ROT_120_NNP("rot_120_nnp", SymmetricGroup3.P312, true, false, true),
   ROT_120_NPN("rot_120_npn", SymmetricGroup3.P312, false, true, true),
   ROT_120_NPP("rot_120_npp", SymmetricGroup3.P231, true, false, true),
   ROT_120_PNN("rot_120_pnn", SymmetricGroup3.P312, true, true, false),
   ROT_120_PNP("rot_120_pnp", SymmetricGroup3.P231, true, true, false),
   ROT_120_PPN("rot_120_ppn", SymmetricGroup3.P231, false, true, true),
   ROT_120_PPP("rot_120_ppp", SymmetricGroup3.P312, false, false, false),
   ROT_180_EDGE_XY_NEG("rot_180_edge_xy_neg", SymmetricGroup3.P213, true, true, true),
   ROT_180_EDGE_XY_POS("rot_180_edge_xy_pos", SymmetricGroup3.P213, false, false, true),
   ROT_180_EDGE_XZ_NEG("rot_180_edge_xz_neg", SymmetricGroup3.P321, true, true, true),
   ROT_180_EDGE_XZ_POS("rot_180_edge_xz_pos", SymmetricGroup3.P321, false, true, false),
   ROT_180_EDGE_YZ_NEG("rot_180_edge_yz_neg", SymmetricGroup3.P132, true, true, true),
   ROT_180_EDGE_YZ_POS("rot_180_edge_yz_pos", SymmetricGroup3.P132, true, false, false),
   ROT_90_X_NEG("rot_90_x_neg", SymmetricGroup3.P132, false, false, true),
   ROT_90_X_POS("rot_90_x_pos", SymmetricGroup3.P132, false, true, false),
   ROT_90_Y_NEG("rot_90_y_neg", SymmetricGroup3.P321, true, false, false),
   ROT_90_Y_POS("rot_90_y_pos", SymmetricGroup3.P321, false, false, true),
   ROT_90_Z_NEG("rot_90_z_neg", SymmetricGroup3.P213, false, true, false),
   ROT_90_Z_POS("rot_90_z_pos", SymmetricGroup3.P213, true, false, false),
   INVERSION("inversion", SymmetricGroup3.P123, true, true, true),
   INVERT_X("invert_x", SymmetricGroup3.P123, true, false, false),
   INVERT_Y("invert_y", SymmetricGroup3.P123, false, true, false),
   INVERT_Z("invert_z", SymmetricGroup3.P123, false, false, true),
   ROT_60_REF_NNN("rot_60_ref_nnn", SymmetricGroup3.P312, true, true, true),
   ROT_60_REF_NNP("rot_60_ref_nnp", SymmetricGroup3.P231, true, false, false),
   ROT_60_REF_NPN("rot_60_ref_npn", SymmetricGroup3.P231, false, false, true),
   ROT_60_REF_NPP("rot_60_ref_npp", SymmetricGroup3.P312, false, false, true),
   ROT_60_REF_PNN("rot_60_ref_pnn", SymmetricGroup3.P231, false, true, false),
   ROT_60_REF_PNP("rot_60_ref_pnp", SymmetricGroup3.P312, true, false, false),
   ROT_60_REF_PPN("rot_60_ref_ppn", SymmetricGroup3.P312, false, true, false),
   ROT_60_REF_PPP("rot_60_ref_ppp", SymmetricGroup3.P231, true, true, true),
   SWAP_XY("swap_xy", SymmetricGroup3.P213, false, false, false),
   SWAP_YZ("swap_yz", SymmetricGroup3.P132, false, false, false),
   SWAP_XZ("swap_xz", SymmetricGroup3.P321, false, false, false),
   SWAP_NEG_XY("swap_neg_xy", SymmetricGroup3.P213, true, true, false),
   SWAP_NEG_YZ("swap_neg_yz", SymmetricGroup3.P132, false, true, true),
   SWAP_NEG_XZ("swap_neg_xz", SymmetricGroup3.P321, true, false, true),
   ROT_90_REF_X_NEG("rot_90_ref_x_neg", SymmetricGroup3.P132, true, false, true),
   ROT_90_REF_X_POS("rot_90_ref_x_pos", SymmetricGroup3.P132, true, true, false),
   ROT_90_REF_Y_NEG("rot_90_ref_y_neg", SymmetricGroup3.P321, true, true, false),
   ROT_90_REF_Y_POS("rot_90_ref_y_pos", SymmetricGroup3.P321, false, true, true),
   ROT_90_REF_Z_NEG("rot_90_ref_z_neg", SymmetricGroup3.P213, false, true, true),
   ROT_90_REF_Z_POS("rot_90_ref_z_pos", SymmetricGroup3.P213, true, false, true);

   private static final Direction.Axis[] AXES = Direction.Axis.values();
   private final Matrix3fc transformation;
   private final String name;
   @Nullable
   private Map<Direction, Direction> rotatedDirections;
   private final boolean invertX;
   private final boolean invertY;
   private final boolean invertZ;
   private final SymmetricGroup3 permutation;
   private static final OctahedralGroup[][] CAYLEY_TABLE = Util.make(
      new OctahedralGroup[values().length][values().length],
      $$0 -> {
         Map<Pair<SymmetricGroup3, BooleanList>, OctahedralGroup> $$1 = (Map)Arrays.stream(values())
            .collect(Collectors.toMap($$0x -> Pair.of($$0x.permutation, $$0x.packInversions()), $$0x -> $$0x));
   
         for(OctahedralGroup $$2 : values()) {
            for(OctahedralGroup $$3 : values()) {
               BooleanList $$4 = $$2.packInversions();
               BooleanList $$5 = $$3.packInversions();
               SymmetricGroup3 $$6 = $$3.permutation.compose($$2.permutation);
               BooleanArrayList $$7 = new BooleanArrayList(3);
   
               for(int $$8 = 0; $$8 < 3; ++$$8) {
                  $$7.add($$4.getBoolean($$8) ^ $$5.getBoolean($$2.permutation.permutation($$8)));
               }
   
               $$0[$$2.ordinal()][$$3.ordinal()] = (OctahedralGroup)$$1.get(Pair.of($$6, $$7));
            }
         }
      }
   );
   private static final OctahedralGroup[] INVERSE_TABLE = (OctahedralGroup[])Arrays.stream(values())
      .map($$0 -> (OctahedralGroup)Arrays.stream(values()).filter($$1 -> $$0.compose($$1) == IDENTITY).findAny().get())
      .toArray($$0 -> new OctahedralGroup[$$0]);
   private static final OctahedralGroup[][] XY_TABLE = Util.make(new OctahedralGroup[Quadrant.values().length][Quadrant.values().length], $$0 -> {
      for(Quadrant $$1 : Quadrant.values()) {
         for(Quadrant $$2 : Quadrant.values()) {
            OctahedralGroup $$3 = IDENTITY;

            for(int $$4 = 0; $$4 < $$2.shift; ++$$4) {
               $$3 = $$3.compose(ROT_90_Y_NEG);
            }

            for(int $$5 = 0; $$5 < $$1.shift; ++$$5) {
               $$3 = $$3.compose(ROT_90_X_NEG);
            }

            $$0[$$1.ordinal()][$$2.ordinal()] = $$3;
         }
      }
   });

   private OctahedralGroup(final String param3, final SymmetricGroup3 param4, final boolean param5, final boolean param6, final boolean param7) {
      this.name = $$0;
      this.invertX = $$2;
      this.invertY = $$3;
      this.invertZ = $$4;
      this.permutation = $$1;
      Matrix3f $$5 = new Matrix3f().scaling($$2 ? -1.0F : 1.0F, $$3 ? -1.0F : 1.0F, $$4 ? -1.0F : 1.0F);
      $$5.mul($$1.transformation());
      this.transformation = $$5;
   }

   private BooleanList packInversions() {
      return new BooleanArrayList(new boolean[]{this.invertX, this.invertY, this.invertZ});
   }

   public OctahedralGroup compose(OctahedralGroup $$0) {
      return CAYLEY_TABLE[this.ordinal()][$$0.ordinal()];
   }

   public OctahedralGroup inverse() {
      return INVERSE_TABLE[this.ordinal()];
   }

   public Matrix3fc transformation() {
      return this.transformation;
   }

   public String toString() {
      return this.name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public Direction rotate(Direction $$0) {
      if (this.rotatedDirections == null) {
         this.rotatedDirections = Util.makeEnumMap(Direction.class, $$0x -> {
            Direction.Axis $$1 = $$0x.getAxis();
            Direction.AxisDirection $$2 = $$0x.getAxisDirection();
            Direction.Axis $$3 = this.permute($$1);
            Direction.AxisDirection $$4 = this.inverts($$3) ? $$2.opposite() : $$2;
            return Direction.fromAxisAndDirection($$3, $$4);
         });
      }

      return (Direction)this.rotatedDirections.get($$0);
   }

   public boolean inverts(Direction.Axis $$0) {
      return switch($$0) {
         case X -> this.invertX;
         case Y -> this.invertY;
         case Z -> this.invertZ;
         default -> throw new MatchException(null, null);
      };
   }

   public Direction.Axis permute(Direction.Axis $$0) {
      return AXES[this.permutation.permutation($$0.ordinal())];
   }

   public FrontAndTop rotate(FrontAndTop $$0) {
      return FrontAndTop.fromFrontAndTop(this.rotate($$0.front()), this.rotate($$0.top()));
   }

   public static OctahedralGroup fromXYAngles(Quadrant $$0, Quadrant $$1) {
      return XY_TABLE[$$0.ordinal()][$$1.ordinal()];
   }
}
