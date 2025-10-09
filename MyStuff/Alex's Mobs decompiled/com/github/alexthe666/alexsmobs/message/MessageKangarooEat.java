package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import java.util.function.Supplier;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

public class MessageKangarooEat {
   public int kangaroo;
   public ItemStack stack;

   public MessageKangarooEat(int kangaroo, ItemStack stack) {
      this.kangaroo = kangaroo;
      this.stack = stack;
   }

   public MessageKangarooEat() {
   }

   public static MessageKangarooEat read(FriendlyByteBuf buf) {
      return new MessageKangarooEat(buf.readInt(), buf.m_130267_());
   }

   public static void write(MessageKangarooEat message, FriendlyByteBuf buf) {
      buf.writeInt(message.kangaroo);
      buf.m_130055_(message.stack);
   }

   public static class Handler {
      public static void handle(MessageKangarooEat message, Supplier<Context> context) {
         context.get().setPacketHandled(true);
         context.get()
            .enqueueWork(
               () -> {
                  Player player = context.get().getSender();
                  if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                     player = AlexsMobs.PROXY.getClientSidePlayer();
                  }

                  if (player != null && player.m_9236_() != null) {
                     Entity entity = player.m_9236_().m_6815_(message.kangaroo);
                     if (entity instanceof EntityKangaroo kangaroo && ((EntityKangaroo)entity).kangarooInventory != null) {
                        for (int i = 0; i < 7; i++) {
                           double d2 = kangaroo.m_217043_().m_188583_() * 0.02;
                           double d0 = kangaroo.m_217043_().m_188583_() * 0.02;
                           double d1 = kangaroo.m_217043_().m_188583_() * 0.02;
                           entity.m_9236_()
                              .m_7106_(
                                 new ItemParticleOption(ParticleTypes.f_123752_, message.stack),
                                 entity.m_20185_() + kangaroo.m_217043_().m_188501_() * entity.m_20205_() - entity.m_20205_() * 0.5,
                                 entity.m_20186_() + entity.m_20206_() * 0.5F + kangaroo.m_217043_().m_188501_() * entity.m_20206_() * 0.5F,
                                 entity.m_20189_() + kangaroo.m_217043_().m_188501_() * entity.m_20205_() - entity.m_20205_() * 0.5,
                                 d0,
                                 d1,
                                 d2
                              );
                        }
                     }
                  }
               }
            );
      }
   }
}
