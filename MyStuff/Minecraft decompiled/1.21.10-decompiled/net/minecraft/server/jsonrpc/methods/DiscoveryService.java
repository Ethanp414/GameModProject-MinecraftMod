package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.OutgoingRpcMethod;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.api.SchemaComponent;

public class DiscoveryService {
   public static DiscoveryService.DiscoverResponse discover(List<SchemaComponent> $$0) {
      List<MethodInfo.Named> $$1 = new ArrayList(BuiltInRegistries.INCOMING_RPC_METHOD.size() + BuiltInRegistries.OUTGOING_RPC_METHOD.size());
      BuiltInRegistries.INCOMING_RPC_METHOD.listElements().forEach($$1x -> {
         if (((IncomingRpcMethod)$$1x.value()).attributes().discoverable()) {
            $$1.add(((IncomingRpcMethod)$$1x.value()).info().named($$1x.key().location()));
         }
      });
      BuiltInRegistries.OUTGOING_RPC_METHOD.listElements().forEach($$1x -> {
         if (((OutgoingRpcMethod)$$1x.value()).attributes().discoverable()) {
            $$1.add(((OutgoingRpcMethod)$$1x.value()).info().named($$1x.key().location()));
         }
      });
      Map<String, Schema> $$2 = new HashMap();

      for(SchemaComponent $$3 : $$0) {
         $$2.put($$3.name(), $$3.schema());
      }

      DiscoveryService.DiscoverInfo $$4 = new DiscoveryService.DiscoverInfo("Minecraft Server JSON-RPC", "1.0.0");
      return new DiscoveryService.DiscoverResponse("1.3.2", $$4, $$1, new DiscoveryService.DiscoverComponents($$2));
   }

   public static record DiscoverComponents(Map<String, Schema> schemas) {
      public static final MapCodec<DiscoveryService.DiscoverComponents> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(Codec.unboundedMap(Codec.STRING, Schema.CODEC).fieldOf("schemas").forGetter(DiscoveryService.DiscoverComponents::schemas))
               .apply($$0, DiscoveryService.DiscoverComponents::new)
      );
   }

   public static record DiscoverInfo(String title, String version) {
      public static final MapCodec<DiscoveryService.DiscoverInfo> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  Codec.STRING.fieldOf("title").forGetter(DiscoveryService.DiscoverInfo::title),
                  Codec.STRING.fieldOf("version").forGetter(DiscoveryService.DiscoverInfo::version)
               )
               .apply($$0, DiscoveryService.DiscoverInfo::new)
      );
   }

   public static record DiscoverResponse(
      String jsonRpcProtocolVersion,
      DiscoveryService.DiscoverInfo discoverInfo,
      List<MethodInfo.Named> methods,
      DiscoveryService.DiscoverComponents components
   ) {
      public static final MapCodec<DiscoveryService.DiscoverResponse> CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  Codec.STRING.fieldOf("openrpc").forGetter(DiscoveryService.DiscoverResponse::jsonRpcProtocolVersion),
                  DiscoveryService.DiscoverInfo.CODEC.codec().fieldOf("info").forGetter(DiscoveryService.DiscoverResponse::discoverInfo),
                  Codec.list(MethodInfo.Named.CODEC).fieldOf("methods").forGetter(DiscoveryService.DiscoverResponse::methods),
                  DiscoveryService.DiscoverComponents.CODEC.codec().fieldOf("components").forGetter(DiscoveryService.DiscoverResponse::components)
               )
               .apply($$0, DiscoveryService.DiscoverResponse::new)
      );
   }
}
