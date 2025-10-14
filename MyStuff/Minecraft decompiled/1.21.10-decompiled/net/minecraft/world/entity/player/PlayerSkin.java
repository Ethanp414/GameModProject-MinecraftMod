package net.minecraft.world.entity.player;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PlayerSkin(
   ClientAsset.Texture body, @Nullable ClientAsset.Texture cape, @Nullable ClientAsset.Texture elytra, PlayerModelType model, boolean secure
) {
   public static PlayerSkin insecure(ClientAsset.Texture $$0, @Nullable ClientAsset.Texture $$1, @Nullable ClientAsset.Texture $$2, PlayerModelType $$3) {
      return new PlayerSkin($$0, $$1, $$2, $$3, false);
   }

   public PlayerSkin with(PlayerSkin.Patch $$0) {
      return $$0.equals(PlayerSkin.Patch.EMPTY)
         ? this
         : insecure(
            DataFixUtils.orElse($$0.body, this.body),
            DataFixUtils.orElse($$0.cape, this.cape),
            DataFixUtils.orElse($$0.elytra, this.elytra),
            (PlayerModelType)$$0.model.orElse(this.model)
         );
   }

   public static record Patch(
      Optional<ClientAsset.ResourceTexture> body,
      Optional<ClientAsset.ResourceTexture> cape,
      Optional<ClientAsset.ResourceTexture> elytra,
      Optional<PlayerModelType> model
   ) {
      final Optional<ClientAsset.ResourceTexture> body;
      final Optional<ClientAsset.ResourceTexture> cape;
      final Optional<ClientAsset.ResourceTexture> elytra;
      final Optional<PlayerModelType> model;
      public static final PlayerSkin.Patch EMPTY = new PlayerSkin.Patch(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
      public static final MapCodec<PlayerSkin.Patch> MAP_CODEC = RecordCodecBuilder.mapCodec(
         $$0 -> $$0.group(
                  ClientAsset.ResourceTexture.CODEC.optionalFieldOf("texture").forGetter(PlayerSkin.Patch::body),
                  ClientAsset.ResourceTexture.CODEC.optionalFieldOf("cape").forGetter(PlayerSkin.Patch::cape),
                  ClientAsset.ResourceTexture.CODEC.optionalFieldOf("elytra").forGetter(PlayerSkin.Patch::elytra),
                  PlayerModelType.CODEC.optionalFieldOf("model").forGetter(PlayerSkin.Patch::model)
               )
               .apply($$0, PlayerSkin.Patch::create)
      );
      public static final StreamCodec<ByteBuf, PlayerSkin.Patch> STREAM_CODEC = StreamCodec.composite(
         ClientAsset.ResourceTexture.STREAM_CODEC.apply(ByteBufCodecs::optional),
         PlayerSkin.Patch::body,
         ClientAsset.ResourceTexture.STREAM_CODEC.apply(ByteBufCodecs::optional),
         PlayerSkin.Patch::cape,
         ClientAsset.ResourceTexture.STREAM_CODEC.apply(ByteBufCodecs::optional),
         PlayerSkin.Patch::elytra,
         PlayerModelType.STREAM_CODEC.apply(ByteBufCodecs::optional),
         PlayerSkin.Patch::model,
         PlayerSkin.Patch::create
      );

      public static PlayerSkin.Patch create(
         Optional<ClientAsset.ResourceTexture> $$0,
         Optional<ClientAsset.ResourceTexture> $$1,
         Optional<ClientAsset.ResourceTexture> $$2,
         Optional<PlayerModelType> $$3
      ) {
         return $$0.isEmpty() && $$1.isEmpty() && $$2.isEmpty() && $$3.isEmpty() ? EMPTY : new PlayerSkin.Patch($$0, $$1, $$2, $$3);
      }
   }
}
