package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyComponentsFunction extends LootItemConditionalFunction {
   private static final ExtraCodecs.LateBoundIdMapper<String, CopyComponentsFunction.Source<?>> SOURCES = new ExtraCodecs.LateBoundIdMapper<>();
   public static final MapCodec<CopyComponentsFunction> CODEC;
   private final CopyComponentsFunction.Source<?> source;
   private final Optional<List<DataComponentType<?>>> include;
   private final Optional<List<DataComponentType<?>>> exclude;
   private final Predicate<DataComponentType<?>> bakedPredicate;

   CopyComponentsFunction(
      List<LootItemCondition> $$0, CopyComponentsFunction.Source<?> $$1, Optional<List<DataComponentType<?>>> $$2, Optional<List<DataComponentType<?>>> $$3
   ) {
      super($$0);
      this.source = $$1;
      this.include = $$2.map(List::copyOf);
      this.exclude = $$3.map(List::copyOf);
      List<Predicate<DataComponentType<?>>> $$4 = new ArrayList(2);
      $$3.ifPresent($$1x -> $$4.add((Predicate)$$1xx -> !$$1x.contains($$1xx)));
      $$2.ifPresent($$1x -> $$4.add($$1x::contains));
      this.bakedPredicate = Util.allOf($$4);
   }

   @Override
   public LootItemFunctionType<CopyComponentsFunction> getType() {
      return LootItemFunctions.COPY_COMPONENTS;
   }

   @Override
   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   @Override
   public ItemStack run(ItemStack $$0, LootContext $$1) {
      DataComponentGetter $$2 = this.source.get($$1);
      if ($$2 != null) {
         if ($$2 instanceof DataComponentMap $$3) {
            $$0.applyComponents($$3.filter(this.bakedPredicate));
         } else {
            Collection<DataComponentType<?>> $$4 = (Collection)this.exclude.orElse(List.of());
            ((Stream)this.include.map(Collection::stream).orElse(BuiltInRegistries.DATA_COMPONENT_TYPE.listElements().map(Holder::value))).forEach($$3x -> {
               if (!$$4.contains($$3x)) {
                  TypedDataComponent<?> $$4xx = $$2.getTyped($$3x);
                  if ($$4xx != null) {
                     $$0.set($$4xx);
                  }
               }
            });
         }
      }

      return $$0;
   }

   public static CopyComponentsFunction.Builder copyComponentsFromEntity(ContextKey<? extends Entity> $$0) {
      return new CopyComponentsFunction.Builder(new CopyComponentsFunction.EntitySource($$0));
   }

   public static CopyComponentsFunction.Builder copyComponentsFromBlockEntity(ContextKey<? extends BlockEntity> $$0) {
      return new CopyComponentsFunction.Builder(new CopyComponentsFunction.BlockEntitySource($$0));
   }

   static {
      for(LootContext.EntityTarget $$0 : LootContext.EntityTarget.values()) {
         SOURCES.put($$0.getSerializedName(), new CopyComponentsFunction.EntitySource($$0.getParam()));
      }

      for(LootContext.BlockEntityTarget $$1 : LootContext.BlockEntityTarget.values()) {
         SOURCES.put($$1.getSerializedName(), new CopyComponentsFunction.BlockEntitySource($$1.getParam()));
      }

      for(LootContext.ItemStackTarget $$2 : LootContext.ItemStackTarget.values()) {
         SOURCES.put($$2.getSerializedName(), new CopyComponentsFunction.ItemStackSource($$2.getParam()));
      }

      CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> commonFields($$0)
               .and(
                  $$0.group(
                     SOURCES.codec(Codec.STRING).fieldOf("source").forGetter($$0x -> $$0x.source),
                     DataComponentType.CODEC.listOf().optionalFieldOf("include").forGetter($$0x -> $$0x.include),
                     DataComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter($$0x -> $$0x.exclude)
                  )
               )
               .apply($$0, CopyComponentsFunction::new)
      );
   }

   static record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements CopyComponentsFunction.Source<BlockEntity> {
      public DataComponentGetter get(BlockEntity $$0) {
         return $$0.collectComponents();
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyComponentsFunction.Builder> {
      private final CopyComponentsFunction.Source<?> source;
      private Optional<ImmutableList.Builder<DataComponentType<?>>> include = Optional.empty();
      private Optional<ImmutableList.Builder<DataComponentType<?>>> exclude = Optional.empty();

      Builder(CopyComponentsFunction.Source<?> $$0) {
         this.source = $$0;
      }

      public CopyComponentsFunction.Builder include(DataComponentType<?> $$0) {
         if (this.include.isEmpty()) {
            this.include = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.include.get()).add($$0);
         return this;
      }

      public CopyComponentsFunction.Builder exclude(DataComponentType<?> $$0) {
         if (this.exclude.isEmpty()) {
            this.exclude = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.exclude.get()).add($$0);
         return this;
      }

      protected CopyComponentsFunction.Builder getThis() {
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new CopyComponentsFunction(
            this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build)
         );
      }
   }

   static record EntitySource(ContextKey<? extends Entity> contextParam) implements CopyComponentsFunction.Source<Entity> {
      public DataComponentGetter get(Entity $$0) {
         return $$0;
      }
   }

   static record ItemStackSource(ContextKey<? extends ItemStack> contextParam) implements CopyComponentsFunction.Source<ItemStack> {
      public DataComponentGetter get(ItemStack $$0) {
         return $$0.getComponents();
      }
   }

   public interface Source<T> {
      ContextKey<? extends T> contextParam();

      DataComponentGetter get(T var1);

      @Nullable
      default DataComponentGetter get(LootContext $$0) {
         T $$1 = $$0.getOptionalParameter(this.contextParam());
         return $$1 != null ? this.get($$1) : null;
      }
   }
}
