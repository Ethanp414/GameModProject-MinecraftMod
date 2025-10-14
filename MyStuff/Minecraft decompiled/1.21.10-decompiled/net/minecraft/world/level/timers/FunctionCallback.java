package net.minecraft.world.level.timers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public record FunctionCallback(ResourceLocation functionId) implements TimerCallback<MinecraftServer> {
   public static final MapCodec<FunctionCallback> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(ResourceLocation.CODEC.fieldOf("Name").forGetter(FunctionCallback::functionId)).apply($$0, FunctionCallback::new)
   );

   public void handle(MinecraftServer $$0, TimerQueue<MinecraftServer> $$1, long $$2) {
      ServerFunctionManager $$3 = $$0.getFunctions();
      $$3.get(this.functionId).ifPresent($$1x -> $$3.execute($$1x, $$3.getGameLoopSender()));
   }

   @Override
   public MapCodec<FunctionCallback> codec() {
      return CODEC;
   }
}
