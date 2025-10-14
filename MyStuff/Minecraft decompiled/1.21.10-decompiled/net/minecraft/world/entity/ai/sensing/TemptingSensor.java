package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TemptingSensor extends Sensor<PathfinderMob> {
   private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().ignoreLineOfSight();
   private final Predicate<ItemStack> temptations;

   public TemptingSensor(Predicate<ItemStack> $$0) {
      this.temptations = $$0;
   }

   protected void doTick(ServerLevel $$0, PathfinderMob $$1) {
      Brain<?> $$2 = $$1.getBrain();
      TargetingConditions $$3 = TEMPT_TARGETING.copy().range((double)((float)$$1.getAttributeValue(Attributes.TEMPT_RANGE)));
      List<Player> $$4 = (List)$$0.players()
         .stream()
         .filter(EntitySelector.NO_SPECTATORS)
         .filter($$3x -> $$3.test($$0, $$1, $$3x))
         .filter(this::playerHoldingTemptation)
         .filter($$1x -> !$$1.hasPassenger($$1x))
         .sorted(Comparator.comparingDouble($$1::distanceToSqr))
         .collect(Collectors.toList());
      if (!$$4.isEmpty()) {
         Player $$5 = (Player)$$4.get(0);
         $$2.setMemory(MemoryModuleType.TEMPTING_PLAYER, $$5);
      } else {
         $$2.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
      }
   }

   private boolean playerHoldingTemptation(Player $$0) {
      return this.isTemptation($$0.getMainHandItem()) || this.isTemptation($$0.getOffhandItem());
   }

   private boolean isTemptation(ItemStack $$0) {
      return this.temptations.test($$0);
   }

   @Override
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
   }
}
