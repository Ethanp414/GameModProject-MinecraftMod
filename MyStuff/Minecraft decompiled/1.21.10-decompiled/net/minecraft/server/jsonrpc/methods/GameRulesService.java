package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameRules;

public class GameRulesService {
   public static List<GameRulesService.TypedRule> get(MinecraftApi $$0) {
      List<? extends GameRules.Key<?>> $$1 = $$0.gameRuleService().getAvailableGameRules().map(Entry::getKey).toList();
      List<GameRulesService.TypedRule> $$2 = new ArrayList();

      for(GameRules.Key<?> $$3 : $$1) {
         GameRules.Value<?> $$4 = $$0.gameRuleService().getRule($$3);
         $$2.add(getTypedRule($$0, $$3.getId(), $$4));
      }

      return $$2;
   }

   public static GameRulesService.TypedRule getTypedRule(MinecraftApi $$0, String $$1, GameRules.Value<?> $$2) {
      return $$0.gameRuleService().getTypedRule($$1, $$2);
   }

   public static GameRulesService.TypedRule update(MinecraftApi $$0, GameRulesService.UntypedRule $$1, ClientInfo $$2) {
      return $$0.gameRuleService().updateGameRule($$1, $$2);
   }

   public static enum RuleType implements StringRepresentable {
      INT("integer"),
      BOOL("boolean");

      private final String name;

      private RuleType(final String param3) {
         this.name = $$0;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }

   public static record TypedRule(String key, String value, GameRulesService.RuleType type) {
      public static final MapCodec<GameRulesService.TypedRule> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  Codec.STRING.fieldOf("key").forGetter(GameRulesService.TypedRule::key),
                  Codec.STRING.fieldOf("value").forGetter(GameRulesService.TypedRule::value),
                  StringRepresentable.fromEnum(GameRulesService.RuleType::values).fieldOf("type").forGetter(GameRulesService.TypedRule::type)
               )
               .apply($$0, GameRulesService.TypedRule::new)
      );
   }

   public static record UntypedRule(String key, String value) {
      public static final MapCodec<GameRulesService.UntypedRule> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  Codec.STRING.fieldOf("key").forGetter(GameRulesService.UntypedRule::key),
                  Codec.STRING.fieldOf("value").forGetter(GameRulesService.UntypedRule::value)
               )
               .apply($$0, GameRulesService.UntypedRule::new)
      );
   }
}
