package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
   private static final DateTimeFormatter OBTAINED_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
   private static final Codec<Instant> OBTAINED_TIME_CODEC = ExtraCodecs.temporalCodec(OBTAINED_TIME_FORMAT)
      .xmap(Instant::from, $$0 -> $$0.atZone(ZoneId.systemDefault()));
   private static final Codec<Map<String, CriterionProgress>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, OBTAINED_TIME_CODEC)
      .xmap(
         $$0 -> Util.mapValues($$0, CriterionProgress::new),
         $$0 -> (Map)$$0.entrySet()
               .stream()
               .filter($$0x -> ((CriterionProgress)$$0x.getValue()).isDone())
               .collect(Collectors.toMap(Entry::getKey, $$0x -> (Instant)Objects.requireNonNull(((CriterionProgress)$$0x.getValue()).getObtained())))
      );
   public static final Codec<AdvancementProgress> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               CRITERIA_CODEC.optionalFieldOf("criteria", Map.of()).forGetter($$0x -> $$0x.criteria),
               Codec.BOOL.fieldOf("done").orElse(true).forGetter(AdvancementProgress::isDone)
            )
            .apply($$0, ($$0x, $$1) -> new AdvancementProgress(new HashMap($$0x)))
   );
   private final Map<String, CriterionProgress> criteria;
   private AdvancementRequirements requirements = AdvancementRequirements.EMPTY;

   private AdvancementProgress(Map<String, CriterionProgress> $$0) {
      this.criteria = $$0;
   }

   public AdvancementProgress() {
      this.criteria = Maps.newHashMap();
   }

   public void update(AdvancementRequirements $$0) {
      Set<String> $$1 = $$0.names();
      this.criteria.entrySet().removeIf($$1x -> !$$1.contains($$1x.getKey()));

      for(String $$2 : $$1) {
         this.criteria.putIfAbsent($$2, new CriterionProgress());
      }

      this.requirements = $$0;
   }

   public boolean isDone() {
      return this.requirements.test(this::isCriterionDone);
   }

   public boolean hasProgress() {
      for(CriterionProgress $$0 : this.criteria.values()) {
         if ($$0.isDone()) {
            return true;
         }
      }

      return false;
   }

   public boolean grantProgress(String $$0) {
      CriterionProgress $$1 = (CriterionProgress)this.criteria.get($$0);
      if ($$1 != null && !$$1.isDone()) {
         $$1.grant();
         return true;
      } else {
         return false;
      }
   }

   public boolean revokeProgress(String $$0) {
      CriterionProgress $$1 = (CriterionProgress)this.criteria.get($$0);
      if ($$1 != null && $$1.isDone()) {
         $$1.revoke();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + this.requirements + "}";
   }

   public void serializeToNetwork(FriendlyByteBuf $$0) {
      $$0.writeMap(this.criteria, FriendlyByteBuf::writeUtf, ($$0x, $$1) -> $$1.serializeToNetwork($$0x));
   }

   public static AdvancementProgress fromNetwork(FriendlyByteBuf $$0) {
      Map<String, CriterionProgress> $$1 = $$0.readMap(FriendlyByteBuf::readUtf, CriterionProgress::fromNetwork);
      return new AdvancementProgress($$1);
   }

   @Nullable
   public CriterionProgress getCriterion(String $$0) {
      return (CriterionProgress)this.criteria.get($$0);
   }

   private boolean isCriterionDone(String $$0) {
      CriterionProgress $$1 = this.getCriterion($$0);
      return $$1 != null && $$1.isDone();
   }

   public float getPercent() {
      if (this.criteria.isEmpty()) {
         return 0.0F;
      } else {
         float $$0 = (float)this.requirements.size();
         float $$1 = (float)this.countCompletedRequirements();
         return $$1 / $$0;
      }
   }

   @Nullable
   public Component getProgressText() {
      if (this.criteria.isEmpty()) {
         return null;
      } else {
         int $$0 = this.requirements.size();
         if ($$0 <= 1) {
            return null;
         } else {
            int $$1 = this.countCompletedRequirements();
            return Component.translatable("advancements.progress", $$1, $$0);
         }
      }
   }

   private int countCompletedRequirements() {
      return this.requirements.count(this::isCriterionDone);
   }

   public Iterable<String> getRemainingCriteria() {
      List<String> $$0 = Lists.newArrayList();

      for(Entry<String, CriterionProgress> $$1 : this.criteria.entrySet()) {
         if (!((CriterionProgress)$$1.getValue()).isDone()) {
            $$0.add((String)$$1.getKey());
         }
      }

      return $$0;
   }

   public Iterable<String> getCompletedCriteria() {
      List<String> $$0 = Lists.newArrayList();

      for(Entry<String, CriterionProgress> $$1 : this.criteria.entrySet()) {
         if (((CriterionProgress)$$1.getValue()).isDone()) {
            $$0.add((String)$$1.getKey());
         }
      }

      return $$0;
   }

   @Nullable
   public Instant getFirstProgressDate() {
      return (Instant)this.criteria.values().stream().map(CriterionProgress::getObtained).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
   }

   public int compareTo(AdvancementProgress $$0) {
      Instant $$1 = this.getFirstProgressDate();
      Instant $$2 = $$0.getFirstProgressDate();
      if ($$1 == null && $$2 != null) {
         return 1;
      } else if ($$1 != null && $$2 == null) {
         return -1;
      } else {
         return $$1 == null && $$2 == null ? 0 : $$1.compareTo($$2);
      }
   }
}
