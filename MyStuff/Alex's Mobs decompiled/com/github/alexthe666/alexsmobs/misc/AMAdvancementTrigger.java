package com.github.alexthe666.alexsmobs.misc;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AMAdvancementTrigger extends SimpleCriterionTrigger<AMAdvancementTrigger.Instance> {
   public final ResourceLocation resourceLocation;

   public AMAdvancementTrigger(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public AMAdvancementTrigger.Instance createInstance(JsonObject p_230241_1_, ContextAwarePredicate p_230241_2_, DeserializationContext p_230241_3_) {
      return new AMAdvancementTrigger.Instance(p_230241_2_, this.resourceLocation);
   }

   public void trigger(ServerPlayer p_192180_1_) {
      this.m_66234_(p_192180_1_, p_226308_1_ -> true);
   }

   public ResourceLocation m_7295_() {
      return this.resourceLocation;
   }

   public static class Instance extends AbstractCriterionTriggerInstance {
      public Instance(ContextAwarePredicate p_i231507_1_, ResourceLocation res) {
         super(res, p_i231507_1_);
      }

      public static TriggerInstance forLevel(Ints p_203912_0_) {
         return new TriggerInstance(ContextAwarePredicate.f_285567_, p_203912_0_);
      }

      public JsonObject m_7683_(SerializationContext p_230240_1_) {
         return super.m_7683_(p_230240_1_);
      }
   }
}
