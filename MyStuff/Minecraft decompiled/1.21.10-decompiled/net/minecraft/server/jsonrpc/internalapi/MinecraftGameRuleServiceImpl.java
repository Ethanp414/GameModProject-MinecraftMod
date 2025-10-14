package net.minecraft.server.jsonrpc.internalapi;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.InvalidParameterJsonRpcException;
import net.minecraft.world.flag.FeatureFlagSet;

public class MinecraftGameRuleServiceImpl implements MinecraftGameRuleService {
   private final DedicatedServer server;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftGameRuleServiceImpl(DedicatedServer $$0, JsonRpcLogger $$1) {
      this.server = $$0;
      this.jsonrpcLogger = $$1;
   }

   @Override
   public GameRulesService.TypedRule updateGameRule(GameRulesService.UntypedRule $$0, ClientInfo $$1) {
      net.minecraft.world.level.GameRules.Value<?> $$2 = this.getRuleValue($$0.key());
      String $$3 = $$2.serialize();
      if ($$2 instanceof net.minecraft.world.level.GameRules.BooleanValue $$4) {
         $$4.set(Boolean.parseBoolean($$0.value()), this.server);
      } else {
         if (!($$2 instanceof net.minecraft.world.level.GameRules.IntegerValue)) {
            throw new InvalidParameterJsonRpcException("Unknown rule type for key: " + $$0.key());
         }

         net.minecraft.world.level.GameRules.IntegerValue $$5 = (net.minecraft.world.level.GameRules.IntegerValue)$$2;
         $$5.set(Integer.parseInt($$0.value()), this.server);
      }

      GameRulesService.TypedRule $$6 = this.getTypedRule($$0.key(), $$2);
      this.jsonrpcLogger.log($$1, "Game rule '{}' updated from '{}' to '{}'", $$6.key(), $$3, $$6.value());
      this.server.onGameRuleChanged($$0.key(), $$2);
      return $$6;
   }

   @Override
   public <T extends net.minecraft.world.level.GameRules.Value<T>> T getRule(net.minecraft.world.level.GameRules.Key<T> $$0) {
      return this.server.getGameRules().getRule($$0);
   }

   @Override
   public GameRulesService.TypedRule getTypedRule(String $$0, net.minecraft.world.level.GameRules.Value<?> $$1) {
      Objects.requireNonNull($$1);
      byte var4 = 0;
      GameRulesService.TypedRule var10000;
      switch(SwitchBootstraps.typeSwitch<"typeSwitch",net.minecraft.world.level.GameRules.BooleanValue,net.minecraft.world.level.GameRules.IntegerValue>(
         $$1, var4
      )) {
         case 0:
            net.minecraft.world.level.GameRules.BooleanValue $$2 = (net.minecraft.world.level.GameRules.BooleanValue)$$1;
            var10000 = new GameRulesService.TypedRule($$0, String.valueOf($$2.get()), GameRulesService.RuleType.BOOL);
            break;
         case 1:
            net.minecraft.world.level.GameRules.IntegerValue $$3 = (net.minecraft.world.level.GameRules.IntegerValue)$$1;
            var10000 = new GameRulesService.TypedRule($$0, String.valueOf($$3.get()), GameRulesService.RuleType.INT);
            break;
         default:
            throw new InvalidParameterJsonRpcException("Unknown rule type");
      }

      return var10000;
   }

   @Override
   public Stream<Entry<net.minecraft.world.level.GameRules.Key<?>, net.minecraft.world.level.GameRules.Type<?>>> getAvailableGameRules() {
      FeatureFlagSet $$0 = this.server.getWorldData().getLevelSettings().getDataConfiguration().enabledFeatures();
      return net.minecraft.world.level.GameRules.availableRules($$0);
   }

   private Optional<net.minecraft.world.level.GameRules.Key<?>> getRuleKey(String $$0) {
      Stream<Entry<net.minecraft.world.level.GameRules.Key<?>, net.minecraft.world.level.GameRules.Type<?>>> $$1 = this.getAvailableGameRules();
      return $$1.filter($$1x -> ((net.minecraft.world.level.GameRules.Key)$$1x.getKey()).getId().equals($$0)).findFirst().map(Entry::getKey);
   }

   private net.minecraft.world.level.GameRules.Value<?> getRuleValue(String $$0) {
      net.minecraft.world.level.GameRules.Key<?> $$1 = (net.minecraft.world.level.GameRules.Key)this.getRuleKey($$0)
         .orElseThrow(() -> new InvalidParameterJsonRpcException("Game rule '" + $$0 + "' does not exist"));
      return this.server.getGameRules().getRule($$1);
   }
}
