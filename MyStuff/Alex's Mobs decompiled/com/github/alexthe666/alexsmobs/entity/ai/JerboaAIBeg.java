package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Items;

public class JerboaAIBeg extends Goal {
   private static final TargetingConditions ENTITY_PREDICATE = TargetingConditions.m_148353_().m_26883_(32.0);
   protected final EntityJerboa jerboa;
   private final double speed;
   protected Player closestPlayer;
   private int delayTemptCounter;
   private boolean isRunning;

   public JerboaAIBeg(EntityJerboa jerboa, double speed) {
      this.jerboa = jerboa;
      this.speed = speed;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      if (this.delayTemptCounter > 0) {
         this.delayTemptCounter--;
         return false;
      } else if (this.jerboa.m_27593_()) {
         return false;
      } else {
         this.closestPlayer = this.jerboa.m_9236_().m_45946_(ENTITY_PREDICATE, this.jerboa);
         return this.closestPlayer == null ? false : this.isFood(this.closestPlayer.m_21205_()) || this.isFood(this.closestPlayer.m_21206_());
      }
   }

   private boolean isFood(ItemStack stack) {
      return stack.m_204117_(Items.SEEDS) || this.jerboa.m_6898_(stack);
   }

   public boolean m_8045_() {
      return this.jerboa.m_21205_().m_41619_() && this.m_8036_() && !this.jerboa.m_27593_();
   }

   public void m_8056_() {
      this.isRunning = true;
   }

   public void m_8041_() {
      this.closestPlayer = null;
      this.jerboa.m_21573_().m_26573_();
      this.delayTemptCounter = 100;
      this.jerboa.setBegging(false);
      this.isRunning = false;
   }

   public void m_8037_() {
      this.jerboa.m_21563_().m_24960_(this.closestPlayer, this.jerboa.m_8085_() + 20, this.jerboa.m_8132_());
      if (this.jerboa.m_20280_(this.closestPlayer) < 12.0) {
         this.jerboa.m_21573_().m_26573_();
         this.jerboa.setBegging(true);
      } else {
         this.jerboa.m_21573_().m_5624_(this.closestPlayer, this.speed);
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
