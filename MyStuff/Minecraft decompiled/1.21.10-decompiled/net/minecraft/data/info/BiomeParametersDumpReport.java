package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.slf4j.Logger;

public class BiomeParametersDumpReport implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path topPath;
   private final CompletableFuture<HolderLookup.Provider> registries;
   private static final MapCodec<ResourceKey<Biome>> ENTRY_CODEC = ResourceKey.codec(Registries.BIOME).fieldOf("biome");
   private static final Codec<Climate.ParameterList<ResourceKey<Biome>>> CODEC = Climate.ParameterList.<ResourceKey<Biome>>codec(ENTRY_CODEC)
      .fieldOf("biomes")
      .codec();

   public BiomeParametersDumpReport(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
      this.topPath = $$0.getOutputFolder(PackOutput.Target.REPORTS).resolve("biome_parameters");
      this.registries = $$1;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput $$0) {
      return this.registries.thenCompose($$1 -> {
         DynamicOps<JsonElement> $$2 = $$1.createSerializationContext(JsonOps.INSTANCE);
         List<CompletableFuture<?>> $$3 = new ArrayList();
         MultiNoiseBiomeSourceParameterList.knownPresets().forEach(($$3x, $$4) -> $$3.add(dumpValue(this.createPath($$3x.id()), $$0, $$2, CODEC, $$4)));
         return CompletableFuture.allOf((CompletableFuture[])$$3.toArray($$0xx -> new CompletableFuture[$$0xx]));
      });
   }

   private static <E> CompletableFuture<?> dumpValue(Path $$0, CachedOutput $$1, DynamicOps<JsonElement> $$2, Encoder<E> $$3, E $$4) {
      Optional<JsonElement> $$5 = $$3.encodeStart($$2, $$4).resultOrPartial($$1x -> LOGGER.error("Couldn't serialize element {}: {}", $$0, $$1x));
      return $$5.isPresent() ? DataProvider.saveStable($$1, (JsonElement)$$5.get(), $$0) : CompletableFuture.completedFuture(null);
   }

   private Path createPath(ResourceLocation $$0) {
      return this.topPath.resolve($$0.getNamespace()).resolve($$0.getPath() + ".json");
   }

   @Override
   public final String getName() {
      return "Biome Parameters";
   }
}
