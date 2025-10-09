package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.LeafcutterAntAIFollowCaravan;
import com.github.alexthe666.alexsmobs.entity.ai.LeafcutterAntAIForageLeaves;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAITempt;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate.MovementType;
import com.google.common.base.Predicates;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLeafcutterAnt extends Animal implements NeutralMob, IAnimatedEntity {
   public static final Animation ANIMATION_BITE = Animation.create(13);
   protected static final EntityDimensions QUEEN_SIZE = EntityDimensions.m_20398_(1.25F, 0.98F);
   public static final ResourceLocation QUEEN_LOOT = new ResourceLocation("alexsmobs", "entities/leafcutter_ant_queen");
   private static final EntityDataAccessor<Optional<BlockPos>> LEAF_HARVESTED_POS = SynchedEntityData.m_135353_(
      EntityLeafcutterAnt.class, EntityDataSerializers.f_135039_
   );
   private static final EntityDataAccessor<Optional<BlockState>> LEAF_HARVESTED_STATE = SynchedEntityData.m_135353_(
      EntityLeafcutterAnt.class, EntityDataSerializers.f_268618_
   );
   private static final EntityDataAccessor<Boolean> HAS_LEAF = SynchedEntityData.m_135353_(EntityLeafcutterAnt.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Float> ANT_SCALE = SynchedEntityData.m_135353_(EntityLeafcutterAnt.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.m_135353_(EntityLeafcutterAnt.class, EntityDataSerializers.f_135040_);
   private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.m_135353_(EntityLeafcutterAnt.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Boolean> QUEEN = SynchedEntityData.m_135353_(EntityLeafcutterAnt.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> ANGER_TIME = SynchedEntityData.m_135353_(EntityLeafcutterAnt.class, EntityDataSerializers.f_135028_);
   private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private static final UniformInt ANGRY_TIMER = TimeUtil.m_145020_(10, 20);
   public float attachChangeProgress = 0.0F;
   public float prevAttachChangeProgress = 0.0F;
   private Direction prevAttachDir = Direction.DOWN;
   @Nullable
   private EntityLeafcutterAnt caravanHead;
   @Nullable
   private EntityLeafcutterAnt caravanTail;
   private UUID lastHurtBy;
   @Nullable
   private BlockPos hivePos = null;
   private int stayOutOfHiveCountdown;
   private int animationTick;
   private Animation currentAnimation;
   private boolean isUpsideDownNavigator;
   private static final Ingredient TEMPTATION_ITEMS = Ingredient.m_204132_(AMTagRegistry.LEAFCUTTER_ANT_FOODSTUFFS);
   private int haveBabyCooldown = 0;

   public EntityLeafcutterAnt(EntityType type, Level world) {
      super(type, world);
      this.m_21441_(BlockPathTypes.WATER, -1.0F);
      this.switchNavigator(true);
   }

   public void m_6710_(@Nullable LivingEntity entitylivingbaseIn) {
      if (!(entitylivingbaseIn instanceof Player) || !((Player)entitylivingbaseIn).m_7500_()) {
         super.m_6710_(entitylivingbaseIn);
      }
   }

   @Nullable
   protected ResourceLocation m_7582_() {
      return this.isQueen() ? QUEEN_LOOT : super.m_7582_();
   }

   public MobType m_6336_() {
      return MobType.f_21642_;
   }

   private void switchNavigator(boolean rightsideUp) {
      if (rightsideUp) {
         this.f_21342_ = new MoveControl(this);
         this.f_21344_ = new AdvancedPathNavigateNoTeleport(this, this.m_9236_(), MovementType.WALKING, true, false);
         this.isUpsideDownNavigator = false;
      } else {
         this.f_21342_ = new FlightMoveController(this, 0.6F, false);
         this.f_21344_ = new DirectPathNavigator(this, this.m_9236_());
         this.isUpsideDownNavigator = true;
      }
   }

   public boolean m_7337_(Entity entity) {
      return !(entity instanceof EntityAnteater) && super.m_7337_(entity);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_()
         .m_22268_(Attributes.f_22276_, 8.0)
         .m_22268_(Attributes.f_22277_, 32.0)
         .m_22268_(Attributes.f_22279_, 0.25)
         .m_22268_(Attributes.f_22281_, 2.0);
   }

   private static boolean isSideSolid(BlockGetter reader, BlockPos pos, Entity entityIn, Direction direction) {
      return Block.m_49918_(reader.m_8055_(pos).m_60742_(reader, pos, CollisionContext.m_82750_(entityIn)), direction);
   }

   protected void m_8099_() {
      this.f_21345_.m_25352_(0, new FloatGoal(this));
      this.f_21345_.m_25352_(1, new EntityLeafcutterAnt.ReturnToHiveGoal());
      this.f_21345_.m_25352_(2, new MeleeAttackGoal(this, 1.0, false));
      this.f_21345_.m_25352_(3, new TameableAITempt(this, 1.1, TEMPTATION_ITEMS, false));
      this.f_21345_.m_25352_(4, new LeafcutterAntAIFollowCaravan(this, 1.0));
      this.f_21345_.m_25352_(5, new LeafcutterAntAIForageLeaves(this));
      this.f_21345_.m_25352_(6, new AnimalAIWanderRanged(this, 30, 1.0, 25, 7));
      this.f_21345_.m_25352_(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.f_21345_.m_25352_(8, new RandomLookAroundGoal(this));
      this.f_21346_.m_25352_(1, new EntityLeafcutterAnt.AngerGoal(this).m_26044_(new Class[0]));
      this.f_21346_.m_25352_(2, new ResetUniversalAngerTargetGoal(this, true));
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return this.isQueen() && !this.m_6162_() ? QUEEN_SIZE : super.m_6972_(poseIn);
   }

   public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
      return false;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.f_19804_.m_135370_(ATTACHED_FACE);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new WallClimberNavigation(this, worldIn);
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return this.isQueen() ? (SoundEvent)AMSoundRegistry.LEAFCUTTER_ANT_QUEEN_HURT.get() : (SoundEvent)AMSoundRegistry.LEAFCUTTER_ANT_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return this.isQueen() ? (SoundEvent)AMSoundRegistry.LEAFCUTTER_ANT_QUEEN_HURT.get() : (SoundEvent)AMSoundRegistry.LEAFCUTTER_ANT_HURT.get();
   }

   protected void m_7355_(BlockPos pos, BlockState state) {
   }

   public void m_7334_(Entity entity) {
      if (!(entity instanceof EntityAnteater)) {
         super.m_7334_(entity);
      }
   }

   private void pacifyAllNearby() {
      this.m_21662_();

      for (EntityLeafcutterAnt ant : this.m_9236_().m_45976_(EntityLeafcutterAnt.class, this.m_20191_().m_82377_(20.0, 6.0, 20.0))) {
         ant.m_21662_();
      }
   }

   public InteractionResult m_6071_(Player player, InteractionHand hand) {
      ItemStack itemstack = player.m_21120_(hand);
      InteractionResult type = super.m_6071_(player, hand);
      if (type != InteractionResult.SUCCESS && itemstack.m_204117_(AMTagRegistry.LEAFCUTTER_ANT_FOODSTUFFS)) {
         if (this.isQueen() && this.haveBabyCooldown == 0) {
            int babies = 1 + this.f_19796_.m_188503_(1);
            this.pacifyAllNearby();

            for (int i = 0; i < babies; i++) {
               EntityLeafcutterAnt leafcutterAnt = (EntityLeafcutterAnt)((EntityType)AMEntityRegistry.LEAFCUTTER_ANT.get()).m_20615_(this.m_9236_());
               leafcutterAnt.m_20359_(this);
               leafcutterAnt.m_146762_(-24000);
               if (!this.m_9236_().f_46443_) {
                  this.m_9236_().m_7605_(this, (byte)18);
                  this.m_9236_().m_7967_(leafcutterAnt);
               }
            }

            if (!player.m_7500_()) {
               itemstack.m_41774_(1);
            }

            this.haveBabyCooldown = 24000;
            this.m_6863_(false);
         } else {
            this.pacifyAllNearby();
            if (!player.m_7500_()) {
               itemstack.m_41774_(1);
            }

            this.m_9236_().m_7605_(this, (byte)48);
            this.m_5634_(3.0F);
         }

         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void m_7822_(byte id) {
      if (id == 48) {
         for (int i = 0; i < 3; i++) {
            double d0 = this.f_19796_.m_188583_() * 0.02;
            double d1 = this.f_19796_.m_188583_() * 0.02;
            double d2 = this.f_19796_.m_188583_() * 0.02;
            this.m_9236_().m_7106_(ParticleTypes.f_123748_, this.m_20208_(1.0), this.m_20187_() + 0.5, this.m_20262_(1.0), d0, d1, d2);
         }
      } else {
         super.m_7822_(id);
      }
   }

   public void m_8119_() {
      this.prevAttachChangeProgress = this.attachChangeProgress;
      super.m_8119_();
      if (this.isQueen() && this.m_20205_() < QUEEN_SIZE.f_20377_) {
         this.m_6210_();
      }

      if (this.attachChangeProgress > 0.0F) {
         this.attachChangeProgress -= 0.25F;
      }

      this.m_274367_(this.isQueen() ? 1.0F : 0.5F);
      Vec3 vector3d = this.m_20184_();
      if (!this.m_9236_().f_46443_ && !this.isQueen()) {
         this.setBesideClimbableBlock(this.f_19862_ || this.f_19863_ && !this.m_20096_());
         if (this.m_20096_() || this.m_20072_() || this.m_20077_()) {
            this.f_19804_.m_135381_(ATTACHED_FACE, Direction.DOWN);
         } else if (this.f_19863_) {
            this.f_19804_.m_135381_(ATTACHED_FACE, Direction.UP);
         } else {
            Direction closestDirection = Direction.DOWN;
            double closestDistance = 100.0;

            for (Direction dir : HORIZONTALS) {
               BlockPos antPos = new BlockPos(Mth.m_14107_(this.m_20185_()), Mth.m_14107_(this.m_20186_()), Mth.m_14107_(this.m_20189_()));
               BlockPos offsetPos = antPos.m_121945_(dir);
               Vec3 offset = Vec3.m_82512_(offsetPos);
               if (closestDistance > this.m_20182_().m_82554_(offset) && this.m_9236_().m_46578_(offsetPos, this, dir.m_122424_())) {
                  closestDistance = this.m_20182_().m_82554_(offset);
                  closestDirection = dir;
               }
            }

            this.f_19804_.m_135381_(ATTACHED_FACE, closestDirection);
         }
      }

      boolean flag = false;
      Direction attachmentFacing = this.getAttachmentFacing();
      if (attachmentFacing != Direction.DOWN) {
         if (attachmentFacing == Direction.UP) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, 1.0, 0.0));
         } else {
            if (!this.f_19862_ && attachmentFacing != Direction.UP) {
               Vec3 vec = Vec3.m_82528_(attachmentFacing.m_122436_());
               this.m_20256_(this.m_20184_().m_82549_(vec.m_82541_().m_82542_(0.1F, 0.1F, 0.1F)));
            }

            if (!this.m_20096_() && vector3d.f_82480_ < 0.0) {
               this.m_20256_(this.m_20184_().m_82542_(1.0, 0.5, 1.0));
               flag = true;
            }
         }
      }

      if (attachmentFacing == Direction.UP) {
         this.m_20242_(true);
         this.m_20256_(vector3d.m_82542_(0.7, 1.0, 0.7));
      } else {
         this.m_20242_(false);
      }

      if (!flag && this.m_6147_()) {
         this.m_20256_(vector3d.m_82542_(1.0, 0.4, 1.0));
      }

      if (this.prevAttachDir != attachmentFacing) {
         this.attachChangeProgress = 1.0F;
      }

      this.prevAttachDir = attachmentFacing;
      if (!this.m_9236_().f_46443_) {
         if (attachmentFacing == Direction.UP && !this.isUpsideDownNavigator) {
            this.switchNavigator(false);
         }

         if (attachmentFacing != Direction.UP && this.isUpsideDownNavigator) {
            this.switchNavigator(true);
         }

         if (this.stayOutOfHiveCountdown > 0) {
            this.stayOutOfHiveCountdown--;
         }

         if (this.f_19797_ % 20 == 0 && !this.isHiveValid()) {
            this.hivePos = null;
         }

         LivingEntity attackTarget = this.m_5448_();
         if (attackTarget != null
            && this.m_20270_(attackTarget) < attackTarget.m_20205_() + this.m_20205_() + 1.0F
            && this.m_142582_(attackTarget)
            && this.getAnimation() == ANIMATION_BITE
            && this.getAnimationTick() == 6) {
            float damage = (int)this.m_21133_(Attributes.f_22281_);
            attackTarget.m_6469_(this.m_269291_().m_269333_(this), damage);
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private boolean isClimeableFromSide(BlockPos offsetPos, Direction opposite) {
      return false;
   }

   private boolean isHiveValid() {
      if (!this.hasHive()) {
         return false;
      } else {
         BlockEntity tileentity = this.m_9236_().m_7702_(this.hivePos);
         return tileentity instanceof TileEntityLeafcutterAnthill;
      }
   }

   protected void m_6763_(BlockState state) {
   }

   public boolean m_6147_() {
      return this.isBesideClimbableBlock();
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

   public int m_6784_() {
      return (Integer)this.f_19804_.m_135370_(ANGER_TIME);
   }

   public void m_7870_(int time) {
      this.f_19804_.m_135381_(ANGER_TIME, time);
   }

   public UUID m_6120_() {
      return this.lastHurtBy;
   }

   public void m_6925_(@Nullable UUID target) {
      this.lastHurtBy = target;
   }

   public void m_6825_() {
      this.m_7870_(ANGRY_TIMER.m_214085_(this.f_19796_));
   }

   protected void m_8024_() {
      if (!this.m_9236_().f_46443_) {
         this.m_21666_((ServerLevel)this.m_9236_(), false);
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(CLIMBING, (byte)0);
      this.f_19804_.m_135372_(LEAF_HARVESTED_POS, Optional.empty());
      this.f_19804_.m_135372_(LEAF_HARVESTED_STATE, Optional.empty());
      this.f_19804_.m_135372_(HAS_LEAF, false);
      this.f_19804_.m_135372_(QUEEN, false);
      this.f_19804_.m_135372_(ATTACHED_FACE, Direction.DOWN);
      this.f_19804_.m_135372_(ANT_SCALE, 1.0F);
      this.f_19804_.m_135372_(ANGER_TIME, 0);
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setAntScale(0.75F + this.f_19796_.m_188501_() * 0.3F);
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public float getAntScale() {
      return (Float)this.f_19804_.m_135370_(ANT_SCALE);
   }

   public void setAntScale(float scale) {
      this.f_19804_.m_135381_(ANT_SCALE, scale);
   }

   public BlockPos getHarvestedPos() {
      return (BlockPos)((Optional)this.f_19804_.m_135370_(LEAF_HARVESTED_POS)).orElse(null);
   }

   public void setLeafHarvestedPos(BlockPos harvestedPos) {
      this.f_19804_.m_135381_(LEAF_HARVESTED_POS, Optional.ofNullable(harvestedPos));
   }

   public BlockState getHarvestedState() {
      return (BlockState)((Optional)this.f_19804_.m_135370_(LEAF_HARVESTED_STATE)).orElse(null);
   }

   public void setLeafHarvestedState(BlockState state) {
      this.f_19804_.m_135381_(LEAF_HARVESTED_STATE, Optional.ofNullable(state));
   }

   public boolean hasLeaf() {
      return (Boolean)this.f_19804_.m_135370_(HAS_LEAF);
   }

   public void setLeaf(boolean leaf) {
      this.f_19804_.m_135381_(HAS_LEAF, leaf);
   }

   public boolean isQueen() {
      return (Boolean)this.f_19804_.m_135370_(QUEEN);
   }

   public void setQueen(boolean queen) {
      boolean prev = this.isQueen();
      if (!prev && queen) {
         this.m_21051_(Attributes.f_22276_).m_22100_(36.0);
         this.m_21051_(Attributes.f_22281_).m_22100_(6.0);
         this.m_21153_(36.0F);
      } else {
         this.m_21051_(Attributes.f_22276_).m_22100_(6.0);
         this.m_21051_(Attributes.f_22281_).m_22100_(2.0);
      }

      this.f_19804_.m_135381_(QUEEN, queen);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.f_19804_.m_135381_(ATTACHED_FACE, Direction.m_122376_(compound.m_128445_("AttachFace")));
      this.setLeaf(compound.m_128471_("Leaf"));
      this.setQueen(compound.m_128471_("Queen"));
      this.setAntScale(compound.m_128457_("AntScale"));
      BlockState blockstate = null;
      if (compound.m_128425_("HarvestedLeafState", 10)) {
         blockstate = NbtUtils.m_247651_(this.m_9236_().m_246945_(Registries.f_256747_), compound.m_128469_("HarvestedLeafState"));
         if (blockstate.m_60795_()) {
            blockstate = null;
         }
      }

      this.stayOutOfHiveCountdown = compound.m_128451_("CannotEnterHiveTicks");
      this.haveBabyCooldown = compound.m_128451_("BabyCooldown");
      this.hivePos = null;
      if (compound.m_128441_("HivePos")) {
         this.hivePos = NbtUtils.m_129239_(compound.m_128469_("HivePos"));
      }

      this.setLeafHarvestedState(blockstate);
      if (compound.m_128441_("HLPX")) {
         int i = compound.m_128451_("HLPX");
         int j = compound.m_128451_("HLPY");
         int k = compound.m_128451_("HLPZ");
         this.f_19804_.m_135381_(LEAF_HARVESTED_POS, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.f_19804_.m_135381_(LEAF_HARVESTED_POS, Optional.empty());
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128344_("AttachFace", (byte)((Direction)this.f_19804_.m_135370_(ATTACHED_FACE)).m_122411_());
      compound.m_128379_("Leaf", this.hasLeaf());
      compound.m_128379_("Queen", this.isQueen());
      compound.m_128350_("AntScale", this.getAntScale());
      BlockState blockstate = this.getHarvestedState();
      if (blockstate != null) {
         compound.m_128365_("HarvestedLeafState", NbtUtils.m_129202_(blockstate));
      }

      if (this.hasHive()) {
         compound.m_128365_("HivePos", NbtUtils.m_129224_(this.getHivePos()));
      }

      compound.m_128405_("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
      compound.m_128405_("BabyCooldown", this.haveBabyCooldown);
      BlockPos blockpos = this.getHarvestedPos();
      if (blockpos != null) {
         compound.m_128405_("HLPX", blockpos.m_123341_());
         compound.m_128405_("HLPY", blockpos.m_123342_());
         compound.m_128405_("HLPZ", blockpos.m_123343_());
      }
   }

   public void setStayOutOfHiveCountdown(int p_226450_1_) {
      this.stayOutOfHiveCountdown = p_226450_1_;
   }

   private boolean isHiveNearFire() {
      if (this.hivePos == null) {
         return false;
      } else {
         BlockEntity tileentity = this.m_9236_().m_7702_(this.hivePos);
         return tileentity instanceof TileEntityLeafcutterAnthill && ((TileEntityLeafcutterAnthill)tileentity).isNearFire();
      }
   }

   private boolean doesHiveHaveSpace(BlockPos pos) {
      BlockEntity tileentity = this.m_9236_().m_7702_(pos);
      return tileentity instanceof TileEntityLeafcutterAnthill ? !((TileEntityLeafcutterAnthill)tileentity).isFullOfAnts() : false;
   }

   public boolean hasHive() {
      return this.hivePos != null;
   }

   @Nullable
   public BlockPos getHivePos() {
      return this.hivePos;
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(EntityLeafcutterAnt caravanHeadIn) {
      this.caravanHead = caravanHeadIn;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTrail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public EntityLeafcutterAnt getCaravanHead() {
      return this.caravanHead;
   }

   @Nullable
   public AgeableMob m_142606_(ServerLevel serverWorld, AgeableMob ageableEntity) {
      return null;
   }

   public boolean shouldLeadCaravan() {
      return !this.hasLeaf();
   }

   public void m_267651_(boolean flying) {
      float f1 = (float)Mth.m_184648_(this.m_20185_() - this.f_19854_, 2.0 * (this.m_20186_() - this.f_19855_), this.m_20189_() - this.f_19856_);
      float f2 = Math.min(f1 * 4.0F, 1.0F);
      this.f_267362_.m_267566_(f2, 0.4F);
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_BITE};
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public boolean m_7327_(Entity entityIn) {
      this.setAnimation(ANIMATION_BITE);
      return true;
   }

   class AngerGoal extends HurtByTargetGoal {
      AngerGoal(EntityLeafcutterAnt beeIn) {
         super(beeIn, new Class[0]);
         this.m_26044_(new Class[]{EntityLeafcutterAnt.class});
      }

      public boolean m_8045_() {
         return EntityLeafcutterAnt.this.m_21660_() && super.m_8045_();
      }

      protected void m_5766_(Mob mobIn, LivingEntity targetIn) {
         if (mobIn instanceof EntityLeafcutterAnt && this.f_26135_.m_142582_(targetIn)) {
            mobIn.m_6710_(targetIn);
         }
      }
   }

   private class ReturnToHiveGoal extends Goal {
      private int searchCooldown = 1;
      private BlockPos hivePos;
      private int approachTime = 0;
      private int moveToCooldown = 0;

      public ReturnToHiveGoal() {
      }

      public boolean m_8036_() {
         if (EntityLeafcutterAnt.this.stayOutOfHiveCountdown > 0) {
            return false;
         } else {
            if (EntityLeafcutterAnt.this.hasLeaf() || EntityLeafcutterAnt.this.isQueen()) {
               this.searchCooldown--;
               BlockPos hive = EntityLeafcutterAnt.this.hivePos;
               if (hive != null && EntityLeafcutterAnt.this.m_9236_().m_7702_(hive) instanceof TileEntityLeafcutterAnthill) {
                  this.hivePos = hive;
                  return true;
               }

               if (this.searchCooldown <= 0) {
                  this.searchCooldown = 400;
                  PoiManager pointofinterestmanager = ((ServerLevel)EntityLeafcutterAnt.this.m_9236_()).m_8904_();
                  Stream<BlockPos> stream = pointofinterestmanager.m_27138_(
                     poiTypeHolder -> poiTypeHolder.m_203565_(AMPointOfInterestRegistry.LEAFCUTTER_ANT_HILL.getKey()),
                     Predicates.alwaysTrue(),
                     EntityLeafcutterAnt.this.m_20183_(),
                     100,
                     Occupancy.ANY
                  );
                  List<BlockPos> listOfHives = stream.collect(Collectors.toList());
                  BlockPos ret = null;

                  for (BlockPos pos : listOfHives) {
                     if (ret == null || pos.m_123331_(EntityLeafcutterAnt.this.m_20183_()) < ret.m_123331_(EntityLeafcutterAnt.this.m_20183_())) {
                        ret = pos;
                     }
                  }

                  this.hivePos = ret;
                  EntityLeafcutterAnt.this.hivePos = ret;
                  return this.hivePos != null;
               }
            }

            return false;
         }
      }

      public boolean m_8045_() {
         return this.hivePos != null && EntityLeafcutterAnt.this.m_20238_(Vec3.m_82514_(this.hivePos, 1.0)) > 1.0;
      }

      public void m_8041_() {
         this.hivePos = null;
         this.searchCooldown = 20;
         this.approachTime = 0;
      }

      public void m_8056_() {
         this.searchCooldown = 20;
         this.approachTime = 0;
         this.moveToCooldown = 10 + EntityLeafcutterAnt.this.f_19796_.m_188503_(10);
      }

      public void m_8037_() {
         if (this.moveToCooldown > 0) {
            this.moveToCooldown--;
         }

         if (this.hivePos != null) {
            double dist = EntityLeafcutterAnt.this.m_20238_(Vec3.m_82514_(this.hivePos, 1.0));
            if (dist < 1.2F
               && EntityLeafcutterAnt.this.m_20099_().equals(this.hivePos)
               && EntityLeafcutterAnt.this.m_9236_().m_7702_(this.hivePos) instanceof TileEntityLeafcutterAnthill beehivetileentity) {
               beehivetileentity.tryEnterHive(EntityLeafcutterAnt.this, EntityLeafcutterAnt.this.hasLeaf());
            }

            if (dist < 16.0) {
               this.approachTime++;
               if (dist < 4.0) {
                  Vec3 center = Vec3.m_82514_(this.hivePos, 1.1F);
                  Vec3 add = center.m_82546_(EntityLeafcutterAnt.this.m_20182_());
                  if (add.m_82553_() > 1.0) {
                     add = add.m_82541_();
                  }

                  add = add.m_82490_(0.2F);
                  EntityLeafcutterAnt.this.m_20256_(EntityLeafcutterAnt.this.m_20184_().m_82549_(add));
               }

               if (dist < (this.approachTime < 200 ? 2 : 10) && EntityLeafcutterAnt.this.m_20186_() >= this.hivePos.m_123342_()) {
                  if (EntityLeafcutterAnt.this.getAttachmentFacing() != Direction.DOWN) {
                     EntityLeafcutterAnt.this.m_20256_(EntityLeafcutterAnt.this.m_20184_().m_82520_(0.0, 0.1, 0.0));
                  }

                  EntityLeafcutterAnt.this.m_21566_()
                     .m_6849_(this.hivePos.m_123341_() + 0.5, this.hivePos.m_123342_() + 1.5, this.hivePos.m_123343_() + 0.5, 1.0);
               }

               if (this.moveToCooldown <= 0) {
                  this.moveToCooldown = 50 + EntityLeafcutterAnt.this.f_19796_.m_188503_(30);
                  EntityLeafcutterAnt.this.f_21344_.m_26566_();
                  EntityLeafcutterAnt.this.f_21344_
                     .m_26519_(this.hivePos.m_123341_() + 0.5, this.hivePos.m_123342_() + 1.6F, this.hivePos.m_123343_() + 0.5, 1.0);
               }
            } else {
               this.startMovingToFar(this.hivePos);
            }
         }
      }

      private boolean startMovingToFar(BlockPos pos) {
         if (this.moveToCooldown <= 0) {
            this.moveToCooldown = 50 + EntityLeafcutterAnt.this.f_19796_.m_188503_(30);
            EntityLeafcutterAnt.this.f_21344_.m_26529_(10.0F);
            EntityLeafcutterAnt.this.f_21344_.m_26519_(pos.m_123341_(), pos.m_123342_(), pos.m_123343_(), 1.0);
         }

         return EntityLeafcutterAnt.this.f_21344_.m_26570_() != null && EntityLeafcutterAnt.this.f_21344_.m_26570_().m_77403_();
      }
   }
}
