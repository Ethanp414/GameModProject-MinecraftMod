package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V1483 extends NamespacedSchema {
   public V1483(int $$0, Schema $$1) {
      super($$0, $$1);
   }

   @Override
   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
      Map<String, Supplier<TypeTemplate>> $$1 = super.registerEntities($$0);
      $$1.put("minecraft:pufferfish", (Supplier)$$1.remove("minecraft:puffer_fish"));
      return $$1;
   }
}
