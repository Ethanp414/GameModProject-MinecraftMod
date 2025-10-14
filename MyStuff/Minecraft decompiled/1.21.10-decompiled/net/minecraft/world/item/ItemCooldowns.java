package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.component.UseCooldown;

public class ItemCooldowns {
   private final Map<ResourceLocation, ItemCooldowns.CooldownInstance> cooldowns = Maps.newHashMap();
   private int tickCount;

   public boolean isOnCooldown(ItemStack $$0) {
      return this.getCooldownPercent($$0, 0.0F) > 0.0F;
   }

   public float getCooldownPercent(ItemStack $$0, float $$1) {
      ResourceLocation $$2 = this.getCooldownGroup($$0);
      ItemCooldowns.CooldownInstance $$3 = (ItemCooldowns.CooldownInstance)this.cooldowns.get($$2);
      if ($$3 != null) {
         float $$4 = (float)($$3.endTime - $$3.startTime);
         float $$5 = (float)$$3.endTime - ((float)this.tickCount + $$1);
         return Mth.clamp($$5 / $$4, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void tick() {
      ++this.tickCount;
      if (!this.cooldowns.isEmpty()) {
         Iterator<Entry<ResourceLocation, ItemCooldowns.CooldownInstance>> $$0 = this.cooldowns.entrySet().iterator();

         while($$0.hasNext()) {
            Entry<ResourceLocation, ItemCooldowns.CooldownInstance> $$1 = (Entry)$$0.next();
            if (((ItemCooldowns.CooldownInstance)$$1.getValue()).endTime <= this.tickCount) {
               $$0.remove();
               this.onCooldownEnded((ResourceLocation)$$1.getKey());
            }
         }
      }
   }

   public ResourceLocation getCooldownGroup(ItemStack $$0) {
      UseCooldown $$1 = $$0.get(DataComponents.USE_COOLDOWN);
      ResourceLocation $$2 = BuiltInRegistries.ITEM.getKey($$0.getItem());
      return $$1 == null ? $$2 : (ResourceLocation)$$1.cooldownGroup().orElse($$2);
   }

   public void addCooldown(ItemStack $$0, int $$1) {
      this.addCooldown(this.getCooldownGroup($$0), $$1);
   }

   public void addCooldown(ResourceLocation $$0, int $$1) {
      this.cooldowns.put($$0, new ItemCooldowns.CooldownInstance(this.tickCount, this.tickCount + $$1));
      this.onCooldownStarted($$0, $$1);
   }

   public void removeCooldown(ResourceLocation $$0) {
      this.cooldowns.remove($$0);
      this.onCooldownEnded($$0);
   }

   protected void onCooldownStarted(ResourceLocation $$0, int $$1) {
   }

   protected void onCooldownEnded(ResourceLocation $$0) {
   }

   static record CooldownInstance(int startTime, int endTime) {
      final int startTime;
      final int endTime;
   }
}
