package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.math.MatrixUtil;
import com.mojang.math.Transformation;
import java.util.Map;
import net.minecraft.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlockMath {
   private static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = Maps.newEnumMap(
      Map.of(
         Direction.SOUTH,
         Transformation.identity(),
         Direction.EAST,
         new Transformation(null, new Quaternionf().rotateY((float) (Math.PI / 2)), null, null),
         Direction.WEST,
         new Transformation(null, new Quaternionf().rotateY((float) (-Math.PI / 2)), null, null),
         Direction.NORTH,
         new Transformation(null, new Quaternionf().rotateY((float) Math.PI), null, null),
         Direction.UP,
         new Transformation(null, new Quaternionf().rotateX((float) (-Math.PI / 2)), null, null),
         Direction.DOWN,
         new Transformation(null, new Quaternionf().rotateX((float) (Math.PI / 2)), null, null)
      )
   );
   private static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = Maps.newEnumMap(
      Util.mapValues(VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL, Transformation::inverse)
   );

   public static Transformation blockCenterToCorner(Transformation $$0) {
      Matrix4f $$1 = new Matrix4f().translation(0.5F, 0.5F, 0.5F);
      $$1.mul($$0.getMatrix());
      $$1.translate(-0.5F, -0.5F, -0.5F);
      return new Transformation($$1);
   }

   public static Transformation blockCornerToCenter(Transformation $$0) {
      Matrix4f $$1 = new Matrix4f().translation(-0.5F, -0.5F, -0.5F);
      $$1.mul($$0.getMatrix());
      $$1.translate(0.5F, 0.5F, 0.5F);
      return new Transformation($$1);
   }

   public static Transformation getFaceTransformation(Transformation $$0, Direction $$1) {
      if (MatrixUtil.isIdentity($$0.getMatrix())) {
         return $$0;
      } else {
         Transformation $$2 = (Transformation)VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get($$1);
         $$2 = $$0.compose($$2);
         Vector3f $$3 = $$2.getMatrix().transformDirection(new Vector3f(0.0F, 0.0F, 1.0F));
         Direction $$4 = Direction.getApproximateNearest($$3.x, $$3.y, $$3.z);
         return ((Transformation)VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get($$4)).compose($$2);
      }
   }
}
