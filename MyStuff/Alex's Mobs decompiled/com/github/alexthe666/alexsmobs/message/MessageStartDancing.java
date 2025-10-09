package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.IDancingMob;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

public class MessageStartDancing {
   public int entityID;
   public boolean dance;
   public BlockPos jukeBox;

   public MessageStartDancing(int entityID, boolean dance, BlockPos jukeBox) {
      this.entityID = entityID;
      this.dance = dance;
      this.jukeBox = jukeBox;
   }

   public MessageStartDancing() {
   }

   public static MessageStartDancing read(FriendlyByteBuf buf) {
      return new MessageStartDancing(buf.readInt(), buf.readBoolean(), buf.m_130135_());
   }

   public static void write(MessageStartDancing message, FriendlyByteBuf buf) {
      buf.writeInt(message.entityID);
      buf.writeBoolean(message.dance);
      buf.m_130064_(message.jukeBox);
   }

   public static class Handler {
      public static void handle(MessageStartDancing message, Supplier<Context> context) {
         context.get().setPacketHandled(true);
         context.get().enqueueWork(() -> {
            Player player = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
               player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null && player.m_9236_() != null) {
               Entity entity = player.m_9236_().m_6815_(message.entityID);
               if (entity instanceof IDancingMob) {
                  ((IDancingMob)entity).setDancing(message.dance);
                  if (message.dance) {
                     ((IDancingMob)entity).setJukeboxPos(message.jukeBox);
                  } else {
                     ((IDancingMob)entity).setJukeboxPos(null);
                  }
               }
            }
         });
      }
   }
}
