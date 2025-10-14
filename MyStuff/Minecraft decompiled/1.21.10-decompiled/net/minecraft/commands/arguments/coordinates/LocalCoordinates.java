package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record LocalCoordinates(double left, double up, double forwards) implements Coordinates {
   public static final char PREFIX_LOCAL_COORDINATE = '^';

   @Override
   public Vec3 getPosition(CommandSourceStack $$0) {
      Vec2 $$1 = $$0.getRotation();
      Vec3 $$2 = $$0.getAnchor().apply($$0);
      float $$3 = Mth.cos(($$1.y + 90.0F) * (float) (Math.PI / 180.0));
      float $$4 = Mth.sin(($$1.y + 90.0F) * (float) (Math.PI / 180.0));
      float $$5 = Mth.cos(-$$1.x * (float) (Math.PI / 180.0));
      float $$6 = Mth.sin(-$$1.x * (float) (Math.PI / 180.0));
      float $$7 = Mth.cos((-$$1.x + 90.0F) * (float) (Math.PI / 180.0));
      float $$8 = Mth.sin((-$$1.x + 90.0F) * (float) (Math.PI / 180.0));
      Vec3 $$9 = new Vec3((double)($$3 * $$5), (double)$$6, (double)($$4 * $$5));
      Vec3 $$10 = new Vec3((double)($$3 * $$7), (double)$$8, (double)($$4 * $$7));
      Vec3 $$11 = $$9.cross($$10).scale(-1.0);
      double $$12 = $$9.x * this.forwards + $$10.x * this.up + $$11.x * this.left;
      double $$13 = $$9.y * this.forwards + $$10.y * this.up + $$11.y * this.left;
      double $$14 = $$9.z * this.forwards + $$10.z * this.up + $$11.z * this.left;
      return new Vec3($$2.x + $$12, $$2.y + $$13, $$2.z + $$14);
   }

   @Override
   public Vec2 getRotation(CommandSourceStack $$0) {
      return Vec2.ZERO;
   }

   @Override
   public boolean isXRelative() {
      return true;
   }

   @Override
   public boolean isYRelative() {
      return true;
   }

   @Override
   public boolean isZRelative() {
      return true;
   }

   public static LocalCoordinates parse(StringReader $$0) throws CommandSyntaxException {
      int $$1 = $$0.getCursor();
      double $$2 = readDouble($$0, $$1);
      if ($$0.canRead() && $$0.peek() == ' ') {
         $$0.skip();
         double $$3 = readDouble($$0, $$1);
         if ($$0.canRead() && $$0.peek() == ' ') {
            $$0.skip();
            double $$4 = readDouble($$0, $$1);
            return new LocalCoordinates($$2, $$3, $$4);
         } else {
            $$0.setCursor($$1);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext($$0);
         }
      } else {
         $$0.setCursor($$1);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext($$0);
      }
   }

   private static double readDouble(StringReader $$0, int $$1) throws CommandSyntaxException {
      if (!$$0.canRead()) {
         throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext($$0);
      } else if ($$0.peek() != '^') {
         $$0.setCursor($$1);
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext($$0);
      } else {
         $$0.skip();
         return $$0.canRead() && $$0.peek() != ' ' ? $$0.readDouble() : 0.0;
      }
   }
}
