package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

public class ServerboundSetJigsawBlockPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSetJigsawBlockPacket> STREAM_CODEC = Packet.codec(
      ServerboundSetJigsawBlockPacket::write, ServerboundSetJigsawBlockPacket::new
   );
   private final BlockPos pos;
   private final ResourceLocation name;
   private final ResourceLocation target;
   private final ResourceLocation pool;
   private final String finalState;
   private final JigsawBlockEntity.JointType joint;
   private final int selectionPriority;
   private final int placementPriority;

   public ServerboundSetJigsawBlockPacket(
      BlockPos $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3, String $$4, JigsawBlockEntity.JointType $$5, int $$6, int $$7
   ) {
      this.pos = $$0;
      this.name = $$1;
      this.target = $$2;
      this.pool = $$3;
      this.finalState = $$4;
      this.joint = $$5;
      this.selectionPriority = $$6;
      this.placementPriority = $$7;
   }

   private ServerboundSetJigsawBlockPacket(FriendlyByteBuf $$0) {
      this.pos = $$0.readBlockPos();
      this.name = $$0.readResourceLocation();
      this.target = $$0.readResourceLocation();
      this.pool = $$0.readResourceLocation();
      this.finalState = $$0.readUtf();
      this.joint = (JigsawBlockEntity.JointType)JigsawBlockEntity.JointType.CODEC.byName($$0.readUtf(), JigsawBlockEntity.JointType.ALIGNED);
      this.selectionPriority = $$0.readVarInt();
      this.placementPriority = $$0.readVarInt();
   }

   private void write(FriendlyByteBuf $$0) {
      $$0.writeBlockPos(this.pos);
      $$0.writeResourceLocation(this.name);
      $$0.writeResourceLocation(this.target);
      $$0.writeResourceLocation(this.pool);
      $$0.writeUtf(this.finalState);
      $$0.writeUtf(this.joint.getSerializedName());
      $$0.writeVarInt(this.selectionPriority);
      $$0.writeVarInt(this.placementPriority);
   }

   @Override
   public PacketType<ServerboundSetJigsawBlockPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_JIGSAW_BLOCK;
   }

   public void handle(ServerGamePacketListener $$0) {
      $$0.handleSetJigsawBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public ResourceLocation getTarget() {
      return this.target;
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public JigsawBlockEntity.JointType getJoint() {
      return this.joint;
   }

   public int getSelectionPriority() {
      return this.selectionPriority;
   }

   public int getPlacementPriority() {
      return this.placementPriority;
   }
}
