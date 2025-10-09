package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityStraddleboard;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class ItemStraddleboard extends Item implements DyeableLeatherItem {
   private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.f_20408_.and(Entity::m_6087_);

   public ItemStraddleboard(Properties properties) {
      super(properties);
   }

   public int m_41121_(ItemStack p_200886_1_) {
      CompoundTag lvt_2_1_ = p_200886_1_.m_41737_("display");
      return lvt_2_1_ != null && lvt_2_1_.m_128425_("color", 99) ? lvt_2_1_.m_128451_("color") : 11387863;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return super.canApplyAtEnchantingTable(stack, enchantment) && enchantment != Enchantments.f_44986_ && enchantment != Enchantments.f_44962_;
   }

   public int m_6473_() {
      return 1;
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.m_21120_(handIn);
      HitResult raytraceresult = m_41435_(worldIn, playerIn, Fluid.ANY);
      if (raytraceresult.m_6662_() == Type.MISS) {
         return InteractionResultHolder.m_19098_(itemstack);
      } else {
         Vec3 vector3d = playerIn.m_20252_(1.0F);
         double d0 = 5.0;
         List<Entity> list = worldIn.m_6249_(playerIn, playerIn.m_20191_().m_82369_(vector3d.m_82490_(5.0)).m_82400_(1.0), ENTITY_PREDICATE);
         if (!list.isEmpty()) {
            Vec3 vector3d1 = playerIn.m_20299_(1.0F);

            for (Entity entity : list) {
               AABB axisalignedbb = entity.m_20191_().m_82400_(entity.m_6143_());
               if (axisalignedbb.m_82390_(vector3d1)) {
                  return InteractionResultHolder.m_19098_(itemstack);
               }
            }
         }

         if (raytraceresult.m_6662_() == Type.BLOCK) {
            EntityStraddleboard boatentity = new EntityStraddleboard(
               worldIn, raytraceresult.m_82450_().f_82479_, raytraceresult.m_82450_().f_82480_, raytraceresult.m_82450_().f_82481_
            );
            boatentity.setDefaultColor(!this.m_41113_(itemstack));
            boatentity.setItemStack(itemstack.m_41777_());
            boatentity.setColor(this.m_41121_(itemstack));
            boatentity.m_146922_(playerIn.m_146908_());
            if (!worldIn.m_45756_(boatentity, boatentity.m_20191_().m_82400_(-0.1))) {
               return InteractionResultHolder.m_19100_(itemstack);
            } else {
               if (!worldIn.f_46443_) {
                  worldIn.m_7967_(boatentity);
                  if (!playerIn.m_150110_().f_35937_) {
                     itemstack.m_41774_(1);
                  }
               }

               playerIn.m_36246_(Stats.f_12982_.m_12902_(this));
               return InteractionResultHolder.m_19092_(itemstack, worldIn.m_5776_());
            }
         } else {
            return InteractionResultHolder.m_19098_(itemstack);
         }
      }
   }
}
