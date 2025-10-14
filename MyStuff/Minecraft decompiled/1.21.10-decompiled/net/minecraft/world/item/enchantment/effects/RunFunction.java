package net.minecraft.world.item.enchantment.effects;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public record RunFunction(ResourceLocation function) implements EnchantmentEntityEffect {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<RunFunction> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(ResourceLocation.CODEC.fieldOf("function").forGetter(RunFunction::function)).apply($$0, RunFunction::new)
   );

   @Override
   public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
      MinecraftServer $$5 = $$0.getServer();
      ServerFunctionManager $$6 = $$5.getFunctions();
      Optional<CommandFunction<CommandSourceStack>> $$7 = $$6.get(this.function);
      if ($$7.isPresent()) {
         CommandSourceStack $$8 = $$5.createCommandSourceStack()
            .withPermission(2)
            .withSuppressedOutput()
            .withEntity($$3)
            .withLevel($$0)
            .withPosition($$4)
            .withRotation($$3.getRotationVector());
         $$6.execute((CommandFunction<CommandSourceStack>)$$7.get(), $$8);
      } else {
         LOGGER.error("Enchantment run_function effect failed for non-existent function {}", this.function);
      }
   }

   @Override
   public MapCodec<RunFunction> codec() {
      return CODEC;
   }
}
