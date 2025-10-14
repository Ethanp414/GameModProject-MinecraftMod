package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1458 extends NamespacedSchema {
   public V1458(int $$0, Schema $$1) {
      super($$0, $$1);
   }

   @Override
   public void registerTypes(Schema $$0, Map<String, Supplier<TypeTemplate>> $$1, Map<String, Supplier<TypeTemplate>> $$2) {
      super.registerTypes($$0, $$1, $$2);
      $$0.registerType(
         true,
         References.ENTITY,
         () -> DSL.and(
               References.ENTITY_EQUIPMENT.in($$0),
               DSL.optionalFields("CustomName", References.TEXT_COMPONENT.in($$0), DSL.taggedChoiceLazy("id", namespacedString(), $$1))
            )
      );
   }

   @Override
   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema $$0) {
      Map<String, Supplier<TypeTemplate>> $$1 = super.registerBlockEntities($$0);
      $$0.register($$1, "minecraft:beacon", (Supplier<TypeTemplate>)(() -> nameable($$0)));
      $$0.register($$1, "minecraft:banner", (Supplier<TypeTemplate>)(() -> nameable($$0)));
      $$0.register($$1, "minecraft:brewing_stand", (Supplier<TypeTemplate>)(() -> nameableInventory($$0)));
      $$0.register($$1, "minecraft:chest", (Supplier<TypeTemplate>)(() -> nameableInventory($$0)));
      $$0.register($$1, "minecraft:trapped_chest", (Supplier<TypeTemplate>)(() -> nameableInventory($$0)));
      $$0.register($$1, "minecraft:dispenser", (Supplier<TypeTemplate>)(() -> nameableInventory($$0)));
      $$0.register($$1, "minecraft:dropper", (Supplier<TypeTemplate>)(() -> nameableInventory($$0)));
      $$0.register($$1, "minecraft:enchanting_table", (Supplier<TypeTemplate>)(() -> nameable($$0)));
      $$0.register($$1, "minecraft:furnace", (Supplier<TypeTemplate>)(() -> nameableInventory($$0)));
      $$0.register($$1, "minecraft:hopper", (Supplier<TypeTemplate>)(() -> nameableInventory($$0)));
      $$0.register($$1, "minecraft:shulker_box", (Supplier<TypeTemplate>)(() -> nameableInventory($$0)));
      return $$1;
   }

   public static TypeTemplate nameableInventory(Schema $$0) {
      return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in($$0)), "CustomName", References.TEXT_COMPONENT.in($$0));
   }

   public static TypeTemplate nameable(Schema $$0) {
      return DSL.optionalFields("CustomName", References.TEXT_COMPONENT.in($$0));
   }
}
