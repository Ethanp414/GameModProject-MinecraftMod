package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.RegistryObject;

public class BlockReptileEgg extends Block {
   public static final IntegerProperty HATCH = BlockStateProperties.f_61416_;
   public static final IntegerProperty EGGS = BlockStateProperties.f_61415_;
   private static final VoxelShape ONE_EGG_SHAPE = Block.m_49796_(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
   private static final VoxelShape MULTI_EGG_SHAPE = Block.m_49796_(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
   private final RegistryObject<EntityType> births;

   public BlockReptileEgg(RegistryObject births) {
      super(Properties.m_284310_().m_284180_(MapColor.f_283761_).m_60978_(0.5F).m_60918_(SoundType.f_56743_).m_60977_().m_60955_());
      this.m_49959_((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(HATCH, 0)).m_61124_(EGGS, 1));
      this.births = births;
   }

   public static boolean hasProperHabitat(BlockGetter reader, BlockPos blockReader) {
      return isProperHabitat(reader, blockReader.m_7495_());
   }

   public static boolean isProperHabitat(BlockGetter reader, BlockPos pos) {
      return reader.m_8055_(pos).m_204336_(BlockTags.f_13029_) || reader.m_8055_(pos).m_204336_(AMTagRegistry.CROCODILE_SPAWNS);
   }

   public void m_141947_(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
      this.tryTrample(worldIn, pos, entityIn, 100);
      super.m_141947_(worldIn, pos, state, entityIn);
   }

   public void m_142072_(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
      if (!(entityIn instanceof Zombie)) {
         this.tryTrample(worldIn, pos, entityIn, 3);
      }

      super.m_142072_(worldIn, state, pos, entityIn, fallDistance);
   }

   private void tryTrample(Level worldIn, BlockPos pos, Entity trampler, int chances) {
      if (this.canTrample(worldIn, trampler) && !worldIn.f_46443_ && worldIn.f_46441_.m_188503_(chances) == 0) {
         AABB bb = new AABB(pos.m_123341_(), pos.m_123342_(), pos.m_123343_(), pos.m_123341_() + 1, pos.m_123342_() + 1, pos.m_123343_() + 1)
            .m_82377_(25.0, 25.0, 25.0);
         if (trampler instanceof LivingEntity) {
            for (Mob living : worldIn.m_6443_(Mob.class, bb, livingx -> livingx.m_6084_() && livingx.m_6095_() == this.births.get())) {
               if (!(living instanceof TamableAnimal) || !((TamableAnimal)living).m_21824_() || !((TamableAnimal)living).m_21830_((LivingEntity)trampler)) {
                  living.m_6710_((LivingEntity)trampler);
               }
            }
         }

         BlockState blockstate = worldIn.m_8055_(pos);
         this.removeOneEgg(worldIn, pos, blockstate);
      }
   }

   private void removeOneEgg(Level worldIn, BlockPos pos, BlockState state) {
      worldIn.m_5594_(null, pos, SoundEvents.f_12533_, SoundSource.BLOCKS, 0.7F, 0.9F + worldIn.f_46441_.m_188501_() * 0.2F);
      int i = (Integer)state.m_61143_(EGGS);
      if (i <= 1) {
         worldIn.m_46961_(pos, false);
      } else {
         worldIn.m_7731_(pos, (BlockState)state.m_61124_(EGGS, i - 1), 2);
         worldIn.m_220407_(GameEvent.f_157794_, pos, Context.m_223722_(state));
         worldIn.m_46796_(2001, pos, Block.m_49956_(state));
      }
   }

   public void m_213898_(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
      if (this.canGrow(worldIn) && hasProperHabitat(worldIn, pos)) {
         int i = (Integer)state.m_61143_(HATCH);
         if (i < 2) {
            worldIn.m_5594_(null, pos, SoundEvents.f_12534_, SoundSource.BLOCKS, 0.7F, 0.9F + random.m_188501_() * 0.2F);
            worldIn.m_220407_(GameEvent.f_157794_, pos, Context.m_223722_(state));
            worldIn.m_7731_(pos, (BlockState)state.m_61124_(HATCH, i + 1), 2);
         } else {
            worldIn.m_5594_(null, pos, SoundEvents.f_12535_, SoundSource.BLOCKS, 0.7F, 0.9F + random.m_188501_() * 0.2F);
            worldIn.m_220407_(GameEvent.f_157794_, pos, Context.m_223722_(state));
            worldIn.m_7471_(pos, false);

            for (int j = 0; j < state.m_61143_(EGGS); j++) {
               worldIn.m_46796_(2001, pos, Block.m_49956_(state));
               Entity fromType = ((EntityType)this.births.get()).m_20615_(worldIn);
               if (fromType instanceof Animal animal) {
                  animal.m_146762_(-24000);
                  animal.m_21446_(pos, 20);
               }

               Holder<Biome> biome = worldIn.m_204166_(pos);
               fromType.m_7678_(pos.m_123341_() + 0.3 + j * 0.2, pos.m_123342_(), pos.m_123343_() + 0.3, 0.0F, 0.0F);
               if (!worldIn.f_46443_) {
                  Player closest = worldIn.m_5788_(pos.m_123341_() + 0.5F, pos.m_123342_() + 0.5F, pos.m_123343_() + 0.5F, 20.0, EntitySelector.f_20408_);
                  if (closest != null) {
                     if (fromType instanceof TamableAnimal tamableAnimal) {
                        tamableAnimal.m_7105_(true);
                        tamableAnimal.m_21839_(true);
                        tamableAnimal.m_21828_(closest);
                     }

                     if (fromType instanceof EntityCrocodile crocodile) {
                        crocodile.setDesert(biome.m_203656_(AMTagRegistry.SPAWNS_DESERT_CROCODILES));
                     }
                  }

                  worldIn.m_7967_(fromType);
               }
            }
         }
      }
   }

   public void m_6807_(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (hasProperHabitat(worldIn, pos) && !worldIn.f_46443_) {
         worldIn.m_46796_(2005, pos, 0);
      }
   }

   private boolean canGrow(Level worldIn) {
      float f = worldIn.m_46942_(1.0F);
      return f < 0.8 && f > 0.65 ? true : worldIn.f_46441_.m_188503_(15) == 0;
   }

   public void m_6240_(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
      super.m_6240_(worldIn, player, pos, state, te, stack);
      this.removeOneEgg(worldIn, pos, state);
   }

   public boolean m_6864_(BlockState state, BlockPlaceContext useContext) {
      return useContext.m_43722_().m_41720_() == this.m_5456_() && (Integer)state.m_61143_(EGGS) < 4 || super.m_6864_(state, useContext);
   }

   @Nullable
   public BlockState m_5573_(BlockPlaceContext context) {
      BlockState blockstate = context.m_43725_().m_8055_(context.m_8083_());
      return blockstate.m_60734_() == this
         ? (BlockState)blockstate.m_61124_(EGGS, Math.min(4, (Integer)blockstate.m_61143_(EGGS) + 1))
         : super.m_5573_(context);
   }

   public VoxelShape m_5940_(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.m_61143_(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{HATCH, EGGS});
   }

   private boolean canTrample(Level worldIn, Entity trampler) {
      if (trampler instanceof EntityCrocodile || trampler instanceof EntityCaiman || trampler instanceof EntityPlatypus || trampler instanceof Bat) {
         return false;
      } else {
         return !(trampler instanceof LivingEntity) ? false : trampler instanceof Player || ForgeEventFactory.getMobGriefingEvent(worldIn, trampler);
      }
   }
}
