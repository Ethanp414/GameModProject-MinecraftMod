package net.minecraft.world;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public record LockCode(ItemPredicate predicate) {
   public static final LockCode NO_LOCK = new LockCode(ItemPredicate.Builder.item().build());
   public static final Codec<LockCode> CODEC = ItemPredicate.CODEC.xmap(LockCode::new, LockCode::predicate);
   public static final String TAG_LOCK = "lock";

   public boolean unlocksWith(ItemStack $$0) {
      return this.predicate.test($$0);
   }

   public void addToTag(ValueOutput $$0) {
      if (this != NO_LOCK) {
         $$0.store("lock", CODEC, this);
      }
   }

   public static LockCode fromTag(ValueInput $$0) {
      return (LockCode)$$0.read("lock", CODEC).orElse(NO_LOCK);
   }
}
