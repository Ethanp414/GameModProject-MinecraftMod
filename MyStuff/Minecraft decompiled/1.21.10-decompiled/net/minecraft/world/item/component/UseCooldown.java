package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record UseCooldown(float seconds, Optional<ResourceLocation> cooldownGroup) {
   public static final Codec<UseCooldown> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               ExtraCodecs.POSITIVE_FLOAT.fieldOf("seconds").forGetter(UseCooldown::seconds),
               ResourceLocation.CODEC.optionalFieldOf("cooldown_group").forGetter(UseCooldown::cooldownGroup)
            )
            .apply($$0, UseCooldown::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, UseCooldown> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.FLOAT, UseCooldown::seconds, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), UseCooldown::cooldownGroup, UseCooldown::new
   );

   public UseCooldown(float $$0) {
      this($$0, Optional.empty());
   }

   public int ticks() {
      return (int)(this.seconds * 20.0F);
   }

   public void apply(ItemStack $$0, LivingEntity $$1) {
      if ($$1 instanceof Player $$2) {
         $$2.getCooldowns().addCooldown($$0, this.ticks());
      }
   }
}
