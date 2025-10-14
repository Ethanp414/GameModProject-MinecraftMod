package net.minecraft.data.loot;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SheepPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

public abstract class EntityLootSubProvider implements LootTableSubProvider {
   protected final HolderLookup.Provider registries;
   private final FeatureFlagSet allowed;
   private final FeatureFlagSet required;
   private final Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> map = Maps.newHashMap();

   protected final AnyOfCondition.Builder shouldSmeltLoot() {
      HolderLookup.RegistryLookup<Enchantment> $$0 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return AnyOfCondition.anyOf(
         LootItemEntityPropertyCondition.hasProperties(
            LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))
         ),
         LootItemEntityPropertyCondition.hasProperties(
            LootContext.EntityTarget.DIRECT_ATTACKER,
            EntityPredicate.Builder.entity()
               .equipment(
                  EntityEquipmentPredicate.Builder.equipment()
                     .mainhand(
                        ItemPredicate.Builder.item()
                           .withComponents(
                              DataComponentMatchers.Builder.components()
                                 .partial(
                                    DataComponentPredicates.ENCHANTMENTS,
                                    EnchantmentsPredicate.enchantments(
                                       List.of(new EnchantmentPredicate($$0.getOrThrow(EnchantmentTags.SMELTS_LOOT), MinMaxBounds.Ints.ANY))
                                    )
                                 )
                                 .build()
                           )
                     )
               )
         )
      );
   }

   protected EntityLootSubProvider(FeatureFlagSet $$0, HolderLookup.Provider $$1) {
      this($$0, $$0, $$1);
   }

   protected EntityLootSubProvider(FeatureFlagSet $$0, FeatureFlagSet $$1, HolderLookup.Provider $$2) {
      this.allowed = $$0;
      this.required = $$1;
      this.registries = $$2;
   }

   public static LootPool.Builder createSheepDispatchPool(Map<DyeColor, ResourceKey<LootTable>> $$0) {
      AlternativesEntry.Builder $$1 = AlternativesEntry.alternatives();

      for(Entry<DyeColor, ResourceKey<LootTable>> $$2 : $$0.entrySet()) {
         $$1 = $$1.otherwise(
            NestedLootTable.lootTableReference((ResourceKey<LootTable>)$$2.getValue())
               .when(
                  LootItemEntityPropertyCondition.hasProperties(
                     LootContext.EntityTarget.THIS,
                     EntityPredicate.Builder.entity()
                        .components(
                           DataComponentMatchers.Builder.components()
                              .exact(DataComponentExactPredicate.expect(DataComponents.SHEEP_COLOR, (DyeColor)$$2.getKey()))
                              .build()
                        )
                        .subPredicate(SheepPredicate.hasWool())
                  )
               )
         );
      }

      return LootPool.lootPool().add($$1);
   }

   public abstract void generate();

   @Override
   public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> $$0) {
      this.generate();
      Set<ResourceKey<LootTable>> $$1 = new HashSet();
      BuiltInRegistries.ENTITY_TYPE
         .listElements()
         .forEach(
            $$2 -> {
               EntityType<?> $$3 = (EntityType)$$2.value();
               if ($$3.isEnabled(this.allowed)) {
                  Optional<ResourceKey<LootTable>> $$4 = $$3.getDefaultLootTable();
                  if ($$4.isPresent()) {
                     Map<ResourceKey<LootTable>, LootTable.Builder> $$5 = (Map)this.map.remove($$3);
                     if ($$3.isEnabled(this.required) && ($$5 == null || !$$5.containsKey($$4.get()))) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", $$4.get(), $$2.key().location()));
                     }
      
                     if ($$5 != null) {
                        $$5.forEach(($$3x, $$4x) -> {
                           if (!$$1.add($$3x)) {
                              throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", $$3x, $$2.key().location()));
                           } else {
                              $$0.accept($$3x, $$4x);
                           }
                        });
                     }
                  } else {
                     Map<ResourceKey<LootTable>, LootTable.Builder> $$6 = (Map)this.map.remove($$3);
                     if ($$6 != null) {
                        throw new IllegalStateException(
                           String.format(
                              Locale.ROOT,
                              "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
                              $$6.keySet().stream().map($$0xx -> $$0xx.location().toString()).collect(Collectors.joining(",")),
                              $$2.key().location()
                           )
                        );
                     }
                  }
               }
            }
         );
      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
      }
   }

   protected LootItemCondition.Builder killedByFrog(HolderGetter<EntityType<?>> $$0) {
      return DamageSourceCondition.hasDamageSource(
         DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of($$0, EntityType.FROG))
      );
   }

   protected LootItemCondition.Builder killedByFrogVariant(HolderGetter<EntityType<?>> $$0, HolderGetter<FrogVariant> $$1, ResourceKey<FrogVariant> $$2) {
      return DamageSourceCondition.hasDamageSource(
         DamageSourcePredicate.Builder.damageType()
            .source(
               EntityPredicate.Builder.entity()
                  .of($$0, EntityType.FROG)
                  .components(
                     DataComponentMatchers.Builder.components()
                        .exact(DataComponentExactPredicate.expect(DataComponents.FROG_VARIANT, $$1.getOrThrow($$2)))
                        .build()
                  )
            )
      );
   }

   protected void add(EntityType<?> $$0, LootTable.Builder $$1) {
      this.add(
         $$0, (ResourceKey<LootTable>)$$0.getDefaultLootTable().orElseThrow(() -> new IllegalStateException("Entity " + $$0 + " has no loot table")), $$1
      );
   }

   protected void add(EntityType<?> $$0, ResourceKey<LootTable> $$1, LootTable.Builder $$2) {
      ((Map)this.map.computeIfAbsent($$0, $$0x -> new HashMap())).put($$1, $$2);
   }
}
