package net.minecraft.network.chat.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class KeybindContents implements ComponentContents {
   public static final MapCodec<KeybindContents> MAP_CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(Codec.STRING.fieldOf("keybind").forGetter($$0x -> $$0x.name)).apply($$0, KeybindContents::new)
   );
   private final String name;
   @Nullable
   private Supplier<Component> nameResolver;

   public KeybindContents(String $$0) {
      this.name = $$0;
   }

   private Component getNestedComponent() {
      if (this.nameResolver == null) {
         this.nameResolver = (Supplier)KeybindResolver.keyResolver.apply(this.name);
      }

      return (Component)this.nameResolver.get();
   }

   @Override
   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> $$0) {
      return this.getNestedComponent().visit($$0);
   }

   @Override
   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> $$0, Style $$1) {
      return this.getNestedComponent().visit($$0, $$1);
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else {
         if ($$0 instanceof KeybindContents $$1 && this.name.equals($$1.name)) {
            return true;
         }

         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public String toString() {
      return "keybind{" + this.name + "}";
   }

   public String getName() {
      return this.name;
   }

   @Override
   public MapCodec<KeybindContents> codec() {
      return MAP_CODEC;
   }
}
