package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class SeagullAIStealFromPlayers extends Goal {
   private final EntitySeagull seagull;
   private Vec3 fleeVec = null;
   private Player target;
   private int fleeTime = 0;

   public SeagullAIStealFromPlayers(EntitySeagull entitySeagull) {
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.TARGET));
      this.seagull = entitySeagull;
   }

   public boolean m_8036_() {
      long worldTime = this.seagull.m_9236_().m_46467_() % 10L;
      if ((this.seagull.m_21216_() < 100 || worldTime == 0L) && !this.seagull.isSitting() && AMConfig.seagullStealing) {
         if ((this.seagull.m_217043_().m_188503_(12) == 0 || worldTime == 0L) && this.seagull.stealCooldown <= 0) {
            if (this.seagull.m_21205_().m_41619_()) {
               Player valid = this.getClosestValidPlayer();
               if (valid != null) {
                  this.target = valid;
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void m_8056_() {
      this.seagull.aiItemFlag = true;
   }

   public void m_8041_() {
      this.seagull.aiItemFlag = false;
      this.target = null;
      this.fleeVec = null;
      this.fleeTime = 0;
   }

   public boolean m_8045_() {
      return this.target != null && !this.target.m_7500_() && (this.seagull.m_21205_().m_41619_() || this.fleeTime > 0);
   }

   public void m_8037_() {
      this.seagull.setFlying(true);
      this.seagull.m_21566_().m_6849_(this.target.m_20185_(), this.target.m_20188_(), this.target.m_20189_(), 1.2F);
      if (this.seagull.m_20270_(this.target) < 2.0F && this.seagull.m_21205_().m_41619_()) {
         if (this.hasFoods(this.target)) {
            ItemStack foodStack = this.getFoodItemFrom(this.target);
            if (!foodStack.m_41619_()) {
               ItemStack copy = foodStack.m_41777_();
               foodStack.m_41774_(1);
               copy.m_41764_(1);
               this.seagull.peck();
               this.seagull.m_21008_(InteractionHand.MAIN_HAND, copy);
               this.fleeTime = 60;
               this.seagull.stealCooldown = 1500 + this.seagull.m_217043_().m_188503_(1500);
               if (this.target instanceof ServerPlayer) {
                  AMAdvancementTriggerRegistry.SEAGULL_STEAL.trigger((ServerPlayer)this.target);
               }
            } else {
               this.m_8041_();
            }
         } else {
            this.m_8041_();
         }
      }

      if (this.fleeTime > 0) {
         if (this.fleeVec == null) {
            this.fleeVec = this.seagull.getBlockInViewAway(this.target.m_20182_(), 4.0F);
         }

         if (this.fleeVec != null) {
            this.seagull.setFlying(true);
            this.seagull.m_21566_().m_6849_(this.fleeVec.f_82479_, this.fleeVec.f_82480_, this.fleeVec.f_82481_, 1.2F);
            if (this.seagull.m_20238_(this.fleeVec) < 5.0) {
               this.fleeVec = this.seagull.getBlockInViewAway(this.fleeVec, 4.0F);
            }
         }

         this.fleeTime--;
      }
   }

   private Player getClosestValidPlayer() {
      List<Player> list = this.seagull.m_9236_().m_6443_(Player.class, this.seagull.m_20191_().m_82377_(10.0, 25.0, 10.0), EntitySelector.f_20406_);
      Player closest = null;
      if (!list.isEmpty()) {
         for (Player player : list) {
            if ((closest == null || closest.m_20270_(this.seagull) > player.m_20270_(this.seagull)) && this.hasFoods(player)) {
               closest = player;
            }
         }
      }

      return closest;
   }

   private boolean hasFoods(Player player) {
      for (int i = 0; i < 9; i++) {
         ItemStack stackIn = (ItemStack)player.m_150109_().f_35974_.get(i);
         if (stackIn.m_41614_() && !this.isBlacklisted(stackIn)) {
            return true;
         }
      }

      return false;
   }

   private boolean isBlacklisted(ItemStack stack) {
      ResourceLocation loc = ForgeRegistries.ITEMS.getKey(stack.m_41720_());
      if (loc != null) {
         for (String str : AMConfig.seagullStealingBlacklist) {
            if (loc.toString().equals(str)) {
               return true;
            }
         }
      }

      return false;
   }

   private ItemStack getFoodItemFrom(Player player) {
      List<ItemStack> foods = new ArrayList<>();

      for (int i = 0; i < 9; i++) {
         ItemStack stackIn = (ItemStack)player.m_150109_().f_35974_.get(i);
         if (stackIn.m_41614_() && !this.isBlacklisted(stackIn)) {
            foods.add(stackIn);
         }
      }

      return !foods.isEmpty() ? foods.get(foods.size() <= 1 ? 0 : this.seagull.m_217043_().m_188503_(foods.size() - 1)) : ItemStack.f_41583_;
   }
}
