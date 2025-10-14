package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.notifications.NotificationService;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class StoredUserList<K, V extends StoredUserEntry<K>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final File file;
   private final Map<String, V> map = Maps.newHashMap();
   protected final NotificationService notificationService;

   public StoredUserList(File $$0, NotificationService $$1) {
      this.file = $$0;
      this.notificationService = $$1;
   }

   public File getFile() {
      return this.file;
   }

   public boolean add(V $$0) {
      String $$1 = this.getKeyForUser($$0.getUser());
      V $$2 = (V)this.map.get($$1);
      if ($$0.equals($$2)) {
         return false;
      } else {
         this.map.put($$1, $$0);

         try {
            this.save();
         } catch (IOException var5) {
            LOGGER.warn("Could not save the list after adding a user.", var5);
         }

         return true;
      }
   }

   @Nullable
   public V get(K $$0) {
      this.removeExpired();
      return (V)this.map.get(this.getKeyForUser($$0));
   }

   public boolean remove(K $$0) {
      V $$1 = (V)this.map.remove(this.getKeyForUser($$0));
      if ($$1 == null) {
         return false;
      } else {
         try {
            this.save();
         } catch (IOException var4) {
            LOGGER.warn("Could not save the list after removing a user.", var4);
         }

         return true;
      }
   }

   public boolean remove(StoredUserEntry<K> $$0) {
      return this.remove((K)Objects.requireNonNull($$0.getUser()));
   }

   public void clear() {
      this.map.clear();

      try {
         this.save();
      } catch (IOException var2) {
         LOGGER.warn("Could not save the list after removing a user.", var2);
      }
   }

   public String[] getUserList() {
      return (String[])this.map.keySet().toArray(new String[0]);
   }

   public boolean isEmpty() {
      return this.map.size() < 1;
   }

   protected String getKeyForUser(K $$0) {
      return $$0.toString();
   }

   protected boolean contains(K $$0) {
      return this.map.containsKey(this.getKeyForUser($$0));
   }

   private void removeExpired() {
      List<K> $$0 = Lists.<K>newArrayList();

      for(V $$1 : this.map.values()) {
         if ($$1.hasExpired()) {
            $$0.add($$1.getUser());
         }
      }

      for(K $$2 : $$0) {
         this.map.remove(this.getKeyForUser($$2));
      }
   }

   protected abstract StoredUserEntry<K> createEntry(JsonObject var1);

   public Collection<V> getEntries() {
      return this.map.values();
   }

   public void save() throws IOException {
      JsonArray $$0 = new JsonArray();
      this.map.values().stream().map($$0x -> Util.make(new JsonObject(), $$0x::serialize)).forEach($$0::add);
      BufferedWriter $$1 = Files.newWriter(this.file, StandardCharsets.UTF_8);

      try {
         GSON.toJson($$0, GSON.newJsonWriter($$1));
      } catch (Throwable var6) {
         if ($$1 != null) {
            try {
               $$1.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if ($$1 != null) {
         $$1.close();
      }
   }

   public void load() throws IOException {
      if (this.file.exists()) {
         BufferedReader $$0 = Files.newReader(this.file, StandardCharsets.UTF_8);

         label54: {
            try {
               this.map.clear();
               JsonArray $$1 = GSON.fromJson($$0, JsonArray.class);
               if ($$1 == null) {
                  break label54;
               }

               for(JsonElement $$2 : $$1) {
                  JsonObject $$3 = GsonHelper.convertToJsonObject($$2, "entry");
                  StoredUserEntry<K> $$4 = this.createEntry($$3);
                  if ($$4.getUser() != null) {
                     this.map.put(this.getKeyForUser($$4.getUser()), $$4);
                  }
               }
            } catch (Throwable var8) {
               if ($$0 != null) {
                  try {
                     $$0.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if ($$0 != null) {
               $$0.close();
            }

            return;
         }

         if ($$0 != null) {
            $$0.close();
         }
      }
   }
}
