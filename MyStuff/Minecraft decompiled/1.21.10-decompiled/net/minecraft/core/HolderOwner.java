package net.minecraft.core;

public interface HolderOwner<T> {
   default boolean canSerializeIn(HolderOwner<T> $$0) {
      return $$0 == this;
   }
}
