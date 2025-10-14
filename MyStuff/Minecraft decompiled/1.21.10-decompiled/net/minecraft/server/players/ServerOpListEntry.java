package net.minecraft.server.players;

import com.google.gson.JsonObject;

public class ServerOpListEntry extends StoredUserEntry<NameAndId> {
   private final int level;
   private final boolean bypassesPlayerLimit;

   public ServerOpListEntry(NameAndId $$0, int $$1, boolean $$2) {
      super($$0);
      this.level = $$1;
      this.bypassesPlayerLimit = $$2;
   }

   public ServerOpListEntry(JsonObject $$0) {
      super(NameAndId.fromJson($$0));
      this.level = $$0.has("level") ? $$0.get("level").getAsInt() : 0;
      this.bypassesPlayerLimit = $$0.has("bypassesPlayerLimit") && $$0.get("bypassesPlayerLimit").getAsBoolean();
   }

   public int getLevel() {
      return this.level;
   }

   public boolean getBypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   @Override
   protected void serialize(JsonObject $$0) {
      if (this.getUser() != null) {
         this.getUser().appendTo($$0);
         $$0.addProperty("level", this.level);
         $$0.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
      }
   }
}
