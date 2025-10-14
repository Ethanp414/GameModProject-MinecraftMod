package net.minecraft.network;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketUtils;

public interface PacketListener {
   PacketFlow flow();

   ConnectionProtocol protocol();

   void onDisconnect(DisconnectionDetails var1);

   default void onPacketError(Packet $$0, Exception $$1) throws ReportedException {
      throw PacketUtils.makeReportedException($$1, $$0, this);
   }

   default DisconnectionDetails createDisconnectionInfo(Component $$0, Throwable $$1) {
      return new DisconnectionDetails($$0);
   }

   boolean isAcceptingMessages();

   default boolean shouldHandleMessage(Packet<?> $$0) {
      return this.isAcceptingMessages();
   }

   default void fillCrashReport(CrashReport $$0) {
      CrashReportCategory $$1 = $$0.addCategory("Connection");
      $$1.setDetail("Protocol", (CrashReportDetail<String>)(() -> this.protocol().id()));
      $$1.setDetail("Flow", (CrashReportDetail<String>)(() -> this.flow().toString()));
      this.fillListenerSpecificCrashDetails($$0, $$1);
   }

   default void fillListenerSpecificCrashDetails(CrashReport $$0, CrashReportCategory $$1) {
   }
}
