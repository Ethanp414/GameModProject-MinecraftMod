package net.minecraft.world.entity.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleBuilder {
   private final Schedule schedule;
   private final List<ScheduleBuilder.ActivityTransition> transitions = Lists.<ScheduleBuilder.ActivityTransition>newArrayList();

   public ScheduleBuilder(Schedule $$0) {
      this.schedule = $$0;
   }

   public ScheduleBuilder changeActivityAt(int $$0, Activity $$1) {
      this.transitions.add(new ScheduleBuilder.ActivityTransition($$0, $$1));
      return this;
   }

   public Schedule build() {
      ((Set)this.transitions.stream().map(ScheduleBuilder.ActivityTransition::getActivity).collect(Collectors.toSet()))
         .forEach(this.schedule::ensureTimelineExistsFor);
      this.transitions.forEach($$0 -> {
         Activity $$1 = $$0.getActivity();
         this.schedule.getAllTimelinesExceptFor($$1).forEach($$1x -> $$1x.addKeyframe($$0.getTime(), 0.0F));
         this.schedule.getTimelineFor($$1).addKeyframe($$0.getTime(), 1.0F);
      });
      return this.schedule;
   }

   static class ActivityTransition {
      private final int time;
      private final Activity activity;

      public ActivityTransition(int $$0, Activity $$1) {
         this.time = $$0;
         this.activity = $$1;
      }

      public int getTime() {
         return this.time;
      }

      public Activity getActivity() {
         return this.activity;
      }
   }
}
