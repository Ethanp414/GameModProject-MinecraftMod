package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.Difficulty;

public final class LevelSettings {
   private final String levelName;
   private final GameType gameType;
   private final boolean hardcore;
   private final Difficulty difficulty;
   private final boolean allowCommands;
   private final GameRules gameRules;
   private final WorldDataConfiguration dataConfiguration;

   public LevelSettings(String $$0, GameType $$1, boolean $$2, Difficulty $$3, boolean $$4, GameRules $$5, WorldDataConfiguration $$6) {
      this.levelName = $$0;
      this.gameType = $$1;
      this.hardcore = $$2;
      this.difficulty = $$3;
      this.allowCommands = $$4;
      this.gameRules = $$5;
      this.dataConfiguration = $$6;
   }

   public static LevelSettings parse(Dynamic<?> $$0, WorldDataConfiguration $$1) {
      GameType $$2 = GameType.byId($$0.get("GameType").asInt(0));
      return new LevelSettings(
         $$0.get("LevelName").asString(""),
         $$2,
         $$0.get("hardcore").asBoolean(false),
         (Difficulty)$$0.get("Difficulty").asNumber().map($$0x -> Difficulty.byId($$0x.byteValue())).result().orElse(Difficulty.NORMAL),
         $$0.get("allowCommands").asBoolean($$2 == GameType.CREATIVE),
         new GameRules($$1.enabledFeatures(), $$0.get("GameRules")),
         $$1
      );
   }

   public String levelName() {
      return this.levelName;
   }

   public GameType gameType() {
      return this.gameType;
   }

   public boolean hardcore() {
      return this.hardcore;
   }

   public Difficulty difficulty() {
      return this.difficulty;
   }

   public boolean allowCommands() {
      return this.allowCommands;
   }

   public GameRules gameRules() {
      return this.gameRules;
   }

   public WorldDataConfiguration getDataConfiguration() {
      return this.dataConfiguration;
   }

   public LevelSettings withGameType(GameType $$0) {
      return new LevelSettings(this.levelName, $$0, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataConfiguration);
   }

   public LevelSettings withDifficulty(Difficulty $$0) {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, $$0, this.allowCommands, this.gameRules, this.dataConfiguration);
   }

   public LevelSettings withDataConfiguration(WorldDataConfiguration $$0) {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, $$0);
   }

   public LevelSettings copy() {
      return new LevelSettings(
         this.levelName,
         this.gameType,
         this.hardcore,
         this.difficulty,
         this.allowCommands,
         this.gameRules.copy(this.dataConfiguration.enabledFeatures()),
         this.dataConfiguration
      );
   }
}
