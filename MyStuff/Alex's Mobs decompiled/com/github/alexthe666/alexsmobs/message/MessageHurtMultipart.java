package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.IHurtableMultipart;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

public class MessageHurtMultipart {
   public int part;
   public int parent;
   public float damage;
   public String damageType;

   public MessageHurtMultipart(int part, int parent, float damage) {
      this.part = part;
      this.parent = parent;
      this.damage = damage;
      this.damageType = "";
   }

   public MessageHurtMultipart(int part, int parent, float damage, String damageType) {
      this.part = part;
      this.parent = parent;
      this.damage = damage;
      this.damageType = damageType;
   }

   public MessageHurtMultipart() {
   }

   public static MessageHurtMultipart read(FriendlyByteBuf buf) {
      return new MessageHurtMultipart(buf.readInt(), buf.readInt(), buf.readFloat(), buf.m_130277_());
   }

   public static void write(MessageHurtMultipart message, FriendlyByteBuf buf) {
      buf.writeInt(message.part);
      buf.writeInt(message.parent);
      buf.writeFloat(message.damage);
      buf.m_130070_(message.damageType);
   }

   public static class Handler {
      public static void handle(MessageHurtMultipart message, Supplier<Context> context) {
         context.get().setPacketHandled(true);
         context.get().enqueueWork(() -> {
            Player player = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
               player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null && player.m_9236_() != null) {
               Entity part = player.m_9236_().m_6815_(message.part);
               Entity parent = player.m_9236_().m_6815_(message.parent);
               Registry<DamageType> registry = (Registry<DamageType>)player.m_9236_().m_9598_().m_6632_(Registries.f_268580_).get();
               DamageType dmg = (DamageType)registry.m_7745_(new ResourceLocation(message.damageType));
               if (dmg != null) {
                  Holder<DamageType> holder = (Holder<DamageType>)registry.m_203300_(registry.m_7447_(dmg)).orElseGet(null);
                  if (holder != null) {
                     DamageSource source = new DamageSource((Holder)registry.m_203300_(registry.m_7447_(dmg)).get());
                     if (part instanceof IHurtableMultipart && parent instanceof LivingEntity) {
                        ((IHurtableMultipart)part).onAttackedFromServer((LivingEntity)parent, message.damage, source);
                     }

                     if (part == null && parent != null && parent.isMultipartEntity()) {
                        parent.m_6469_(source, message.damage);
                     }
                  }
               }
            }
         });
      }
   }
}
