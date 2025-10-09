package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockSculkBoomer;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class TileEntitySculkBoomer extends BlockEntity implements GameEventListener {
   private final BlockPositionSource blockPosSource = new BlockPositionSource(this.f_58858_);
   private boolean prevOpen = false;
   private int screamTime = 0;

   public TileEntitySculkBoomer(BlockPos pos, BlockState state) {
      super((BlockEntityType)AMTileEntityRegistry.SCULK_BOOMER.get(), pos, state);
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntitySculkBoomer tileEntity) {
      boolean hasPower = false;
      if (state.m_60734_() instanceof BlockSculkBoomer && !tileEntity.m_58901_()) {
         if (tileEntity.screamTime < 0 && !(Boolean)state.m_61143_(BlockSculkBoomer.POWERED)) {
            AABB screamBox = new AABB(
               pos.m_123341_() - 4, pos.m_123342_() - 0.25F, pos.m_123343_() - 4, pos.m_123341_() + 4, pos.m_123342_() + 0.25F, pos.m_123343_() + 4.0F
            );
            level.m_46597_(pos, (BlockState)state.m_61124_(BlockSculkBoomer.OPEN, true));
            tileEntity.screamTime++;
            if (tileEntity.screamTime >= 0) {
               tileEntity.screamTime = 100;
               level.m_46597_(pos, (BlockState)state.m_61124_(BlockSculkBoomer.OPEN, false));
            }

            float screamProgress = 1.0F - tileEntity.screamTime / -20.0F;
            Vec3 center = screamBox.m_82399_();

            for (LivingEntity entity : level.m_45976_(LivingEntity.class, screamBox)) {
               double distance = 0.5 + entity.m_20182_().m_82546_(center).m_165924_();
               if (distance < 4.0F * screamProgress && distance > 3.5F * screamProgress && !isOccluded(level, Vec3.m_82512_(pos), entity.m_20182_())) {
                  entity.m_6469_(entity.m_269291_().m_269425_(), 6 + entity.m_217043_().m_188503_(3));
                  entity.m_147240_(0.4F, center.f_82479_ - entity.m_20185_(), center.f_82481_ - entity.m_20189_());
               }
            }
         }

         if (tileEntity.screamTime > 0) {
            tileEntity.screamTime--;
         }

         boolean openNow = (Boolean)state.m_61143_(BlockSculkBoomer.OPEN);
         if (!tileEntity.prevOpen && openNow) {
            SoundEvent sound = (SoundEvent)AMSoundRegistry.SCULK_BOOMER.get();
            if (level.m_213780_().m_188503_(100) == 0) {
               sound = (SoundEvent)AMSoundRegistry.SCULK_BOOMER_FART.get();
            }

            level.m_5594_((Player)null, pos, sound, SoundSource.BLOCKS, 4.0F, level.f_46441_.m_188501_() * 0.2F + 0.9F);
            level.m_7106_(
               (ParticleOptions)AMParticleRegistry.SKULK_BOOM.get(), pos.m_123341_() + 0.5F, pos.m_123342_() + 0.5F, pos.m_123343_() + 0.5F, 0.0, 0.0, 0.0
            );
         }

         tileEntity.prevOpen = openNow;
      }
   }

   public void tick() {
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      if (tag.m_128425_("ScreamCooldown", 99)) {
         this.screamTime = tag.m_128451_("ScreamCooldown");
      }
   }

   protected void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      tag.m_128405_("ScreamCooldown", this.screamTime);
   }

   public PositionSource m_142460_() {
      return this.blockPosSource;
   }

   public int m_142078_() {
      return 8;
   }

   public boolean m_214068_(ServerLevel serverLevel, GameEvent event, Context message, Vec3 from) {
      if (event == GameEvent.f_223700_ && !isOccluded(serverLevel, Vec3.m_82512_(this.m_58899_()), from)) {
         double distance = from.m_82554_(Vec3.m_82512_(this.m_58899_()));
         serverLevel.m_8767_(
            new VibrationParticleOption(new BlockPositionSource(this.m_58899_()), Mth.m_14107_(distance)),
            from.f_82479_,
            from.f_82480_,
            from.f_82481_,
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
         if (this.screamTime == 0) {
            this.screamTime = -20;
         }
      }

      return false;
   }

   private static boolean isOccluded(Level level, Vec3 vec1, Vec3 vec2) {
      Vec3 vec3 = new Vec3(Mth.m_14107_(vec1.f_82479_) + 0.5, Mth.m_14107_(vec1.f_82480_) + 0.5, Mth.m_14107_(vec1.f_82481_) + 0.5);
      Vec3 vec31 = new Vec3(Mth.m_14107_(vec2.f_82479_) + 0.5, Mth.m_14107_(vec2.f_82480_) + 0.5, Mth.m_14107_(vec2.f_82481_) + 0.5);

      for (Direction direction : Direction.values()) {
         Vec3 vec32 = vec3.m_231075_(direction, 1.0E-5F);
         if (level.m_151353_(new ClipBlockStateContext(vec32, vec31, p_223780_ -> p_223780_.m_204336_(BlockTags.f_144272_))).m_6662_() != Type.BLOCK) {
            return false;
         }
      }

      return true;
   }
}
