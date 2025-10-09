package com.github.alexthe666.alexsmobs.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(
   modid = "alexsmobs",
   bus = Bus.MOD
)
public class AMEntityRegistry {
   public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "alexsmobs");
   public static final RegistryObject<EntityType<EntityGrizzlyBear>> GRIZZLY_BEAR = DEF_REG.register(
      "grizzly_bear",
      () -> registerEntity(Builder.m_20704_(EntityGrizzlyBear::new, MobCategory.CREATURE).m_20699_(1.6F, 1.8F).setTrackingRange(10), "grizzly_bear")
   );
   public static final RegistryObject<EntityType<EntityRoadrunner>> ROADRUNNER = DEF_REG.register(
      "roadrunner",
      () -> registerEntity(Builder.m_20704_(EntityRoadrunner::new, MobCategory.CREATURE).m_20699_(0.45F, 0.75F).setTrackingRange(10), "roadrunner")
   );
   public static final RegistryObject<EntityType<EntityBoneSerpent>> BONE_SERPENT = DEF_REG.register(
      "bone_serpent",
      () -> registerEntity(Builder.m_20704_(EntityBoneSerpent::new, MobCategory.MONSTER).m_20699_(1.2F, 1.15F).m_20719_().setTrackingRange(10), "bone_serpent")
   );
   public static final RegistryObject<EntityType<EntityBoneSerpentPart>> BONE_SERPENT_PART = DEF_REG.register(
      "bone_serpent_part",
      () -> registerEntity(
         Builder.m_20704_(EntityBoneSerpentPart::new, MobCategory.MONSTER).m_20699_(1.0F, 1.0F).m_20719_().setTrackingRange(10), "bone_serpent_part"
      )
   );
   public static final RegistryObject<EntityType<EntityGazelle>> GAZELLE = DEF_REG.register(
      "gazelle", () -> registerEntity(Builder.m_20704_(EntityGazelle::new, MobCategory.CREATURE).m_20699_(0.85F, 1.25F).setTrackingRange(10), "gazelle")
   );
   public static final RegistryObject<EntityType<EntityCrocodile>> CROCODILE = DEF_REG.register(
      "crocodile", () -> registerEntity(Builder.m_20704_(EntityCrocodile::new, MobCategory.CREATURE).m_20699_(2.15F, 0.75F).setTrackingRange(10), "crocodile")
   );
   public static final RegistryObject<EntityType<EntityFly>> FLY = DEF_REG.register(
      "fly", () -> registerEntity(Builder.m_20704_(EntityFly::new, MobCategory.AMBIENT).m_20699_(0.35F, 0.35F).setTrackingRange(4), "fly")
   );
   public static final RegistryObject<EntityType<EntityHummingbird>> HUMMINGBIRD = DEF_REG.register(
      "hummingbird",
      () -> registerEntity(Builder.m_20704_(EntityHummingbird::new, MobCategory.CREATURE).m_20699_(0.45F, 0.45F).setTrackingRange(5), "hummingbird")
   );
   public static final RegistryObject<EntityType<EntityOrca>> ORCA = DEF_REG.register(
      "orca", () -> registerEntity(Builder.m_20704_(EntityOrca::new, MobCategory.WATER_CREATURE).m_20699_(3.75F, 1.75F).setTrackingRange(10), "orca")
   );
   public static final RegistryObject<EntityType<EntitySunbird>> SUNBIRD = DEF_REG.register(
      "sunbird",
      () -> registerEntity(
         Builder.m_20704_(EntitySunbird::new, MobCategory.CREATURE)
            .m_20699_(2.75F, 1.5F)
            .m_20719_()
            .setTrackingRange(12)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1),
         "sunbird"
      )
   );
   public static final RegistryObject<EntityType<EntityGorilla>> GORILLA = DEF_REG.register(
      "gorilla", () -> registerEntity(Builder.m_20704_(EntityGorilla::new, MobCategory.CREATURE).m_20699_(1.15F, 1.35F).setTrackingRange(10), "gorilla")
   );
   public static final RegistryObject<EntityType<EntityCrimsonMosquito>> CRIMSON_MOSQUITO = DEF_REG.register(
      "crimson_mosquito",
      () -> registerEntity(
         Builder.m_20704_(EntityCrimsonMosquito::new, MobCategory.MONSTER).m_20699_(1.25F, 1.15F).m_20719_().setTrackingRange(8), "crimson_mosquito"
      )
   );
   public static final RegistryObject<EntityType<EntityMosquitoSpit>> MOSQUITO_SPIT = DEF_REG.register(
      "mosquito_spit",
      () -> registerEntity(
         Builder.m_20704_(EntityMosquitoSpit::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntityMosquitoSpit::new).m_20719_(),
         "mosquito_spit"
      )
   );
   public static final RegistryObject<EntityType<EntityRattlesnake>> RATTLESNAKE = DEF_REG.register(
      "rattlesnake",
      () -> registerEntity(Builder.m_20704_(EntityRattlesnake::new, MobCategory.CREATURE).m_20699_(0.95F, 0.35F).setTrackingRange(10), "rattlesnake")
   );
   public static final RegistryObject<EntityType<EntityEndergrade>> ENDERGRADE = DEF_REG.register(
      "endergrade",
      () -> registerEntity(Builder.m_20704_(EntityEndergrade::new, MobCategory.CREATURE).m_20699_(0.95F, 0.85F).setTrackingRange(10), "endergrade")
   );
   public static final RegistryObject<EntityType<EntityHammerheadShark>> HAMMERHEAD_SHARK = DEF_REG.register(
      "hammerhead_shark",
      () -> registerEntity(
         Builder.m_20704_(EntityHammerheadShark::new, MobCategory.WATER_CREATURE).m_20699_(2.4F, 1.25F).setTrackingRange(10), "hammerhead_shark"
      )
   );
   public static final RegistryObject<EntityType<EntitySharkToothArrow>> SHARK_TOOTH_ARROW = DEF_REG.register(
      "shark_tooth_arrow",
      () -> registerEntity(
         Builder.m_20704_(EntitySharkToothArrow::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntitySharkToothArrow::new),
         "shark_tooth_arrow"
      )
   );
   public static final RegistryObject<EntityType<EntityLobster>> LOBSTER = DEF_REG.register(
      "lobster", () -> registerEntity(Builder.m_20704_(EntityLobster::new, MobCategory.WATER_AMBIENT).m_20699_(0.7F, 0.4F).setTrackingRange(5), "lobster")
   );
   public static final RegistryObject<EntityType<EntityKomodoDragon>> KOMODO_DRAGON = DEF_REG.register(
      "komodo_dragon",
      () -> registerEntity(Builder.m_20704_(EntityKomodoDragon::new, MobCategory.CREATURE).m_20699_(1.9F, 0.9F).setTrackingRange(10), "komodo_dragon")
   );
   public static final RegistryObject<EntityType<EntityCapuchinMonkey>> CAPUCHIN_MONKEY = DEF_REG.register(
      "capuchin_monkey",
      () -> registerEntity(Builder.m_20704_(EntityCapuchinMonkey::new, MobCategory.CREATURE).m_20699_(0.65F, 0.75F).setTrackingRange(10), "capuchin_monkey")
   );
   public static final RegistryObject<EntityType<EntityTossedItem>> TOSSED_ITEM = DEF_REG.register(
      "tossed_item",
      () -> registerEntity(
         Builder.m_20704_(EntityTossedItem::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntityTossedItem::new).m_20719_(), "tossed_item"
      )
   );
   public static final RegistryObject<EntityType<EntityCentipedeHead>> CENTIPEDE_HEAD = DEF_REG.register(
      "centipede_head",
      () -> registerEntity(Builder.m_20704_(EntityCentipedeHead::new, MobCategory.MONSTER).m_20699_(0.9F, 0.9F).setTrackingRange(8), "centipede_head")
   );
   public static final RegistryObject<EntityType<EntityCentipedeBody>> CENTIPEDE_BODY = DEF_REG.register(
      "centipede_body",
      () -> registerEntity(
         Builder.m_20704_(EntityCentipedeBody::new, MobCategory.MISC)
            .m_20699_(0.9F, 0.9F)
            .m_20719_()
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(8),
         "centipede_body"
      )
   );
   public static final RegistryObject<EntityType<EntityCentipedeTail>> CENTIPEDE_TAIL = DEF_REG.register(
      "centipede_tail",
      () -> registerEntity(
         Builder.m_20704_(EntityCentipedeTail::new, MobCategory.MISC)
            .m_20699_(0.9F, 0.9F)
            .m_20719_()
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(8),
         "centipede_tail"
      )
   );
   public static final RegistryObject<EntityType<EntityWarpedToad>> WARPED_TOAD = DEF_REG.register(
      "warped_toad",
      () -> registerEntity(
         Builder.m_20704_(EntityWarpedToad::new, MobCategory.CREATURE)
            .m_20699_(0.9F, 1.4F)
            .m_20719_()
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(10),
         "warped_toad"
      )
   );
   public static final RegistryObject<EntityType<EntityMoose>> MOOSE = DEF_REG.register(
      "moose", () -> registerEntity(Builder.m_20704_(EntityMoose::new, MobCategory.CREATURE).m_20699_(1.7F, 2.4F).setTrackingRange(10), "moose")
   );
   public static final RegistryObject<EntityType<EntityMimicube>> MIMICUBE = DEF_REG.register(
      "mimicube", () -> registerEntity(Builder.m_20704_(EntityMimicube::new, MobCategory.MONSTER).m_20699_(0.9F, 0.9F).setTrackingRange(8), "mimicube")
   );
   public static final RegistryObject<EntityType<EntityRaccoon>> RACCOON = DEF_REG.register(
      "raccoon", () -> registerEntity(Builder.m_20704_(EntityRaccoon::new, MobCategory.CREATURE).m_20699_(0.8F, 0.9F).setTrackingRange(10), "raccoon")
   );
   public static final RegistryObject<EntityType<EntityBlobfish>> BLOBFISH = DEF_REG.register(
      "blobfish", () -> registerEntity(Builder.m_20704_(EntityBlobfish::new, MobCategory.WATER_AMBIENT).m_20699_(0.7F, 0.45F).setTrackingRange(5), "blobfish")
   );
   public static final RegistryObject<EntityType<EntitySeal>> SEAL = DEF_REG.register(
      "seal", () -> registerEntity(Builder.m_20704_(EntitySeal::new, MobCategory.CREATURE).m_20699_(1.45F, 0.9F).setTrackingRange(10), "seal")
   );
   public static final RegistryObject<EntityType<EntityCockroach>> COCKROACH = DEF_REG.register(
      "cockroach", () -> registerEntity(Builder.m_20704_(EntityCockroach::new, MobCategory.AMBIENT).m_20699_(0.7F, 0.3F).setTrackingRange(5), "cockroach")
   );
   public static final RegistryObject<EntityType<EntityCockroachEgg>> COCKROACH_EGG = DEF_REG.register(
      "cockroach_egg",
      () -> registerEntity(
         Builder.m_20704_(EntityCockroachEgg::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntityCockroachEgg::new).m_20719_(),
         "cockroach_egg"
      )
   );
   public static final RegistryObject<EntityType<EntityShoebill>> SHOEBILL = DEF_REG.register(
      "shoebill",
      () -> registerEntity(
         Builder.m_20704_(EntityShoebill::new, MobCategory.CREATURE).m_20699_(0.8F, 1.5F).setUpdateInterval(1).setTrackingRange(10), "shoebill"
      )
   );
   public static final RegistryObject<EntityType<EntityElephant>> ELEPHANT = DEF_REG.register(
      "elephant",
      () -> registerEntity(
         Builder.m_20704_(EntityElephant::new, MobCategory.CREATURE).m_20699_(3.1F, 3.5F).setUpdateInterval(1).setTrackingRange(10), "elephant"
      )
   );
   public static final RegistryObject<EntityType<EntitySoulVulture>> SOUL_VULTURE = DEF_REG.register(
      "soul_vulture",
      () -> registerEntity(
         Builder.m_20704_(EntitySoulVulture::new, MobCategory.MONSTER).m_20699_(0.9F, 1.3F).setUpdateInterval(1).m_20719_().setTrackingRange(8), "soul_vulture"
      )
   );
   public static final RegistryObject<EntityType<EntitySnowLeopard>> SNOW_LEOPARD = DEF_REG.register(
      "snow_leopard",
      () -> registerEntity(
         Builder.m_20704_(EntitySnowLeopard::new, MobCategory.CREATURE).m_20699_(1.2F, 1.3F).m_20714_(new Block[]{Blocks.f_152499_}).setTrackingRange(10),
         "snow_leopard"
      )
   );
   public static final RegistryObject<EntityType<EntitySpectre>> SPECTRE = DEF_REG.register(
      "spectre",
      () -> registerEntity(
         Builder.m_20704_(EntitySpectre::new, MobCategory.CREATURE)
            .m_20699_(3.15F, 0.8F)
            .m_20719_()
            .setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1),
         "spectre"
      )
   );
   public static final RegistryObject<EntityType<EntityCrow>> CROW = DEF_REG.register(
      "crow", () -> registerEntity(Builder.m_20704_(EntityCrow::new, MobCategory.CREATURE).m_20699_(0.45F, 0.45F).setTrackingRange(10), "crow")
   );
   public static final RegistryObject<EntityType<EntityAlligatorSnappingTurtle>> ALLIGATOR_SNAPPING_TURTLE = DEF_REG.register(
      "alligator_snapping_turtle",
      () -> registerEntity(
         Builder.m_20704_(EntityAlligatorSnappingTurtle::new, MobCategory.CREATURE).m_20699_(1.25F, 0.65F).setTrackingRange(10), "alligator_snapping_turtle"
      )
   );
   public static final RegistryObject<EntityType<EntityMungus>> MUNGUS = DEF_REG.register(
      "mungus", () -> registerEntity(Builder.m_20704_(EntityMungus::new, MobCategory.CREATURE).m_20699_(0.75F, 1.45F).setTrackingRange(10), "mungus")
   );
   public static final RegistryObject<EntityType<EntityMantisShrimp>> MANTIS_SHRIMP = DEF_REG.register(
      "mantis_shrimp",
      () -> registerEntity(Builder.m_20704_(EntityMantisShrimp::new, MobCategory.WATER_CREATURE).m_20699_(1.25F, 1.2F).setTrackingRange(10), "mantis_shrimp")
   );
   public static final RegistryObject<EntityType<EntityGuster>> GUSTER = DEF_REG.register(
      "guster", () -> registerEntity(Builder.m_20704_(EntityGuster::new, MobCategory.MONSTER).m_20699_(1.42F, 2.35F).m_20719_().setTrackingRange(8), "guster")
   );
   public static final RegistryObject<EntityType<EntitySandShot>> SAND_SHOT = DEF_REG.register(
      "sand_shot",
      () -> registerEntity(
         Builder.m_20704_(EntitySandShot::new, MobCategory.MISC).m_20699_(0.95F, 0.65F).setCustomClientFactory(EntitySandShot::new).m_20719_(), "sand_shot"
      )
   );
   public static final RegistryObject<EntityType<EntityGust>> GUST = DEF_REG.register(
      "gust",
      () -> registerEntity(Builder.m_20704_(EntityGust::new, MobCategory.MISC).m_20699_(0.8F, 0.8F).setCustomClientFactory(EntityGust::new).m_20719_(), "gust")
   );
   public static final RegistryObject<EntityType<EntityWarpedMosco>> WARPED_MOSCO = DEF_REG.register(
      "warped_mosco",
      () -> registerEntity(Builder.m_20704_(EntityWarpedMosco::new, MobCategory.MONSTER).m_20699_(1.99F, 3.25F).m_20719_().setTrackingRange(10), "warped_mosco")
   );
   public static final RegistryObject<EntityType<EntityHemolymph>> HEMOLYMPH = DEF_REG.register(
      "hemolymph",
      () -> registerEntity(
         Builder.m_20704_(EntityHemolymph::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntityHemolymph::new).m_20719_(), "hemolymph"
      )
   );
   public static final RegistryObject<EntityType<EntityStraddler>> STRADDLER = DEF_REG.register(
      "straddler",
      () -> registerEntity(Builder.m_20704_(EntityStraddler::new, MobCategory.MONSTER).m_20699_(1.65F, 3.0F).m_20719_().setTrackingRange(8), "straddler")
   );
   public static final RegistryObject<EntityType<EntityStradpole>> STRADPOLE = DEF_REG.register(
      "stradpole",
      () -> registerEntity(Builder.m_20704_(EntityStradpole::new, MobCategory.WATER_AMBIENT).m_20699_(0.5F, 0.5F).m_20719_().setTrackingRange(4), "stradpole")
   );
   public static final RegistryObject<EntityType<EntityStraddleboard>> STRADDLEBOARD = DEF_REG.register(
      "straddleboard",
      () -> registerEntity(
         Builder.m_20704_(EntityStraddleboard::new, MobCategory.MISC)
            .m_20699_(1.5F, 0.35F)
            .setCustomClientFactory(EntityStraddleboard::new)
            .m_20719_()
            .setUpdateInterval(1)
            .m_20702_(10)
            .setShouldReceiveVelocityUpdates(true),
         "straddleboard"
      )
   );
   public static final RegistryObject<EntityType<EntityEmu>> EMU = DEF_REG.register(
      "emu", () -> registerEntity(Builder.m_20704_(EntityEmu::new, MobCategory.CREATURE).m_20699_(1.1F, 1.8F).setTrackingRange(10), "emu")
   );
   public static final RegistryObject<EntityType<EntityEmuEgg>> EMU_EGG = DEF_REG.register(
      "emu_egg",
      () -> registerEntity(
         Builder.m_20704_(EntityEmuEgg::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntityEmuEgg::new).m_20719_(), "emu_egg"
      )
   );
   public static final RegistryObject<EntityType<EntityPlatypus>> PLATYPUS = DEF_REG.register(
      "platypus", () -> registerEntity(Builder.m_20704_(EntityPlatypus::new, MobCategory.CREATURE).m_20699_(0.8F, 0.5F).setTrackingRange(10), "platypus")
   );
   public static final RegistryObject<EntityType<EntityDropBear>> DROPBEAR = DEF_REG.register(
      "dropbear",
      () -> registerEntity(Builder.m_20704_(EntityDropBear::new, MobCategory.MONSTER).m_20699_(1.65F, 1.5F).m_20719_().setTrackingRange(8), "dropbear")
   );
   public static final RegistryObject<EntityType<EntityTasmanianDevil>> TASMANIAN_DEVIL = DEF_REG.register(
      "tasmanian_devil",
      () -> registerEntity(Builder.m_20704_(EntityTasmanianDevil::new, MobCategory.CREATURE).m_20699_(0.7F, 0.8F).setTrackingRange(10), "tasmanian_devil")
   );
   public static final RegistryObject<EntityType<EntityKangaroo>> KANGAROO = DEF_REG.register(
      "kangaroo", () -> registerEntity(Builder.m_20704_(EntityKangaroo::new, MobCategory.CREATURE).m_20699_(1.65F, 1.5F).setTrackingRange(10), "kangaroo")
   );
   public static final RegistryObject<EntityType<EntityCachalotWhale>> CACHALOT_WHALE = DEF_REG.register(
      "cachalot_whale",
      () -> registerEntity(Builder.m_20704_(EntityCachalotWhale::new, MobCategory.WATER_CREATURE).m_20699_(9.0F, 4.0F).setTrackingRange(10), "cachalot_whale")
   );
   public static final RegistryObject<EntityType<EntityCachalotEcho>> CACHALOT_ECHO = DEF_REG.register(
      "cachalot_echo",
      () -> registerEntity(
         Builder.m_20704_(EntityCachalotEcho::new, MobCategory.MISC).m_20699_(2.0F, 2.0F).setCustomClientFactory(EntityCachalotEcho::new).m_20719_(),
         "cachalot_echo"
      )
   );
   public static final RegistryObject<EntityType<EntityLeafcutterAnt>> LEAFCUTTER_ANT = DEF_REG.register(
      "leafcutter_ant",
      () -> registerEntity(Builder.m_20704_(EntityLeafcutterAnt::new, MobCategory.CREATURE).m_20699_(0.8F, 0.5F).setTrackingRange(5), "leafcutter_ant")
   );
   public static final RegistryObject<EntityType<EntityEnderiophage>> ENDERIOPHAGE = DEF_REG.register(
      "enderiophage",
      () -> registerEntity(
         Builder.m_20704_(EntityEnderiophage::new, MobCategory.CREATURE).m_20699_(0.85F, 1.95F).setUpdateInterval(1).setTrackingRange(8), "enderiophage"
      )
   );
   public static final RegistryObject<EntityType<EntityEnderiophageRocket>> ENDERIOPHAGE_ROCKET = DEF_REG.register(
      "enderiophage_rocket",
      () -> registerEntity(
         Builder.m_20704_(EntityEnderiophageRocket::new, MobCategory.MISC)
            .m_20699_(0.5F, 0.5F)
            .setCustomClientFactory(EntityEnderiophageRocket::new)
            .m_20719_(),
         "enderiophage_rocket"
      )
   );
   public static final RegistryObject<EntityType<EntityBaldEagle>> BALD_EAGLE = DEF_REG.register(
      "bald_eagle",
      () -> registerEntity(
         Builder.m_20704_(EntityBaldEagle::new, MobCategory.CREATURE).m_20699_(0.5F, 0.95F).setUpdateInterval(1).setTrackingRange(14), "bald_eagle"
      )
   );
   public static final RegistryObject<EntityType<EntityTiger>> TIGER = DEF_REG.register(
      "tiger", () -> registerEntity(Builder.m_20704_(EntityTiger::new, MobCategory.CREATURE).m_20699_(1.45F, 1.2F).setTrackingRange(10), "tiger")
   );
   public static final RegistryObject<EntityType<EntityTarantulaHawk>> TARANTULA_HAWK = DEF_REG.register(
      "tarantula_hawk",
      () -> registerEntity(Builder.m_20704_(EntityTarantulaHawk::new, MobCategory.CREATURE).m_20699_(1.2F, 0.9F).setTrackingRange(10), "tarantula_hawk")
   );
   public static final RegistryObject<EntityType<EntityVoidWorm>> VOID_WORM = DEF_REG.register(
      "void_worm",
      () -> registerEntity(
         Builder.m_20704_(EntityVoidWorm::new, MobCategory.MONSTER)
            .m_20699_(3.4F, 3.0F)
            .m_20719_()
            .setTrackingRange(20)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1),
         "void_worm"
      )
   );
   public static final RegistryObject<EntityType<EntityVoidWormPart>> VOID_WORM_PART = DEF_REG.register(
      "void_worm_part",
      () -> registerEntity(
         Builder.m_20704_(EntityVoidWormPart::new, MobCategory.MONSTER)
            .m_20699_(1.2F, 1.35F)
            .m_20719_()
            .setTrackingRange(20)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1),
         "void_worm_part"
      )
   );
   public static final RegistryObject<EntityType<EntityVoidWormShot>> VOID_WORM_SHOT = DEF_REG.register(
      "void_worm_shot",
      () -> registerEntity(
         Builder.m_20704_(EntityVoidWormShot::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntityVoidWormShot::new).m_20719_(),
         "void_worm_shot"
      )
   );
   public static final RegistryObject<EntityType<EntityVoidPortal>> VOID_PORTAL = DEF_REG.register(
      "void_portal",
      () -> registerEntity(
         Builder.m_20704_(EntityVoidPortal::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntityVoidPortal::new).m_20719_(), "void_portal"
      )
   );
   public static final RegistryObject<EntityType<EntityFrilledShark>> FRILLED_SHARK = DEF_REG.register(
      "frilled_shark",
      () -> registerEntity(Builder.m_20704_(EntityFrilledShark::new, MobCategory.WATER_CREATURE).m_20699_(1.3F, 0.4F).setTrackingRange(8), "frilled_shark")
   );
   public static final RegistryObject<EntityType<EntityMimicOctopus>> MIMIC_OCTOPUS = DEF_REG.register(
      "mimic_octopus",
      () -> registerEntity(Builder.m_20704_(EntityMimicOctopus::new, MobCategory.WATER_CREATURE).m_20699_(0.9F, 0.6F).setTrackingRange(8), "mimic_octopus")
   );
   public static final RegistryObject<EntityType<EntitySeagull>> SEAGULL = DEF_REG.register(
      "seagull", () -> registerEntity(Builder.m_20704_(EntitySeagull::new, MobCategory.CREATURE).m_20699_(0.45F, 0.45F).setTrackingRange(10), "seagull")
   );
   public static final RegistryObject<EntityType<EntityFroststalker>> FROSTSTALKER = DEF_REG.register(
      "froststalker",
      () -> registerEntity(
         Builder.m_20704_(EntityFroststalker::new, MobCategory.CREATURE).m_20699_(0.95F, 1.15F).m_20714_(new Block[]{Blocks.f_152499_}), "froststalker"
      )
   );
   public static final RegistryObject<EntityType<EntityIceShard>> ICE_SHARD = DEF_REG.register(
      "ice_shard",
      () -> registerEntity(
         Builder.m_20704_(EntityIceShard::new, MobCategory.MISC).m_20699_(0.45F, 0.45F).setCustomClientFactory(EntityIceShard::new).m_20719_(), "ice_shard"
      )
   );
   public static final RegistryObject<EntityType<EntityTusklin>> TUSKLIN = DEF_REG.register(
      "tusklin",
      () -> registerEntity(
         Builder.m_20704_(EntityTusklin::new, MobCategory.CREATURE).m_20699_(2.2F, 1.9F).m_20714_(new Block[]{Blocks.f_152499_}).setTrackingRange(10),
         "tusklin"
      )
   );
   public static final RegistryObject<EntityType<EntityLaviathan>> LAVIATHAN = DEF_REG.register(
      "laviathan",
      () -> registerEntity(
         Builder.m_20704_(EntityLaviathan::new, MobCategory.CREATURE)
            .m_20699_(3.3F, 2.4F)
            .m_20719_()
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(10),
         "laviathan"
      )
   );
   public static final RegistryObject<EntityType<EntityCosmaw>> COSMAW = DEF_REG.register(
      "cosmaw", () -> registerEntity(Builder.m_20704_(EntityCosmaw::new, MobCategory.CREATURE).m_20699_(1.95F, 1.8F).setTrackingRange(10), "cosmaw")
   );
   public static final RegistryObject<EntityType<EntityToucan>> TOUCAN = DEF_REG.register(
      "toucan", () -> registerEntity(Builder.m_20704_(EntityToucan::new, MobCategory.CREATURE).m_20699_(0.45F, 0.45F).setTrackingRange(10), "toucan")
   );
   public static final RegistryObject<EntityType<EntityManedWolf>> MANED_WOLF = DEF_REG.register(
      "maned_wolf", () -> registerEntity(Builder.m_20704_(EntityManedWolf::new, MobCategory.CREATURE).m_20699_(0.9F, 1.26F).setTrackingRange(10), "maned_wolf")
   );
   public static final RegistryObject<EntityType<EntityAnaconda>> ANACONDA = DEF_REG.register(
      "anaconda", () -> registerEntity(Builder.m_20704_(EntityAnaconda::new, MobCategory.CREATURE).m_20699_(0.8F, 0.8F).setTrackingRange(10), "anaconda")
   );
   public static final RegistryObject<EntityType<EntityAnacondaPart>> ANACONDA_PART = DEF_REG.register(
      "anaconda_part",
      () -> registerEntity(
         Builder.m_20704_(EntityAnacondaPart::new, MobCategory.MISC)
            .m_20699_(0.8F, 0.8F)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(10),
         "anaconda_part"
      )
   );
   public static final RegistryObject<EntityType<EntityVineLasso>> VINE_LASSO = DEF_REG.register(
      "vine_lasso",
      () -> registerEntity(
         Builder.m_20704_(EntityVineLasso::new, MobCategory.MISC).m_20699_(0.85F, 0.2F).setCustomClientFactory(EntityVineLasso::new).m_20719_(), "vine_lasso"
      )
   );
   public static final RegistryObject<EntityType<EntityAnteater>> ANTEATER = DEF_REG.register(
      "anteater", () -> registerEntity(Builder.m_20704_(EntityAnteater::new, MobCategory.CREATURE).m_20699_(1.3F, 1.1F).setTrackingRange(10), "anteater")
   );
   public static final RegistryObject<EntityType<EntityRockyRoller>> ROCKY_ROLLER = DEF_REG.register(
      "rocky_roller",
      () -> registerEntity(Builder.m_20704_(EntityRockyRoller::new, MobCategory.MONSTER).m_20699_(1.2F, 1.45F).setTrackingRange(8), "rocky_roller")
   );
   public static final RegistryObject<EntityType<EntityFlutter>> FLUTTER = DEF_REG.register(
      "flutter", () -> registerEntity(Builder.m_20704_(EntityFlutter::new, MobCategory.AMBIENT).m_20699_(0.5F, 0.7F).setTrackingRange(6), "flutter")
   );
   public static final RegistryObject<EntityType<EntityPollenBall>> POLLEN_BALL = DEF_REG.register(
      "pollen_ball",
      () -> registerEntity(
         Builder.m_20704_(EntityPollenBall::new, MobCategory.MISC).m_20699_(0.35F, 0.35F).setCustomClientFactory(EntityPollenBall::new).m_20719_(),
         "pollen_ball"
      )
   );
   public static final RegistryObject<EntityType<EntityGeladaMonkey>> GELADA_MONKEY = DEF_REG.register(
      "gelada_monkey",
      () -> registerEntity(Builder.m_20704_(EntityGeladaMonkey::new, MobCategory.CREATURE).m_20699_(1.2F, 1.2F).setTrackingRange(10), "gelada_monkey")
   );
   public static final RegistryObject<EntityType<EntityJerboa>> JERBOA = DEF_REG.register(
      "jerboa", () -> registerEntity(Builder.m_20704_(EntityJerboa::new, MobCategory.AMBIENT).m_20699_(0.5F, 0.5F).setTrackingRange(5), "jerboa")
   );
   public static final RegistryObject<EntityType<EntityTerrapin>> TERRAPIN = DEF_REG.register(
      "terrapin", () -> registerEntity(Builder.m_20704_(EntityTerrapin::new, MobCategory.WATER_AMBIENT).m_20699_(0.75F, 0.45F).setTrackingRange(5), "terrapin")
   );
   public static final RegistryObject<EntityType<EntityCombJelly>> COMB_JELLY = DEF_REG.register(
      "comb_jelly",
      () -> registerEntity(Builder.m_20704_(EntityCombJelly::new, MobCategory.WATER_AMBIENT).m_20699_(0.65F, 0.8F).setTrackingRange(5), "comb_jelly")
   );
   public static final RegistryObject<EntityType<EntityCosmicCod>> COSMIC_COD = DEF_REG.register(
      "cosmic_cod", () -> registerEntity(Builder.m_20704_(EntityCosmicCod::new, MobCategory.AMBIENT).m_20699_(0.85F, 0.4F).setTrackingRange(5), "cosmic_cod")
   );
   public static final RegistryObject<EntityType<EntityBunfungus>> BUNFUNGUS = DEF_REG.register(
      "bunfungus", () -> registerEntity(Builder.m_20704_(EntityBunfungus::new, MobCategory.CREATURE).m_20699_(1.85F, 2.1F).setTrackingRange(10), "bunfungus")
   );
   public static final RegistryObject<EntityType<EntityBison>> BISON = DEF_REG.register(
      "bison", () -> registerEntity(Builder.m_20704_(EntityBison::new, MobCategory.CREATURE).m_20699_(2.4F, 2.1F).setTrackingRange(10), "bison")
   );
   public static final RegistryObject<EntityType<EntityGiantSquid>> GIANT_SQUID = DEF_REG.register(
      "giant_squid",
      () -> registerEntity(Builder.m_20704_(EntityGiantSquid::new, MobCategory.WATER_CREATURE).m_20699_(0.9F, 1.2F).setTrackingRange(10), "giant_squid")
   );
   public static final RegistryObject<EntityType<EntitySquidGrapple>> SQUID_GRAPPLE = DEF_REG.register(
      "squid_grapple",
      () -> registerEntity(
         Builder.m_20704_(EntitySquidGrapple::new, MobCategory.MISC).m_20699_(0.5F, 0.5F).setCustomClientFactory(EntitySquidGrapple::new).m_20719_(),
         "squid_grapple"
      )
   );
   public static final RegistryObject<EntityType<EntitySeaBear>> SEA_BEAR = DEF_REG.register(
      "sea_bear", () -> registerEntity(Builder.m_20704_(EntitySeaBear::new, MobCategory.WATER_CREATURE).m_20699_(2.4F, 1.99F).setTrackingRange(10), "sea_bear")
   );
   public static final RegistryObject<EntityType<EntityDevilsHolePupfish>> DEVILS_HOLE_PUPFISH = DEF_REG.register(
      "devils_hole_pupfish",
      () -> registerEntity(
         Builder.m_20704_(EntityDevilsHolePupfish::new, MobCategory.WATER_AMBIENT).m_20699_(0.6F, 0.4F).setTrackingRange(4), "devils_hole_pupfish"
      )
   );
   public static final RegistryObject<EntityType<EntityCatfish>> CATFISH = DEF_REG.register(
      "catfish", () -> registerEntity(Builder.m_20704_(EntityCatfish::new, MobCategory.WATER_AMBIENT).m_20699_(0.9F, 0.6F).setTrackingRange(5), "catfish")
   );
   public static final RegistryObject<EntityType<EntityFlyingFish>> FLYING_FISH = DEF_REG.register(
      "flying_fish",
      () -> registerEntity(Builder.m_20704_(EntityFlyingFish::new, MobCategory.WATER_AMBIENT).m_20699_(0.6F, 0.4F).setTrackingRange(5), "flying_fish")
   );
   public static final RegistryObject<EntityType<EntitySkelewag>> SKELEWAG = DEF_REG.register(
      "skelewag",
      () -> registerEntity(
         Builder.m_20704_(EntitySkelewag::new, MobCategory.MONSTER)
            .m_20699_(2.0F, 1.2F)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(8),
         "skelewag"
      )
   );
   public static final RegistryObject<EntityType<EntityRainFrog>> RAIN_FROG = DEF_REG.register(
      "rain_frog", () -> registerEntity(Builder.m_20704_(EntityRainFrog::new, MobCategory.AMBIENT).m_20699_(0.55F, 0.5F).setTrackingRange(5), "rain_frog")
   );
   public static final RegistryObject<EntityType<EntityPotoo>> POTOO = DEF_REG.register(
      "potoo", () -> registerEntity(Builder.m_20704_(EntityPotoo::new, MobCategory.CREATURE).m_20699_(0.6F, 0.8F).setTrackingRange(10), "potoo")
   );
   public static final RegistryObject<EntityType<EntityMudskipper>> MUDSKIPPER = DEF_REG.register(
      "mudskipper",
      () -> registerEntity(Builder.m_20704_(EntityMudskipper::new, MobCategory.CREATURE).m_20699_(0.7F, 0.44F).setTrackingRange(10), "mudskipper")
   );
   public static final RegistryObject<EntityType<EntityMudBall>> MUD_BALL = DEF_REG.register(
      "mud_ball",
      () -> registerEntity(
         Builder.m_20704_(EntityMudBall::new, MobCategory.MISC).m_20699_(0.35F, 0.35F).setCustomClientFactory(EntityMudBall::new).m_20719_(), "mud_ball"
      )
   );
   public static final RegistryObject<EntityType<EntityRhinoceros>> RHINOCEROS = DEF_REG.register(
      "rhinoceros", () -> registerEntity(Builder.m_20704_(EntityRhinoceros::new, MobCategory.CREATURE).m_20699_(2.3F, 2.4F).setTrackingRange(10), "rhinoceros")
   );
   public static final RegistryObject<EntityType<EntitySugarGlider>> SUGAR_GLIDER = DEF_REG.register(
      "sugar_glider",
      () -> registerEntity(Builder.m_20704_(EntitySugarGlider::new, MobCategory.CREATURE).m_20699_(0.8F, 0.45F).setTrackingRange(10), "sugar_glider")
   );
   public static final RegistryObject<EntityType<EntityFarseer>> FARSEER = DEF_REG.register(
      "farseer",
      () -> registerEntity(
         Builder.m_20704_(EntityFarseer::new, MobCategory.MONSTER)
            .m_20699_(0.99F, 1.5F)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .m_20719_()
            .setTrackingRange(8),
         "farseer"
      )
   );
   public static final RegistryObject<EntityType<EntitySkreecher>> SKREECHER = DEF_REG.register(
      "skreecher",
      () -> registerEntity(
         Builder.m_20704_(EntitySkreecher::new, MobCategory.CREATURE)
            .m_20699_(0.99F, 0.95F)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(8),
         "skreecher"
      )
   );
   public static final RegistryObject<EntityType<EntityUnderminer>> UNDERMINER = DEF_REG.register(
      "underminer", () -> registerEntity(Builder.m_20704_(EntityUnderminer::new, MobCategory.AMBIENT).m_20699_(0.8F, 1.8F).setTrackingRange(8), "underminer")
   );
   public static final RegistryObject<EntityType<EntityMurmur>> MURMUR = DEF_REG.register(
      "murmur", () -> registerEntity(Builder.m_20704_(EntityMurmur::new, MobCategory.MONSTER).m_20699_(0.7F, 1.45F).setTrackingRange(8), "murmur")
   );
   public static final RegistryObject<EntityType<EntityMurmurHead>> MURMUR_HEAD = DEF_REG.register(
      "murmur_head",
      () -> registerEntity(Builder.m_20704_(EntityMurmurHead::new, MobCategory.MONSTER).m_20699_(0.55F, 0.55F).setTrackingRange(8), "murmur_head")
   );
   public static final RegistryObject<EntityType<EntityTendonSegment>> TENDON_SEGMENT = DEF_REG.register(
      "tendon_segment",
      () -> registerEntity(
         Builder.m_20704_(EntityTendonSegment::new, MobCategory.MISC).m_20699_(0.1F, 0.1F).setCustomClientFactory(EntityTendonSegment::new).m_20719_(),
         "tendon_segment"
      )
   );
   public static final RegistryObject<EntityType<EntitySkunk>> SKUNK = DEF_REG.register(
      "skunk", () -> registerEntity(Builder.m_20704_(EntitySkunk::new, MobCategory.CREATURE).m_20699_(0.85F, 0.65F).setTrackingRange(10), "skunk")
   );
   public static final RegistryObject<EntityType<EntityFart>> FART = DEF_REG.register(
      "fart",
      () -> registerEntity(Builder.m_20704_(EntityFart::new, MobCategory.MISC).m_20699_(0.7F, 0.3F).setCustomClientFactory(EntityFart::new).m_20719_(), "fart")
   );
   public static final RegistryObject<EntityType<EntityBananaSlug>> BANANA_SLUG = DEF_REG.register(
      "banana_slug",
      () -> registerEntity(Builder.m_20704_(EntityBananaSlug::new, MobCategory.CREATURE).m_20699_(0.8F, 0.4F).setTrackingRange(10), "banana_slug")
   );
   public static final RegistryObject<EntityType<EntityBlueJay>> BLUE_JAY = DEF_REG.register(
      "blue_jay", () -> registerEntity(Builder.m_20704_(EntityBlueJay::new, MobCategory.CREATURE).m_20699_(0.5F, 0.6F).setTrackingRange(10), "blue_jay")
   );
   public static final RegistryObject<EntityType<EntityCaiman>> CAIMAN = DEF_REG.register(
      "caiman", () -> registerEntity(Builder.m_20704_(EntityCaiman::new, MobCategory.CREATURE).m_20699_(1.3F, 0.6F).setTrackingRange(10), "caiman")
   );
   public static final RegistryObject<EntityType<EntityTriops>> TRIOPS = DEF_REG.register(
      "triops", () -> registerEntity(Builder.m_20704_(EntityTriops::new, MobCategory.WATER_AMBIENT).m_20699_(0.7F, 0.25F).setTrackingRange(5), "triops")
   );

   private static EntityType registerEntity(Builder builder, String entityName) {
      return builder.m_20712_(entityName);
   }

   @SubscribeEvent
   public static void initializeAttributes(EntityAttributeCreationEvent event) {
      Type spawnsOnLeaves = Type.create("am_leaves", AMEntityRegistry::createLeavesSpawnPlacement);
      SpawnPlacements.m_21754_((EntityType)GRIZZLY_BEAR.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)ROADRUNNER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityRoadrunner::canRoadrunnerSpawn);
      SpawnPlacements.m_21754_((EntityType)BONE_SERPENT.get(), Type.IN_LAVA, Types.MOTION_BLOCKING_NO_LEAVES, EntityBoneSerpent::canBoneSerpentSpawn);
      SpawnPlacements.m_21754_((EntityType)GAZELLE.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)CROCODILE.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCrocodile::canCrocodileSpawn);
      SpawnPlacements.m_21754_((EntityType)FLY.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityFly::canFlySpawn);
      SpawnPlacements.m_21754_((EntityType)HUMMINGBIRD.get(), Type.ON_GROUND, Types.MOTION_BLOCKING, EntityHummingbird::canHummingbirdSpawn);
      SpawnPlacements.m_21754_((EntityType)ORCA.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityOrca::canOrcaSpawn);
      SpawnPlacements.m_21754_((EntityType)SUNBIRD.get(), Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntitySunbird::canSunbirdSpawn);
      SpawnPlacements.m_21754_((EntityType)GORILLA.get(), Type.ON_GROUND, Types.MOTION_BLOCKING, EntityGorilla::canGorillaSpawn);
      SpawnPlacements.m_21754_((EntityType)CRIMSON_MOSQUITO.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCrimsonMosquito::canMosquitoSpawn);
      SpawnPlacements.m_21754_((EntityType)RATTLESNAKE.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityRattlesnake::canRattlesnakeSpawn);
      SpawnPlacements.m_21754_((EntityType)ENDERGRADE.get(), Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntityEndergrade::canEndergradeSpawn);
      SpawnPlacements.m_21754_(
         (EntityType)HAMMERHEAD_SHARK.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityHammerheadShark::canHammerheadSharkSpawn
      );
      SpawnPlacements.m_21754_((EntityType)LOBSTER.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityLobster::canLobsterSpawn);
      SpawnPlacements.m_21754_((EntityType)KOMODO_DRAGON.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityKomodoDragon::canKomodoDragonSpawn);
      SpawnPlacements.m_21754_((EntityType)CAPUCHIN_MONKEY.get(), spawnsOnLeaves, Types.MOTION_BLOCKING, EntityCapuchinMonkey::canCapuchinSpawn);
      SpawnPlacements.m_21754_((EntityType)CENTIPEDE_HEAD.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCentipedeHead::canCentipedeSpawn);
      SpawnPlacements.m_21754_((EntityType)WARPED_TOAD.get(), Type.ON_GROUND, Types.MOTION_BLOCKING, EntityWarpedToad::canWarpedToadSpawn);
      SpawnPlacements.m_21754_((EntityType)MOOSE.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityMoose::canMooseSpawn);
      SpawnPlacements.m_21754_((EntityType)MIMICUBE.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Mob::m_217057_);
      SpawnPlacements.m_21754_((EntityType)RACCOON.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)BLOBFISH.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityBlobfish::canBlobfishSpawn);
      SpawnPlacements.m_21754_((EntityType)SEAL.get(), Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntitySeal::canSealSpawn);
      SpawnPlacements.m_21754_((EntityType)COCKROACH.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCockroach::canCockroachSpawn);
      SpawnPlacements.m_21754_((EntityType)SHOEBILL.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)ELEPHANT.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)SOUL_VULTURE.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntitySoulVulture::canVultureSpawn);
      SpawnPlacements.m_21754_((EntityType)SNOW_LEOPARD.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntitySnowLeopard::canSnowLeopardSpawn);
      SpawnPlacements.m_21754_((EntityType)CROW.get(), Type.ON_GROUND, Types.MOTION_BLOCKING, EntityCrow::canCrowSpawn);
      SpawnPlacements.m_21754_(
         (EntityType)ALLIGATOR_SNAPPING_TURTLE.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityAlligatorSnappingTurtle::canTurtleSpawn
      );
      SpawnPlacements.m_21754_((EntityType)MUNGUS.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityMungus::canMungusSpawn);
      SpawnPlacements.m_21754_((EntityType)MANTIS_SHRIMP.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityMantisShrimp::canMantisShrimpSpawn);
      SpawnPlacements.m_21754_((EntityType)GUSTER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityGuster::canGusterSpawn);
      SpawnPlacements.m_21754_((EntityType)WARPED_MOSCO.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::m_219019_);
      SpawnPlacements.m_21754_((EntityType)STRADDLER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityStraddler::canStraddlerSpawn);
      SpawnPlacements.m_21754_((EntityType)STRADPOLE.get(), Type.IN_LAVA, Types.MOTION_BLOCKING_NO_LEAVES, EntityStradpole::canStradpoleSpawn);
      SpawnPlacements.m_21754_((EntityType)EMU.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityEmu::canEmuSpawn);
      SpawnPlacements.m_21754_((EntityType)PLATYPUS.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityPlatypus::canPlatypusSpawn);
      SpawnPlacements.m_21754_((EntityType)DROPBEAR.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::m_219019_);
      SpawnPlacements.m_21754_((EntityType)TASMANIAN_DEVIL.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)KANGAROO.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityKangaroo::canKangarooSpawn);
      SpawnPlacements.m_21754_((EntityType)CACHALOT_WHALE.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityCachalotWhale::canCachalotWhaleSpawn);
      SpawnPlacements.m_21754_((EntityType)LEAFCUTTER_ANT.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)ENDERIOPHAGE.get(), Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntityEnderiophage::canEnderiophageSpawn);
      SpawnPlacements.m_21754_((EntityType)BALD_EAGLE.get(), Type.ON_GROUND, Types.MOTION_BLOCKING, EntityBaldEagle::canEagleSpawn);
      SpawnPlacements.m_21754_((EntityType)TIGER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityTiger::canTigerSpawn);
      SpawnPlacements.m_21754_((EntityType)TARANTULA_HAWK.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityTarantulaHawk::canTarantulaHawkSpawn);
      SpawnPlacements.m_21754_((EntityType)VOID_WORM.get(), Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntityVoidWorm::canVoidWormSpawn);
      SpawnPlacements.m_21754_((EntityType)FRILLED_SHARK.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityFrilledShark::canFrilledSharkSpawn);
      SpawnPlacements.m_21754_((EntityType)MIMIC_OCTOPUS.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityMimicOctopus::canMimicOctopusSpawn);
      SpawnPlacements.m_21754_((EntityType)SEAGULL.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntitySeagull::canSeagullSpawn);
      SpawnPlacements.m_21754_((EntityType)FROSTSTALKER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityFroststalker::canFroststalkerSpawn);
      SpawnPlacements.m_21754_((EntityType)TUSKLIN.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityTusklin::canTusklinSpawn);
      SpawnPlacements.m_21754_((EntityType)LAVIATHAN.get(), Type.IN_LAVA, Types.MOTION_BLOCKING_NO_LEAVES, EntityLaviathan::canLaviathanSpawn);
      SpawnPlacements.m_21754_((EntityType)COSMAW.get(), Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, EntityCosmaw::canCosmawSpawn);
      SpawnPlacements.m_21754_((EntityType)TOUCAN.get(), spawnsOnLeaves, Types.MOTION_BLOCKING, EntityToucan::canToucanSpawn);
      SpawnPlacements.m_21754_((EntityType)MANED_WOLF.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)ANACONDA.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityAnaconda::canAnacondaSpawn);
      SpawnPlacements.m_21754_((EntityType)ANTEATER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityAnteater::canAnteaterSpawn);
      SpawnPlacements.m_21754_((EntityType)ROCKY_ROLLER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityRockyRoller::checkRockyRollerSpawnRules);
      SpawnPlacements.m_21754_((EntityType)FLUTTER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityFlutter::canFlutterSpawn);
      SpawnPlacements.m_21754_((EntityType)GELADA_MONKEY.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)JERBOA.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityJerboa::canJerboaSpawn);
      SpawnPlacements.m_21754_((EntityType)TERRAPIN.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityTerrapin::canTerrapinSpawn);
      SpawnPlacements.m_21754_((EntityType)COMB_JELLY.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityCombJelly::canCombJellySpawn);
      SpawnPlacements.m_21754_((EntityType)BUNFUNGUS.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityBunfungus::canBunfungusSpawn);
      SpawnPlacements.m_21754_((EntityType)BISON.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)GIANT_SQUID.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityGiantSquid::canGiantSquidSpawn);
      SpawnPlacements.m_21754_((EntityType)DEVILS_HOLE_PUPFISH.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityDevilsHolePupfish::canPupfishSpawn);
      SpawnPlacements.m_21754_((EntityType)CATFISH.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntityCatfish::canCatfishSpawn);
      SpawnPlacements.m_21754_((EntityType)FLYING_FISH.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::m_218282_);
      SpawnPlacements.m_21754_((EntityType)SKELEWAG.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, EntitySkelewag::canSkelewagSpawn);
      SpawnPlacements.m_21754_((EntityType)RAIN_FROG.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityRainFrog::canRainFrogSpawn);
      SpawnPlacements.m_21754_((EntityType)POTOO.get(), spawnsOnLeaves, Types.MOTION_BLOCKING, EntityPotoo::canPotooSpawn);
      SpawnPlacements.m_21754_((EntityType)MUDSKIPPER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityMudskipper::canMudskipperSpawn);
      SpawnPlacements.m_21754_((EntityType)RHINOCEROS.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)SUGAR_GLIDER.get(), spawnsOnLeaves, Types.MOTION_BLOCKING, EntitySugarGlider::canSugarGliderSpawn);
      SpawnPlacements.m_21754_((EntityType)FARSEER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityFarseer::checkFarseerSpawnRules);
      SpawnPlacements.m_21754_((EntityType)SKREECHER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntitySkreecher::checkSkreecherSpawnRules);
      SpawnPlacements.m_21754_((EntityType)UNDERMINER.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityUnderminer::checkUnderminerSpawnRules);
      SpawnPlacements.m_21754_((EntityType)MURMUR.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityMurmur::checkMurmurSpawnRules);
      SpawnPlacements.m_21754_((EntityType)SKUNK.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::m_218104_);
      SpawnPlacements.m_21754_((EntityType)BANANA_SLUG.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityBananaSlug::checkBananaSlugSpawnRules);
      SpawnPlacements.m_21754_((EntityType)BLUE_JAY.get(), spawnsOnLeaves, Types.MOTION_BLOCKING, EntityBlueJay::checkBlueJaySpawnRules);
      SpawnPlacements.m_21754_((EntityType)CAIMAN.get(), Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, EntityCaiman::canCaimanSpawn);
      SpawnPlacements.m_21754_((EntityType)TRIOPS.get(), Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::m_218282_);
      event.put((EntityType)GRIZZLY_BEAR.get(), EntityGrizzlyBear.bakeAttributes().m_22265_());
      event.put((EntityType)ROADRUNNER.get(), EntityRoadrunner.bakeAttributes().m_22265_());
      event.put((EntityType)BONE_SERPENT.get(), EntityBoneSerpent.bakeAttributes().m_22265_());
      event.put((EntityType)BONE_SERPENT_PART.get(), EntityBoneSerpentPart.bakeAttributes().m_22265_());
      event.put((EntityType)GAZELLE.get(), EntityGazelle.bakeAttributes().m_22265_());
      event.put((EntityType)CROCODILE.get(), EntityCrocodile.bakeAttributes().m_22265_());
      event.put((EntityType)FLY.get(), EntityFly.bakeAttributes().m_22265_());
      event.put((EntityType)HUMMINGBIRD.get(), EntityHummingbird.bakeAttributes().m_22265_());
      event.put((EntityType)ORCA.get(), EntityOrca.bakeAttributes().m_22265_());
      event.put((EntityType)SUNBIRD.get(), EntitySunbird.bakeAttributes().m_22265_());
      event.put((EntityType)GORILLA.get(), EntityGorilla.bakeAttributes().m_22265_());
      event.put((EntityType)CRIMSON_MOSQUITO.get(), EntityCrimsonMosquito.bakeAttributes().m_22265_());
      event.put((EntityType)RATTLESNAKE.get(), EntityRattlesnake.bakeAttributes().m_22265_());
      event.put((EntityType)ENDERGRADE.get(), EntityEndergrade.bakeAttributes().m_22265_());
      event.put((EntityType)HAMMERHEAD_SHARK.get(), EntityHammerheadShark.bakeAttributes().m_22265_());
      event.put((EntityType)LOBSTER.get(), EntityLobster.bakeAttributes().m_22265_());
      event.put((EntityType)KOMODO_DRAGON.get(), EntityKomodoDragon.bakeAttributes().m_22265_());
      event.put((EntityType)CAPUCHIN_MONKEY.get(), EntityCapuchinMonkey.bakeAttributes().m_22265_());
      event.put((EntityType)CENTIPEDE_HEAD.get(), EntityCentipedeHead.bakeAttributes().m_22265_());
      event.put((EntityType)CENTIPEDE_BODY.get(), EntityCentipedeBody.bakeAttributes().m_22265_());
      event.put((EntityType)CENTIPEDE_TAIL.get(), EntityCentipedeTail.bakeAttributes().m_22265_());
      event.put((EntityType)WARPED_TOAD.get(), EntityWarpedToad.bakeAttributes().m_22265_());
      event.put((EntityType)MOOSE.get(), EntityMoose.bakeAttributes().m_22265_());
      event.put((EntityType)MIMICUBE.get(), EntityMimicube.bakeAttributes().m_22265_());
      event.put((EntityType)RACCOON.get(), EntityRaccoon.bakeAttributes().m_22265_());
      event.put((EntityType)BLOBFISH.get(), EntityBlobfish.bakeAttributes().m_22265_());
      event.put((EntityType)SEAL.get(), EntitySeal.bakeAttributes().m_22265_());
      event.put((EntityType)COCKROACH.get(), EntityCockroach.bakeAttributes().m_22265_());
      event.put((EntityType)SHOEBILL.get(), EntityShoebill.bakeAttributes().m_22265_());
      event.put((EntityType)ELEPHANT.get(), EntityElephant.bakeAttributes().m_22265_());
      event.put((EntityType)SOUL_VULTURE.get(), EntitySoulVulture.bakeAttributes().m_22265_());
      event.put((EntityType)SNOW_LEOPARD.get(), EntitySnowLeopard.bakeAttributes().m_22265_());
      event.put((EntityType)SPECTRE.get(), EntitySpectre.bakeAttributes().m_22265_());
      event.put((EntityType)CROW.get(), EntityCrow.bakeAttributes().m_22265_());
      event.put((EntityType)ALLIGATOR_SNAPPING_TURTLE.get(), EntityAlligatorSnappingTurtle.bakeAttributes().m_22265_());
      event.put((EntityType)MUNGUS.get(), EntityMungus.bakeAttributes().m_22265_());
      event.put((EntityType)MANTIS_SHRIMP.get(), EntityMantisShrimp.bakeAttributes().m_22265_());
      event.put((EntityType)GUSTER.get(), EntityGuster.bakeAttributes().m_22265_());
      event.put((EntityType)WARPED_MOSCO.get(), EntityWarpedMosco.bakeAttributes().m_22265_());
      event.put((EntityType)STRADDLER.get(), EntityStraddler.bakeAttributes().m_22265_());
      event.put((EntityType)STRADPOLE.get(), EntityStradpole.bakeAttributes().m_22265_());
      event.put((EntityType)EMU.get(), EntityEmu.bakeAttributes().m_22265_());
      event.put((EntityType)PLATYPUS.get(), EntityPlatypus.bakeAttributes().m_22265_());
      event.put((EntityType)DROPBEAR.get(), EntityDropBear.bakeAttributes().m_22265_());
      event.put((EntityType)TASMANIAN_DEVIL.get(), EntityTasmanianDevil.bakeAttributes().m_22265_());
      event.put((EntityType)KANGAROO.get(), EntityKangaroo.bakeAttributes().m_22265_());
      event.put((EntityType)CACHALOT_WHALE.get(), EntityCachalotWhale.bakeAttributes().m_22265_());
      event.put((EntityType)LEAFCUTTER_ANT.get(), EntityLeafcutterAnt.bakeAttributes().m_22265_());
      event.put((EntityType)ENDERIOPHAGE.get(), EntityEnderiophage.bakeAttributes().m_22265_());
      event.put((EntityType)BALD_EAGLE.get(), EntityBaldEagle.bakeAttributes().m_22265_());
      event.put((EntityType)TIGER.get(), EntityTiger.bakeAttributes().m_22265_());
      event.put((EntityType)TARANTULA_HAWK.get(), EntityTarantulaHawk.bakeAttributes().m_22265_());
      event.put((EntityType)VOID_WORM.get(), EntityVoidWorm.bakeAttributes().m_22265_());
      event.put((EntityType)VOID_WORM_PART.get(), EntityVoidWormPart.bakeAttributes().m_22265_());
      event.put((EntityType)FRILLED_SHARK.get(), EntityFrilledShark.bakeAttributes().m_22265_());
      event.put((EntityType)MIMIC_OCTOPUS.get(), EntityMimicOctopus.bakeAttributes().m_22265_());
      event.put((EntityType)SEAGULL.get(), EntitySeagull.bakeAttributes().m_22265_());
      event.put((EntityType)FROSTSTALKER.get(), EntityFroststalker.bakeAttributes().m_22265_());
      event.put((EntityType)TUSKLIN.get(), EntityTusklin.bakeAttributes().m_22265_());
      event.put((EntityType)LAVIATHAN.get(), EntityLaviathan.bakeAttributes().m_22265_());
      event.put((EntityType)COSMAW.get(), EntityCosmaw.bakeAttributes().m_22265_());
      event.put((EntityType)TOUCAN.get(), EntityToucan.bakeAttributes().m_22265_());
      event.put((EntityType)MANED_WOLF.get(), EntityManedWolf.bakeAttributes().m_22265_());
      event.put((EntityType)ANACONDA.get(), EntityAnaconda.bakeAttributes().m_22265_());
      event.put((EntityType)ANACONDA_PART.get(), EntityAnacondaPart.bakeAttributes().m_22265_());
      event.put((EntityType)ANTEATER.get(), EntityAnteater.bakeAttributes().m_22265_());
      event.put((EntityType)ROCKY_ROLLER.get(), EntityRockyRoller.bakeAttributes().m_22265_());
      event.put((EntityType)FLUTTER.get(), EntityFlutter.bakeAttributes().m_22265_());
      event.put((EntityType)GELADA_MONKEY.get(), EntityGeladaMonkey.bakeAttributes().m_22265_());
      event.put((EntityType)JERBOA.get(), EntityJerboa.bakeAttributes().m_22265_());
      event.put((EntityType)TERRAPIN.get(), EntityTerrapin.bakeAttributes().m_22265_());
      event.put((EntityType)COMB_JELLY.get(), EntityCombJelly.bakeAttributes().m_22265_());
      event.put((EntityType)COSMIC_COD.get(), EntityCosmicCod.bakeAttributes().m_22265_());
      event.put((EntityType)BUNFUNGUS.get(), EntityBunfungus.bakeAttributes().m_22265_());
      event.put((EntityType)BISON.get(), EntityBison.bakeAttributes().m_22265_());
      event.put((EntityType)GIANT_SQUID.get(), EntityGiantSquid.bakeAttributes().m_22265_());
      event.put((EntityType)SEA_BEAR.get(), EntitySeaBear.bakeAttributes().m_22265_());
      event.put((EntityType)DEVILS_HOLE_PUPFISH.get(), EntityDevilsHolePupfish.bakeAttributes().m_22265_());
      event.put((EntityType)CATFISH.get(), EntityCatfish.bakeAttributes().m_22265_());
      event.put((EntityType)FLYING_FISH.get(), EntityFlyingFish.bakeAttributes().m_22265_());
      event.put((EntityType)SKELEWAG.get(), EntitySkelewag.bakeAttributes().m_22265_());
      event.put((EntityType)RAIN_FROG.get(), EntityRainFrog.bakeAttributes().m_22265_());
      event.put((EntityType)POTOO.get(), EntityPotoo.bakeAttributes().m_22265_());
      event.put((EntityType)MUDSKIPPER.get(), EntityMudskipper.bakeAttributes().m_22265_());
      event.put((EntityType)RHINOCEROS.get(), EntityRhinoceros.bakeAttributes().m_22265_());
      event.put((EntityType)SUGAR_GLIDER.get(), EntitySugarGlider.bakeAttributes().m_22265_());
      event.put((EntityType)FARSEER.get(), EntityFarseer.bakeAttributes().m_22265_());
      event.put((EntityType)SKREECHER.get(), EntitySkreecher.bakeAttributes().m_22265_());
      event.put((EntityType)UNDERMINER.get(), EntityUnderminer.bakeAttributes().m_22265_());
      event.put((EntityType)MURMUR.get(), EntityMurmur.bakeAttributes().m_22265_());
      event.put((EntityType)MURMUR_HEAD.get(), EntityMurmurHead.bakeAttributes().m_22265_());
      event.put((EntityType)SKUNK.get(), EntitySkunk.bakeAttributes().m_22265_());
      event.put((EntityType)BANANA_SLUG.get(), EntityBananaSlug.bakeAttributes().m_22265_());
      event.put((EntityType)BLUE_JAY.get(), EntityBlueJay.bakeAttributes().m_22265_());
      event.put((EntityType)CAIMAN.get(), EntityCaiman.bakeAttributes().m_22265_());
      event.put((EntityType)TRIOPS.get(), EntityTriops.bakeAttributes().m_22265_());
   }

   public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> entityTag) {
      return entityTag == null ? Predicates.alwaysFalse() : e -> e.m_6084_() && e.m_6095_().m_204039_(entityTag);
   }

   public static Predicate<LivingEntity> buildPredicateFromTagTameable(TagKey<EntityType<?>> entityTag, LivingEntity owner) {
      return entityTag == null ? Predicates.alwaysFalse() : e -> e.m_6084_() && e.m_6095_().m_204039_(entityTag) && !owner.m_7307_(e);
   }

   public static boolean rollSpawn(int rolls, RandomSource random, MobSpawnType reason) {
      return reason == MobSpawnType.SPAWNER ? true : rolls <= 0 || random.m_188503_(rolls) == 0;
   }

   public static boolean createLeavesSpawnPlacement(LevelReader level, BlockPos pos, EntityType<?> type) {
      BlockPos blockpos = pos.m_7494_();
      BlockPos blockpos1 = pos.m_7495_();
      FluidState fluidstate = level.m_6425_(pos);
      BlockState blockstate = level.m_8055_(pos);
      BlockState blockstate1 = level.m_8055_(blockpos1);
      return !blockstate1.isValidSpawn(level, blockpos1, Type.ON_GROUND, type) && !blockstate1.m_204336_(BlockTags.f_13035_)
         ? false
         : NaturalSpawner.m_47056_(level, pos, blockstate, fluidstate, type)
            && NaturalSpawner.m_47056_(level, blockpos, level.m_8055_(blockpos), level.m_6425_(blockpos), type);
   }
}
