package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class EntitySharkToothArrow extends Arrow {
   public EntitySharkToothArrow(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   public EntitySharkToothArrow(EntityType type, double x, double y, double z, Level worldIn) {
      this(type, worldIn);
      this.m_6034_(x, y, z);
   }

   public EntitySharkToothArrow(Level worldIn, LivingEntity shooter) {
      this((EntityType)AMEntityRegistry.SHARK_TOOTH_ARROW.get(), shooter.m_20185_(), shooter.m_20188_() - 0.1F, shooter.m_20189_(), worldIn);
      this.m_5602_(shooter);
      if (shooter instanceof Player) {
         this.f_36705_ = Pickup.ALLOWED;
      }
   }

   protected void damageShield(Player player, float damage) {
      if (damage >= 3.0F && player.m_21211_().m_41720_().canPerformAction(player.m_21211_(), ToolActions.SHIELD_BLOCK)) {
         ItemStack copyBeforeUse = player.m_21211_().m_41777_();
         int i = 1 + Mth.m_14143_(damage);
         player.m_21211_().m_41622_(i, player, p_213360_0_ -> p_213360_0_.m_21166_(EquipmentSlot.CHEST));
         if (player.m_21211_().m_41619_()) {
            InteractionHand Hand = player.m_7655_();
            ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, Hand);
            if (Hand == InteractionHand.MAIN_HAND) {
               this.m_8061_(EquipmentSlot.MAINHAND, ItemStack.f_41583_);
            } else {
               this.m_8061_(EquipmentSlot.OFFHAND, ItemStack.f_41583_);
            }

            player.m_5810_();
            this.m_5496_(SoundEvents.f_12347_, 0.8F, 0.8F + this.m_9236_().f_46441_.m_188501_() * 0.4F);
         }
      }
   }

   protected void m_7761_(LivingEntity living) {
      if (living instanceof Player) {
         this.damageShield((Player)living, (float)this.m_36789_());
      }

      Entity entity1 = this.m_19749_();
      if (living.m_6336_() == MobType.f_21644_ || living instanceof Drowned || living.m_6336_() != MobType.f_21641_ && living.m_6040_()) {
         DamageSource damagesource;
         if (entity1 == null) {
            damagesource = this.m_269291_().m_269418_(this, this);
         } else {
            damagesource = this.m_269291_().m_269418_(this, entity1);
         }

         living.m_6469_(damagesource, 7.0F);
      }
   }

   public boolean m_20069_() {
      return false;
   }

   public EntitySharkToothArrow(SpawnEntity spawnEntity, Level world) {
      this((EntityType)AMEntityRegistry.SHARK_TOOTH_ARROW.get(), world);
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected ItemStack m_7941_() {
      return new ItemStack((ItemLike)AMItemRegistry.SHARK_TOOTH_ARROW.get());
   }
}
