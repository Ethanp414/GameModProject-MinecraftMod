package net.minecraft.gametest.framework;

import java.util.Collection;
import net.minecraft.core.Holder;

public record GameTestBatch(int index, Collection<GameTestInfo> gameTestInfos, Holder<TestEnvironmentDefinition> environment) {
   public GameTestBatch(int param1, Collection<GameTestInfo> param2, Holder<TestEnvironmentDefinition> param3) {
      if ($$1.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
      } else {
         this.index = $$0;
         this.gameTestInfos = $$1;
         this.environment = $$2;
      }
   }
}
