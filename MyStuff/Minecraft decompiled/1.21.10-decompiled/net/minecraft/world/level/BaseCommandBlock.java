package net.minecraft.world.level;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock {
   private static final Component DEFAULT_NAME = Component.literal("@");
   private static final int NO_LAST_EXECUTION = -1;
   private long lastExecution = -1L;
   private boolean updateLastExecution = true;
   private int successCount;
   private boolean trackOutput = true;
   @Nullable
   Component lastOutput;
   private String command = "";
   @Nullable
   private Component customName;

   public int getSuccessCount() {
      return this.successCount;
   }

   public void setSuccessCount(int $$0) {
      this.successCount = $$0;
   }

   public Component getLastOutput() {
      return this.lastOutput == null ? CommonComponents.EMPTY : this.lastOutput;
   }

   public void save(ValueOutput $$0) {
      $$0.putString("Command", this.command);
      $$0.putInt("SuccessCount", this.successCount);
      $$0.storeNullable("CustomName", ComponentSerialization.CODEC, this.customName);
      $$0.putBoolean("TrackOutput", this.trackOutput);
      if (this.trackOutput) {
         $$0.storeNullable("LastOutput", ComponentSerialization.CODEC, this.lastOutput);
      }

      $$0.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution != -1L) {
         $$0.putLong("LastExecution", this.lastExecution);
      }
   }

   public void load(ValueInput $$0) {
      this.command = $$0.getStringOr("Command", "");
      this.successCount = $$0.getIntOr("SuccessCount", 0);
      this.setCustomName(BlockEntity.parseCustomNameSafe($$0, "CustomName"));
      this.trackOutput = $$0.getBooleanOr("TrackOutput", true);
      if (this.trackOutput) {
         this.lastOutput = BlockEntity.parseCustomNameSafe($$0, "LastOutput");
      } else {
         this.lastOutput = null;
      }

      this.updateLastExecution = $$0.getBooleanOr("UpdateLastExecution", true);
      if (this.updateLastExecution) {
         this.lastExecution = $$0.getLongOr("LastExecution", -1L);
      } else {
         this.lastExecution = -1L;
      }
   }

   public void setCommand(String $$0) {
      this.command = $$0;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean performCommand(Level $$0) {
      if ($$0.isClientSide() || $$0.getGameTime() == this.lastExecution) {
         return false;
      } else if ("Searge".equalsIgnoreCase(this.command)) {
         this.lastOutput = Component.literal("#itzlipofutzli");
         this.successCount = 1;
         return true;
      } else {
         this.successCount = 0;
         MinecraftServer $$1 = this.getLevel().getServer();
         if ($$1.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
            try {
               this.lastOutput = null;

               try (BaseCommandBlock.CloseableCommandBlockSource $$2 = this.createSource()) {
                  CommandSource $$3 = (CommandSource)Objects.requireNonNullElse($$2, CommandSource.NULL);
                  CommandSourceStack $$4 = this.createCommandSourceStack($$3).withCallback(($$0x, $$1x) -> {
                     if ($$0x) {
                        ++this.successCount;
                     }
                  });
                  $$1.getCommands().performPrefixedCommand($$4, this.command);
               }
            } catch (Throwable var8) {
               CrashReport $$6 = CrashReport.forThrowable(var8, "Executing command block");
               CrashReportCategory $$7 = $$6.addCategory("Command to be executed");
               $$7.setDetail("Command", this::getCommand);
               $$7.setDetail("Name", (CrashReportDetail<String>)(() -> this.getName().getString()));
               throw new ReportedException($$6);
            }
         }

         if (this.updateLastExecution) {
            this.lastExecution = $$0.getGameTime();
         } else {
            this.lastExecution = -1L;
         }

         return true;
      }
   }

   @Nullable
   private BaseCommandBlock.CloseableCommandBlockSource createSource() {
      return this.trackOutput ? new BaseCommandBlock.CloseableCommandBlockSource() : null;
   }

   public Component getName() {
      return this.customName != null ? this.customName : DEFAULT_NAME;
   }

   @Nullable
   public Component getCustomName() {
      return this.customName;
   }

   public void setCustomName(@Nullable Component $$0) {
      this.customName = $$0;
   }

   public abstract ServerLevel getLevel();

   public abstract void onUpdated();

   public void setLastOutput(@Nullable Component $$0) {
      this.lastOutput = $$0;
   }

   public void setTrackOutput(boolean $$0) {
      this.trackOutput = $$0;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }

   public InteractionResult usedBy(Player $$0) {
      if (!$$0.canUseGameMasterBlocks()) {
         return InteractionResult.PASS;
      } else {
         if ($$0.level().isClientSide()) {
            $$0.openMinecartCommandBlock(this);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public abstract Vec3 getPosition();

   public abstract CommandSourceStack createCommandSourceStack(CommandSource var1);

   public abstract boolean isValid();

   protected class CloseableCommandBlockSource implements CommandSource, AutoCloseable {
      private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT);
      private boolean closed;

      @Override
      public boolean acceptsSuccess() {
         return !this.closed && BaseCommandBlock.this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
      }

      @Override
      public boolean acceptsFailure() {
         return !this.closed;
      }

      @Override
      public boolean shouldInformAdmins() {
         return !this.closed && BaseCommandBlock.this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
      }

      @Override
      public void sendSystemMessage(Component $$0) {
         if (!this.closed) {
            BaseCommandBlock.this.lastOutput = Component.literal("[" + TIME_FORMAT.format(ZonedDateTime.now()) + "] ").append($$0);
            BaseCommandBlock.this.onUpdated();
         }
      }

      public void close() throws Exception {
         this.closed = true;
      }
   }
}
