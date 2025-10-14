package net.minecraft.world.level.entity;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.level.ChunkPos;

public class ChunkEntities<T> {
   private final ChunkPos pos;
   private final List<T> entities;

   public ChunkEntities(ChunkPos $$0, List<T> $$1) {
      this.pos = $$0;
      this.entities = $$1;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public Stream<T> getEntities() {
      return this.entities.stream();
   }

   public boolean isEmpty() {
      return this.entities.isEmpty();
   }
}
