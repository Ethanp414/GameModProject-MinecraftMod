package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_2 extends NamespacedSchema {
   public V1451_2(int $$0, Schema $$1) {
      super($$0, $$1);
   }

   @Override
   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema $$0) {
      Map<String, Supplier<TypeTemplate>> $$1 = super.registerBlockEntities($$0);
      $$0.register($$1, "minecraft:piston", (Function<String, TypeTemplate>)($$1x -> DSL.optionalFields("blockState", References.BLOCK_STATE.in($$0))));
      return $$1;
   }
}
