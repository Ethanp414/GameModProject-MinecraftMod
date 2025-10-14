package net.minecraft.locale;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

public record DeprecatedTranslationsInfo(List<String> removed, Map<String, String> renamed) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final DeprecatedTranslationsInfo EMPTY = new DeprecatedTranslationsInfo(List.of(), Map.of());
   public static final Codec<DeprecatedTranslationsInfo> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               Codec.STRING.listOf().fieldOf("removed").forGetter(DeprecatedTranslationsInfo::removed),
               Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("renamed").forGetter(DeprecatedTranslationsInfo::renamed)
            )
            .apply($$0, DeprecatedTranslationsInfo::new)
   );

   public static DeprecatedTranslationsInfo loadFromJson(InputStream $$0) {
      JsonElement $$1 = StrictJsonParser.parse(new InputStreamReader($$0, StandardCharsets.UTF_8));
      return (DeprecatedTranslationsInfo)CODEC.parse(JsonOps.INSTANCE, $$1)
         .getOrThrow($$0x -> new IllegalStateException("Failed to parse deprecated language data: " + $$0x));
   }

   public static DeprecatedTranslationsInfo loadFromResource(String $$0) {
      try {
         InputStream $$1 = Language.class.getResourceAsStream($$0);

         DeprecatedTranslationsInfo var2;
         label49: {
            try {
               if ($$1 != null) {
                  var2 = loadFromJson($$1);
                  break label49;
               }
            } catch (Throwable var5) {
               if ($$1 != null) {
                  try {
                     $$1.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if ($$1 != null) {
               $$1.close();
            }

            return EMPTY;
         }

         if ($$1 != null) {
            $$1.close();
         }

         return var2;
      } catch (Exception var6) {
         LOGGER.error("Failed to read {}", $$0, var6);
         return EMPTY;
      }
   }

   public static DeprecatedTranslationsInfo loadFromDefaultResource() {
      return loadFromResource("/assets/minecraft/lang/deprecated.json");
   }

   public void applyToMap(Map<String, String> $$0) {
      for(String $$1 : this.removed) {
         $$0.remove($$1);
      }

      this.renamed.forEach(($$1x, $$2) -> {
         String $$3 = (String)$$0.remove($$1x);
         if ($$3 == null) {
            LOGGER.warn("Missing translation key for rename: {}", $$1x);
            $$0.remove($$2);
         } else {
            $$0.put($$2, $$3);
         }
      });
   }
}
