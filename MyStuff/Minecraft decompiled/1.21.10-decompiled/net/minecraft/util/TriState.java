package net.minecraft.util;

public enum TriState {
   TRUE,
   FALSE,
   DEFAULT;

   public boolean toBoolean(boolean $$0) {
      return switch(this.ordinal()) {
         case 0 -> true;
         case 1 -> false;
         default -> $$0;
      };
   }
}
