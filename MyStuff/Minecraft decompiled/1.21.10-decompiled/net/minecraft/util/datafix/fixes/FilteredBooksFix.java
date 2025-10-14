package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;

public class FilteredBooksFix extends ItemStackTagFix {
   public FilteredBooksFix(Schema $$0) {
      super($$0, "Remove filtered text from books", $$0x -> $$0x.equals("minecraft:writable_book") || $$0x.equals("minecraft:written_book"));
   }

   @Override
   protected Typed<?> fixItemStackTag(Typed<?> $$0) {
      return Util.writeAndReadTypedOrThrow($$0, $$0.getType(), $$0x -> $$0x.remove("filtered_title").remove("filtered_pages"));
   }
}
