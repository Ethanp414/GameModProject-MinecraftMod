package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.BlockCapsid;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.message.MessageUpdateCapsid;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.CapsidRecipe;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityCapsid extends BaseContainerBlockEntity implements WorldlyContainer {
   private static final int[] slotsTop = new int[]{0};
   public int ticksExisted;
   public float prevFloatUpProgress;
   public float floatUpProgress;
   public float prevYawSwitchProgress;
   public float yawSwitchProgress;
   public boolean vibratingThisTick = false;
   LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, new Direction[]{Direction.UP, Direction.DOWN});
   private float yawTarget = 0.0F;
   private int transformTime = 0;
   private boolean fnaf = false;
   private CapsidRecipe lastRecipe = null;
   private NonNullList<ItemStack> stacks = NonNullList.m_122780_(1, ItemStack.f_41583_);

   public TileEntityCapsid(BlockPos pos, BlockState state) {
      super((BlockEntityType)AMTileEntityRegistry.CAPSID.get(), pos, state);
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityCapsid entity) {
      entity.tick();
   }

   public void tick() {
      this.prevFloatUpProgress = this.floatUpProgress;
      this.prevYawSwitchProgress = this.yawSwitchProgress;
      this.ticksExisted++;
      this.vibratingThisTick = false;
      if (!this.m_8020_(0).m_41619_()) {
         BlockEntity up = this.f_58857_.m_7702_(this.f_58858_.m_7494_());
         if (up instanceof Container) {
            if (this.floatUpProgress >= 1.0F) {
               LazyOptional<IItemHandler> handler = this.f_58857_.m_7702_(this.f_58858_.m_7494_()).getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
               if (handler.orElse(null) != null && ItemHandlerHelper.insertItem((IItemHandler)handler.orElse(null), this.m_8020_(0), true).m_41619_()) {
                  ItemHandlerHelper.insertItem((IItemHandler)handler.orElse(null), this.m_8020_(0).m_41777_(), false);
                  this.m_6836_(0, ItemStack.f_41583_);
               }

               this.yawTarget = 0.0F;
               this.floatUpProgress = 0.0F;
               this.yawSwitchProgress = 0.0F;
            } else {
               if (up instanceof TileEntityCapsid) {
                  this.yawTarget = Mth.m_14177_(((TileEntityCapsid)up).getBlockAngle() - this.getBlockAngle());
               } else {
                  this.yawTarget = 0.0F;
               }

               if (this.yawTarget < this.yawSwitchProgress) {
                  this.yawSwitchProgress = this.yawSwitchProgress + this.yawTarget * 0.1F;
               } else if (this.yawTarget > this.yawSwitchProgress) {
                  this.yawSwitchProgress = this.yawSwitchProgress + this.yawTarget * 0.1F;
               }

               this.floatUpProgress += 0.05F;
            }
         } else {
            this.floatUpProgress = 0.0F;
         }

         if (this.m_8020_(0).m_41720_() == Items.f_42545_
            && this.f_58857_.m_8055_(this.m_58899_().m_7495_()).m_60734_() == Blocks.f_50489_
            && ((Direction)this.f_58857_.m_8055_(this.m_58899_().m_7495_()).m_61143_(EndRodBlock.f_52588_)).m_122434_() == Axis.Y) {
            this.vibratingThisTick = true;
            if (this.transformTime > 20) {
               this.m_6836_(0, ItemStack.f_41583_);
               this.f_58857_.m_46961_(this.m_58899_(), false);
               this.f_58857_.m_46961_(this.m_58899_().m_7495_(), false);
               EntityEnderiophage phage = (EntityEnderiophage)((EntityType)AMEntityRegistry.ENDERIOPHAGE.get()).m_20615_(this.f_58857_);
               phage.m_6034_(this.m_58899_().m_123341_() + 0.5F, this.m_58899_().m_123342_() - 1.0F, this.m_58899_().m_123343_() + 0.5F);
               phage.setVariant(0);
               if (!this.f_58857_.f_46443_) {
                  this.f_58857_.m_7967_(phage);
               }
            }
         } else if (!this.m_8020_(0).m_41619_()
            && this.f_58857_.m_8055_(this.m_58899_().m_7494_()).m_60734_() != this.m_58900_().m_60734_()
            && this.lastRecipe != null
            && this.lastRecipe.matches(this.m_8020_(0))) {
            this.floatUpProgress = 0.0F;
            this.vibratingThisTick = true;
            if (this.transformTime == 1 && (AlexsMobs.isAprilFools() || new Random().nextInt(100) == 0)) {
               this.fnaf = true;
               this.f_58857_.m_5594_(null, this.m_58899_(), (SoundEvent)AMSoundRegistry.MOSQUITO_CAPSID_CONVERT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            if (this.transformTime > (this.fnaf ? Math.max(160, this.lastRecipe.getTime()) : this.lastRecipe.getTime())) {
               ItemStack current = this.m_8020_(0).m_41777_();
               current.m_41774_(1);
               this.fnaf = false;
               if (!current.m_41619_()) {
                  ItemEntity itemEntity = new ItemEntity(
                     this.f_58857_, this.m_58899_().m_123341_() + 0.5F, this.m_58899_().m_123342_() + 0.5F, this.m_58899_().m_123343_() + 0.5F, current
                  );
                  if (!this.f_58857_.f_46443_) {
                     this.f_58857_.m_7967_(itemEntity);
                  }
               }

               this.m_6836_(0, this.lastRecipe.getResult().m_41777_());
            }
         }
      }

      if (!this.vibratingThisTick) {
         this.transformTime = 0;
      } else {
         this.transformTime++;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AABB getRenderBoundingBox() {
      return new AABB(this.f_58858_, this.f_58858_.m_7918_(1, 2, 1));
   }

   public int m_6643_() {
      return this.stacks.size();
   }

   public ItemStack m_8020_(int index) {
      return (ItemStack)this.stacks.get(index);
   }

   public ItemStack m_7407_(int index, int count) {
      if (!((ItemStack)this.stacks.get(index)).m_41619_()) {
         if (((ItemStack)this.stacks.get(index)).m_41613_() <= count) {
            ItemStack itemstack = (ItemStack)this.stacks.get(index);
            this.stacks.set(index, ItemStack.f_41583_);
            return itemstack;
         } else {
            ItemStack itemstack = ((ItemStack)this.stacks.get(index)).m_41620_(count);
            if (((ItemStack)this.stacks.get(index)).m_41619_()) {
               this.stacks.set(index, ItemStack.f_41583_);
            }

            return itemstack;
         }
      } else {
         return ItemStack.f_41583_;
      }
   }

   public ItemStack getStackInSlotOnClosing(int index) {
      if (!((ItemStack)this.stacks.get(index)).m_41619_()) {
         ItemStack itemstack = (ItemStack)this.stacks.get(index);
         this.stacks.set(index, itemstack);
         return itemstack;
      } else {
         return ItemStack.f_41583_;
      }
   }

   public void m_6836_(int index, ItemStack stack) {
      if (!stack.m_41619_() && ItemStack.m_150942_(stack, (ItemStack)this.stacks.get(index))) {
         boolean var4 = true;
      } else {
         boolean var10000 = false;
      }

      this.stacks.set(index, stack);
      if (!stack.m_41619_() && stack.m_41613_() > this.m_6893_()) {
         stack.m_41764_(this.m_6893_());
      }

      this.lastRecipe = AlexsMobs.PROXY.getCapsidRecipeManager().getRecipeFor(stack);
      this.m_183515_(this.m_5995_());
      if (!this.f_58857_.f_46443_) {
         AlexsMobs.sendMSGToAll(new MessageUpdateCapsid(this.m_58899_().m_121878_(), (ItemStack)this.stacks.get(0)));
      }
   }

   public void m_142466_(CompoundTag compound) {
      super.m_142466_(compound);
      this.stacks = NonNullList.m_122780_(this.m_6643_(), ItemStack.f_41583_);
      ContainerHelper.m_18980_(compound, this.stacks);
   }

   public void m_183515_(CompoundTag compound) {
      super.m_183515_(compound);
      ContainerHelper.m_18973_(compound, this.stacks);
   }

   public void m_5856_(Player player) {
   }

   public void m_5785_(Player player) {
   }

   public boolean m_7155_(int index, ItemStack stack, Direction direction) {
      return true;
   }

   public int m_6893_() {
      return 64;
   }

   public boolean m_6542_(Player player) {
      return true;
   }

   public void m_6211_() {
      this.stacks.clear();
   }

   public int[] m_7071_(Direction side) {
      return slotsTop;
   }

   public boolean m_7157_(int index, ItemStack stack, Direction direction) {
      return false;
   }

   public boolean m_8077_() {
      return false;
   }

   public boolean m_7013_(int index, ItemStack stack) {
      return true;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
      if (packet != null && packet.m_131708_() != null) {
         this.stacks = NonNullList.m_122780_(this.m_6643_(), ItemStack.f_41583_);
         ContainerHelper.m_18980_(packet.m_131708_(), this.stacks);
      }
   }

   public CompoundTag m_5995_() {
      return this.m_187482_();
   }

   public ItemStack m_8016_(int index) {
      ItemStack lvt_2_1_ = (ItemStack)this.stacks.get(index);
      if (lvt_2_1_.m_41619_()) {
         return ItemStack.f_41583_;
      } else {
         this.stacks.set(index, ItemStack.f_41583_);
         return lvt_2_1_;
      }
   }

   public Component m_5446_() {
      return this.m_6820_();
   }

   protected Component m_6820_() {
      return Component.m_237115_("block.alexsmobs.capsid");
   }

   protected AbstractContainerMenu m_6555_(int id, Inventory player) {
      return null;
   }

   public boolean m_7983_() {
      for (int i = 0; i < this.m_6643_(); i++) {
         if (!this.m_8020_(i).m_41619_()) {
            return false;
         }
      }

      return true;
   }

   public float getBlockAngle() {
      if (this.m_58900_().m_60734_() instanceof BlockCapsid) {
         Direction dir = (Direction)this.m_58900_().m_61143_(BlockCapsid.HORIZONTAL_FACING);
         return dir.m_122435_();
      } else {
         return 0.0F;
      }
   }

   public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
      if (this.f_58859_ || facing == null || capability != ForgeCapabilities.ITEM_HANDLER) {
         return super.getCapability(capability, facing);
      } else {
         return facing == Direction.DOWN ? this.handlers[0].cast() : this.handlers[1].cast();
      }
   }
}
