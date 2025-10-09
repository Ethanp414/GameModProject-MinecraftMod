package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.message.MessageUpdateTransmutablesToDisplay;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.TransmutationData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class TileEntityTransmutationTable extends BlockEntity {
   private static final ResourceLocation COMMON_ITEMS = new ResourceLocation("alexsmobs", "gameplay/transmutation_table_common");
   private static final ResourceLocation UNCOMMON_ITEMS = new ResourceLocation("alexsmobs", "gameplay/transmutation_table_uncommon");
   private static final ResourceLocation RARE_ITEMS = new ResourceLocation("alexsmobs", "gameplay/transmutation_table_rare");
   public int ticksExisted;
   private int totalTransmuteCount = 0;
   private final Map<UUID, TransmutationData> playerToData = new HashMap<>();
   private final ItemStack[] possiblities = new ItemStack[3];
   private static final Random RANDOM = new Random();
   private UUID rerollPlayerUUID = null;

   public TileEntityTransmutationTable(BlockPos pos, BlockState state) {
      super((BlockEntityType)AMTileEntityRegistry.TRANSMUTATION_TABLE.get(), pos, state);
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityTransmutationTable entity) {
      entity.tick();
   }

   private static ItemStack createFromLootTable(Player player, ResourceLocation loc) {
      if (player.m_9236_().f_46443_) {
         return ItemStack.f_41583_;
      } else {
         LootTable loottable = player.m_9236_().m_7654_().m_278653_().m_278676_(loc);
         List<ItemStack> loots = loottable.m_287195_(
            new Builder((ServerLevel)player.m_9236_()).m_287286_(LootContextParams.f_81455_, player).m_287235_(LootContextParamSets.f_81410_)
         );
         return loots.isEmpty() ? ItemStack.f_41583_ : loots.get(0);
      }
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      this.totalTransmuteCount = tag.m_128451_("TotalCount");
      ListTag list = new ListTag();

      for (Entry<UUID, TransmutationData> entry : this.playerToData.entrySet()) {
         CompoundTag innerTag = new CompoundTag();
         innerTag.m_128362_("UUID", entry.getKey());
         innerTag.m_128365_("TransmutationData", entry.getValue().saveAsNBT());
         list.add(innerTag);
      }

      tag.m_128365_("PlayerTransmutationData", list);

      for (int i = 0; i < 3; i++) {
         if (tag.m_128441_("Possibility" + i)) {
            this.possiblities[i] = ItemStack.m_41712_(tag.m_128469_("Possiblity" + i));
         }
      }
   }

   protected void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      tag.m_128405_("TotalCount", this.totalTransmuteCount);
      ListTag list = tag.m_128437_("PlayerTransmutationData", 10);
      if (!list.isEmpty()) {
         for (int i = 0; i < list.size(); i++) {
            CompoundTag compoundtag = list.m_128728_(i);
            UUID uuid = compoundtag.m_128342_("UUID");
            if (uuid != null) {
               this.playerToData.put(uuid, TransmutationData.fromNBT(compoundtag.m_128469_("TransmutationData")));
            }
         }
      }

      for (int ix = 0; ix < 3; ix++) {
         if (this.possiblities[ix] != null && !this.possiblities[ix].m_41619_()) {
            tag.m_128365_("Possiblity" + ix, this.possiblities[ix].serializeNBT());
         }
      }
   }

   private void randomizeResults(Player player) {
      this.rollPossiblity(player, 0);
      this.rollPossiblity(player, 1);
      this.rollPossiblity(player, 2);
      int dataIndex = RANDOM.nextInt(2);
      if (this.playerToData.containsKey(player.m_20148_()) && !AMConfig.limitTransmutingToLootTables) {
         TransmutationData data = this.playerToData.get(player.m_20148_());
         if (RANDOM.nextFloat() < Math.min(0.01875F * data.getTotalWeight(), 0.2F)) {
            ItemStack stack = data.getRandomItem(RANDOM);
            if (stack != null && !stack.m_41619_()) {
               this.possiblities[dataIndex] = stack;
            }
         }
      }

      AlexsMobs.sendMSGToAll(new MessageUpdateTransmutablesToDisplay(player.m_19879_(), this.possiblities[0], this.possiblities[1], this.possiblities[2]));
   }

   public void rollPossiblity(Player player, int i) {
      if (player != null && !player.m_9236_().f_46443_ && player.m_9236_() instanceof ServerLevel) {
         int safeIndex = Mth.m_14045_(i, 0, 2);

         this.possiblities[safeIndex] = createFromLootTable(player, switch (safeIndex) {
            default -> COMMON_ITEMS;
            case 1 -> UNCOMMON_ITEMS;
            case 2 -> RARE_ITEMS;
         });
      }
   }

   public boolean hasPossibilities() {
      for (int i = 0; i < 3; i++) {
         if (this.possiblities[i] == null || this.possiblities[i].m_41619_()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getPossibility(int i) {
      int safeIndex = Mth.m_14045_(i, 0, 2);
      ItemStack possible = this.possiblities[safeIndex];
      return possible == null ? ItemStack.f_41583_ : possible;
   }

   public void postTransmute(Player player, ItemStack from, ItemStack to) {
      TransmutationData data;
      if (this.playerToData.containsKey(player.m_20148_())) {
         data = this.playerToData.get(player.m_20148_());
      } else {
         data = new TransmutationData();
      }

      data.onTransmuteItem(from, to);
      this.playerToData.put(player.m_20148_(), data);
      this.totalTransmuteCount = this.totalTransmuteCount + from.m_41613_();
      if (player instanceof ServerPlayer && this.totalTransmuteCount >= 1000) {
         AMAdvancementTriggerRegistry.TRANSMUTE_1000_ITEMS.trigger((ServerPlayer)player);
      }

      this.setRerollPlayerUUID(player.m_20148_());
   }

   public void tick() {
      this.ticksExisted++;
      if (this.rerollPlayerUUID != null) {
         Player player = this.f_58857_.m_46003_(this.rerollPlayerUUID);
         if (player != null) {
            this.f_58857_
               .m_5594_(
                  null,
                  this.m_58899_(),
                  (SoundEvent)AMSoundRegistry.TRANSMUTE_ITEM.get(),
                  SoundSource.BLOCKS,
                  1.0F,
                  0.9F + player.m_217043_().m_188501_() * 0.2F
               );
            this.randomizeResults(player);
         }

         this.rerollPlayerUUID = null;
      }
   }

   public void setRerollPlayerUUID(UUID uuid) {
      this.rerollPlayerUUID = uuid;
   }
}
