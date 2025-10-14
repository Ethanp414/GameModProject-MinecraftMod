package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;

public record DropChances(Map<EquipmentSlot, Float> byEquipment) {
   public static final float DEFAULT_EQUIPMENT_DROP_CHANCE = 0.085F;
   public static final float PRESERVE_ITEM_DROP_CHANCE_THRESHOLD = 1.0F;
   public static final int PRESERVE_ITEM_DROP_CHANCE = 2;
   public static final DropChances DEFAULT = new DropChances(Util.makeEnumMap(EquipmentSlot.class, $$0 -> 0.085F));
   public static final Codec<DropChances> CODEC = Codec.unboundedMap(EquipmentSlot.CODEC, ExtraCodecs.NON_NEGATIVE_FLOAT)
      .xmap(DropChances::toEnumMap, DropChances::filterDefaultValues)
      .xmap(DropChances::new, DropChances::byEquipment);

   private static Map<EquipmentSlot, Float> filterDefaultValues(Map<EquipmentSlot, Float> $$0) {
      Map<EquipmentSlot, Float> $$1 = new HashMap($$0);
      $$1.values().removeIf($$0x -> $$0x == 0.085F);
      return $$1;
   }

   private static Map<EquipmentSlot, Float> toEnumMap(Map<EquipmentSlot, Float> $$0) {
      return Util.makeEnumMap(EquipmentSlot.class, $$1 -> (Float)$$0.getOrDefault($$1, 0.085F));
   }

   public DropChances withGuaranteedDrop(EquipmentSlot $$0) {
      return this.withEquipmentChance($$0, 2.0F);
   }

   public DropChances withEquipmentChance(EquipmentSlot $$0, float $$1) {
      if ($$1 < 0.0F) {
         throw new IllegalArgumentException("Tried to set invalid equipment chance " + $$1 + " for " + $$0);
      } else {
         return this.byEquipment($$0) == $$1 ? this : new DropChances(Util.makeEnumMap(EquipmentSlot.class, $$2 -> $$2 == $$0 ? $$1 : this.byEquipment($$2)));
      }
   }

   public float byEquipment(EquipmentSlot $$0) {
      return this.byEquipment.getOrDefault($$0, 0.085F);
   }

   public boolean isPreserved(EquipmentSlot $$0) {
      return this.byEquipment($$0) > 1.0F;
   }
}
