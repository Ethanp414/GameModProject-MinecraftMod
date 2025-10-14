package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_3 extends NamespacedSchema {
   public V1451_3(int $$0, Schema $$1) {
      super($$0, $$1);
   }

   @Override
   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema $$0) {
      Map<String, Supplier<TypeTemplate>> $$1 = super.registerEntities($$0);
      $$0.registerSimple($$1, "minecraft:egg");
      $$0.registerSimple($$1, "minecraft:ender_pearl");
      $$0.registerSimple($$1, "minecraft:fireball");
      $$0.register($$1, "minecraft:potion", (Function<String, TypeTemplate>)($$1x -> DSL.optionalFields("Potion", References.ITEM_STACK.in($$0))));
      $$0.registerSimple($$1, "minecraft:small_fireball");
      $$0.registerSimple($$1, "minecraft:snowball");
      $$0.registerSimple($$1, "minecraft:wither_skull");
      $$0.registerSimple($$1, "minecraft:xp_bottle");
      $$0.register($$1, "minecraft:arrow", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inBlockState", References.BLOCK_STATE.in($$0))));
      $$0.register($$1, "minecraft:enderman", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("carriedBlockState", References.BLOCK_STATE.in($$0))));
      $$0.register(
         $$1,
         "minecraft:falling_block",
         (Supplier<TypeTemplate>)(() -> DSL.optionalFields("BlockState", References.BLOCK_STATE.in($$0), "TileEntityData", References.BLOCK_ENTITY.in($$0)))
      );
      $$0.register($$1, "minecraft:spectral_arrow", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inBlockState", References.BLOCK_STATE.in($$0))));
      $$0.register(
         $$1,
         "minecraft:chest_minecart",
         (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", References.BLOCK_STATE.in($$0), "Items", DSL.list(References.ITEM_STACK.in($$0))))
      );
      $$0.register(
         $$1,
         "minecraft:commandblock_minecart",
         (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", References.BLOCK_STATE.in($$0), "LastOutput", References.TEXT_COMPONENT.in($$0)))
      );
      $$0.register($$1, "minecraft:furnace_minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", References.BLOCK_STATE.in($$0))));
      $$0.register(
         $$1,
         "minecraft:hopper_minecart",
         (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", References.BLOCK_STATE.in($$0), "Items", DSL.list(References.ITEM_STACK.in($$0))))
      );
      $$0.register($$1, "minecraft:minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", References.BLOCK_STATE.in($$0))));
      $$0.register(
         $$1,
         "minecraft:spawner_minecart",
         (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", References.BLOCK_STATE.in($$0), References.UNTAGGED_SPAWNER.in($$0)))
      );
      $$0.register($$1, "minecraft:tnt_minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", References.BLOCK_STATE.in($$0))));
      return $$1;
   }
}
