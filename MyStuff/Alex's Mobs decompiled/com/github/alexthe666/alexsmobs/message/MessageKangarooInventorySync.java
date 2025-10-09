package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent.Context;

public class MessageKangarooInventorySync {
   public int kangaroo;
   public int slotId;
   public ItemStack stack;

   public MessageKangarooInventorySync(int kangaroo, int slotId, ItemStack stack) {
      this.kangaroo = kangaroo;
      this.slotId = slotId;
      this.stack = stack;
   }

   public MessageKangarooInventorySync() {
   }

   public static MessageKangarooInventorySync read(FriendlyByteBuf buf) {
      return new MessageKangarooInventorySync(buf.readInt(), buf.readInt(), buf.m_130267_());
   }

   public static void write(MessageKangarooInventorySync message, FriendlyByteBuf buf) {
      buf.writeInt(message.kangaroo);
      buf.writeInt(message.slotId);
      buf.m_130055_(message.stack);
   }

   public static class Handler {
      public static void handle(MessageKangarooInventorySync message, Supplier<Context> context) {
         context.get().setPacketHandled(true);
         context.get().enqueueWork(() -> {
            Player player = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
               player = AlexsMobs.PROXY.getClientSidePlayer();
            }

            if (player != null && player.m_9236_() != null) {
               Entity entity = player.m_9236_().m_6815_(message.kangaroo);
               if (entity instanceof EntityKangaroo && ((EntityKangaroo)entity).kangarooInventory != null && message.slotId >= 0) {
                  ((EntityKangaroo)entity).kangarooInventory.m_6836_(message.slotId, message.stack);
               }
            }
         });
      }
   }
}
