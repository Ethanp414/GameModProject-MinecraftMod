package net.minecraft.gametest.framework;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record GeneratedTest(
   Map<ResourceLocation, TestData<ResourceKey<TestEnvironmentDefinition>>> tests,
   ResourceKey<Consumer<GameTestHelper>> functionKey,
   Consumer<GameTestHelper> function
) {
   public GeneratedTest(Map<ResourceLocation, TestData<ResourceKey<TestEnvironmentDefinition>>> $$0, ResourceLocation $$1, Consumer<GameTestHelper> $$2) {
      this($$0, ResourceKey.create(Registries.TEST_FUNCTION, $$1), $$2);
   }

   public GeneratedTest(ResourceLocation $$0, TestData<ResourceKey<TestEnvironmentDefinition>> $$1, Consumer<GameTestHelper> $$2) {
      this(Map.of($$0, $$1), $$0, $$2);
   }
}
