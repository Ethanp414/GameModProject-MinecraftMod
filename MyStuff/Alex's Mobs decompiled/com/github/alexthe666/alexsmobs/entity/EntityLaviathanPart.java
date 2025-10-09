package com.github.alexthe666.alexsmobs.entity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

public class EntityLaviathanPart extends PartEntity<EntityLaviathan> {
   private final EntityDimensions size;
   public float scale = 1.0F;

   public EntityLaviathanPart(EntityLaviathan parent, float sizeX, float sizeY) {
      super(parent);
      this.size = EntityDimensions.m_20395_(sizeX, sizeY);
      this.m_6210_();
   }

   public EntityLaviathanPart(EntityLaviathan entityCachalotWhale, float sizeX, float sizeY, EntityDimensions size) {
      super(entityCachalotWhale);
      this.size = size;
   }

   public boolean m_5825_() {
      return true;
   }

   public Vec3 m_7939_() {
      return new Vec3(0.0, this.m_20192_() * 0.15F, this.m_20205_() * 0.1F);
   }

   protected void collideWithNearbyEntities() {
   }

   public InteractionResult getEntityInteractionResult(Player player, InteractionHand hand) {
      return this.getParent() == null ? InteractionResult.PASS : ((EntityLaviathan)this.getParent()).m_6071_(player, hand);
   }

   public boolean m_5829_() {
      return false;
   }

   protected void collideWithEntity(Entity entityIn) {
      if (!(entityIn instanceof EntityLaviathan)) {
         entityIn.m_7334_(this);
      }
   }

   public boolean m_6087_() {
      return true;
   }

   @Nullable
   public ItemStack m_142340_() {
      Entity parent = this.getParent();
      return parent != null ? parent.m_142340_() : ItemStack.f_41583_;
   }

   public boolean m_6469_(DamageSource source, float amount) {
      return !this.m_6673_(source) && ((EntityLaviathan)this.getParent()).attackEntityPartFrom(this, source, amount);
   }

   public boolean m_7306_(Entity entityIn) {
      return this == entityIn || this.getParent() == entityIn;
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      throw new UnsupportedOperationException();
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return this.size == null ? EntityDimensions.m_20395_(0.0F, 0.0F) : this.size.m_20388_(this.scale);
   }

   protected void m_8097_() {
   }

   public void m_8119_() {
      super.m_8119_();
   }

   protected void m_7378_(CompoundTag compound) {
   }

   protected void m_7380_(CompoundTag compound) {
   }
}
