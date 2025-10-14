package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public record PlaySoundEffect(Holder<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch) implements EnchantmentEntityEffect {
   public static final MapCodec<PlaySoundEffect> CODEC = RecordCodecBuilder.mapCodec(
      $$0 -> $$0.group(
               SoundEvent.CODEC.fieldOf("sound").forGetter(PlaySoundEffect::soundEvent),
               FloatProvider.codec(1.0E-5F, 10.0F).fieldOf("volume").forGetter(PlaySoundEffect::volume),
               FloatProvider.codec(1.0E-5F, 2.0F).fieldOf("pitch").forGetter(PlaySoundEffect::pitch)
            )
            .apply($$0, PlaySoundEffect::new)
   );

   @Override
   public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
      RandomSource $$5 = $$3.getRandom();
      if (!$$3.isSilent()) {
         $$0.playSound(null, $$4.x(), $$4.y(), $$4.z(), this.soundEvent, $$3.getSoundSource(), this.volume.sample($$5), this.pitch.sample($$5));
      }
   }

   @Override
   public MapCodec<PlaySoundEffect> codec() {
      return CODEC;
   }
}
