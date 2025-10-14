package net.minecraft.world.item;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Item implements FeatureElement, ItemLike {
   public static final Codec<Holder<Item>> CODEC = BuiltInRegistries.ITEM
      .holderByNameCodec()
      .validate($$0 -> $$0.is(Items.AIR.builtInRegistryHolder()) ? DataResult.error(() -> "Item must not be minecraft:air") : DataResult.success($$0));
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ITEM);
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Map<Block, Item> BY_BLOCK = Maps.<Block, Item>newHashMap();
   public static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.withDefaultNamespace("base_attack_damage");
   public static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.withDefaultNamespace("base_attack_speed");
   public static final int DEFAULT_MAX_STACK_SIZE = 64;
   public static final int ABSOLUTE_MAX_STACK_SIZE = 99;
   public static final int MAX_BAR_WIDTH = 13;
   protected static final int APPROXIMATELY_INFINITE_USE_DURATION = 72000;
   private final Holder.Reference<Item> builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(this);
   private final DataComponentMap components;
   @Nullable
   private final Item craftingRemainingItem;
   protected final String descriptionId;
   private final FeatureFlagSet requiredFeatures;

   public static int getId(Item $$0) {
      return $$0 == null ? 0 : BuiltInRegistries.ITEM.getId($$0);
   }

   public static Item byId(int $$0) {
      return BuiltInRegistries.ITEM.byId($$0);
   }

   @Deprecated
   public static Item byBlock(Block $$0) {
      return (Item)BY_BLOCK.getOrDefault($$0, Items.AIR);
   }

   public Item(Item.Properties $$0) {
      this.descriptionId = $$0.effectiveDescriptionId();
      this.components = $$0.buildAndValidateComponents(Component.translatable(this.descriptionId), $$0.effectiveModel());
      this.craftingRemainingItem = $$0.craftingRemainingItem;
      this.requiredFeatures = $$0.requiredFeatures;
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         String $$1 = this.getClass().getSimpleName();
         if (!$$1.endsWith("Item")) {
            LOGGER.error("Item classes should end with Item and {} doesn't.", $$1);
         }
      }
   }

   @Deprecated
   public Holder.Reference<Item> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public DataComponentMap components() {
      return this.components;
   }

   public int getDefaultMaxStackSize() {
      return this.components.getOrDefault(DataComponents.MAX_STACK_SIZE, Integer.valueOf(1));
   }

   public void onUseTick(Level $$0, LivingEntity $$1, ItemStack $$2, int $$3) {
   }

   public void onDestroyed(ItemEntity $$0) {
   }

   public boolean canDestroyBlock(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, LivingEntity $$4) {
      Tool $$5 = $$0.get(DataComponents.TOOL);
      if ($$5 != null && !$$5.canDestroyBlocksInCreative()) {
         if ($$4 instanceof Player $$6 && $$6.getAbilities().instabuild) {
            return false;
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public Item asItem() {
      return this;
   }

   public InteractionResult useOn(UseOnContext $$0) {
      return InteractionResult.PASS;
   }

   public float getDestroySpeed(ItemStack $$0, BlockState $$1) {
      Tool $$2 = $$0.get(DataComponents.TOOL);
      return $$2 != null ? $$2.getMiningSpeed($$1) : 1.0F;
   }

   public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
      ItemStack $$3 = $$1.getItemInHand($$2);
      Consumable $$4 = $$3.get(DataComponents.CONSUMABLE);
      if ($$4 != null) {
         return $$4.startConsuming($$1, $$3, $$2);
      } else {
         Equippable $$5 = $$3.get(DataComponents.EQUIPPABLE);
         if ($$5 != null && $$5.swappable()) {
            return $$5.swapWithEquipmentSlot($$3, $$1);
         } else {
            BlocksAttacks $$6 = $$3.get(DataComponents.BLOCKS_ATTACKS);
            if ($$6 != null) {
               $$1.startUsingItem($$2);
               return InteractionResult.CONSUME;
            } else {
               return InteractionResult.PASS;
            }
         }
      }
   }

   public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
      Consumable $$3 = $$0.get(DataComponents.CONSUMABLE);
      return $$3 != null ? $$3.onConsume($$1, $$2, $$0) : $$0;
   }

   public boolean isBarVisible(ItemStack $$0) {
      return $$0.isDamaged();
   }

   public int getBarWidth(ItemStack $$0) {
      return Mth.clamp(Math.round(13.0F - (float)$$0.getDamageValue() * 13.0F / (float)$$0.getMaxDamage()), 0, 13);
   }

   public int getBarColor(ItemStack $$0) {
      int $$1 = $$0.getMaxDamage();
      float $$2 = Math.max(0.0F, ((float)$$1 - (float)$$0.getDamageValue()) / (float)$$1);
      return Mth.hsvToRgb($$2 / 3.0F, 1.0F, 1.0F);
   }

   public boolean overrideStackedOnOther(ItemStack $$0, Slot $$1, ClickAction $$2, Player $$3) {
      return false;
   }

   public boolean overrideOtherStackedOnMe(ItemStack $$0, ItemStack $$1, Slot $$2, ClickAction $$3, Player $$4, SlotAccess $$5) {
      return false;
   }

   public float getAttackDamageBonus(Entity $$0, float $$1, DamageSource $$2) {
      return 0.0F;
   }

   @Nullable
   public DamageSource getDamageSource(LivingEntity $$0) {
      return null;
   }

   public void hurtEnemy(ItemStack $$0, LivingEntity $$1, LivingEntity $$2) {
   }

   public void postHurtEnemy(ItemStack $$0, LivingEntity $$1, LivingEntity $$2) {
   }

   public boolean mineBlock(ItemStack $$0, Level $$1, BlockState $$2, BlockPos $$3, LivingEntity $$4) {
      Tool $$5 = $$0.get(DataComponents.TOOL);
      if ($$5 == null) {
         return false;
      } else {
         if (!$$1.isClientSide() && $$2.getDestroySpeed($$1, $$3) != 0.0F && $$5.damagePerBlock() > 0) {
            $$0.hurtAndBreak($$5.damagePerBlock(), $$4, EquipmentSlot.MAINHAND);
         }

         return true;
      }
   }

   public boolean isCorrectToolForDrops(ItemStack $$0, BlockState $$1) {
      Tool $$2 = $$0.get(DataComponents.TOOL);
      return $$2 != null && $$2.isCorrectForDrops($$1);
   }

   public InteractionResult interactLivingEntity(ItemStack $$0, Player $$1, LivingEntity $$2, InteractionHand $$3) {
      return InteractionResult.PASS;
   }

   public String toString() {
      return BuiltInRegistries.ITEM.wrapAsHolder(this).getRegisteredName();
   }

   public final ItemStack getCraftingRemainder() {
      return this.craftingRemainingItem == null ? ItemStack.EMPTY : new ItemStack(this.craftingRemainingItem);
   }

   public void inventoryTick(ItemStack $$0, ServerLevel $$1, Entity $$2, @Nullable EquipmentSlot $$3) {
   }

   public void onCraftedBy(ItemStack $$0, Player $$1) {
      this.onCraftedPostProcess($$0, $$1.level());
   }

   public void onCraftedPostProcess(ItemStack $$0, Level $$1) {
   }

   public ItemUseAnimation getUseAnimation(ItemStack $$0) {
      Consumable $$1 = $$0.get(DataComponents.CONSUMABLE);
      if ($$1 != null) {
         return $$1.animation();
      } else {
         BlocksAttacks $$2 = $$0.get(DataComponents.BLOCKS_ATTACKS);
         return $$2 != null ? ItemUseAnimation.BLOCK : ItemUseAnimation.NONE;
      }
   }

   public int getUseDuration(ItemStack $$0, LivingEntity $$1) {
      Consumable $$2 = $$0.get(DataComponents.CONSUMABLE);
      if ($$2 != null) {
         return $$2.consumeTicks();
      } else {
         BlocksAttacks $$3 = $$0.get(DataComponents.BLOCKS_ATTACKS);
         return $$3 != null ? 72000 : 0;
      }
   }

   public boolean releaseUsing(ItemStack $$0, Level $$1, LivingEntity $$2, int $$3) {
      return false;
   }

   @Deprecated
   public void appendHoverText(ItemStack $$0, Item.TooltipContext $$1, TooltipDisplay $$2, Consumer<Component> $$3, TooltipFlag $$4) {
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack $$0) {
      return Optional.empty();
   }

   @VisibleForTesting
   public final String getDescriptionId() {
      return this.descriptionId;
   }

   public final Component getName() {
      return this.components.getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
   }

   public Component getName(ItemStack $$0) {
      return $$0.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
   }

   public boolean isFoil(ItemStack $$0) {
      return $$0.isEnchanted();
   }

   protected static BlockHitResult getPlayerPOVHitResult(Level $$0, Player $$1, ClipContext.Fluid $$2) {
      Vec3 $$3 = $$1.getEyePosition();
      Vec3 $$4 = $$3.add($$1.calculateViewVector($$1.getXRot(), $$1.getYRot()).scale($$1.blockInteractionRange()));
      return $$0.clip(new ClipContext($$3, $$4, ClipContext.Block.OUTLINE, $$2, $$1));
   }

   public boolean useOnRelease(ItemStack $$0) {
      return false;
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean canFitInsideContainerItems() {
      return true;
   }

   @Override
   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   public boolean shouldPrintOpWarning(ItemStack $$0, @Nullable Player $$1) {
      return false;
   }

   public static class Properties {
      private static final DependantName<Item, String> BLOCK_DESCRIPTION_ID = $$0 -> Util.makeDescriptionId("block", $$0.location());
      private static final DependantName<Item, String> ITEM_DESCRIPTION_ID = $$0 -> Util.makeDescriptionId("item", $$0.location());
      private final DataComponentMap.Builder components = DataComponentMap.builder().addAll(DataComponents.COMMON_ITEM_COMPONENTS);
      @Nullable
      Item craftingRemainingItem;
      FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
      @Nullable
      private ResourceKey<Item> id;
      private DependantName<Item, String> descriptionId = ITEM_DESCRIPTION_ID;
      private DependantName<Item, ResourceLocation> model = ResourceKey::location;

      public Item.Properties food(FoodProperties $$0) {
         return this.food($$0, Consumables.DEFAULT_FOOD);
      }

      public Item.Properties food(FoodProperties $$0, Consumable $$1) {
         return this.component(DataComponents.FOOD, $$0).component(DataComponents.CONSUMABLE, $$1);
      }

      public Item.Properties usingConvertsTo(Item $$0) {
         return this.component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStack($$0)));
      }

      public Item.Properties useCooldown(float $$0) {
         return this.component(DataComponents.USE_COOLDOWN, new UseCooldown($$0));
      }

      public Item.Properties stacksTo(int $$0) {
         return this.component(DataComponents.MAX_STACK_SIZE, $$0);
      }

      public Item.Properties durability(int $$0) {
         this.component(DataComponents.MAX_DAMAGE, $$0);
         this.component(DataComponents.MAX_STACK_SIZE, 1);
         this.component(DataComponents.DAMAGE, 0);
         return this;
      }

      public Item.Properties craftRemainder(Item $$0) {
         this.craftingRemainingItem = $$0;
         return this;
      }

      public Item.Properties rarity(Rarity $$0) {
         return this.component(DataComponents.RARITY, $$0);
      }

      public Item.Properties fireResistant() {
         return this.component(DataComponents.DAMAGE_RESISTANT, new DamageResistant(DamageTypeTags.IS_FIRE));
      }

      public Item.Properties jukeboxPlayable(ResourceKey<JukeboxSong> $$0) {
         return this.component(DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayable(new EitherHolder($$0)));
      }

      public Item.Properties enchantable(int $$0) {
         return this.component(DataComponents.ENCHANTABLE, new Enchantable($$0));
      }

      public Item.Properties repairable(Item $$0) {
         return this.component(DataComponents.REPAIRABLE, new Repairable(HolderSet.direct($$0.builtInRegistryHolder())));
      }

      public Item.Properties repairable(TagKey<Item> $$0) {
         HolderGetter<Item> $$1 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM);
         return this.component(DataComponents.REPAIRABLE, new Repairable($$1.getOrThrow($$0)));
      }

      public Item.Properties equippable(EquipmentSlot $$0) {
         return this.component(DataComponents.EQUIPPABLE, Equippable.builder($$0).build());
      }

      public Item.Properties equippableUnswappable(EquipmentSlot $$0) {
         return this.component(DataComponents.EQUIPPABLE, Equippable.builder($$0).setSwappable(false).build());
      }

      public Item.Properties tool(ToolMaterial $$0, TagKey<Block> $$1, float $$2, float $$3, float $$4) {
         return $$0.applyToolProperties(this, $$1, $$2, $$3, $$4);
      }

      public Item.Properties pickaxe(ToolMaterial $$0, float $$1, float $$2) {
         return this.tool($$0, BlockTags.MINEABLE_WITH_PICKAXE, $$1, $$2, 0.0F);
      }

      public Item.Properties axe(ToolMaterial $$0, float $$1, float $$2) {
         return this.tool($$0, BlockTags.MINEABLE_WITH_AXE, $$1, $$2, 5.0F);
      }

      public Item.Properties hoe(ToolMaterial $$0, float $$1, float $$2) {
         return this.tool($$0, BlockTags.MINEABLE_WITH_HOE, $$1, $$2, 0.0F);
      }

      public Item.Properties shovel(ToolMaterial $$0, float $$1, float $$2) {
         return this.tool($$0, BlockTags.MINEABLE_WITH_SHOVEL, $$1, $$2, 0.0F);
      }

      public Item.Properties sword(ToolMaterial $$0, float $$1, float $$2) {
         return $$0.applySwordProperties(this, $$1, $$2);
      }

      public Item.Properties spawnEgg(EntityType<?> $$0) {
         return this.component(DataComponents.ENTITY_DATA, TypedEntityData.<EntityType<?>>of($$0, new CompoundTag()));
      }

      public Item.Properties humanoidArmor(ArmorMaterial $$0, ArmorType $$1) {
         return this.durability($$1.getDurability($$0.durability()))
            .attributes($$0.createAttributes($$1))
            .enchantable($$0.enchantmentValue())
            .component(DataComponents.EQUIPPABLE, Equippable.builder($$1.getSlot()).setEquipSound($$0.equipSound()).setAsset($$0.assetId()).build())
            .repairable($$0.repairIngredient());
      }

      public Item.Properties wolfArmor(ArmorMaterial $$0) {
         return this.durability(ArmorType.BODY.getDurability($$0.durability()))
            .attributes($$0.createAttributes(ArmorType.BODY))
            .repairable($$0.repairIngredient())
            .component(
               DataComponents.EQUIPPABLE,
               Equippable.builder(EquipmentSlot.BODY)
                  .setEquipSound($$0.equipSound())
                  .setAsset($$0.assetId())
                  .setAllowedEntities(HolderSet.direct(EntityType.WOLF.builtInRegistryHolder()))
                  .setCanBeSheared(true)
                  .setShearingSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ARMOR_UNEQUIP_WOLF))
                  .build()
            )
            .component(DataComponents.BREAK_SOUND, SoundEvents.WOLF_ARMOR_BREAK)
            .stacksTo(1);
      }

      public Item.Properties horseArmor(ArmorMaterial $$0) {
         HolderGetter<EntityType<?>> $$1 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ENTITY_TYPE);
         return this.attributes($$0.createAttributes(ArmorType.BODY))
            .component(
               DataComponents.EQUIPPABLE,
               Equippable.builder(EquipmentSlot.BODY)
                  .setEquipSound(SoundEvents.HORSE_ARMOR)
                  .setAsset($$0.assetId())
                  .setAllowedEntities($$1.getOrThrow(EntityTypeTags.CAN_WEAR_HORSE_ARMOR))
                  .setDamageOnHurt(false)
                  .setCanBeSheared(true)
                  .setShearingSound(SoundEvents.HORSE_ARMOR_UNEQUIP)
                  .build()
            )
            .stacksTo(1);
      }

      public Item.Properties trimMaterial(ResourceKey<TrimMaterial> $$0) {
         return this.component(DataComponents.PROVIDES_TRIM_MATERIAL, new ProvidesTrimMaterial($$0));
      }

      public Item.Properties requiredFeatures(FeatureFlag... $$0) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset($$0);
         return this;
      }

      public Item.Properties setId(ResourceKey<Item> $$0) {
         this.id = $$0;
         return this;
      }

      public Item.Properties overrideDescription(String $$0) {
         this.descriptionId = DependantName.fixed($$0);
         return this;
      }

      public Item.Properties useBlockDescriptionPrefix() {
         this.descriptionId = BLOCK_DESCRIPTION_ID;
         return this;
      }

      public Item.Properties useItemDescriptionPrefix() {
         this.descriptionId = ITEM_DESCRIPTION_ID;
         return this;
      }

      protected String effectiveDescriptionId() {
         return (String)this.descriptionId.get((ResourceKey<Item>)Objects.requireNonNull(this.id, "Item id not set"));
      }

      public ResourceLocation effectiveModel() {
         return this.model.get((ResourceKey<Item>)Objects.requireNonNull(this.id, "Item id not set"));
      }

      public <T> Item.Properties component(DataComponentType<T> $$0, T $$1) {
         this.components.set($$0, $$1);
         return this;
      }

      public Item.Properties attributes(ItemAttributeModifiers $$0) {
         return this.component(DataComponents.ATTRIBUTE_MODIFIERS, $$0);
      }

      DataComponentMap buildAndValidateComponents(Component $$0, ResourceLocation $$1) {
         DataComponentMap $$2 = this.components.set(DataComponents.ITEM_NAME, $$0).set(DataComponents.ITEM_MODEL, $$1).build();
         if ($$2.has(DataComponents.DAMAGE) && $$2.getOrDefault(DataComponents.MAX_STACK_SIZE, Integer.valueOf(1)) > 1) {
            throw new IllegalStateException("Item cannot have both durability and be stackable");
         } else {
            return $$2;
         }
      }
   }

   public interface TooltipContext {
      Item.TooltipContext EMPTY = new Item.TooltipContext() {
         @Nullable
         @Override
         public HolderLookup.Provider registries() {
            return null;
         }

         @Override
         public float tickRate() {
            return 20.0F;
         }

         @Nullable
         @Override
         public MapItemSavedData mapData(MapId $$0) {
            return null;
         }

         @Override
         public boolean isPeaceful() {
            return false;
         }
      };

      @Nullable
      HolderLookup.Provider registries();

      float tickRate();

      @Nullable
      MapItemSavedData mapData(MapId var1);

      boolean isPeaceful();

      static Item.TooltipContext of(@Nullable final Level $$0) {
         return $$0 == null ? EMPTY : new Item.TooltipContext() {
            @Override
            public HolderLookup.Provider registries() {
               return $$0.registryAccess();
            }

            @Override
            public float tickRate() {
               return $$0.tickRateManager().tickrate();
            }

            @Override
            public MapItemSavedData mapData(MapId $$0x) {
               return $$0.getMapData($$0);
            }

            @Override
            public boolean isPeaceful() {
               return $$0.getDifficulty() == Difficulty.PEACEFUL;
            }
         };
      }

      static Item.TooltipContext of(final HolderLookup.Provider $$0) {
         return new Item.TooltipContext() {
            @Override
            public HolderLookup.Provider registries() {
               return $$0;
            }

            @Override
            public float tickRate() {
               return 20.0F;
            }

            @Nullable
            @Override
            public MapItemSavedData mapData(MapId $$0x) {
               return null;
            }

            @Override
            public boolean isPeaceful() {
               return false;
            }
         };
      }
   }
}
