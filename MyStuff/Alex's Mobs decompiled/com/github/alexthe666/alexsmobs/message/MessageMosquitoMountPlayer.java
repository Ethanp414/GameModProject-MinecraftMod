package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

public class MessageMosquitoMountPlayer {
   public int rider;
   public int mount;

   public MessageMosquitoMountPlayer(int rider, int mount) {
      this.rider = rider;
      this.mount = mount;
   }

   public MessageMosquitoMountPlayer() {
   }

   public static MessageMosquitoMountPlayer read(FriendlyByteBuf buf) {
      return new MessageMosquitoMountPlayer(buf.readInt(), buf.readInt());
   }

   public static void write(MessageMosquitoMountPlayer message, FriendlyByteBuf buf) {
      buf.writeInt(message.rider);
      buf.writeInt(message.mount);
   }

   public static class Handler {
      public static void handle(MessageMosquitoMountPlayer message, Supplier<Context> context) {
         context.get().setPacketHandled(true);
         context.get()
            .enqueueWork(
               () -> {
                  Player player = context.get().getSender();
                  if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                     player = AlexsMobs.PROXY.getClientSidePlayer();
                  }

                  if (player != null && player.m_9236_() != null) {
                     Entity entity = player.m_9236_().m_6815_(message.rider);
                     Entity mountEntity = player.m_9236_().m_6815_(message.mount);
                     if ((entity instanceof EntityCrimsonMosquito || entity instanceof EntityEnderiophage || entity instanceof EntityBaldEagle)
                        && mountEntity instanceof Player
                        && entity.m_20270_(mountEntity) < 16.0) {
                        entity.m_7998_(mountEntity, true);
                     }
                  }
               }
            );
      }
   }
}
