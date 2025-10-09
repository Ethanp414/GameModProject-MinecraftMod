package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class TransmutationData {
   private final Object2DoubleMap<ItemStack> itemstackData = new Object2DoubleOpenHashMap();

   public void onTransmuteItem(ItemStack beingTransmuted, ItemStack turnedInto) {
      double fromWeight = this.getWeight(beingTransmuted);
      double toWeight = this.getWeight(turnedInto);
      this.putWeight(beingTransmuted, fromWeight + calculateAddWeight(beingTransmuted.m_41613_()));
      this.putWeight(turnedInto, toWeight + calculateRemoveWeight(turnedInto.m_41613_()));
   }

   public double getWeight(ItemStack stack) {
      ObjectIterator var2 = this.itemstackData.object2DoubleEntrySet().iterator();

      while (var2.hasNext()) {
         Entry<ItemStack> entry = (Entry<ItemStack>)var2.next();
         if (ItemStack.m_150942_(stack, (ItemStack)entry.getKey())) {
            return entry.getDoubleValue();
         }
      }

      return 0.0;
   }

   private static double calculateAddWeight(int count) {
      return Math.log(Math.pow(count, AMConfig.transmutingWeightAddStep));
   }

   private static double calculateRemoveWeight(int count) {
      return -Math.log(Math.pow(count, AMConfig.transmutingWeightRemoveStep));
   }

   public void putWeight(ItemStack stack, double newWeight) {
      ItemStack replace = stack;
      ObjectIterator var5 = this.itemstackData.keySet().iterator();

      while (var5.hasNext()) {
         ItemStack entry = (ItemStack)var5.next();
         if (ItemStack.m_150942_(stack, entry)) {
            replace = entry;
            break;
         }
      }

      this.itemstackData.put(replace, Math.max(newWeight, 0.0));
   }

   @Nullable
   public ItemStack getRandomItem(Random random) {
      ItemStack result = null;
      double bestValue = Double.MAX_VALUE;
      ObjectIterator var5 = this.itemstackData.object2DoubleEntrySet().iterator();

      while (var5.hasNext()) {
         Entry<ItemStack> entry = (Entry<ItemStack>)var5.next();
         if (!(entry.getDoubleValue() <= 0.0)) {
            double value = -Math.log(random.nextDouble()) / entry.getDoubleValue();
            if (value < bestValue) {
               bestValue = value;
               result = ((ItemStack)entry.getKey()).m_41777_();
            }
         }
      }

      return result;
   }

   public CompoundTag saveAsNBT() {
      CompoundTag compound = new CompoundTag();
      ListTag listTag = new ListTag();
      ObjectIterator var3 = this.itemstackData.object2DoubleEntrySet().iterator();

      while (var3.hasNext()) {
         Entry<ItemStack> entry = (Entry<ItemStack>)var3.next();
         CompoundTag tag = new CompoundTag();
         tag.m_128365_("Item", ((ItemStack)entry.getKey()).m_41739_(new CompoundTag()));
         tag.m_128347_("Weight", entry.getDoubleValue());
         listTag.add(tag);
      }

      compound.m_128365_("TransmutationData", listTag);
      return compound;
   }

   public static TransmutationData fromNBT(CompoundTag compound) {
      TransmutationData data = new TransmutationData();
      if (compound.m_128441_("TransmutationData")) {
         ListTag listtag = compound.m_128437_("TransmutationData", 10);

         for (int i = 0; i < listtag.size(); i++) {
            CompoundTag innerTag = listtag.m_128728_(i);

            try {
               ItemStack from = ItemStack.m_41712_(innerTag.m_128469_("Item"));
               if (!from.m_41619_()) {
                  data.putWeight(from, innerTag.m_128459_("Weight"));
               }
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }
      }

      return data;
   }

   public double getTotalWeight() {
      return this.itemstackData.values().doubleStream().sum();
   }
}
