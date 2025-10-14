package net.minecraft.world.level.saveddata;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.DataFixTypes;

public record SavedDataType<T extends SavedData>(
   String id, Function<SavedData.Context, T> constructor, Function<SavedData.Context, Codec<T>> codec, DataFixTypes dataFixType
) {
   public SavedDataType(String $$0, Supplier<T> $$1, Codec<T> $$2, DataFixTypes $$3) {
      this($$0, $$1x -> (SavedData)$$1.get(), $$1x -> $$2, $$3);
   }

   public boolean equals(Object $$0) {
      if ($$0 instanceof SavedDataType $$1 && this.id.equals($$1.id)) {
         return true;
      }

      return false;
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return "SavedDataType[" + this.id + "]";
   }
}
