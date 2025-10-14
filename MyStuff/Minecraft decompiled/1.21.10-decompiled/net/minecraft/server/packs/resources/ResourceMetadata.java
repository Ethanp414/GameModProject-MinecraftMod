package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;

public interface ResourceMetadata {
   ResourceMetadata EMPTY = new ResourceMetadata() {
      @Override
      public <T> Optional<T> getSection(MetadataSectionType<T> $$0) {
         return Optional.empty();
      }
   };
   IoSupplier<ResourceMetadata> EMPTY_SUPPLIER = () -> EMPTY;

   static ResourceMetadata fromJsonStream(InputStream $$0) throws IOException {
      BufferedReader $$1 = new BufferedReader(new InputStreamReader($$0, StandardCharsets.UTF_8));

      ResourceMetadata var3;
      try {
         final JsonObject $$2 = GsonHelper.parse($$1);
         var3 = new ResourceMetadata() {
            @Override
            public <T> Optional<T> getSection(MetadataSectionType<T> $$0) {
               String $$1 = $$0.name();
               if ($$2.has($$1)) {
                  T $$2x = $$0.codec().parse(JsonOps.INSTANCE, $$2.get($$1)).getOrThrow(JsonParseException::new);
                  return Optional.of($$2x);
               } else {
                  return Optional.empty();
               }
            }
         };
      } catch (Throwable var5) {
         try {
            $$1.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      $$1.close();
      return var3;
   }

   <T> Optional<T> getSection(MetadataSectionType<T> var1);

   default <T> Optional<MetadataSectionType.WithValue<T>> getTypedSection(MetadataSectionType<T> $$0) {
      return this.getSection($$0).map($$0::withValue);
   }

   default List<MetadataSectionType.WithValue<?>> getTypedSections(Collection<MetadataSectionType<?>> $$0) {
      return (List<MetadataSectionType.WithValue<?>>)$$0.stream()
         .map(this::getTypedSection)
         .flatMap(Optional::stream)
         .collect(Collectors.toUnmodifiableList());
   }
}
