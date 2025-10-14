package net.minecraft.tags;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class TagEntry {
   private static final Codec<TagEntry> FULL_CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("id").forGetter(TagEntry::elementOrTag),
               Codec.BOOL.optionalFieldOf("required", Boolean.valueOf(true)).forGetter($$0x -> $$0x.required)
            )
            .apply($$0, TagEntry::new)
   );
   public static final Codec<TagEntry> CODEC = Codec.either(ExtraCodecs.TAG_OR_ELEMENT_ID, FULL_CODEC)
      .xmap($$0 -> $$0.map($$0x -> new TagEntry($$0x, true), $$0x -> $$0x), $$0 -> $$0.required ? Either.left($$0.elementOrTag()) : Either.right($$0));
   private final ResourceLocation id;
   private final boolean tag;
   private final boolean required;

   private TagEntry(ResourceLocation $$0, boolean $$1, boolean $$2) {
      this.id = $$0;
      this.tag = $$1;
      this.required = $$2;
   }

   private TagEntry(ExtraCodecs.TagOrElementLocation $$0, boolean $$1) {
      this.id = $$0.id();
      this.tag = $$0.tag();
      this.required = $$1;
   }

   private ExtraCodecs.TagOrElementLocation elementOrTag() {
      return new ExtraCodecs.TagOrElementLocation(this.id, this.tag);
   }

   public static TagEntry element(ResourceLocation $$0) {
      return new TagEntry($$0, false, true);
   }

   public static TagEntry optionalElement(ResourceLocation $$0) {
      return new TagEntry($$0, false, false);
   }

   public static TagEntry tag(ResourceLocation $$0) {
      return new TagEntry($$0, true, true);
   }

   public static TagEntry optionalTag(ResourceLocation $$0) {
      return new TagEntry($$0, true, false);
   }

   public <T> boolean build(TagEntry.Lookup<T> $$0, Consumer<T> $$1) {
      if (this.tag) {
         Collection<T> $$2 = $$0.tag(this.id);
         if ($$2 == null) {
            return !this.required;
         }

         $$2.forEach($$1);
      } else {
         T $$3 = $$0.element(this.id, this.required);
         if ($$3 == null) {
            return !this.required;
         }

         $$1.accept($$3);
      }

      return true;
   }

   public void visitRequiredDependencies(Consumer<ResourceLocation> $$0) {
      if (this.tag && this.required) {
         $$0.accept(this.id);
      }
   }

   public void visitOptionalDependencies(Consumer<ResourceLocation> $$0) {
      if (this.tag && !this.required) {
         $$0.accept(this.id);
      }
   }

   public boolean verifyIfPresent(Predicate<ResourceLocation> $$0, Predicate<ResourceLocation> $$1) {
      return !this.required || (this.tag ? $$1 : $$0).test(this.id);
   }

   public String toString() {
      StringBuilder $$0 = new StringBuilder();
      if (this.tag) {
         $$0.append('#');
      }

      $$0.append(this.id);
      if (!this.required) {
         $$0.append('?');
      }

      return $$0.toString();
   }

   public interface Lookup<T> {
      @Nullable
      T element(ResourceLocation var1, boolean var2);

      @Nullable
      Collection<T> tag(ResourceLocation var1);
   }
}
