package com.github.alexthe666.alexsmobs.config;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class CommonConfig {
   public final DoubleValue lavaOpacity;
   public final BooleanValue shadersCompat;
   public final BooleanValue neutralBoneSerpents;
   public final BooleanValue lavaBottleEnabled;
   public final BooleanValue spidersAttackFlies;
   public final BooleanValue wolvesAttackMoose;
   public final BooleanValue polarBearsAttackSeals;
   public final BooleanValue catsAndFoxesAttackJerboas;
   public final BooleanValue dolphinsAttackFlyingFish;
   public final BooleanValue bananasDropFromLeaves;
   public final IntValue bananaChance;
   public final IntValue grizzlyBearSpawnWeight;
   public final IntValue grizzlyBearSpawnRolls;
   public final IntValue roadrunnerSpawnWeight;
   public final IntValue roadrunnerSpawnRolls;
   public final IntValue boneSerpentSpawnWeight;
   public final IntValue boneSeprentSpawnRolls;
   public final IntValue gazelleSpawnWeight;
   public final IntValue gazelleSpawnRolls;
   public final IntValue crocodileSpawnWeight;
   public final IntValue crocSpawnRolls;
   public final IntValue flySpawnWeight;
   public final IntValue flySpawnRolls;
   public final IntValue hummingbirdSpawnWeight;
   public final IntValue hummingbirdSpawnRolls;
   public final IntValue orcaSpawnWeight;
   public final IntValue orcaSpawnRolls;
   public final IntValue sunbirdSpawnWeight;
   public final IntValue sunbirdSpawnRolls;
   public final IntValue gorillaSpawnWeight;
   public final IntValue gorillaSpawnRolls;
   public final IntValue crimsonMosquitoSpawnWeight;
   public final IntValue crimsonMosquitoSpawnRolls;
   public final IntValue rattlesnakeSpawnWeight;
   public final IntValue rattlesnakeSpawnRolls;
   public final IntValue endergradeSpawnWeight;
   public final IntValue endergradeSpawnRolls;
   public final IntValue hammerheadSharkSpawnWeight;
   public final IntValue hammerheadSharkSpawnRolls;
   public final IntValue lobsterSpawnWeight;
   public final IntValue lobsterSpawnRolls;
   public final IntValue komodoDragonSpawnWeight;
   public final IntValue komodoDragonSpawnRolls;
   public final IntValue capuchinMonkeySpawnWeight;
   public final IntValue capuchinMonkeySpawnRolls;
   public final IntValue caveCentipedeSpawnWeight;
   public final IntValue caveCentipedeSpawnRolls;
   public final IntValue caveCentipedeSpawnHeight;
   public final IntValue warpedToadSpawnWeight;
   public final IntValue warpedToadSpawnRolls;
   public final IntValue mooseSpawnWeight;
   public final IntValue mooseSpawnRolls;
   public final IntValue mimicubeSpawnWeight;
   public final IntValue mimicubeSpawnRolls;
   public final IntValue raccoonSpawnWeight;
   public final IntValue raccoonSpawnRolls;
   public final IntValue blobfishSpawnWeight;
   public final IntValue blobfishSpawnRolls;
   public final IntValue blobfishSpawnHeight;
   public final IntValue sealSpawnWeight;
   public final IntValue sealSpawnRolls;
   public final IntValue cockroachSpawnWeight;
   public final IntValue cockroachSpawnRolls;
   public final IntValue shoebillSpawnWeight;
   public final IntValue shoebillSpawnRolls;
   public final IntValue elephantSpawnWeight;
   public final IntValue elephantSpawnRolls;
   public final IntValue soulVultureSpawnWeight;
   public final IntValue soulVultureSpawnRolls;
   public final IntValue snowLeopardSpawnWeight;
   public final IntValue snowLeopardSpawnRolls;
   public final IntValue spectreSpawnWeight;
   public final IntValue spectreSpawnRolls;
   public final IntValue crowSpawnWeight;
   public final IntValue crowSpawnRolls;
   public final IntValue alligatorSnappingTurtleSpawnWeight;
   public final IntValue alligatorSnappingTurtleSpawnRolls;
   public final IntValue mungusSpawnWeight;
   public final IntValue mungusSpawnRolls;
   public final IntValue mantisShrimpSpawnWeight;
   public final IntValue mantisShrimpSpawnRolls;
   public final IntValue gusterSpawnWeight;
   public final IntValue gusterSpawnRolls;
   public final IntValue warpedMoscoSpawnWeight;
   public final IntValue warpedMoscoSpawnRolls;
   public final IntValue straddlerSpawnWeight;
   public final IntValue straddlerSpawnRolls;
   public final IntValue stradpoleSpawnWeight;
   public final IntValue stradpoleSpawnRolls;
   public final IntValue emuSpawnWeight;
   public final IntValue emuSpawnRolls;
   public final IntValue platypusSpawnWeight;
   public final IntValue platypusSpawnRolls;
   public final IntValue dropbearSpawnWeight;
   public final IntValue dropbearSpawnRolls;
   public final IntValue tasmanianDevilSpawnWeight;
   public final IntValue tasmanianDevilSpawnRolls;
   public final IntValue kangarooSpawnWeight;
   public final IntValue kangarooSpawnRolls;
   public final IntValue cachalotWhaleSpawnWeight;
   public final IntValue cachalotWhaleSpawnRolls;
   public final IntValue enderiophageSpawnWeight;
   public final IntValue enderiophageSpawnRolls;
   public final IntValue baldEagleSpawnWeight;
   public final IntValue baldEagleSpawnRolls;
   public final IntValue tigerSpawnWeight;
   public final IntValue tigerSpawnRolls;
   public final IntValue tarantulaHawkSpawnWeight;
   public final IntValue tarantulaHawkSpawnRolls;
   public final IntValue voidWormSpawnWeight;
   public final IntValue voidWormSpawnRolls;
   public final IntValue frilledSharkSpawnWeight;
   public final IntValue frilledSharkSpawnRolls;
   public final IntValue mimicOctopusSpawnWeight;
   public final IntValue mimicOctopusSpawnRolls;
   public final IntValue seagullSpawnWeight;
   public final IntValue seagullSpawnRolls;
   public final IntValue froststalkerSpawnWeight;
   public final IntValue froststalkerSpawnRolls;
   public final IntValue tusklinSpawnWeight;
   public final IntValue tusklinSpawnRolls;
   public final IntValue laviathanSpawnWeight;
   public final IntValue laviathanSpawnRolls;
   public final IntValue cosmawSpawnWeight;
   public final IntValue cosmawSpawnRolls;
   public final IntValue toucanSpawnWeight;
   public final IntValue toucanSpawnRolls;
   public final IntValue manedWolfSpawnWeight;
   public final IntValue manedWolfSpawnRolls;
   public final IntValue anacondaSpawnWeight;
   public final IntValue anacondaSpawnRolls;
   public final IntValue anteaterSpawnWeight;
   public final IntValue anteaterSpawnRolls;
   public final IntValue rockyRollerSpawnWeight;
   public final IntValue rockyRollerSpawnRolls;
   public final IntValue flutterSpawnWeight;
   public final IntValue flutterSpawnRolls;
   public final IntValue geladaMonkeySpawnWeight;
   public final IntValue geladaMonkeySpawnRolls;
   public final IntValue geladaMonkeySpawnHeight;
   public final IntValue jerboaSpawnRolls;
   public final IntValue jerboaSpawnWeight;
   public final IntValue terrapinSpawnRolls;
   public final IntValue terrapinSpawnWeight;
   public final IntValue combJellySpawnRolls;
   public final IntValue combJellySpawnWeight;
   public final IntValue cosmicCodSpawnRolls;
   public final IntValue cosmicCodSpawnWeight;
   public final IntValue bunfungusSpawnWeight;
   public final IntValue bunfungusSpawnRolls;
   public final IntValue bisonSpawnWeight;
   public final IntValue bisonSpawnRolls;
   public final IntValue giantSquidSpawnWeight;
   public final IntValue giantSquidSpawnRolls;
   public final IntValue devilsHolePupfishSpawnWeight;
   public final IntValue devilsHolePupfishSpawnRolls;
   public final IntValue catfishSpawnWeight;
   public final IntValue catfishSpawnRolls;
   public final IntValue flyingFishSpawnWeight;
   public final IntValue flyingFishSpawnRolls;
   public final IntValue skelewagSpawnWeight;
   public final IntValue skelewagSpawnRolls;
   public final IntValue rainFrogSpawnWeight;
   public final IntValue rainFrogSpawnRolls;
   public final IntValue potooSpawnWeight;
   public final IntValue potooSpawnRolls;
   public final IntValue mudskipperSpawnWeight;
   public final IntValue mudskipperSpawnRolls;
   public final IntValue rhinocerosSpawnWeight;
   public final IntValue rhinocerosSpawnRolls;
   public final IntValue sugarGliderSpawnWeight;
   public final IntValue sugarGliderSpawnRolls;
   public final IntValue farseerSpawnWeight;
   public final IntValue farseerSpawnRolls;
   public final IntValue skreecherSpawnWeight;
   public final IntValue skreecherSpawnRolls;
   public final IntValue underminerSpawnWeight;
   public final IntValue underminerSpawnRolls;
   public final IntValue murmurSpawnWeight;
   public final IntValue murmurSpawnRolls;
   public final IntValue murmurSpawnHeight;
   public final IntValue skunkSpawnWeight;
   public final IntValue skunkSpawnRolls;
   public final IntValue bananaSlugSpawnWeight;
   public final IntValue bananaSlugSpawnRolls;
   public final IntValue blueJaySpawnWeight;
   public final IntValue blueJaySpawnRolls;
   public final IntValue caimanSpawnWeight;
   public final IntValue caimanSpawnRolls;
   public final IntValue triopsSpawnWeight;
   public final IntValue triopsSpawnRolls;
   public final BooleanValue giveBookOnStartup;
   public final BooleanValue mimicubeSpawnInEndCity;
   public final BooleanValue mimicreamRepair;
   public final ConfigValue<List<? extends String>> mimicreamBlacklist;
   public final BooleanValue raccoonStealFromChests;
   public final BooleanValue crowsStealCrops;
   public final BooleanValue fishOilMeme;
   public final BooleanValue soulVultureSpawnOnFossil;
   public final BooleanValue acaciaBlossomsDropFromLeaves;
   public final IntValue acaciaBlossomChance;
   public final BooleanValue wanderingTraderOffers;
   public final IntValue mungusBiomeTransformationType;
   public final ConfigValue<List<? extends String>> mungusBiomeMatches;
   public BooleanValue limitGusterSpawnsToWeather;
   public BooleanValue warpedMoscoTransformation;
   public final ConfigValue<List<? extends String>> warpedMoscoMobTriggers;
   public final BooleanValue straddleboardEnchants;
   public final BooleanValue emuTargetSkeletons;
   public final DoubleValue emuPantsDodgeChance;
   public final DoubleValue leafcutterAntFungusGrowChance;
   public final IntValue leafcutterAntRepopulateFeedings;
   public final IntValue leafcutterAntColonySize;
   public final DoubleValue leafcutterAntBreakLeavesChance;
   public final BooleanValue beachedCachalotWhales;
   public final BooleanValue cachalotDestruction;
   public final DoubleValue cachalotVolume;
   public final IntValue beachedCachalotWhaleSpawnChance;
   public final IntValue beachedCachalotWhaleSpawnDelay;
   public final DoubleValue leafcutterAnthillSpawnChance;
   public final BooleanValue falconryTeleportsBack;
   public final BooleanValue fireproofTarantulaHawk;
   public final BooleanValue voidWormSummonable;
   public final ConfigValue<List<? extends String>> voidWormSpawnDimensions;
   public final DoubleValue voidWormDamageModifier;
   public final DoubleValue voidWormMaxHealth;
   public final BooleanValue seagullStealing;
   public final ConfigValue<List<? extends String>> seagullStealingBlacklist;
   public final BooleanValue clingingFlipEffect;
   public final DoubleValue tusklinShoesBarteringChance;
   public final DoubleValue rainbowGlassFidelity;
   public BooleanValue bunfungusTransformation;
   public BooleanValue restrictPupfishSpawns;
   public IntValue pupfishChunkSpawnDistance;
   public BooleanValue restrictSkelewagSpawns;
   public BooleanValue restrictFarseerSpawns;
   public BooleanValue restrictUnderminerSpawns;
   public IntValue farseerBorderSpawnDistance;
   public BooleanValue superSecretSettings;
   public BooleanValue addLootToChests;
   public final ConfigValue<List<? extends String>> transmutationBlacklist;
   public BooleanValue limitTransmutingToLootTables;
   public BooleanValue transmutingTableExplodes;
   public IntValue transmutingExperienceCost;
   public DoubleValue transmutingWeightAddStep;
   public DoubleValue transmutingWeightRemoveStep;
   public DoubleValue underminerDisappearDistance;
   public final BooleanValue skreechersSummonWarden;
   public IntValue pathfindingThreads;

   public CommonConfig(Builder builder) {
      builder.push("general");
      this.giveBookOnStartup = buildBoolean(
         builder, "giveBookOnStartup", "all", true, "Whether all players should get an Animal Dictionary when joining the world for the first time."
      );
      this.lavaOpacity = buildDouble(builder, "lavaVisionOpacity", "all", 0.65, 0.01, 1.0, "Lava Opacity for the Lava Vision Potion.");
      this.shadersCompat = buildBoolean(
         builder, "shadersCompat", "all", false, "Whether to disable certain aspects of the Lava Vision Potion. Enable if issues with shaders persist."
      );
      this.bananasDropFromLeaves = buildBoolean(
         builder, "bananasDropFromLeaves", "all", true, "Whether bananas should drop from blocks tagged with #alexsmobs:drops_bananas"
      );
      this.bananaChance = buildInt(
         builder,
         "bananaChance",
         "all",
         AMConfig.bananaChance,
         0,
         Integer.MAX_VALUE,
         "1 out of this number chance for leaves to drop a banana when broken. Fortune is automatically factored in"
      );
      this.spidersAttackFlies = buildBoolean(builder, "spidersAttackFlies", "all", true, "Whether spiders should target fly mobs.");
      this.wolvesAttackMoose = buildBoolean(builder, "wolvesAttackMoose", "all", true, "Whether wolves should target moose mobs.");
      this.polarBearsAttackSeals = buildBoolean(builder, "polarBearsAttackSeals", "all", true, "Whether polar bears should target seal mobs.");
      this.catsAndFoxesAttackJerboas = buildBoolean(
         builder, "catsAndFoxesAttackJerboas", "all", true, "Whether cats, ocelots and foxes should target jerboa mobs."
      );
      this.dolphinsAttackFlyingFish = buildBoolean(builder, "dolphinsAttackFlyingFish", "all", true, "Whether dolphins should target flying fish mobs.");
      this.lavaBottleEnabled = buildBoolean(builder, "lavaBottleEnabled", "all", true, "Whether lava can be bottled with a right click of a glass bottle.");
      this.neutralBoneSerpents = buildBoolean(builder, "neutralBoneSerpents", "all", false, "Whether bone serpents are neutral or hostile.");
      this.mimicubeSpawnInEndCity = buildBoolean(
         builder,
         "mimicubeSpawnInEndCity",
         "all",
         true,
         "Whether mimicubes spawns should be restricted solely to the end city structure or to whatever biome is specified in their respective biome config."
      );
      this.mimicreamRepair = buildBoolean(builder, "mimicreamRepair", "all", true, "Whether mimicream can be used to duplicate items.");
      this.mimicreamBlacklist = builder.comment(
            "Blacklist for items that mimicream cannot make a copy of. Ex: \"minecraft:stone_sword\", \"alexsmobs:blood_sprayer\""
         )
         .defineList("mimicreamBlacklist", Lists.newArrayList(new String[]{"alexsmobs:blood_sprayer", "alexsmobs:hemolymph_blaster"}), o -> o instanceof String);
      this.raccoonStealFromChests = buildBoolean(builder, "raccoonStealFromChests", "all", true, "Whether wild raccoons steal food from chests.");
      this.crowsStealCrops = buildBoolean(builder, "crowsStealCrops", "all", true, "Whether wild crows steal crops from farmland.");
      this.fishOilMeme = buildBoolean(builder, "fishOilMeme", "all", true, "Whether fish oil gives players a special levitation effect.");
      this.soulVultureSpawnOnFossil = buildBoolean(
         builder,
         "soulVultureSpawnOnFossil",
         "all",
         true,
         "Whether soul vulture spawns should be restricted solely to the nether fossil structure or to whatever biome is specified in their respective biome config."
      );
      this.acaciaBlossomsDropFromLeaves = buildBoolean(
         builder, "acaciaBlossomsDropFromLeaves", "all", true, "Whether acacia blossoms should drop from blocks tagged with #alexsmobs:drops_acacia_blossoms"
      );
      this.acaciaBlossomChance = buildInt(
         builder,
         "acaciaBlossomChance",
         "all",
         AMConfig.acaciaBlossomChance,
         0,
         Integer.MAX_VALUE,
         "1 out of this number chance for leaves to drop an acacia when broken. Fortune is automatically factored in"
      );
      this.wanderingTraderOffers = buildBoolean(
         builder, "wanderingTraderOffers", "all", true, "Whether wandering traders offer items like acacia blossoms, mosquito larva, crocodile egg, etc."
      );
      this.mungusBiomeTransformationType = buildInt(
         builder,
         "mungusBiomeTransformationType",
         "all",
         AMConfig.mungusBiomeTransformationType,
         0,
         2,
         "0 = no mungus biome transformation. 1 = mungus changes blocks, but not chunk's biome. 2 = mungus transforms blocks and biome of chunk."
      );
      this.mungusBiomeMatches = builder.comment(
            "List of all mungus mushrooms, biome transformations and surface blocks. Each is seperated by a |. Add an entry with a block registry name, biome registry name, and block registry name(for the ground)."
         )
         .defineList("mungusBiomeMatches", AMConfig.mungusBiomeMatches, o -> o instanceof String);
      this.limitGusterSpawnsToWeather = buildBoolean(
         builder, "limitGusterSpawnsToWeather", "all", true, "Whether guster spawns are limited to when it is raining/thundering."
      );
      this.warpedMoscoTransformation = buildBoolean(
         builder,
         "warpedMoscoTransformation",
         "all",
         true,
         "Whether Crimson Mosquitoes can transform into Warped Moscos if attacking a Mungus or any listed creature."
      );
      this.warpedMoscoMobTriggers = builder.comment(
            "List of extra(non mungus) mobs that will trigger a crimson mosquito to become a warped mosquito. Ex: \"minecraft:mooshroom\", \"alexsmobs:warped_toad\""
         )
         .defineList("warpedMoscoMobTriggers", Lists.newArrayList(new String[]{""}), o -> o instanceof String);
      this.straddleboardEnchants = buildBoolean(builder, "straddleboardEnchants", "all", true, "True if straddleboard enchants are enabled.");
      this.emuTargetSkeletons = buildBoolean(builder, "emuTargetSkeletons", "all", true, "Whether emu should target skeletons.");
      this.emuPantsDodgeChance = buildDouble(
         builder, "emuPantsDodgeChance", "all", 0.45, 0.0, 1.0, "Percent chance for emu leggings to dodge projectile attacks."
      );
      this.cachalotDestruction = buildBoolean(builder, "cachalotDestruction", "all", true, "Whether cachalots can destroy wood blocks if angry.");
      this.cachalotVolume = buildDouble(
         builder,
         "cachalotVolume",
         "all",
         3.0,
         0.0,
         10.0,
         "Relative volume of cachalot whales compared to other animals. Note that irl they are the loudest animal. Turn this down if you find their clicks annoying."
      );
      this.leafcutterAntFungusGrowChance = buildDouble(
         builder,
         "leafcutterAntFungusGrowChance",
         "all",
         0.3,
         0.0,
         1.0,
         "Percent chance for fungus to grow per each leaf a leafcutter ant returns to the colony."
      );
      this.leafcutterAntRepopulateFeedings = buildInt(
         builder,
         "leafcutterAntRepopulateFeedings",
         "all",
         AMConfig.leafcutterAntRepopulateFeedings,
         2,
         100000,
         "How many feedings of leaves does a leafcutter colony need in order to regain a worker ant, if below half the max members."
      );
      this.leafcutterAntColonySize = buildInt(
         builder,
         "leafcutterAntColonySize",
         "all",
         AMConfig.leafcutterAntColonySize,
         2,
         100000,
         "Max number of ant entities allowed inside a leafcutter anthill."
      );
      this.leafcutterAntBreakLeavesChance = buildDouble(
         builder,
         "leafcutterAntBreakLeavesChance",
         "all",
         0.2,
         0.0,
         1.0,
         "Percent chance for leafcutter ants to break leaves blocks when harvesting. Set to zero so that they can not break any blocks."
      );
      this.falconryTeleportsBack = buildBoolean(
         builder,
         "falconryTeleportsBack",
         "all",
         false,
         "Makes eagles teleport back to their owner if they get stuck during controlled flight. Useful for when playing with the Optifine mod, since this mod is the fault of many issues with the falconry system."
      );
      this.fireproofTarantulaHawk = buildBoolean(
         builder, "fireproofTarantulaHawk", "all", false, "Makes Tarantula Hawks fireproof, perfect if you also want these guys to spawn in the nether."
      );
      this.voidWormSpawnDimensions = builder.comment("List of dimensions in which spawning void worms via mysterious worm items is allowed.")
         .defineList("voidWormSpawnDimensions", Lists.newArrayList(new String[]{"minecraft:the_end"}), o -> o instanceof String);
      this.voidWormDamageModifier = buildDouble(builder, "voidWormDamageModifier", "all", 1.0, 0.0, 100.0, "All void worm damage is scaled to this.");
      this.voidWormMaxHealth = buildDouble(builder, "voidWormMaxHealth", "all", 160.0, 0.0, 1000000.0, "Max Health of the void worm boss.");
      this.voidWormSummonable = buildBoolean(
         builder, "voidWormSummonable", "all", true, "Whether the void worm boss is summonable or not, via the mysterious worm item."
      );
      this.seagullStealing = buildBoolean(builder, "seagullStealing", "all", true, "Whether seagulls should steal food out of players' hotbar slots.");
      this.seagullStealingBlacklist = builder.comment("List of items that seagulls cannot take from players.")
         .defineList("seagullStealingBlacklist", Lists.newArrayList(), o -> o instanceof String);
      this.clingingFlipEffect = buildBoolean(
         builder, "clingingFlipEffect", "all", false, "Whether the Clinging Potion effect should flip the screen. Warning: may cause nausea."
      );
      this.tusklinShoesBarteringChance = buildDouble(
         builder, "tusklinShoesBarteringChance", "all", 0.025F, 0.0, 1.0, "Percent chance of getting Pigshoes from Piglin Bartering. Set to zero to disable."
      );
      this.rainbowGlassFidelity = buildDouble(
         builder,
         "rainbowGlassFidelity",
         "all",
         16.0,
         1.0,
         10000.0,
         "The visual zoom of the rainbow pattern on the rainbow glass block. Higher number = bigger pattern."
      );
      this.bunfungusTransformation = buildBoolean(
         builder, "bunfungusTransformation", "all", true, "Whether Rabbits can transform into Bunfungus if fed Mungal spores."
      );
      this.addLootToChests = buildBoolean(builder, "addLootToChests", "all", true, "True if some Alex's Mobs items should spawn in loot chests.");
      this.transmutationBlacklist = builder.comment("List of items that cannot be put in a Transmuting Table.")
         .defineList("transmutationBlacklist", Lists.newArrayList(new String[]{"minecraft:beacon"}), o -> o instanceof String);
      this.limitTransmutingToLootTables = buildBoolean(
         builder,
         "limitTransmutingToLootTables",
         "all",
         false,
         "True if transmutation tables should not have the ability to pick up new items to transmute, and only give options from the loot tables."
      );
      this.transmutingTableExplodes = buildBoolean(builder, "transmutingTableExplodes", "all", true, "True if transmutation tables can explode when broken.");
      this.transmutingExperienceCost = buildInt(
         builder,
         "transmutingExperienceCost",
         "all",
         AMConfig.transmutingExperienceCost,
         0,
         100000,
         "The experience, in levels, that each transmutation of a stack takes in the transmuting table."
      );
      this.transmutingWeightAddStep = buildDouble(
         builder,
         "transmutingWeightAddStep",
         "all",
         3.0,
         1.0,
         10000.0,
         "The step value multiplied by the log of the stack size when transmuting an item, used to determine its weight for appearing in future transmutation possibilities. Higher number = more likely to appear."
      );
      this.transmutingWeightRemoveStep = buildDouble(
         builder,
         "transmutingWeightRemoveStep",
         "all",
         4.0,
         1.0,
         10000.0,
         "The step value that an item looses when selecting it as the transmutation result. Keep this number higher than the one above for balance reasons. Higher number = less likely to appear after transmuting multiple times."
      );
      this.skreechersSummonWarden = buildBoolean(builder, "skreechersSummonWarden", "all", true, "True if skreechers can summon a new warden, when applicable.");
      this.underminerDisappearDistance = buildDouble(
         builder,
         "underminerDisappearDistance",
         "all",
         8.0,
         1.0,
         10000.0,
         "The distance in blocks that will cause an underminer to dissapear when approached by a player."
      );
      builder.pop();
      builder.push("spawning");
      this.grizzlyBearSpawnWeight = buildInt(
         builder,
         "grizzlyBearSpawnWeight",
         "spawns",
         AMConfig.grizzlyBearSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.grizzlyBearSpawnRolls = buildInt(
         builder,
         "grizzlyBearSpawnRolls",
         "spawns",
         AMConfig.grizzlyBearSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.roadrunnerSpawnWeight = buildInt(
         builder,
         "roadrunnerSpawnWeight",
         "spawns",
         AMConfig.roadrunnerSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.roadrunnerSpawnRolls = buildInt(
         builder,
         "roadrunnerSpawnRolls",
         "spawns",
         AMConfig.roadrunnerSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.boneSerpentSpawnWeight = buildInt(
         builder,
         "boneSerpentSpawnWeight",
         "spawns",
         AMConfig.boneSerpentSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.boneSeprentSpawnRolls = buildInt(
         builder,
         "boneSeprentSpawnRolls",
         "spawns",
         AMConfig.boneSeprentSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.gazelleSpawnWeight = buildInt(
         builder,
         "gazelleSpawnWeight",
         "spawns",
         AMConfig.gazelleSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.gazelleSpawnRolls = buildInt(
         builder,
         "gazelleSpawnRolls",
         "spawns",
         AMConfig.gazelleSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.crocodileSpawnWeight = buildInt(
         builder,
         "crocodileSpawnWeight",
         "spawns",
         AMConfig.crocodileSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.crocSpawnRolls = buildInt(
         builder,
         "crocSpawnRolls",
         "spawns",
         AMConfig.crocSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.flySpawnWeight = buildInt(
         builder,
         "flySpawnWeight",
         "spawns",
         AMConfig.flySpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.flySpawnRolls = buildInt(
         builder,
         "flySpawnRolls",
         "spawns",
         AMConfig.flySpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.hummingbirdSpawnWeight = buildInt(
         builder,
         "hummingbirdSpawnWeight",
         "spawns",
         AMConfig.hummingbirdSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.hummingbirdSpawnRolls = buildInt(
         builder,
         "hummingbirdSpawnRolls",
         "spawns",
         AMConfig.flySpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.orcaSpawnWeight = buildInt(
         builder,
         "orcaSpawnWeight",
         "spawns",
         AMConfig.orcaSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.orcaSpawnRolls = buildInt(
         builder,
         "orcaSpawnRolls",
         "spawns",
         AMConfig.orcaSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.sunbirdSpawnWeight = buildInt(
         builder,
         "sunbirdSpawnWeight",
         "spawns",
         AMConfig.sunbirdSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.sunbirdSpawnRolls = buildInt(
         builder,
         "sunbirdSpawnRolls",
         "spawns",
         AMConfig.sunbirdSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.gorillaSpawnWeight = buildInt(
         builder,
         "gorillaSpawnWeight",
         "spawns",
         AMConfig.gorillaSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.gorillaSpawnRolls = buildInt(
         builder,
         "gorillaSpawnRolls",
         "spawns",
         AMConfig.gorillaSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.crimsonMosquitoSpawnWeight = buildInt(
         builder,
         "crimsonMosquitoSpawnWeight",
         "spawns",
         AMConfig.crimsonMosquitoSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.crimsonMosquitoSpawnRolls = buildInt(
         builder,
         "crimsonMosquitoSpawnRolls",
         "spawns",
         AMConfig.crimsonMosquitoSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.rattlesnakeSpawnWeight = buildInt(
         builder,
         "rattlesnakeSpawnWeight",
         "spawns",
         AMConfig.rattlesnakeSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.rattlesnakeSpawnRolls = buildInt(
         builder,
         "rattlesnakeSpawnRolls",
         "spawns",
         AMConfig.rattlesnakeSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.endergradeSpawnWeight = buildInt(
         builder,
         "endergradeSpawnWeight",
         "spawns",
         AMConfig.endergradeSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.endergradeSpawnRolls = buildInt(
         builder,
         "endergradeSpawnRolls",
         "spawns",
         AMConfig.endergradeSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.hammerheadSharkSpawnWeight = buildInt(
         builder,
         "hammerheadSharkSpawnWeight",
         "spawns",
         AMConfig.hammerheadSharkSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.hammerheadSharkSpawnRolls = buildInt(
         builder,
         "hammerheadSharkSpawnRolls",
         "spawns",
         AMConfig.hammerheadSharkSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.lobsterSpawnWeight = buildInt(
         builder,
         "lobsterSpawnWeight",
         "spawns",
         AMConfig.lobsterSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.lobsterSpawnRolls = buildInt(
         builder,
         "lobsterSpawnRolls",
         "spawns",
         AMConfig.lobsterSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.komodoDragonSpawnWeight = buildInt(
         builder,
         "komodoDragonSpawnWeight",
         "spawns",
         AMConfig.komodoDragonSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.komodoDragonSpawnRolls = buildInt(
         builder,
         "komodoDragonSpawnRolls",
         "spawns",
         AMConfig.komodoDragonSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.capuchinMonkeySpawnWeight = buildInt(
         builder,
         "capuchinMonkeySpawnWeight",
         "spawns",
         AMConfig.capuchinMonkeySpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.capuchinMonkeySpawnRolls = buildInt(
         builder,
         "capuchinMonkeySpawnRolls",
         "spawns",
         AMConfig.capuchinMonkeySpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.caveCentipedeSpawnWeight = buildInt(
         builder,
         "caveCentipedeSpawnWeight",
         "spawns",
         AMConfig.caveCentipedeSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.caveCentipedeSpawnRolls = buildInt(
         builder,
         "caveCentipedeSpawnRolls",
         "spawns",
         AMConfig.caveCentipedeSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.warpedToadSpawnWeight = buildInt(
         builder,
         "warpedToadSpawnWeight",
         "spawns",
         AMConfig.warpedToadSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.warpedToadSpawnRolls = buildInt(
         builder,
         "warpedToadSpawnRolls",
         "spawns",
         AMConfig.warpedToadSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.mooseSpawnWeight = buildInt(
         builder,
         "mooseSpawnWeight",
         "spawns",
         AMConfig.mooseSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.mooseSpawnRolls = buildInt(
         builder,
         "mooseSpawnRolls",
         "spawns",
         AMConfig.mooseSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.mimicubeSpawnWeight = buildInt(
         builder,
         "mimicubeSpawnWeight",
         "spawns",
         AMConfig.mimicubeSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.mimicubeSpawnRolls = buildInt(
         builder,
         "mimicubeSpawnRolls",
         "spawns",
         AMConfig.mimicubeSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.raccoonSpawnWeight = buildInt(
         builder,
         "raccoonSpawnWeight",
         "spawns",
         AMConfig.raccoonSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.raccoonSpawnRolls = buildInt(
         builder,
         "raccoonSpawnRolls",
         "spawns",
         AMConfig.raccoonSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.blobfishSpawnWeight = buildInt(
         builder,
         "blobfishSpawnWeight",
         "spawns",
         AMConfig.blobfishSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.blobfishSpawnRolls = buildInt(
         builder,
         "blobfishSpawnRolls",
         "spawns",
         AMConfig.blobfishSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.sealSpawnWeight = buildInt(
         builder,
         "sealSpawnWeight",
         "spawns",
         AMConfig.sealSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.sealSpawnRolls = buildInt(
         builder,
         "sealSpawnRolls",
         "spawns",
         AMConfig.sealSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.cockroachSpawnWeight = buildInt(
         builder,
         "cockroachSpawnWeight",
         "spawns",
         AMConfig.cockroachSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.cockroachSpawnRolls = buildInt(
         builder,
         "cockroachSpawnRolls",
         "spawns",
         AMConfig.cockroachSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.shoebillSpawnWeight = buildInt(
         builder,
         "shoebillSpawnWeight",
         "spawns",
         AMConfig.shoebillSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.shoebillSpawnRolls = buildInt(
         builder,
         "shoebillSpawnRolls",
         "spawns",
         AMConfig.shoebillSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.elephantSpawnWeight = buildInt(
         builder,
         "elephantSpawnWeight",
         "spawns",
         AMConfig.elephantSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.elephantSpawnRolls = buildInt(
         builder,
         "elephantSpawnRolls",
         "spawns",
         AMConfig.elephantSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.soulVultureSpawnWeight = buildInt(
         builder,
         "soulVultureSpawnWeight",
         "spawns",
         AMConfig.soulVultureSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.soulVultureSpawnRolls = buildInt(
         builder,
         "soulVultureSpawnRolls",
         "spawns",
         AMConfig.soulVultureSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.snowLeopardSpawnWeight = buildInt(
         builder,
         "snowLeopardSpawnWeight",
         "spawns",
         AMConfig.snowLeopardSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.snowLeopardSpawnRolls = buildInt(
         builder,
         "snowLeopardSpawnRolls",
         "spawns",
         AMConfig.snowLeopardSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.spectreSpawnWeight = buildInt(
         builder,
         "spectreSpawnWeight",
         "spawns",
         AMConfig.spectreSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.spectreSpawnRolls = buildInt(
         builder,
         "spectreSpawnRolls",
         "spawns",
         AMConfig.spectreSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.crowSpawnWeight = buildInt(
         builder,
         "crowSpawnWeight",
         "spawns",
         AMConfig.crowSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.crowSpawnRolls = buildInt(
         builder,
         "crowSpawnRolls",
         "spawns",
         AMConfig.crowSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.alligatorSnappingTurtleSpawnWeight = buildInt(
         builder,
         "alligatorSnappingTurtleSpawnWeight",
         "spawns",
         AMConfig.alligatorSnappingTurtleSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.alligatorSnappingTurtleSpawnRolls = buildInt(
         builder,
         "alligatorSnappingTurtleSpawnRolls",
         "spawns",
         AMConfig.alligatorSnappingTurtleSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.mungusSpawnWeight = buildInt(
         builder,
         "mungusSpawnWeight",
         "spawns",
         AMConfig.mungusSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.mungusSpawnRolls = buildInt(
         builder,
         "mungusSpawnRolls",
         "spawns",
         AMConfig.mungusSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.mantisShrimpSpawnWeight = buildInt(
         builder,
         "mantisShrimpSpawnWeight",
         "spawns",
         AMConfig.mantisShrimpSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.mantisShrimpSpawnRolls = buildInt(
         builder,
         "mantisShrimpSpawnRolls",
         "spawns",
         AMConfig.mantisShrimpSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.gusterSpawnWeight = buildInt(
         builder,
         "gusterSpawnWeight",
         "spawns",
         AMConfig.gusterSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.gusterSpawnRolls = buildInt(
         builder,
         "gusterSpawnRolls",
         "spawns",
         AMConfig.gusterSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.warpedMoscoSpawnWeight = buildInt(
         builder,
         "warpedMoscoSpawnWeight",
         "spawns",
         AMConfig.warpedMoscoSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn. NOTE: By default the warped mosco doesn't spawn in any biomes."
      );
      this.warpedMoscoSpawnRolls = buildInt(
         builder,
         "warpedMoscoSpawnRolls",
         "spawns",
         AMConfig.warpedMoscoSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.straddlerSpawnWeight = buildInt(
         builder,
         "straddlerSpawnWeight",
         "spawns",
         AMConfig.straddlerSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.straddlerSpawnRolls = buildInt(
         builder,
         "straddlerSpawnRolls",
         "spawns",
         AMConfig.straddlerSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.stradpoleSpawnWeight = buildInt(
         builder,
         "stradpoleSpawnWeight",
         "spawns",
         AMConfig.stradpoleSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.stradpoleSpawnRolls = buildInt(
         builder,
         "stradpoleSpawnRolls",
         "spawns",
         AMConfig.stradpoleSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.emuSpawnWeight = buildInt(
         builder,
         "emuSpawnWeight",
         "spawns",
         AMConfig.emuSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.emuSpawnRolls = buildInt(
         builder,
         "emuSpawnRolls",
         "spawns",
         AMConfig.emuSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.platypusSpawnWeight = buildInt(
         builder,
         "platypusSpawnWeight",
         "spawns",
         AMConfig.platypusSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.platypusSpawnRolls = buildInt(
         builder,
         "platypusSpawnRolls",
         "spawns",
         AMConfig.platypusSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.dropbearSpawnWeight = buildInt(
         builder,
         "dropbearSpawnWeight",
         "spawns",
         AMConfig.dropbearSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.dropbearSpawnRolls = buildInt(
         builder,
         "dropbearSpawnRolls",
         "spawns",
         AMConfig.dropbearSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.tasmanianDevilSpawnWeight = buildInt(
         builder,
         "tasmanianDevilSpawnWeight",
         "spawns",
         AMConfig.tasmanianDevilSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.tasmanianDevilSpawnRolls = buildInt(
         builder,
         "tasmanianDevilSpawnRolls",
         "spawns",
         AMConfig.tasmanianDevilSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.kangarooSpawnWeight = buildInt(
         builder,
         "kangarooSpawnWeight",
         "spawns",
         AMConfig.kangarooSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.kangarooSpawnRolls = buildInt(
         builder,
         "kangarooSpawnRolls",
         "spawns",
         AMConfig.kangarooSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.cachalotWhaleSpawnWeight = buildInt(
         builder,
         "cachalotWhaleSpawnWeight",
         "spawns",
         AMConfig.cachalotWhaleSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.cachalotWhaleSpawnRolls = buildInt(
         builder,
         "cachalotWhaleSpawnRolls",
         "spawns",
         AMConfig.cachalotWhaleSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.enderiophageSpawnWeight = buildInt(
         builder,
         "enderiophageSpawnWeight",
         "spawns",
         AMConfig.enderiophageSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.enderiophageSpawnRolls = buildInt(
         builder,
         "enderiophageSpawnRolls",
         "spawns",
         AMConfig.enderiophageSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.baldEagleSpawnWeight = buildInt(
         builder,
         "baldEagleSpawnWeight",
         "spawns",
         AMConfig.baldEagleSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.baldEagleSpawnRolls = buildInt(
         builder,
         "baldEagleSpawnRolls",
         "spawns",
         AMConfig.baldEagleSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.tigerSpawnWeight = buildInt(
         builder,
         "tigerSpawnWeight",
         "spawns",
         AMConfig.tigerSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.tigerSpawnRolls = buildInt(
         builder,
         "tigerSpawnRolls",
         "spawns",
         AMConfig.tigerSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.tarantulaHawkSpawnWeight = buildInt(
         builder,
         "tarantulaHawkSpawnWeight",
         "spawns",
         AMConfig.tarantulaHawkSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.tarantulaHawkSpawnRolls = buildInt(
         builder,
         "tarantulaHawkSpawnRolls",
         "spawns",
         AMConfig.tarantulaHawkSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.voidWormSpawnWeight = buildInt(
         builder,
         "voidWormSpawnWeight",
         "spawns",
         AMConfig.voidWormSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.voidWormSpawnRolls = buildInt(
         builder,
         "voidWormSpawnRolls",
         "spawns",
         AMConfig.voidWormSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.frilledSharkSpawnWeight = buildInt(
         builder,
         "frilledSharkSpawnWeight",
         "spawns",
         AMConfig.frilledSharkSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.frilledSharkSpawnRolls = buildInt(
         builder,
         "frilledSharkSpawnRolls",
         "spawns",
         AMConfig.frilledSharkSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.mimicOctopusSpawnWeight = buildInt(
         builder,
         "mimicOctopusSpawnWeight",
         "spawns",
         AMConfig.mimicOctopusSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.mimicOctopusSpawnRolls = buildInt(
         builder,
         "mimicOctopusSpawnRolls",
         "spawns",
         AMConfig.mimicOctopusSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.seagullSpawnWeight = buildInt(
         builder,
         "seagullSpawnWeight",
         "spawns",
         AMConfig.seagullSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.seagullSpawnRolls = buildInt(
         builder,
         "seagullSpawnRolls",
         "spawns",
         AMConfig.seagullSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.froststalkerSpawnWeight = buildInt(
         builder,
         "froststalkerSpawnWeight",
         "spawns",
         AMConfig.froststalkerSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.froststalkerSpawnRolls = buildInt(
         builder,
         "froststalkerSpawnRolls",
         "spawns",
         AMConfig.froststalkerSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.tusklinSpawnWeight = buildInt(
         builder,
         "tusklinSpawnWeight",
         "spawns",
         AMConfig.tusklinSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.tusklinSpawnRolls = buildInt(
         builder,
         "tusklinSpawnRolls",
         "spawns",
         AMConfig.tusklinSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.laviathanSpawnWeight = buildInt(
         builder,
         "laviathanSpawnWeight",
         "spawns",
         AMConfig.laviathanSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.laviathanSpawnRolls = buildInt(
         builder,
         "laviathanSpawnRolls",
         "spawns",
         AMConfig.laviathanSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.cosmawSpawnWeight = buildInt(
         builder,
         "cosmawSpawnWeight",
         "spawns",
         AMConfig.cosmawSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.cosmawSpawnRolls = buildInt(
         builder,
         "cosmawSpawnRolls",
         "spawns",
         AMConfig.cosmawSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.toucanSpawnWeight = buildInt(
         builder,
         "toucanSpawnWeight",
         "spawns",
         AMConfig.toucanSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.toucanSpawnRolls = buildInt(
         builder,
         "toucanSpawnRolls",
         "spawns",
         AMConfig.toucanSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.manedWolfSpawnWeight = buildInt(
         builder,
         "manedWolfSpawnWeight",
         "spawns",
         AMConfig.manedWolfSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.manedWolfSpawnRolls = buildInt(
         builder,
         "manedWolfSpawnRolls",
         "spawns",
         AMConfig.manedWolfSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.anacondaSpawnWeight = buildInt(
         builder,
         "anacondaSpawnWeight",
         "spawns",
         AMConfig.anacondaSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.anacondaSpawnRolls = buildInt(
         builder,
         "anacondaSpawnRolls",
         "spawns",
         AMConfig.anacondaSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.anteaterSpawnWeight = buildInt(
         builder,
         "anteaterSpawnWeight",
         "spawns",
         AMConfig.anteaterSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.anteaterSpawnRolls = buildInt(
         builder,
         "anteaterSpawnRolls",
         "spawns",
         AMConfig.anteaterSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.rockyRollerSpawnWeight = buildInt(
         builder,
         "rockyRollerSpawnWeight",
         "spawns",
         AMConfig.rockyRollerSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.rockyRollerSpawnRolls = buildInt(
         builder,
         "rockyRollerSpawnRolls",
         "spawns",
         AMConfig.rockyRollerSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.flutterSpawnWeight = buildInt(
         builder,
         "flutterSpawnWeight",
         "spawns",
         AMConfig.flutterSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.flutterSpawnRolls = buildInt(
         builder,
         "flutterSpawnRolls",
         "spawns",
         AMConfig.flutterSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.geladaMonkeySpawnWeight = buildInt(
         builder,
         "geladaMonkeySpawnWeight",
         "spawns",
         AMConfig.geladaMonkeySpawnWeight,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.geladaMonkeySpawnRolls = buildInt(
         builder,
         "geladaMonkeySpawnRolls",
         "spawns",
         AMConfig.geladaMonkeySpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.jerboaSpawnWeight = buildInt(
         builder,
         "jerboaSpawnWeight",
         "spawns",
         AMConfig.jerboaSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.jerboaSpawnRolls = buildInt(
         builder,
         "jerboaSpawnRolls",
         "spawns",
         AMConfig.jerboaSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.terrapinSpawnWeight = buildInt(
         builder,
         "terrapinSpawnWeight",
         "spawns",
         AMConfig.terrapinSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.terrapinSpawnRolls = buildInt(
         builder,
         "terrapinSpawnRolls",
         "spawns",
         AMConfig.terrapinSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.combJellySpawnWeight = buildInt(
         builder,
         "combJellySpawnWeight",
         "spawns",
         AMConfig.combJellySpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.combJellySpawnRolls = buildInt(
         builder,
         "combJellySpawnRolls",
         "spawns",
         AMConfig.combJellySpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.cosmicCodSpawnWeight = buildInt(
         builder,
         "cosmicCodSpawnWeight",
         "spawns",
         AMConfig.cosmicCodSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.cosmicCodSpawnRolls = buildInt(
         builder,
         "cosmicCodSpawnRolls",
         "spawns",
         AMConfig.cosmicCodSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.bunfungusSpawnWeight = buildInt(
         builder,
         "bunfungusSpawnWeight",
         "spawns",
         AMConfig.bunfungusSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.bunfungusSpawnRolls = buildInt(
         builder,
         "bunfungusSpawnRolls",
         "spawns",
         AMConfig.bunfungusSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.bisonSpawnWeight = buildInt(
         builder,
         "bisonSpawnWeight",
         "spawns",
         AMConfig.bisonSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.bisonSpawnRolls = buildInt(
         builder,
         "bisonSpawnRolls",
         "spawns",
         AMConfig.bisonSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.giantSquidSpawnWeight = buildInt(
         builder,
         "giantSquidSpawnWeight",
         "spawns",
         AMConfig.giantSquidSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.giantSquidSpawnRolls = buildInt(
         builder,
         "giantSquidSpawnRolls",
         "spawns",
         AMConfig.giantSquidSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.devilsHolePupfishSpawnWeight = buildInt(
         builder,
         "devilsHolePupfishSpawnWeight",
         "spawns",
         AMConfig.devilsHolePupfishSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn (NOTE: this mob spawns are restricted exclusively to one chunk, see below)"
      );
      this.devilsHolePupfishSpawnRolls = buildInt(
         builder,
         "devilsHolePupfishSpawnRolls",
         "spawns",
         AMConfig.devilsHolePupfishSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning (NOTE: this mob spawns are restricted exclusively to one chunk, see below)"
      );
      this.catfishSpawnWeight = buildInt(
         builder,
         "catfishSpawnWeight",
         "spawns",
         AMConfig.catfishSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.catfishSpawnRolls = buildInt(
         builder,
         "catfishSpawnRolls",
         "spawns",
         AMConfig.catfishSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.flyingFishSpawnWeight = buildInt(
         builder,
         "flyingFishSpawnWeight",
         "spawns",
         AMConfig.flyingFishSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.flyingFishSpawnRolls = buildInt(
         builder,
         "flyingFishSpawnRolls",
         "spawns",
         AMConfig.flyingFishSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.skelewagSpawnWeight = buildInt(
         builder,
         "skelewagSpawnWeight",
         "spawns",
         AMConfig.skelewagSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.skelewagSpawnRolls = buildInt(
         builder,
         "skelewagSpawnRolls",
         "spawns",
         AMConfig.skelewagSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.rainFrogSpawnWeight = buildInt(
         builder,
         "rainFrogSpawnWeight",
         "spawns",
         AMConfig.rainFrogSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.rainFrogSpawnRolls = buildInt(
         builder,
         "rainFrogSpawnRolls",
         "spawns",
         AMConfig.rainFrogSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.potooSpawnWeight = buildInt(
         builder,
         "potooSpawnWeight",
         "spawns",
         AMConfig.potooSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.potooSpawnRolls = buildInt(
         builder,
         "potooSpawnRolls",
         "spawns",
         AMConfig.potooSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.mudskipperSpawnWeight = buildInt(
         builder,
         "mudskipperSpawnWeight",
         "spawns",
         AMConfig.mudskipperSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.mudskipperSpawnRolls = buildInt(
         builder,
         "mudskipperSpawnRolls",
         "spawns",
         AMConfig.mudskipperSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.rhinocerosSpawnWeight = buildInt(
         builder,
         "rhinocerosSpawnWeight",
         "spawns",
         AMConfig.rhinocerosSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.rhinocerosSpawnRolls = buildInt(
         builder,
         "rhinocerosSpawnRolls",
         "spawns",
         AMConfig.rhinocerosSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.sugarGliderSpawnWeight = buildInt(
         builder,
         "sugarGliderSpawnWeight",
         "spawns",
         AMConfig.sugarGliderSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.sugarGliderSpawnRolls = buildInt(
         builder,
         "sugarGliderSpawnRolls",
         "spawns",
         AMConfig.sugarGliderSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.farseerSpawnWeight = buildInt(
         builder,
         "farseerSpawnWeight",
         "spawns",
         AMConfig.farseerSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.farseerSpawnRolls = buildInt(
         builder,
         "farseerSpawnRolls",
         "spawns",
         AMConfig.farseerSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.skreecherSpawnWeight = buildInt(
         builder,
         "skreecherSpawnWeight",
         "spawns",
         AMConfig.skreecherSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.skreecherSpawnRolls = buildInt(
         builder,
         "skreecherSpawnRolls",
         "spawns",
         AMConfig.skreecherSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.underminerSpawnWeight = buildInt(
         builder,
         "underminerSpawnWeight",
         "spawns",
         AMConfig.underminerSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.underminerSpawnRolls = buildInt(
         builder,
         "underminerSpawnRolls",
         "spawns",
         AMConfig.underminerSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.murmurSpawnWeight = buildInt(
         builder,
         "murmurSpawnWeight",
         "spawns",
         AMConfig.murmurSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.murmurSpawnRolls = buildInt(
         builder,
         "murmurSpawnRolls",
         "spawns",
         AMConfig.murmurSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.skunkSpawnWeight = buildInt(
         builder,
         "skunkSpawnWeight",
         "spawns",
         AMConfig.skunkSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.skunkSpawnRolls = buildInt(
         builder,
         "skunkSpawnRolls",
         "spawns",
         AMConfig.skunkSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.bananaSlugSpawnWeight = buildInt(
         builder,
         "bananaSlugSpawnWeight",
         "spawns",
         AMConfig.bananaSlugSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.bananaSlugSpawnRolls = buildInt(
         builder,
         "bananaSlugSpawnRolls",
         "spawns",
         AMConfig.bananaSlugSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.blueJaySpawnWeight = buildInt(
         builder,
         "blueJaySpawnWeight",
         "spawns",
         AMConfig.blueJaySpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.blueJaySpawnRolls = buildInt(
         builder,
         "blueJaySpawnRolls",
         "spawns",
         AMConfig.blueJaySpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.caimanSpawnWeight = buildInt(
         builder,
         "caimanSpawnWeight",
         "spawns",
         AMConfig.caimanSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.caimanSpawnRolls = buildInt(
         builder,
         "caimanSpawnRolls",
         "spawns",
         AMConfig.caimanSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      this.triopsSpawnWeight = buildInt(
         builder,
         "triopsSpawnWeight",
         "spawns",
         AMConfig.triopsSpawnWeight,
         0,
         1000,
         "Spawn Weight, added to a pool of other mobs for each biome. Higher number = higher chance of spawning. 0 = disable spawn"
      );
      this.triopsSpawnRolls = buildInt(
         builder,
         "triopsSpawnRolls",
         "spawns",
         AMConfig.triopsSpawnRolls,
         0,
         Integer.MAX_VALUE,
         "Random roll chance to enable mob spawning. Higher number = lower chance of spawning"
      );
      builder.pop();
      builder.push("uniqueSpawning");
      this.caveCentipedeSpawnHeight = buildInt(
         builder, "caveCentipedeSpawnHeight", "all", AMConfig.caveCentipedeSpawnHeight, -64, 320, "Maximum world y-level that cave centipedes can spawn at"
      );
      this.blobfishSpawnHeight = buildInt(
         builder, "blobfishSpawnHeight", "all", AMConfig.blobfishSpawnHeight, -64, 320, "Maximum world y-level that blobfish can spawn at"
      );
      this.beachedCachalotWhales = buildBoolean(
         builder, "beachedCachalotWhales", "uniqueSpawning", true, "Whether to enable beached cachalot whales to spawn on beaches during thunder storms."
      );
      this.beachedCachalotWhaleSpawnChance = buildInt(
         builder,
         "beachedCachalotWhaleSpawnChance",
         "uniqueSpawning",
         AMConfig.beachedCachalotWhaleSpawnChance,
         0,
         100,
         "Percent chance increase for each failed attempt to spawn a beached cachalot whale. Higher value = more spawns."
      );
      this.beachedCachalotWhaleSpawnDelay = buildInt(
         builder,
         "beachedCachalotWhaleSpawnDelay",
         "uniqueSpawning",
         AMConfig.beachedCachalotWhaleSpawnDelay,
         0,
         Integer.MAX_VALUE,
         "Delay (in ticks) between attempts to spawn beached cachalot whales. Default is a single day. Works like wandering traders."
      );
      this.leafcutterAnthillSpawnChance = buildDouble(
         builder,
         "leafcutterAnthillSpawnChance",
         "uniqueSpawning",
         AMConfig.leafcutterAnthillSpawnChance,
         0.0,
         1.0,
         "Percent chance for leafcutter anthills to spawn as world gen in each chunk. Set to zero to disable spawning."
      );
      this.geladaMonkeySpawnHeight = buildInt(
         builder, "geladaMonkeySpawnRolls", "spawns", AMConfig.geladaMonkeySpawnRolls, -64, 320, "Minimum world y-level that gelada monkeys can spawn at"
      );
      this.restrictPupfishSpawns = buildBoolean(
         builder,
         "restrictPupfishSpawns",
         "uniqueSpawning",
         true,
         "Whether to restrict all pupfish spawns to one chunk (similar to real life) or have them only obey their spawn config."
      );
      this.pupfishChunkSpawnDistance = buildInt(
         builder,
         "pupfishChunkSpawnDistance",
         "uniqueSpawning",
         AMConfig.pupfishChunkSpawnDistance,
         2,
         1000000000,
         "The maximum distance a pupfish spawn chunk is from world spawn(0, 0) in blocks."
      );
      this.restrictSkelewagSpawns = buildBoolean(
         builder, "restrictSkelewagSpawns", "uniqueSpawning", true, "Whether to restrict all skelewag spawns to shipwreck structures."
      );
      this.restrictFarseerSpawns = buildBoolean(
         builder, "restrictFarseerSpawns", "uniqueSpawning", true, "Whether to restrict all farseer spawns to near the world border."
      );
      this.restrictUnderminerSpawns = buildBoolean(
         builder, "restrictUnderminerSpawns", "uniqueSpawning", true, "Whether to restrict all underminer spawns to abandoned mineshafts."
      );
      this.farseerBorderSpawnDistance = buildInt(
         builder,
         "farseerBorderSpawnDistance",
         "uniqueSpawning",
         AMConfig.farseerBorderSpawnDistance,
         2,
         1000000000,
         "The maximum distance a farseer can spawn from the world border."
      );
      this.murmurSpawnHeight = buildInt(
         builder, "murmurSpawnHeight", "all", AMConfig.murmurSpawnHeight, -64, 320, "Maximum world y-level that murmur can spawn at"
      );
      builder.pop();
      builder.push("dangerZone");
      this.superSecretSettings = buildBoolean(builder, "superSecretSettings", "dangerZone", false, "Its been so long...");
      this.pathfindingThreads = buildInt(
         builder,
         "pathfindingThreads",
         "dangerZone",
         AMConfig.pathfindingThreads,
         1,
         100,
         "How many cpu cores some mobs(elephants, leafcutter ants, bison etc) should utilize when pathing. Bigger number = less impact on TPS"
      );
      builder.pop();
   }

   private static BooleanValue buildBoolean(Builder builder, String name, String catagory, boolean defaultValue, String comment) {
      return builder.comment(comment).translation(name).define(name, defaultValue);
   }

   private static IntValue buildInt(Builder builder, String name, String catagory, int defaultValue, int min, int max, String comment) {
      return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
   }

   private static DoubleValue buildDouble(Builder builder, String name, String catagory, double defaultValue, double min, double max, String comment) {
      return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
   }
}
