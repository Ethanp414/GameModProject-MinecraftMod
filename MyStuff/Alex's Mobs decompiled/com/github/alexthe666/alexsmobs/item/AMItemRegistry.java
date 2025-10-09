package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroachEgg;
import com.github.alexthe666.alexsmobs.entity.EntityEmuEgg;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophageRocket;
import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.block.LecternBooks.BookData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMItemRegistry {
   public static final AMArmorMaterial ROADRUNNER_ARMOR_MATERIAL = new AMArmorMaterial("roadrunner", 18, new int[]{3, 3, 3, 3}, 20, SoundEvents.f_11680_, 0.0F);
   public static final AMArmorMaterial CROCODILE_ARMOR_MATERIAL = new AMArmorMaterial("crocodile", 22, new int[]{2, 5, 7, 3}, 25, SoundEvents.f_11680_, 1.0F);
   public static final AMArmorMaterial CENTIPEDE_ARMOR_MATERIAL = new AMArmorMaterial("centipede", 20, new int[]{6, 6, 6, 6}, 22, SoundEvents.f_11680_, 0.5F);
   public static final AMArmorMaterial MOOSE_ARMOR_MATERIAL = new AMArmorMaterial("moose", 19, new int[]{3, 3, 3, 3}, 21, SoundEvents.f_11680_, 0.5F);
   public static final AMArmorMaterial RACCOON_ARMOR_MATERIAL = new AMArmorMaterial("raccoon", 17, new int[]{3, 3, 3, 3}, 21, SoundEvents.f_11678_, 2.5F);
   public static final AMArmorMaterial SOMBRERO_ARMOR_MATERIAL = new AMArmorMaterial("sombrero", 14, new int[]{2, 2, 2, 2}, 30, SoundEvents.f_11678_, 0.5F);
   public static final AMArmorMaterial SPIKED_TURTLE_SHELL_ARMOR_MATERIAL = new AMArmorMaterial(
      "spiked_turtle_shell", 35, new int[]{3, 3, 3, 3}, 30, SoundEvents.f_11680_, 1.0F, 0.2F
   );
   public static final AMArmorMaterial FEDORA_ARMOR_MATERIAL = new AMArmorMaterial("fedora", 10, new int[]{2, 2, 2, 2}, 30, SoundEvents.f_11678_, 0.5F);
   public static final AMArmorMaterial EMU_ARMOR_MATERIAL = new AMArmorMaterial("emu", 9, new int[]{4, 4, 4, 4}, 20, SoundEvents.f_11678_, 0.5F);
   public static final AMArmorMaterial TARANTULA_HAWK_ELYTRA_MATERIAL = new AMArmorMaterial(
      "tarantula_hawk_elytra", 9, new int[]{3, 3, 3, 3}, 5, SoundEvents.f_11678_, 0.0F
   );
   public static final AMArmorMaterial FROSTSTALKER_ARMOR_MATERIAL = new AMArmorMaterial(
      "froststalker", 9, new int[]{3, 3, 3, 3}, 15, SoundEvents.f_11678_, 0.5F
   );
   public static final AMArmorMaterial ROCKY_ARMOR_MATERIAL = new AMArmorMaterial("rocky_roller", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.f_11680_, 0.5F);
   public static final AMArmorMaterial FLYING_FISH_MATERIAL = new AMArmorMaterial("flying_fish", 9, new int[]{1, 1, 1, 1}, 8, SoundEvents.f_11678_, 0.0F);
   public static final AMArmorMaterial NOVELTY_HAT_MATERIAL = new AMArmorMaterial("novelty_hat", 10, new int[]{2, 2, 2, 2}, 30, SoundEvents.f_11678_, 0.0F);
   public static final AMArmorMaterial KIMONO_MATERIAL = new AMArmorMaterial("kimono", 8, new int[]{3, 3, 3, 3}, 15, SoundEvents.f_11678_, 0.0F);
   public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, "alexsmobs");
   public static final RegistryObject<Item> TAB_ICON = DEF_REG.register("tab_icon", () -> new ItemTabIcon(new Properties()));
   public static final RegistryObject<Item> ANIMAL_DICTIONARY = DEF_REG.register(
      "animal_dictionary", () -> new ItemAnimalDictionary(new Properties().m_41487_(1))
   );
   public static final RegistryObject<Item> BEAR_FUR = DEF_REG.register("bear_fur", () -> new Item(new Properties()));
   public static final RegistryObject<Item> BEAR_DUST = DEF_REG.register("bear_dust", () -> new ItemBearDust(new Properties().m_41497_(Rarity.EPIC)));
   public static final RegistryObject<Item> ROADRUNNER_FEATHER = DEF_REG.register("roadrunner_feather", () -> new Item(new Properties()));
   public static final RegistryObject<Item> ROADDRUNNER_BOOTS = DEF_REG.register(
      "roadrunner_boots", () -> new ItemModArmor(ROADRUNNER_ARMOR_MATERIAL, Type.BOOTS)
   );
   public static final RegistryObject<Item> LAVA_BOTTLE = DEF_REG.register("lava_bottle", () -> new Item(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> BONE_SERPENT_TOOTH = DEF_REG.register("bone_serpent_tooth", () -> new Item(new Properties().m_41486_()));
   public static final RegistryObject<Item> GAZELLE_HORN = DEF_REG.register("gazelle_horn", () -> new Item(new Properties().m_41486_()));
   public static final RegistryObject<Item> CROCODILE_SCUTE = DEF_REG.register("crocodile_scute", () -> new Item(new Properties()));
   public static final RegistryObject<Item> CROCODILE_CHESTPLATE = DEF_REG.register(
      "crocodile_chestplate", () -> new ItemModArmor(CROCODILE_ARMOR_MATERIAL, Type.CHESTPLATE)
   );
   public static final RegistryObject<Item> MAGGOT = DEF_REG.register(
      "maggot", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(1).m_38758_(0.2F).m_38767_()))
   );
   public static final RegistryObject<Item> BANANA = DEF_REG.register(
      "banana", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(4).m_38758_(0.3F).m_38767_()))
   );
   public static final RegistryObject<Item> ANCIENT_DART = DEF_REG.register(
      "ancient_dart", () -> new Item(new Properties().m_41487_(1).m_41497_(Rarity.UNCOMMON))
   );
   public static final RegistryObject<Item> HALO = DEF_REG.register("halo", () -> new ItemInventoryOnly(new Properties()));
   public static final RegistryObject<Item> BLOOD_SAC = DEF_REG.register("blood_sac", () -> new Item(new Properties()));
   public static final RegistryObject<Item> MOSQUITO_PROBOSCIS = DEF_REG.register("mosquito_proboscis", () -> new Item(new Properties()));
   public static final RegistryObject<Item> BLOOD_SPRAYER = DEF_REG.register("blood_sprayer", () -> new ItemBloodSprayer(new Properties().m_41503_(100)));
   public static final RegistryObject<Item> RATTLESNAKE_RATTLE = DEF_REG.register("rattlesnake_rattle", () -> new Item(new Properties()));
   public static final RegistryObject<Item> CHORUS_ON_A_STICK = DEF_REG.register("chorus_on_a_stick", () -> new Item(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> SHARK_TOOTH = DEF_REG.register("shark_tooth", () -> new Item(new Properties()));
   public static final RegistryObject<Item> SHARK_TOOTH_ARROW = DEF_REG.register("shark_tooth_arrow", () -> new ItemModArrow(new Properties()));
   public static final RegistryObject<Item> LOBSTER_TAIL = DEF_REG.register(
      "lobster_tail", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(2).m_38758_(0.4F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> COOKED_LOBSTER_TAIL = DEF_REG.register(
      "cooked_lobster_tail", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(6).m_38758_(0.65F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> LOBSTER_BUCKET = DEF_REG.register(
      "lobster_bucket", () -> new ItemModFishBucket(AMEntityRegistry.LOBSTER, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> KOMODO_SPIT = DEF_REG.register("komodo_spit", () -> new Item(new Properties()));
   public static final RegistryObject<Item> KOMODO_SPIT_BOTTLE = DEF_REG.register("komodo_spit_bottle", () -> new Item(new Properties()));
   public static final RegistryObject<Item> POISON_BOTTLE = DEF_REG.register("poison_bottle", () -> new Item(new Properties()));
   public static final RegistryObject<Item> SOPA_DE_MACACO = DEF_REG.register(
      "sopa_de_macaco", () -> new BowlFoodItem(new Properties().m_41489_(new Builder().m_38760_(5).m_38758_(0.4F).m_38757_().m_38767_()).m_41487_(1))
   );
   public static final RegistryObject<Item> CENTIPEDE_LEG = DEF_REG.register("centipede_leg", () -> new Item(new Properties()));
   public static final RegistryObject<Item> CENTIPEDE_LEGGINGS = DEF_REG.register(
      "centipede_leggings", () -> new ItemModArmor(CENTIPEDE_ARMOR_MATERIAL, Type.LEGGINGS)
   );
   public static final RegistryObject<Item> MOSQUITO_LARVA = DEF_REG.register("mosquito_larva", () -> new Item(new Properties()));
   public static final RegistryObject<Item> MOOSE_ANTLER = DEF_REG.register("moose_antler", () -> new Item(new Properties()));
   public static final RegistryObject<Item> MOOSE_HEADGEAR = DEF_REG.register("moose_headgear", () -> new ItemModArmor(MOOSE_ARMOR_MATERIAL, Type.HELMET));
   public static final RegistryObject<Item> MOOSE_RIBS = DEF_REG.register(
      "moose_ribs", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(3).m_38758_(0.6F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> COOKED_MOOSE_RIBS = DEF_REG.register(
      "cooked_moose_ribs", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(7).m_38758_(0.85F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> MIMICREAM = DEF_REG.register("mimicream", () -> new Item(new Properties()));
   public static final RegistryObject<Item> RACCOON_TAIL = DEF_REG.register("raccoon_tail", () -> new Item(new Properties()));
   public static final RegistryObject<Item> FRONTIER_CAP = DEF_REG.register("frontier_cap", () -> new ItemModArmor(RACCOON_ARMOR_MATERIAL, Type.HELMET));
   public static final RegistryObject<Item> BLOBFISH = DEF_REG.register(
      "blobfish",
      () -> new Item(
         new Properties()
            .m_41489_(new Builder().m_38760_(3).m_38758_(0.4F).m_38757_().m_38762_(new MobEffectInstance(MobEffects.f_19614_, 120, 0), 1.0F).m_38767_())
      )
   );
   public static final RegistryObject<Item> BLOBFISH_BUCKET = DEF_REG.register(
      "blobfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.BLOBFISH, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> FISH_OIL = DEF_REG.register(
      "fish_oil", () -> new ItemFishOil(new Properties().m_41495_(Items.f_42590_).m_41489_(new Builder().m_38760_(0).m_38758_(0.2F).m_38767_()))
   );
   public static final RegistryObject<Item> MARACA = DEF_REG.register("maraca", () -> new ItemMaraca(new Properties()));
   public static final RegistryObject<Item> SOMBRERO = DEF_REG.register("sombrero", () -> new ItemModArmor(SOMBRERO_ARMOR_MATERIAL, Type.HELMET));
   public static final RegistryObject<Item> COCKROACH_WING_FRAGMENT = DEF_REG.register("cockroach_wing_fragment", () -> new Item(new Properties()));
   public static final RegistryObject<Item> COCKROACH_WING = DEF_REG.register("cockroach_wing", () -> new Item(new Properties()));
   public static final RegistryObject<Item> COCKROACH_OOTHECA = DEF_REG.register("cockroach_ootheca", () -> new ItemAnimalEgg(new Properties()));
   public static final RegistryObject<Item> ACACIA_BLOSSOM = DEF_REG.register("acacia_blossom", () -> new Item(new Properties()));
   public static final RegistryObject<Item> SOUL_HEART = DEF_REG.register("soul_heart", () -> new Item(new Properties()));
   public static final RegistryObject<Item> SPIKED_SCUTE = DEF_REG.register("spiked_scute", () -> new Item(new Properties()));
   public static final RegistryObject<Item> SPIKED_TURTLE_SHELL = DEF_REG.register(
      "spiked_turtle_shell", () -> new ItemModArmor(SPIKED_TURTLE_SHELL_ARMOR_MATERIAL, Type.HELMET)
   );
   public static final RegistryObject<Item> SHRIMP_FRIED_RICE = DEF_REG.register(
      "shrimp_fried_rice", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(12).m_38758_(1.0F).m_38767_()))
   );
   public static final RegistryObject<Item> GUSTER_EYE = DEF_REG.register("guster_eye", () -> new Item(new Properties()));
   public static final RegistryObject<Item> POCKET_SAND = DEF_REG.register("pocket_sand", () -> new ItemPocketSand(new Properties().m_41503_(220)));
   public static final RegistryObject<Item> WARPED_MUSCLE = DEF_REG.register("warped_muscle", () -> new Item(new Properties()));
   public static final RegistryObject<Item> HEMOLYMPH_SAC = DEF_REG.register("hemolymph_sac", () -> new Item(new Properties()));
   public static final RegistryObject<Item> HEMOLYMPH_BLASTER = DEF_REG.register(
      "hemolymph_blaster", () -> new ItemHemolymphBlaster(new Properties().m_41503_(150))
   );
   public static final RegistryObject<Item> WARPED_MIXTURE = DEF_REG.register(
      "warped_mixture", () -> new Item(new Properties().m_41497_(Rarity.RARE).m_41487_(1).m_41495_(Items.f_42590_))
   );
   public static final RegistryObject<Item> STRADDLITE = DEF_REG.register("straddlite", () -> new Item(new Properties().m_41486_()));
   public static final RegistryObject<Item> STRADPOLE_BUCKET = DEF_REG.register(
      "stradpole_bucket", () -> new ItemModFishBucket(AMEntityRegistry.STRADPOLE, Fluids.f_76195_, new Properties())
   );
   public static final RegistryObject<Item> STRADDLEBOARD = DEF_REG.register(
      "straddleboard", () -> new ItemStraddleboard(new Properties().m_41486_().m_41503_(220))
   );
   public static final RegistryObject<Item> EMU_EGG = DEF_REG.register("emu_egg", () -> new ItemAnimalEgg(new Properties().m_41487_(8)));
   public static final RegistryObject<Item> BOILED_EMU_EGG = DEF_REG.register(
      "boiled_emu_egg", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(4).m_38758_(1.0F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> EMU_FEATHER = DEF_REG.register("emu_feather", () -> new Item(new Properties().m_41486_()));
   public static final RegistryObject<Item> EMU_LEGGINGS = DEF_REG.register("emu_leggings", () -> new ItemModArmor(EMU_ARMOR_MATERIAL, Type.LEGGINGS));
   public static final RegistryObject<Item> PLATYPUS_BUCKET = DEF_REG.register(
      "platypus_bucket", () -> new ItemModFishBucket(AMEntityRegistry.PLATYPUS, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> FEDORA = DEF_REG.register("fedora", () -> new ItemModArmor(FEDORA_ARMOR_MATERIAL, Type.HELMET));
   public static final RegistryObject<Item> DROPBEAR_CLAW = DEF_REG.register("dropbear_claw", () -> new Item(new Properties()));
   public static final RegistryObject<Item> KANGAROO_MEAT = DEF_REG.register(
      "kangaroo_meat", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(4).m_38758_(0.6F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> COOKED_KANGAROO_MEAT = DEF_REG.register(
      "cooked_kangaroo_meat", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(8).m_38758_(0.85F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> KANGAROO_HIDE = DEF_REG.register("kangaroo_hide", () -> new Item(new Properties()));
   public static final RegistryObject<Item> KANGAROO_BURGER = DEF_REG.register(
      "kangaroo_burger", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(12).m_38758_(1.0F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> AMBERGRIS = DEF_REG.register("ambergris", () -> new ItemFuel(new Properties(), 12800));
   public static final RegistryObject<Item> CACHALOT_WHALE_TOOTH = DEF_REG.register("cachalot_whale_tooth", () -> new Item(new Properties()));
   public static final RegistryObject<Item> ECHOLOCATOR = DEF_REG.register(
      "echolocator", () -> new ItemEcholocator(new Properties().m_41503_(100), ItemEcholocator.EchoType.ECHOLOCATION)
   );
   public static final RegistryObject<Item> ENDOLOCATOR = DEF_REG.register(
      "endolocator", () -> new ItemEcholocator(new Properties().m_41503_(25), ItemEcholocator.EchoType.ENDER)
   );
   public static final RegistryObject<Item> GONGYLIDIA = DEF_REG.register(
      "gongylidia", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(3).m_38758_(1.2F).m_38767_()))
   );
   public static final RegistryObject<Item> LEAFCUTTER_ANT_PUPA = DEF_REG.register("leafcutter_ant_pupa", () -> new ItemLeafcutterPupa(new Properties()));
   public static final RegistryObject<Item> ENDERIOPHAGE_ROCKET = DEF_REG.register("enderiophage_rocket", () -> new ItemEnderiophageRocket(new Properties()));
   public static final RegistryObject<Item> FALCONRY_GLOVE_INVENTORY = DEF_REG.register(
      "falconry_glove_inventory", () -> new ItemInventoryOnly(new Properties())
   );
   public static final RegistryObject<Item> FALCONRY_GLOVE_HAND = DEF_REG.register("falconry_glove_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final RegistryObject<Item> FALCONRY_GLOVE = DEF_REG.register("falconry_glove", () -> new ItemFalconryGlove(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> FALCONRY_HOOD = DEF_REG.register("falconry_hood", () -> new Item(new Properties()));
   public static final RegistryObject<Item> TARANTULA_HAWK_WING_FRAGMENT = DEF_REG.register("tarantula_hawk_wing_fragment", () -> new Item(new Properties()));
   public static final RegistryObject<Item> TARANTULA_HAWK_WING = DEF_REG.register("tarantula_hawk_wing", () -> new Item(new Properties()));
   public static final RegistryObject<Item> TARANTULA_HAWK_ELYTRA = DEF_REG.register(
      "tarantula_hawk_elytra", () -> new ItemTarantulaHawkElytra(new Properties().m_41503_(800).m_41497_(Rarity.UNCOMMON), TARANTULA_HAWK_ELYTRA_MATERIAL)
   );
   public static final RegistryObject<Item> MYSTERIOUS_WORM = DEF_REG.register(
      "mysterious_worm", () -> new ItemMysteriousWorm(new Properties().m_41497_(Rarity.RARE))
   );
   public static final RegistryObject<Item> VOID_WORM_MANDIBLE = DEF_REG.register("void_worm_mandible", () -> new Item(new Properties()));
   public static final RegistryObject<Item> VOID_WORM_EYE = DEF_REG.register("void_worm_eye", () -> new Item(new Properties().m_41497_(Rarity.RARE)));
   public static final RegistryObject<Item> DIMENSIONAL_CARVER = DEF_REG.register(
      "dimensional_carver", () -> new ItemDimensionalCarver(new Properties().m_41503_(20).m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SHATTERED_DIMENSIONAL_CARVER = DEF_REG.register(
      "shattered_dimensional_carver", () -> new ItemShatteredDimensionalCarver(new Properties().m_41503_(4).m_41497_(Rarity.RARE))
   );
   public static final RegistryObject<Item> SERRATED_SHARK_TOOTH = DEF_REG.register("serrated_shark_tooth", () -> new Item(new Properties()));
   public static final RegistryObject<Item> FRILLED_SHARK_BUCKET = DEF_REG.register(
      "frilled_shark_bucket", () -> new ItemModFishBucket(AMEntityRegistry.FRILLED_SHARK, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> SHIELD_OF_THE_DEEP = DEF_REG.register(
      "shield_of_the_deep", () -> new ItemShieldOfTheDeep(new Properties().m_41503_(400).m_41497_(Rarity.UNCOMMON))
   );
   public static final RegistryObject<Item> MIMIC_OCTOPUS_BUCKET = DEF_REG.register(
      "mimic_octopus_bucket", () -> new ItemModFishBucket(AMEntityRegistry.MIMIC_OCTOPUS, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> FROSTSTALKER_HORN = DEF_REG.register("froststalker_horn", () -> new Item(new Properties()));
   public static final RegistryObject<Item> FROSTSTALKER_HELMET = DEF_REG.register(
      "froststalker_helmet", () -> new ItemModArmor(FROSTSTALKER_ARMOR_MATERIAL, Type.HELMET)
   );
   public static final RegistryObject<Item> PIGSHOES = DEF_REG.register("pigshoes", () -> new ItemPigshoes(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> STRADDLE_HELMET = DEF_REG.register("straddle_helmet", () -> new Item(new Properties().m_41486_()));
   public static final RegistryObject<Item> STRADDLE_SADDLE = DEF_REG.register("straddle_saddle", () -> new Item(new Properties().m_41486_()));
   public static final RegistryObject<Item> COSMIC_COD = DEF_REG.register(
      "cosmic_cod",
      () -> new Item(
         new Properties()
            .m_41489_(
               new Builder().m_38760_(6).m_38758_(0.3F).m_38762_(new MobEffectInstance((MobEffect)AMEffectRegistry.ENDER_FLU.get(), 12000), 0.15F).m_38767_()
            )
      )
   );
   public static final RegistryObject<Item> SHED_SNAKE_SKIN = DEF_REG.register("shed_snake_skin", () -> new Item(new Properties()));
   public static final RegistryObject<Item> VINE_LASSO_INVENTORY = DEF_REG.register("vine_lasso_inventory", () -> new ItemInventoryOnly(new Properties()));
   public static final RegistryObject<Item> VINE_LASSO_HAND = DEF_REG.register("vine_lasso_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final RegistryObject<Item> VINE_LASSO = DEF_REG.register("vine_lasso", () -> new ItemVineLasso(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> ROCKY_SHELL = DEF_REG.register("rocky_shell", () -> new Item(new Properties()));
   public static final RegistryObject<Item> ROCKY_CHESTPLATE = DEF_REG.register(
      "rocky_chestplate", () -> new ItemModArmor(ROCKY_ARMOR_MATERIAL, Type.CHESTPLATE)
   );
   public static final RegistryObject<Item> POTTED_FLUTTER = DEF_REG.register("potted_flutter", () -> new ItemFlutterPot(new Properties()));
   public static final RegistryObject<Item> TERRAPIN_BUCKET = DEF_REG.register(
      "terrapin_bucket", () -> new ItemModFishBucket(AMEntityRegistry.TERRAPIN, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> COMB_JELLY_BUCKET = DEF_REG.register(
      "comb_jelly_bucket", () -> new ItemModFishBucket(AMEntityRegistry.COMB_JELLY, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> RAINBOW_JELLY = DEF_REG.register(
      "rainbow_jelly", () -> new ItemRainbowJelly(new Properties().m_41489_(new Builder().m_38760_(1).m_38758_(0.2F).m_38767_()))
   );
   public static final RegistryObject<Item> COSMIC_COD_BUCKET = DEF_REG.register("cosmic_cod_bucket", () -> new ItemCosmicCodBucket(new Properties()));
   public static final RegistryObject<Item> MUNGAL_SPORES = DEF_REG.register("mungal_spores", () -> new Item(new Properties()));
   public static final RegistryObject<Item> BISON_FUR = DEF_REG.register("bison_fur", () -> new Item(new Properties()));
   public static final RegistryObject<Item> LOST_TENTACLE = DEF_REG.register("lost_tentacle", () -> new Item(new Properties()));
   public static final RegistryObject<Item> SQUID_GRAPPLE = DEF_REG.register("squid_grapple", () -> new ItemSquidGrapple(new Properties().m_41503_(450)));
   public static final RegistryObject<Item> DEVILS_HOLE_PUPFISH_BUCKET = DEF_REG.register(
      "devils_hole_pupfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.DEVILS_HOLE_PUPFISH, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> PUPFISH_LOCATOR = DEF_REG.register(
      "pupfish_locator", () -> new ItemEcholocator(new Properties().m_41503_(200), ItemEcholocator.EchoType.PUPFISH)
   );
   public static final RegistryObject<Item> SMALL_CATFISH_BUCKET = DEF_REG.register(
      "small_catfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.CATFISH, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> MEDIUM_CATFISH_BUCKET = DEF_REG.register(
      "medium_catfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.CATFISH, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> LARGE_CATFISH_BUCKET = DEF_REG.register(
      "large_catfish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.CATFISH, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> RAW_CATFISH = DEF_REG.register(
      "raw_catfish", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(2).m_38758_(0.3F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> COOKED_CATFISH = DEF_REG.register(
      "cooked_catfish", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(5).m_38758_(0.5F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> FLYING_FISH = DEF_REG.register(
      "flying_fish", () -> new Item(new Properties().m_41489_(new Builder().m_38760_(3).m_38758_(0.4F).m_38757_().m_38767_()))
   );
   public static final RegistryObject<Item> FLYING_FISH_BOOTS = DEF_REG.register("flying_fish_boots", () -> new ItemModArmor(FLYING_FISH_MATERIAL, Type.BOOTS));
   public static final RegistryObject<Item> FLYING_FISH_BUCKET = DEF_REG.register(
      "flying_fish_bucket", () -> new ItemModFishBucket(AMEntityRegistry.FLYING_FISH, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> FISH_BONES = DEF_REG.register("fish_bones", () -> new Item(new Properties()));
   public static final RegistryObject<Item> SKELEWAG_SWORD_INVENTORY = DEF_REG.register(
      "skelewag_sword_inventory", () -> new ItemInventoryOnly(new Properties())
   );
   public static final RegistryObject<Item> SKELEWAG_SWORD_HAND = DEF_REG.register("skelewag_sword_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final RegistryObject<Item> SKELEWAG_SWORD = DEF_REG.register(
      "skelewag_sword", () -> new ItemSkelewagSword(new Properties().m_41487_(1).m_41503_(430))
   );
   public static final RegistryObject<Item> NOVELTY_HAT = DEF_REG.register("novelty_hat", () -> new ItemModArmor(NOVELTY_HAT_MATERIAL, Type.HELMET));
   public static final RegistryObject<Item> MUDSKIPPER_BUCKET = DEF_REG.register(
      "mudskipper_bucket", () -> new ItemModFishBucket(AMEntityRegistry.MUDSKIPPER, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> FARSEER_ARM = DEF_REG.register("farseer_arm", () -> new Item(new Properties().m_41497_(Rarity.RARE)));
   public static final RegistryObject<Item> SKREECHER_SOUL = DEF_REG.register("skreecher_soul", () -> new Item(new Properties()));
   public static final RegistryObject<Item> GHOSTLY_PICKAXE = DEF_REG.register("ghostly_pickaxe", () -> new ItemGhostlyPickaxe(new Properties()));
   public static final RegistryObject<Item> ELASTIC_TENDON = DEF_REG.register("elastic_tendon", () -> new Item(new Properties()));
   public static final RegistryObject<Item> TENDON_WHIP = DEF_REG.register("tendon_whip", () -> new ItemTendonWhip(new Properties()));
   public static final RegistryObject<Item> UNSETTLING_KIMONO = DEF_REG.register("unsettling_kimono", () -> new ItemModArmor(KIMONO_MATERIAL, Type.CHESTPLATE));
   public static final RegistryObject<Item> STINK_BOTTLE = DEF_REG.register(
      "stink_bottle", () -> new ItemStinkBottle(AMBlockRegistry.SKUNK_SPRAY, new Properties().m_41487_(16))
   );
   public static final RegistryObject<Item> STINK_RAY_HAND = DEF_REG.register("stink_ray_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final RegistryObject<Item> STINK_RAY_INVENTORY = DEF_REG.register("stink_ray_inventory", () -> new ItemInventoryOnly(new Properties()));
   public static final RegistryObject<Item> STINK_RAY_EMPTY_HAND = DEF_REG.register("stink_ray_empty_hand", () -> new ItemInventoryOnly(new Properties()));
   public static final RegistryObject<Item> STINK_RAY_EMPTY_INVENTORY = DEF_REG.register(
      "stink_ray_empty_inventory", () -> new ItemInventoryOnly(new Properties())
   );
   public static final RegistryObject<Item> STINK_RAY = DEF_REG.register("stink_ray", () -> new ItemStinkRay(new Properties().m_41503_(5)));
   public static final RegistryObject<Item> BANANA_SLUG_SLIME = DEF_REG.register("banana_slug_slime", () -> new Item(new Properties()));
   public static final RegistryObject<Item> MOSQUITO_REPELLENT_STEW = DEF_REG.register(
      "mosquito_repellent_stew",
      () -> new BowlFoodItem(
         new Properties()
            .m_41489_(
               new Builder()
                  .m_38760_(4)
                  .m_38765_()
                  .m_38758_(0.3F)
                  .effect(() -> new MobEffectInstance((MobEffect)AMEffectRegistry.MOSQUITO_REPELLENT.get(), 24000), 1.0F)
                  .m_38767_()
            )
            .m_41487_(1)
      )
   );
   public static final RegistryObject<Item> TRIOPS_BUCKET = DEF_REG.register(
      "triops_bucket", () -> new ItemModFishBucket(AMEntityRegistry.TRIOPS, Fluids.f_76193_, new Properties())
   );
   public static final RegistryObject<Item> MUSIC_DISC_THIME = DEF_REG.register(
      "music_disc_thime", () -> new RecordItem(14, AMSoundRegistry.MUSIC_DISC_THIME, new Properties().m_41487_(1).m_41497_(Rarity.RARE), 6280)
   );
   public static final RegistryObject<Item> MUSIC_DISC_DAZE = DEF_REG.register(
      "music_disc_daze", () -> new RecordItem(14, AMSoundRegistry.MUSIC_DISC_DAZE, new Properties().m_41487_(1).m_41497_(Rarity.RARE), 3820)
   );

   public static void initSpawnEggs() {
      DEF_REG.register("spawn_egg_grizzly_bear", () -> new ForgeSpawnEggItem(AMEntityRegistry.GRIZZLY_BEAR, 6896172, 9920836, new Properties()));
      DEF_REG.register("spawn_egg_roadrunner", () -> new ForgeSpawnEggItem(AMEntityRegistry.ROADRUNNER, 3812902, 16509390, new Properties()));
      DEF_REG.register("spawn_egg_bone_serpent", () -> new ForgeSpawnEggItem(AMEntityRegistry.BONE_SERPENT, 15063492, 16736312, new Properties()));
      DEF_REG.register("spawn_egg_gazelle", () -> new ForgeSpawnEggItem(AMEntityRegistry.GAZELLE, 14526069, 2894117, new Properties()));
      DEF_REG.register("spawn_egg_crocodile", () -> new ForgeSpawnEggItem(AMEntityRegistry.CROCODILE, 7571776, 10920286, new Properties()));
      DEF_REG.register("spawn_egg_fly", () -> new ForgeSpawnEggItem(AMEntityRegistry.FLY, 4604481, 8990254, new Properties()));
      DEF_REG.register("spawn_egg_hummingbird", () -> new ForgeSpawnEggItem(AMEntityRegistry.HUMMINGBIRD, 3300991, 4499295, new Properties()));
      DEF_REG.register("spawn_egg_orca", () -> new ForgeSpawnEggItem(AMEntityRegistry.ORCA, 2894892, 14080228, new Properties()));
      DEF_REG.register("spawn_egg_sunbird", () -> new ForgeSpawnEggItem(AMEntityRegistry.SUNBIRD, 16148815, 16768416, new Properties()));
      DEF_REG.register("spawn_egg_gorilla", () -> new ForgeSpawnEggItem(AMEntityRegistry.GORILLA, 5856093, 1842209, new Properties()));
      DEF_REG.register("spawn_egg_crimson_mosquito", () -> new ForgeSpawnEggItem(AMEntityRegistry.CRIMSON_MOSQUITO, 5455935, 12655130, new Properties()));
      DEF_REG.register("spawn_egg_rattlesnake", () -> new ForgeSpawnEggItem(AMEntityRegistry.RATTLESNAKE, 13547924, 9665115, new Properties()));
      DEF_REG.register("spawn_egg_endergrade", () -> new ForgeSpawnEggItem(AMEntityRegistry.ENDERGRADE, 7889587, 8502763, new Properties()));
      DEF_REG.register("spawn_egg_hammerhead_shark", () -> new ForgeSpawnEggItem(AMEntityRegistry.HAMMERHEAD_SHARK, 9081525, 12173016, new Properties()));
      DEF_REG.register("spawn_egg_lobster", () -> new ForgeSpawnEggItem(AMEntityRegistry.LOBSTER, 12857635, 14507832, new Properties()));
      DEF_REG.register("spawn_egg_komodo_dragon", () -> new ForgeSpawnEggItem(AMEntityRegistry.KOMODO_DRAGON, 7629903, 5653041, new Properties()));
      DEF_REG.register("spawn_egg_capuchin_monkey", () -> new ForgeSpawnEggItem(AMEntityRegistry.CAPUCHIN_MONKEY, 2433311, 15850163, new Properties()));
      DEF_REG.register("spawn_egg_centipede", () -> new ForgeSpawnEggItem(AMEntityRegistry.CENTIPEDE_HEAD, 3418926, 7550025, new Properties()));
      DEF_REG.register("spawn_egg_warped_toad", () -> new ForgeSpawnEggItem(AMEntityRegistry.WARPED_TOAD, 2070158, 16690285, new Properties()));
      DEF_REG.register("spawn_egg_moose", () -> new ForgeSpawnEggItem(AMEntityRegistry.MOOSE, 3551274, 13939075, new Properties()));
      DEF_REG.register("spawn_egg_mimicube", () -> new ForgeSpawnEggItem(AMEntityRegistry.MIMICUBE, 9076929, 6180719, new Properties()));
      DEF_REG.register("spawn_egg_raccoon", () -> new ForgeSpawnEggItem(AMEntityRegistry.RACCOON, 8749694, 2762534, new Properties()));
      DEF_REG.register("spawn_egg_blobfish", () -> new ForgeSpawnEggItem(AMEntityRegistry.BLOBFISH, 14403261, 10386047, new Properties()));
      DEF_REG.register("spawn_egg_seal", () -> new ForgeSpawnEggItem(AMEntityRegistry.SEAL, 4734002, 6707532, new Properties()));
      DEF_REG.register("spawn_egg_cockroach", () -> new ForgeSpawnEggItem(AMEntityRegistry.COCKROACH, 854281, 4334622, new Properties()));
      DEF_REG.register("spawn_egg_shoebill", () -> new ForgeSpawnEggItem(AMEntityRegistry.SHOEBILL, 8553090, 14005386, new Properties()));
      DEF_REG.register("spawn_egg_elephant", () -> new ForgeSpawnEggItem(AMEntityRegistry.ELEPHANT, 9275783, 15590865, new Properties()));
      DEF_REG.register("spawn_egg_soul_vulture", () -> new ForgeSpawnEggItem(AMEntityRegistry.SOUL_VULTURE, 2303533, 5764351, new Properties()));
      DEF_REG.register("spawn_egg_snow_leopard", () -> new ForgeSpawnEggItem(AMEntityRegistry.SNOW_LEOPARD, 11313811, 2498589, new Properties()));
      DEF_REG.register("spawn_egg_spectre", () -> new ForgeSpawnEggItem(AMEntityRegistry.SPECTRE, 13160687, 8884719, new Properties()));
      DEF_REG.register("spawn_egg_crow", () -> new ForgeSpawnEggItem(AMEntityRegistry.CROW, 856348, 1843248, new Properties()));
      DEF_REG.register(
         "spawn_egg_alligator_snapping_turtle", () -> new ForgeSpawnEggItem(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, 7101522, 4548902, new Properties())
      );
      DEF_REG.register("spawn_egg_mungus", () -> new ForgeSpawnEggItem(AMEntityRegistry.MUNGUS, 8612493, 4539724, new Properties()));
      DEF_REG.register("spawn_egg_mantis_shrimp", () -> new ForgeSpawnEggItem(AMEntityRegistry.MANTIS_SHRIMP, 14370904, 1415454, new Properties()));
      DEF_REG.register("spawn_egg_guster", () -> new ForgeSpawnEggItem(AMEntityRegistry.GUSTER, 16307354, 16740874, new Properties()));
      DEF_REG.register("spawn_egg_warped_mosco", () -> new ForgeSpawnEggItem(AMEntityRegistry.WARPED_MOSCO, 3288920, 5988081, new Properties()));
      DEF_REG.register("spawn_egg_straddler", () -> new ForgeSpawnEggItem(AMEntityRegistry.STRADDLER, 6119278, 13478022, new Properties()));
      DEF_REG.register("spawn_egg_stradpole", () -> new ForgeSpawnEggItem(AMEntityRegistry.STRADPOLE, 6119278, 5728907, new Properties()));
      DEF_REG.register("spawn_egg_emu", () -> new ForgeSpawnEggItem(AMEntityRegistry.EMU, 6705990, 3881272, new Properties()));
      DEF_REG.register("spawn_egg_platypus", () -> new ForgeSpawnEggItem(AMEntityRegistry.PLATYPUS, 8212542, 3554115, new Properties()));
      DEF_REG.register("spawn_egg_dropbear", () -> new ForgeSpawnEggItem(AMEntityRegistry.DROPBEAR, 9055541, 6333347, new Properties()));
      DEF_REG.register("spawn_egg_tasmanian_devil", () -> new ForgeSpawnEggItem(AMEntityRegistry.TASMANIAN_DEVIL, 2434086, 11056319, new Properties()));
      DEF_REG.register("spawn_egg_kangaroo", () -> new ForgeSpawnEggItem(AMEntityRegistry.KANGAROO, 13540709, 14597536, new Properties()));
      DEF_REG.register("spawn_egg_cachalot_whale", () -> new ForgeSpawnEggItem(AMEntityRegistry.CACHALOT_WHALE, 9738393, 6252142, new Properties()));
      DEF_REG.register("spawn_egg_leafcutter_ant", () -> new ForgeSpawnEggItem(AMEntityRegistry.LEAFCUTTER_ANT, 9846819, 10901808, new Properties()));
      DEF_REG.register("spawn_egg_enderiophage", () -> new ForgeSpawnEggItem(AMEntityRegistry.ENDERIOPHAGE, 8859011, 16179917, new Properties()));
      DEF_REG.register("spawn_egg_bald_eagle", () -> new ForgeSpawnEggItem(AMEntityRegistry.BALD_EAGLE, 3284760, 16053492, new Properties()));
      DEF_REG.register("spawn_egg_tiger", () -> new ForgeSpawnEggItem(AMEntityRegistry.TIGER, 13066542, 2765363, new Properties()));
      DEF_REG.register("spawn_egg_tarantula_hawk", () -> new ForgeSpawnEggItem(AMEntityRegistry.TARANTULA_HAWK, 2312035, 14908216, new Properties()));
      DEF_REG.register("spawn_egg_void_worm", () -> new ForgeSpawnEggItem(AMEntityRegistry.VOID_WORM, 987174, 1481131, new Properties()));
      DEF_REG.register("spawn_egg_frilled_shark", () -> new ForgeSpawnEggItem(AMEntityRegistry.FRILLED_SHARK, 7498603, 8863037, new Properties()));
      DEF_REG.register("spawn_egg_mimic_octopus", () -> new ForgeSpawnEggItem(AMEntityRegistry.MIMIC_OCTOPUS, 16772060, 1907743, new Properties()));
      DEF_REG.register("spawn_egg_seagull", () -> new ForgeSpawnEggItem(AMEntityRegistry.SEAGULL, 13226716, 16767056, new Properties()));
      DEF_REG.register("spawn_egg_froststalker", () -> new ForgeSpawnEggItem(AMEntityRegistry.FROSTSTALKER, 7899841, 10601471, new Properties()));
      DEF_REG.register("spawn_egg_tusklin", () -> new ForgeSpawnEggItem(AMEntityRegistry.TUSKLIN, 7559233, 15262421, new Properties()));
      DEF_REG.register("spawn_egg_laviathan", () -> new ForgeSpawnEggItem(AMEntityRegistry.LAVIATHAN, 14058326, 3946823, new Properties()));
      DEF_REG.register("spawn_egg_cosmaw", () -> new ForgeSpawnEggItem(AMEntityRegistry.COSMAW, 7630269, 14073827, new Properties()));
      DEF_REG.register("spawn_egg_toucan", () -> new ForgeSpawnEggItem(AMEntityRegistry.TOUCAN, 16092979, 1974579, new Properties()));
      DEF_REG.register("spawn_egg_maned_wolf", () -> new ForgeSpawnEggItem(AMEntityRegistry.MANED_WOLF, 12286535, 4204314, new Properties()));
      DEF_REG.register("spawn_egg_anaconda", () -> new ForgeSpawnEggItem(AMEntityRegistry.ANACONDA, 5659682, 13858367, new Properties()));
      DEF_REG.register("spawn_egg_anteater", () -> new ForgeSpawnEggItem(AMEntityRegistry.ANTEATER, 4996922, 13417652, new Properties()));
      DEF_REG.register("spawn_egg_rocky_roller", () -> new ForgeSpawnEggItem(AMEntityRegistry.ROCKY_ROLLER, 11568495, 10064260, new Properties()));
      DEF_REG.register("spawn_egg_flutter", () -> new ForgeSpawnEggItem(AMEntityRegistry.FLUTTER, 7377453, 13663203, new Properties()));
      DEF_REG.register("spawn_egg_gelada_monkey", () -> new ForgeSpawnEggItem(AMEntityRegistry.GELADA_MONKEY, 11570276, 16731987, new Properties()));
      DEF_REG.register("spawn_egg_jerboa", () -> new ForgeSpawnEggItem(AMEntityRegistry.JERBOA, 14599562, 14589328, new Properties()));
      DEF_REG.register("spawn_egg_terrapin", () -> new ForgeSpawnEggItem(AMEntityRegistry.TERRAPIN, 7237168, 9606727, new Properties()));
      DEF_REG.register("spawn_egg_comb_jelly", () -> new ForgeSpawnEggItem(AMEntityRegistry.COMB_JELLY, 13625854, 7274379, new Properties()));
      DEF_REG.register("spawn_egg_cosmic_cod", () -> new ForgeSpawnEggItem(AMEntityRegistry.COSMIC_COD, 6915527, 14864895, new Properties()));
      DEF_REG.register("spawn_egg_bunfungus", () -> new ForgeSpawnEggItem(AMEntityRegistry.BUNFUNGUS, 7302545, 13183785, new Properties()));
      DEF_REG.register("spawn_egg_bison", () -> new ForgeSpawnEggItem(AMEntityRegistry.BISON, 4995630, 8021318, new Properties()));
      DEF_REG.register("spawn_egg_giant_squid", () -> new ForgeSpawnEggItem(AMEntityRegistry.GIANT_SQUID, 11225933, 14056811, new Properties()));
      DEF_REG.register("spawn_egg_devils_hole_pupfish", () -> new ForgeSpawnEggItem(AMEntityRegistry.DEVILS_HOLE_PUPFISH, 5667780, 7095413, new Properties()));
      DEF_REG.register("spawn_egg_catfish", () -> new ForgeSpawnEggItem(AMEntityRegistry.CATFISH, 8419159, 9073766, new Properties()));
      DEF_REG.register("spawn_egg_flying_fish", () -> new ForgeSpawnEggItem(AMEntityRegistry.FLYING_FISH, 8109293, 6848947, new Properties()));
      DEF_REG.register("spawn_egg_skelewag", () -> new ForgeSpawnEggItem(AMEntityRegistry.SKELEWAG, 14286001, 3821360, new Properties()));
      DEF_REG.register("spawn_egg_rain_frog", () -> new ForgeSpawnEggItem(AMEntityRegistry.RAIN_FROG, 12629403, 8086863, new Properties()));
      DEF_REG.register("spawn_egg_potoo", () -> new ForgeSpawnEggItem(AMEntityRegistry.POTOO, 9205587, 16760898, new Properties()));
      DEF_REG.register("spawn_egg_mudskipper", () -> new ForgeSpawnEggItem(AMEntityRegistry.MUDSKIPPER, 6320202, 4817004, new Properties()));
      DEF_REG.register("spawn_egg_rhinoceros", () -> new ForgeSpawnEggItem(AMEntityRegistry.RHINOCEROS, 10589588, 8549492, new Properties()));
      DEF_REG.register("spawn_egg_sugar_glider", () -> new ForgeSpawnEggItem(AMEntityRegistry.SUGAR_GLIDER, 8814977, 15461344, new Properties()));
      DEF_REG.register("spawn_egg_farseer", () -> new ForgeSpawnEggItem(AMEntityRegistry.FARSEER, 3356495, 9568089, new Properties()));
      DEF_REG.register("spawn_egg_skreecher", () -> new ForgeSpawnEggItem(AMEntityRegistry.SKREECHER, 477271, 8386815, new Properties()));
      DEF_REG.register("spawn_egg_underminer", () -> new ForgeSpawnEggItem(AMEntityRegistry.UNDERMINER, 14082815, 7111876, new Properties()));
      DEF_REG.register("spawn_egg_murmur", () -> new ForgeSpawnEggItem(AMEntityRegistry.MURMUR, 8406088, 11906972, new Properties()));
      DEF_REG.register("spawn_egg_skunk", () -> new ForgeSpawnEggItem(AMEntityRegistry.SKUNK, 2239798, 15001074, new Properties()));
      DEF_REG.register("spawn_egg_banana_slug", () -> new ForgeSpawnEggItem(AMEntityRegistry.BANANA_SLUG, 16764997, 16773491, new Properties()));
      DEF_REG.register("spawn_egg_blue_jay", () -> new ForgeSpawnEggItem(AMEntityRegistry.BLUE_JAY, 6273022, 2702146, new Properties()));
      DEF_REG.register("spawn_egg_caiman", () -> new ForgeSpawnEggItem(AMEntityRegistry.CAIMAN, 6051377, 12305500, new Properties()));
      DEF_REG.register("spawn_egg_triops", () -> new ForgeSpawnEggItem(AMEntityRegistry.TRIOPS, 9861460, 13267280, new Properties()));
      registerPatternItem("bear");
      registerPatternItem("australia_0");
      registerPatternItem("australia_1");
      registerPatternItem("new_mexico");
      registerPatternItem("brazil");

      for (int i = 0; i <= 10; i++) {
         DEF_REG.register("dimensional_carver_shard_" + i, () -> new ItemInventoryOnly(new Properties()));
      }
   }

   private static void registerPatternItem(String name) {
      TagKey<BannerPattern> bannerPatternTagKey = TagKey.m_203882_(Registries.f_256969_, new ResourceLocation("alexsmobs", "pattern_for_" + name));
      DEF_REG.register("banner_pattern_" + name, () -> new BannerPatternItem(bannerPatternTagKey, new Properties().m_41487_(1)));
   }

   public static void init() {
      CROCODILE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)CROCODILE_SCUTE.get()}));
      ROADRUNNER_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)ROADRUNNER_FEATHER.get()}));
      CENTIPEDE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)CENTIPEDE_LEG.get()}));
      MOOSE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)MOOSE_ANTLER.get()}));
      RACCOON_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)RACCOON_TAIL.get()}));
      SOMBRERO_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{Items.f_42129_}));
      SPIKED_TURTLE_SHELL_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)SPIKED_SCUTE.get()}));
      FEDORA_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{Items.f_42454_}));
      EMU_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)EMU_FEATHER.get()}));
      ROCKY_ARMOR_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)ROCKY_SHELL.get()}));
      FLYING_FISH_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{(ItemLike)FLYING_FISH.get()}));
      NOVELTY_HAT_MATERIAL.setRepairMaterial(Ingredient.m_43929_(new ItemLike[]{Items.f_42500_}));
      KIMONO_MATERIAL.setRepairMaterial(Ingredient.m_204132_(ItemTags.f_13167_));
      LecternBooks.BOOKS.put(ANIMAL_DICTIONARY.getId(), new BookData(6318886, 16644333));
   }

   public static void initDispenser() {
      DispenserBlock.m_52672_(
         (ItemLike)SHARK_TOOTH_ARROW.get(),
         new AbstractProjectileDispenseBehavior() {
            protected Projectile m_6895_(Level worldIn, Position position, ItemStack stackIn) {
               EntitySharkToothArrow entityarrow = new EntitySharkToothArrow(
                  (EntityType)AMEntityRegistry.SHARK_TOOTH_ARROW.get(), position.m_7096_(), position.m_7098_(), position.m_7094_(), worldIn
               );
               entityarrow.f_36705_ = Pickup.ALLOWED;
               return entityarrow;
            }
         }
      );
      DispenserBlock.m_52672_((ItemLike)ANCIENT_DART.get(), new AbstractProjectileDispenseBehavior() {
         protected Projectile m_6895_(Level worldIn, Position position, ItemStack stackIn) {
            EntityTossedItem tossedItem = new EntityTossedItem(worldIn, position.m_7096_(), position.m_7098_(), position.m_7094_());
            tossedItem.setDart(true);
            return tossedItem;
         }
      });
      DispenserBlock.m_52672_((ItemLike)COCKROACH_OOTHECA.get(), new AbstractProjectileDispenseBehavior() {
         protected Projectile m_6895_(Level worldIn, Position position, ItemStack stackIn) {
            return new EntityCockroachEgg(worldIn, position.m_7096_(), position.m_7098_(), position.m_7094_());
         }
      });
      DispenserBlock.m_52672_((ItemLike)EMU_EGG.get(), new AbstractProjectileDispenseBehavior() {
         protected Projectile m_6895_(Level worldIn, Position position, ItemStack stackIn) {
            return new EntityEmuEgg(worldIn, position.m_7096_(), position.m_7098_(), position.m_7094_());
         }
      });
      DispenserBlock.m_52672_((ItemLike)ENDERIOPHAGE_ROCKET.get(), new AbstractProjectileDispenseBehavior() {
         protected Projectile m_6895_(Level worldIn, Position position, ItemStack stackIn) {
            return new EntityEnderiophageRocket(worldIn, position.m_7096_(), position.m_7098_(), position.m_7094_(), stackIn);
         }
      });
      DispenseItemBehavior bucketDispenseBehavior = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack m_7498_(BlockSource blockSource, ItemStack stack) {
            DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)stack.m_41720_();
            BlockPos blockpos = blockSource.m_7961_().m_121945_((Direction)blockSource.m_6414_().m_61143_(DispenserBlock.f_52659_));
            Level level = blockSource.m_7727_();
            if (dispensiblecontaineritem.m_142073_((Player)null, level, blockpos, (BlockHitResult)null)) {
               dispensiblecontaineritem.m_142131_((Player)null, level, stack, blockpos);
               return new ItemStack(Items.f_42446_);
            } else {
               return this.defaultDispenseItemBehavior.m_6115_(blockSource, stack);
            }
         }
      };
      DispenserBlock.m_52672_((ItemLike)LOBSTER_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)BLOBFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)STRADPOLE_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)PLATYPUS_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)FRILLED_SHARK_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)MIMIC_OCTOPUS_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)TERRAPIN_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)COMB_JELLY_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)COSMIC_COD_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)DEVILS_HOLE_PUPFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)SMALL_CATFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)MEDIUM_CATFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)LARGE_CATFISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)FLYING_FISH_BUCKET.get(), bucketDispenseBehavior);
      DispenserBlock.m_52672_((ItemLike)MUDSKIPPER_BUCKET.get(), bucketDispenseBehavior);
      ComposterBlock.f_51914_.put((ItemLike)BANANA.get(), 0.65F);
      ComposterBlock.f_51914_.put(((Block)AMBlockRegistry.BANANA_PEEL.get()).m_5456_(), 1.0F);
      ComposterBlock.f_51914_.put((ItemLike)ACACIA_BLOSSOM.get(), 0.65F);
      ComposterBlock.f_51914_.put((ItemLike)GONGYLIDIA.get(), 0.9F);
   }

   static {
      initSpawnEggs();
   }
}
