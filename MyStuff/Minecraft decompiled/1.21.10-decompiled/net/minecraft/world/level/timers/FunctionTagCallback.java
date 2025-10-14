package net.minecraft.world.level.timers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public record FunctionTagCallback(ResourceLocation tagId) implements TimerCallback<MinecraftServer> {
   public static final MapCodec<FunctionTagCallback> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(ResourceLocation.CODEC.fieldOf("Name").forGetter(FunctionTagCallback::tagId)).apply($$0, FunctionTagCallback::new)
   );

   public void handle(MinecraftServer $$0, TimerQueue<MinecraftServer> $$1, long $$2) {
      ServerFunctionManager $$3 = $$0.getFunctions();

      for(CommandFunction<CommandSourceStack> $$5 : $$3.getTag(this.tagId)) {
         $$3.execute($$5, $$3.getGameLoopSender());
      }
   }

   @Override
   public MapCodec<FunctionTagCallback> codec() {
      return CODEC;
   }
}
