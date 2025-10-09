package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityBananaSlug extends Animal {
   private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.m_135353_(EntityBananaSlug.class, EntityDataSerializers.f_135040_);
   private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.m_135353_(EntityBananaSlug.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.m_135353_(EntityBananaSlug.class, EntityDataSerializers.f_135028_);
   private static final Direction[] POSSIBLE_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   public float trailYaw;
   public float prevTrailYaw;
   public float trailVisability;
   public float prevTrailVisability;
   public float attachChangeProgress = 0.0F;
   public float prevAttachChangeProgress = 0.0F;
   public Direction prevAttachDir = Direction.DOWN;
   public int timeUntilSlime = this.f_19796_.m_188503_(12000) + 24000;

   protected EntityBananaSlug(EntityType<? extends Animal> animal, Level level) {
      super(animal, level);
      this.prevTrailYaw = this.f_20883_;
      this.trailYaw = this.f_20883_;
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new WallClimberNavigation(this, worldIn);
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return (SoundEvent)AMSoundRegistry.BANANA_SLUG_HURT.get();
   }

   protected SoundEvent m_5592_() {
      return (SoundEvent)AMSoundRegistry.BANANA_SLUG_HURT.get();
   }

   public static boolean checkBananaSlugSpawnRules(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return !worldIn.m_8055_(pos.m_7495_()).m_60795_();
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.bananaSlugSpawnRolls, this.m_217043_(), spawnReasonIn) && super.m_5545_(worldIn, spawnReasonIn);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(CLIMBING, (byte)0);
      this.f_19804_.m_135372_(ATTACHED_FACE, Direction.DOWN);
      this.f_19804_.m_135372_(VARIANT, 0);
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

   public boolean m_6040_() {
      return true;
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setVariant(this.f_19796_.m_188503_(4));
      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public boolean m_6673_(DamageSource source) {
      return source.m_276093_(DamageTypes.f_268612_) || super.m_6673_(source);
   }

   public boolean m_6147_() {
      return this.isBesideClimbableBlock();
   }

   public boolean isBesideClimbableBlock() {
      return ((Byte)this.f_19804_.m_135370_(CLIMBING) & 1) != 0 && this.getAttachmentFacing() != Direction.DOWN;
   }

   public boolean m_6898_(ItemStack stack) {
      return stack.m_204117_(AMTagRegistry.BANANA_SLUG_BREEDABLES);
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

   protected void m_8099_() {
      this.f_21345_.m_25352_(1, new FloatGoal(this));
      this.f_21345_.m_25352_(2, new TemptGoal(this, 1.0, Ingredient.m_204132_(AMTagRegistry.BANANA_SLUG_BREEDABLES), false));
      this.f_21345_.m_25352_(3, new BreedGoal(this, 1.0));
      this.f_21345_.m_25352_(4, new AnimalAIWanderRanged(this, 40, 1.0, 10, 7));
      this.f_21345_.m_25352_(5, new LookAtPlayerGoal(this, Player.class, 5.0F));
      this.f_21345_.m_25352_(6, new RandomLookAroundGoal(this));
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 4.0).m_22268_(Attributes.f_22281_, 1.0).m_22268_(Attributes.f_22279_, 0.1F);
   }

   public void m_8119_() {
      super.m_8119_();
      this.prevTrailYaw = this.trailYaw;
      this.f_20883_ = Mth.m_14148_(this.f_20884_, this.f_20883_, this.m_8085_());
      this.trailYaw = Mth.m_14148_(this.trailYaw, this.f_20883_, 2.0F);
      this.prevTrailVisability = this.trailVisability;
      this.prevAttachChangeProgress = this.attachChangeProgress;
      boolean showTrail = this.isTrailVisible() && this.m_20184_().m_82553_() > 0.03F;
      if (this.prevAttachDir != this.getAttachmentFacing()) {
         if (this.attachChangeProgress < 5.0F) {
            this.attachChangeProgress++;
            this.trailYaw = this.f_20883_;
         } else if (this.attachChangeProgress >= 5.0F) {
            this.prevAttachDir = this.getAttachmentFacing();
         }
      } else {
         this.attachChangeProgress = 5.0F;
      }

      if (this.trailVisability < 1.0F && showTrail) {
         this.trailVisability = Math.min(1.0F, this.trailVisability + 0.1F);
      }

      if (this.trailVisability > 0.0F && !showTrail) {
         float dec = this.m_20184_().m_82553_() > 0.03F ? 1.0F : 0.1F;
         this.trailVisability = Math.max(0.0F, this.trailVisability - dec);
      }

      Vec3 vector3d = this.m_20184_();
      if (!this.m_9236_().f_46443_) {
         this.setBesideClimbableBlock(this.f_19862_);
         this.setBesideClimbableBlock(this.f_19862_ || this.f_19863_ && !this.m_20096_());
         if (this.m_20096_() || this.m_20072_() || this.m_20077_()) {
            this.f_19804_.m_135381_(ATTACHED_FACE, Direction.DOWN);
         } else if (this.f_19863_) {
            this.f_19804_.m_135381_(ATTACHED_FACE, Direction.UP);
         } else {
            boolean flag = false;
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

            this.f_19804_.m_135381_(ATTACHED_FACE, closestDirection);
         }
      }

      boolean flag = false;
      if (this.getAttachmentFacing() != Direction.DOWN) {
         if (this.getAttachmentFacing() == Direction.UP) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, 1.0, 0.0));
         } else {
            if (!this.f_19862_ && this.getAttachmentFacing() != Direction.UP) {
               Vec3 vec = Vec3.m_82528_(this.getAttachmentFacing().m_122436_());
               this.m_20256_(this.m_20184_().m_82549_(vec.m_82541_().m_82542_(0.1F, 0.1F, 0.1F)));
            }

            if (!this.m_20096_() && vector3d.f_82480_ < 0.0) {
               this.m_20256_(this.m_20184_().m_82542_(1.0, 0.5, 1.0));
               flag = true;
            }
         }
      }

      if (this.getAttachmentFacing() == Direction.UP) {
         this.m_20242_(true);
         this.m_20256_(vector3d.m_82542_(0.7, 1.0, 0.7));
      } else {
         this.m_20242_(false);
      }

      if (!flag && this.m_6147_()) {
         this.m_20256_(vector3d.m_82542_(1.0, 0.4, 1.0));
      }

      if (!this.m_9236_().f_46443_ && this.m_6084_() && !this.m_6162_() && --this.timeUntilSlime <= 0) {
         this.m_19998_((ItemLike)AMItemRegistry.BANANA_SLUG_SLIME.get());
         this.timeUntilSlime = this.f_19796_.m_188503_(12000) + 24000;
      }
   }

   protected float m_6118_() {
      return super.m_6118_();
   }

   public int m_8132_() {
      return 1;
   }

   public int m_8085_() {
      return 4;
   }

   protected void m_7355_(BlockPos pos, BlockState state) {
   }

   private boolean isTrailVisible() {
      if (this.m_20072_()) {
         return false;
      } else if (this.m_20096_()) {
         Vec3 modelBack = new Vec3(0.0, -0.1F, this.m_6162_() ? -0.35F : -0.7F).m_82524_(-this.trailYaw * (float) (Math.PI / 180.0));
         Vec3 slugBack = this.m_20182_().m_82549_(modelBack);
         BlockPos backPos = AMBlockPos.fromVec3(slugBack);
         BlockState state = this.m_9236_().m_8055_(backPos);
         VoxelShape shape = state.m_60812_(this.m_9236_(), backPos);
         if (shape.m_83281_()) {
            return false;
         } else {
            Optional<Vec3> closest = shape.m_166067_(modelBack.m_82520_(0.0, 1.0, 0.0));
            return closest.isPresent() && Math.min((float)closest.get().f_82480_, 1.0F) >= 0.8F;
         }
      } else if (this.getAttachmentFacing().m_122434_() != Axis.Y) {
         BlockPos pos = this.m_20183_().m_121945_(this.getAttachmentFacing()).m_6630_(this.m_20184_().f_82480_ <= -0.001F ? 1 : -1);
         BlockState state = this.m_9236_().m_8055_(pos);
         VoxelShape shape = state.m_60812_(this.m_9236_(), pos);
         return !shape.m_83281_();
      } else {
         return this.getAttachmentFacing() != Direction.DOWN;
      }
   }

   public void m_7350_(EntityDataAccessor<?> entityDataAccessor) {
      super.m_7350_(entityDataAccessor);
      if (ATTACHED_FACE.equals(entityDataAccessor)) {
         this.prevAttachChangeProgress = 0.0F;
         this.attachChangeProgress = 0.0F;
      }
   }

   public void m_267651_(boolean flying) {
      float f1 = (float)Mth.m_184648_(this.m_20185_() - this.f_19854_, 0.5 * (this.m_20186_() - this.f_19855_), this.m_20189_() - this.f_19856_);
      float f2 = Math.min(f1 * 16.0F, 1.0F);
      this.f_267362_.m_267566_(f2, 0.4F);
   }

   @org.jetbrains.annotations.Nullable
   public AgeableMob m_142606_(ServerLevel level, AgeableMob mob) {
      EntityBananaSlug slug = (EntityBananaSlug)((EntityType)AMEntityRegistry.BANANA_SLUG.get()).m_20615_(this.m_9236_());
      slug.setVariant(this.getVariant());
      return slug;
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128405_("Variant", this.getVariant());
      compound.m_128405_("SlimeTime", this.timeUntilSlime);
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      if (compound.m_128441_("SlimeTime")) {
         this.timeUntilSlime = compound.m_128451_("SlimeTime");
      }

      this.setVariant(compound.m_128451_("Variant"));
   }

   public int getVariant() {
      return (Integer)this.f_19804_.m_135370_(VARIANT);
   }

   public void setVariant(int i) {
      this.f_19804_.m_135381_(VARIANT, i);
   }
}
