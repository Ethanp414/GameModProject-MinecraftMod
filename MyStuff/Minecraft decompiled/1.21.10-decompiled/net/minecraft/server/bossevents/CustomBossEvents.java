package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class CustomBossEvents {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec<Map<ResourceLocation, CustomBossEvent.Packed>> EVENTS_CODEC = Codec.unboundedMap(
      ResourceLocation.CODEC, CustomBossEvent.Packed.CODEC
   );
   private final Map<ResourceLocation, CustomBossEvent> events = Maps.<ResourceLocation, CustomBossEvent>newHashMap();

   @Nullable
   public CustomBossEvent get(ResourceLocation $$0) {
      return (CustomBossEvent)this.events.get($$0);
   }

   public CustomBossEvent create(ResourceLocation $$0, Component $$1) {
      CustomBossEvent $$2 = new CustomBossEvent($$0, $$1);
      this.events.put($$0, $$2);
      return $$2;
   }

   public void remove(CustomBossEvent $$0) {
      this.events.remove($$0.getTextId());
   }

   public Collection<ResourceLocation> getIds() {
      return this.events.keySet();
   }

   public Collection<CustomBossEvent> getEvents() {
      return this.events.values();
   }

   public CompoundTag save(HolderLookup.Provider $$0) {
      Map<ResourceLocation, CustomBossEvent.Packed> $$1 = Util.mapValues(this.events, CustomBossEvent::pack);
      return EVENTS_CODEC.encodeStart($$0.createSerializationContext(NbtOps.INSTANCE), $$1).getOrThrow();
   }

   public void load(CompoundTag $$0, HolderLookup.Provider $$1) {
      Map<ResourceLocation, CustomBossEvent.Packed> $$2 = (Map)EVENTS_CODEC.parse($$1.createSerializationContext(NbtOps.INSTANCE), $$0)
         .resultOrPartial($$0x -> LOGGER.error("Failed to parse boss bar events: {}", $$0x))
         .orElse(Map.of());
      $$2.forEach(($$0x, $$1x) -> this.events.put($$0x, CustomBossEvent.load($$0x, $$1x)));
   }

   public void onPlayerConnect(ServerPlayer $$0) {
      for(CustomBossEvent $$1 : this.events.values()) {
         $$1.onPlayerConnect($$0);
      }
   }

   public void onPlayerDisconnect(ServerPlayer $$0) {
      for(CustomBossEvent $$1 : this.events.values()) {
         $$1.onPlayerDisconnect($$0);
      }
   }
}
