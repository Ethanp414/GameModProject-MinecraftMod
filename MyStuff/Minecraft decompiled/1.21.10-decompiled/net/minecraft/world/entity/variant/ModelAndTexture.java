package net.minecraft.world.entity.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ModelAndTexture<T>(T model, ClientAsset.ResourceTexture asset) {
   public ModelAndTexture(T $$0, ResourceLocation $$1) {
      this($$0, new ClientAsset.ResourceTexture($$1));
   }

   public static <T> MapCodec<ModelAndTexture<T>> codec(Codec<T> $$0, T $$1) {
      return RecordCodecBuilder.mapCodec(
         $$2 -> $$2.group(
                  $$0.optionalFieldOf("model", $$1).forGetter(ModelAndTexture::model),
                  ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(ModelAndTexture::asset)
               )
               .apply($$2, ModelAndTexture::new)
      );
   }

   public static <T> StreamCodec<RegistryFriendlyByteBuf, ModelAndTexture<T>> streamCodec(StreamCodec<? super RegistryFriendlyByteBuf, T> $$0) {
      return StreamCodec.composite($$0, ModelAndTexture::model, ClientAsset.ResourceTexture.STREAM_CODEC, ModelAndTexture::asset, ModelAndTexture::new);
   }
}
