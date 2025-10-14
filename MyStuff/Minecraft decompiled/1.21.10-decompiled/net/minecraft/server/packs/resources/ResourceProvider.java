package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface ResourceProvider {
   ResourceProvider EMPTY = $$0 -> Optional.empty();

   Optional<Resource> getResource(ResourceLocation var1);

   default Resource getResourceOrThrow(ResourceLocation $$0) throws FileNotFoundException {
      return (Resource)this.getResource($$0).orElseThrow(() -> new FileNotFoundException($$0.toString()));
   }

   default InputStream open(ResourceLocation $$0) throws IOException {
      return this.getResourceOrThrow($$0).open();
   }

   default BufferedReader openAsReader(ResourceLocation $$0) throws IOException {
      return this.getResourceOrThrow($$0).openAsReader();
   }

   static ResourceProvider fromMap(Map<ResourceLocation, Resource> $$0) {
      return $$1 -> Optional.ofNullable((Resource)$$0.get($$1));
   }
}
