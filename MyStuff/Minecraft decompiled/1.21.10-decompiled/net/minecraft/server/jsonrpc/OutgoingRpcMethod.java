package net.minecraft.server.jsonrpc;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.methods.IllegalMethodDefinitionException;

public interface OutgoingRpcMethod<Params, Result> {
   String NOTIFICATION_PREFIX = "notification/";

   MethodInfo info();

   OutgoingRpcMethod.Attributes attributes();

   @Nullable
   default JsonElement encodeParams(Params $$0) {
      return null;
   }

   @Nullable
   default Result decodeResult(JsonElement $$0) {
      return null;
   }

   static OutgoingRpcMethod.OutgoingRpcMethodBuilder<OutgoingRpcMethod.ParmeterlessNotification> notification() {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder<>(($$0, $$1) -> {
         if ($$0.params().isPresent()) {
            throw new IllegalMethodDefinitionException("Method defined as not having parameters but is describing them");
         } else if ($$0.result().isPresent()) {
            throw new IllegalMethodDefinitionException("Method defined as not having result but is describing it");
         } else {
            return new OutgoingRpcMethod.ParmeterlessNotification($$0, $$1);
         }
      });
   }

   static <Params> OutgoingRpcMethod.OutgoingRpcMethodBuilder<OutgoingRpcMethod.Notification<Params>> notification(Codec<Params> $$0) {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder<>(($$1, $$2) -> {
         if ($$1.params().isEmpty()) {
            throw new IllegalMethodDefinitionException("Method defined as having parameters without describing them");
         } else if ($$1.result().isPresent()) {
            throw new IllegalMethodDefinitionException("Method defined as not having result but is describing it");
         } else {
            return new OutgoingRpcMethod.Notification<>($$1, $$2, $$0);
         }
      });
   }

   static <Result> OutgoingRpcMethod.OutgoingRpcMethodBuilder<OutgoingRpcMethod.ParameterlessMethod<Result>> request(Codec<Result> $$0) {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder<>(($$1, $$2) -> {
         if ($$1.params().isPresent()) {
            throw new IllegalMethodDefinitionException("Method defined as not having parameters but is describing them");
         } else if ($$1.result().isEmpty()) {
            throw new IllegalMethodDefinitionException("Method lacks result");
         } else {
            return new OutgoingRpcMethod.ParameterlessMethod<>($$1, $$2, $$0);
         }
      });
   }

   static <Params, Result> OutgoingRpcMethod.OutgoingRpcMethodBuilder<OutgoingRpcMethod.Method<Params, Result>> request(Codec<Params> $$0, Codec<Result> $$1) {
      return new OutgoingRpcMethod.OutgoingRpcMethodBuilder<>(($$2, $$3) -> {
         if ($$2.params().isEmpty()) {
            throw new IllegalMethodDefinitionException("Method defined as having parameters without describing them");
         } else if ($$2.result().isEmpty()) {
            throw new IllegalMethodDefinitionException("Method lacks result");
         } else {
            return new OutgoingRpcMethod.Method<>($$2, $$3, $$0, $$1);
         }
      });
   }

   public static record Attributes(boolean discoverable) {
   }

   @FunctionalInterface
   public interface Factory<T extends OutgoingRpcMethod<?, ?>> {
      T create(MethodInfo var1, OutgoingRpcMethod.Attributes var2);
   }

   public static record Method<Params, Result>(MethodInfo info, OutgoingRpcMethod.Attributes attributes, Codec<Params> paramsCodec, Codec<Result> resultCodec)
      implements OutgoingRpcMethod<Params, Result> {
      @Nullable
      @Override
      public JsonElement encodeParams(Params $$0) {
         return this.paramsCodec.encodeStart(JsonOps.INSTANCE, $$0).getOrThrow();
      }

      @Override
      public Result decodeResult(JsonElement $$0) {
         return this.resultCodec.parse(JsonOps.INSTANCE, $$0).getOrThrow();
      }
   }

   public static record Notification<Params>(MethodInfo info, OutgoingRpcMethod.Attributes attributes, Codec<Params> paramsCodec)
      implements OutgoingRpcMethod<Params, Void> {
      @Nullable
      @Override
      public JsonElement encodeParams(Params $$0) {
         return this.paramsCodec.encodeStart(JsonOps.INSTANCE, $$0).getOrThrow();
      }
   }

   public static class OutgoingRpcMethodBuilder<T extends OutgoingRpcMethod<?, ?>> {
      public static final OutgoingRpcMethod.Attributes DEFAULT_ATTRIBUTES = new OutgoingRpcMethod.Attributes(true);
      private final OutgoingRpcMethod.Factory<T> method;
      private String description = "";
      @Nullable
      private ParamInfo paramInfo;
      @Nullable
      private ResultInfo resultInfo;

      public OutgoingRpcMethodBuilder(OutgoingRpcMethod.Factory<T> $$0) {
         this.method = $$0;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder<T> description(String $$0) {
         this.description = $$0;
         return this;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder<T> response(ResultInfo $$0) {
         this.resultInfo = $$0;
         return this;
      }

      public OutgoingRpcMethod.OutgoingRpcMethodBuilder<T> param(ParamInfo $$0) {
         this.paramInfo = $$0;
         return this;
      }

      private T build() {
         MethodInfo $$0 = new MethodInfo(this.description, this.paramInfo, this.resultInfo);
         return this.method.create($$0, DEFAULT_ATTRIBUTES);
      }

      public Holder.Reference<T> register(String $$0) {
         return this.register(ResourceLocation.withDefaultNamespace("notification/" + $$0));
      }

      private Holder.Reference<T> register(ResourceLocation $$0) {
         return Registry.registerForHolder(BuiltInRegistries.OUTGOING_RPC_METHOD, $$0, this.build());
      }
   }

   public static record ParameterlessMethod<Result>(MethodInfo info, OutgoingRpcMethod.Attributes attributes, Codec<Result> resultCodec)
      implements OutgoingRpcMethod<Void, Result> {
      @Override
      public Result decodeResult(JsonElement $$0) {
         return this.resultCodec.parse(JsonOps.INSTANCE, $$0).getOrThrow();
      }
   }

   public static record ParmeterlessNotification(MethodInfo info, OutgoingRpcMethod.Attributes attributes) implements OutgoingRpcMethod<Void, Void> {
   }
}
