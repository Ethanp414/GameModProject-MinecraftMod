package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ContextNbtProvider implements NbtProvider {
   private static final ExtraCodecs.LateBoundIdMapper<String, ContextNbtProvider.Source<?>> SOURCES = new ExtraCodecs.LateBoundIdMapper<>();
   private static final Codec<ContextNbtProvider.Source<?>> GETTER_CODEC;
   public static final MapCodec<ContextNbtProvider> MAP_CODEC;
   public static final Codec<ContextNbtProvider> INLINE_CODEC;
   private final ContextNbtProvider.Source<?> source;

   private ContextNbtProvider(ContextNbtProvider.Source<?> $$0) {
      this.source = $$0;
   }

   @Override
   public LootNbtProviderType getType() {
      return NbtProviders.CONTEXT;
   }

   @Nullable
   @Override
   public Tag get(LootContext $$0) {
      return this.source.get($$0);
   }

   @Override
   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public static NbtProvider forContextEntity(LootContext.EntityTarget $$0) {
      return new ContextNbtProvider(new ContextNbtProvider.EntitySource($$0.getParam()));
   }

   static {
      for(LootContext.EntityTarget $$0 : LootContext.EntityTarget.values()) {
         SOURCES.put($$0.getSerializedName(), new ContextNbtProvider.EntitySource($$0.getParam()));
      }

      for(LootContext.BlockEntityTarget $$1 : LootContext.BlockEntityTarget.values()) {
         SOURCES.put($$1.getSerializedName(), new ContextNbtProvider.BlockEntitySource($$1.getParam()));
      }

      GETTER_CODEC = SOURCES.codec(Codec.STRING);
      MAP_CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(GETTER_CODEC.fieldOf("target").forGetter($$0x -> $$0x.source)).apply($$0, ContextNbtProvider::new)
      );
      INLINE_CODEC = GETTER_CODEC.xmap(ContextNbtProvider::new, $$0 -> $$0.source);
   }

   static record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements ContextNbtProvider.Source<BlockEntity> {
      public Tag get(BlockEntity $$0) {
         return $$0.saveWithFullMetadata($$0.getLevel().registryAccess());
      }
   }

   static record EntitySource(ContextKey<? extends Entity> contextParam) implements ContextNbtProvider.Source<Entity> {
      public Tag get(Entity $$0) {
         return NbtPredicate.getEntityTagToCompare($$0);
      }
   }

   interface Source<T> {
      ContextKey<? extends T> contextParam();

      @Nullable
      Tag get(T var1);

      @Nullable
      default Tag get(LootContext $$0) {
         T $$1 = $$0.getOptionalParameter(this.contextParam());
         return $$1 != null ? this.get($$1) : null;
      }
   }
}
