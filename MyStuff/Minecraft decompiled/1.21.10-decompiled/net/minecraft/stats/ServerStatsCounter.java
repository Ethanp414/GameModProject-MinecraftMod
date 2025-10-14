package net.minecraft.stats;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatsCounter extends StatsCounter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec<Map<Stat<?>, Integer>> STATS_CODEC = Codec.dispatchedMap(
         BuiltInRegistries.STAT_TYPE.byNameCodec(), Util.memoize(ServerStatsCounter::createTypedStatsCodec)
      )
      .xmap($$0 -> {
         Map<Stat<?>, Integer> $$1 = new HashMap();
         $$0.forEach(($$1x, $$2) -> $$1.putAll($$2));
         return $$1;
      }, $$0 -> (Map)$$0.entrySet().stream().collect(Collectors.groupingBy($$0x -> ((Stat)$$0x.getKey()).getType(), Util.toMap())));
   private final MinecraftServer server;
   private final File file;
   private final Set<Stat<?>> dirty = Sets.<Stat<?>>newHashSet();

   private static <T> Codec<Map<Stat<?>, Integer>> createTypedStatsCodec(StatType<T> $$0) {
      Codec<T> $$1 = $$0.getRegistry().byNameCodec();
      Codec<Stat<?>> $$2 = $$1.flatComapMap(
         $$0::get,
         $$1x -> $$1x.getType() == $$0 ? DataResult.success($$1x.getValue()) : DataResult.error(() -> "Expected type " + $$0 + ", but got " + $$1x.getType())
      );
      return Codec.unboundedMap($$2, Codec.INT);
   }

   public ServerStatsCounter(MinecraftServer $$0, File $$1) {
      this.server = $$0;
      this.file = $$1;
      if ($$1.isFile()) {
         try {
            this.parseLocal($$0.getFixerUpper(), FileUtils.readFileToString($$1));
         } catch (IOException var4) {
            LOGGER.error("Couldn't read statistics file {}", $$1, var4);
         } catch (JsonParseException var5) {
            LOGGER.error("Couldn't parse statistics file {}", $$1, var5);
         }
      }
   }

   public void save() {
      try {
         FileUtils.writeStringToFile(this.file, this.toJson());
      } catch (IOException var2) {
         LOGGER.error("Couldn't save stats", var2);
      }
   }

   @Override
   public void setValue(Player $$0, Stat<?> $$1, int $$2) {
      super.setValue($$0, $$1, $$2);
      this.dirty.add($$1);
   }

   private Set<Stat<?>> getDirty() {
      Set<Stat<?>> $$0 = Sets.<Stat<?>>newHashSet(this.dirty);
      this.dirty.clear();
      return $$0;
   }

   public void parseLocal(DataFixer $$0, String $$1) {
      try {
         JsonElement $$2 = StrictJsonParser.parse($$1);
         if ($$2.isJsonNull()) {
            LOGGER.error("Unable to parse Stat data from {}", this.file);
            return;
         }

         Dynamic<JsonElement> $$3 = new Dynamic<>(JsonOps.INSTANCE, $$2);
         $$3 = DataFixTypes.STATS.updateToCurrentVersion($$0, $$3, NbtUtils.getDataVersion($$3, 1343));
         this.stats
            .putAll(
               (Map)STATS_CODEC.parse($$3.get("stats").orElseEmptyMap())
                  .resultOrPartial($$0x -> LOGGER.error("Failed to parse statistics for {}: {}", this.file, $$0x))
                  .orElse(Map.of())
            );
      } catch (JsonParseException var5) {
         LOGGER.error("Unable to parse Stat data from {}", this.file, var5);
      }
   }

   protected String toJson() {
      JsonObject $$0 = new JsonObject();
      $$0.add("stats", STATS_CODEC.encodeStart(JsonOps.INSTANCE, this.stats).getOrThrow());
      $$0.addProperty("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());
      return $$0.toString();
   }

   public void markAllDirty() {
      this.dirty.addAll(this.stats.keySet());
   }

   public void sendStats(ServerPlayer $$0) {
      Object2IntMap<Stat<?>> $$1 = new Object2IntOpenHashMap<>();

      for(Stat<?> $$2 : this.getDirty()) {
         $$1.put($$2, this.getValue($$2));
      }

      $$0.connection.send(new ClientboundAwardStatsPacket($$1));
   }
}
