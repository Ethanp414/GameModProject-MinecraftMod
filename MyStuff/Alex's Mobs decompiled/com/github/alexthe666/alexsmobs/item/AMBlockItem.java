package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;

public class AMBlockItem extends BlockItem implements CustomTabBehavior {
   private final RegistryObject<Block> blockSupplier;

   public AMBlockItem(RegistryObject<Block> blockSupplier, Properties props) {
      super((Block)null, props);
      this.blockSupplier = blockSupplier;
   }

   public Block m_40614_() {
      return (Block)this.blockSupplier.get();
   }

   public boolean canFitInsideCraftingRemainingItems() {
      return !(this.blockSupplier.get() instanceof ShulkerBoxBlock);
   }

   public void m_142023_(ItemEntity p_150700_) {
      if (this.blockSupplier.get() instanceof ShulkerBoxBlock) {
         ItemStack itemstack = p_150700_.m_32055_();
         CompoundTag compoundtag = m_186336_(itemstack);
         if (compoundtag != null && compoundtag.m_128425_("Items", 9)) {
            ListTag listtag = compoundtag.m_128437_("Items", 10);
            ItemUtils.m_150952_(p_150700_, listtag.stream().map(CompoundTag.class::cast).map(ItemStack::m_41712_));
         }
      }
   }

   public boolean m_41386_(DamageSource damage) {
      return super.m_41386_(damage) && (this != ((Block)AMBlockRegistry.TRANSMUTATION_TABLE.get()).m_5456_() || !damage.m_269533_(DamageTypeTags.f_268415_));
   }

   @Override
   public void fillItemCategory(Output contents) {
      if (!this.blockSupplier.equals(AMBlockRegistry.SAND_CIRCLE) && !this.blockSupplier.equals(AMBlockRegistry.RED_SAND_CIRCLE)) {
         contents.m_246326_(this);
      }
   }

   public InteractionResult m_6225_(UseOnContext context) {
      return this.blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS) ? InteractionResult.PASS : super.m_6225_(context);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      if (this.blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS)) {
         BlockHitResult blockhitresult = m_41435_(level, player, Fluid.SOURCE_ONLY);
         BlockHitResult blockhitresult1 = blockhitresult.m_82430_(blockhitresult.m_82425_().m_7494_());
         InteractionResult interactionresult = super.m_6225_(new UseOnContext(player, hand, blockhitresult1));
         return new InteractionResultHolder(interactionresult, player.m_21120_(hand));
      } else {
         return super.m_7203_(level, player, hand);
      }
   }
}
