package net.minecraft.data.loot.packs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public record VanillaChargedCreeperExplosionLoot(HolderLookup.Provider registries) implements LootTableSubProvider {
   private static final List<VanillaChargedCreeperExplosionLoot.Entry> ENTRIES = List.of(
      new VanillaChargedCreeperExplosionLoot.Entry(BuiltInLootTables.CHARGED_CREEPER_PIGLIN, EntityType.PIGLIN, Items.PIGLIN_HEAD),
      new VanillaChargedCreeperExplosionLoot.Entry(BuiltInLootTables.CHARGED_CREEPER_CREEPER, EntityType.CREEPER, Items.CREEPER_HEAD),
      new VanillaChargedCreeperExplosionLoot.Entry(BuiltInLootTables.CHARGED_CREEPER_SKELETON, EntityType.SKELETON, Items.SKELETON_SKULL),
      new VanillaChargedCreeperExplosionLoot.Entry(BuiltInLootTables.CHARGED_CREEPER_WITHER_SKELETON, EntityType.WITHER_SKELETON, Items.WITHER_SKELETON_SKULL),
      new VanillaChargedCreeperExplosionLoot.Entry(BuiltInLootTables.CHARGED_CREEPER_ZOMBIE, EntityType.ZOMBIE, Items.ZOMBIE_HEAD)
   );

   @Override
   public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> $$0) {
      HolderGetter<EntityType<?>> $$1 = this.registries.lookupOrThrow(Registries.ENTITY_TYPE);
      List<LootPoolEntryContainer.Builder<?>> $$2 = new ArrayList(ENTRIES.size());

      for(VanillaChargedCreeperExplosionLoot.Entry $$3 : ENTRIES) {
         $$0.accept(
            $$3.lootTable, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem($$3.item)))
         );
         LootItemCondition.Builder $$4 = LootItemEntityPropertyCondition.hasProperties(
            LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of($$1, $$3.entityType))
         );
         $$2.add(NestedLootTable.lootTableReference($$3.lootTable).when($$4));
      }

      $$0.accept(
         BuiltInLootTables.CHARGED_CREEPER,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(AlternativesEntry.alternatives((LootPoolEntryContainer.Builder<?>[])$$2.toArray($$0x -> new LootPoolEntryContainer.Builder[$$0x])))
            )
      );
   }

   static record Entry(ResourceKey<LootTable> lootTable, EntityType<?> entityType, Item item) {
      final ResourceKey<LootTable> lootTable;
      final EntityType<?> entityType;
      final Item item;
   }
}
