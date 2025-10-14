package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public abstract class Team {
   public boolean isAlliedTo(@Nullable Team $$0) {
      if ($$0 == null) {
         return false;
      } else {
         return this == $$0;
      }
   }

   public abstract String getName();

   public abstract MutableComponent getFormattedName(Component var1);

   public abstract boolean canSeeFriendlyInvisibles();

   public abstract boolean isAllowFriendlyFire();

   public abstract Team.Visibility getNameTagVisibility();

   public abstract ChatFormatting getColor();

   public abstract Collection<String> getPlayers();

   public abstract Team.Visibility getDeathMessageVisibility();

   public abstract Team.CollisionRule getCollisionRule();

   public static enum CollisionRule implements StringRepresentable {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      public static final Codec<Team.CollisionRule> CODEC = StringRepresentable.fromEnum(Team.CollisionRule::values);
      private static final IntFunction<Team.CollisionRule> BY_ID = ByIdMap.continuous($$0 -> $$0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Team.CollisionRule> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
      public final String name;
      public final int id;

      private CollisionRule(final String param3, final int param4) {
         this.name = $$0;
         this.id = $$1;
      }

      public Component getDisplayName() {
         return Component.translatable("team.collision." + this.name);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum Visibility implements StringRepresentable {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      public static final Codec<Team.Visibility> CODEC = StringRepresentable.fromEnum(Team.Visibility::values);
      private static final IntFunction<Team.Visibility> BY_ID = ByIdMap.continuous($$0 -> $$0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Team.Visibility> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
      public final String name;
      public final int id;

      private Visibility(final String param3, final int param4) {
         this.name = $$0;
         this.id = $$1;
      }

      public Component getDisplayName() {
         return Component.translatable("team.visibility." + this.name);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
