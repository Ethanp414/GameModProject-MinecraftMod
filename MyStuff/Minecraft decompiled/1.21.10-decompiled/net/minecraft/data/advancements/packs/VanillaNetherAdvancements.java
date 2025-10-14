package net.minecraft.data.advancements.packs;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.BrewedPotionTrigger;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.DistanceTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemDurabilityTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LootTableTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.PickedUpItemTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

public class VanillaNetherAdvancements implements AdvancementSubProvider {
   @Override
   public void generate(HolderLookup.Provider $$0, Consumer<AdvancementHolder> $$1) {
      HolderGetter<EntityType<?>> $$2 = $$0.lookupOrThrow(Registries.ENTITY_TYPE);
      HolderGetter<Item> $$3 = $$0.lookupOrThrow(Registries.ITEM);
      HolderGetter<Block> $$4 = $$0.lookupOrThrow(Registries.BLOCK);
      AdvancementHolder $$5 = Advancement.Builder.advancement()
         .display(
            Blocks.RED_NETHER_BRICKS,
            Component.translatable("advancements.nether.root.title"),
            Component.translatable("advancements.nether.root.description"),
            ResourceLocation.withDefaultNamespace("gui/advancements/backgrounds/nether"),
            AdvancementType.TASK,
            false,
            false,
            false
         )
         .addCriterion("entered_nether", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER))
         .save($$1, "nether/root");
      AdvancementHolder $$6 = Advancement.Builder.advancement()
         .parent($$5)
         .display(
            Items.FIRE_CHARGE,
            Component.translatable("advancements.nether.return_to_sender.title"),
            Component.translatable("advancements.nether.return_to_sender.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(50))
         .addCriterion(
            "killed_ghast",
            KilledTrigger.TriggerInstance.playerKilledEntity(
               EntityPredicate.Builder.entity().of($$2, EntityType.GHAST),
               DamageSourcePredicate.Builder.damageType()
                  .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                  .direct(EntityPredicate.Builder.entity().of($$2, EntityType.FIREBALL))
            )
         )
         .save($$1, "nether/return_to_sender");
      AdvancementHolder $$7 = Advancement.Builder.advancement()
         .parent($$5)
         .display(
            Blocks.NETHER_BRICKS,
            Component.translatable("advancements.nether.find_fortress.title"),
            Component.translatable("advancements.nether.find_fortress.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "fortress",
            PlayerTrigger.TriggerInstance.located(
               LocationPredicate.Builder.inStructure($$0.<Structure>lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.FORTRESS))
            )
         )
         .save($$1, "nether/find_fortress");
      Advancement.Builder.advancement()
         .parent($$5)
         .display(
            Items.MAP,
            Component.translatable("advancements.nether.fast_travel.title"),
            Component.translatable("advancements.nether.fast_travel.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(100))
         .addCriterion("travelled", DistanceTrigger.TriggerInstance.travelledThroughNether(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(7000.0))))
         .save($$1, "nether/fast_travel");
      Advancement.Builder.advancement()
         .parent($$6)
         .display(
            Items.GHAST_TEAR,
            Component.translatable("advancements.nether.uneasy_alliance.title"),
            Component.translatable("advancements.nether.uneasy_alliance.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(100))
         .addCriterion(
            "killed_ghast",
            KilledTrigger.TriggerInstance.playerKilledEntity(
               EntityPredicate.Builder.entity().of($$2, EntityType.GHAST).located(LocationPredicate.Builder.inDimension(Level.OVERWORLD))
            )
         )
         .save($$1, "nether/uneasy_alliance");
      AdvancementHolder $$8 = Advancement.Builder.advancement()
         .parent($$7)
         .display(
            Blocks.WITHER_SKELETON_SKULL,
            Component.translatable("advancements.nether.get_wither_skull.title"),
            Component.translatable("advancements.nether.get_wither_skull.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("wither_skull", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.WITHER_SKELETON_SKULL))
         .save($$1, "nether/get_wither_skull");
      AdvancementHolder $$9 = Advancement.Builder.advancement()
         .parent($$8)
         .display(
            Items.NETHER_STAR,
            Component.translatable("advancements.nether.summon_wither.title"),
            Component.translatable("advancements.nether.summon_wither.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("summoned", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of($$2, EntityType.WITHER)))
         .save($$1, "nether/summon_wither");
      AdvancementHolder $$10 = Advancement.Builder.advancement()
         .parent($$7)
         .display(
            Items.BLAZE_ROD,
            Component.translatable("advancements.nether.obtain_blaze_rod.title"),
            Component.translatable("advancements.nether.obtain_blaze_rod.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("blaze_rod", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BLAZE_ROD))
         .save($$1, "nether/obtain_blaze_rod");
      AdvancementHolder $$11 = Advancement.Builder.advancement()
         .parent($$9)
         .display(
            Blocks.BEACON,
            Component.translatable("advancements.nether.create_beacon.title"),
            Component.translatable("advancements.nether.create_beacon.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("beacon", ConstructBeaconTrigger.TriggerInstance.constructedBeacon(MinMaxBounds.Ints.atLeast(1)))
         .save($$1, "nether/create_beacon");
      Advancement.Builder.advancement()
         .parent($$11)
         .display(
            Blocks.BEACON,
            Component.translatable("advancements.nether.create_full_beacon.title"),
            Component.translatable("advancements.nether.create_full_beacon.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
         )
         .addCriterion("beacon", ConstructBeaconTrigger.TriggerInstance.constructedBeacon(MinMaxBounds.Ints.exactly(4)))
         .save($$1, "nether/create_full_beacon");
      AdvancementHolder $$12 = Advancement.Builder.advancement()
         .parent($$10)
         .display(
            Items.POTION,
            Component.translatable("advancements.nether.brew_potion.title"),
            Component.translatable("advancements.nether.brew_potion.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("potion", BrewedPotionTrigger.TriggerInstance.brewedPotion())
         .save($$1, "nether/brew_potion");
      AdvancementHolder $$13 = Advancement.Builder.advancement()
         .parent($$12)
         .display(
            Items.MILK_BUCKET,
            Component.translatable("advancements.nether.all_potions.title"),
            Component.translatable("advancements.nether.all_potions.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(100))
         .addCriterion(
            "all_effects",
            EffectsChangedTrigger.TriggerInstance.hasEffects(
               MobEffectsPredicate.Builder.effects()
                  .and(MobEffects.SPEED)
                  .and(MobEffects.SLOWNESS)
                  .and(MobEffects.STRENGTH)
                  .and(MobEffects.JUMP_BOOST)
                  .and(MobEffects.REGENERATION)
                  .and(MobEffects.FIRE_RESISTANCE)
                  .and(MobEffects.WATER_BREATHING)
                  .and(MobEffects.INVISIBILITY)
                  .and(MobEffects.NIGHT_VISION)
                  .and(MobEffects.WEAKNESS)
                  .and(MobEffects.POISON)
                  .and(MobEffects.SLOW_FALLING)
                  .and(MobEffects.RESISTANCE)
                  .and(MobEffects.OOZING)
                  .and(MobEffects.INFESTED)
                  .and(MobEffects.WIND_CHARGED)
                  .and(MobEffects.WEAVING)
            )
         )
         .save($$1, "nether/all_potions");
      Advancement.Builder.advancement()
         .parent($$13)
         .display(
            Items.BUCKET,
            Component.translatable("advancements.nether.all_effects.title"),
            Component.translatable("advancements.nether.all_effects.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            true
         )
         .rewards(AdvancementRewards.Builder.experience(1000))
         .addCriterion(
            "all_effects",
            EffectsChangedTrigger.TriggerInstance.hasEffects(
               MobEffectsPredicate.Builder.effects()
                  .and(MobEffects.SPEED)
                  .and(MobEffects.SLOWNESS)
                  .and(MobEffects.STRENGTH)
                  .and(MobEffects.JUMP_BOOST)
                  .and(MobEffects.REGENERATION)
                  .and(MobEffects.FIRE_RESISTANCE)
                  .and(MobEffects.WATER_BREATHING)
                  .and(MobEffects.INVISIBILITY)
                  .and(MobEffects.NIGHT_VISION)
                  .and(MobEffects.WEAKNESS)
                  .and(MobEffects.POISON)
                  .and(MobEffects.WITHER)
                  .and(MobEffects.HASTE)
                  .and(MobEffects.MINING_FATIGUE)
                  .and(MobEffects.LEVITATION)
                  .and(MobEffects.GLOWING)
                  .and(MobEffects.ABSORPTION)
                  .and(MobEffects.HUNGER)
                  .and(MobEffects.NAUSEA)
                  .and(MobEffects.RESISTANCE)
                  .and(MobEffects.SLOW_FALLING)
                  .and(MobEffects.CONDUIT_POWER)
                  .and(MobEffects.DOLPHINS_GRACE)
                  .and(MobEffects.BLINDNESS)
                  .and(MobEffects.BAD_OMEN)
                  .and(MobEffects.HERO_OF_THE_VILLAGE)
                  .and(MobEffects.DARKNESS)
                  .and(MobEffects.OOZING)
                  .and(MobEffects.INFESTED)
                  .and(MobEffects.WIND_CHARGED)
                  .and(MobEffects.WEAVING)
                  .and(MobEffects.TRIAL_OMEN)
                  .and(MobEffects.RAID_OMEN)
            )
         )
         .save($$1, "nether/all_effects");
      AdvancementHolder $$14 = Advancement.Builder.advancement()
         .parent($$5)
         .display(
            Items.ANCIENT_DEBRIS,
            Component.translatable("advancements.nether.obtain_ancient_debris.title"),
            Component.translatable("advancements.nether.obtain_ancient_debris.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("ancient_debris", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ANCIENT_DEBRIS))
         .save($$1, "nether/obtain_ancient_debris");
      Advancement.Builder.advancement()
         .parent($$14)
         .display(
            Items.NETHERITE_CHESTPLATE,
            Component.translatable("advancements.nether.netherite_armor.title"),
            Component.translatable("advancements.nether.netherite_armor.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(100))
         .addCriterion(
            "netherite_armor",
            InventoryChangeTrigger.TriggerInstance.hasItems(
               Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS
            )
         )
         .save($$1, "nether/netherite_armor");
      AdvancementHolder $$15 = Advancement.Builder.advancement()
         .parent($$5)
         .display(
            Items.CRYING_OBSIDIAN,
            Component.translatable("advancements.nether.obtain_crying_obsidian.title"),
            Component.translatable("advancements.nether.obtain_crying_obsidian.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("crying_obsidian", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CRYING_OBSIDIAN))
         .save($$1, "nether/obtain_crying_obsidian");
      Advancement.Builder.advancement()
         .parent($$15)
         .display(
            Items.RESPAWN_ANCHOR,
            Component.translatable("advancements.nether.charge_respawn_anchor.title"),
            Component.translatable("advancements.nether.charge_respawn_anchor.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "charge_respawn_anchor",
            ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
               LocationPredicate.Builder.location()
                  .setBlock(
                     BlockPredicate.Builder.block()
                        .of($$4, Blocks.RESPAWN_ANCHOR)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RespawnAnchorBlock.CHARGE, 4))
                  ),
               ItemPredicate.Builder.item().of($$3, Blocks.GLOWSTONE)
            )
         )
         .save($$1, "nether/charge_respawn_anchor");
      AdvancementHolder $$16 = Advancement.Builder.advancement()
         .parent($$5)
         .display(
            Items.WARPED_FUNGUS_ON_A_STICK,
            Component.translatable("advancements.nether.ride_strider.title"),
            Component.translatable("advancements.nether.ride_strider.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "used_warped_fungus_on_a_stick",
            ItemDurabilityTrigger.TriggerInstance.changedDurability(
               Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity().of($$2, EntityType.STRIDER)))),
               Optional.of(ItemPredicate.Builder.item().of($$3, Items.WARPED_FUNGUS_ON_A_STICK).build()),
               MinMaxBounds.Ints.ANY
            )
         )
         .save($$1, "nether/ride_strider");
      Advancement.Builder.advancement()
         .parent($$16)
         .display(
            Items.WARPED_FUNGUS_ON_A_STICK,
            Component.translatable("advancements.nether.ride_strider_in_overworld_lava.title"),
            Component.translatable("advancements.nether.ride_strider_in_overworld_lava.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "ride_entity_distance",
            DistanceTrigger.TriggerInstance.rideEntityInLava(
               EntityPredicate.Builder.entity()
                  .located(LocationPredicate.Builder.inDimension(Level.OVERWORLD))
                  .vehicle(EntityPredicate.Builder.entity().of($$2, EntityType.STRIDER)),
               DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(50.0))
            )
         )
         .save($$1, "nether/ride_strider_in_overworld_lava");
      VanillaAdventureAdvancements.addBiomes(Advancement.Builder.advancement(), $$0, MultiNoiseBiomeSourceParameterList.Preset.NETHER.usedBiomes().toList())
         .parent($$16)
         .display(
            Items.NETHERITE_BOOTS,
            Component.translatable("advancements.nether.explore_nether.title"),
            Component.translatable("advancements.nether.explore_nether.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(500))
         .save($$1, "nether/explore_nether");
      AdvancementHolder $$17 = Advancement.Builder.advancement()
         .parent($$5)
         .display(
            Items.POLISHED_BLACKSTONE_BRICKS,
            Component.translatable("advancements.nether.find_bastion.title"),
            Component.translatable("advancements.nether.find_bastion.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "bastion",
            PlayerTrigger.TriggerInstance.located(
               LocationPredicate.Builder.inStructure($$0.<Structure>lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.BASTION_REMNANT))
            )
         )
         .save($$1, "nether/find_bastion");
      Advancement.Builder.advancement()
         .parent($$17)
         .display(
            Blocks.CHEST,
            Component.translatable("advancements.nether.loot_bastion.title"),
            Component.translatable("advancements.nether.loot_bastion.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .requirements(AdvancementRequirements.Strategy.OR)
         .addCriterion("loot_bastion_other", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.BASTION_OTHER))
         .addCriterion("loot_bastion_treasure", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.BASTION_TREASURE))
         .addCriterion("loot_bastion_hoglin_stable", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.BASTION_HOGLIN_STABLE))
         .addCriterion("loot_bastion_bridge", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.BASTION_BRIDGE))
         .save($$1, "nether/loot_bastion");
      ContextAwarePredicate $$18 = ContextAwarePredicate.create(
         LootItemEntityPropertyCondition.hasProperties(
               LootContext.EntityTarget.THIS,
               EntityPredicate.Builder.entity()
                  .equipment(EntityEquipmentPredicate.Builder.equipment().head(ItemPredicate.Builder.item().of($$3, ItemTags.PIGLIN_SAFE_ARMOR)))
            )
            .invert()
            .build(),
         LootItemEntityPropertyCondition.hasProperties(
               LootContext.EntityTarget.THIS,
               EntityPredicate.Builder.entity()
                  .equipment(EntityEquipmentPredicate.Builder.equipment().chest(ItemPredicate.Builder.item().of($$3, ItemTags.PIGLIN_SAFE_ARMOR)))
            )
            .invert()
            .build(),
         LootItemEntityPropertyCondition.hasProperties(
               LootContext.EntityTarget.THIS,
               EntityPredicate.Builder.entity()
                  .equipment(EntityEquipmentPredicate.Builder.equipment().legs(ItemPredicate.Builder.item().of($$3, ItemTags.PIGLIN_SAFE_ARMOR)))
            )
            .invert()
            .build(),
         LootItemEntityPropertyCondition.hasProperties(
               LootContext.EntityTarget.THIS,
               EntityPredicate.Builder.entity()
                  .equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of($$3, ItemTags.PIGLIN_SAFE_ARMOR)))
            )
            .invert()
            .build()
      );
      Advancement.Builder.advancement()
         .parent($$5)
         .requirements(AdvancementRequirements.Strategy.OR)
         .display(
            Items.GOLD_INGOT,
            Component.translatable("advancements.nether.distract_piglin.title"),
            Component.translatable("advancements.nether.distract_piglin.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "distract_piglin",
            PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByEntity(
               $$18,
               Optional.of(ItemPredicate.Builder.item().of($$3, ItemTags.PIGLIN_LOVED).build()),
               Optional.of(
                  EntityPredicate.wrap(
                     EntityPredicate.Builder.entity().of($$2, EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false))
                  )
               )
            )
         )
         .addCriterion(
            "distract_piglin_directly",
            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
               Optional.of($$18),
               ItemPredicate.Builder.item().of($$3, PiglinAi.BARTERING_ITEM),
               Optional.of(
                  EntityPredicate.wrap(
                     EntityPredicate.Builder.entity().of($$2, EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false))
                  )
               )
            )
         )
         .save($$1, "nether/distract_piglin");
   }
}
