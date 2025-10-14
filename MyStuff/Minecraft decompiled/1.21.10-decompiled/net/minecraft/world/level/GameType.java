package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Abilities;
import org.jetbrains.annotations.Contract;

public enum GameType implements StringRepresentable {
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   public static final GameType DEFAULT_MODE = SURVIVAL;
   public static final StringRepresentable.EnumCodec<GameType> CODEC = StringRepresentable.fromEnum(GameType::values);
   private static final IntFunction<GameType> BY_ID = ByIdMap.continuous(GameType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec<ByteBuf, GameType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GameType::getId);
   @Deprecated
   public static final Codec<GameType> LEGACY_ID_CODEC = Codec.INT.xmap(GameType::byId, GameType::getId);
   private static final int NOT_SET = -1;
   private final int id;
   private final String name;
   private final Component shortName;
   private final Component longName;

   private GameType(final int param3, final String param4) {
      this.id = $$0;
      this.name = $$1;
      this.shortName = Component.translatable("selectWorld.gameMode." + $$1);
      this.longName = Component.translatable("gameMode." + $$1);
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public Component getLongDisplayName() {
      return this.longName;
   }

   public Component getShortDisplayName() {
      return this.shortName;
   }

   public void updatePlayerAbilities(Abilities $$0) {
      if (this == CREATIVE) {
         $$0.mayfly = true;
         $$0.instabuild = true;
         $$0.invulnerable = true;
      } else if (this == SPECTATOR) {
         $$0.mayfly = true;
         $$0.instabuild = false;
         $$0.invulnerable = true;
         $$0.flying = true;
      } else {
         $$0.mayfly = false;
         $$0.instabuild = false;
         $$0.invulnerable = false;
         $$0.flying = false;
      }

      $$0.mayBuild = !this.isBlockPlacingRestricted();
   }

   public boolean isBlockPlacingRestricted() {
      return this == ADVENTURE || this == SPECTATOR;
   }

   public boolean isCreative() {
      return this == CREATIVE;
   }

   public boolean isSurvival() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   public static GameType byId(int $$0) {
      return (GameType)BY_ID.apply($$0);
   }

   public static GameType byName(String $$0) {
      return byName($$0, SURVIVAL);
   }

   @Nullable
   @Contract("_,!null->!null;_,null->_")
   public static GameType byName(String $$0, @Nullable GameType $$1) {
      GameType $$2 = (GameType)CODEC.byName($$0);
      return $$2 != null ? $$2 : $$1;
   }

   public static int getNullableId(@Nullable GameType $$0) {
      return $$0 != null ? $$0.id : -1;
   }

   @Nullable
   public static GameType byNullableId(int $$0) {
      return $$0 == -1 ? null : byId($$0);
   }

   public static boolean isValidId(int $$0) {
      return Arrays.stream(values()).anyMatch($$1 -> $$1.id == $$0);
   }
}
