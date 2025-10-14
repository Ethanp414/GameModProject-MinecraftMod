package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum SideChainPart implements StringRepresentable {
   UNCONNECTED("unconnected"),
   RIGHT("right"),
   CENTER("center"),
   LEFT("left");

   private final String name;

   private SideChainPart(final String param3) {
      this.name = $$0;
   }

   public String toString() {
      return this.getSerializedName();
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public boolean isConnected() {
      return this != UNCONNECTED;
   }

   public boolean isConnectionTowards(SideChainPart $$0) {
      return this == CENTER || this == $$0;
   }

   public boolean isChainEnd() {
      return this != CENTER;
   }

   public SideChainPart whenConnectedToTheRight() {
      return switch(this.ordinal()) {
         case 0, 3 -> LEFT;
         case 1, 2 -> CENTER;
         default -> throw new MatchException(null, null);
      };
   }

   public SideChainPart whenConnectedToTheLeft() {
      return switch(this.ordinal()) {
         case 0, 1 -> RIGHT;
         case 2, 3 -> CENTER;
         default -> throw new MatchException(null, null);
      };
   }

   public SideChainPart whenDisconnectedFromTheRight() {
      return switch(this.ordinal()) {
         case 0, 3 -> UNCONNECTED;
         case 1, 2 -> RIGHT;
         default -> throw new MatchException(null, null);
      };
   }

   public SideChainPart whenDisconnectedFromTheLeft() {
      return switch(this.ordinal()) {
         case 0, 1 -> UNCONNECTED;
         case 2, 3 -> LEFT;
         default -> throw new MatchException(null, null);
      };
   }
}
