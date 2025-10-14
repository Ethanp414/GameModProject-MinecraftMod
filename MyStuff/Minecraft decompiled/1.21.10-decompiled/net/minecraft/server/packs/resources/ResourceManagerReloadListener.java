package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener extends PreparableReloadListener {
   @Override
   default CompletableFuture<Void> reload(
      PreparableReloadListener.SharedState $$0, Executor $$1, PreparableReloadListener.PreparationBarrier $$2, Executor $$3
   ) {
      ResourceManager $$4 = $$0.resourceManager();
      return $$2.wait(Unit.INSTANCE).thenRunAsync(() -> {
         ProfilerFiller $$1xx = Profiler.get();
         $$1xx.push("listener");
         this.onResourceManagerReload($$4);
         $$1xx.pop();
      }, $$3);
   }

   void onResourceManagerReload(ResourceManager var1);
}
