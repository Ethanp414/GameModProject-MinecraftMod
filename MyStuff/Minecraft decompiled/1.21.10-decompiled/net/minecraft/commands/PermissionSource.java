package net.minecraft.commands;

import net.minecraft.server.commands.PermissionCheck;

public interface PermissionSource {
   boolean hasPermission(int var1);

   default boolean allowsSelectors() {
      return this.hasPermission(2);
   }

   public static record Check<T extends PermissionSource>(int requiredLevel) implements PermissionCheck<T> {
      public boolean test(T $$0) {
         return $$0.hasPermission(this.requiredLevel);
      }
   }
}
