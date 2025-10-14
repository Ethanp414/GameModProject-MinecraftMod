package net.minecraft.world.item;

import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServerItemCooldowns extends ItemCooldowns {
   private final ServerPlayer player;

   public ServerItemCooldowns(ServerPlayer $$0) {
      this.player = $$0;
   }

   @Override
   protected void onCooldownStarted(ResourceLocation $$0, int $$1) {
      super.onCooldownStarted($$0, $$1);
      this.player.connection.send(new ClientboundCooldownPacket($$0, $$1));
   }

   @Override
   protected void onCooldownEnded(ResourceLocation $$0) {
      super.onCooldownEnded($$0);
      this.player.connection.send(new ClientboundCooldownPacket($$0, 0));
   }
}
