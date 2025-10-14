package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.ChickenVariant;
import net.minecraft.world.entity.animal.CowVariant;
import net.minecraft.world.entity.animal.PigVariant;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.slf4j.Logger;

public class RegistryDataLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Comparator<ResourceKey<?>> ERROR_KEY_COMPARATOR = Comparator.comparing(ResourceKey::registry).thenComparing(ResourceKey::location);
   private static final RegistrationInfo NETWORK_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());
   private static final Function<Optional<KnownPack>, RegistrationInfo> REGISTRATION_INFO_CACHE = Util.memoize((Function)($$0 -> {
      Lifecycle $$1 = (Lifecycle)$$0.map(KnownPack::isVanilla).map($$0x -> Lifecycle.stable()).orElse(Lifecycle.experimental());
      return new RegistrationInfo($$0, $$1);
   }));
   public static final List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES = List.of(
      new RegistryDataLoader.RegistryData(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.BIOME, Biome.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.STRUCTURE, Structure.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.NOISE, NormalNoise.NoiseParameters.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.TRIAL_SPAWNER_CONFIG, TrialSpawnerConfig.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.WOLF_VARIANT, WolfVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData(Registries.WOLF_SOUND_VARIANT, WolfSoundVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.PIG_VARIANT, PigVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.FROG_VARIANT, FrogVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.CAT_VARIANT, CatVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.COW_VARIANT, CowVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.CHICKEN_VARIANT, ChickenVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.ENCHANTMENT_PROVIDER, EnchantmentProvider.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.INSTRUMENT, Instrument.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TEST_ENVIRONMENT, TestEnvironmentDefinition.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TEST_INSTANCE, GameTestInstance.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.DIALOG, Dialog.DIRECT_CODEC)
   );
   public static final List<RegistryDataLoader.RegistryData<?>> DIMENSION_REGISTRIES = List.of(
      new RegistryDataLoader.RegistryData(Registries.LEVEL_STEM, LevelStem.CODEC)
   );
   public static final List<RegistryDataLoader.RegistryData<?>> SYNCHRONIZED_REGISTRIES = List.of(
      new RegistryDataLoader.RegistryData<>(Registries.BIOME, Biome.NETWORK_CODEC),
      new RegistryDataLoader.RegistryData(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.WOLF_VARIANT, WolfVariant.NETWORK_CODEC, true),
      new RegistryDataLoader.RegistryData(Registries.WOLF_SOUND_VARIANT, WolfSoundVariant.NETWORK_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.PIG_VARIANT, PigVariant.NETWORK_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.FROG_VARIANT, FrogVariant.NETWORK_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.CAT_VARIANT, CatVariant.NETWORK_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.COW_VARIANT, CowVariant.NETWORK_CODEC, true),
      new RegistryDataLoader.RegistryData<>(Registries.CHICKEN_VARIANT, ChickenVariant.NETWORK_CODEC, true),
      new RegistryDataLoader.RegistryData(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, true),
      new RegistryDataLoader.RegistryData(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData(Registries.INSTRUMENT, Instrument.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TEST_ENVIRONMENT, TestEnvironmentDefinition.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.TEST_INSTANCE, GameTestInstance.DIRECT_CODEC),
      new RegistryDataLoader.RegistryData<>(Registries.DIALOG, Dialog.DIRECT_CODEC)
   );

   public static RegistryAccess.Frozen load(ResourceManager $$0, List<HolderLookup.RegistryLookup<?>> $$1, List<RegistryDataLoader.RegistryData<?>> $$2) {
      return load(($$1x, $$2x) -> $$1x.loadFromResources($$0, $$2x), $$1, $$2);
   }

   public static RegistryAccess.Frozen load(
      Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> $$0,
      ResourceProvider $$1,
      List<HolderLookup.RegistryLookup<?>> $$2,
      List<RegistryDataLoader.RegistryData<?>> $$3
   ) {
      return load(($$2x, $$3x) -> $$2x.loadFromNetwork($$0, $$1, $$3x), $$2, $$3);
   }

   private static RegistryAccess.Frozen load(
      RegistryDataLoader.LoadingFunction $$0, List<HolderLookup.RegistryLookup<?>> $$1, List<RegistryDataLoader.RegistryData<?>> $$2
   ) {
      Map<ResourceKey<?>, Exception> $$3 = new HashMap();
      List<RegistryDataLoader.Loader<?>> $$4 = (List)$$2.stream().map($$1x -> $$1x.create(Lifecycle.stable(), $$3)).collect(Collectors.toUnmodifiableList());
      RegistryOps.RegistryInfoLookup $$5 = createContext($$1, $$4);
      $$4.forEach($$2x -> $$0.apply($$2x, $$5));
      $$4.forEach($$1x -> {
         Registry<?> $$2xx = $$1x.registry();

         try {
            $$2xx.freeze();
         } catch (Exception var4xx) {
            $$3.put($$2xx.key(), var4xx);
         }

         if ($$1x.data.requiredNonEmpty && $$2xx.size() == 0) {
            $$3.put($$2xx.key(), new IllegalStateException("Registry must be non-empty: " + $$2xx.key().location()));
         }
      });
      if (!$$3.isEmpty()) {
         throw logErrors($$3);
      } else {
         return new RegistryAccess.ImmutableRegistryAccess($$4.stream().map(RegistryDataLoader.Loader::registry).toList()).freeze();
      }
   }

   private static RegistryOps.RegistryInfoLookup createContext(List<HolderLookup.RegistryLookup<?>> $$0, List<RegistryDataLoader.Loader<?>> $$1) {
      final Map<ResourceKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> $$2 = new HashMap();
      $$0.forEach($$1x -> $$2.put($$1x.key(), createInfoForContextRegistry($$1x)));
      $$1.forEach($$1x -> $$2.put($$1x.registry.key(), createInfoForNewRegistry($$1x.registry)));
      return new RegistryOps.RegistryInfoLookup() {
         @Override
         public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> $$0) {
            return Optional.ofNullable((RegistryOps.RegistryInfo)$$2.get($$0));
         }
      };
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForNewRegistry(WritableRegistry<T> $$0) {
      return new RegistryOps.RegistryInfo<>($$0, $$0.createRegistrationLookup(), $$0.registryLifecycle());
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForContextRegistry(HolderLookup.RegistryLookup<T> $$0) {
      return new RegistryOps.RegistryInfo<>($$0, $$0, $$0.registryLifecycle());
   }

   private static ReportedException logErrors(Map<ResourceKey<?>, Exception> $$0) {
      printFullDetailsToLog($$0);
      return createReportWithBriefInfo($$0);
   }

   private static void printFullDetailsToLog(Map<ResourceKey<?>, Exception> $$0) {
      StringWriter $$1 = new StringWriter();
      PrintWriter $$2 = new PrintWriter($$1);
      Map<ResourceLocation, Map<ResourceLocation, Exception>> $$3 = (Map)$$0.entrySet()
         .stream()
         .collect(
            Collectors.groupingBy(
               $$0x -> ((ResourceKey)$$0x.getKey()).registry(), Collectors.toMap($$0x -> ((ResourceKey)$$0x.getKey()).location(), Entry::getValue)
            )
         );
      $$3.entrySet().stream().sorted(Entry.comparingByKey()).forEach($$1x -> {
         $$2.printf("> Errors in registry %s:%n", $$1x.getKey());
         ((Map)$$1x.getValue()).entrySet().stream().sorted(Entry.comparingByKey()).forEach($$1xx -> {
            $$2.printf(">> Errors in element %s:%n", $$1xx.getKey());
            ((Exception)$$1xx.getValue()).printStackTrace($$2);
         });
      });
      $$2.flush();
      LOGGER.error("Registry loading errors:\n{}", $$1);
   }

   private static ReportedException createReportWithBriefInfo(Map<ResourceKey<?>, Exception> $$0) {
      CrashReport $$1 = CrashReport.forThrowable(new IllegalStateException("Failed to load registries due to errors"), "Registry Loading");
      CrashReportCategory $$2 = $$1.addCategory("Loading info");
      $$2.setDetail(
         "Errors",
         (CrashReportDetail<String>)(() -> {
            StringBuilder $$1xx = new StringBuilder();
            $$0.entrySet()
               .stream()
               .sorted(Entry.comparingByKey(ERROR_KEY_COMPARATOR))
               .forEach(
                  $$1xx -> $$1x.append("\n\t\t")
                        .append(((ResourceKey)$$1xx.getKey()).registry())
                        .append("/")
                        .append(((ResourceKey)$$1xx.getKey()).location())
                        .append(": ")
                        .append(((Exception)$$1xx.getValue()).getMessage())
               );
            return $$1xx.toString();
         })
      );
      return new ReportedException($$1);
   }

   private static <E> void loadElementFromResource(
      WritableRegistry<E> $$0, Decoder<E> $$1, RegistryOps<JsonElement> $$2, ResourceKey<E> $$3, Resource $$4, RegistrationInfo $$5
   ) throws IOException {
      Reader $$6 = $$4.openAsReader();

      try {
         JsonElement $$7 = StrictJsonParser.parse($$6);
         DataResult<E> $$8 = $$1.parse($$2, $$7);
         E $$9 = $$8.getOrThrow();
         $$0.register($$3, $$9, $$5);
      } catch (Throwable var11) {
         if ($$6 != null) {
            try {
               $$6.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if ($$6 != null) {
         $$6.close();
      }
   }

   static <E> void loadContentsFromManager(
      ResourceManager $$0, RegistryOps.RegistryInfoLookup $$1, WritableRegistry<E> $$2, Decoder<E> $$3, Map<ResourceKey<?>, Exception> $$4
   ) {
      FileToIdConverter $$5 = FileToIdConverter.registry($$2.key());
      RegistryOps<JsonElement> $$6 = RegistryOps.create(JsonOps.INSTANCE, $$1);

      for(Entry<ResourceLocation, Resource> $$7 : $$5.listMatchingResources($$0).entrySet()) {
         ResourceLocation $$8 = (ResourceLocation)$$7.getKey();
         ResourceKey<E> $$9 = ResourceKey.create($$2.key(), $$5.fileToId($$8));
         Resource $$10 = (Resource)$$7.getValue();
         RegistrationInfo $$11 = (RegistrationInfo)REGISTRATION_INFO_CACHE.apply($$10.knownPackInfo());

         try {
            loadElementFromResource($$2, $$3, $$6, $$9, $$10, $$11);
         } catch (Exception var14) {
            $$4.put($$9, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", $$8, $$10.sourcePackId()), var14));
         }
      }

      TagLoader.loadTagsForRegistry($$0, $$2);
   }

   static <E> void loadContentsFromNetwork(
      Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> $$0,
      ResourceProvider $$1,
      RegistryOps.RegistryInfoLookup $$2,
      WritableRegistry<E> $$3,
      Decoder<E> $$4,
      Map<ResourceKey<?>, Exception> $$5
   ) {
      RegistryDataLoader.NetworkedRegistryData $$6 = (RegistryDataLoader.NetworkedRegistryData)$$0.get($$3.key());
      if ($$6 != null) {
         RegistryOps<Tag> $$7 = RegistryOps.create(NbtOps.INSTANCE, $$2);
         RegistryOps<JsonElement> $$8 = RegistryOps.create(JsonOps.INSTANCE, $$2);
         FileToIdConverter $$9 = FileToIdConverter.registry($$3.key());

         for(RegistrySynchronization.PackedRegistryEntry $$10 : $$6.elements) {
            ResourceKey<E> $$11 = ResourceKey.create($$3.key(), $$10.id());
            Optional<Tag> $$12 = $$10.data();
            if ($$12.isPresent()) {
               try {
                  DataResult<E> $$13 = $$4.parse($$7, (Tag)$$12.get());
                  E $$14 = $$13.getOrThrow();
                  $$3.register($$11, $$14, NETWORK_REGISTRATION_INFO);
               } catch (Exception var16) {
                  $$5.put($$11, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse value %s from server", $$12.get()), var16));
               }
            } else {
               ResourceLocation $$16 = $$9.idToFile($$10.id());

               try {
                  Resource $$17 = $$1.getResourceOrThrow($$16);
                  loadElementFromResource($$3, $$4, $$8, $$11, $$17, NETWORK_REGISTRATION_INFO);
               } catch (Exception var17) {
                  $$5.put($$11, new IllegalStateException("Failed to parse local data", var17));
               }
            }
         }

         TagLoader.loadTagsFromNetwork($$6.tags, $$3);
      }
   }

   static record Loader<T>(RegistryDataLoader.RegistryData<T> data, WritableRegistry<T> registry, Map<ResourceKey<?>, Exception> loadingErrors) {
      final RegistryDataLoader.RegistryData<T> data;
      final WritableRegistry<T> registry;

      public void loadFromResources(ResourceManager $$0, RegistryOps.RegistryInfoLookup $$1) {
         RegistryDataLoader.loadContentsFromManager($$0, $$1, this.registry, this.data.elementCodec, this.loadingErrors);
      }

      public void loadFromNetwork(
         Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> $$0, ResourceProvider $$1, RegistryOps.RegistryInfoLookup $$2
      ) {
         RegistryDataLoader.loadContentsFromNetwork($$0, $$1, $$2, this.registry, this.data.elementCodec, this.loadingErrors);
      }
   }

   @FunctionalInterface
   interface LoadingFunction {
      void apply(RegistryDataLoader.Loader<?> var1, RegistryOps.RegistryInfoLookup var2);
   }

   public static record NetworkedRegistryData(List<RegistrySynchronization.PackedRegistryEntry> elements, TagNetworkSerialization.NetworkPayload tags) {
      final List<RegistrySynchronization.PackedRegistryEntry> elements;
      final TagNetworkSerialization.NetworkPayload tags;
   }

   public static record RegistryData<T>(ResourceKey<? extends Registry<T>> key, Codec<T> elementCodec, boolean requiredNonEmpty) {
      final Codec<T> elementCodec;
      final boolean requiredNonEmpty;

      RegistryData(ResourceKey<? extends Registry<T>> $$0, Codec<T> $$1) {
         this($$0, $$1, false);
      }

      RegistryDataLoader.Loader<T> create(Lifecycle $$0, Map<ResourceKey<?>, Exception> $$1) {
         WritableRegistry<T> $$2 = new MappedRegistry<>(this.key, $$0);
         return new RegistryDataLoader.Loader<>(this, $$2, $$1);
      }

      public void runWithArguments(BiConsumer<ResourceKey<? extends Registry<T>>, Codec<T>> $$0) {
         $$0.accept(this.key, this.elementCodec);
      }
   }
}
