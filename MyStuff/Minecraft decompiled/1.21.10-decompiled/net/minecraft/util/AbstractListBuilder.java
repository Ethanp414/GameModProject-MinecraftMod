package net.minecraft.util;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.function.UnaryOperator;

abstract class AbstractListBuilder<T, B> implements ListBuilder<T> {
   private final DynamicOps<T> ops;
   protected DataResult<B> builder = DataResult.success(this.initBuilder(), Lifecycle.stable());

   protected AbstractListBuilder(DynamicOps<T> $$0) {
      this.ops = $$0;
   }

   @Override
   public DynamicOps<T> ops() {
      return this.ops;
   }

   protected abstract B initBuilder();

   protected abstract B append(B var1, T var2);

   protected abstract DataResult<T> build(B var1, T var2);

   @Override
   public ListBuilder<T> add(T $$0) {
      this.builder = this.builder.map($$1 -> this.append((B)$$1, $$0));
      return this;
   }

   @Override
   public ListBuilder<T> add(DataResult<T> $$0) {
      this.builder = this.builder.apply2stable(this::append, $$0);
      return this;
   }

   @Override
   public ListBuilder<T> withErrorsFrom(DataResult<?> $$0) {
      this.builder = this.builder.flatMap($$1 -> $$0.map($$1x -> $$1));
      return this;
   }

   @Override
   public ListBuilder<T> mapError(UnaryOperator<String> $$0) {
      this.builder = this.builder.mapError($$0);
      return this;
   }

   @Override
   public DataResult<T> build(T $$0) {
      DataResult<T> $$1 = this.builder.flatMap($$1x -> this.build((B)$$1x, $$0));
      this.builder = DataResult.success(this.initBuilder(), Lifecycle.stable());
      return $$1;
   }
}
