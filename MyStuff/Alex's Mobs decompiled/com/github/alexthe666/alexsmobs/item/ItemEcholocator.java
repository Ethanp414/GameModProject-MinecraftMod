package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotEcho;
import com.github.alexthe666.alexsmobs.message.MessageSetPupfishChunkOnClient;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemEcholocator extends Item {
   public ItemEcholocator.EchoType type;

   public ItemEcholocator(Properties properties, ItemEcholocator.EchoType ender) {
      super(properties);
      this.type = ender;
   }

   private List<BlockPos> getNearbyPortals(BlockPos blockpos, ServerLevel world, int range) {
      if (this.type == ItemEcholocator.EchoType.ENDER) {
         PoiManager pointofinterestmanager = world.m_8904_();
         Stream<BlockPos> stream = pointofinterestmanager.m_27138_(
            poiTypeHolder -> poiTypeHolder.m_203565_(AMPointOfInterestRegistry.END_PORTAL_FRAME.getKey()),
            Predicates.alwaysTrue(),
            blockpos,
            range,
            Occupancy.ANY
         );
         List<BlockPos> portals = stream.collect(Collectors.toList());
         if (portals.isEmpty()) {
            BlockPos nearestMapStructure = world.m_215011_(StructureTags.f_215882_, blockpos, 100, false);
            return nearestMapStructure == null ? Collections.emptyList() : List.of(nearestMapStructure);
         } else {
            return portals;
         }
      } else if (this.type == ItemEcholocator.EchoType.PUPFISH) {
         AMWorldData data = AMWorldData.get(world);
         if (data != null && data.getPupfishChunk() != null) {
            AlexsMobs.sendMSGToAll(new MessageSetPupfishChunkOnClient(data.getPupfishChunk().f_45578_, data.getPupfishChunk().f_45579_));
            if (!data.isInPupfishChunk(blockpos)) {
               return Collections.singletonList(data.getPupfishChunk().m_151394_(blockpos.m_123342_()));
            }
         }

         return Collections.emptyList();
      } else {
         RandomSource random = world.m_213780_();

         for (int i = 0; i < 256; i++) {
            BlockPos checkPos = blockpos.m_7918_(
               random.m_188503_(range) - range / 2, random.m_188503_(range) / 2 - range / 2, random.m_188503_(range) - range / 2
            );
            if (this.isCaveAir(world, checkPos)) {
               return Collections.singletonList(checkPos);
            }
         }

         return Collections.emptyList();
      }
   }

   private boolean isCaveAir(Level world, BlockPos checkPos) {
      return world.m_8055_(checkPos).m_60795_() && world.m_45517_(LightLayer.SKY, checkPos) == 0 && world.m_45517_(LightLayer.BLOCK, checkPos) < 4;
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level worldIn, Player livingEntityIn, InteractionHand handIn) {
      ItemStack stack = livingEntityIn.m_21120_(handIn);
      boolean left = false;
      if (livingEntityIn.m_7655_() == InteractionHand.OFF_HAND && livingEntityIn.m_5737_() == HumanoidArm.RIGHT
         || livingEntityIn.m_7655_() == InteractionHand.MAIN_HAND && livingEntityIn.m_5737_() == HumanoidArm.LEFT) {
         left = true;
      }

      EntityCachalotEcho whaleEcho = new EntityCachalotEcho(worldIn, livingEntityIn, !left, this.type == ItemEcholocator.EchoType.PUPFISH);
      if (!worldIn.f_46443_ && worldIn instanceof ServerLevel) {
         BlockPos playerPos = livingEntityIn.m_20183_();
         List<BlockPos> portals = this.getNearbyPortals(playerPos, (ServerLevel)worldIn, 128);
         BlockPos pos = null;
         if (this.type == ItemEcholocator.EchoType.ENDER) {
            for (BlockPos portalPos : portals) {
               if (pos == null || pos.m_123331_(playerPos) > portalPos.m_123331_(playerPos)) {
                  pos = portalPos;
               }
            }
         } else if (this.type == ItemEcholocator.EchoType.PUPFISH) {
            for (BlockPos portalPosx : portals) {
               if (pos == null || pos.m_123331_(playerPos) > portalPosx.m_123331_(playerPos)) {
                  pos = portalPosx;
               }
            }
         } else {
            CompoundTag nbt = stack.m_41784_();
            if (nbt.m_128441_("CavePos") && nbt.m_128471_("ValidCavePos")) {
               pos = BlockPos.m_122022_(nbt.m_128454_("CavePos"));
               if (this.isCaveAir(worldIn, pos) || 1000000.0 < pos.m_123331_(playerPos)) {
                  nbt.m_128379_("ValidCavePos", false);
               }
            } else {
               for (BlockPos portalPosxx : portals) {
                  if (pos == null || pos.m_123331_(playerPos) < portalPosxx.m_123331_(playerPos)) {
                     pos = portalPosxx;
                  }
               }

               if (pos != null) {
                  nbt.m_128356_("CavePos", pos.m_121878_());
                  nbt.m_128379_("ValidCavePos", true);
                  stack.m_41751_(nbt);
               }
            }
         }

         if (pos != null) {
            double d0 = pos.m_123341_() + 0.5F - whaleEcho.m_20185_();
            double d1 = pos.m_123342_() + 0.5F - whaleEcho.m_20186_();
            double d2 = pos.m_123343_() + 0.5F - whaleEcho.m_20189_();
            whaleEcho.f_19797_ = 15;
            whaleEcho.shoot(d0, d1, d2, 0.4F, 0.3F);
            worldIn.m_7967_(whaleEcho);
            livingEntityIn.m_146850_(GameEvent.f_223698_);
            worldIn.m_6263_(
               (Player)null,
               whaleEcho.m_20185_(),
               whaleEcho.m_20186_(),
               whaleEcho.m_20189_(),
               (SoundEvent)AMSoundRegistry.CACHALOT_WHALE_CLICK.get(),
               SoundSource.PLAYERS,
               1.0F,
               1.0F
            );
            stack.m_41622_(1, livingEntityIn, player -> player.m_21190_(livingEntityIn.m_7655_()));
         }
      }

      livingEntityIn.m_36335_().m_41524_(this, 5);
      return InteractionResultHolder.m_19092_(stack, worldIn.m_5776_());
   }

   public static enum EchoType {
      ECHOLOCATION,
      ENDER,
      PUPFISH;
   }
}
