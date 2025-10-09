package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

public class BlossomLootModifier implements IGlobalLootModifier {
   public static final Supplier<Codec<BlossomLootModifier>> CODEC = () -> RecordCodecBuilder.create(
      inst -> inst.group(LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions)).apply(inst, BlossomLootModifier::new)
   );
   private final LootItemCondition[] conditions;
   private final Predicate<LootContext> orConditions;

   public BlossomLootModifier(LootItemCondition[] conditionsIn) {
      this.conditions = conditionsIn;
      this.orConditions = LootItemConditions.m_81841_(conditionsIn);
   }

   @NotNull
   public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      return this.orConditions.test(context) ? this.doApply(generatedLoot, context) : generatedLoot;
   }

   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      if (AMConfig.acaciaBlossomsDropFromLeaves) {
         ItemStack ctxTool = (ItemStack)context.m_78953_(LootContextParams.f_81463_);
         RandomSource random = context.m_230907_();
         if (ctxTool != null) {
            int silkTouch = EnchantmentHelper.m_44843_(Enchantments.f_44985_, ctxTool);
            if (silkTouch > 0 || ctxTool.m_41720_() instanceof ShearsItem) {
               return generatedLoot;
            }
         }

         int bonusLevel = ctxTool != null ? EnchantmentHelper.m_44843_(Enchantments.f_44987_, ctxTool) : 0;
         int blossomStep = (int)Math.floor(AMConfig.acaciaBlossomChance * 0.1F);
         int blossomRarity = AMConfig.acaciaBlossomChance - bonusLevel * blossomStep;
         if (blossomRarity < 1 || random.m_188503_(blossomRarity) == 0) {
            generatedLoot.add(new ItemStack((ItemLike)AMItemRegistry.ACACIA_BLOSSOM.get()));
         }
      }

      return generatedLoot;
   }

   public Codec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }
}
