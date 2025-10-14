package net.minecraft.resources;

@FunctionalInterface
public interface DependantName<T, V> {
   V get(ResourceKey<T> var1);

   static <T, V> DependantName<T, V> fixed(V $$0) {
      return $$1 -> $$0;
   }
}
