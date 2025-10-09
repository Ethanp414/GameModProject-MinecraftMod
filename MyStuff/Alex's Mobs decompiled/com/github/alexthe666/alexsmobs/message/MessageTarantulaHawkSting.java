package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

public class MessageTarantulaHawkSting {
   public int hawk;
   public int spider;

   public MessageTarantulaHawkSting(int rider, int mount) {
      this.hawk = rider;
      this.spider = mount;
   }

   public MessageTarantulaHawkSting() {
   }

   public static MessageTarantulaHawkSting read(FriendlyByteBuf buf) {
      return new MessageTarantulaHawkSting(buf.readInt(), buf.readInt());
   }

   public static void write(MessageTarantulaHawkSting message, FriendlyByteBuf buf) {
      buf.writeInt(message.hawk);
      buf.writeInt(message.spider);
   }

   public static class Handler {
      public static void handle(MessageTarantulaHawkSting message, Supplier<Context> context) {
         context.get().setPacketHandled(true);
         context.get().enqueueWork(() -> {
            Player player = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
               player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null && player.m_9236_() != null) {
               Entity entity = player.m_9236_().m_6815_(message.hawk);
               Entity spider = player.m_9236_().m_6815_(message.spider);
               if (entity instanceof EntityTarantulaHawk && spider instanceof LivingEntity && ((LivingEntity)spider).m_6336_() == MobType.f_21642_) {
                  ((LivingEntity)spider).m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.DEBILITATING_STING.get(), 2400));
               }
            }
         });
      }
   }
}
