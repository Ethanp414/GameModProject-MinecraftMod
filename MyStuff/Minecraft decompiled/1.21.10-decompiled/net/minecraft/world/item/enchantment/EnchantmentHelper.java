package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableObject;

public class EnchantmentHelper {
   public static int getItemEnchantmentLevel(Holder<Enchantment> $$0, ItemStack $$1) {
      ItemEnchantments $$2 = $$1.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      return $$2.getLevel($$0);
   }

   public static ItemEnchantments updateEnchantments(ItemStack $$0, Consumer<ItemEnchantments.Mutable> $$1) {
      DataComponentType<ItemEnchantments> $$2 = getComponentType($$0);
      ItemEnchantments $$3 = $$0.get($$2);
      if ($$3 == null) {
         return ItemEnchantments.EMPTY;
      } else {
         ItemEnchantments.Mutable $$4 = new ItemEnchantments.Mutable($$3);
         $$1.accept($$4);
         ItemEnchantments $$5 = $$4.toImmutable();
         $$0.set($$2, $$5);
         return $$5;
      }
   }

   public static boolean canStoreEnchantments(ItemStack $$0) {
      return $$0.has(getComponentType($$0));
   }

   public static void setEnchantments(ItemStack $$0, ItemEnchantments $$1) {
      $$0.set(getComponentType($$0), $$1);
   }

   public static ItemEnchantments getEnchantmentsForCrafting(ItemStack $$0) {
      return $$0.getOrDefault(getComponentType($$0), ItemEnchantments.EMPTY);
   }

   private static DataComponentType<ItemEnchantments> getComponentType(ItemStack $$0) {
      return $$0.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
   }

   public static boolean hasAnyEnchantments(ItemStack $$0) {
      return !$$0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()
         || !$$0.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty();
   }

   public static int processDurabilityChange(ServerLevel $$0, ItemStack $$1, int $$2) {
      MutableFloat $$3 = new MutableFloat((float)$$2);
      runIterationOnItem($$1, ($$3x, $$4) -> ((Enchantment)$$3x.value()).modifyDurabilityChange($$0, $$4, $$1, $$3));
      return $$3.intValue();
   }

   public static int processAmmoUse(ServerLevel $$0, ItemStack $$1, ItemStack $$2, int $$3) {
      MutableFloat $$4 = new MutableFloat((float)$$3);
      runIterationOnItem($$1, ($$3x, $$4x) -> ((Enchantment)$$3x.value()).modifyAmmoCount($$0, $$4x, $$2, $$4));
      return $$4.intValue();
   }

   public static int processBlockExperience(ServerLevel $$0, ItemStack $$1, int $$2) {
      MutableFloat $$3 = new MutableFloat((float)$$2);
      runIterationOnItem($$1, ($$3x, $$4) -> ((Enchantment)$$3x.value()).modifyBlockExperience($$0, $$4, $$1, $$3));
      return $$3.intValue();
   }

   public static int processMobExperience(ServerLevel $$0, @Nullable Entity $$1, Entity $$2, int $$3) {
      if ($$1 instanceof LivingEntity $$4) {
         MutableFloat $$5 = new MutableFloat((float)$$3);
         runIterationOnEquipment($$4, ($$3x, $$4x, $$5x) -> ((Enchantment)$$3x.value()).modifyMobExperience($$0, $$4x, $$5x.itemStack(), $$2, $$5));
         return $$5.intValue();
      } else {
         return $$3;
      }
   }

   public static ItemStack createBook(EnchantmentInstance $$0) {
      ItemStack $$1 = new ItemStack(Items.ENCHANTED_BOOK);
      $$1.enchant($$0.enchantment(), $$0.level());
      return $$1;
   }

   private static void runIterationOnItem(ItemStack $$0, EnchantmentHelper.EnchantmentVisitor $$1) {
      ItemEnchantments $$2 = $$0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

      for(Entry<Holder<Enchantment>> $$3 : $$2.entrySet()) {
         $$1.accept((Holder<Enchantment>)$$3.getKey(), $$3.getIntValue());
      }
   }

   private static void runIterationOnItem(ItemStack $$0, EquipmentSlot $$1, LivingEntity $$2, EnchantmentHelper.EnchantmentInSlotVisitor $$3) {
      if (!$$0.isEmpty()) {
         ItemEnchantments $$4 = $$0.get(DataComponents.ENCHANTMENTS);
         if ($$4 != null && !$$4.isEmpty()) {
            EnchantedItemInUse $$5 = new EnchantedItemInUse($$0, $$1, $$2);

            for(Entry<Holder<Enchantment>> $$6 : $$4.entrySet()) {
               Holder<Enchantment> $$7 = (Holder)$$6.getKey();
               if (((Enchantment)$$7.value()).matchingSlot($$1)) {
                  $$3.accept($$7, $$6.getIntValue(), $$5);
               }
            }
         }
      }
   }

   private static void runIterationOnEquipment(LivingEntity $$0, EnchantmentHelper.EnchantmentInSlotVisitor $$1) {
      for(EquipmentSlot $$2 : EquipmentSlot.VALUES) {
         runIterationOnItem($$0.getItemBySlot($$2), $$2, $$0, $$1);
      }
   }

   public static boolean isImmuneToDamage(ServerLevel $$0, LivingEntity $$1, DamageSource $$2) {
      MutableBoolean $$3 = new MutableBoolean();
      runIterationOnEquipment($$1, ($$4, $$5, $$6) -> $$3.setValue($$3.isTrue() || ((Enchantment)$$4.value()).isImmuneToDamage($$0, $$5, $$1, $$2)));
      return $$3.isTrue();
   }

   public static float getDamageProtection(ServerLevel $$0, LivingEntity $$1, DamageSource $$2) {
      MutableFloat $$3 = new MutableFloat(0.0F);
      runIterationOnEquipment($$1, ($$4, $$5, $$6) -> ((Enchantment)$$4.value()).modifyDamageProtection($$0, $$5, $$6.itemStack(), $$1, $$2, $$3));
      return $$3.floatValue();
   }

   public static float modifyDamage(ServerLevel $$0, ItemStack $$1, Entity $$2, DamageSource $$3, float $$4) {
      MutableFloat $$5 = new MutableFloat($$4);
      runIterationOnItem($$1, ($$5x, $$6) -> ((Enchantment)$$5x.value()).modifyDamage($$0, $$6, $$1, $$2, $$3, $$5));
      return $$5.floatValue();
   }

   public static float modifyFallBasedDamage(ServerLevel $$0, ItemStack $$1, Entity $$2, DamageSource $$3, float $$4) {
      MutableFloat $$5 = new MutableFloat($$4);
      runIterationOnItem($$1, ($$5x, $$6) -> ((Enchantment)$$5x.value()).modifyFallBasedDamage($$0, $$6, $$1, $$2, $$3, $$5));
      return $$5.floatValue();
   }

   public static float modifyArmorEffectiveness(ServerLevel $$0, ItemStack $$1, Entity $$2, DamageSource $$3, float $$4) {
      MutableFloat $$5 = new MutableFloat($$4);
      runIterationOnItem($$1, ($$5x, $$6) -> ((Enchantment)$$5x.value()).modifyArmorEffectivness($$0, $$6, $$1, $$2, $$3, $$5));
      return $$5.floatValue();
   }

   public static float modifyKnockback(ServerLevel $$0, ItemStack $$1, Entity $$2, DamageSource $$3, float $$4) {
      MutableFloat $$5 = new MutableFloat($$4);
      runIterationOnItem($$1, ($$5x, $$6) -> ((Enchantment)$$5x.value()).modifyKnockback($$0, $$6, $$1, $$2, $$3, $$5));
      return $$5.floatValue();
   }

   public static void doPostAttackEffects(ServerLevel $$0, Entity $$1, DamageSource $$2) {
      Entity var4 = $$2.getEntity();
      if (var4 instanceof LivingEntity $$3) {
         doPostAttackEffectsWithItemSource($$0, $$1, $$2, $$3.getWeaponItem());
      } else {
         doPostAttackEffectsWithItemSource($$0, $$1, $$2, null);
      }
   }

   public static void doPostAttackEffectsWithItemSource(ServerLevel $$0, Entity $$1, DamageSource $$2, @Nullable ItemStack $$3) {
      doPostAttackEffectsWithItemSourceOnBreak($$0, $$1, $$2, $$3, null);
   }

   public static void doPostAttackEffectsWithItemSourceOnBreak(
      ServerLevel $$0, Entity $$1, DamageSource $$2, @Nullable ItemStack $$3, @Nullable Consumer<Item> $$4
   ) {
      if ($$1 instanceof LivingEntity $$5) {
         runIterationOnEquipment($$5, ($$3x, $$4x, $$5) -> ((Enchantment)$$3x.value()).doPostAttack($$0, $$4x, $$5, EnchantmentTarget.VICTIM, $$1, $$2));
      }

      if ($$3 != null) {
         Entity $$7 = $$2.getEntity();
         if ($$7 instanceof LivingEntity $$6) {
            runIterationOnItem(
               $$3,
               EquipmentSlot.MAINHAND,
               $$6,
               ($$3x, $$4x, $$5) -> ((Enchantment)$$3x.value()).doPostAttack($$0, $$4x, $$5, EnchantmentTarget.ATTACKER, $$1, $$2)
            );
         } else if ($$4 != null) {
            EnchantedItemInUse $$7 = new EnchantedItemInUse($$3, null, null, $$4);
            runIterationOnItem($$3, ($$4x, $$5) -> ((Enchantment)$$4x.value()).doPostAttack($$0, $$5, $$7, EnchantmentTarget.ATTACKER, $$1, $$2));
         }
      }
   }

   public static void runLocationChangedEffects(ServerLevel $$0, LivingEntity $$1) {
      runIterationOnEquipment($$1, ($$2, $$3, $$4) -> ((Enchantment)$$2.value()).runLocationChangedEffects($$0, $$3, $$4, $$1));
   }

   public static void runLocationChangedEffects(ServerLevel $$0, ItemStack $$1, LivingEntity $$2, EquipmentSlot $$3) {
      runIterationOnItem($$1, $$3, $$2, ($$2x, $$3x, $$4) -> ((Enchantment)$$2x.value()).runLocationChangedEffects($$0, $$3x, $$4, $$2));
   }

   public static void stopLocationBasedEffects(LivingEntity $$0) {
      runIterationOnEquipment($$0, ($$1, $$2, $$3) -> ((Enchantment)$$1.value()).stopLocationBasedEffects($$2, $$3, $$0));
   }

   public static void stopLocationBasedEffects(ItemStack $$0, LivingEntity $$1, EquipmentSlot $$2) {
      runIterationOnItem($$0, $$2, $$1, ($$1x, $$2x, $$3) -> ((Enchantment)$$1x.value()).stopLocationBasedEffects($$2x, $$3, $$1));
   }

   public static void tickEffects(ServerLevel $$0, LivingEntity $$1) {
      runIterationOnEquipment($$1, ($$2, $$3, $$4) -> ((Enchantment)$$2.value()).tick($$0, $$3, $$4, $$1));
   }

   public static int getEnchantmentLevel(Holder<Enchantment> $$0, LivingEntity $$1) {
      Iterable<ItemStack> $$2 = ((Enchantment)$$0.value()).getSlotItems($$1).values();
      int $$3 = 0;

      for(ItemStack $$4 : $$2) {
         int $$5 = getItemEnchantmentLevel($$0, $$4);
         if ($$5 > $$3) {
            $$3 = $$5;
         }
      }

      return $$3;
   }

   public static int processProjectileCount(ServerLevel $$0, ItemStack $$1, Entity $$2, int $$3) {
      MutableFloat $$4 = new MutableFloat((float)$$3);
      runIterationOnItem($$1, ($$4x, $$5) -> ((Enchantment)$$4x.value()).modifyProjectileCount($$0, $$5, $$1, $$2, $$4));
      return Math.max(0, $$4.intValue());
   }

   public static float processProjectileSpread(ServerLevel $$0, ItemStack $$1, Entity $$2, float $$3) {
      MutableFloat $$4 = new MutableFloat($$3);
      runIterationOnItem($$1, ($$4x, $$5) -> ((Enchantment)$$4x.value()).modifyProjectileSpread($$0, $$5, $$1, $$2, $$4));
      return Math.max(0.0F, $$4.floatValue());
   }

   public static int getPiercingCount(ServerLevel $$0, ItemStack $$1, ItemStack $$2) {
      MutableFloat $$3 = new MutableFloat(0.0F);
      runIterationOnItem($$1, ($$3x, $$4) -> ((Enchantment)$$3x.value()).modifyPiercingCount($$0, $$4, $$2, $$3));
      return Math.max(0, $$3.intValue());
   }

   public static void onProjectileSpawned(ServerLevel $$0, ItemStack $$1, Projectile $$2, Consumer<Item> $$3) {
      Entity var6 = $$2.getOwner();
      LivingEntity $$5 = var6 instanceof LivingEntity $$4 ? $$4 : null;
      EnchantedItemInUse $$6 = new EnchantedItemInUse($$1, null, $$5, $$3);
      runIterationOnItem($$1, ($$3x, $$4) -> ((Enchantment)$$3x.value()).onProjectileSpawned($$0, $$4, $$6, $$2));
   }

   public static void onHitBlock(
      ServerLevel $$0, ItemStack $$1, @Nullable LivingEntity $$2, Entity $$3, @Nullable EquipmentSlot $$4, Vec3 $$5, BlockState $$6, Consumer<Item> $$7
   ) {
      EnchantedItemInUse $$8 = new EnchantedItemInUse($$1, $$4, $$2, $$7);
      runIterationOnItem($$1, ($$5x, $$6x) -> ((Enchantment)$$5x.value()).onHitBlock($$0, $$6x, $$8, $$3, $$5, $$6));
   }

   public static int modifyDurabilityToRepairFromXp(ServerLevel $$0, ItemStack $$1, int $$2) {
      MutableFloat $$3 = new MutableFloat((float)$$2);
      runIterationOnItem($$1, ($$3x, $$4) -> ((Enchantment)$$3x.value()).modifyDurabilityToRepairFromXp($$0, $$4, $$1, $$3));
      return Math.max(0, $$3.intValue());
   }

   public static float processEquipmentDropChance(ServerLevel $$0, LivingEntity $$1, DamageSource $$2, float $$3) {
      MutableFloat $$4 = new MutableFloat($$3);
      RandomSource $$5 = $$1.getRandom();
      runIterationOnEquipment($$1, ($$5x, $$6x, $$7x) -> {
         LootContext $$8 = Enchantment.damageContext($$0, $$6x, $$1, $$2);
         ((Enchantment)$$5x.value()).getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach($$4xx -> {
            if ($$4xx.enchanted() == EnchantmentTarget.VICTIM && $$4xx.affected() == EnchantmentTarget.VICTIM && $$4xx.matches($$8)) {
               $$4.setValue(((EnchantmentValueEffect)$$4xx.effect()).process($$6x, $$5, $$4.floatValue()));
            }
         });
      });
      Entity $$6 = $$2.getEntity();
      if ($$6 instanceof LivingEntity $$7) {
         runIterationOnEquipment($$7, ($$5x, $$6x, $$7x) -> {
            LootContext $$8 = Enchantment.damageContext($$0, $$6x, $$1, $$2);
            ((Enchantment)$$5x.value()).getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach($$4xx -> {
               if ($$4xx.enchanted() == EnchantmentTarget.ATTACKER && $$4xx.affected() == EnchantmentTarget.VICTIM && $$4xx.matches($$8)) {
                  $$4.setValue(((EnchantmentValueEffect)$$4xx.effect()).process($$6x, $$5, $$4.floatValue()));
               }
            });
         });
      }

      return $$4.floatValue();
   }

   public static void forEachModifier(ItemStack $$0, EquipmentSlotGroup $$1, BiConsumer<Holder<Attribute>, AttributeModifier> $$2) {
      runIterationOnItem($$0, ($$2x, $$3) -> ((Enchantment)$$2x.value()).getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach($$4 -> {
            if (((Enchantment)$$2x.value()).definition().slots().contains($$1)) {
               $$2.accept($$4.attribute(), $$4.getModifier($$3, $$1));
            }
         }));
   }

   public static void forEachModifier(ItemStack $$0, EquipmentSlot $$1, BiConsumer<Holder<Attribute>, AttributeModifier> $$2) {
      runIterationOnItem($$0, ($$2x, $$3) -> ((Enchantment)$$2x.value()).getEffects(EnchantmentEffectComponents.ATTRIBUTES).forEach($$4 -> {
            if (((Enchantment)$$2x.value()).matchingSlot($$1)) {
               $$2.accept($$4.attribute(), $$4.getModifier($$3, $$1));
            }
         }));
   }

   public static int getFishingLuckBonus(ServerLevel $$0, ItemStack $$1, Entity $$2) {
      MutableFloat $$3 = new MutableFloat(0.0F);
      runIterationOnItem($$1, ($$4, $$5) -> ((Enchantment)$$4.value()).modifyFishingLuckBonus($$0, $$5, $$1, $$2, $$3));
      return Math.max(0, $$3.intValue());
   }

   public static float getFishingTimeReduction(ServerLevel $$0, ItemStack $$1, Entity $$2) {
      MutableFloat $$3 = new MutableFloat(0.0F);
      runIterationOnItem($$1, ($$4, $$5) -> ((Enchantment)$$4.value()).modifyFishingTimeReduction($$0, $$5, $$1, $$2, $$3));
      return Math.max(0.0F, $$3.floatValue());
   }

   public static int getTridentReturnToOwnerAcceleration(ServerLevel $$0, ItemStack $$1, Entity $$2) {
      MutableFloat $$3 = new MutableFloat(0.0F);
      runIterationOnItem($$1, ($$4, $$5) -> ((Enchantment)$$4.value()).modifyTridentReturnToOwnerAcceleration($$0, $$5, $$1, $$2, $$3));
      return Math.max(0, $$3.intValue());
   }

   public static float modifyCrossbowChargingTime(ItemStack $$0, LivingEntity $$1, float $$2) {
      MutableFloat $$3 = new MutableFloat($$2);
      runIterationOnItem($$0, ($$2x, $$3x) -> ((Enchantment)$$2x.value()).modifyCrossbowChargeTime($$1.getRandom(), $$3x, $$3));
      return Math.max(0.0F, $$3.floatValue());
   }

   public static float getTridentSpinAttackStrength(ItemStack $$0, LivingEntity $$1) {
      MutableFloat $$2 = new MutableFloat(0.0F);
      runIterationOnItem($$0, ($$2x, $$3) -> ((Enchantment)$$2x.value()).modifyTridentSpinAttackStrength($$1.getRandom(), $$3, $$2));
      return $$2.floatValue();
   }

   public static boolean hasTag(ItemStack $$0, TagKey<Enchantment> $$1) {
      ItemEnchantments $$2 = $$0.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

      for(Entry<Holder<Enchantment>> $$3 : $$2.entrySet()) {
         Holder<Enchantment> $$4 = (Holder)$$3.getKey();
         if ($$4.is($$1)) {
            return true;
         }
      }

      return false;
   }

   public static boolean has(ItemStack $$0, DataComponentType<?> $$1) {
      MutableBoolean $$2 = new MutableBoolean(false);
      runIterationOnItem($$0, ($$2x, $$3) -> {
         if (((Enchantment)$$2x.value()).effects().has($$1)) {
            $$2.setTrue();
         }
      });
      return $$2.booleanValue();
   }

   public static <T> Optional<T> pickHighestLevel(ItemStack $$0, DataComponentType<List<T>> $$1) {
      Pair<List<T>, Integer> $$2 = getHighestLevel($$0, $$1);
      if ($$2 != null) {
         List<T> $$3 = (List)$$2.getFirst();
         int $$4 = $$2.getSecond();
         return Optional.of($$3.get(Math.min($$4, $$3.size()) - 1));
      } else {
         return Optional.empty();
      }
   }

   @Nullable
   public static <T> Pair<T, Integer> getHighestLevel(ItemStack $$0, DataComponentType<T> $$1) {
      MutableObject<Pair<T, Integer>> $$2 = new MutableObject<>();
      runIterationOnItem($$0, ($$2x, $$3) -> {
         if ($$2.getValue() == null || $$2.getValue().getSecond() < $$3) {
            T $$4 = ((Enchantment)$$2x.value()).effects().get($$1);
            if ($$4 != null) {
               $$2.setValue(Pair.of($$4, $$3));
            }
         }
      });
      return $$2.getValue();
   }

   public static Optional<EnchantedItemInUse> getRandomItemWith(DataComponentType<?> $$0, LivingEntity $$1, Predicate<ItemStack> $$2) {
      List<EnchantedItemInUse> $$3 = new ArrayList();

      for(EquipmentSlot $$4 : EquipmentSlot.VALUES) {
         ItemStack $$5 = $$1.getItemBySlot($$4);
         if ($$2.test($$5)) {
            ItemEnchantments $$6 = $$5.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

            for(Entry<Holder<Enchantment>> $$7 : $$6.entrySet()) {
               Holder<Enchantment> $$8 = (Holder)$$7.getKey();
               if (((Enchantment)$$8.value()).effects().has($$0) && ((Enchantment)$$8.value()).matchingSlot($$4)) {
                  $$3.add(new EnchantedItemInUse($$5, $$4, $$1));
               }
            }
         }
      }

      return Util.getRandomSafe($$3, $$1.getRandom());
   }

   public static int getEnchantmentCost(RandomSource $$0, int $$1, int $$2, ItemStack $$3) {
      Enchantable $$4 = $$3.get(DataComponents.ENCHANTABLE);
      if ($$4 == null) {
         return 0;
      } else {
         if ($$2 > 15) {
            $$2 = 15;
         }

         int $$5 = $$0.nextInt(8) + 1 + ($$2 >> 1) + $$0.nextInt($$2 + 1);
         if ($$1 == 0) {
            return Math.max($$5 / 3, 1);
         } else {
            return $$1 == 1 ? $$5 * 2 / 3 + 1 : Math.max($$5, $$2 * 2);
         }
      }
   }

   public static ItemStack enchantItem(RandomSource $$0, ItemStack $$1, int $$2, RegistryAccess $$3, Optional<? extends HolderSet<Enchantment>> $$4) {
      return enchantItem(
         $$0,
         $$1,
         $$2,
         (Stream<Holder<Enchantment>>)$$4.map(HolderSet::stream).orElseGet(() -> $$3.lookupOrThrow(Registries.ENCHANTMENT).listElements().map($$0xx -> $$0xx))
      );
   }

   public static ItemStack enchantItem(RandomSource $$0, ItemStack $$1, int $$2, Stream<Holder<Enchantment>> $$3) {
      List<EnchantmentInstance> $$4 = selectEnchantment($$0, $$1, $$2, $$3);
      if ($$1.is(Items.BOOK)) {
         $$1 = new ItemStack(Items.ENCHANTED_BOOK);
      }

      for(EnchantmentInstance $$5 : $$4) {
         $$1.enchant($$5.enchantment(), $$5.level());
      }

      return $$1;
   }

   public static List<EnchantmentInstance> selectEnchantment(RandomSource $$0, ItemStack $$1, int $$2, Stream<Holder<Enchantment>> $$3) {
      List<EnchantmentInstance> $$4 = Lists.newArrayList();
      Enchantable $$5 = $$1.get(DataComponents.ENCHANTABLE);
      if ($$5 == null) {
         return $$4;
      } else {
         $$2 += 1 + $$0.nextInt($$5.value() / 4 + 1) + $$0.nextInt($$5.value() / 4 + 1);
         float $$6 = ($$0.nextFloat() + $$0.nextFloat() - 1.0F) * 0.15F;
         $$2 = Mth.clamp(Math.round((float)$$2 + (float)$$2 * $$6), 1, Integer.MAX_VALUE);
         List<EnchantmentInstance> $$7 = getAvailableEnchantmentResults($$2, $$1, $$3);
         if (!$$7.isEmpty()) {
            WeightedRandom.getRandomItem($$0, $$7, EnchantmentInstance::weight).ifPresent($$4::add);

            while($$0.nextInt(50) <= $$2) {
               if (!$$4.isEmpty()) {
                  filterCompatibleEnchantments($$7, Util.lastOf($$4));
               }

               if ($$7.isEmpty()) {
                  break;
               }

               WeightedRandom.getRandomItem($$0, $$7, EnchantmentInstance::weight).ifPresent($$4::add);
               $$2 /= 2;
            }
         }

         return $$4;
      }
   }

   public static void filterCompatibleEnchantments(List<EnchantmentInstance> $$0, EnchantmentInstance $$1) {
      $$0.removeIf($$1x -> !Enchantment.areCompatible($$1.enchantment(), $$1x.enchantment()));
   }

   public static boolean isEnchantmentCompatible(Collection<Holder<Enchantment>> $$0, Holder<Enchantment> $$1) {
      for(Holder<Enchantment> $$2 : $$0) {
         if (!Enchantment.areCompatible($$2, $$1)) {
            return false;
         }
      }

      return true;
   }

   public static List<EnchantmentInstance> getAvailableEnchantmentResults(int $$0, ItemStack $$1, Stream<Holder<Enchantment>> $$2) {
      List<EnchantmentInstance> $$3 = Lists.newArrayList();
      boolean $$4 = $$1.is(Items.BOOK);
      $$2.filter($$2x -> ((Enchantment)$$2x.value()).isPrimaryItem($$1) || $$4).forEach($$2x -> {
         Enchantment $$3xx = (Enchantment)$$2x.value();

         for(int $$4xx = $$3xx.getMaxLevel(); $$4xx >= $$3xx.getMinLevel(); --$$4xx) {
            if ($$0 >= $$3xx.getMinCost($$4xx) && $$0 <= $$3xx.getMaxCost($$4xx)) {
               $$3.add(new EnchantmentInstance($$2x, $$4xx));
               break;
            }
         }
      });
      return $$3;
   }

   public static void enchantItemFromProvider(
      ItemStack $$0, RegistryAccess $$1, ResourceKey<EnchantmentProvider> $$2, DifficultyInstance $$3, RandomSource $$4
   ) {
      EnchantmentProvider $$5 = $$1.lookupOrThrow(Registries.ENCHANTMENT_PROVIDER).getValue($$2);
      if ($$5 != null) {
         updateEnchantments($$0, $$4x -> $$5.enchant($$0, $$4x, $$4, $$3));
      }
   }

   @FunctionalInterface
   interface EnchantmentInSlotVisitor {
      void accept(Holder<Enchantment> var1, int var2, EnchantedItemInUse var3);
   }

   @FunctionalInterface
   interface EnchantmentVisitor {
      void accept(Holder<Enchantment> var1, int var2);
   }
}
