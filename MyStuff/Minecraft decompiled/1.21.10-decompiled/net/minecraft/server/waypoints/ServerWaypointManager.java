package net.minecraft.server.waypoints;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.Sets.SetView;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.waypoints.WaypointManager;
import net.minecraft.world.waypoints.WaypointTransmitter;

public class ServerWaypointManager implements WaypointManager<WaypointTransmitter> {
   private final Set<WaypointTransmitter> waypoints = new HashSet();
   private final Set<ServerPlayer> players = new HashSet();
   private final Table<ServerPlayer, WaypointTransmitter, WaypointTransmitter.Connection> connections = HashBasedTable.create();

   public void trackWaypoint(WaypointTransmitter $$0) {
      this.waypoints.add($$0);

      for(ServerPlayer $$1 : this.players) {
         this.createConnection($$1, $$0);
      }
   }

   public void updateWaypoint(WaypointTransmitter $$0) {
      if (this.waypoints.contains($$0)) {
         Map<ServerPlayer, WaypointTransmitter.Connection> $$1 = Tables.transpose(this.connections).row($$0);
         SetView<ServerPlayer> $$2 = Sets.difference(this.players, $$1.keySet());

         for(Entry<ServerPlayer, WaypointTransmitter.Connection> $$3 : ImmutableSet.copyOf($$1.entrySet())) {
            this.updateConnection((ServerPlayer)$$3.getKey(), $$0, (WaypointTransmitter.Connection)$$3.getValue());
         }

         for(ServerPlayer $$4 : $$2) {
            this.createConnection($$4, $$0);
         }
      }
   }

   public void untrackWaypoint(WaypointTransmitter $$0) {
      this.connections.column($$0).forEach(($$0x, $$1) -> $$1.disconnect());
      Tables.transpose(this.connections).row($$0).clear();
      this.waypoints.remove($$0);
   }

   public void addPlayer(ServerPlayer $$0) {
      this.players.add($$0);

      for(WaypointTransmitter $$1 : this.waypoints) {
         this.createConnection($$0, $$1);
      }

      if ($$0.isTransmittingWaypoint()) {
         this.trackWaypoint((WaypointTransmitter)$$0);
      }
   }

   public void updatePlayer(ServerPlayer $$0) {
      Map<WaypointTransmitter, WaypointTransmitter.Connection> $$1 = this.connections.row($$0);
      SetView<WaypointTransmitter> $$2 = Sets.difference(this.waypoints, $$1.keySet());

      for(Entry<WaypointTransmitter, WaypointTransmitter.Connection> $$3 : ImmutableSet.copyOf($$1.entrySet())) {
         this.updateConnection($$0, (WaypointTransmitter)$$3.getKey(), (WaypointTransmitter.Connection)$$3.getValue());
      }

      for(WaypointTransmitter $$4 : $$2) {
         this.createConnection($$0, $$4);
      }
   }

   public void removePlayer(ServerPlayer $$0) {
      this.connections.row($$0).values().removeIf($$0x -> {
         $$0x.disconnect();
         return true;
      });
      this.untrackWaypoint((WaypointTransmitter)$$0);
      this.players.remove($$0);
   }

   public void breakAllConnections() {
      this.connections.values().forEach(WaypointTransmitter.Connection::disconnect);
      this.connections.clear();
   }

   public void remakeConnections(WaypointTransmitter $$0) {
      for(ServerPlayer $$1 : this.players) {
         this.createConnection($$1, $$0);
      }
   }

   public Set<WaypointTransmitter> transmitters() {
      return this.waypoints;
   }

   private static boolean isLocatorBarEnabledFor(ServerPlayer $$0) {
      return $$0.level().getServer().getGameRules().getBoolean(GameRules.RULE_LOCATOR_BAR);
   }

   private void createConnection(ServerPlayer $$0, WaypointTransmitter $$1) {
      if ($$0 != $$1) {
         if (isLocatorBarEnabledFor($$0)) {
            $$1.makeWaypointConnectionWith($$0).ifPresentOrElse($$2 -> {
               this.connections.put($$0, $$1, $$2);
               $$2.connect();
            }, () -> {
               WaypointTransmitter.Connection $$2 = this.connections.remove($$0, $$1);
               if ($$2 != null) {
                  $$2.disconnect();
               }
            });
         }
      }
   }

   private void updateConnection(ServerPlayer $$0, WaypointTransmitter $$1, WaypointTransmitter.Connection $$2) {
      if ($$0 != $$1) {
         if (isLocatorBarEnabledFor($$0)) {
            if (!$$2.isBroken()) {
               $$2.update();
            } else {
               $$1.makeWaypointConnectionWith($$0).ifPresentOrElse($$2x -> {
                  $$2x.connect();
                  this.connections.put($$0, $$1, $$2x);
               }, () -> {
                  $$2.disconnect();
                  this.connections.remove($$0, $$1);
               });
            }
         }
      }
   }
}
