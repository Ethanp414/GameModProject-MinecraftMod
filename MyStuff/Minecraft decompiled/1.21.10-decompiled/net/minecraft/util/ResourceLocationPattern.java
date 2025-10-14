package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationPattern {
   public static final Codec<ResourceLocationPattern> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter($$0x -> $$0x.namespacePattern),
               ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter($$0x -> $$0x.pathPattern)
            )
            .apply($$0, ResourceLocationPattern::new)
   );
   private final Optional<Pattern> namespacePattern;
   private final Predicate<String> namespacePredicate;
   private final Optional<Pattern> pathPattern;
   private final Predicate<String> pathPredicate;
   private final Predicate<ResourceLocation> locationPredicate;

   private ResourceLocationPattern(Optional<Pattern> $$0, Optional<Pattern> $$1) {
      this.namespacePattern = $$0;
      this.namespacePredicate = (Predicate)$$0.map(Pattern::asPredicate).orElse((Predicate)$$0x -> true);
      this.pathPattern = $$1;
      this.pathPredicate = (Predicate)$$1.map(Pattern::asPredicate).orElse((Predicate)$$0x -> true);
      this.locationPredicate = $$0x -> this.namespacePredicate.test($$0x.getNamespace()) && this.pathPredicate.test($$0x.getPath());
   }

   public Predicate<String> namespacePredicate() {
      return this.namespacePredicate;
   }

   public Predicate<String> pathPredicate() {
      return this.pathPredicate;
   }

   public Predicate<ResourceLocation> locationPredicate() {
      return this.locationPredicate;
   }
}
