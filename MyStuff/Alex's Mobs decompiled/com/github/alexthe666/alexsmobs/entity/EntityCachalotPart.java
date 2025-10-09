package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.message.MessageInteractMultipart;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;

public class EntityCachalotPart extends PartEntity<EntityCachalotWhale> {
   private final EntityDimensions size;
   public float scale = 1.0F;

   public EntityCachalotPart(EntityCachalotWhale parent, float sizeX, float sizeY) {
      super(parent);
      this.size = EntityDimensions.m_20395_(sizeX, sizeY);
      this.m_6210_();
   }

   public EntityCachalotPart(EntityCachalotWhale entityCachalotWhale, float sizeX, float sizeY, EntityDimensions size) {
      super(entityCachalotWhale);
      this.size = size;
   }

   protected void collideWithNearbyEntities() {
      List<Entity> entities = this.m_9236_().m_45933_(this, this.m_20191_().m_82363_(0.2, 0.0, 0.2));
      Entity parent = this.getParent();
      if (parent != null) {
         entities.stream()
            .filter(
               entity -> entity != parent
                  && (!(entity instanceof EntityCachalotPart) || ((EntityCachalotPart)entity).getParent() != parent)
                  && entity.m_6094_()
            )
            .forEach(entity -> entity.m_7334_(parent));
      }
   }

   public InteractionResult m_6096_(Player player, InteractionHand hand) {
      if (this.m_9236_().f_46443_ && this.getParent() != null) {
         AlexsMobs.sendMSGToServer(new MessageInteractMultipart(((EntityCachalotWhale)this.getParent()).m_19879_(), hand == InteractionHand.OFF_HAND));
      }

      return this.getParent() == null ? InteractionResult.PASS : ((EntityCachalotWhale)this.getParent()).m_6071_(player, hand);
   }

   protected void collideWithEntity(Entity entityIn) {
      entityIn.m_7334_(this);
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
      if (this.m_9236_().f_46443_ && this.getParent() != null && !((EntityCachalotWhale)this.getParent()).m_6673_(source)) {
         ResourceLocation key = ((Registry)this.m_9236_().m_9598_().m_6632_(Registries.f_268580_).get()).m_7981_(source.m_269415_());
         if (key != null) {
            AlexsMobs.sendMSGToServer(new MessageHurtMultipart(this.m_19879_(), ((EntityCachalotWhale)this.getParent()).m_19879_(), amount, key.toString()));
         }
      }

      return !this.m_6673_(source) && ((EntityCachalotWhale)this.getParent()).attackEntityPartFrom(this, source, amount);
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
