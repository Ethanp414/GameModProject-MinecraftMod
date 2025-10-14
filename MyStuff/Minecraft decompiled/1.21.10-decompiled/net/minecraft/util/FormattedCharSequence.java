package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import net.minecraft.network.chat.Style;

@FunctionalInterface
public interface FormattedCharSequence {
   FormattedCharSequence EMPTY = $$0 -> true;

   boolean accept(FormattedCharSink var1);

   static FormattedCharSequence codepoint(int $$0, Style $$1) {
      return $$2 -> $$2.accept(0, $$1, $$0);
   }

   static FormattedCharSequence forward(String $$0, Style $$1) {
      return $$0.isEmpty() ? EMPTY : $$2 -> StringDecomposer.iterate($$0, $$1, $$2);
   }

   static FormattedCharSequence forward(String $$0, Style $$1, Int2IntFunction $$2) {
      return $$0.isEmpty() ? EMPTY : $$3 -> StringDecomposer.iterate($$0, $$1, decorateOutput($$3, $$2));
   }

   static FormattedCharSequence backward(String $$0, Style $$1) {
      return $$0.isEmpty() ? EMPTY : $$2 -> StringDecomposer.iterateBackwards($$0, $$1, $$2);
   }

   static FormattedCharSequence backward(String $$0, Style $$1, Int2IntFunction $$2) {
      return $$0.isEmpty() ? EMPTY : $$3 -> StringDecomposer.iterateBackwards($$0, $$1, decorateOutput($$3, $$2));
   }

   static FormattedCharSink decorateOutput(FormattedCharSink $$0, Int2IntFunction $$1) {
      return ($$2, $$3, $$4) -> $$0.accept($$2, $$3, $$1.apply(Integer.valueOf($$4)));
   }

   static FormattedCharSequence composite() {
      return EMPTY;
   }

   static FormattedCharSequence composite(FormattedCharSequence $$0) {
      return $$0;
   }

   static FormattedCharSequence composite(FormattedCharSequence $$0, FormattedCharSequence $$1) {
      return fromPair($$0, $$1);
   }

   static FormattedCharSequence composite(FormattedCharSequence... $$0) {
      return fromList(ImmutableList.copyOf($$0));
   }

   static FormattedCharSequence composite(List<FormattedCharSequence> $$0) {
      int $$1 = $$0.size();
      switch($$1) {
         case 0:
            return EMPTY;
         case 1:
            return (FormattedCharSequence)$$0.get(0);
         case 2:
            return fromPair((FormattedCharSequence)$$0.get(0), (FormattedCharSequence)$$0.get(1));
         default:
            return fromList(ImmutableList.copyOf($$0));
      }
   }

   static FormattedCharSequence fromPair(FormattedCharSequence $$0, FormattedCharSequence $$1) {
      return $$2 -> $$0.accept($$2) && $$1.accept($$2);
   }

   static FormattedCharSequence fromList(List<FormattedCharSequence> $$0) {
      return $$1 -> {
         for(FormattedCharSequence $$2 : $$0) {
            if (!$$2.accept($$1)) {
               return false;
            }
         }

         return true;
      };
   }
}
