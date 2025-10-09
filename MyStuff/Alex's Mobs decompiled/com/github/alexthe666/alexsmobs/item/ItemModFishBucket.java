package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemModFishBucket extends MobBucketItem {
   public ItemModFishBucket(Supplier<? extends EntityType<?>> fishTypeIn, Fluid fluid, Properties builder) {
      super(fishTypeIn, () -> fluid, () -> SoundEvents.f_11779_, builder.m_41487_(1));
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7373_(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      EntityType fishType = this.getFishType();
      if (fishType == AMEntityRegistry.LOBSTER.get()) {
         CompoundTag compoundnbt = stack.m_41783_();
         if (compoundnbt != null && compoundnbt.m_128425_("BucketVariantTag", 3)) {
            int i = compoundnbt.m_128451_("BucketVariantTag");
            String s = "entity.alexsmobs.lobster.variant_" + EntityLobster.getVariantName(i);
            tooltip.add(Component.m_237115_(s).m_130940_(ChatFormatting.GRAY).m_130940_(ChatFormatting.ITALIC));
         }
      }

      if (fishType == AMEntityRegistry.TERRAPIN.get()) {
         CompoundTag compoundnbt = stack.m_41783_();
         if (compoundnbt != null && compoundnbt.m_128441_("TerrapinData")) {
            int i = compoundnbt.m_128469_("TerrapinData").m_128451_("TurtleType");
            tooltip.add(
               Component.m_237115_(TerrapinTypes.values()[Mth.m_14045_(i, 0, TerrapinTypes.values().length - 1)].getTranslationName())
                  .m_130940_(ChatFormatting.GRAY)
                  .m_130940_(ChatFormatting.ITALIC)
            );
         }
      }

      if (fishType == AMEntityRegistry.COMB_JELLY.get()) {
         CompoundTag compoundnbt = stack.m_41783_();
         if (compoundnbt != null && compoundnbt.m_128425_("BucketVariantTag", 3)) {
            int i = compoundnbt.m_128451_("BucketVariantTag");
            String s = "entity.alexsmobs.comb_jelly.variant_" + i;
            tooltip.add(Component.m_237115_(s).m_130940_(ChatFormatting.GRAY).m_130940_(ChatFormatting.ITALIC));
         }
      }
   }

   public void m_142131_(@Nullable Player player, Level level, ItemStack stack, BlockPos pos) {
      if (level instanceof ServerLevel) {
         this.spawnFish((ServerLevel)level, stack, pos);
         level.m_142346_(player, GameEvent.f_157810_, pos);
      }
   }

   private void spawnFish(ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
      Entity entity = this.getFishType().m_20592_(serverLevel, stack, (Player)null, pos, MobSpawnType.BUCKET, true, false);
      if (entity instanceof Bucketable bucketable) {
         bucketable.m_142278_(stack.m_41784_());
         bucketable.m_27497_(true);
      }

      this.addExtraAttributes(entity, stack);
   }

   private void addExtraAttributes(Entity entity, ItemStack stack) {
      if (entity instanceof EntityCatfish catfish) {
         if (stack.m_150930_((Item)AMItemRegistry.SMALL_CATFISH_BUCKET.get())) {
            catfish.setCatfishSize(0);
         } else if (stack.m_150930_((Item)AMItemRegistry.MEDIUM_CATFISH_BUCKET.get())) {
            catfish.setCatfishSize(1);
         } else if (stack.m_150930_((Item)AMItemRegistry.LARGE_CATFISH_BUCKET.get())) {
            catfish.setCatfishSize(2);
         }
      }
   }
}
