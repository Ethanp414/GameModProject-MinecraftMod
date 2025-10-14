package net.minecraft.world.level.saveddata;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;

public abstract class SavedData {
   private boolean dirty;

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean $$0) {
      this.dirty = $$0;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public static record Context(@Nullable ServerLevel level, long worldSeed) {
      public Context(ServerLevel $$0) {
         this($$0, $$0.getSeed());
      }

      public ServerLevel levelOrThrow() {
         return (ServerLevel)Objects.requireNonNull(this.level);
      }
   }
}
