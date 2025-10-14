package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public interface TestEnvironmentDefinition {
   Codec<TestEnvironmentDefinition> DIRECT_CODEC = BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE
      .byNameCodec()
      .dispatch(TestEnvironmentDefinition::codec, $$0 -> $$0);
   Codec<Holder<TestEnvironmentDefinition>> CODEC = RegistryFileCodec.create(Registries.TEST_ENVIRONMENT, DIRECT_CODEC);

   static MapCodec<? extends TestEnvironmentDefinition> bootstrap(Registry<MapCodec<? extends TestEnvironmentDefinition>> $$0) {
      Registry.register($$0, "all_of", TestEnvironmentDefinition.AllOf.CODEC);
      Registry.register($$0, "game_rules", TestEnvironmentDefinition.SetGameRules.CODEC);
      Registry.register($$0, "time_of_day", TestEnvironmentDefinition.TimeOfDay.CODEC);
      Registry.register($$0, "weather", TestEnvironmentDefinition.Weather.CODEC);
      return Registry.register($$0, "function", TestEnvironmentDefinition.Functions.CODEC);
   }

   void setup(ServerLevel var1);

   default void teardown(ServerLevel $$0) {
   }

   MapCodec<? extends TestEnvironmentDefinition> codec();

   public static record AllOf(List<Holder<TestEnvironmentDefinition>> definitions) implements TestEnvironmentDefinition {
      public static final MapCodec<TestEnvironmentDefinition.AllOf> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(TestEnvironmentDefinition.CODEC.listOf().fieldOf("definitions").forGetter(TestEnvironmentDefinition.AllOf::definitions))
               .apply($$0, TestEnvironmentDefinition.AllOf::new)
      );

      public AllOf(TestEnvironmentDefinition... $$0) {
         this(Arrays.stream($$0).map(Holder::direct).toList());
      }

      @Override
      public void setup(ServerLevel $$0) {
         this.definitions.forEach($$1 -> ((TestEnvironmentDefinition)$$1.value()).setup($$0));
      }

      @Override
      public void teardown(ServerLevel $$0) {
         this.definitions.forEach($$1 -> ((TestEnvironmentDefinition)$$1.value()).teardown($$0));
      }

      @Override
      public MapCodec<TestEnvironmentDefinition.AllOf> codec() {
         return CODEC;
      }
   }

   public static record Functions(Optional<ResourceLocation> setupFunction, Optional<ResourceLocation> teardownFunction) implements TestEnvironmentDefinition {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec<TestEnvironmentDefinition.Functions> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  ResourceLocation.CODEC.optionalFieldOf("setup").forGetter(TestEnvironmentDefinition.Functions::setupFunction),
                  ResourceLocation.CODEC.optionalFieldOf("teardown").forGetter(TestEnvironmentDefinition.Functions::teardownFunction)
               )
               .apply($$0, TestEnvironmentDefinition.Functions::new)
      );

      @Override
      public void setup(ServerLevel $$0) {
         this.setupFunction.ifPresent($$1 -> run($$0, $$1));
      }

      @Override
      public void teardown(ServerLevel $$0) {
         this.teardownFunction.ifPresent($$1 -> run($$0, $$1));
      }

      private static void run(ServerLevel $$0, ResourceLocation $$1) {
         MinecraftServer $$2 = $$0.getServer();
         ServerFunctionManager $$3 = $$2.getFunctions();
         Optional<CommandFunction<CommandSourceStack>> $$4 = $$3.get($$1);
         if ($$4.isPresent()) {
            CommandSourceStack $$5 = $$2.createCommandSourceStack().withPermission(2).withSuppressedOutput().withLevel($$0);
            $$3.execute((CommandFunction<CommandSourceStack>)$$4.get(), $$5);
         } else {
            LOGGER.error("Test Batch failed for non-existent function {}", $$1);
         }
      }

      @Override
      public MapCodec<TestEnvironmentDefinition.Functions> codec() {
         return CODEC;
      }
   }

   public static record SetGameRules(
      List<TestEnvironmentDefinition.SetGameRules.Entry<Boolean, GameRules.BooleanValue>> boolRules,
      List<TestEnvironmentDefinition.SetGameRules.Entry<Integer, GameRules.IntegerValue>> intRules
   ) implements TestEnvironmentDefinition {
      public static final MapCodec<TestEnvironmentDefinition.SetGameRules> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  TestEnvironmentDefinition.SetGameRules.Entry.codec(GameRules.BooleanValue.class, Codec.BOOL)
                     .listOf()
                     .fieldOf("bool_rules")
                     .forGetter(TestEnvironmentDefinition.SetGameRules::boolRules),
                  TestEnvironmentDefinition.SetGameRules.Entry.codec(GameRules.IntegerValue.class, Codec.INT)
                     .listOf()
                     .fieldOf("int_rules")
                     .forGetter(TestEnvironmentDefinition.SetGameRules::intRules)
               )
               .apply($$0, TestEnvironmentDefinition.SetGameRules::new)
      );

      @Override
      public void setup(ServerLevel $$0) {
         GameRules $$1 = $$0.getGameRules();
         MinecraftServer $$2 = $$0.getServer();

         for(TestEnvironmentDefinition.SetGameRules.Entry<Boolean, GameRules.BooleanValue> $$3 : this.boolRules) {
            $$1.getRule($$3.key()).set($$3.value(), $$2);
         }

         for(TestEnvironmentDefinition.SetGameRules.Entry<Integer, GameRules.IntegerValue> $$4 : this.intRules) {
            $$1.getRule($$4.key()).set($$4.value(), $$2);
         }
      }

      @Override
      public void teardown(ServerLevel $$0) {
         GameRules $$1 = $$0.getGameRules();
         MinecraftServer $$2 = $$0.getServer();

         for(TestEnvironmentDefinition.SetGameRules.Entry<Boolean, GameRules.BooleanValue> $$3 : this.boolRules) {
            $$1.getRule($$3.key()).setFrom(GameRules.getType($$3.key()).createRule(), $$2);
         }

         for(TestEnvironmentDefinition.SetGameRules.Entry<Integer, GameRules.IntegerValue> $$4 : this.intRules) {
            $$1.getRule($$4.key()).setFrom(GameRules.getType($$4.key()).createRule(), $$2);
         }
      }

      @Override
      public MapCodec<TestEnvironmentDefinition.SetGameRules> codec() {
         return CODEC;
      }

      public static <S, T extends GameRules.Value<T>> TestEnvironmentDefinition.SetGameRules.Entry<S, T> entry(GameRules.Key<T> $$0, S $$1) {
         return new TestEnvironmentDefinition.SetGameRules.Entry<>($$0, $$1);
      }

      public static record Entry<S, T extends GameRules.Value<T>>(GameRules.Key<T> key, S value) {
         public static <S, T extends GameRules.Value<T>> Codec<TestEnvironmentDefinition.SetGameRules.Entry<S, T>> codec(Class<T> $$0, Codec<S> $$1) {
            return RecordCodecBuilder.create(
               $$2 -> $$2.group(
                        GameRules.keyCodec($$0).fieldOf("rule").forGetter(TestEnvironmentDefinition.SetGameRules.Entry::key),
                        $$1.fieldOf("value").forGetter(TestEnvironmentDefinition.SetGameRules.Entry::value)
                     )
                     .apply($$2, TestEnvironmentDefinition.SetGameRules.Entry::new)
            );
         }
      }
   }

   public static record TimeOfDay(int time) implements TestEnvironmentDefinition {
      public static final MapCodec<TestEnvironmentDefinition.TimeOfDay> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("time").forGetter(TestEnvironmentDefinition.TimeOfDay::time))
               .apply($$0, TestEnvironmentDefinition.TimeOfDay::new)
      );

      @Override
      public void setup(ServerLevel $$0) {
         $$0.setDayTime((long)this.time);
      }

      @Override
      public MapCodec<TestEnvironmentDefinition.TimeOfDay> codec() {
         return CODEC;
      }
   }

   public static record Weather(TestEnvironmentDefinition.Weather.Type weather) implements TestEnvironmentDefinition {
      public static final MapCodec<TestEnvironmentDefinition.Weather> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(TestEnvironmentDefinition.Weather.Type.CODEC.fieldOf("weather").forGetter(TestEnvironmentDefinition.Weather::weather))
               .apply($$0, TestEnvironmentDefinition.Weather::new)
      );

      @Override
      public void setup(ServerLevel $$0) {
         this.weather.apply($$0);
      }

      @Override
      public void teardown(ServerLevel $$0) {
         $$0.resetWeatherCycle();
      }

      @Override
      public MapCodec<TestEnvironmentDefinition.Weather> codec() {
         return CODEC;
      }

      public static enum Type implements StringRepresentable {
         CLEAR("clear", 100000, 0, false, false),
         RAIN("rain", 0, 100000, true, false),
         THUNDER("thunder", 0, 100000, true, true);

         public static final Codec<TestEnvironmentDefinition.Weather.Type> CODEC = StringRepresentable.fromEnum(TestEnvironmentDefinition.Weather.Type::values);
         private final String id;
         private final int clearTime;
         private final int rainTime;
         private final boolean raining;
         private final boolean thundering;

         private Type(final String param3, final int param4, final int param5, final boolean param6, final boolean param7) {
            this.id = $$0;
            this.clearTime = $$1;
            this.rainTime = $$2;
            this.raining = $$3;
            this.thundering = $$4;
         }

         void apply(ServerLevel $$0) {
            $$0.setWeatherParameters(this.clearTime, this.rainTime, this.raining, this.thundering);
         }

         @Override
         public String getSerializedName() {
            return this.id;
         }
      }
   }
}
