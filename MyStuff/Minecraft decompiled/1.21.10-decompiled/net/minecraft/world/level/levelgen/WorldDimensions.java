package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.PrimaryLevelData;

public record WorldDimensions(Map<ResourceKey<LevelStem>, LevelStem> dimensions) {
   public static final MapCodec<WorldDimensions> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)
            )
            .apply($$0, $$0.stable(WorldDimensions::new))
   );
   private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER = ImmutableSet.of(LevelStem.OVERWORLD, LevelStem.NETHER, LevelStem.END);
   private static final int VANILLA_DIMENSION_COUNT = BUILTIN_ORDER.size();

   public WorldDimensions(Map<ResourceKey<LevelStem>, LevelStem> param1) {
      LevelStem $$1 = (LevelStem)$$0.get(LevelStem.OVERWORLD);
      if ($$1 == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         this.dimensions = $$0;
      }
   }

   public WorldDimensions(Registry<LevelStem> $$0) {
      this((Map<ResourceKey<LevelStem>, LevelStem>)$$0.listElements().collect(Collectors.toMap(Holder.Reference::key, Holder.Reference::value)));
   }

   public static Stream<ResourceKey<LevelStem>> keysInOrder(Stream<ResourceKey<LevelStem>> $$0) {
      return Stream.concat(BUILTIN_ORDER.stream(), $$0.filter($$0x -> !BUILTIN_ORDER.contains($$0x)));
   }

   public WorldDimensions replaceOverworldGenerator(HolderLookup.Provider $$0, ChunkGenerator $$1) {
      HolderLookup<DimensionType> $$2 = $$0.lookupOrThrow(Registries.DIMENSION_TYPE);
      Map<ResourceKey<LevelStem>, LevelStem> $$3 = withOverworld($$2, this.dimensions, $$1);
      return new WorldDimensions($$3);
   }

   public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(
      HolderLookup<DimensionType> $$0, Map<ResourceKey<LevelStem>, LevelStem> $$1, ChunkGenerator $$2
   ) {
      LevelStem $$3 = (LevelStem)$$1.get(LevelStem.OVERWORLD);
      Holder<DimensionType> $$4 = (Holder<DimensionType>)($$3 == null ? $$0.getOrThrow(BuiltinDimensionTypes.OVERWORLD) : $$3.type());
      return withOverworld($$1, $$4, $$2);
   }

   public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(
      Map<ResourceKey<LevelStem>, LevelStem> $$0, Holder<DimensionType> $$1, ChunkGenerator $$2
   ) {
      Builder<ResourceKey<LevelStem>, LevelStem> $$3 = ImmutableMap.builder();
      $$3.putAll($$0);
      $$3.put(LevelStem.OVERWORLD, new LevelStem($$1, $$2));
      return $$3.buildKeepingLast();
   }

   public ChunkGenerator overworld() {
      LevelStem $$0 = (LevelStem)this.dimensions.get(LevelStem.OVERWORLD);
      if ($$0 == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         return $$0.generator();
      }
   }

   public Optional<LevelStem> get(ResourceKey<LevelStem> $$0) {
      return Optional.ofNullable((LevelStem)this.dimensions.get($$0));
   }

   public ImmutableSet<ResourceKey<Level>> levels() {
      return (ImmutableSet<ResourceKey<Level>>)this.dimensions().keySet().stream().map(Registries::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
   }

   public boolean isDebug() {
      return this.overworld() instanceof DebugLevelSource;
   }

   private static PrimaryLevelData.SpecialWorldProperty specialWorldProperty(Registry<LevelStem> $$0) {
      return (PrimaryLevelData.SpecialWorldProperty)$$0.getOptional(LevelStem.OVERWORLD).map($$0x -> {
         ChunkGenerator $$1 = $$0x.generator();
         if ($$1 instanceof DebugLevelSource) {
            return PrimaryLevelData.SpecialWorldProperty.DEBUG;
         } else {
            return $$1 instanceof FlatLevelSource ? PrimaryLevelData.SpecialWorldProperty.FLAT : PrimaryLevelData.SpecialWorldProperty.NONE;
         }
      }).orElse(PrimaryLevelData.SpecialWorldProperty.NONE);
   }

   static Lifecycle checkStability(ResourceKey<LevelStem> $$0, LevelStem $$1) {
      return isVanillaLike($$0, $$1) ? Lifecycle.stable() : Lifecycle.experimental();
   }

   private static boolean isVanillaLike(ResourceKey<LevelStem> $$0, LevelStem $$1) {
      if ($$0 == LevelStem.OVERWORLD) {
         return isStableOverworld($$1);
      } else if ($$0 == LevelStem.NETHER) {
         return isStableNether($$1);
      } else {
         return $$0 == LevelStem.END ? isStableEnd($$1) : false;
      }
   }

   private static boolean isStableOverworld(LevelStem $$0) {
      Holder<DimensionType> $$1 = $$0.type();
      if (!$$1.is(BuiltinDimensionTypes.OVERWORLD) && !$$1.is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
         return false;
      } else {
         BiomeSource var3 = $$0.generator().getBiomeSource();
         if (var3 instanceof MultiNoiseBiomeSource $$2 && !$$2.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
            return false;
         }

         return true;
      }
   }

   private static boolean isStableNether(LevelStem $$0) {
      if ($$0.type().is(BuiltinDimensionTypes.NETHER)) {
         ChunkGenerator var3 = $$0.generator();
         if (var3 instanceof NoiseBasedChunkGenerator $$1 && $$1.stable(NoiseGeneratorSettings.NETHER)) {
            BiomeSource var4 = $$1.getBiomeSource();
            if (var4 instanceof MultiNoiseBiomeSource $$2 && $$2.stable(MultiNoiseBiomeSourceParameterLists.NETHER)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean isStableEnd(LevelStem $$0) {
      if ($$0.type().is(BuiltinDimensionTypes.END)) {
         ChunkGenerator var2 = $$0.generator();
         if (var2 instanceof NoiseBasedChunkGenerator $$1 && $$1.stable(NoiseGeneratorSettings.END) && $$1.getBiomeSource() instanceof TheEndBiomeSource) {
            return true;
         }
      }

      return false;
   }

   public WorldDimensions.Complete bake(Registry<LevelStem> $$0) {
      Stream<ResourceKey<LevelStem>> $$1 = Stream.concat($$0.registryKeySet().stream(), this.dimensions.keySet().stream()).distinct();

      record Entry(ResourceKey<LevelStem> key, LevelStem value) {
         final ResourceKey<LevelStem> key;
         final LevelStem value;

         RegistrationInfo registrationInfo() {
            return new RegistrationInfo(Optional.empty(), WorldDimensions.checkStability(this.key, this.value));
         }
      }

      List<Entry> $$2 = new ArrayList();
      keysInOrder($$1)
         .forEach(
            $$2x -> $$0.getOptional($$2x)
                  .or(() -> Optional.ofNullable((LevelStem)this.dimensions.get($$2x)))
                  .ifPresent($$2xx -> $$2.add(new Entry($$2x, $$2xx)))
         );
      Lifecycle $$3 = $$2.size() == VANILLA_DIMENSION_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
      WritableRegistry<LevelStem> $$4 = new MappedRegistry(Registries.LEVEL_STEM, $$3);
      $$2.forEach($$1x -> $$4.register($$1x.key, $$1x.value, $$1x.registrationInfo()));
      Registry<LevelStem> $$5 = $$4.freeze();
      PrimaryLevelData.SpecialWorldProperty $$6 = specialWorldProperty($$5);
      return new WorldDimensions.Complete($$5.freeze(), $$6);
   }

   public static record Complete(Registry<LevelStem> dimensions, PrimaryLevelData.SpecialWorldProperty specialWorldProperty) {
      public Lifecycle lifecycle() {
         return this.dimensions.registryLifecycle();
      }

      public RegistryAccess.Frozen dimensionsRegistryAccess() {
         return new RegistryAccess.ImmutableRegistryAccess(List.of(this.dimensions)).freeze();
      }
   }
}
