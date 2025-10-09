package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityStraddleboard;
import com.github.alexthe666.alexsmobs.entity.IFalconry;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

public class MessageSyncEntityPos {
   public int eagleId;
   public double posX;
   public double posY;
   public double posZ;

   public MessageSyncEntityPos(int eagleId, double posX, double posY, double posZ) {
      this.eagleId = eagleId;
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
   }

   public MessageSyncEntityPos() {
   }

   public static MessageSyncEntityPos read(FriendlyByteBuf buf) {
      return new MessageSyncEntityPos(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble());
   }

   public static void write(MessageSyncEntityPos message, FriendlyByteBuf buf) {
      buf.writeInt(message.eagleId);
      buf.writeDouble(message.posX);
      buf.writeDouble(message.posY);
      buf.writeDouble(message.posZ);
   }

   public static class Handler {
      public static void handle(MessageSyncEntityPos message, Supplier<Context> context) {
         context.get().setPacketHandled(true);
         context.get().enqueueWork(() -> {
            Player player = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
               player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null && player.m_9236_() != null) {
               Entity entity = player.m_9236_().m_6815_(message.eagleId);
               if (entity instanceof IFalconry || entity instanceof EntityStraddleboard) {
                  entity.m_6034_(message.posX, message.posY, message.posZ);
                  entity.m_20324_(message.posX, message.posY, message.posZ);
               }
            }
         });
      }
   }
}
