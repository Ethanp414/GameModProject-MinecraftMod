package net.minecraft.world.entity.animal.wolf;

import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class WolfVariants {
   public static final ResourceKey<WolfVariant> PALE = createKey("pale");
   public static final ResourceKey<WolfVariant> SPOTTED = createKey("spotted");
   public static final ResourceKey<WolfVariant> SNOWY = createKey("snowy");
   public static final ResourceKey<WolfVariant> BLACK = createKey("black");
   public static final ResourceKey<WolfVariant> ASHEN = createKey("ashen");
   public static final ResourceKey<WolfVariant> RUSTY = createKey("rusty");
   public static final ResourceKey<WolfVariant> WOODS = createKey("woods");
   public static final ResourceKey<WolfVariant> CHESTNUT = createKey("chestnut");
   public static final ResourceKey<WolfVariant> STRIPED = createKey("striped");
   public static final ResourceKey<WolfVariant> DEFAULT = PALE;

   private static ResourceKey<WolfVariant> createKey(String $$0) {
      return ResourceKey.create(Registries.WOLF_VARIANT, ResourceLocation.withDefaultNamespace($$0));
   }

   private static void register(BootstrapContext<WolfVariant> $$0, ResourceKey<WolfVariant> $$1, String $$2, ResourceKey<Biome> $$3) {
      register($$0, $$1, $$2, highPrioBiome(HolderSet.direct($$0.lookup(Registries.BIOME).getOrThrow($$3))));
   }

   private static void register(BootstrapContext<WolfVariant> $$0, ResourceKey<WolfVariant> $$1, String $$2, TagKey<Biome> $$3) {
      register($$0, $$1, $$2, highPrioBiome($$0.lookup(Registries.BIOME).getOrThrow($$3)));
   }

   private static SpawnPrioritySelectors highPrioBiome(HolderSet<Biome> $$0) {
      return SpawnPrioritySelectors.single(new BiomeCheck($$0), 1);
   }

   private static void register(BootstrapContext<WolfVariant> $$0, ResourceKey<WolfVariant> $$1, String $$2, SpawnPrioritySelectors $$3) {
      ResourceLocation $$4 = ResourceLocation.withDefaultNamespace("entity/wolf/" + $$2);
      ResourceLocation $$5 = ResourceLocation.withDefaultNamespace("entity/wolf/" + $$2 + "_tame");
      ResourceLocation $$6 = ResourceLocation.withDefaultNamespace("entity/wolf/" + $$2 + "_angry");
      $$0.register(
         $$1,
         new WolfVariant(
            new WolfVariant.AssetInfo(new ClientAsset.ResourceTexture($$4), new ClientAsset.ResourceTexture($$5), new ClientAsset.ResourceTexture($$6)), $$3
         )
      );
   }

   public static void bootstrap(BootstrapContext<WolfVariant> $$0) {
      register($$0, PALE, "wolf", SpawnPrioritySelectors.fallback(0));
      register($$0, SPOTTED, "wolf_spotted", BiomeTags.IS_SAVANNA);
      register($$0, SNOWY, "wolf_snowy", Biomes.GROVE);
      register($$0, BLACK, "wolf_black", Biomes.OLD_GROWTH_PINE_TAIGA);
      register($$0, ASHEN, "wolf_ashen", Biomes.SNOWY_TAIGA);
      register($$0, RUSTY, "wolf_rusty", BiomeTags.IS_JUNGLE);
      register($$0, WOODS, "wolf_woods", Biomes.FOREST);
      register($$0, CHESTNUT, "wolf_chestnut", Biomes.OLD_GROWTH_SPRUCE_TAIGA);
      register($$0, STRIPED, "wolf_striped", BiomeTags.IS_BADLANDS);
   }
}
