package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;

public class MutableComponent implements Component {
   private final ComponentContents contents;
   private final List<Component> siblings;
   private Style style;
   private FormattedCharSequence visualOrderText = FormattedCharSequence.EMPTY;
   @Nullable
   private Language decomposedWith;

   MutableComponent(ComponentContents $$0, List<Component> $$1, Style $$2) {
      this.contents = $$0;
      this.siblings = $$1;
      this.style = $$2;
   }

   public static MutableComponent create(ComponentContents $$0) {
      return new MutableComponent($$0, Lists.<Component>newArrayList(), Style.EMPTY);
   }

   @Override
   public ComponentContents getContents() {
      return this.contents;
   }

   @Override
   public List<Component> getSiblings() {
      return this.siblings;
   }

   public MutableComponent setStyle(Style $$0) {
      this.style = $$0;
      return this;
   }

   @Override
   public Style getStyle() {
      return this.style;
   }

   public MutableComponent append(String $$0) {
      return $$0.isEmpty() ? this : this.append(Component.literal($$0));
   }

   public MutableComponent append(Component $$0) {
      this.siblings.add($$0);
      return this;
   }

   public MutableComponent withStyle(UnaryOperator<Style> $$0) {
      this.setStyle((Style)$$0.apply(this.getStyle()));
      return this;
   }

   public MutableComponent withStyle(Style $$0) {
      this.setStyle($$0.applyTo(this.getStyle()));
      return this;
   }

   public MutableComponent withStyle(ChatFormatting... $$0) {
      this.setStyle(this.getStyle().applyFormats($$0));
      return this;
   }

   public MutableComponent withStyle(ChatFormatting $$0) {
      this.setStyle(this.getStyle().applyFormat($$0));
      return this;
   }

   public MutableComponent withColor(int $$0) {
      this.setStyle(this.getStyle().withColor($$0));
      return this;
   }

   @Override
   public FormattedCharSequence getVisualOrderText() {
      Language $$0 = Language.getInstance();
      if (this.decomposedWith != $$0) {
         this.visualOrderText = $$0.getVisualOrder(this);
         this.decomposedWith = $$0;
      }

      return this.visualOrderText;
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else if (!($$0 instanceof MutableComponent)) {
         return false;
      } else {
         MutableComponent $$1 = (MutableComponent)$$0;
         return this.contents.equals($$1.contents) && this.style.equals($$1.style) && this.siblings.equals($$1.siblings);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.contents, this.style, this.siblings});
   }

   public String toString() {
      StringBuilder $$0 = new StringBuilder(this.contents.toString());
      boolean $$1 = !this.style.isEmpty();
      boolean $$2 = !this.siblings.isEmpty();
      if ($$1 || $$2) {
         $$0.append('[');
         if ($$1) {
            $$0.append("style=");
            $$0.append(this.style);
         }

         if ($$1 && $$2) {
            $$0.append(", ");
         }

         if ($$2) {
            $$0.append("siblings=");
            $$0.append(this.siblings);
         }

         $$0.append(']');
      }

      return $$0.toString();
   }
}
