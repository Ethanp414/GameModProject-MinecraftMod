package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownLingeringPotion;
import net.minecraft.world.entity.projectile.ThrownSplashPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.ChestRaft;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.entity.vehicle.Raft;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class EntityType<T extends Entity> implements FeatureElement, EntityTypeTest<Entity, T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Holder.Reference<EntityType<?>> builtInRegistryHolder = BuiltInRegistries.ENTITY_TYPE.createIntrusiveHolder(this);
   public static final Codec<EntityType<?>> CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec();
   public static final StreamCodec<RegistryFriendlyByteBuf, EntityType<?>> STREAM_CODEC = ByteBufCodecs.registry(Registries.ENTITY_TYPE);
   private static final float MAGIC_HORSE_WIDTH = 1.3964844F;
   private static final int DISPLAY_TRACKING_RANGE = 10;
   public static final EntityType<Boat> ACACIA_BOAT = register(
      "acacia_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.ACACIA_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> ACACIA_CHEST_BOAT = register(
      "acacia_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.ACACIA_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Allay> ALLAY = register(
      "allay",
      EntityType.Builder.<Allay>of(Allay::new, MobCategory.CREATURE)
         .sized(0.35F, 0.6F)
         .eyeHeight(0.36F)
         .ridingOffset(0.04F)
         .clientTrackingRange(8)
         .updateInterval(2)
   );
   public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD = register(
      "area_effect_cloud",
      EntityType.Builder.<AreaEffectCloud>of(AreaEffectCloud::new, MobCategory.MISC)
         .noLootTable()
         .fireImmune()
         .sized(6.0F, 0.5F)
         .clientTrackingRange(10)
         .updateInterval(Integer.MAX_VALUE)
   );
   public static final EntityType<Armadillo> ARMADILLO = register(
      "armadillo", EntityType.Builder.<Armadillo>of(Armadillo::new, MobCategory.CREATURE).sized(0.7F, 0.65F).eyeHeight(0.26F).clientTrackingRange(10)
   );
   public static final EntityType<ArmorStand> ARMOR_STAND = register(
      "armor_stand", EntityType.Builder.<ArmorStand>of(ArmorStand::new, MobCategory.MISC).sized(0.5F, 1.975F).eyeHeight(1.7775F).clientTrackingRange(10)
   );
   public static final EntityType<Arrow> ARROW = register(
      "arrow",
      EntityType.Builder.<Arrow>of(Arrow::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20)
   );
   public static final EntityType<Axolotl> AXOLOTL = register(
      "axolotl", EntityType.Builder.<Axolotl>of(Axolotl::new, MobCategory.AXOLOTLS).sized(0.75F, 0.42F).eyeHeight(0.2751F).clientTrackingRange(10)
   );
   public static final EntityType<ChestRaft> BAMBOO_CHEST_RAFT = register(
      "bamboo_chest_raft",
      EntityType.Builder.<ChestRaft>of(chestRaftFactory(() -> Items.BAMBOO_CHEST_RAFT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Raft> BAMBOO_RAFT = register(
      "bamboo_raft",
      EntityType.Builder.<Raft>of(raftFactory(() -> Items.BAMBOO_RAFT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Bat> BAT = register(
      "bat", EntityType.Builder.<Bat>of(Bat::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).eyeHeight(0.45F).clientTrackingRange(5)
   );
   public static final EntityType<Bee> BEE = register(
      "bee", EntityType.Builder.<Bee>of(Bee::new, MobCategory.CREATURE).sized(0.7F, 0.6F).eyeHeight(0.3F).clientTrackingRange(8)
   );
   public static final EntityType<Boat> BIRCH_BOAT = register(
      "birch_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.BIRCH_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> BIRCH_CHEST_BOAT = register(
      "birch_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.BIRCH_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Blaze> BLAZE = register(
      "blaze", EntityType.Builder.<Blaze>of(Blaze::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8).notInPeaceful()
   );
   public static final EntityType<Display.BlockDisplay> BLOCK_DISPLAY = register(
      "block_display",
      EntityType.Builder.<Display.BlockDisplay>of(Display.BlockDisplay::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.0F, 0.0F)
         .clientTrackingRange(10)
         .updateInterval(1)
   );
   public static final EntityType<Bogged> BOGGED = register(
      "bogged",
      EntityType.Builder.<Bogged>of(Bogged::new, MobCategory.MONSTER)
         .sized(0.6F, 1.99F)
         .eyeHeight(1.74F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Breeze> BREEZE = register(
      "breeze", EntityType.Builder.<Breeze>of(Breeze::new, MobCategory.MONSTER).sized(0.6F, 1.77F).eyeHeight(1.3452F).clientTrackingRange(10).notInPeaceful()
   );
   public static final EntityType<BreezeWindCharge> BREEZE_WIND_CHARGE = register(
      "breeze_wind_charge",
      EntityType.Builder.<BreezeWindCharge>of(BreezeWindCharge::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.3125F, 0.3125F)
         .eyeHeight(0.0F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<Camel> CAMEL = register(
      "camel", EntityType.Builder.<Camel>of(Camel::new, MobCategory.CREATURE).sized(1.7F, 2.375F).eyeHeight(2.275F).clientTrackingRange(10)
   );
   public static final EntityType<Cat> CAT = register(
      "cat",
      EntityType.Builder.<Cat>of(Cat::new, MobCategory.CREATURE).sized(0.6F, 0.7F).eyeHeight(0.35F).passengerAttachments(0.5125F).clientTrackingRange(8)
   );
   public static final EntityType<CaveSpider> CAVE_SPIDER = register(
      "cave_spider",
      EntityType.Builder.<CaveSpider>of(CaveSpider::new, MobCategory.MONSTER).sized(0.7F, 0.5F).eyeHeight(0.45F).clientTrackingRange(8).notInPeaceful()
   );
   public static final EntityType<Boat> CHERRY_BOAT = register(
      "cherry_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.CHERRY_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> CHERRY_CHEST_BOAT = register(
      "cherry_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.CHERRY_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<MinecartChest> CHEST_MINECART = register(
      "chest_minecart",
      EntityType.Builder.<MinecartChest>of(MinecartChest::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.98F, 0.7F)
         .passengerAttachments(0.1875F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Chicken> CHICKEN = register(
      "chicken",
      EntityType.Builder.<Chicken>of(Chicken::new, MobCategory.CREATURE)
         .sized(0.4F, 0.7F)
         .eyeHeight(0.644F)
         .passengerAttachments(new Vec3(0.0, 0.7, -0.1))
         .clientTrackingRange(10)
   );
   public static final EntityType<Cod> COD = register(
      "cod", EntityType.Builder.<Cod>of(Cod::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).eyeHeight(0.195F).clientTrackingRange(4)
   );
   public static final EntityType<CopperGolem> COPPER_GOLEM = register(
      "copper_golem", EntityType.Builder.<CopperGolem>of(CopperGolem::new, MobCategory.MISC).sized(0.49F, 0.98F).eyeHeight(0.8125F).clientTrackingRange(10)
   );
   public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART = register(
      "command_block_minecart",
      EntityType.Builder.<MinecartCommandBlock>of(MinecartCommandBlock::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.98F, 0.7F)
         .passengerAttachments(0.1875F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Cow> COW = register(
      "cow",
      EntityType.Builder.<Cow>of(Cow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).eyeHeight(1.3F).passengerAttachments(1.36875F).clientTrackingRange(10)
   );
   public static final EntityType<Creaking> CREAKING = register(
      "creaking", EntityType.Builder.<Creaking>of(Creaking::new, MobCategory.MONSTER).sized(0.9F, 2.7F).eyeHeight(2.3F).clientTrackingRange(8).notInPeaceful()
   );
   public static final EntityType<Creeper> CREEPER = register(
      "creeper", EntityType.Builder.<Creeper>of(Creeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).notInPeaceful()
   );
   public static final EntityType<Boat> DARK_OAK_BOAT = register(
      "dark_oak_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.DARK_OAK_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> DARK_OAK_CHEST_BOAT = register(
      "dark_oak_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.DARK_OAK_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Dolphin> DOLPHIN = register(
      "dolphin", EntityType.Builder.<Dolphin>of(Dolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F).eyeHeight(0.3F)
   );
   public static final EntityType<Donkey> DONKEY = register(
      "donkey",
      EntityType.Builder.<Donkey>of(Donkey::new, MobCategory.CREATURE)
         .sized(1.3964844F, 1.5F)
         .eyeHeight(1.425F)
         .passengerAttachments(1.1125F)
         .clientTrackingRange(10)
   );
   public static final EntityType<DragonFireball> DRAGON_FIREBALL = register(
      "dragon_fireball",
      EntityType.Builder.<DragonFireball>of(DragonFireball::new, MobCategory.MISC).noLootTable().sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Drowned> DROWNED = register(
      "drowned",
      EntityType.Builder.<Drowned>of(Drowned::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.74F)
         .passengerAttachments(2.0125F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<ThrownEgg> EGG = register(
      "egg", EntityType.Builder.<ThrownEgg>of(ThrownEgg::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<ElderGuardian> ELDER_GUARDIAN = register(
      "elder_guardian",
      EntityType.Builder.<ElderGuardian>of(ElderGuardian::new, MobCategory.MONSTER)
         .sized(1.9975F, 1.9975F)
         .eyeHeight(0.99875F)
         .passengerAttachments(2.350625F)
         .clientTrackingRange(10)
         .notInPeaceful()
   );
   public static final EntityType<EnderMan> ENDERMAN = register(
      "enderman",
      EntityType.Builder.<EnderMan>of(EnderMan::new, MobCategory.MONSTER)
         .sized(0.6F, 2.9F)
         .eyeHeight(2.55F)
         .passengerAttachments(2.80625F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Endermite> ENDERMITE = register(
      "endermite",
      EntityType.Builder.<Endermite>of(Endermite::new, MobCategory.MONSTER)
         .sized(0.4F, 0.3F)
         .eyeHeight(0.13F)
         .passengerAttachments(0.2375F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<EnderDragon> ENDER_DRAGON = register(
      "ender_dragon",
      EntityType.Builder.<EnderDragon>of(EnderDragon::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(16.0F, 8.0F)
         .passengerAttachments(3.0F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ThrownEnderpearl> ENDER_PEARL = register(
      "ender_pearl",
      EntityType.Builder.<ThrownEnderpearl>of(ThrownEnderpearl::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<EndCrystal> END_CRYSTAL = register(
      "end_crystal",
      EntityType.Builder.<EndCrystal>of(EndCrystal::new, MobCategory.MISC)
         .noLootTable()
         .fireImmune()
         .sized(2.0F, 2.0F)
         .clientTrackingRange(16)
         .updateInterval(Integer.MAX_VALUE)
   );
   public static final EntityType<Evoker> EVOKER = register(
      "evoker",
      EntityType.Builder.<Evoker>of(Evoker::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .passengerAttachments(2.0F)
         .ridingOffset(-0.6F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<EvokerFangs> EVOKER_FANGS = register(
      "evoker_fangs",
      EntityType.Builder.<EvokerFangs>of(EvokerFangs::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.8F).clientTrackingRange(6).updateInterval(2)
   );
   public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE = register(
      "experience_bottle",
      EntityType.Builder.<ThrownExperienceBottle>of(ThrownExperienceBottle::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<ExperienceOrb> EXPERIENCE_ORB = register(
      "experience_orb",
      EntityType.Builder.<ExperienceOrb>of(ExperienceOrb::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20)
   );
   public static final EntityType<EyeOfEnder> EYE_OF_ENDER = register(
      "eye_of_ender",
      EntityType.Builder.<EyeOfEnder>of(EyeOfEnder::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(4)
   );
   public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register(
      "falling_block",
      EntityType.Builder.<FallingBlockEntity>of(FallingBlockEntity::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.98F, 0.98F)
         .clientTrackingRange(10)
         .updateInterval(20)
   );
   public static final EntityType<LargeFireball> FIREBALL = register(
      "fireball",
      EntityType.Builder.<LargeFireball>of(LargeFireball::new, MobCategory.MISC).noLootTable().sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register(
      "firework_rocket",
      EntityType.Builder.<FireworkRocketEntity>of(FireworkRocketEntity::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<Fox> FOX = register(
      "fox",
      EntityType.Builder.<Fox>of(Fox::new, MobCategory.CREATURE)
         .sized(0.6F, 0.7F)
         .eyeHeight(0.4F)
         .passengerAttachments(new Vec3(0.0, 0.6375, -0.25))
         .clientTrackingRange(8)
         .immuneTo(Blocks.SWEET_BERRY_BUSH)
   );
   public static final EntityType<Frog> FROG = register(
      "frog",
      EntityType.Builder.<Frog>of(Frog::new, MobCategory.CREATURE).sized(0.5F, 0.5F).passengerAttachments(new Vec3(0.0, 0.375, -0.25)).clientTrackingRange(10)
   );
   public static final EntityType<MinecartFurnace> FURNACE_MINECART = register(
      "furnace_minecart",
      EntityType.Builder.<MinecartFurnace>of(MinecartFurnace::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.98F, 0.7F)
         .passengerAttachments(0.1875F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Ghast> GHAST = register(
      "ghast",
      EntityType.Builder.<Ghast>of(Ghast::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(4.0F, 4.0F)
         .eyeHeight(2.6F)
         .passengerAttachments(4.0625F)
         .ridingOffset(0.5F)
         .clientTrackingRange(10)
         .notInPeaceful()
   );
   public static final EntityType<HappyGhast> HAPPY_GHAST = register(
      "happy_ghast",
      EntityType.Builder.<HappyGhast>of(HappyGhast::new, MobCategory.CREATURE)
         .sized(4.0F, 4.0F)
         .eyeHeight(2.6F)
         .passengerAttachments(new Vec3(0.0, 4.0, 1.7), new Vec3(-1.7, 4.0, 0.0), new Vec3(0.0, 4.0, -1.7), new Vec3(1.7, 4.0, 0.0))
         .ridingOffset(0.5F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Giant> GIANT = register(
      "giant",
      EntityType.Builder.<Giant>of(Giant::new, MobCategory.MONSTER)
         .sized(3.6F, 12.0F)
         .eyeHeight(10.44F)
         .ridingOffset(-3.75F)
         .clientTrackingRange(10)
         .notInPeaceful()
   );
   public static final EntityType<GlowItemFrame> GLOW_ITEM_FRAME = register(
      "glow_item_frame",
      EntityType.Builder.<GlowItemFrame>of(GlowItemFrame::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.5F, 0.5F)
         .eyeHeight(0.0F)
         .clientTrackingRange(10)
         .updateInterval(Integer.MAX_VALUE)
   );
   public static final EntityType<GlowSquid> GLOW_SQUID = register(
      "glow_squid",
      EntityType.Builder.<GlowSquid>of(GlowSquid::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).eyeHeight(0.4F).clientTrackingRange(10)
   );
   public static final EntityType<Goat> GOAT = register(
      "goat", EntityType.Builder.<Goat>of(Goat::new, MobCategory.CREATURE).sized(0.9F, 1.3F).passengerAttachments(1.1125F).clientTrackingRange(10)
   );
   public static final EntityType<Guardian> GUARDIAN = register(
      "guardian",
      EntityType.Builder.<Guardian>of(Guardian::new, MobCategory.MONSTER)
         .sized(0.85F, 0.85F)
         .eyeHeight(0.425F)
         .passengerAttachments(0.975F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Hoglin> HOGLIN = register(
      "hoglin", EntityType.Builder.<Hoglin>of(Hoglin::new, MobCategory.MONSTER).sized(1.3964844F, 1.4F).passengerAttachments(1.49375F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartHopper> HOPPER_MINECART = register(
      "hopper_minecart",
      EntityType.Builder.<MinecartHopper>of(MinecartHopper::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.98F, 0.7F)
         .passengerAttachments(0.1875F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Horse> HORSE = register(
      "horse",
      EntityType.Builder.<Horse>of(Horse::new, MobCategory.CREATURE)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(1.44375F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Husk> HUSK = register(
      "husk",
      EntityType.Builder.<Husk>of(Husk::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.74F)
         .passengerAttachments(2.075F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Illusioner> ILLUSIONER = register(
      "illusioner",
      EntityType.Builder.<Illusioner>of(Illusioner::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .passengerAttachments(2.0F)
         .ridingOffset(-0.6F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Interaction> INTERACTION = register(
      "interaction", EntityType.Builder.<Interaction>of(Interaction::new, MobCategory.MISC).noLootTable().sized(0.0F, 0.0F).clientTrackingRange(10)
   );
   public static final EntityType<IronGolem> IRON_GOLEM = register(
      "iron_golem", EntityType.Builder.<IronGolem>of(IronGolem::new, MobCategory.MISC).sized(1.4F, 2.7F).clientTrackingRange(10)
   );
   public static final EntityType<ItemEntity> ITEM = register(
      "item",
      EntityType.Builder.<ItemEntity>of(ItemEntity::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.25F, 0.25F)
         .eyeHeight(0.2125F)
         .clientTrackingRange(6)
         .updateInterval(20)
   );
   public static final EntityType<Display.ItemDisplay> ITEM_DISPLAY = register(
      "item_display",
      EntityType.Builder.<Display.ItemDisplay>of(Display.ItemDisplay::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.0F, 0.0F)
         .clientTrackingRange(10)
         .updateInterval(1)
   );
   public static final EntityType<ItemFrame> ITEM_FRAME = register(
      "item_frame",
      EntityType.Builder.<ItemFrame>of(ItemFrame::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.5F, 0.5F)
         .eyeHeight(0.0F)
         .clientTrackingRange(10)
         .updateInterval(Integer.MAX_VALUE)
   );
   public static final EntityType<Boat> JUNGLE_BOAT = register(
      "jungle_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.JUNGLE_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> JUNGLE_CHEST_BOAT = register(
      "jungle_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.JUNGLE_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT = register(
      "leash_knot",
      EntityType.Builder.<LeashFenceKnotEntity>of(LeashFenceKnotEntity::new, MobCategory.MISC)
         .noLootTable()
         .noSave()
         .sized(0.375F, 0.5F)
         .eyeHeight(0.0625F)
         .clientTrackingRange(10)
         .updateInterval(Integer.MAX_VALUE)
   );
   public static final EntityType<LightningBolt> LIGHTNING_BOLT = register(
      "lightning_bolt",
      EntityType.Builder.<LightningBolt>of(LightningBolt::new, MobCategory.MISC)
         .noLootTable()
         .noSave()
         .sized(0.0F, 0.0F)
         .clientTrackingRange(16)
         .updateInterval(Integer.MAX_VALUE)
   );
   public static final EntityType<Llama> LLAMA = register(
      "llama",
      EntityType.Builder.<Llama>of(Llama::new, MobCategory.CREATURE)
         .sized(0.9F, 1.87F)
         .eyeHeight(1.7765F)
         .passengerAttachments(new Vec3(0.0, 1.37, -0.3))
         .clientTrackingRange(10)
   );
   public static final EntityType<LlamaSpit> LLAMA_SPIT = register(
      "llama_spit",
      EntityType.Builder.<LlamaSpit>of(LlamaSpit::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<MagmaCube> MAGMA_CUBE = register(
      "magma_cube",
      EntityType.Builder.<MagmaCube>of(MagmaCube::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(0.52F, 0.52F)
         .eyeHeight(0.325F)
         .spawnDimensionsScale(4.0F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Boat> MANGROVE_BOAT = register(
      "mangrove_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.MANGROVE_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> MANGROVE_CHEST_BOAT = register(
      "mangrove_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.MANGROVE_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Mannequin> MANNEQUIN = register(
      "mannequin",
      EntityType.Builder.<Mannequin>of(Mannequin::create, MobCategory.MISC)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .vehicleAttachment(Avatar.DEFAULT_VEHICLE_ATTACHMENT)
         .clientTrackingRange(32)
         .updateInterval(2)
   );
   public static final EntityType<Marker> MARKER = register(
      "marker", EntityType.Builder.<Marker>of(Marker::new, MobCategory.MISC).noLootTable().sized(0.0F, 0.0F).clientTrackingRange(0)
   );
   public static final EntityType<Minecart> MINECART = register(
      "minecart",
      EntityType.Builder.<Minecart>of(Minecart::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
   );
   public static final EntityType<MushroomCow> MOOSHROOM = register(
      "mooshroom",
      EntityType.Builder.<MushroomCow>of(MushroomCow::new, MobCategory.CREATURE)
         .sized(0.9F, 1.4F)
         .eyeHeight(1.3F)
         .passengerAttachments(1.36875F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Mule> MULE = register(
      "mule",
      EntityType.Builder.<Mule>of(Mule::new, MobCategory.CREATURE)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(1.2125F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Boat> OAK_BOAT = register(
      "oak_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.OAK_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> OAK_CHEST_BOAT = register(
      "oak_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.OAK_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Ocelot> OCELOT = register(
      "ocelot", EntityType.Builder.<Ocelot>of(Ocelot::new, MobCategory.CREATURE).sized(0.6F, 0.7F).passengerAttachments(0.6375F).clientTrackingRange(10)
   );
   public static final EntityType<OminousItemSpawner> OMINOUS_ITEM_SPAWNER = register(
      "ominous_item_spawner",
      EntityType.Builder.<OminousItemSpawner>of(OminousItemSpawner::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(8)
   );
   public static final EntityType<Painting> PAINTING = register(
      "painting",
      EntityType.Builder.<Painting>of(Painting::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.5F, 0.5F)
         .clientTrackingRange(10)
         .updateInterval(Integer.MAX_VALUE)
   );
   public static final EntityType<Boat> PALE_OAK_BOAT = register(
      "pale_oak_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.PALE_OAK_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> PALE_OAK_CHEST_BOAT = register(
      "pale_oak_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.PALE_OAK_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Panda> PANDA = register(
      "panda", EntityType.Builder.<Panda>of(Panda::new, MobCategory.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10)
   );
   public static final EntityType<Parrot> PARROT = register(
      "parrot",
      EntityType.Builder.<Parrot>of(Parrot::new, MobCategory.CREATURE).sized(0.5F, 0.9F).eyeHeight(0.54F).passengerAttachments(0.4625F).clientTrackingRange(8)
   );
   public static final EntityType<Phantom> PHANTOM = register(
      "phantom",
      EntityType.Builder.<Phantom>of(Phantom::new, MobCategory.MONSTER)
         .sized(0.9F, 0.5F)
         .eyeHeight(0.175F)
         .passengerAttachments(0.3375F)
         .ridingOffset(-0.125F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Pig> PIG = register(
      "pig", EntityType.Builder.<Pig>of(Pig::new, MobCategory.CREATURE).sized(0.9F, 0.9F).passengerAttachments(0.86875F).clientTrackingRange(10)
   );
   public static final EntityType<Piglin> PIGLIN = register(
      "piglin",
      EntityType.Builder.<Piglin>of(Piglin::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.79F)
         .passengerAttachments(2.0125F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
   );
   public static final EntityType<PiglinBrute> PIGLIN_BRUTE = register(
      "piglin_brute",
      EntityType.Builder.<PiglinBrute>of(PiglinBrute::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.79F)
         .passengerAttachments(2.0125F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Pillager> PILLAGER = register(
      "pillager",
      EntityType.Builder.<Pillager>of(Pillager::new, MobCategory.MONSTER)
         .canSpawnFarFromPlayer()
         .sized(0.6F, 1.95F)
         .passengerAttachments(2.0F)
         .ridingOffset(-0.6F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<PolarBear> POLAR_BEAR = register(
      "polar_bear",
      EntityType.Builder.<PolarBear>of(PolarBear::new, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.4F).clientTrackingRange(10)
   );
   public static final EntityType<ThrownSplashPotion> SPLASH_POTION = register(
      "splash_potion",
      EntityType.Builder.<ThrownSplashPotion>of(ThrownSplashPotion::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<ThrownLingeringPotion> LINGERING_POTION = register(
      "lingering_potion",
      EntityType.Builder.<ThrownLingeringPotion>of(ThrownLingeringPotion::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<Pufferfish> PUFFERFISH = register(
      "pufferfish", EntityType.Builder.<Pufferfish>of(Pufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F).eyeHeight(0.455F).clientTrackingRange(4)
   );
   public static final EntityType<Rabbit> RABBIT = register(
      "rabbit", EntityType.Builder.<Rabbit>of(Rabbit::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8)
   );
   public static final EntityType<Ravager> RAVAGER = register(
      "ravager",
      EntityType.Builder.<Ravager>of(Ravager::new, MobCategory.MONSTER)
         .sized(1.95F, 2.2F)
         .passengerAttachments(new Vec3(0.0, 2.2625, -0.0625))
         .clientTrackingRange(10)
         .notInPeaceful()
   );
   public static final EntityType<Salmon> SALMON = register(
      "salmon", EntityType.Builder.<Salmon>of(Salmon::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).eyeHeight(0.26F).clientTrackingRange(4)
   );
   public static final EntityType<Sheep> SHEEP = register(
      "sheep",
      EntityType.Builder.<Sheep>of(Sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).eyeHeight(1.235F).passengerAttachments(1.2375F).clientTrackingRange(10)
   );
   public static final EntityType<Shulker> SHULKER = register(
      "shulker",
      EntityType.Builder.<Shulker>of(Shulker::new, MobCategory.MONSTER)
         .fireImmune()
         .canSpawnFarFromPlayer()
         .sized(1.0F, 1.0F)
         .eyeHeight(0.5F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ShulkerBullet> SHULKER_BULLET = register(
      "shulker_bullet",
      EntityType.Builder.<ShulkerBullet>of(ShulkerBullet::new, MobCategory.MISC).noLootTable().sized(0.3125F, 0.3125F).clientTrackingRange(8)
   );
   public static final EntityType<Silverfish> SILVERFISH = register(
      "silverfish",
      EntityType.Builder.<Silverfish>of(Silverfish::new, MobCategory.MONSTER)
         .sized(0.4F, 0.3F)
         .eyeHeight(0.13F)
         .passengerAttachments(0.2375F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Skeleton> SKELETON = register(
      "skeleton",
      EntityType.Builder.<Skeleton>of(Skeleton::new, MobCategory.MONSTER)
         .sized(0.6F, 1.99F)
         .eyeHeight(1.74F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<SkeletonHorse> SKELETON_HORSE = register(
      "skeleton_horse",
      EntityType.Builder.<SkeletonHorse>of(SkeletonHorse::new, MobCategory.CREATURE)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(1.31875F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Slime> SLIME = register(
      "slime",
      EntityType.Builder.<Slime>of(Slime::new, MobCategory.MONSTER)
         .sized(0.52F, 0.52F)
         .eyeHeight(0.325F)
         .spawnDimensionsScale(4.0F)
         .clientTrackingRange(10)
         .notInPeaceful()
   );
   public static final EntityType<SmallFireball> SMALL_FIREBALL = register(
      "small_fireball",
      EntityType.Builder.<SmallFireball>of(SmallFireball::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.3125F, 0.3125F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<Sniffer> SNIFFER = register(
      "sniffer",
      EntityType.Builder.<Sniffer>of(Sniffer::new, MobCategory.CREATURE)
         .sized(1.9F, 1.75F)
         .eyeHeight(1.05F)
         .passengerAttachments(2.09375F)
         .nameTagOffset(2.05F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Snowball> SNOWBALL = register(
      "snowball", EntityType.Builder.<Snowball>of(Snowball::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<SnowGolem> SNOW_GOLEM = register(
      "snow_golem",
      EntityType.Builder.<SnowGolem>of(SnowGolem::new, MobCategory.MISC).immuneTo(Blocks.POWDER_SNOW).sized(0.7F, 1.9F).eyeHeight(1.7F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartSpawner> SPAWNER_MINECART = register(
      "spawner_minecart",
      EntityType.Builder.<MinecartSpawner>of(MinecartSpawner::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.98F, 0.7F)
         .passengerAttachments(0.1875F)
         .clientTrackingRange(8)
   );
   public static final EntityType<SpectralArrow> SPECTRAL_ARROW = register(
      "spectral_arrow",
      EntityType.Builder.<SpectralArrow>of(SpectralArrow::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.5F, 0.5F)
         .eyeHeight(0.13F)
         .clientTrackingRange(4)
         .updateInterval(20)
   );
   public static final EntityType<Spider> SPIDER = register(
      "spider",
      EntityType.Builder.<Spider>of(Spider::new, MobCategory.MONSTER)
         .sized(1.4F, 0.9F)
         .eyeHeight(0.65F)
         .passengerAttachments(0.765F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Boat> SPRUCE_BOAT = register(
      "spruce_boat",
      EntityType.Builder.<Boat>of(boatFactory(() -> Items.SPRUCE_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> SPRUCE_CHEST_BOAT = register(
      "spruce_chest_boat",
      EntityType.Builder.<ChestBoat>of(chestBoatFactory(() -> Items.SPRUCE_CHEST_BOAT), MobCategory.MISC)
         .noLootTable()
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Squid> SQUID = register(
      "squid", EntityType.Builder.<Squid>of(Squid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).eyeHeight(0.4F).clientTrackingRange(8)
   );
   public static final EntityType<Stray> STRAY = register(
      "stray",
      EntityType.Builder.<Stray>of(Stray::new, MobCategory.MONSTER)
         .sized(0.6F, 1.99F)
         .eyeHeight(1.74F)
         .ridingOffset(-0.7F)
         .immuneTo(Blocks.POWDER_SNOW)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Strider> STRIDER = register(
      "strider", EntityType.Builder.<Strider>of(Strider::new, MobCategory.CREATURE).fireImmune().sized(0.9F, 1.7F).clientTrackingRange(10)
   );
   public static final EntityType<Tadpole> TADPOLE = register(
      "tadpole", EntityType.Builder.<Tadpole>of(Tadpole::new, MobCategory.CREATURE).sized(0.4F, 0.3F).eyeHeight(0.19500001F).clientTrackingRange(10)
   );
   public static final EntityType<Display.TextDisplay> TEXT_DISPLAY = register(
      "text_display",
      EntityType.Builder.<Display.TextDisplay>of(Display.TextDisplay::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.0F, 0.0F)
         .clientTrackingRange(10)
         .updateInterval(1)
   );
   public static final EntityType<PrimedTnt> TNT = register(
      "tnt",
      EntityType.Builder.<PrimedTnt>of(PrimedTnt::new, MobCategory.MISC)
         .noLootTable()
         .fireImmune()
         .sized(0.98F, 0.98F)
         .eyeHeight(0.15F)
         .clientTrackingRange(10)
         .updateInterval(10)
   );
   public static final EntityType<MinecartTNT> TNT_MINECART = register(
      "tnt_minecart",
      EntityType.Builder.<MinecartTNT>of(MinecartTNT::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.98F, 0.7F)
         .passengerAttachments(0.1875F)
         .clientTrackingRange(8)
   );
   public static final EntityType<TraderLlama> TRADER_LLAMA = register(
      "trader_llama",
      EntityType.Builder.<TraderLlama>of(TraderLlama::new, MobCategory.CREATURE)
         .sized(0.9F, 1.87F)
         .eyeHeight(1.7765F)
         .passengerAttachments(new Vec3(0.0, 1.37, -0.3))
         .clientTrackingRange(10)
   );
   public static final EntityType<ThrownTrident> TRIDENT = register(
      "trident",
      EntityType.Builder.<ThrownTrident>of(ThrownTrident::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.5F, 0.5F)
         .eyeHeight(0.13F)
         .clientTrackingRange(4)
         .updateInterval(20)
   );
   public static final EntityType<TropicalFish> TROPICAL_FISH = register(
      "tropical_fish",
      EntityType.Builder.<TropicalFish>of(TropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).eyeHeight(0.26F).clientTrackingRange(4)
   );
   public static final EntityType<Turtle> TURTLE = register(
      "turtle",
      EntityType.Builder.<Turtle>of(Turtle::new, MobCategory.CREATURE)
         .sized(1.2F, 0.4F)
         .passengerAttachments(new Vec3(0.0, 0.55625, -0.25))
         .clientTrackingRange(10)
   );
   public static final EntityType<Vex> VEX = register(
      "vex",
      EntityType.Builder.<Vex>of(Vex::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(0.4F, 0.8F)
         .eyeHeight(0.51875F)
         .passengerAttachments(0.7375F)
         .ridingOffset(0.04F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Villager> VILLAGER = register(
      "villager", EntityType.Builder.<Villager>of(Villager::new, MobCategory.MISC).sized(0.6F, 1.95F).eyeHeight(1.62F).clientTrackingRange(10)
   );
   public static final EntityType<Vindicator> VINDICATOR = register(
      "vindicator",
      EntityType.Builder.<Vindicator>of(Vindicator::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .passengerAttachments(2.0F)
         .ridingOffset(-0.6F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<WanderingTrader> WANDERING_TRADER = register(
      "wandering_trader",
      EntityType.Builder.<WanderingTrader>of(WanderingTrader::new, MobCategory.CREATURE).sized(0.6F, 1.95F).eyeHeight(1.62F).clientTrackingRange(10)
   );
   public static final EntityType<Warden> WARDEN = register(
      "warden",
      EntityType.Builder.<Warden>of(Warden::new, MobCategory.MONSTER)
         .sized(0.9F, 2.9F)
         .passengerAttachments(3.15F)
         .attach(EntityAttachment.WARDEN_CHEST, 0.0F, 1.6F, 0.0F)
         .clientTrackingRange(16)
         .fireImmune()
         .notInPeaceful()
   );
   public static final EntityType<WindCharge> WIND_CHARGE = register(
      "wind_charge",
      EntityType.Builder.<WindCharge>of(WindCharge::new, MobCategory.MISC)
         .noLootTable()
         .sized(0.3125F, 0.3125F)
         .eyeHeight(0.0F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<Witch> WITCH = register(
      "witch",
      EntityType.Builder.<Witch>of(Witch::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.62F)
         .passengerAttachments(2.2625F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<WitherBoss> WITHER = register(
      "wither",
      EntityType.Builder.<WitherBoss>of(WitherBoss::new, MobCategory.MONSTER)
         .fireImmune()
         .immuneTo(Blocks.WITHER_ROSE)
         .sized(0.9F, 3.5F)
         .clientTrackingRange(10)
         .notInPeaceful()
   );
   public static final EntityType<WitherSkeleton> WITHER_SKELETON = register(
      "wither_skeleton",
      EntityType.Builder.<WitherSkeleton>of(WitherSkeleton::new, MobCategory.MONSTER)
         .fireImmune()
         .immuneTo(Blocks.WITHER_ROSE)
         .sized(0.7F, 2.4F)
         .eyeHeight(2.1F)
         .ridingOffset(-0.875F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<WitherSkull> WITHER_SKULL = register(
      "wither_skull",
      EntityType.Builder.<WitherSkull>of(WitherSkull::new, MobCategory.MISC).noLootTable().sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Wolf> WOLF = register(
      "wolf",
      EntityType.Builder.<Wolf>of(Wolf::new, MobCategory.CREATURE)
         .sized(0.6F, 0.85F)
         .eyeHeight(0.68F)
         .passengerAttachments(new Vec3(0.0, 0.81875, -0.0625))
         .clientTrackingRange(10)
   );
   public static final EntityType<Zoglin> ZOGLIN = register(
      "zoglin",
      EntityType.Builder.<Zoglin>of(Zoglin::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(1.3964844F, 1.4F)
         .passengerAttachments(1.49375F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Zombie> ZOMBIE = register(
      "zombie",
      EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.74F)
         .passengerAttachments(2.0125F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<ZombieHorse> ZOMBIE_HORSE = register(
      "zombie_horse",
      EntityType.Builder.<ZombieHorse>of(ZombieHorse::new, MobCategory.CREATURE)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(1.31875F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER = register(
      "zombie_villager",
      EntityType.Builder.<ZombieVillager>of(ZombieVillager::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .passengerAttachments(2.125F)
         .ridingOffset(-0.7F)
         .eyeHeight(1.74F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<ZombifiedPiglin> ZOMBIFIED_PIGLIN = register(
      "zombified_piglin",
      EntityType.Builder.<ZombifiedPiglin>of(ZombifiedPiglin::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(0.6F, 1.95F)
         .eyeHeight(1.79F)
         .passengerAttachments(2.0F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
         .notInPeaceful()
   );
   public static final EntityType<Player> PLAYER = register(
      "player",
      EntityType.Builder.<Player>createNothing(MobCategory.MISC)
         .noSave()
         .noSummon()
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .vehicleAttachment(Avatar.DEFAULT_VEHICLE_ATTACHMENT)
         .clientTrackingRange(32)
         .updateInterval(2)
   );
   public static final EntityType<FishingHook> FISHING_BOBBER = register(
      "fishing_bobber",
      EntityType.Builder.<FishingHook>of(FishingHook::new, MobCategory.MISC)
         .noLootTable()
         .noSave()
         .noSummon()
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(5)
   );
   private static final Set<EntityType<?>> OP_ONLY_CUSTOM_DATA = Set.of(FALLING_BLOCK, COMMAND_BLOCK_MINECART, SPAWNER_MINECART);
   private final EntityType.EntityFactory<T> factory;
   private final MobCategory category;
   private final ImmutableSet<Block> immuneTo;
   private final boolean serialize;
   private final boolean summon;
   private final boolean fireImmune;
   private final boolean canSpawnFarFromPlayer;
   private final int clientTrackingRange;
   private final int updateInterval;
   private final String descriptionId;
   @Nullable
   private Component description;
   private final Optional<ResourceKey<LootTable>> lootTable;
   private final EntityDimensions dimensions;
   private final float spawnDimensionsScale;
   private final FeatureFlagSet requiredFeatures;
   private final boolean allowedInPeaceful;

   private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> $$0, EntityType.Builder<T> $$1) {
      return Registry.register(BuiltInRegistries.ENTITY_TYPE, $$0, $$1.build($$0));
   }

   private static ResourceKey<EntityType<?>> vanillaEntityId(String $$0) {
      return ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.withDefaultNamespace($$0));
   }

   private static <T extends Entity> EntityType<T> register(String $$0, EntityType.Builder<T> $$1) {
      return register(vanillaEntityId($$0), $$1);
   }

   public static ResourceLocation getKey(EntityType<?> $$0) {
      return BuiltInRegistries.ENTITY_TYPE.getKey($$0);
   }

   public static Optional<EntityType<?>> byString(String $$0) {
      return BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse($$0));
   }

   public EntityType(
      EntityType.EntityFactory<T> $$0,
      MobCategory $$1,
      boolean $$2,
      boolean $$3,
      boolean $$4,
      boolean $$5,
      ImmutableSet<Block> $$6,
      EntityDimensions $$7,
      float $$8,
      int $$9,
      int $$10,
      String $$11,
      Optional<ResourceKey<LootTable>> $$12,
      FeatureFlagSet $$13,
      boolean $$14
   ) {
      this.factory = $$0;
      this.category = $$1;
      this.canSpawnFarFromPlayer = $$5;
      this.serialize = $$2;
      this.summon = $$3;
      this.fireImmune = $$4;
      this.immuneTo = $$6;
      this.dimensions = $$7;
      this.spawnDimensionsScale = $$8;
      this.clientTrackingRange = $$9;
      this.updateInterval = $$10;
      this.descriptionId = $$11;
      this.lootTable = $$12;
      this.requiredFeatures = $$13;
      this.allowedInPeaceful = $$14;
   }

   @Nullable
   public T spawn(ServerLevel $$0, @Nullable ItemStack $$1, @Nullable LivingEntity $$2, BlockPos $$3, EntitySpawnReason $$4, boolean $$5, boolean $$6) {
      Consumer<T> $$7;
      if ($$1 != null) {
         $$7 = createDefaultStackConfig($$0, $$1, $$2);
      } else {
         $$7 = $$0x -> {
         };
      }

      return this.spawn($$0, $$7, $$3, $$4, $$5, $$6);
   }

   public static <T extends Entity> Consumer<T> createDefaultStackConfig(Level $$0, ItemStack $$1, @Nullable LivingEntity $$2) {
      return appendDefaultStackConfig($$0x -> {
      }, $$0, $$1, $$2);
   }

   public static <T extends Entity> Consumer<T> appendDefaultStackConfig(Consumer<T> $$0, Level $$1, ItemStack $$2, @Nullable LivingEntity $$3) {
      return appendCustomEntityStackConfig(appendComponentsConfig($$0, $$2), $$1, $$2, $$3);
   }

   public static <T extends Entity> Consumer<T> appendComponentsConfig(Consumer<T> $$0, ItemStack $$1) {
      return $$0.andThen($$1x -> $$1x.applyComponentsFromItemStack($$1));
   }

   public static <T extends Entity> Consumer<T> appendCustomEntityStackConfig(Consumer<T> $$0, Level $$1, ItemStack $$2, @Nullable LivingEntity $$3) {
      TypedEntityData<EntityType<?>> $$4 = $$2.get(DataComponents.ENTITY_DATA);
      return $$4 != null ? $$0.andThen($$3x -> updateCustomEntityTag($$1, $$3, $$3x, $$4)) : $$0;
   }

   @Nullable
   public T spawn(ServerLevel $$0, BlockPos $$1, EntitySpawnReason $$2) {
      return this.spawn($$0, null, $$1, $$2, false, false);
   }

   @Nullable
   public T spawn(ServerLevel $$0, @Nullable Consumer<T> $$1, BlockPos $$2, EntitySpawnReason $$3, boolean $$4, boolean $$5) {
      T $$6 = this.create($$0, $$1, $$2, $$3, $$4, $$5);
      if ($$6 != null) {
         $$0.addFreshEntityWithPassengers($$6);
         if ($$6 instanceof Mob $$7) {
            $$7.playAmbientSound();
         }
      }

      return $$6;
   }

   @Nullable
   public T create(ServerLevel $$0, @Nullable Consumer<T> $$1, BlockPos $$2, EntitySpawnReason $$3, boolean $$4, boolean $$5) {
      T $$6 = this.create($$0, $$3);
      if ($$6 == null) {
         return null;
      } else {
         double $$7;
         if ($$4) {
            $$6.setPos((double)$$2.getX() + 0.5, (double)($$2.getY() + 1), (double)$$2.getZ() + 0.5);
            $$7 = getYOffset($$0, $$2, $$5, $$6.getBoundingBox());
         } else {
            $$7 = 0.0;
         }

         $$6.snapTo((double)$$2.getX() + 0.5, (double)$$2.getY() + $$7, (double)$$2.getZ() + 0.5, Mth.wrapDegrees($$0.random.nextFloat() * 360.0F), 0.0F);
         if ($$6 instanceof Mob $$9) {
            $$9.yHeadRot = $$9.getYRot();
            $$9.yBodyRot = $$9.getYRot();
            $$9.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$9.blockPosition()), $$3, null);
         }

         if ($$1 != null) {
            $$1.accept($$6);
         }

         return $$6;
      }
   }

   protected static double getYOffset(LevelReader $$0, BlockPos $$1, boolean $$2, AABB $$3) {
      AABB $$4 = new AABB($$1);
      if ($$2) {
         $$4 = $$4.expandTowards(0.0, -1.0, 0.0);
      }

      Iterable<VoxelShape> $$5 = $$0.getCollisions(null, $$4);
      return 1.0 + Shapes.collide(Direction.Axis.Y, $$3, $$5, $$2 ? -2.0 : -1.0);
   }

   public static void updateCustomEntityTag(Level $$0, @Nullable LivingEntity $$1, @Nullable Entity $$2, TypedEntityData<EntityType<?>> $$3) {
      MinecraftServer $$4 = $$0.getServer();
      if ($$4 != null && $$2 != null) {
         if ($$2.getType() == $$3.type()) {
            if (!$$0.isClientSide() && $$2.getType().onlyOpCanSetNbt()) {
               if (!($$1 instanceof Player)) {
                  return;
               }

               Player $$5 = (Player)$$1;
               if (!$$4.getPlayerList().isOp($$5.nameAndId())) {
                  return;
               }
            }

            $$3.loadInto($$2);
         }
      }
   }

   public boolean canSerialize() {
      return this.serialize;
   }

   public boolean canSummon() {
      return this.summon;
   }

   public boolean fireImmune() {
      return this.fireImmune;
   }

   public boolean canSpawnFarFromPlayer() {
      return this.canSpawnFarFromPlayer;
   }

   public MobCategory getCategory() {
      return this.category;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }

   public Component getDescription() {
      if (this.description == null) {
         this.description = Component.translatable(this.getDescriptionId());
      }

      return this.description;
   }

   public String toString() {
      return this.getDescriptionId();
   }

   public String toShortString() {
      int $$0 = this.getDescriptionId().lastIndexOf(46);
      return $$0 == -1 ? this.getDescriptionId() : this.getDescriptionId().substring($$0 + 1);
   }

   public Optional<ResourceKey<LootTable>> getDefaultLootTable() {
      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width();
   }

   public float getHeight() {
      return this.dimensions.height();
   }

   @Override
   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   @Nullable
   public T create(Level $$0, EntitySpawnReason $$1) {
      return !this.isEnabled($$0.enabledFeatures()) ? null : this.factory.create(this, $$0);
   }

   public static Optional<Entity> create(ValueInput $$0, Level $$1, EntitySpawnReason $$2) {
      return Util.ifElse(
         by($$0).map($$2x -> $$2x.create($$1, $$2)),
         $$1x -> $$1x.load($$0),
         () -> LOGGER.warn("Skipping Entity with id {}", $$0.getStringOr("id", "[invalid]"))
      );
   }

   public static Optional<Entity> create(EntityType<?> $$0, ValueInput $$1, Level $$2, EntitySpawnReason $$3) {
      Optional<Entity> $$4 = Optional.ofNullable($$0.create($$2, $$3));
      $$4.ifPresent($$1x -> $$1x.load($$1));
      return $$4;
   }

   public AABB getSpawnAABB(double $$0, double $$1, double $$2) {
      float $$3 = this.spawnDimensionsScale * this.getWidth() / 2.0F;
      float $$4 = this.spawnDimensionsScale * this.getHeight();
      return new AABB($$0 - (double)$$3, $$1, $$2 - (double)$$3, $$0 + (double)$$3, $$1 + (double)$$4, $$2 + (double)$$3);
   }

   public boolean isBlockDangerous(BlockState $$0) {
      if (this.immuneTo.contains($$0.getBlock())) {
         return false;
      } else if (!this.fireImmune && NodeEvaluator.isBurningBlock($$0)) {
         return true;
      } else {
         return $$0.is(Blocks.WITHER_ROSE) || $$0.is(Blocks.SWEET_BERRY_BUSH) || $$0.is(Blocks.CACTUS) || $$0.is(Blocks.POWDER_SNOW);
      }
   }

   public EntityDimensions getDimensions() {
      return this.dimensions;
   }

   public static Optional<EntityType<?>> by(ValueInput $$0) {
      return $$0.read("id", CODEC);
   }

   @Nullable
   public static Entity loadEntityRecursive(CompoundTag $$0, Level $$1, EntitySpawnReason $$2, Function<Entity, Entity> $$3) {
      Entity var5;
      try (ProblemReporter.ScopedCollector $$4 = new ProblemReporter.ScopedCollector(LOGGER)) {
         var5 = loadEntityRecursive(TagValueInput.create($$4, $$1.registryAccess(), $$0), $$1, $$2, $$3);
      }

      return var5;
   }

   @Nullable
   public static Entity loadEntityRecursive(EntityType<?> $$0, CompoundTag $$1, Level $$2, EntitySpawnReason $$3, Function<Entity, Entity> $$4) {
      Entity var6;
      try (ProblemReporter.ScopedCollector $$5 = new ProblemReporter.ScopedCollector(LOGGER)) {
         var6 = loadEntityRecursive($$0, TagValueInput.create($$5, $$2.registryAccess(), $$1), $$2, $$3, $$4);
      }

      return var6;
   }

   @Nullable
   public static Entity loadEntityRecursive(ValueInput $$0, Level $$1, EntitySpawnReason $$2, Function<Entity, Entity> $$3) {
      return (Entity)loadStaticEntity($$0, $$1, $$2).map($$3).map($$4 -> loadPassengersRecursive($$4, $$0, $$1, $$2, $$3)).orElse(null);
   }

   @Nullable
   public static Entity loadEntityRecursive(EntityType<?> $$0, ValueInput $$1, Level $$2, EntitySpawnReason $$3, Function<Entity, Entity> $$4) {
      return (Entity)loadStaticEntity($$0, $$1, $$2, $$3).map($$4).map($$4x -> loadPassengersRecursive($$4x, $$1, $$2, $$3, $$4)).orElse(null);
   }

   private static Entity loadPassengersRecursive(Entity $$0, ValueInput $$1, Level $$2, EntitySpawnReason $$3, Function<Entity, Entity> $$4) {
      for(ValueInput $$5 : $$1.childrenListOrEmpty("Passengers")) {
         Entity $$6 = loadEntityRecursive($$5, $$2, $$3, $$4);
         if ($$6 != null) {
            $$6.startRiding($$0, true, false);
         }
      }

      return $$0;
   }

   public static Stream<Entity> loadEntitiesRecursive(ValueInput.ValueInputList $$0, Level $$1, EntitySpawnReason $$2) {
      return $$0.stream().mapMulti(($$2x, $$3) -> loadEntityRecursive($$2x, $$1, $$2, $$1xx -> {
            $$3.accept($$1xx);
            return $$1xx;
         }));
   }

   private static Optional<Entity> loadStaticEntity(ValueInput $$0, Level $$1, EntitySpawnReason $$2) {
      try {
         return create($$0, $$1, $$2);
      } catch (RuntimeException var4) {
         LOGGER.warn("Exception loading entity: ", var4);
         return Optional.empty();
      }
   }

   private static Optional<Entity> loadStaticEntity(EntityType<?> $$0, ValueInput $$1, Level $$2, EntitySpawnReason $$3) {
      try {
         return create($$0, $$1, $$2, $$3);
      } catch (RuntimeException var5) {
         LOGGER.warn("Exception loading entity: ", var5);
         return Optional.empty();
      }
   }

   public int clientTrackingRange() {
      return this.clientTrackingRange;
   }

   public int updateInterval() {
      return this.updateInterval;
   }

   public boolean trackDeltas() {
      return this != PLAYER
         && this != LLAMA_SPIT
         && this != WITHER
         && this != BAT
         && this != ITEM_FRAME
         && this != GLOW_ITEM_FRAME
         && this != LEASH_KNOT
         && this != PAINTING
         && this != END_CRYSTAL
         && this != EVOKER_FANGS;
   }

   public boolean is(TagKey<EntityType<?>> $$0) {
      return this.builtInRegistryHolder.is($$0);
   }

   public boolean is(HolderSet<EntityType<?>> $$0) {
      return $$0.contains(this.builtInRegistryHolder);
   }

   @Nullable
   public T tryCast(Entity $$0) {
      return (T)($$0.getType() == this ? $$0 : null);
   }

   @Override
   public Class<? extends Entity> getBaseClass() {
      return Entity.class;
   }

   @Deprecated
   public Holder.Reference<EntityType<?>> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public boolean isAllowedInPeaceful() {
      return this.allowedInPeaceful;
   }

   private static EntityType.EntityFactory<Boat> boatFactory(Supplier<Item> $$0) {
      return ($$1, $$2) -> new Boat($$1, $$2, $$0);
   }

   private static EntityType.EntityFactory<ChestBoat> chestBoatFactory(Supplier<Item> $$0) {
      return ($$1, $$2) -> new ChestBoat($$1, $$2, $$0);
   }

   private static EntityType.EntityFactory<Raft> raftFactory(Supplier<Item> $$0) {
      return ($$1, $$2) -> new Raft($$1, $$2, $$0);
   }

   private static EntityType.EntityFactory<ChestRaft> chestRaftFactory(Supplier<Item> $$0) {
      return ($$1, $$2) -> new ChestRaft($$1, $$2, $$0);
   }

   public boolean onlyOpCanSetNbt() {
      return OP_ONLY_CUSTOM_DATA.contains(this);
   }

   public static class Builder<T extends Entity> {
      private final EntityType.EntityFactory<T> factory;
      private final MobCategory category;
      private ImmutableSet<Block> immuneTo = ImmutableSet.of();
      private boolean serialize = true;
      private boolean summon = true;
      private boolean fireImmune;
      private boolean canSpawnFarFromPlayer;
      private int clientTrackingRange = 5;
      private int updateInterval = 3;
      private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);
      private float spawnDimensionsScale = 1.0F;
      private EntityAttachments.Builder attachments = EntityAttachments.builder();
      private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
      private DependantName<EntityType<?>, Optional<ResourceKey<LootTable>>> lootTable = $$0x -> Optional.of(
            ResourceKey.create(Registries.LOOT_TABLE, $$0x.location().withPrefix("entities/"))
         );
      private final DependantName<EntityType<?>, String> descriptionId = $$0x -> Util.makeDescriptionId("entity", $$0x.location());
      private boolean allowedInPeaceful = true;

      private Builder(EntityType.EntityFactory<T> $$0, MobCategory $$1) {
         this.factory = $$0;
         this.category = $$1;
         this.canSpawnFarFromPlayer = $$1 == MobCategory.CREATURE || $$1 == MobCategory.MISC;
      }

      public static <T extends Entity> EntityType.Builder<T> of(EntityType.EntityFactory<T> $$0, MobCategory $$1) {
         return new EntityType.Builder<>($$0, $$1);
      }

      public static <T extends Entity> EntityType.Builder<T> createNothing(MobCategory $$0) {
         return new EntityType.Builder<>(($$0x, $$1) -> null, $$0);
      }

      public EntityType.Builder<T> sized(float $$0, float $$1) {
         this.dimensions = EntityDimensions.scalable($$0, $$1);
         return this;
      }

      public EntityType.Builder<T> spawnDimensionsScale(float $$0) {
         this.spawnDimensionsScale = $$0;
         return this;
      }

      public EntityType.Builder<T> eyeHeight(float $$0) {
         this.dimensions = this.dimensions.withEyeHeight($$0);
         return this;
      }

      public EntityType.Builder<T> passengerAttachments(float... $$0) {
         for(float $$1 : $$0) {
            this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, 0.0F, $$1, 0.0F);
         }

         return this;
      }

      public EntityType.Builder<T> passengerAttachments(Vec3... $$0) {
         for(Vec3 $$1 : $$0) {
            this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, $$1);
         }

         return this;
      }

      public EntityType.Builder<T> vehicleAttachment(Vec3 $$0) {
         return this.attach(EntityAttachment.VEHICLE, $$0);
      }

      public EntityType.Builder<T> ridingOffset(float $$0) {
         return this.attach(EntityAttachment.VEHICLE, 0.0F, -$$0, 0.0F);
      }

      public EntityType.Builder<T> nameTagOffset(float $$0) {
         return this.attach(EntityAttachment.NAME_TAG, 0.0F, $$0, 0.0F);
      }

      public EntityType.Builder<T> attach(EntityAttachment $$0, float $$1, float $$2, float $$3) {
         this.attachments = this.attachments.attach($$0, $$1, $$2, $$3);
         return this;
      }

      public EntityType.Builder<T> attach(EntityAttachment $$0, Vec3 $$1) {
         this.attachments = this.attachments.attach($$0, $$1);
         return this;
      }

      public EntityType.Builder<T> noSummon() {
         this.summon = false;
         return this;
      }

      public EntityType.Builder<T> noSave() {
         this.serialize = false;
         return this;
      }

      public EntityType.Builder<T> fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public EntityType.Builder<T> immuneTo(Block... $$0) {
         this.immuneTo = ImmutableSet.copyOf($$0);
         return this;
      }

      public EntityType.Builder<T> canSpawnFarFromPlayer() {
         this.canSpawnFarFromPlayer = true;
         return this;
      }

      public EntityType.Builder<T> clientTrackingRange(int $$0) {
         this.clientTrackingRange = $$0;
         return this;
      }

      public EntityType.Builder<T> updateInterval(int $$0) {
         this.updateInterval = $$0;
         return this;
      }

      public EntityType.Builder<T> requiredFeatures(FeatureFlag... $$0) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset($$0);
         return this;
      }

      public EntityType.Builder<T> noLootTable() {
         this.lootTable = DependantName.fixed(Optional.empty());
         return this;
      }

      public EntityType.Builder<T> notInPeaceful() {
         this.allowedInPeaceful = false;
         return this;
      }

      public EntityType<T> build(ResourceKey<EntityType<?>> $$0) {
         if (this.serialize) {
            Util.fetchChoiceType(References.ENTITY_TREE, $$0.location().toString());
         }

         return new EntityType<>(
            this.factory,
            this.category,
            this.serialize,
            this.summon,
            this.fireImmune,
            this.canSpawnFarFromPlayer,
            this.immuneTo,
            this.dimensions.withAttachments(this.attachments),
            this.spawnDimensionsScale,
            this.clientTrackingRange,
            this.updateInterval,
            (String)this.descriptionId.get($$0),
            (Optional<ResourceKey<LootTable>>)this.lootTable.get($$0),
            this.requiredFeatures,
            this.allowedInPeaceful
         );
      }
   }

   @FunctionalInterface
   public interface EntityFactory<T extends Entity> {
      @Nullable
      T create(EntityType<T> var1, Level var2);
   }
}
