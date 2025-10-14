package net.minecraft.server.jsonrpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.Locale;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.EncodeJsonRpcException;
import net.minecraft.server.jsonrpc.methods.IllegalMethodDefinitionException;
import net.minecraft.server.jsonrpc.methods.InvalidParameterJsonRpcException;

public interface IncomingRpcMethod {
   MethodInfo info();

   IncomingRpcMethod.Attributes attributes();

   JsonElement apply(MinecraftApi var1, @Nullable JsonElement var2, ClientInfo var3);

   static <Result> IncomingRpcMethod.IncomingRpcMethodBuilder<IncomingRpcMethod.ParameterlessMethod<Result>> method(
      IncomingRpcMethod.ParameterlessRpcMethodFunction<Result> $$0, Codec<Result> $$1
   ) {
      return new IncomingRpcMethod.IncomingRpcMethodBuilder<>(($$2, $$3) -> {
         if ($$2.params().isPresent()) {
            throw new IllegalMethodDefinitionException("Method defined as not having parameters but is describing them");
         } else if ($$2.result().isEmpty()) {
            throw new IllegalMethodDefinitionException("Method lacks result");
         } else {
            return new IncomingRpcMethod.ParameterlessMethod<>($$2, $$3, $$1, $$0);
         }
      });
   }

   static <Params, Result> IncomingRpcMethod.IncomingRpcMethodBuilder<IncomingRpcMethod.Method<Params, Result>> method(
      IncomingRpcMethod.RpcMethodFunction<Params, Result> $$0, Codec<Params> $$1, Codec<Result> $$2
   ) {
      return new IncomingRpcMethod.IncomingRpcMethodBuilder<>(($$3, $$4) -> {
         if ($$3.params().isEmpty()) {
            throw new IllegalMethodDefinitionException("Method defined as having parameters without describing them");
         } else if ($$3.result().isEmpty()) {
            throw new IllegalMethodDefinitionException("Method lacks result");
         } else {
            return new IncomingRpcMethod.Method<>($$3, $$4, $$1, $$2, $$0);
         }
      });
   }

   static <Result> IncomingRpcMethod.IncomingRpcMethodBuilder<IncomingRpcMethod.ParameterlessMethod<Result>> method(
      Function<MinecraftApi, Result> $$0, Codec<Result> $$1
   ) {
      return new IncomingRpcMethod.IncomingRpcMethodBuilder<>(($$2, $$3) -> {
         if ($$2.params().isPresent()) {
            throw new IllegalMethodDefinitionException("Method defined as not having parameters but is describing them");
         } else if ($$2.result().isEmpty()) {
            throw new IllegalMethodDefinitionException("Method lacks result");
         } else {
            return new IncomingRpcMethod.ParameterlessMethod<>($$2, $$3, $$1, ($$1xx, $$2x) -> (Result)$$0.apply($$1xx));
         }
      });
   }

   public static record Attributes(boolean runOnMainThread, boolean discoverable) {
   }

   @FunctionalInterface
   public interface Factory<T extends IncomingRpcMethod> {
      T create(MethodInfo var1, IncomingRpcMethod.Attributes var2);
   }

   public static class IncomingRpcMethodBuilder<T extends IncomingRpcMethod> {
      private final IncomingRpcMethod.Factory<T> method;
      private String description = "";
      @Nullable
      private ParamInfo paramInfo;
      @Nullable
      private ResultInfo resultInfo;
      private boolean discoverable = true;
      private boolean runOnMainThread = true;

      public IncomingRpcMethodBuilder(IncomingRpcMethod.Factory<T> $$0) {
         this.method = $$0;
      }

      public IncomingRpcMethod.IncomingRpcMethodBuilder<T> description(String $$0) {
         this.description = $$0;
         return this;
      }

      public IncomingRpcMethod.IncomingRpcMethodBuilder<T> response(ResultInfo $$0) {
         this.resultInfo = $$0;
         return this;
      }

      public IncomingRpcMethod.IncomingRpcMethodBuilder<T> param(ParamInfo $$0) {
         this.paramInfo = $$0;
         return this;
      }

      public IncomingRpcMethod.IncomingRpcMethodBuilder<T> undiscoverable() {
         this.discoverable = false;
         return this;
      }

      public IncomingRpcMethod.IncomingRpcMethodBuilder<T> notOnMainThread() {
         this.runOnMainThread = false;
         return this;
      }

      public T build() {
         MethodInfo $$0 = new MethodInfo(this.description, this.paramInfo, this.resultInfo);
         return this.method.create($$0, new IncomingRpcMethod.Attributes(this.runOnMainThread, this.discoverable));
      }

      public T register(Registry<IncomingRpcMethod> $$0, String $$1) {
         return this.register($$0, ResourceLocation.withDefaultNamespace($$1));
      }

      private T register(Registry<IncomingRpcMethod> $$0, ResourceLocation $$1) {
         return Registry.register($$0, $$1, this.build());
      }
   }

   public static record Method<Params, Result>(
      MethodInfo info,
      IncomingRpcMethod.Attributes attributes,
      Codec<Params> paramsCodec,
      Codec<Result> resultCodec,
      IncomingRpcMethod.RpcMethodFunction<Params, Result> function
   ) implements IncomingRpcMethod {
      @Override
      public JsonElement apply(MinecraftApi $$0, @Nullable JsonElement $$1, ClientInfo $$2) {
         if ($$1 != null && ($$1.isJsonArray() || $$1.isJsonObject())) {
            if (this.info.params().isEmpty()) {
               throw new IllegalArgumentException("Method defined as having parameters without describing them");
            } else {
               JsonElement $$5;
               if ($$1.isJsonObject()) {
                  String $$3 = ((ParamInfo)this.info.params().get()).name();
                  JsonElement $$4 = $$1.getAsJsonObject().get($$3);
                  if ($$4 == null) {
                     throw new InvalidParameterJsonRpcException(
                        String.format(Locale.ROOT, "Params passed by-name, but expected param [%s] does not exist", $$3)
                     );
                  }

                  $$5 = $$4;
               } else {
                  JsonArray $$6 = $$1.getAsJsonArray();
                  if ($$6.isEmpty() || $$6.size() > 1) {
                     throw new InvalidParameterJsonRpcException("Expected exactly one element in the params array");
                  }

                  $$5 = $$6.get(0);
               }

               Params $$8 = this.paramsCodec.parse(JsonOps.INSTANCE, $$5).getOrThrow(InvalidParameterJsonRpcException::new);
               Result $$9 = this.function.apply($$0, $$8, $$2);
               return this.resultCodec.encodeStart(JsonOps.INSTANCE, $$9).getOrThrow(EncodeJsonRpcException::new);
            }
         } else {
            throw new InvalidParameterJsonRpcException("Expected params as array or named");
         }
      }
   }

   public static record ParameterlessMethod<Result>(
      MethodInfo info, IncomingRpcMethod.Attributes attributes, Codec<Result> resultCodec, IncomingRpcMethod.ParameterlessRpcMethodFunction<Result> supplier
   ) implements IncomingRpcMethod {
      @Override
      public JsonElement apply(MinecraftApi $$0, @Nullable JsonElement $$1, ClientInfo $$2) {
         if ($$1 == null || $$1.isJsonArray() && $$1.getAsJsonArray().isEmpty()) {
            if (this.info.params().isPresent()) {
               throw new IllegalArgumentException("Method defined as not having parameters but is describing them");
            } else {
               Result $$3 = this.supplier.apply($$0, $$2);
               return this.resultCodec.encodeStart(JsonOps.INSTANCE, $$3).getOrThrow(InvalidParameterJsonRpcException::new);
            }
         } else {
            throw new InvalidParameterJsonRpcException("Expected no params, or an empty array");
         }
      }
   }

   @FunctionalInterface
   public interface ParameterlessRpcMethodFunction<Result> {
      Result apply(MinecraftApi var1, ClientInfo var2);
   }

   @FunctionalInterface
   public interface RpcMethodFunction<Params, Result> {
      Result apply(MinecraftApi var1, Params var2, ClientInfo var3);
   }
}
