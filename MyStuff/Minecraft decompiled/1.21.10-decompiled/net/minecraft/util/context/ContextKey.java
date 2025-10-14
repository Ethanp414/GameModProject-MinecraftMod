package net.minecraft.util.context;

import net.minecraft.resources.ResourceLocation;

public class ContextKey<T> {
   private final ResourceLocation name;

   public ContextKey(ResourceLocation $$0) {
      this.name = $$0;
   }

   public static <T> ContextKey<T> vanilla(String $$0) {
      return new ContextKey<>(ResourceLocation.withDefaultNamespace($$0));
   }

   public ResourceLocation name() {
      return this.name;
   }

   public String toString() {
      return "<parameter " + this.name + ">";
   }
}
