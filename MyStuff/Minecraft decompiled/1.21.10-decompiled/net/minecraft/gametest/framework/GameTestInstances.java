package net.minecraft.gametest.framework;

import java.util.function.Consumer;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface GameTestInstances {
   ResourceKey<GameTestInstance> ALWAYS_PASS = create("always_pass");

   static void bootstrap(BootstrapContext<GameTestInstance> $$0) {
      HolderGetter<Consumer<GameTestHelper>> $$1 = $$0.lookup(Registries.TEST_FUNCTION);
      HolderGetter<TestEnvironmentDefinition> $$2 = $$0.lookup(Registries.TEST_ENVIRONMENT);
      $$0.register(
         ALWAYS_PASS,
         new FunctionGameTestInstance(
            BuiltinTestFunctions.ALWAYS_PASS,
            new TestData<>($$2.getOrThrow(GameTestEnvironments.DEFAULT_KEY), ResourceLocation.withDefaultNamespace("empty"), 1, 1, false)
         )
      );
   }

   private static ResourceKey<GameTestInstance> create(String $$0) {
      return ResourceKey.create(Registries.TEST_INSTANCE, ResourceLocation.withDefaultNamespace($$0));
   }
}
