package net.minecraft.world.entity.ai.memory;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class NearestVisibleLivingEntities {
   private static final NearestVisibleLivingEntities EMPTY = new NearestVisibleLivingEntities();
   private final List<LivingEntity> nearbyEntities;
   private final Predicate<LivingEntity> lineOfSightTest;

   private NearestVisibleLivingEntities() {
      this.nearbyEntities = List.of();
      this.lineOfSightTest = $$0 -> false;
   }

   public NearestVisibleLivingEntities(ServerLevel $$0, LivingEntity $$1, List<LivingEntity> $$2) {
      this.nearbyEntities = $$2;
      Object2BooleanOpenHashMap<LivingEntity> $$3 = new Object2BooleanOpenHashMap<>($$2.size());
      Predicate<LivingEntity> $$4 = $$2x -> Sensor.isEntityTargetable($$0, $$1, $$2x);
      this.lineOfSightTest = $$2x -> $$3.computeIfAbsent($$2x, $$4);
   }

   public static NearestVisibleLivingEntities empty() {
      return EMPTY;
   }

   public Optional<LivingEntity> findClosest(Predicate<LivingEntity> $$0) {
      for(LivingEntity $$1 : this.nearbyEntities) {
         if ($$0.test($$1) && this.lineOfSightTest.test($$1)) {
            return Optional.of($$1);
         }
      }

      return Optional.empty();
   }

   public Iterable<LivingEntity> findAll(Predicate<LivingEntity> $$0) {
      return Iterables.filter(this.nearbyEntities, $$1 -> $$0.test($$1) && this.lineOfSightTest.test($$1));
   }

   public Stream<LivingEntity> find(Predicate<LivingEntity> $$0) {
      return this.nearbyEntities.stream().filter($$1 -> $$0.test($$1) && this.lineOfSightTest.test($$1));
   }

   public boolean contains(LivingEntity $$0) {
      return this.nearbyEntities.contains($$0) && this.lineOfSightTest.test($$0);
   }

   public boolean contains(Predicate<LivingEntity> $$0) {
      for(LivingEntity $$1 : this.nearbyEntities) {
         if ($$0.test($$1) && this.lineOfSightTest.test($$1)) {
            return true;
         }
      }

      return false;
   }
}
