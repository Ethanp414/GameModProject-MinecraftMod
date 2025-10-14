package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ServerFunctionManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation TICK_FUNCTION_TAG = ResourceLocation.withDefaultNamespace("tick");
   private static final ResourceLocation LOAD_FUNCTION_TAG = ResourceLocation.withDefaultNamespace("load");
   private final MinecraftServer server;
   private List<CommandFunction<CommandSourceStack>> ticking = ImmutableList.of();
   private boolean postReload;
   private ServerFunctionLibrary library;

   public ServerFunctionManager(MinecraftServer $$0, ServerFunctionLibrary $$1) {
      this.server = $$0;
      this.library = $$1;
      this.postReload($$1);
   }

   public CommandDispatcher<CommandSourceStack> getDispatcher() {
      return this.server.getCommands().getDispatcher();
   }

   public void tick() {
      if (this.server.tickRateManager().runsNormally()) {
         if (this.postReload) {
            this.postReload = false;
            Collection<CommandFunction<CommandSourceStack>> $$0 = this.library.getTag(LOAD_FUNCTION_TAG);
            this.executeTagFunctions($$0, LOAD_FUNCTION_TAG);
         }

         this.executeTagFunctions(this.ticking, TICK_FUNCTION_TAG);
      }
   }

   private void executeTagFunctions(Collection<CommandFunction<CommandSourceStack>> $$0, ResourceLocation $$1) {
      Profiler.get().push($$1::toString);

      for(CommandFunction<CommandSourceStack> $$2 : $$0) {
         this.execute($$2, this.getGameLoopSender());
      }

      Profiler.get().pop();
   }

   public void execute(CommandFunction<CommandSourceStack> $$0, CommandSourceStack $$1) {
      ProfilerFiller $$2 = Profiler.get();
      $$2.push((Supplier<String>)(() -> "function " + $$0.id()));

      try {
         InstantiatedFunction<CommandSourceStack> $$3 = $$0.instantiate(null, this.getDispatcher());
         Commands.executeCommandInContext($$1, $$2x -> ExecutionContext.queueInitialFunctionCall($$2x, $$3, $$1, CommandResultCallback.EMPTY));
      } catch (FunctionInstantiationException var9) {
      } catch (Exception var10) {
         LOGGER.warn("Failed to execute function {}", $$0.id(), var10);
      } finally {
         $$2.pop();
      }
   }

   public void replaceLibrary(ServerFunctionLibrary $$0) {
      this.library = $$0;
      this.postReload($$0);
   }

   private void postReload(ServerFunctionLibrary $$0) {
      this.ticking = List.copyOf($$0.getTag(TICK_FUNCTION_TAG));
      this.postReload = true;
   }

   public CommandSourceStack getGameLoopSender() {
      return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
   }

   public Optional<CommandFunction<CommandSourceStack>> get(ResourceLocation $$0) {
      return this.library.getFunction($$0);
   }

   public List<CommandFunction<CommandSourceStack>> getTag(ResourceLocation $$0) {
      return this.library.getTag($$0);
   }

   public Iterable<ResourceLocation> getFunctionNames() {
      return this.library.getFunctions().keySet();
   }

   public Iterable<ResourceLocation> getTagNames() {
      return this.library.getAvailableTags();
   }
}
