package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.SmartClimbPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class EntitySugarGlider extends TamableAnimal implements IFollower {
   public static final ResourceLocation SUGAR_GLIDER_REWARD = new ResourceLocation("alexsmobs", "gameplay/sugar_glider_reward");
   public static final Map<Block, Item> LEAF_TO_SAPLING = (Map<Block, Item>)Util.m_137469_(Maps.newHashMap(), map -> {
      map.put(Blocks.f_50050_, Items.f_42799_);
      map.put(Blocks.f_50052_, Items.f_42801_);
      map.put(Blocks.f_50051_, Items.f_42800_);
      map.put(Blocks.f_50053_, Items.f_41826_);
      map.put(Blocks.f_50054_, Items.f_41827_);
      map.put(Blocks.f_50055_, Items.f_41828_);
      map.put(Blocks.f_220838_, Items.f_220175_);
   });
   public static final Map<Block, List<Item>> LEAF_TO_RARES = (Map<Block, List<Item>>)Util.m_137469_(Maps.newHashMap(), map -> {
      map.put(Blocks.f_50050_, List.of(Items.f_42410_));
      map.put(Blocks.f_50053_, List.of((Item)AMItemRegistry.BANANA.get(), (Item)AMItemRegistry.LEAFCUTTER_ANT_PUPA.get(), Items.f_42533_));
      map.put(Blocks.f_50054_, List.of((Item)AMItemRegistry.ACACIA_BLOSSOM.get()));
   });
   private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.m_135353_(EntitySugarGlider.class, EntityDataSerializers.f_135040_);
   private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.m_135353_(EntitySugarGlider.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Boolean> GLIDING = SynchedEntityData.m_135353_(EntitySugarGlider.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> FORAGING_TIME = SynchedEntityData.m_135353_(EntitySugarGlider.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.m_135353_(EntitySugarGlider.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.m_135353_(EntitySugarGlider.class, EntityDataSerializers.f_135035_);
   private static final Direction[] POSSIBLE_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private final int attachChangeCooldown = 0;
   public float glideProgress;
   public float prevGlideProgress;
   public float forageProgress;
   public float prevForageProgress;
   public float sitProgress;
   public float prevSitProgress;
   public float attachChangeProgress = 0.0F;
   public float prevAttachChangeProgress = 0.0F;
   public Direction prevAttachDir = Direction.DOWN;
   private boolean isGlidingNavigator;
   private boolean stopClimbing = false;
   private int forageCooldown = 0;
   private int detachCooldown = 0;
   private int rideCooldown = 0;

   protected EntitySugarGlider(EntityType type, Level level) {
      super(type, level);
      this.m_21441_(BlockPathTypes.WATER, -1.0F);
      this.switchNavigator(true);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 8.0).m_22268_(Attributes.f_22281_, 2.0).m_22268_(Attributes.f_22279_, 0.25);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new SitWhenOrderedToGoal(this));
      this.f_21345_.m_25352_(2, new FlyingAIFollowOwner(this, 1.0, 5.0F, 2.0F, true));
      this.f_21345_
         .m_25352_(
            3,
            new TemptGoal(
               this,
               1.1,
               Ingredient.m_43938_(Stream.of(new TagValue(AMTagRegistry.SUGAR_GLIDER_BREEDABLES), new TagValue(AMTagRegistry.SUGAR_GLIDER_TAMEABLES))),
               false
            ) {
               public void m_8056_() {
                  super.m_8056_();
                  EntitySugarGlider.this.f_19804_.m_135381_(EntitySugarGlider.ATTACHED_FACE, Direction.DOWN);
               }
            }
         );
      this.f_21345_.m_25352_(4, new BreedGoal(this, 0.8) {
         public void m_8056_() {
            super.m_8056_();
            EntitySugarGlider.this.f_19804_.m_135381_(EntitySugarGlider.ATTACHED_FACE, Direction.DOWN);
         }
      });
      this.f_21345_.m_25352_(5, new EntitySugarGlider.GlideGoal());
      this.f_21345_.m_25352_(6, new PanicGoal(this, 1.0));
      this.f_21345_.m_25352_(7, new AnimalAIWanderRanged(this, 100, 1.0, 10, 7));
      this.f_21345_.m_25352_(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(9, new RandomLookAroundGoal(this));
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(CLIMBING, (byte)0);
      this.f_19804_.m_135372_(ATTACHED_FACE, Direction.DOWN);
      this.f_19804_.m_135372_(GLIDING, false);
      this.f_19804_.m_135372_(FORAGING_TIME, 0);
      this.f_19804_.m_135372_(COMMAND, 0);
      this.f_19804_.m_135372_(SITTING, false);
   }

   private void switchNavigator(boolean onGround) {
      if (onGround) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new SmartClimbPathNavigator(this, this.m_9236_());
         this.isGlidingNavigator = false;
      } else {
         this.f_21342_ = new FlightMoveController(this, 0.6F, false);
         this.f_21344_ = new DirectPathNavigator(this, this.m_9236_());
         this.isGlidingNavigator = true;
      }
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.SUGAR_GLIDER_BREEDABLES);
   }

   protected SoundEvent m_7515_() {
      return (SoundEvent)AMSoundRegistry.SUGAR_GLIDER_IDLE.get();
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.SUGAR_GLIDER_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.SUGAR_GLIDER_HURT.get();
   }

   public static boolean canSugarGliderSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return m_186209_(worldIn, pos);
   }

   public boolean m_6914_(LevelReader reader) {
      if (reader.m_45784_(this) && !reader.m_46855_(this.m_20191_())) {
         BlockPos blockpos = this.m_20183_();
         BlockState blockstate2 = reader.m_8055_(blockpos.m_7495_());
         return blockstate2.m_204336_(BlockTags.f_13035_) || blockstate2.m_204336_(BlockTags.f_13106_) || blockstate2.m_60713_(Blocks.f_50440_);
      } else {
         return false;
      }
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.sugarGliderSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   public void m_8119_() {
      super.m_8119_();
      this.m_274367_(1.0F);
      this.prevGlideProgress = this.glideProgress;
      this.prevAttachChangeProgress = this.attachChangeProgress;
      this.prevForageProgress = this.forageProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.attachChangeProgress > 0.0F) {
         this.attachChangeProgress -= 0.25F;
      }

      if (this.glideProgress < 5.0F && this.isGliding()) {
         this.glideProgress += 2.5F;
      }

      if (this.glideProgress > 0.0F && !this.isGliding()) {
         this.glideProgress -= 2.5F;
      }

      if (this.forageProgress < 5.0F && this.getForagingTime() > 0) {
         this.forageProgress++;
      }

      if (this.forageProgress > 0.0F && this.getForagingTime() <= 0) {
         this.forageProgress--;
      }

      boolean sitVisual = this.m_21827_() && !this.m_20069_() && this.m_20096_();
      if (this.sitProgress < 5.0F && sitVisual) {
         this.sitProgress++;
      }

      if (this.sitProgress > 0.0F && !sitVisual) {
         this.sitProgress--;
      }

      if (this.isGliding()) {
         if (this.shouldStopGliding()) {
            this.setGliding(false);
         } else {
            this.m_20256_(this.m_20184_().m_82542_(0.99, 0.5, 0.99));
         }
      }

      Vec3 vector3d = this.m_20184_();
      if (!this.m_9236_().f_46443_) {
         this.setBesideClimbableBlock(this.f_19862_);
         if (!this.m_20096_() && !this.m_21827_() && !this.m_20072_() && !this.m_20077_() && !this.isGliding() && !this.m_20159_()) {
            Direction closestDirection = Direction.DOWN;
            double closestDistance = 100.0;

            for (Direction dir : POSSIBLE_DIRECTIONS) {
               BlockPos antPos = new BlockPos(Mth.m_14107_(this.m_20185_()), Mth.m_14107_(this.m_20186_()), Mth.m_14107_(this.m_20189_()));
               BlockPos offsetPos = antPos.m_121945_(dir);
               Vec3 offset = Vec3.m_82512_(offsetPos);
               if (closestDistance > this.m_20182_().m_82554_(offset) && this.m_9236_().m_46578_(offsetPos, this, dir.m_122424_())) {
                  closestDistance = this.m_20182_().m_82554_(offset);
                  closestDirection = dir;
               }
            }

            this.f_19804_.m_135381_(ATTACHED_FACE, closestDistance > this.m_20205_() * 0.5F + 0.7F ? Direction.DOWN : closestDirection);
         } else {
            this.f_19804_.m_135381_(ATTACHED_FACE, Direction.DOWN);
         }
      }

      boolean flag = false;
      if (this.getAttachmentFacing() != Direction.DOWN) {
         if (!this.f_19862_ && this.getAttachmentFacing() != Direction.UP) {
            Vec3 vec = Vec3.m_82528_(this.getAttachmentFacing().m_122436_());
            this.m_20256_(vector3d.m_82549_(vec.m_82541_().m_82542_(0.1F, 0.1F, 0.1F)));
         }

         if (!this.m_20096_() && vector3d.f_82480_ < 0.0) {
            this.m_20256_(vector3d.m_82542_(1.0, 0.5, 1.0));
            flag = true;
         }
      }

      if (this.getAttachmentFacing() != Direction.DOWN && !this.isGliding()) {
         this.m_20242_(true);
         this.m_20256_(vector3d.m_82542_(0.6, 0.4, 0.6));
      } else {
         this.m_20242_(false);
      }

      if (this.prevAttachDir != this.getAttachmentFacing()) {
         this.attachChangeProgress = 1.0F;
      }

      this.prevAttachDir = this.getAttachmentFacing();
      if (!this.m_9236_().f_46443_) {
         if ((this.getAttachmentFacing() == Direction.UP || this.isGliding()) && !this.isGlidingNavigator) {
            this.switchNavigator(false);
         }

         if (this.getAttachmentFacing() != Direction.UP && this.isGlidingNavigator) {
            this.switchNavigator(true);
         }
      }

      BlockPos on = this.m_20183_().m_121945_(this.getAttachmentFacing());
      if (this.shouldForage() && this.m_9236_().m_8055_(on).m_204336_(BlockTags.f_13035_)) {
         BlockState state = this.m_9236_().m_8055_(on);
         if (this.getForagingTime() < 100) {
            if (this.f_19796_.m_188503_(2) == 0) {
               for (int i = 0; i < 4 + this.f_19796_.m_188503_(2); i++) {
                  double motX = this.f_19796_.m_188583_() * 0.02;
                  double motY = this.f_19796_.m_188583_() * 0.02;
                  double motZ = this.f_19796_.m_188583_() * 0.02;
                  this.m_9236_()
                     .m_7106_(
                        new BlockParticleOption(ParticleTypes.f_123794_, state),
                        on.m_123341_() + this.f_19796_.m_188501_(),
                        on.m_123342_() + this.f_19796_.m_188501_(),
                        on.m_123343_() + this.f_19796_.m_188501_(),
                        motX,
                        motY,
                        motZ
                     );
               }
            }

            this.setForagingTime(this.getForagingTime() + 1);
         } else {
            if (!this.m_9236_().f_46443_) {
               List<ItemStack> lootList = this.getForageLoot(state);
               if (lootList.size() > 0) {
                  for (ItemStack stack : lootList) {
                     ItemEntity e = this.m_19983_(stack.m_41777_());
                     if (e != null) {
                        e.f_19812_ = true;
                        e.m_20256_(e.m_20184_().m_82542_(0.2, 0.2, 0.2));
                     }
                  }
               }
            }

            this.forageCooldown = 8000 + 8000 * this.f_19796_.m_188503_(2);
            this.setForagingTime(0);
         }
      } else {
         this.setForagingTime(0);
      }

      if (this.detachCooldown > 0) {
         this.detachCooldown--;
      }

      if (this.rideCooldown > 0) {
         this.rideCooldown--;
      }
   }

   public void m_6083_() {
      Entity entity = this.m_20202_();
      if (this.m_20159_() && !entity.m_6084_()) {
         this.m_8127_();
      } else if (this.m_21824_() && entity instanceof LivingEntity && this.m_21830_((LivingEntity)entity)) {
         this.m_20334_(0.0, 0.0, 0.0);
         this.m_8119_();
         if (this.m_20159_()) {
            Entity mount = this.m_20202_();
            if (mount instanceof Player) {
               ((LivingEntity)mount).m_7292_(new MobEffectInstance(MobEffects.f_19591_, 100, 0, true, false));
               this.f_20883_ = ((LivingEntity)mount).f_20883_;
               this.m_146922_(mount.m_146908_());
               this.f_20885_ = ((LivingEntity)mount).f_20885_;
               this.f_19859_ = ((LivingEntity)mount).f_20885_;
               float radius = 0.0F;
               float angle = (float) (Math.PI / 180.0) * (((LivingEntity)mount).f_20883_ - 180.0F);
               double extraX = 0.0F * Mth.m_14031_((float) Math.PI + angle);
               double extraZ = 0.0F * Mth.m_14089_(angle);
               this.m_6034_(mount.m_20185_() + extraX, Math.max(mount.m_20186_() + mount.m_20206_() + 0.1, mount.m_20186_()), mount.m_20189_() + extraZ);
               if (!mount.m_6084_() || this.rideCooldown == 0 && mount.m_6144_()) {
                  this.m_6038_();
               }
            }
         }
      } else {
         super.m_6083_();
      }
   }

   private List<ItemStack> getForageLoot(BlockState leafState) {
      Item sapling = LEAF_TO_SAPLING.get(leafState.m_60734_());
      List<Item> rares = LEAF_TO_RARES.get(leafState.m_60734_());
      float rng = this.m_217043_().m_188501_();
      if (rng < 0.1F && rares != null) {
         Item item = rares.size() <= 1 ? rares.get(0) : rares.get(this.m_217043_().m_188503_(rares.size()));
         return List.of(new ItemStack(item));
      } else if (rng < 0.25F && sapling != null) {
         return List.of(new ItemStack(sapling));
      } else {
         LootTable loottable = this.m_9236_().m_7654_().m_278653_().m_278676_(SUGAR_GLIDER_REWARD);
         return loottable.m_287195_(
            new net.minecraft.world.level.storage.loot.LootParams.Builder((ServerLevel)this.m_9236_())
               .m_287286_(LootContextParams.f_81455_, this)
               .m_287286_(LootContextParams.f_81461_, leafState)
               .m_287235_(LootContextParamSets.f_81417_)
         );
      }
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21827_()) {
         if (this.m_21573_().m_26570_() != null) {
            this.m_21573_().m_26573_();
         }

         travelVector = Vec3.f_82478_;
      }

      if (this.m_20069_() && this.m_20184_().f_82480_ > 0.0) {
         this.m_20256_(this.m_20184_().m_82542_(1.0, 0.5, 1.0));
      }

      super.m_7023_(travelVector);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.f_19804_.m_135381_(ATTACHED_FACE, Direction.m_122376_(compound.m_128445_("AttachFace")));
      this.setCommand(compound.m_128451_("SugarGliderCommand"));
      this.m_21839_(compound.m_128471_("SugarGliderSitting"));
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128344_("AttachFace", (byte)((Direction)this.f_19804_.m_135370_(ATTACHED_FACE)).m_122411_());
      compound.m_128405_("SugarGliderCommand", this.getCommand());
      compound.m_128379_("SugarGliderSitting", this.m_21827_());
   }

   public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
      return false;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   protected void m_6763_(BlockState state) {
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   public boolean m_6147_() {
      return this.isBesideClimbableBlock() && !this.isGliding() && !this.stopClimbing && !this.m_21827_();
   }

   public boolean isBesideClimbableBlock() {
      return ((Byte)this.f_19804_.m_135370_(CLIMBING) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = (Byte)this.f_19804_.m_135370_(CLIMBING);
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.f_19804_.m_135381_(CLIMBING, b0);
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.f_19804_.m_135370_(ATTACHED_FACE);
   }

   public boolean isGliding() {
      return (Boolean)this.f_19804_.m_135370_(GLIDING);
   }

   public void setGliding(boolean gliding) {
      this.f_19804_.m_135381_(GLIDING, gliding);
   }

   public int getForagingTime() {
      return (Integer)this.f_19804_.m_135370_(FORAGING_TIME);
   }

   public void setForagingTime(int feedingTime) {
      this.f_19804_.m_135381_(FORAGING_TIME, feedingTime);
   }

   public boolean m_21827_() {
      return (Boolean)this.f_19804_.m_135370_(SITTING);
   }

   public void m_21839_(boolean sit) {
      this.f_19804_.m_135381_(SITTING, sit);
   }

   public int getCommand() {
      return (Integer)this.f_19804_.m_135370_(COMMAND);
   }

   public void setCommand(int command) {
      this.f_19804_.m_135381_(COMMAND, command);
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      Item item = itemstack.m_41720_();
      InteractionResult type = super.m_6071_(player, hand);
      if (!this.m_21824_() && itemstack.m_204117_(AMTagRegistry.SUGAR_GLIDER_TAMEABLES)) {
         this.m_142075_(player, hand, itemstack);
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11947_, this.m_6121_(), this.m_6100_());
         if (this.m_217043_().m_188503_(2) == 0) {
            this.m_21828_(player);
            this.m_9236_().m_7605_(this, (byte)7);
         } else {
            this.m_9236_().m_7605_(this, (byte)6);
         }

         return InteractionResult.SUCCESS;
      } else if (this.m_21824_() && itemstack.m_204117_(AMTagRegistry.INSECT_ITEMS)) {
         if (this.m_21223_() < this.m_21233_()) {
            this.m_142075_(player, hand, itemstack);
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_11947_, this.m_6121_(), this.m_6100_());
            this.m_5634_(5.0F);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         InteractionResult interactionresult = itemstack.m_41647_(player, this, hand);
         if (interactionresult == InteractionResult.SUCCESS
            || type == InteractionResult.SUCCESS
            || !this.m_21824_()
            || !this.m_21830_(player)
            || this.m_6898_(itemstack)) {
            return type;
         } else if (player.m_6144_() && player.m_20197_().isEmpty()) {
            this.m_20329_(player);
            this.rideCooldown = 20;
            return InteractionResult.SUCCESS;
         } else {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
               this.setCommand(0);
            }

            player.m_5661_(Component.m_237110_("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.m_7755_()}), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
               this.m_21839_(true);
               return InteractionResult.SUCCESS;
            } else {
               this.m_21839_(false);
               return InteractionResult.SUCCESS;
            }
         }
      }
   }

   public void m_267651_(boolean b) {
      float f1 = (float)Mth.m_184648_(this.m_20185_() - this.f_19854_, (this.m_20186_() - this.f_19855_) * 2.0, this.m_20189_() - this.f_19856_);
      float f2 = Math.min(f1 * 6.0F, 1.0F);
      this.f_267362_.m_267566_(f2, 0.4F);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new WallClimberNavigation(this, worldIn) {
         protected boolean m_7632_() {
            return super.m_7632_() || ((EntitySugarGlider)this.f_26494_).isBesideClimbableBlock() || this.f_26494_.f_20899_;
         }
      };
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverLevel, AgeableMob ageableMob) {
      return (AgeableMob)((EntityType)AMEntityRegistry.SUGAR_GLIDER.get()).m_20615_(serverLevel);
   }

   private boolean shouldStopGliding() {
      return this.m_20096_() || this.getAttachmentFacing() != Direction.DOWN;
   }

   private boolean shouldForage() {
      return this.m_21824_() && !this.m_6162_() && this.forageCooldown == 0;
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   @Override
   public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
      if (!(this.m_20270_(owner) < 5.0F) && !this.m_6162_()) {
         Vec3 fly = new Vec3(0.0, 0.0, 0.0);
         float f = 0.5F;
         if (this.m_20096_()) {
            fly = fly.m_82520_(0.0, 0.4, 0.0);
            f = 0.9F;
         }

         fly = fly.m_82549_(owner.m_146892_().m_82546_(this.m_20182_()).m_82541_().m_82490_(f));
         this.m_20256_(fly);
         Vec3 move = this.m_20184_();
         double d0 = move.m_165924_();
         this.m_146926_((float)(-Mth.m_14136_(move.f_82480_, d0) * 180.0F / (float)Math.PI));
         this.m_146922_((float)Mth.m_14136_(move.f_82481_, move.f_82479_) * (180.0F / (float)Math.PI) - 90.0F);
         this.setGliding(true);
      } else {
         this.setGliding(!this.m_20096_());
         this.m_21573_().m_5624_(owner, followSpeed);
      }
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      Vec3 blockVec = Vec3.m_82512_(destinationBlock);
      BlockHitResult result = this.m_9236_()
         .m_45547_(new ClipContext(Vector3d, blockVec, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, this));
      return result.m_82425_().equals(destinationBlock);
   }

   private class GlideGoal extends Goal {
      private boolean climbing;
      private int climbTime = 0;
      private int leapSearchCooldown = 0;
      private int climbTimeout = 0;
      private BlockPos climb;
      private BlockPos glide;
      private boolean itsOver = false;
      private int airtime = 0;
      private Direction climbOffset = Direction.UP;

      private GlideGoal() {
         this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean m_8036_() {
         if (EntitySugarGlider.this.getForagingTime() <= 0
            && !EntitySugarGlider.this.m_6162_()
            && !EntitySugarGlider.this.m_21827_()
            && EntitySugarGlider.this.m_217043_().m_188503_(45) == 0) {
            if (EntitySugarGlider.this.getAttachmentFacing() != Direction.DOWN) {
               this.climb = EntitySugarGlider.this.m_20183_().m_121945_(EntitySugarGlider.this.getAttachmentFacing());
            } else {
               this.climb = this.findClimbPos();
            }

            return this.climb != null;
         } else {
            return false;
         }
      }

      public boolean m_8045_() {
         return this.climb != null
            && !this.itsOver
            && this.climbTimeout < 30
            && (!this.climbing || !EntitySugarGlider.this.m_9236_().m_46859_(this.climb) && !EntitySugarGlider.this.m_21573_().m_26577_())
            && EntitySugarGlider.this.getForagingTime() <= 0
            && !EntitySugarGlider.this.m_21827_();
      }

      public void m_8056_() {
         this.climbTimeout = 0;
         this.leapSearchCooldown = 0;
         this.airtime = 0;
         this.climbing = true;
         this.climbTime = 0;
         EntitySugarGlider.this.m_21573_().m_26573_();
      }

      public void m_8041_() {
         this.climbTimeout = 0;
         this.climb = null;
         this.glide = null;
         this.itsOver = false;
         EntitySugarGlider.this.stopClimbing = false;
         EntitySugarGlider.this.setGliding(false);
         EntitySugarGlider.this.m_21573_().m_26573_();
      }

      public void m_8037_() {
         if (this.leapSearchCooldown > 0) {
            this.leapSearchCooldown--;
         }

         if (this.climbing) {
            float inDir = EntitySugarGlider.this.getAttachmentFacing() == Direction.DOWN && EntitySugarGlider.this.m_20186_() > this.climb.m_123342_() + 0.3F
               ? 0.5F + EntitySugarGlider.this.m_20205_() * 0.5F
               : 0.5F;
            Vec3 offset = Vec3.m_82512_(this.climb)
               .m_82492_(0.0, 0.0, 0.0)
               .m_82520_(this.climbOffset.m_122429_() * inDir, this.climbOffset.m_122430_() * inDir, this.climbOffset.m_122431_() * inDir);
            double d0 = this.climb.m_123341_() + 0.5F - EntitySugarGlider.this.m_20185_();
            double d2 = this.climb.m_123343_() + 0.5F - EntitySugarGlider.this.m_20189_();
            double xzDistSqr = d0 * d0 + d2 * d2;
            if (EntitySugarGlider.this.m_20186_() > offset.f_82480_ - 0.3F - EntitySugarGlider.this.m_20206_()) {
               EntitySugarGlider.this.stopClimbing = true;
            }

            if (xzDistSqr < 3.0 && EntitySugarGlider.this.getAttachmentFacing() != Direction.DOWN) {
               Vec3 silly = new Vec3(d0, 0.0, d2).m_82541_().m_82490_(0.1);
               EntitySugarGlider.this.m_20256_(EntitySugarGlider.this.m_20184_().m_82549_(silly));
            } else {
               EntitySugarGlider.this.m_21573_().m_26519_(offset.f_82479_, offset.f_82480_, offset.f_82481_, 1.0);
            }

            if (EntitySugarGlider.this.getAttachmentFacing() == Direction.DOWN) {
               this.climbTimeout++;
               this.climbTime = 0;
            } else {
               this.climbTimeout = 0;
               this.climbTime++;
               if (this.climbTime > 40 && this.leapSearchCooldown == 0) {
                  BlockPos leapTo = this.findLeapPos(EntitySugarGlider.this.shouldForage() && EntitySugarGlider.this.f_19796_.m_188503_(5) != 0);
                  this.leapSearchCooldown = 5 + EntitySugarGlider.this.m_217043_().m_188503_(10);
                  if (leapTo != null) {
                     EntitySugarGlider.this.stopClimbing = false;
                     EntitySugarGlider.this.setGliding(true);
                     EntitySugarGlider.this.m_21573_().m_26573_();
                     EntitySugarGlider.this.f_19804_.m_135381_(EntitySugarGlider.ATTACHED_FACE, Direction.DOWN);
                     this.glide = leapTo;
                     this.climbing = false;
                  }
               }
            }
         } else if (this.glide != null) {
            EntitySugarGlider.this.stopClimbing = false;
            EntitySugarGlider.this.setGliding(true);
            if (this.airtime > 5
               && (
                  EntitySugarGlider.this.f_19862_
                     || EntitySugarGlider.this.m_20096_()
                     || Math.sqrt(EntitySugarGlider.this.m_20238_(Vec3.m_82512_(this.glide))) < 1.1F
               )) {
               EntitySugarGlider.this.setGliding(false);
               EntitySugarGlider.this.detachCooldown = 20 + EntitySugarGlider.this.f_19796_.m_188503_(80);
               this.itsOver = true;
            }

            Vec3 fly = Vec3.m_82512_(this.glide).m_82546_(EntitySugarGlider.this.m_20182_()).m_82541_().m_82490_(0.3F);
            EntitySugarGlider.this.m_20256_(fly);
            Vec3 move = EntitySugarGlider.this.m_20184_();
            double d0x = move.m_165924_();
            EntitySugarGlider.this.m_146926_((float)(-Mth.m_14136_(move.f_82480_, d0x) * 180.0F / (float)Math.PI));
            EntitySugarGlider.this.m_146922_((float)Mth.m_14136_(move.f_82481_, move.f_82479_) * (180.0F / (float)Math.PI) - 90.0F);
            this.airtime++;
         }
      }

      private BlockPos findClimbPos() {
         BlockPos mobPos = EntitySugarGlider.this.m_20183_();

         for (int i = 0; i < 15; i++) {
            BlockPos offset = mobPos.m_7918_(
               EntitySugarGlider.this.f_19796_.m_188503_(16) - 8,
               EntitySugarGlider.this.f_19796_.m_188503_(4) + 1,
               EntitySugarGlider.this.f_19796_.m_188503_(16) - 8
            );
            double d0 = offset.m_123341_() + 0.5F - EntitySugarGlider.this.m_20185_();
            double d2 = offset.m_123343_() + 0.5F - EntitySugarGlider.this.m_20189_();
            double xzDistSqr = d0 * d0 + d2 * d2;
            Vec3 blockVec = Vec3.m_82512_(offset);
            BlockHitResult result = EntitySugarGlider.this.m_9236_()
               .m_45547_(
                  new ClipContext(
                     EntitySugarGlider.this.m_146892_(), blockVec, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, EntitySugarGlider.this
                  )
               );
            if (result.m_6662_() != Type.MISS
               && xzDistSqr > 4.0
               && result.m_82434_().m_122434_() != Axis.Y
               && this.getDistanceOffGround(result.m_82425_().m_121945_(result.m_82434_())) > 3
               && this.isPositionEasilyClimbable(result.m_82425_())) {
               this.climbOffset = result.m_82434_();
               return result.m_82425_();
            }
         }

         return null;
      }

      private BlockPos findLeapPos(boolean leavesOnly) {
         BlockPos mobPos = EntitySugarGlider.this.m_20183_().m_121945_(this.climbOffset.m_122424_());

         for (int i = 0; i < 15; i++) {
            BlockPos offset = mobPos.m_7918_(
               EntitySugarGlider.this.f_19796_.m_188503_(32) - 16,
               -1 - EntitySugarGlider.this.f_19796_.m_188503_(4),
               EntitySugarGlider.this.f_19796_.m_188503_(32) - 16
            );
            Vec3 blockVec = Vec3.m_82512_(offset);
            BlockHitResult result = EntitySugarGlider.this.m_9236_()
               .m_45547_(
                  new ClipContext(
                     EntitySugarGlider.this.m_146892_(), blockVec, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, EntitySugarGlider.this
                  )
               );
            if (result.m_6662_() != Type.MISS
               && result.m_82425_().m_123331_(mobPos) > 4.0
               && (!leavesOnly || EntitySugarGlider.this.m_9236_().m_8055_(result.m_82425_()).m_204336_(BlockTags.f_13035_))) {
               return result.m_82425_();
            }
         }

         return null;
      }

      private int getDistanceOffGround(BlockPos pos) {
         int dist;
         for (dist = 0; pos.m_123342_() > -64 && EntitySugarGlider.this.m_9236_().m_46859_(pos); dist++) {
            pos = pos.m_7495_();
         }

         return dist;
      }

      private boolean isPositionEasilyClimbable(BlockPos pos) {
         pos = pos.m_7495_();

         while (pos.m_123342_() > EntitySugarGlider.this.m_20186_() && !EntitySugarGlider.this.m_9236_().m_46859_(pos)) {
            pos = pos.m_7495_();
         }

         return pos.m_123342_() <= EntitySugarGlider.this.m_20186_();
      }
   }
}
