package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.saveddata.SavedData;

public class ScoreboardSaveData extends SavedData {
   public static final String FILE_ID = "scoreboard";
   private final Scoreboard scoreboard;

   public ScoreboardSaveData(Scoreboard $$0) {
      this.scoreboard = $$0;
   }

   public void loadFrom(ScoreboardSaveData.Packed $$0) {
      $$0.objectives().forEach(this.scoreboard::loadObjective);
      $$0.scores().forEach(this.scoreboard::loadPlayerScore);
      $$0.displaySlots().forEach(($$0x, $$1) -> {
         Objective $$2 = this.scoreboard.getObjective($$1);
         this.scoreboard.setDisplayObjective($$0x, $$2);
      });
      $$0.teams().forEach(this.scoreboard::loadPlayerTeam);
   }

   public ScoreboardSaveData.Packed pack() {
      Map<DisplaySlot, String> $$0 = new EnumMap(DisplaySlot.class);

      for(DisplaySlot $$1 : DisplaySlot.values()) {
         Objective $$2 = this.scoreboard.getDisplayObjective($$1);
         if ($$2 != null) {
            $$0.put($$1, $$2.getName());
         }
      }

      return new ScoreboardSaveData.Packed(
         this.scoreboard.getObjectives().stream().map(Objective::pack).toList(),
         this.scoreboard.packPlayerScores(),
         $$0,
         this.scoreboard.getPlayerTeams().stream().map(PlayerTeam::pack).toList()
      );
   }

   public static record Packed(
      List<Objective.Packed> objectives, List<Scoreboard.PackedScore> scores, Map<DisplaySlot, String> displaySlots, List<PlayerTeam.Packed> teams
   ) {
      public static final Codec<ScoreboardSaveData.Packed> CODEC = RecordCodecBuilder.create(
         $$0 -> $$0.group(
                  Objective.Packed.CODEC.listOf().optionalFieldOf("Objectives", List.of()).forGetter(ScoreboardSaveData.Packed::objectives),
                  Scoreboard.PackedScore.CODEC.listOf().optionalFieldOf("PlayerScores", List.of()).forGetter(ScoreboardSaveData.Packed::scores),
                  Codec.unboundedMap(DisplaySlot.CODEC, Codec.STRING)
                     .optionalFieldOf("DisplaySlots", Map.of())
                     .forGetter(ScoreboardSaveData.Packed::displaySlots),
                  PlayerTeam.Packed.CODEC.listOf().optionalFieldOf("Teams", List.of()).forGetter(ScoreboardSaveData.Packed::teams)
               )
               .apply($$0, ScoreboardSaveData.Packed::new)
      );
   }
}
