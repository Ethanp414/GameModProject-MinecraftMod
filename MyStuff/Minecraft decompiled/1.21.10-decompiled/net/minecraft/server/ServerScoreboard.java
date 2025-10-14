package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardSaveData;

public class ServerScoreboard extends Scoreboard {
   public static final SavedDataType<ScoreboardSaveData> TYPE = new SavedDataType<>(
      "scoreboard", $$0 -> $$0.levelOrThrow().getScoreboard().createData(), $$0 -> {
         ServerScoreboard $$1 = $$0.levelOrThrow().getScoreboard();
         return ScoreboardSaveData.Packed.CODEC.xmap($$1::createData, ScoreboardSaveData::pack);
      }, DataFixTypes.SAVED_DATA_SCOREBOARD
   );
   private final MinecraftServer server;
   private final Set<Objective> trackedObjectives = Sets.<Objective>newHashSet();
   private final List<Runnable> dirtyListeners = Lists.newArrayList();

   public ServerScoreboard(MinecraftServer $$0) {
      this.server = $$0;
   }

   @Override
   protected void onScoreChanged(ScoreHolder $$0, Objective $$1, Score $$2) {
      super.onScoreChanged($$0, $$1, $$2);
      if (this.trackedObjectives.contains($$1)) {
         this.server
            .getPlayerList()
            .broadcastAll(
               new ClientboundSetScorePacket(
                  $$0.getScoreboardName(), $$1.getName(), $$2.value(), Optional.ofNullable($$2.display()), Optional.ofNullable($$2.numberFormat())
               )
            );
      }

      this.setDirty();
   }

   @Override
   protected void onScoreLockChanged(ScoreHolder $$0, Objective $$1) {
      super.onScoreLockChanged($$0, $$1);
      this.setDirty();
   }

   @Override
   public void onPlayerRemoved(ScoreHolder $$0) {
      super.onPlayerRemoved($$0);
      this.server.getPlayerList().broadcastAll(new ClientboundResetScorePacket($$0.getScoreboardName(), null));
      this.setDirty();
   }

   @Override
   public void onPlayerScoreRemoved(ScoreHolder $$0, Objective $$1) {
      super.onPlayerScoreRemoved($$0, $$1);
      if (this.trackedObjectives.contains($$1)) {
         this.server.getPlayerList().broadcastAll(new ClientboundResetScorePacket($$0.getScoreboardName(), $$1.getName()));
      }

      this.setDirty();
   }

   @Override
   public void setDisplayObjective(DisplaySlot $$0, @Nullable Objective $$1) {
      Objective $$2 = this.getDisplayObjective($$0);
      super.setDisplayObjective($$0, $$1);
      if ($$2 != $$1 && $$2 != null) {
         if (this.getObjectiveDisplaySlotCount($$2) > 0) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket($$0, $$1));
         } else {
            this.stopTrackingObjective($$2);
         }
      }

      if ($$1 != null) {
         if (this.trackedObjectives.contains($$1)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket($$0, $$1));
         } else {
            this.startTrackingObjective($$1);
         }
      }

      this.setDirty();
   }

   @Override
   public boolean addPlayerToTeam(String $$0, PlayerTeam $$1) {
      if (super.addPlayerToTeam($$0, $$1)) {
         this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket($$1, $$0, ClientboundSetPlayerTeamPacket.Action.ADD));
         this.updatePlayerWaypoint($$0);
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removePlayerFromTeam(String $$0, PlayerTeam $$1) {
      super.removePlayerFromTeam($$0, $$1);
      this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket($$1, $$0, ClientboundSetPlayerTeamPacket.Action.REMOVE));
      this.updatePlayerWaypoint($$0);
      this.setDirty();
   }

   @Override
   public void onObjectiveAdded(Objective $$0) {
      super.onObjectiveAdded($$0);
      this.setDirty();
   }

   @Override
   public void onObjectiveChanged(Objective $$0) {
      super.onObjectiveChanged($$0);
      if (this.trackedObjectives.contains($$0)) {
         this.server.getPlayerList().broadcastAll(new ClientboundSetObjectivePacket($$0, 2));
      }

      this.setDirty();
   }

   @Override
   public void onObjectiveRemoved(Objective $$0) {
      super.onObjectiveRemoved($$0);
      if (this.trackedObjectives.contains($$0)) {
         this.stopTrackingObjective($$0);
      }

      this.setDirty();
   }

   @Override
   public void onTeamAdded(PlayerTeam $$0) {
      super.onTeamAdded($$0);
      this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket($$0, true));
      this.setDirty();
   }

   @Override
   public void onTeamChanged(PlayerTeam $$0) {
      super.onTeamChanged($$0);
      this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket($$0, false));
      this.updateTeamWaypoints($$0);
      this.setDirty();
   }

   @Override
   public void onTeamRemoved(PlayerTeam $$0) {
      super.onTeamRemoved($$0);
      this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createRemovePacket($$0));
      this.updateTeamWaypoints($$0);
      this.setDirty();
   }

   public void addDirtyListener(Runnable $$0) {
      this.dirtyListeners.add($$0);
   }

   protected void setDirty() {
      for(Runnable $$0 : this.dirtyListeners) {
         $$0.run();
      }
   }

   public List<Packet<?>> getStartTrackingPackets(Objective $$0) {
      List<Packet<?>> $$1 = Lists.<Packet<?>>newArrayList();
      $$1.add(new ClientboundSetObjectivePacket($$0, 0));

      for(DisplaySlot $$2 : DisplaySlot.values()) {
         if (this.getDisplayObjective($$2) == $$0) {
            $$1.add(new ClientboundSetDisplayObjectivePacket($$2, $$0));
         }
      }

      for(PlayerScoreEntry $$3 : this.listPlayerScores($$0)) {
         $$1.add(
            new ClientboundSetScorePacket(
               $$3.owner(), $$0.getName(), $$3.value(), Optional.ofNullable($$3.display()), Optional.ofNullable($$3.numberFormatOverride())
            )
         );
      }

      return $$1;
   }

   public void startTrackingObjective(Objective $$0) {
      List<Packet<?>> $$1 = this.getStartTrackingPackets($$0);

      for(ServerPlayer $$2 : this.server.getPlayerList().getPlayers()) {
         for(Packet<?> $$3 : $$1) {
            $$2.connection.send($$3);
         }
      }

      this.trackedObjectives.add($$0);
   }

   public List<Packet<?>> getStopTrackingPackets(Objective $$0) {
      List<Packet<?>> $$1 = Lists.<Packet<?>>newArrayList();
      $$1.add(new ClientboundSetObjectivePacket($$0, 1));

      for(DisplaySlot $$2 : DisplaySlot.values()) {
         if (this.getDisplayObjective($$2) == $$0) {
            $$1.add(new ClientboundSetDisplayObjectivePacket($$2, $$0));
         }
      }

      return $$1;
   }

   public void stopTrackingObjective(Objective $$0) {
      List<Packet<?>> $$1 = this.getStopTrackingPackets($$0);

      for(ServerPlayer $$2 : this.server.getPlayerList().getPlayers()) {
         for(Packet<?> $$3 : $$1) {
            $$2.connection.send($$3);
         }
      }

      this.trackedObjectives.remove($$0);
   }

   public int getObjectiveDisplaySlotCount(Objective $$0) {
      int $$1 = 0;

      for(DisplaySlot $$2 : DisplaySlot.values()) {
         if (this.getDisplayObjective($$2) == $$0) {
            ++$$1;
         }
      }

      return $$1;
   }

   private ScoreboardSaveData createData() {
      ScoreboardSaveData $$0 = new ScoreboardSaveData(this);
      this.addDirtyListener($$0::setDirty);
      return $$0;
   }

   private ScoreboardSaveData createData(ScoreboardSaveData.Packed $$0) {
      ScoreboardSaveData $$1 = this.createData();
      $$1.loadFrom($$0);
      return $$1;
   }

   private void updatePlayerWaypoint(String $$0) {
      ServerPlayer $$1 = this.server.getPlayerList().getPlayerByName($$0);
      if ($$1 != null) {
         ServerLevel var4 = $$1.level();
         if (var4 instanceof ServerLevel) {
            var4.getWaypointManager().remakeConnections($$1);
         }
      }
   }

   private void updateTeamWaypoints(PlayerTeam $$0) {
      for(ServerLevel $$1 : this.server.getAllLevels()) {
         $$0.getPlayers()
            .stream()
            .map($$0x -> this.server.getPlayerList().getPlayerByName($$0x))
            .filter(Objects::nonNull)
            .forEach($$1x -> $$1.getWaypointManager().remakeConnections($$1x));
      }
   }
}
