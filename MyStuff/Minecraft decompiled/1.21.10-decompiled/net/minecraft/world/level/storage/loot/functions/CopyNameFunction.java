package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   private static final ExtraCodecs.LateBoundIdMapper<String, CopyNameFunction.Source> SOURCES = new ExtraCodecs.LateBoundIdMapper();
   public static final MapCodec<CopyNameFunction> CODEC;
   private final CopyNameFunction.Source source;

   private CopyNameFunction(List<LootItemCondition> $$0, CopyNameFunction.Source $$1) {
      super($$0);
      this.source = $$1;
   }

   @Override
   public LootItemFunctionType<CopyNameFunction> getType() {
      return LootItemFunctions.COPY_NAME;
   }

   @Override
   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.param);
   }

   @Override
   public ItemStack run(ItemStack $$0, LootContext $$1) {
      Object $$2 = $$1.getOptionalParameter(this.source.param);
      if ($$2 instanceof Nameable $$3) {
         $$0.set(DataComponents.CUSTOM_NAME, $$3.getCustomName());
      }

      return $$0;
   }

   public static LootItemConditionalFunction.Builder<?> copyName(CopyNameFunction.Source $$0) {
      return simpleBuilder($$1 -> new CopyNameFunction($$1, $$0));
   }

   static {
      for(LootContext.EntityTarget $$0 : LootContext.EntityTarget.values()) {
         SOURCES.put($$0.getSerializedName(), new CopyNameFunction.Source($$0.getParam()));
      }

      for(LootContext.BlockEntityTarget $$1 : LootContext.BlockEntityTarget.values()) {
         SOURCES.put($$1.getSerializedName(), new CopyNameFunction.Source($$1.getParam()));
      }

      CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> commonFields($$0).and(SOURCES.codec(Codec.STRING).fieldOf("source").forGetter($$0x -> $$0x.source)).apply($$0, CopyNameFunction::new)
      );
   }

   public static record Source(ContextKey<?> param) {
      final ContextKey<?> param;
   }
}
