package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.entity.util.TendonWhipUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class ItemTendonWhip extends SwordItem implements ILeftClick {
   private final ImmutableMultimap<Attribute, AttributeModifier> tendonModifiers;

   public ItemTendonWhip(Properties props) {
      super(Tiers.IRON, 3, 0.0F, props);
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(Attributes.f_22281_, new AttributeModifier(f_41374_, "Weapon modifier", 4.0, Operation.ADDITION));
      builder.put(Attributes.f_22283_, new AttributeModifier(f_41375_, "Weapon modifier", -3.0, Operation.ADDITION));
      this.tendonModifiers = builder.build();
   }

   public static boolean isActive(ItemStack stack, LivingEntity holder) {
      return holder != null && (holder.m_21205_() == stack || holder.m_21206_() == stack) ? !TendonWhipUtil.canLaunchTendons(holder.m_9236_(), holder) : false;
   }

   public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot slot) {
      return (Multimap<Attribute, AttributeModifier>)(slot == EquipmentSlot.MAINHAND ? this.tendonModifiers : super.m_7167_(slot));
   }

   public boolean m_7579_(ItemStack stack, LivingEntity entity, LivingEntity player) {
      this.launchTendonsAt(stack, player, entity);
      return super.m_7579_(stack, entity, player);
   }

   private boolean isCharged(Player player, ItemStack stack) {
      return player.m_36403_(0.5F) > 0.9F;
   }

   @Override
   public boolean onLeftClick(ItemStack stack, LivingEntity playerIn) {
      if (stack.m_150930_((Item)AMItemRegistry.TENDON_WHIP.get()) && (!(playerIn instanceof Player) || this.isCharged((Player)playerIn, stack))) {
         Level worldIn = playerIn.m_9236_();
         Entity closestValid = null;
         Vec3 playerEyes = playerIn.m_20299_(1.0F);
         HitResult hitresult = worldIn.m_45547_(
            new ClipContext(playerEyes, playerEyes.m_82549_(playerIn.m_20154_().m_82490_(12.0)), Block.VISUAL, Fluid.NONE, playerIn)
         );
         if (hitresult instanceof EntityHitResult) {
            Entity entity = ((EntityHitResult)hitresult).m_82443_();
            if (!entity.equals(playerIn) && !playerIn.m_7307_(entity) && !entity.m_7307_(playerIn) && entity instanceof Mob && playerIn.m_142582_(entity)) {
               closestValid = entity;
            }
         } else {
            for (Entity entity : worldIn.m_45976_(LivingEntity.class, playerIn.m_20191_().m_82400_(12.0))) {
               if (!entity.equals(playerIn)
                  && !playerIn.m_7307_(entity)
                  && !entity.m_7307_(playerIn)
                  && entity instanceof Mob
                  && playerIn.m_142582_(entity)
                  && (closestValid == null || playerIn.m_20270_(entity) < playerIn.m_20270_(closestValid))) {
                  closestValid = entity;
               }
            }
         }

         if (closestValid != null) {
            stack.m_41622_(1, playerIn, player -> player.m_21190_(playerIn.m_7655_()));
         }

         return this.launchTendonsAt(stack, playerIn, closestValid);
      } else {
         return false;
      }
   }

   public boolean launchTendonsAt(ItemStack stack, LivingEntity playerIn, Entity closestValid) {
      Level worldIn = playerIn.m_9236_();
      if (TendonWhipUtil.canLaunchTendons(worldIn, playerIn)) {
         TendonWhipUtil.retractFarTendons(worldIn, playerIn);
         if (!worldIn.f_46443_ && closestValid != null) {
            EntityTendonSegment segment = (EntityTendonSegment)((EntityType)AMEntityRegistry.TENDON_SEGMENT.get()).m_20615_(worldIn);
            segment.m_20359_(playerIn);
            worldIn.m_7967_(segment);
            segment.setCreatorEntityUUID(playerIn.m_20148_());
            segment.setFromEntityID(playerIn.m_19879_());
            segment.setToEntityID(closestValid.m_19879_());
            segment.m_20359_(playerIn);
            segment.setProgress(0.0F);
            segment.setHasGlint(stack.m_41790_());
            TendonWhipUtil.setLastTendon(playerIn, segment);
            return true;
         }
      }

      return false;
   }

   public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
      return toolAction != ToolActions.SWORD_SWEEP && super.canPerformAction(stack, toolAction);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.m_41656_(oldStack, newStack);
   }

   public int getMaxDamage(ItemStack stack) {
      return 450;
   }

   public boolean m_6832_(ItemStack pickaxe, ItemStack stack) {
      return stack.m_150930_((Item)AMItemRegistry.ELASTIC_TENDON.get());
   }
}
