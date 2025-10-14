package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.DataVersion;
import org.slf4j.Logger;

public class DetectedVersion {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final WorldVersion BUILT_IN = createBuiltIn(UUID.randomUUID().toString().replaceAll("-", ""), "Development Version");

   public static WorldVersion createBuiltIn(String $$0, String $$1) {
      return createBuiltIn($$0, $$1, true);
   }

   public static WorldVersion createBuiltIn(String $$0, String $$1, boolean $$2) {
      return new WorldVersion.Simple(
         $$0, $$1, new DataVersion(4556, "main"), SharedConstants.getProtocolVersion(), PackFormat.of(69, 0), PackFormat.of(88, 0), new Date(), $$2
      );
   }

   private static WorldVersion createFromJson(JsonObject $$0) {
      JsonObject $$1 = GsonHelper.getAsJsonObject($$0, "pack_version");
      return new WorldVersion.Simple(
         GsonHelper.getAsString($$0, "id"),
         GsonHelper.getAsString($$0, "name"),
         new DataVersion(GsonHelper.getAsInt($$0, "world_version"), GsonHelper.getAsString($$0, "series_id", "main")),
         GsonHelper.getAsInt($$0, "protocol_version"),
         PackFormat.of(GsonHelper.getAsInt($$1, "resource_major"), GsonHelper.getAsInt($$1, "resource_minor")),
         PackFormat.of(GsonHelper.getAsInt($$1, "data_major"), GsonHelper.getAsInt($$1, "data_minor")),
         Date.from(ZonedDateTime.parse(GsonHelper.getAsString($$0, "build_time")).toInstant()),
         GsonHelper.getAsBoolean($$0, "stable")
      );
   }

   public static WorldVersion tryDetectVersion() {
      try {
         InputStream $$0 = DetectedVersion.class.getResourceAsStream("/version.json");

         WorldVersion var9;
         label63: {
            WorldVersion var2;
            try {
               if ($$0 == null) {
                  LOGGER.warn("Missing version information!");
                  var9 = BUILT_IN;
                  break label63;
               }

               InputStreamReader $$1 = new InputStreamReader($$0);

               try {
                  var2 = createFromJson(GsonHelper.parse($$1));
               } catch (Throwable var6) {
                  try {
                     $$1.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }

                  throw var6;
               }

               $$1.close();
            } catch (Throwable var7) {
               if ($$0 != null) {
                  try {
                     $$0.close();
                  } catch (Throwable var4) {
                     var7.addSuppressed(var4);
                  }
               }

               throw var7;
            }

            if ($$0 != null) {
               $$0.close();
            }

            return var2;
         }

         if ($$0 != null) {
            $$0.close();
         }

         return var9;
      } catch (JsonParseException | IOException var8) {
         throw new IllegalStateException("Game version information is corrupt", var8);
      }
   }
}
