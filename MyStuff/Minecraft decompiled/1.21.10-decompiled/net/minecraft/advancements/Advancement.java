package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record Advancement(
   Optional<ResourceLocation> parent,
   Optional<DisplayInfo> display,
   AdvancementRewards rewards,
   Map<String, Criterion<?>> criteria,
   AdvancementRequirements requirements,
   boolean sendsTelemetryEvent,
   Optional<Component> name
) {
   private static final Codec<Map<String, Criterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, Criterion.CODEC)
      .validate($$0 -> $$0.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success($$0));
   public static final Codec<Advancement> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent),
                  DisplayInfo.CODEC.optionalFieldOf("display").forGetter(Advancement::display),
                  AdvancementRewards.CODEC.optionalFieldOf("rewards", AdvancementRewards.EMPTY).forGetter(Advancement::rewards),
                  CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria),
                  AdvancementRequirements.CODEC.optionalFieldOf("requirements").forGetter($$0x -> Optional.of($$0x.requirements())),
                  Codec.BOOL.optionalFieldOf("sends_telemetry_event", Boolean.valueOf(false)).forGetter(Advancement::sendsTelemetryEvent)
               )
               .apply($$0, ($$0x, $$1, $$2, $$3, $$4, $$5) -> {
                  AdvancementRequirements $$6 = (AdvancementRequirements)$$4.orElseGet(() -> AdvancementRequirements.allOf($$3.keySet()));
                  return new Advancement($$0x, $$1, $$2, $$3, $$6, $$5);
               })
      )
      .validate(Advancement::validate);
   public static final StreamCodec<RegistryFriendlyByteBuf, Advancement> STREAM_CODEC = StreamCodec.ofMember(Advancement::write, Advancement::read);

   public Advancement(
      Optional<ResourceLocation> $$0,
      Optional<DisplayInfo> $$1,
      AdvancementRewards $$2,
      Map<String, Criterion<?>> $$3,
      AdvancementRequirements $$4,
      boolean $$5
   ) {
      this($$0, $$1, $$2, Map.copyOf($$3), $$4, $$5, $$1.map(Advancement::decorateName));
   }

   private static DataResult<Advancement> validate(Advancement $$0) {
      return $$0.requirements().validate($$0.criteria().keySet()).map($$1 -> $$0);
   }

   private static Component decorateName(DisplayInfo $$0) {
      Component $$1 = $$0.getTitle();
      ChatFormatting $$2 = $$0.getType().getChatColor();
      Component $$3 = ComponentUtils.mergeStyles($$1.copy(), Style.EMPTY.withColor($$2)).append("\n").append($$0.getDescription());
      Component $$4 = $$1.copy().withStyle($$1x -> $$1x.withHoverEvent(new HoverEvent.ShowText($$3)));
      return ComponentUtils.wrapInSquareBrackets($$4).withStyle($$2);
   }

   public static Component name(AdvancementHolder $$0) {
      return (Component)$$0.value().name().orElseGet(() -> Component.literal($$0.id().toString()));
   }

   private void write(RegistryFriendlyByteBuf $$0) {
      $$0.writeOptional(this.parent, FriendlyByteBuf::writeResourceLocation);
      DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional).encode($$0, this.display);
      this.requirements.write($$0);
      $$0.writeBoolean(this.sendsTelemetryEvent);
   }

   private static Advancement read(RegistryFriendlyByteBuf $$0) {
      return new Advancement(
         $$0.readOptional(FriendlyByteBuf::readResourceLocation),
         (Optional<DisplayInfo>)DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional).decode($$0),
         AdvancementRewards.EMPTY,
         Map.of(),
         new AdvancementRequirements($$0),
         $$0.readBoolean()
      );
   }

   public boolean isRoot() {
      return this.parent.isEmpty();
   }

   public void validate(ProblemReporter $$0, HolderGetter.Provider $$1) {
      this.criteria.forEach(($$2, $$3) -> {
         CriterionValidator $$4 = new CriterionValidator($$0.forChild(new ProblemReporter.RootFieldPathElement($$2)), $$1);
         $$3.triggerInstance().validate($$4);
      });
   }

   public static class Builder {
      private Optional<ResourceLocation> parent = Optional.empty();
      private Optional<DisplayInfo> display = Optional.empty();
      private AdvancementRewards rewards = AdvancementRewards.EMPTY;
      private final ImmutableMap.Builder<String, Criterion<?>> criteria = ImmutableMap.builder();
      private Optional<AdvancementRequirements> requirements = Optional.empty();
      private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
      private boolean sendsTelemetryEvent;

      public static Advancement.Builder advancement() {
         return new Advancement.Builder().sendsTelemetryEvent();
      }

      public static Advancement.Builder recipeAdvancement() {
         return new Advancement.Builder();
      }

      public Advancement.Builder parent(AdvancementHolder $$0) {
         this.parent = Optional.of($$0.id());
         return this;
      }

      @Deprecated(
         forRemoval = true
      )
      public Advancement.Builder parent(ResourceLocation $$0) {
         this.parent = Optional.of($$0);
         return this;
      }

      public Advancement.Builder display(
         ItemStack $$0, Component $$1, Component $$2, @Nullable ResourceLocation $$3, AdvancementType $$4, boolean $$5, boolean $$6, boolean $$7
      ) {
         return this.display(new DisplayInfo($$0, $$1, $$2, Optional.ofNullable($$3).map(ClientAsset.ResourceTexture::new), $$4, $$5, $$6, $$7));
      }

      public Advancement.Builder display(
         ItemLike $$0, Component $$1, Component $$2, @Nullable ResourceLocation $$3, AdvancementType $$4, boolean $$5, boolean $$6, boolean $$7
      ) {
         return this.display(
            new DisplayInfo(new ItemStack($$0.asItem()), $$1, $$2, Optional.ofNullable($$3).map(ClientAsset.ResourceTexture::new), $$4, $$5, $$6, $$7)
         );
      }

      public Advancement.Builder display(DisplayInfo $$0) {
         this.display = Optional.of($$0);
         return this;
      }

      public Advancement.Builder rewards(AdvancementRewards.Builder $$0) {
         return this.rewards($$0.build());
      }

      public Advancement.Builder rewards(AdvancementRewards $$0) {
         this.rewards = $$0;
         return this;
      }

      public Advancement.Builder addCriterion(String $$0, Criterion<?> $$1) {
         this.criteria.put($$0, $$1);
         return this;
      }

      public Advancement.Builder requirements(AdvancementRequirements.Strategy $$0) {
         this.requirementsStrategy = $$0;
         return this;
      }

      public Advancement.Builder requirements(AdvancementRequirements $$0) {
         this.requirements = Optional.of($$0);
         return this;
      }

      public Advancement.Builder sendsTelemetryEvent() {
         this.sendsTelemetryEvent = true;
         return this;
      }

      public AdvancementHolder build(ResourceLocation $$0) {
         Map<String, Criterion<?>> $$1 = this.criteria.buildOrThrow();
         AdvancementRequirements $$2 = (AdvancementRequirements)this.requirements.orElseGet(() -> this.requirementsStrategy.create($$1.keySet()));
         return new AdvancementHolder($$0, new Advancement(this.parent, this.display, this.rewards, $$1, $$2, this.sendsTelemetryEvent));
      }

      public AdvancementHolder save(Consumer<AdvancementHolder> $$0, String $$1) {
         AdvancementHolder $$2 = this.build(ResourceLocation.parse($$1));
         $$0.accept($$2);
         return $$2;
      }
   }
}
