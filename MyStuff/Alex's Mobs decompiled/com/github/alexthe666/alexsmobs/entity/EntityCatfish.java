package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityCatfish extends WaterAnimal implements FlyingAnimal, Bucketable, ContainerListener {
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.m_135353_(EntityCatfish.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Integer> CATFISH_SIZE = SynchedEntityData.m_135353_(EntityCatfish.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> SPIT_TIME = SynchedEntityData.m_135353_(EntityCatfish.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Boolean> HAS_SWALLOWED_ENTITY = SynchedEntityData.m_135353_(EntityCatfish.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<String> SWALLOWED_ENTITY_TYPE = SynchedEntityData.m_135353_(EntityCatfish.class, EntityDataSerializers.f_135030_);
   private static final EntityDataAccessor<CompoundTag> SWALLOWED_ENTITY_DATA = SynchedEntityData.m_135353_(
      EntityCatfish.class, EntityDataSerializers.f_135042_
   );
   private static final EntityDimensions SMALL_SIZE = EntityDimensions.m_20395_(0.9F, 0.6F);
   private static final EntityDimensions MEDIUM_SIZE = EntityDimensions.m_20395_(1.25F, 0.9F);
   private static final EntityDimensions LARGE_SIZE = EntityDimensions.m_20395_(1.9F, 0.9F);
   public static final ResourceLocation MEDIUM_LOOT = new ResourceLocation("alexsmobs", "entities/catfish_medium");
   public static final ResourceLocation LARGE_LOOT = new ResourceLocation("alexsmobs", "entities/catfish_large");
   public SimpleContainer catfishInventory;
   private int eatCooldown = 0;

   protected EntityCatfish(EntityType<? extends WaterAnimal> type, Level level) {
      super(type, level);
      this.initCatfishInventory();
      this.f_21342_ = new AquaticMoveController(this, 1.0F, 15.0F);
   }

   public static Builder bakeAttributes() {
      return Monster.m_33035_().m_22268_(Attributes.f_22276_, 10.0).m_22268_(Attributes.f_22279_, 0.25);
   }

   public int m_5792_() {
      return 2;
   }

   public boolean m_7296_(int sze) {
      return sze > 2;
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21345_.m_25352_(1, new TryFindWaterGoal(this));
      this.f_21345_.m_25352_(2, new PanicGoal(this, 1.0));
      this.f_21345_.m_25352_(3, new EntityCatfish.TargetFoodGoal(this));
      this.f_21345_.m_25352_(4, new TemptGoal(this, 1.0, Ingredient.m_204132_(AMTagRegistry.CATFISH_ITEM_FASCINATIONS), false));
      this.f_21345_.m_25352_(5, new EntityCatfish.FascinateLanternGoal(this));
      this.f_21345_.m_25352_(6, new AnimalAISwimBottom(this, 1.0, 7));
   }

   public boolean m_6785_(double p_27492_) {
      return !this.m_27487_() && !this.m_8023_() && !this.m_8077_();
   }

   private void initCatfishInventory() {
      SimpleContainer animalchest = this.catfishInventory;
      int size = this.getCatfishSize() > 2 ? 1 : (this.getCatfishSize() == 1 ? 9 : 3);
      this.catfishInventory = new SimpleContainer(size) {
         public boolean m_6542_(Player player) {
            return EntityCatfish.this.m_6084_() && !EntityCatfish.this.f_19817_;
         }
      };
      this.catfishInventory.m_19164_(this);
      if (animalchest != null) {
         int i = Math.min(animalchest.m_6643_(), this.catfishInventory.m_6643_());

         for (int j = 0; j < i; j++) {
            ItemStack itemstack = animalchest.m_8020_(j);
            if (!itemstack.m_41619_()) {
               this.catfishInventory.m_6836_(j, itemstack.m_41777_());
            }
         }
      }
   }

   protected void m_5907_() {
      super.m_5907_();
      if (this.catfishInventory != null) {
         for (int i = 0; i < this.catfishInventory.m_6643_(); i++) {
            this.m_19983_(this.catfishInventory.m_8020_(i));
         }

         this.catfishInventory.m_6211_();
      }

      if (this.getCatfishSize() == 2) {
         this.spit();
      }
   }

   public boolean m_8023_() {
      return super.m_8023_()
         || this.m_8077_()
         || this.m_27487_()
         || this.hasSwallowedEntity()
         || this.catfishInventory != null && !this.catfishInventory.m_7983_();
   }

   public static boolean canCatfishSpawn(
      EntityType<EntityCatfish> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return reason == MobSpawnType.SPAWNER || iServerWorld.m_8055_(pos).m_60819_().m_192917_(Fluids.f_76193_) && random.m_188503_(1) == 0;
   }

   public boolean m_5545_(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.catfishSpawnRolls, this.m_217043_(), spawnReasonIn);
   }

   protected PathNavigation m_6037_(Level worldIn) {
      return new WaterBoundPathNavigation(this, worldIn);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(FROM_BUCKET, false);
      this.f_19804_.m_135372_(CATFISH_SIZE, 0);
      this.f_19804_.m_135372_(SPIT_TIME, 0);
      this.f_19804_.m_135372_(SWALLOWED_ENTITY_TYPE, "minecraft:pig");
      this.f_19804_.m_135372_(SWALLOWED_ENTITY_DATA, new CompoundTag());
      this.f_19804_.m_135372_(HAS_SWALLOWED_ENTITY, false);
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         if (this.getSpitTime() > 0) {
            this.setSpitTime(this.getSpitTime() - 1);
         }

         if (this.eatCooldown > 0) {
            this.eatCooldown--;
         }
      }
   }

   public void m_8107_() {
      super.m_8107_();
      boolean inSeaPickle = false;
      int width = (int)Math.ceil(this.m_20205_() / 2.0F);
      int height = (int)Math.ceil(this.m_20206_() / 2.0F);
      MutableBlockPos pos = this.m_20183_().m_122032_();
      BlockPos vomitTo = null;

      for (int i = -width; i <= width; i++) {
         for (int j = -height; j <= height; j++) {
            for (int k = -width; k <= width; k++) {
               pos.m_122169_(this.m_20185_() + i, this.m_20186_() + j, this.m_20189_() + k);
               if (this.m_9236_().m_8055_(pos).m_60713_(Blocks.f_50567_)) {
                  inSeaPickle = true;
                  vomitTo = pos;
                  break;
               }
            }
         }
      }

      if (inSeaPickle && this.canSpit()) {
         if (this.getSpitTime() == 0) {
            this.m_146850_(GameEvent.f_157806_);
            this.m_5496_(SoundEvents.f_12321_, this.m_6121_(), this.m_6100_());
         }

         if (vomitTo != null) {
            Vec3 face = Vec3.m_82512_(vomitTo).m_82546_(this.getMouthVec());
            double d0 = face.m_165924_();
            this.m_146926_((float)(-Mth.m_14136_(face.f_82480_, d0) * 180.0F / (float)Math.PI));
            this.m_146922_((float)Mth.m_14136_(face.f_82481_, face.f_82479_) * (180.0F / (float)Math.PI) - 90.0F);
            this.f_20883_ = this.m_146908_();
            this.f_20885_ = this.m_146908_();
         }

         this.spit();
      }
   }

   @Nullable
   protected ResourceLocation m_7582_() {
      if (this.getCatfishSize() == 2) {
         return LARGE_LOOT;
      } else {
         return this.getCatfishSize() == 1 ? MEDIUM_LOOT : super.m_7582_();
      }
   }

   public void m_7350_(EntityDataAccessor<?> accessor) {
      if (CATFISH_SIZE.equals(accessor)) {
         this.m_6210_();
         this.m_21051_(Attributes.f_22276_).m_22100_(10.0F * this.getCatfishSize() + 10.0F);
         this.m_5634_(50.0F);
      }

      super.m_7350_(accessor);
   }

   public boolean m_27487_() {
      return (Boolean)this.f_19804_.m_135370_(FROM_BUCKET);
   }

   public void m_27497_(boolean bucketed) {
      this.f_19804_.m_135381_(FROM_BUCKET, bucketed);
   }

   public void m_6872_(@Nonnull ItemStack bucket) {
      if (this.m_8077_()) {
         bucket.m_41714_(this.m_7770_());
      }

      Bucketable.m_148822_(this, bucket);
      CompoundTag compound = bucket.m_41784_();
      this.m_7380_(compound);
   }

   public void m_142278_(@Nonnull CompoundTag compound) {
      Bucketable.m_148825_(this, compound);
      this.m_7378_(compound);
   }

   public ItemStack m_28282_() {
      int catfishSize = this.getCatfishSize();

      Item item = switch (catfishSize) {
         case 1 -> (Item)AMItemRegistry.MEDIUM_CATFISH_BUCKET.get();
         case 2 -> (Item)AMItemRegistry.LARGE_CATFISH_BUCKET.get();
         default -> (Item)AMItemRegistry.SMALL_CATFISH_BUCKET.get();
      };
      return new ItemStack(item);
   }

   public SoundEvent m_142623_() {
      return SoundEvents.f_11782_;
   }

   public int getCatfishSize() {
      return Mth.m_14045_((Integer)this.f_19804_.m_135370_(CATFISH_SIZE), 0, 2);
   }

   public void setCatfishSize(int catfishSize) {
      this.f_19804_.m_135381_(CATFISH_SIZE, catfishSize);
   }

   public int getSpitTime() {
      return (Integer)this.f_19804_.m_135370_(SPIT_TIME);
   }

   public void setSpitTime(int time) {
      this.f_19804_.m_135381_(SPIT_TIME, time);
   }

   public boolean isSpitting() {
      return this.getSpitTime() > 0;
   }

   public String getSwallowedEntityType() {
      return (String)this.f_19804_.m_135370_(SWALLOWED_ENTITY_TYPE);
   }

   public void setSwallowedEntityType(String containedEntityType) {
      this.f_19804_.m_135381_(SWALLOWED_ENTITY_TYPE, containedEntityType);
   }

   public CompoundTag getSwallowedData() {
      return (CompoundTag)this.f_19804_.m_135370_(SWALLOWED_ENTITY_DATA);
   }

   public void setSwallowedData(CompoundTag containedData) {
      this.f_19804_.m_135381_(SWALLOWED_ENTITY_DATA, containedData);
   }

   public boolean hasSwallowedEntity() {
      return (Boolean)this.f_19804_.m_135370_(HAS_SWALLOWED_ENTITY);
   }

   public void setHasSwallowedEntity(boolean swallowedEntity) {
      this.f_19804_.m_135381_(HAS_SWALLOWED_ENTITY, swallowedEntity);
   }

   public EntityDimensions m_6972_(Pose poseIn) {
      return this.getDimsForCatfish().m_20388_(this.m_6134_());
   }

   public boolean m_6469_(DamageSource source, float f) {
      if (super.m_6469_(source, f)) {
         this.spit();
         return true;
      } else {
         return false;
      }
   }

   @Nonnull
   protected InteractionResult m_6071_(@Nonnull Player player, @Nonnull InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (stack.m_41720_() == Items.f_41868_) {
         this.spit();
         return InteractionResult.m_19078_(this.m_9236_().f_46443_);
      } else {
         return Bucketable.m_148828_(player, hand, this).orElse(super.m_6071_(player, hand));
      }
   }

   public void m_7380_(CompoundTag compound) {
      super.m_7380_(compound);
      compound.m_128379_("FromBucket", this.m_27487_());
      compound.m_128350_("CatfishSize", this.getCatfishSize());
      if (this.catfishInventory != null) {
         ListTag nbttaglist = new ListTag();

         for (int i = 0; i < this.catfishInventory.m_6643_(); i++) {
            ItemStack itemstack = this.catfishInventory.m_8020_(i);
            if (!itemstack.m_41619_()) {
               CompoundTag CompoundNBT = new CompoundTag();
               CompoundNBT.m_128344_("Slot", (byte)i);
               itemstack.m_41739_(CompoundNBT);
               nbttaglist.add(CompoundNBT);
            }
         }

         compound.m_128365_("Items", nbttaglist);
      }

      compound.m_128359_("ContainedEntityType", this.getSwallowedEntityType());
      compound.m_128365_("ContainedData", this.getSwallowedData());
      compound.m_128379_("HasSwallowedEntity", this.hasSwallowedEntity());
   }

   public void m_7378_(CompoundTag compound) {
      super.m_7378_(compound);
      this.m_27497_(compound.m_128471_("FromBucket"));
      this.setCatfishSize(compound.m_128451_("CatfishSize"));
      if (this.catfishInventory != null) {
         ListTag nbttaglist = compound.m_128437_("Items", 10);
         this.initCatfishInventory();

         for (int i = 0; i < nbttaglist.size(); i++) {
            CompoundTag CompoundNBT = nbttaglist.m_128728_(i);
            int j = CompoundNBT.m_128445_("Slot") & 255;
            this.catfishInventory.m_6836_(j, ItemStack.m_41712_(CompoundNBT));
         }
      }

      this.setSwallowedEntityType(compound.m_128461_("ContainedEntityType"));
      if (!compound.m_128469_("ContainedData").m_128456_()) {
         this.setSwallowedData(compound.m_128469_("ContainedData"));
      }

      this.setHasSwallowedEntity(compound.m_128471_("HasSwallowedEntity"));
   }

   private EntityDimensions getDimsForCatfish() {
      return switch (this.getCatfishSize()) {
         case 1 -> MEDIUM_SIZE;
         case 2 -> LARGE_SIZE;
         default -> SMALL_SIZE;
      };
   }

   @Nullable
   public SpawnGroupData m_6518_(
      ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag
   ) {
      this.setCatfishSize(this.f_19796_.m_188501_() < 0.35F ? 1 : 0);
      if (this.f_19796_.m_188501_() < 0.1F) {
         Holder<Biome> holder = worldIn.m_204166_(this.m_20183_());
         if (holder.m_203656_(AMTagRegistry.SPAWNS_HUGE_CATFISH) || reason == MobSpawnType.SPAWN_EGG) {
            this.setCatfishSize(2);
         }
      }

      return super.m_6518_(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   public void m_7023_(Vec3 travelVector) {
      if (this.m_21515_() && this.m_20069_()) {
         this.m_19920_(this.m_6113_(), travelVector);
         this.m_6478_(MoverType.SELF, this.m_20184_());
         this.m_20256_(this.m_20184_().m_82490_(0.9));
         if (this.m_5448_() == null) {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.005, 0.0));
         }
      } else {
         super.m_7023_(travelVector);
      }
   }

   protected void m_7355_(BlockPos p_180429_1_, BlockState p_180429_2_) {
   }

   public void m_5757_(Container p_18983_) {
   }

   protected void m_7581_(ItemEntity itemEntity) {
      ItemStack itemstack = itemEntity.m_32055_();
      if (this.getCatfishSize() != 2 && !this.isFull() && this.catfishInventory != null && this.catfishInventory.m_19173_(itemstack).m_41619_()) {
         this.m_21053_(itemEntity);
         this.m_7938_(itemEntity, itemstack.m_41613_());
         itemEntity.m_146870_();
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
      }
   }

   public boolean isFull() {
      if (this.getCatfishSize() != 2 && this.catfishInventory != null) {
         for (int i = 0; i < this.catfishInventory.m_6643_(); i++) {
            if (this.catfishInventory.m_8020_(i).m_41619_()) {
               return false;
            }
         }

         return true;
      } else {
         return this.hasSwallowedEntity();
      }
   }

   public float m_6100_() {
      float f = (3 - this.getCatfishSize()) * 0.33F;
      return (float)(super.m_6100_() * Math.sqrt(f) * 1.2F);
   }

   public boolean swallowEntity(Entity entity) {
      if (this.getCatfishSize() == 2 && entity instanceof Mob mob) {
         this.setHasSwallowedEntity(true);
         ResourceLocation mobtype = ForgeRegistries.ENTITY_TYPES.getKey(mob.m_6095_());
         if (mobtype != null) {
            this.setSwallowedEntityType(mobtype.toString());
         }

         CompoundTag tag = new CompoundTag();
         mob.m_7380_(tag);
         this.setSwallowedData(tag);
         this.m_146850_(GameEvent.f_157806_);
         this.m_5496_(SoundEvents.f_11912_, this.m_6121_(), this.m_6100_());
         return true;
      } else {
         if (this.getCatfishSize() < 2 && entity instanceof ItemEntity item) {
            this.m_7581_(item);
         }

         return false;
      }
   }

   public boolean canSpit() {
      return this.getCatfishSize() == 2 ? this.hasSwallowedEntity() : this.catfishInventory != null && !this.catfishInventory.m_7983_();
   }

   public void spit() {
      this.setSpitTime(10);
      this.eatCooldown = 60 + this.f_19796_.m_188503_(60);
      if (this.getCatfishSize() == 2) {
         if (this.hasSwallowedEntity()) {
            EntityType type = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(this.getSwallowedEntityType()));
            if (type != null && type.m_20615_(this.m_9236_()) instanceof LivingEntity alive) {
               alive.m_7378_(this.getSwallowedData());
               alive.m_21153_(Math.max(2.0F, alive.m_21233_() * 0.25F));
               alive.m_146922_(this.f_19796_.m_188501_() * 360.0F - 180.0F);
               alive.m_146884_(this.getMouthVec());
               if (this.m_9236_().m_7967_(alive)) {
                  this.setHasSwallowedEntity(false);
                  this.setSwallowedEntityType("minecraft:pig");
                  this.setSwallowedData(new CompoundTag());
               }
            }
         }
      } else {
         ItemStack itemStack = ItemStack.f_41583_;
         int index = -1;
         if (this.catfishInventory != null) {
            for (int i = 0; i < this.catfishInventory.m_6643_(); i++) {
               if (!this.catfishInventory.m_8020_(i).m_41619_()) {
                  itemStack = this.catfishInventory.m_8020_(i);
                  index = i;
                  break;
               }
            }
         }

         if (!itemStack.m_41619_()) {
            Vec3 vec3 = this.getMouthVec();
            Vec3 vec32 = vec3.m_82546_(this.m_20182_()).m_82541_().m_82490_(0.14F);
            ItemEntity item = new ItemEntity(
               this.m_9236_(), vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, itemStack, vec32.f_82479_, vec32.f_82480_, vec32.f_82481_
            );
            item.m_20256_(Vec3.f_82478_);
            item.m_32010_(30);
            if (this.m_9236_().m_7967_(item) && this.catfishInventory != null) {
               this.catfishInventory.m_6836_(index, ItemStack.f_41583_);
            }
         }
      }
   }

   private Vec3 getMouthVec() {
      Vec3 vec3 = new Vec3(0.0, this.m_20206_() * 0.25F, this.m_20205_() * 0.8F)
         .m_82496_(this.m_146909_() * (float) (Math.PI / 180.0))
         .m_82524_(-this.m_146908_() * (float) (Math.PI / 180.0));
      return this.m_20182_().m_82549_(vec3);
   }

   private boolean isFood(Entity entity) {
      return this.getCatfishSize() == 2
         ? !entity.m_6095_().m_204039_(AMTagRegistry.CATFISH_IGNORE_EATING)
            && entity instanceof Mob
            && !(entity instanceof EntityCatfish)
            && entity.m_20206_() <= 1.0F
         : entity instanceof ItemEntity && ((ItemEntity)entity).m_32059_() > 35;
   }

   private boolean canSeeBlock(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.m_20185_(), this.m_20188_(), this.m_20189_());
      Vec3 blockVec = Vec3.m_82512_(destinationBlock);
      BlockHitResult result = this.m_9236_().m_45547_(new ClipContext(Vector3d, blockVec, Block.COLLIDER, Fluid.NONE, this));
      return result.m_82425_().equals(destinationBlock);
   }

   protected SoundEvent m_5592_() {
      return SoundEvents.f_11759_;
   }

   protected SoundEvent m_7975_(DamageSource damageSourceIn) {
      return SoundEvents.f_11761_;
   }

   public boolean m_29443_() {
      return false;
   }

   private class FascinateLanternGoal extends Goal {
      private final int searchLength;
      private final int verticalSearchRange;
      protected BlockPos destinationBlock;
      private final EntityCatfish fish;
      private int runDelay = 70;
      private int chillTime = 0;
      private int maxChillTime = 200;

      private FascinateLanternGoal(EntityCatfish fish) {
         this.m_7021_(EnumSet.of(Flag.MOVE));
         this.fish = fish;
         this.searchLength = 16;
         this.verticalSearchRange = 6;
      }

      public boolean m_8045_() {
         return this.destinationBlock != null
            && this.isSeaLantern(this.fish.m_9236_(), this.destinationBlock.m_122032_())
            && this.isCloseToLantern(16.0)
            && !this.fish.isFull();
      }

      public boolean isCloseToLantern(double dist) {
         return this.destinationBlock == null || this.fish.m_20238_(Vec3.m_82512_(this.destinationBlock)) < dist * dist;
      }

      public boolean m_8036_() {
         if (!this.fish.m_20072_()) {
            return false;
         } else if (this.runDelay > 0) {
            this.runDelay--;
            return false;
         } else {
            this.runDelay = 70 + this.fish.f_19796_.m_188503_(70);
            return !this.fish.isFull() && this.searchForDestination();
         }
      }

      public void m_8056_() {
         this.chillTime = 0;
         this.maxChillTime = 10 + EntityCatfish.this.f_19796_.m_188503_(20);
      }

      public void m_8037_() {
         Vec3 vec = Vec3.m_82512_(this.destinationBlock);
         this.fish.m_21573_().m_26519_(vec.f_82479_, vec.f_82480_, vec.f_82481_, 1.0);
         if (this.fish.m_20238_(vec) < 1.0F + this.fish.m_20205_() * 0.6F) {
            Vec3 face = vec.m_82546_(this.fish.m_20182_());
            this.fish.m_20256_(this.fish.m_20184_().m_82549_(face.m_82541_().m_82490_(0.1F)));
            if (this.chillTime++ > this.maxChillTime) {
               this.destinationBlock = null;
            }
         }
      }

      public void m_8041_() {
         this.destinationBlock = null;
      }

      protected boolean searchForDestination() {
         int lvt_1_1_ = this.searchLength;
         BlockPos lvt_3_1_ = this.fish.m_20183_();
         MutableBlockPos lvt_4_1_ = new MutableBlockPos();

         for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; lvt_6_1_++) {
               for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                  for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0;
                     lvt_8_1_ <= lvt_6_1_;
                     lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_
                  ) {
                     lvt_4_1_.m_122154_(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                     if (this.isSeaLantern(this.fish.m_9236_(), lvt_4_1_) && this.fish.canSeeBlock(lvt_4_1_)) {
                        this.destinationBlock = lvt_4_1_;
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }

      private boolean isSeaLantern(Level world, MutableBlockPos pos) {
         return world.m_8055_(pos).m_204336_(AMTagRegistry.CATFISH_BLOCK_FASCINATIONS);
      }
   }

   private class TargetFoodGoal extends Goal {
      private final EntityCatfish catfish;
      private Entity food;
      private int executionCooldown = 50;

      public TargetFoodGoal(EntityCatfish catfish) {
         this.m_7021_(EnumSet.of(Flag.MOVE));
         this.catfish = catfish;
      }

      public boolean m_8036_() {
         if (this.catfish.m_20072_() && this.catfish.eatCooldown <= 0) {
            if (this.executionCooldown > 0) {
               this.executionCooldown--;
            } else {
               this.executionCooldown = 50 + EntityCatfish.this.f_19796_.m_188503_(50);
               if (!this.catfish.isFull()) {
                  List<Entity> list = this.catfish
                     .m_9236_()
                     .m_6443_(
                        Entity.class,
                        this.catfish.m_20191_().m_82377_(8.0, 8.0, 8.0),
                        EntitySelector.f_20408_.and(entity -> entity != this.catfish && this.catfish.isFood(entity))
                     );
                  list.sort(Comparator.comparingDouble(this.catfish::m_20280_));
                  if (!list.isEmpty()) {
                     this.food = list.get(0);
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public boolean m_8045_() {
         return this.food != null && this.food.m_6084_() && !this.catfish.isFull();
      }

      public void m_8041_() {
         this.executionCooldown = 5;
      }

      public void m_8037_() {
         this.catfish.m_21573_().m_26519_(this.food.m_20185_(), this.food.m_20227_(0.5), this.food.m_20189_(), 1.0);
         float eatDist = this.catfish.m_20205_() * 0.65F + this.food.m_20205_();
         if (this.catfish.m_20270_(this.food) < eatDist + 3.0F && this.catfish.m_142582_(this.food)) {
            Vec3 delta = this.catfish.getMouthVec().m_82546_(this.food.m_20182_()).m_82541_().m_82490_(0.1F);
            this.food.m_20256_(this.food.m_20184_().m_82549_(delta));
            if (this.catfish.m_20270_(this.food) < eatDist) {
               if (this.food instanceof Player) {
                  this.food.m_6469_(this.catfish.m_269291_().m_269333_(this.catfish), 12000.0F);
               } else if (this.catfish.swallowEntity(this.food)) {
                  this.catfish.m_146850_(GameEvent.f_157806_);
                  this.catfish.m_5496_(SoundEvents.f_11912_, this.catfish.m_6121_(), this.catfish.m_6100_());
                  this.food.m_146870_();
               }
            }
         }
      }
   }
}
