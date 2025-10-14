package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PackOutput {
   private final Path outputFolder;

   public PackOutput(Path $$0) {
      this.outputFolder = $$0;
   }

   public Path getOutputFolder() {
      return this.outputFolder;
   }

   public Path getOutputFolder(PackOutput.Target $$0) {
      return this.getOutputFolder().resolve($$0.directory);
   }

   public PackOutput.PathProvider createPathProvider(PackOutput.Target $$0, String $$1) {
      return new PackOutput.PathProvider(this, $$0, $$1);
   }

   public PackOutput.PathProvider createRegistryElementsPathProvider(ResourceKey<? extends Registry<?>> $$0) {
      return this.createPathProvider(PackOutput.Target.DATA_PACK, Registries.elementsDirPath($$0));
   }

   public PackOutput.PathProvider createRegistryTagsPathProvider(ResourceKey<? extends Registry<?>> $$0) {
      return this.createPathProvider(PackOutput.Target.DATA_PACK, Registries.tagsDirPath($$0));
   }

   public static class PathProvider {
      private final Path root;
      private final String kind;

      PathProvider(PackOutput $$0, PackOutput.Target $$1, String $$2) {
         this.root = $$0.getOutputFolder($$1);
         this.kind = $$2;
      }

      public Path file(ResourceLocation $$0, String $$1) {
         return this.root.resolve($$0.getNamespace()).resolve(this.kind).resolve($$0.getPath() + "." + $$1);
      }

      public Path json(ResourceLocation $$0) {
         return this.root.resolve($$0.getNamespace()).resolve(this.kind).resolve($$0.getPath() + ".json");
      }

      public Path json(ResourceKey<?> $$0) {
         return this.root.resolve($$0.location().getNamespace()).resolve(this.kind).resolve($$0.location().getPath() + ".json");
      }
   }

   public static enum Target {
      DATA_PACK("data"),
      RESOURCE_PACK("assets"),
      REPORTS("reports");

      final String directory;

      private Target(final String param3) {
         this.directory = $$0;
      }
   }
}
