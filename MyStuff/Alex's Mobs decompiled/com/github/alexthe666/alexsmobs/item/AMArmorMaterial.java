package com.github.alexthe666.alexsmobs.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.crafting.Ingredient;

public class AMArmorMaterial implements ArmorMaterial {
   protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
   private final String name;
   private final int durability;
   private final int[] damageReduction;
   private final int encantability;
   private final SoundEvent sound;
   private final float toughness;
   private Ingredient ingredient = null;
   public float knockbackResistance = 0.0F;

   public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness) {
      this.name = name;
      this.durability = durability;
      this.damageReduction = damageReduction;
      this.encantability = encantability;
      this.sound = sound;
      this.toughness = toughness;
      this.knockbackResistance = 0.0F;
   }

   public AMArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, float knockbackResist) {
      this.name = name;
      this.durability = durability;
      this.damageReduction = damageReduction;
      this.encantability = encantability;
      this.sound = sound;
      this.toughness = toughness;
      this.knockbackResistance = knockbackResist;
   }

   public int m_266425_(Type type) {
      return MAX_DAMAGE_ARRAY[type.ordinal()] * this.durability;
   }

   public int m_7366_(Type type) {
      return this.damageReduction[type.ordinal()];
   }

   public int m_6646_() {
      return this.encantability;
   }

   public SoundEvent m_7344_() {
      return this.sound;
   }

   public Ingredient m_6230_() {
      return this.ingredient == null ? Ingredient.f_43901_ : this.ingredient;
   }

   public void setRepairMaterial(Ingredient ingredient) {
      this.ingredient = ingredient;
   }

   public String m_6082_() {
      return this.name;
   }

   public float m_6651_() {
      return this.toughness;
   }

   public float m_6649_() {
      return this.knockbackResistance;
   }
}
