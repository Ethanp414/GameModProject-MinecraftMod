package net.minecraft.world.item.equipment;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ArmorMaterial(
   int durability,
   Map<ArmorType, Integer> defense,
   int enchantmentValue,
   Holder<SoundEvent> equipSound,
   float toughness,
   float knockbackResistance,
   TagKey<Item> repairIngredient,
   ResourceKey<EquipmentAsset> assetId
) {
   public ItemAttributeModifiers createAttributes(ArmorType $$0) {
      int $$1 = this.defense.getOrDefault($$0, 0);
      ItemAttributeModifiers.Builder $$2 = ItemAttributeModifiers.builder();
      EquipmentSlotGroup $$3 = EquipmentSlotGroup.bySlot($$0.getSlot());
      ResourceLocation $$4 = ResourceLocation.withDefaultNamespace("armor." + $$0.getName());
      $$2.add(Attributes.ARMOR, new AttributeModifier($$4, (double)$$1, AttributeModifier.Operation.ADD_VALUE), $$3);
      $$2.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier($$4, (double)this.toughness, AttributeModifier.Operation.ADD_VALUE), $$3);
      if (this.knockbackResistance > 0.0F) {
         $$2.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier($$4, (double)this.knockbackResistance, AttributeModifier.Operation.ADD_VALUE), $$3);
      }

      return $$2.build();
   }
}
