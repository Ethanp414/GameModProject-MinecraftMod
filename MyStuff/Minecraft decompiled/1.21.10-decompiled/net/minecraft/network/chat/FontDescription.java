package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;

public interface FontDescription {
   Codec<FontDescription> CODEC = ResourceLocation.CODEC
      .flatComapMap(
         FontDescription.Resource::new,
         $$0 -> $$0 instanceof FontDescription.Resource $$1
               ? DataResult.success($$1.id())
               : DataResult.error(() -> "Unsupported font description type: " + $$0)
      );
   FontDescription.Resource DEFAULT = new FontDescription.Resource(ResourceLocation.withDefaultNamespace("default"));

   public static record AtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId) implements FontDescription {
   }

   public static record PlayerSprite(ResolvableProfile profile, boolean hat) implements FontDescription {
   }

   public static record Resource(ResourceLocation id) implements FontDescription {
   }
}
