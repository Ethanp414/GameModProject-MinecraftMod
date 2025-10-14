package net.minecraft.world.scores;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

public interface ScoreHolder {
   String WILDCARD_NAME = "*";
   ScoreHolder WILDCARD = new ScoreHolder() {
      @Override
      public String getScoreboardName() {
         return "*";
      }
   };

   String getScoreboardName();

   @Nullable
   default Component getDisplayName() {
      return null;
   }

   default Component getFeedbackDisplayName() {
      Component $$0 = this.getDisplayName();
      return $$0 != null
         ? $$0.copy().withStyle($$0x -> $$0x.withHoverEvent(new HoverEvent.ShowText(Component.literal(this.getScoreboardName()))))
         : Component.literal(this.getScoreboardName());
   }

   static ScoreHolder forNameOnly(final String $$0) {
      if ($$0.equals("*")) {
         return WILDCARD;
      } else {
         final Component $$1 = Component.literal($$0);
         return new ScoreHolder() {
            @Override
            public String getScoreboardName() {
               return $$0;
            }

            @Override
            public Component getFeedbackDisplayName() {
               return $$1;
            }
         };
      }
   }

   static ScoreHolder fromGameProfile(GameProfile $$0) {
      final String $$1 = $$0.name();
      return new ScoreHolder() {
         @Override
         public String getScoreboardName() {
            return $$1;
         }
      };
   }
}
