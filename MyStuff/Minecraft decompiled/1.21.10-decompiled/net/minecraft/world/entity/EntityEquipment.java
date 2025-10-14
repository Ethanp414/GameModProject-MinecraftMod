package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.world.item.ItemStack;

public class EntityEquipment {
   public static final Codec<EntityEquipment> CODEC = Codec.unboundedMap(EquipmentSlot.CODEC, ItemStack.CODEC).xmap($$0 -> {
      EnumMap<EquipmentSlot, ItemStack> $$1 = new EnumMap(EquipmentSlot.class);
      $$1.putAll($$0);
      return new EntityEquipment($$1);
   }, $$0 -> {
      Map<EquipmentSlot, ItemStack> $$1 = new EnumMap($$0.items);
      $$1.values().removeIf(ItemStack::isEmpty);
      return $$1;
   });
   private final EnumMap<EquipmentSlot, ItemStack> items;

   private EntityEquipment(EnumMap<EquipmentSlot, ItemStack> $$0) {
      this.items = $$0;
   }

   public EntityEquipment() {
      this(new EnumMap(EquipmentSlot.class));
   }

   public ItemStack set(EquipmentSlot $$0, ItemStack $$1) {
      return (ItemStack)Objects.requireNonNullElse((ItemStack)this.items.put($$0, $$1), ItemStack.EMPTY);
   }

   public ItemStack get(EquipmentSlot $$0) {
      return (ItemStack)this.items.getOrDefault($$0, ItemStack.EMPTY);
   }

   public boolean isEmpty() {
      for(ItemStack $$0 : this.items.values()) {
         if (!$$0.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void tick(Entity $$0) {
      for(Entry<EquipmentSlot, ItemStack> $$1 : this.items.entrySet()) {
         ItemStack $$2 = (ItemStack)$$1.getValue();
         if (!$$2.isEmpty()) {
            $$2.inventoryTick($$0.level(), $$0, (EquipmentSlot)$$1.getKey());
         }
      }
   }

   public void setAll(EntityEquipment $$0) {
      this.items.clear();
      this.items.putAll($$0.items);
   }

   public void dropAll(LivingEntity $$0) {
      for(ItemStack $$1 : this.items.values()) {
         $$0.drop($$1, true, false);
      }

      this.clear();
   }

   public void clear() {
      this.items.replaceAll(($$0, $$1) -> ItemStack.EMPTY);
   }
}
