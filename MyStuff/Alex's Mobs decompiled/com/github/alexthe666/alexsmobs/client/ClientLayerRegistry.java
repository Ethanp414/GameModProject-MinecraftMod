package com.github.alexthe666.alexsmobs.client;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerRainbow;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.AddLayers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class ClientLayerRegistry {
   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void onAddLayers(AddLayers event) {
      List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(
         ForgeRegistries.ENTITY_TYPES
            .getValues()
            .stream()
            .filter(DefaultAttributes::m_22301_)
            .map(entityType -> (EntityType)entityType)
            .collect(Collectors.toList())
      );
      entityTypes.forEach(entityType -> addLayerIfApplicable((EntityType<? extends LivingEntity>)entityType, event));

      for (String skinType : event.getSkins()) {
         event.getSkin(skinType).m_115326_(new LayerRainbow(event.getSkin(skinType)));
      }
   }

   private static void addLayerIfApplicable(EntityType<? extends LivingEntity> entityType, AddLayers event) {
      LivingEntityRenderer renderer = null;
      if (entityType != EntityType.f_20565_) {
         try {
            renderer = event.getRenderer(entityType);
         } catch (Exception var4) {
            AlexsMobs.LOGGER
               .warn(
                  "Could not apply rainbow color layer to "
                     + ForgeRegistries.ENTITY_TYPES.getKey(entityType)
                     + ", has custom renderer that is not LivingEntityRenderer."
               );
         }

         if (renderer != null) {
            renderer.m_115326_(new LayerRainbow(renderer));
         }
      }
   }
}
