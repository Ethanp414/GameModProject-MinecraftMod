package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityFlyingFish;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.alexsmobs.entity.EntitySeaBear;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ILeftClick;
import com.github.alexthe666.alexsmobs.item.ItemGhostlyPickaxe;
import com.github.alexthe666.alexsmobs.message.MessageSwingArm;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import com.github.alexthe666.alexsmobs.misc.ItemsForEmeraldsTrade;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.github.alexthe666.alexsmobs.world.BeachedCachalotWhaleSpawner;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.EntityEvent.Size;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.ItemHandlerHelper;
import org.antlr.v4.runtime.misc.Triple;

@EventBusSubscriber(
   modid = "alexsmobs",
   bus = Bus.FORGE
)
public class ServerEvents {
   public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
   public static final UUID CARRO_UUID = UUID.fromString("98905d4a-1cbc-41a4-9ded-2300404e2290");
   private static final UUID SAND_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28E");
   private static final UUID SNEAK_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28F");
   private static final AttributeModifier SAND_SPEED_BONUS = new AttributeModifier(SAND_SPEED_MODIFIER, "roadrunner speed bonus", 0.1F, Operation.ADDITION);
   private static final AttributeModifier SNEAK_SPEED_BONUS = new AttributeModifier(SNEAK_SPEED_MODIFIER, "frontier cap speed bonus", 0.1F, Operation.ADDITION);
   private static final Map<ServerLevel, BeachedCachalotWhaleSpawner> BEACHED_CACHALOT_WHALE_SPAWNER_MAP = new HashMap<>();
   public static final ObjectList<Triple<ServerPlayer, ServerLevel, BlockPos>> teleportPlayers = new ObjectArrayList();
   private static final Random RAND = new Random();

   @SubscribeEvent
   public static void onServerTick(LevelTickEvent tick) {
      if (!tick.level.f_46443_ && tick.level instanceof ServerLevel serverWorld) {
         BEACHED_CACHALOT_WHALE_SPAWNER_MAP.computeIfAbsent(serverWorld, k -> new BeachedCachalotWhaleSpawner(serverWorld));
         BeachedCachalotWhaleSpawner spawner = BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(serverWorld);
         spawner.tick();
         if (!teleportPlayers.isEmpty()) {
            ObjectListIterator var3 = teleportPlayers.iterator();

            while (var3.hasNext()) {
               Triple<ServerPlayer, ServerLevel, BlockPos> triple = (Triple<ServerPlayer, ServerLevel, BlockPos>)var3.next();
               ServerPlayer player = (ServerPlayer)triple.a;
               ServerLevel endpointWorld = (ServerLevel)triple.b;
               BlockPos endpoint = (BlockPos)triple.c;
               int heightFromMap = endpointWorld.m_6924_(Types.MOTION_BLOCKING_NO_LEAVES, endpoint.m_123341_(), endpoint.m_123343_());
               endpoint = new BlockPos(endpoint.m_123341_(), Math.max(heightFromMap, endpoint.m_123342_()), endpoint.m_123343_());
               player.m_8999_(
                  endpointWorld, endpoint.m_123341_() + 0.5, endpoint.m_123342_() + 0.5, endpoint.m_123343_() + 0.5, player.m_146908_(), player.m_146909_()
               );
               ChunkPos chunkpos = new ChunkPos(endpoint);
               endpointWorld.m_7726_().m_8387_(TicketType.f_9448_, chunkpos, 1, player.m_19879_());
               player.f_8906_.m_9829_(new ClientboundSetExperiencePacket(player.f_36080_, player.f_36079_, player.f_36078_));
            }

            teleportPlayers.clear();
         }
      }

      AMWorldData data = AMWorldData.get(tick.level);
      if (data != null) {
         data.tickPupfish();
      }
   }

   protected static BlockHitResult rayTrace(Level worldIn, Player player, Fluid fluidMode) {
      float x = player.m_146909_();
      float y = player.m_146908_();
      Vec3 vector3d = player.m_20299_(1.0F);
      float f0 = -y * (float) (Math.PI / 180.0) - (float) Math.PI;
      float f1 = -x * (float) (Math.PI / 180.0);
      float f2 = Mth.m_14089_(f0);
      float f3 = Mth.m_14031_(f0);
      float f4 = -Mth.m_14089_(f1);
      float f5 = Mth.m_14031_(f1);
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d0 = player.m_21051_((Attribute)ForgeMod.BLOCK_REACH.get()).m_22135_();
      Vec3 vector3d1 = vector3d.m_82520_(f6 * d0, f5 * d0, f7 * d0);
      return worldIn.m_45547_(new ClipContext(vector3d, vector3d1, Block.OUTLINE, fluidMode, player));
   }

   @SubscribeEvent
   public static void onItemUseLast(Finish event) {
      if (event.getItem().m_41720_() == Items.f_42730_ && RAND.nextInt(3) == 0 && event.getEntity().m_21023_((MobEffect)AMEffectRegistry.ENDER_FLU.get())) {
         event.getEntity().m_21195_((MobEffect)AMEffectRegistry.ENDER_FLU.get());
      }
   }

   @SubscribeEvent
   public static void onEntityResize(Size event) {
      if (event.getEntity() instanceof Player entity) {
         Map<MobEffect, MobEffectInstance> potions = entity.m_21221_();
         if (event.getEntity().m_9236_() != null
            && potions != null
            && !potions.isEmpty()
            && potions.containsKey(AMEffectRegistry.CLINGING)
            && EffectClinging.isUpsideDown(entity)) {
            float minus = event.getOldSize().f_20378_ - event.getOldEyeHeight();
            event.setNewEyeHeight(minus);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (AMConfig.giveBookOnStartup) {
         CompoundTag playerData = event.getEntity().getPersistentData();
         CompoundTag data = playerData.m_128469_("PlayerPersisted");
         if (data != null && !data.m_128471_("alexsmobs_has_book")) {
            ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack((ItemLike)AMItemRegistry.ANIMAL_DICTIONARY.get()));
            boolean isAlex = Objects.equals(event.getEntity().m_20148_(), ALEX_UUID);
            if (isAlex || Objects.equals(event.getEntity().m_20148_(), CARRO_UUID)) {
               ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack((ItemLike)AMItemRegistry.BEAR_DUST.get()));
            }

            if (isAlex) {
               ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack((ItemLike)AMItemRegistry.NOVELTY_HAT.get()));
            }

            data.m_128379_("alexsmobs_has_book", true);
            playerData.m_128365_("PlayerPersisted", data);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLeftClick(LeftClickEmpty event) {
      boolean flag = false;
      ItemStack leftItem = event.getEntity().m_21206_();
      ItemStack rightItem = event.getEntity().m_21205_();
      if (leftItem.m_41720_() instanceof ILeftClick iLeftClick) {
         iLeftClick.onLeftClick(leftItem, event.getEntity());
         flag = true;
      }

      if (rightItem.m_41720_() instanceof ILeftClick iLeftClick) {
         iLeftClick.onLeftClick(rightItem, event.getEntity());
         flag = true;
      }

      if (flag && event.getLevel().f_46443_) {
         AlexsMobs.sendMSGToServer(MessageSwingArm.INSTANCE);
      }
   }

   @SubscribeEvent
   public static void onStruckByLightning(EntityStruckByLightningEvent event) {
      if (event.getEntity().m_6095_() == EntityType.f_20480_ && !event.getEntity().m_9236_().f_46443_) {
         ServerLevel level = (ServerLevel)event.getEntity().m_9236_();
         event.setCanceled(true);
         EntityGiantSquid squid = (EntityGiantSquid)((EntityType)AMEntityRegistry.GIANT_SQUID.get()).m_20615_(level);
         squid.m_7678_(
            event.getEntity().m_20185_(),
            event.getEntity().m_20186_(),
            event.getEntity().m_20189_(),
            event.getEntity().m_146908_(),
            event.getEntity().m_146909_()
         );
         squid.m_6518_(level, level.m_6436_(squid.m_20183_()), MobSpawnType.CONVERSION, null, null);
         if (event.getEntity().m_8077_()) {
            squid.m_6593_(event.getEntity().m_7770_());
            squid.m_20340_(event.getEntity().m_20151_());
         }

         squid.setBlue(true);
         squid.m_21530_();
         level.m_47205_(squid);
         event.getEntity().m_146870_();
      }
   }

   @SubscribeEvent
   public void onProjectileHit(ProjectileImpactEvent event) {
      if (event.getRayTraceResult() instanceof EntityHitResult hitResult
         && hitResult.m_82443_() instanceof EntityEmu emu
         && !event.getEntity().m_9236_().f_46443_) {
         if (event.getEntity() instanceof AbstractArrow arrow) {
            arrow.m_36767_((byte)0);
         }

         if ((emu.getAnimation() == EntityEmu.ANIMATION_DODGE_RIGHT || emu.getAnimation() == EntityEmu.ANIMATION_DODGE_LEFT) && emu.getAnimationTick() < 7) {
            event.setCanceled(true);
         }

         if (emu.getAnimation() != EntityEmu.ANIMATION_DODGE_RIGHT && emu.getAnimation() != EntityEmu.ANIMATION_DODGE_LEFT) {
            boolean left = true;
            Vec3 arrowPos = event.getEntity().m_20182_();
            Vec3 rightVector = emu.m_20154_().m_82524_((float) (Math.PI / 2)).m_82549_(emu.m_20182_());
            Vec3 leftVector = emu.m_20154_().m_82524_((float) (-Math.PI / 2)).m_82549_(emu.m_20182_());
            if (arrowPos.m_82554_(rightVector) < arrowPos.m_82554_(leftVector)) {
               left = false;
            } else if (arrowPos.m_82554_(rightVector) > arrowPos.m_82554_(leftVector)) {
               left = true;
            } else {
               left = emu.m_217043_().m_188499_();
            }

            Vec3 vector3d2 = event.getEntity().m_20184_().m_82524_((float)((left ? -0.5F : 0.5F) * Math.PI)).m_82541_();
            emu.setAnimation(left ? EntityEmu.ANIMATION_DODGE_LEFT : EntityEmu.ANIMATION_DODGE_RIGHT);
            emu.f_19812_ = true;
            if (!emu.f_19862_) {
               emu.m_6478_(MoverType.SELF, new Vec3(vector3d2.m_7096_() * 0.25, 0.1F, vector3d2.m_7094_() * 0.25));
            }

            if (!event.getEntity().m_9236_().f_46443_
               && event.getEntity() instanceof Projectile projectile
               && projectile.m_19749_() instanceof ServerPlayer serverPlayer) {
               AMAdvancementTriggerRegistry.EMU_DODGE.trigger(serverPlayer);
            }

            emu.m_20256_(emu.m_20184_().m_82520_(vector3d2.m_7096_() * 0.5, 0.32F, vector3d2.m_7094_() * 0.5));
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public void onEntityDespawnAttempt(AllowDespawn event) {
      if (event.getEntity().m_21023_((MobEffect)AMEffectRegistry.DEBILITATING_STING.get())
         && event.getEntity().m_21124_((MobEffect)AMEffectRegistry.DEBILITATING_STING.get()) != null
         && event.getEntity().m_21124_((MobEffect)AMEffectRegistry.DEBILITATING_STING.get()).m_19564_() > 0) {
         event.setResult(Result.DENY);
      }
   }

   @SubscribeEvent
   public void onTradeSetup(VillagerTradesEvent event) {
      if (event.getType() == VillagerProfession.f_35591_) {
         ItemListing ambergrisTrade = new EmeraldsForItemsTrade((ItemLike)AMItemRegistry.AMBERGRIS.get(), 20, 3, 4);
         List<ItemListing> list = (List<ItemListing>)event.getTrades().get(2);
         list.add(ambergrisTrade);
         event.getTrades().put(2, list);
      }
   }

   @SubscribeEvent
   public void onWanderingTradeSetup(WandererTradesEvent event) {
      if (AMConfig.wanderingTraderOffers) {
         List<ItemListing> genericTrades = event.getGenericTrades();
         List<ItemListing> rareTrades = event.getRareTrades();
         genericTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.ANIMAL_DICTIONARY.get(), 4, 1, 2, 1));
         genericTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.ACACIA_BLOSSOM.get(), 3, 2, 2, 1));
         if (AMConfig.cockroachSpawnWeight > 0) {
            genericTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.COCKROACH_OOTHECA.get(), 2, 1, 2, 1));
         }

         if (AMConfig.blobfishSpawnWeight > 0) {
            genericTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.BLOBFISH_BUCKET.get(), 4, 1, 3, 1));
         }

         if (AMConfig.crocodileSpawnWeight > 0) {
            genericTrades.add(new ItemsForEmeraldsTrade(((net.minecraft.world.level.block.Block)AMBlockRegistry.CROCODILE_EGG.get()).m_5456_(), 6, 1, 2, 1));
         }

         genericTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.BEAR_FUR.get(), 1, 1, 2, 1));
         genericTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.CROCODILE_SCUTE.get(), 5, 1, 2, 1));
         genericTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.ROADRUNNER_FEATHER.get(), 1, 2, 2, 2));
         genericTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.MOSQUITO_LARVA.get(), 1, 3, 5, 1));
         rareTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.SOMBRERO.get(), 20, 1, 1, 1));
         rareTrades.add(new ItemsForEmeraldsTrade((net.minecraft.world.level.block.Block)AMBlockRegistry.BANANA_PEEL.get(), 1, 2, 1, 1));
         rareTrades.add(new ItemsForEmeraldsTrade((Item)AMItemRegistry.BLOOD_SAC.get(), 5, 2, 3, 1));
      }
   }

   @SubscribeEvent
   public void onLootLevelEvent(LootingLevelEvent event) {
      DamageSource src = event.getDamageSource();
      if (src != null && src.m_7639_() instanceof EntitySnowLeopard) {
         event.setLootingLevel(event.getLootingLevel() + 2);
      }
   }

   @SubscribeEvent
   public void onUseItem(RightClickItem event) {
      Player player = event.getEntity();
      if (event.getItemStack().m_41720_() == Items.f_42405_
         && player.m_20202_() instanceof EntityElephant elephant
         && elephant.triggerCharge(event.getItemStack())) {
         player.m_6674_(event.getHand());
         if (!player.m_7500_()) {
            event.getItemStack().m_41774_(1);
         }
      }

      if (event.getItemStack().m_41720_() == Items.f_42590_ && AMConfig.lavaBottleEnabled) {
         HitResult raytraceresult = rayTrace(event.getLevel(), player, Fluid.SOURCE_ONLY);
         if (raytraceresult.m_6662_() == Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)raytraceresult).m_82425_();
            if (event.getLevel().m_7966_(player, blockpos) && event.getLevel().m_6425_(blockpos).m_205070_(FluidTags.f_13132_)) {
               player.m_146850_(GameEvent.f_223698_);
               event.getLevel().m_6263_(player, player.m_20185_(), player.m_20186_(), player.m_20189_(), SoundEvents.f_11770_, SoundSource.NEUTRAL, 1.0F, 1.0F);
               player.m_36246_(Stats.f_12982_.m_12902_(Items.f_42590_));
               player.m_20254_(6);
               if (!player.m_36356_(new ItemStack((ItemLike)AMItemRegistry.LAVA_BOTTLE.get()))) {
                  player.m_19983_(new ItemStack((ItemLike)AMItemRegistry.LAVA_BOTTLE.get()));
               }

               player.m_6674_(event.getHand());
               if (!player.m_7500_()) {
                  event.getItemStack().m_41774_(1);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onInteractWithEntity(EntityInteract event) {
      if (event.getTarget() instanceof LivingEntity living) {
         if (!event.getEntity().m_6144_() && VineLassoUtil.hasLassoData(living)) {
            if (!event.getEntity().m_9236_().f_46443_) {
               event.getTarget().m_19983_(new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO.get()));
            }

            VineLassoUtil.lassoTo(null, living);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
         }

         if (!(event.getTarget() instanceof Player)
            && !(event.getTarget() instanceof EntityEndergrade)
            && living.m_21023_((MobEffect)AMEffectRegistry.ENDER_FLU.get())
            && event.getItemStack().m_41720_() == Items.f_42730_) {
            if (!event.getEntity().m_7500_()) {
               event.getItemStack().m_41774_(1);
            }

            event.getTarget().m_146850_(GameEvent.f_157806_);
            event.getTarget().m_5496_(SoundEvents.f_11912_, 1.0F, 0.5F + event.getEntity().m_217043_().m_188501_());
            if (event.getEntity().m_217043_().m_188501_() < 0.4F) {
               living.m_21195_((MobEffect)AMEffectRegistry.ENDER_FLU.get());
               Items.f_42730_.m_5922_(event.getItemStack().m_41777_(), event.getLevel(), (LivingEntity)event.getTarget());
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
         }

         if (RainbowUtil.getRainbowType(living) > 0 && event.getItemStack().m_41720_() == Items.f_41902_) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            RainbowUtil.setRainbowType(living, 0);
            if (!event.getEntity().m_7500_()) {
               event.getItemStack().m_41774_(1);
            }

            ItemStack wetSponge = new ItemStack(Items.f_41903_);
            if (!event.getEntity().m_36356_(wetSponge)) {
               event.getEntity().m_36176_(wetSponge, true);
            }
         }

         if (living instanceof Rabbit rabbit && event.getItemStack().m_41720_() == AMItemRegistry.MUNGAL_SPORES.get() && AMConfig.bunfungusTransformation) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (!event.getEntity().m_9236_().f_46443_ && random.nextFloat() < 0.15F) {
               EntityBunfungus bunfungus = (EntityBunfungus)rabbit.m_21406_((EntityType)AMEntityRegistry.BUNFUNGUS.get(), true);
               if (bunfungus != null) {
                  event.getEntity().m_9236_().m_7967_(bunfungus);
                  bunfungus.setTransformsIn(50);
               }
            } else {
               for (int i = 0; i < 2 + random.nextInt(2); i++) {
                  double d0 = random.nextGaussian() * 0.02;
                  double d1 = 0.05F + random.nextGaussian() * 0.02;
                  double d2 = random.nextGaussian() * 0.02;
                  event.getTarget()
                     .m_9236_()
                     .m_7106_(
                        (ParticleOptions)AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(),
                        event.getTarget().m_20208_(0.7F),
                        event.getTarget().m_20227_(0.6F),
                        event.getTarget().m_20262_(0.7F),
                        d0,
                        d1,
                        d2
                     );
               }
            }

            if (!event.getEntity().m_7500_()) {
               event.getItemStack().m_41774_(1);
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
         }
      }
   }

   @SubscribeEvent
   public void onUseItemAir(RightClickEmpty event) {
      ItemStack stack = event.getEntity().m_21120_(event.getHand());
      if (stack.m_41619_()) {
         stack = event.getEntity().m_6844_(EquipmentSlot.MAINHAND);
      }

      if (RainbowUtil.getRainbowType(event.getEntity()) > 0 && stack.m_150930_(Items.f_41902_)) {
         event.getEntity().m_6674_(InteractionHand.MAIN_HAND);
         RainbowUtil.setRainbowType(event.getEntity(), 0);
         if (!event.getEntity().m_7500_()) {
            stack.m_41774_(1);
         }

         ItemStack wetSponge = new ItemStack(Items.f_41903_);
         if (!event.getEntity().m_36356_(wetSponge)) {
            event.getEntity().m_36176_(wetSponge, true);
         }
      }
   }

   @SubscribeEvent
   public void onUseItemOnBlock(RightClickBlock event) {
      if (AlexsMobs.isAprilFools() && event.getItemStack().m_150930_(Items.f_42398_) && !event.getEntity().m_36335_().m_41519_(Items.f_42398_)) {
         BlockState state = event.getEntity().m_9236_().m_8055_(event.getPos());
         boolean flag = false;
         if (state.m_60713_(Blocks.f_49992_)) {
            flag = true;
            event.getEntity().m_9236_().m_46597_(event.getPos(), ((net.minecraft.world.level.block.Block)AMBlockRegistry.SAND_CIRCLE.get()).m_49966_());
         } else if (state.m_60713_(Blocks.f_49993_)) {
            flag = true;
            event.getEntity().m_9236_().m_46597_(event.getPos(), ((net.minecraft.world.level.block.Block)AMBlockRegistry.RED_SAND_CIRCLE.get()).m_49966_());
         }

         if (flag) {
            event.setCanceled(true);
            event.getEntity().m_146850_(GameEvent.f_157797_);
            event.getEntity().m_5496_(SoundEvents.f_12331_, 1.0F, 1.0F);
            event.getEntity().m_36335_().m_41524_(Items.f_42398_, 30);
            event.setCancellationResult(InteractionResult.SUCCESS);
         }
      }
   }

   @SubscribeEvent
   public void onEntityDrops(LivingDropsEvent event) {
      if (VineLassoUtil.hasLassoData(event.getEntity())) {
         VineLassoUtil.lassoTo(null, event.getEntity());
         event.getDrops()
            .add(
               new ItemEntity(
                  event.getEntity().m_9236_(),
                  event.getEntity().m_20185_(),
                  event.getEntity().m_20186_(),
                  event.getEntity().m_20189_(),
                  new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO.get())
               )
            );
      }
   }

   @SubscribeEvent
   public void onEntityFinalizeSpawn(FinalizeSpawn event) {
      Mob entity = event.getEntity();
      if (entity instanceof WanderingTrader trader && AMConfig.elephantTraderSpawnChance > 0.0) {
         Biome biome = (Biome)event.getLevel().m_204166_(entity.m_20183_()).m_203334_();
         if (RAND.nextFloat() <= AMConfig.elephantTraderSpawnChance && (!AMConfig.limitElephantTraderBiomes || biome.m_47554_() >= 1.0F)) {
            ChunkPos chunkPos = new ChunkPos(trader.m_20183_());
            if (event.getLevel().m_7726_().m_7131_(chunkPos.f_45578_, chunkPos.f_45579_) != null) {
               EntityElephant elephant = (EntityElephant)((EntityType)AMEntityRegistry.ELEPHANT.get()).m_20615_(trader.m_9236_());
               elephant.m_20359_(trader);
               if (elephant.canSpawnWithTraderHere()) {
                  elephant.setTrader(true);
                  elephant.setChested(true);
                  if (!event.getLevel().m_5776_()) {
                     trader.m_9236_().m_7967_(elephant);
                     trader.m_7998_(elephant, true);
                  }

                  elephant.addElephantLoot(null, RAND.nextInt());
               }
            }
         }
      }

      try {
         if (AMConfig.spidersAttackFlies && entity instanceof Spider spider) {
            spider.f_21346_.m_25352_(4, new NearestAttackableTargetGoal(spider, EntityFly.class, 1, true, false, null));
         } else if (AMConfig.wolvesAttackMoose && entity instanceof Wolf wolf) {
            wolf.f_21346_.m_25352_(6, new NonTameRandomTargetGoal(wolf, EntityMoose.class, false, null));
         } else if (AMConfig.polarBearsAttackSeals && entity instanceof PolarBear bear) {
            bear.f_21346_.m_25352_(6, new NearestAttackableTargetGoal(bear, EntitySeal.class, 15, true, true, null));
         } else if (entity instanceof Creeper creeper) {
            creeper.f_21346_.m_25352_(3, new AvoidEntityGoal(creeper, EntitySnowLeopard.class, 6.0F, 1.0, 1.2));
            creeper.f_21346_.m_25352_(3, new AvoidEntityGoal(creeper, EntityTiger.class, 6.0F, 1.0, 1.2));
         } else if (!AMConfig.catsAndFoxesAttackJerboas || !(entity instanceof Fox) && !(entity instanceof Cat) && !(entity instanceof Ocelot)) {
            if (AMConfig.bunfungusTransformation && entity instanceof Rabbit rabbit) {
               rabbit.f_21345_
                  .m_25352_(3, new TemptGoal(rabbit, 1.0, Ingredient.m_43929_(new ItemLike[]{(ItemLike)AMItemRegistry.MUNGAL_SPORES.get()}), false));
            } else if (AMConfig.dolphinsAttackFlyingFish && entity instanceof Dolphin dolphin) {
               dolphin.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(dolphin, EntityFlyingFish.class, 70, true, true, null));
            }
         } else {
            entity.f_21346_.m_25352_(6, new NearestAttackableTargetGoal(entity, EntityJerboa.class, 45, true, true, null));
         }
      } catch (Exception var10) {
         AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
      }
   }

   @SubscribeEvent
   public void onPlayerAttackEntityEvent(AttackEntityEvent event) {
      if (event.getTarget() instanceof LivingEntity living) {
         if (event.getEntity().m_6844_(EquipmentSlot.HEAD).m_41720_() == AMItemRegistry.MOOSE_HEADGEAR.get()) {
            living.m_147240_(
               1.0,
               Mth.m_14031_(event.getEntity().m_146908_() * (float) (Math.PI / 180.0)),
               -Mth.m_14089_(event.getEntity().m_146908_() * (float) (Math.PI / 180.0))
            );
         }

         if (event.getEntity().m_21023_((MobEffect)AMEffectRegistry.TIGERS_BLESSING.get())
            && !event.getTarget().m_7307_(event.getEntity())
            && !(event.getTarget() instanceof EntityTiger)) {
            AABB bb = new AABB(
               event.getEntity().m_20185_() - 32.0,
               event.getEntity().m_20186_() - 32.0,
               event.getEntity().m_20189_() - 32.0,
               event.getEntity().m_20189_() + 32.0,
               event.getEntity().m_20186_() + 32.0,
               event.getEntity().m_20189_() + 32.0
            );

            for (EntityTiger tiger : event.getEntity().m_9236_().m_6443_(EntityTiger.class, bb, EntitySelector.f_20402_)) {
               if (!tiger.m_6162_()) {
                  tiger.m_6710_(living);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onLivingDamageEvent(LivingDamageEvent event) {
      if (event.getSource().m_7639_() instanceof LivingEntity attacker) {
         if (event.getAmount() > 0.0F
            && attacker.m_21023_((MobEffect)AMEffectRegistry.SOULSTEAL.get())
            && attacker.m_21124_((MobEffect)AMEffectRegistry.SOULSTEAL.get()) != null) {
            int level = attacker.m_21124_((MobEffect)AMEffectRegistry.SOULSTEAL.get()).m_19564_() + 1;
            if (attacker.m_21223_() < attacker.m_21233_() && ThreadLocalRandom.current().nextFloat() < 0.25F + level * 0.25F) {
               attacker.m_5634_(Math.min(event.getAmount() / 2.0F * level, (float)(2 + 2 * level)));
            }
         }

         if (event.getEntity() instanceof Player player) {
            if (attacker instanceof EntityMimicOctopus octupus && octupus.m_21830_(player)) {
               event.setCanceled(true);
               return;
            }

            if (player.m_6844_(EquipmentSlot.HEAD).m_41720_() == AMItemRegistry.SPIKED_TURTLE_SHELL.get()
               && attacker.m_20270_(player) < attacker.m_20205_() + player.m_20205_() + 0.5F) {
               attacker.m_6469_(attacker.m_269291_().m_269374_(player), 1.0F);
               attacker.m_147240_(
                  0.5,
                  Mth.m_14031_((attacker.m_146908_() + 180.0F) * (float) (Math.PI / 180.0)),
                  -Mth.m_14089_((attacker.m_146908_() + 180.0F) * (float) (Math.PI / 180.0))
               );
            }
         }
      }

      if (!event.getEntity().m_6844_(EquipmentSlot.LEGS).m_41619_()
         && event.getEntity().m_6844_(EquipmentSlot.LEGS).m_41720_() == AMItemRegistry.EMU_LEGGINGS.get()
         && event.getSource().m_269533_(DamageTypeTags.f_268524_)
         && event.getEntity().m_217043_().m_188501_() < AMConfig.emuPantsDodgeChance) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void onLivingSetTargetEvent(LivingChangeTargetEvent event) {
      if (event.getNewTarget() != null && event.getEntity() instanceof Mob mob) {
         if (mob.m_6336_() == MobType.f_21642_
            && event.getNewTarget().m_21023_((MobEffect)AMEffectRegistry.BUG_PHEROMONES.get())
            && event.getEntity().m_21188_() != event.getNewTarget()) {
            event.setCanceled(true);
            return;
         }

         if (mob.m_6336_() == MobType.f_21641_
            && !mob.m_6095_().m_204039_(AMTagRegistry.IGNORES_KIMONO)
            && event.getNewTarget().m_6844_(EquipmentSlot.CHEST).m_150930_((Item)AMItemRegistry.UNSETTLING_KIMONO.get())
            && event.getEntity().m_21188_() != event.getNewTarget()) {
            event.setCanceled(true);
            return;
         }
      }
   }

   @SubscribeEvent
   public void onLivingUpdateEvent(LivingTickEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity instanceof Player player) {
         if (player.m_20192_() < player.m_20206_() * 0.5) {
            player.m_6210_();
         }

         if (entity.m_21204_().m_22171_(Attributes.f_22279_)) {
            AttributeInstance attributes = entity.m_21051_(Attributes.f_22279_);
            if (player.m_6844_(EquipmentSlot.FEET).m_41720_() == AMItemRegistry.ROADDRUNNER_BOOTS.get() || attributes.m_22109_(SAND_SPEED_BONUS)) {
               boolean sand = player.m_9236_().m_8055_(this.getDownPos(player.m_20183_(), player.m_9236_())).m_204336_(BlockTags.f_13029_);
               if (sand && !attributes.m_22109_(SAND_SPEED_BONUS)) {
                  attributes.m_22125_(SAND_SPEED_BONUS);
               }

               if (player.f_19797_ % 25 == 0
                  && (player.m_6844_(EquipmentSlot.FEET).m_41720_() != AMItemRegistry.ROADDRUNNER_BOOTS.get() || !sand)
                  && attributes.m_22109_(SAND_SPEED_BONUS)) {
                  attributes.m_22130_(SAND_SPEED_BONUS);
               }
            }

            if (player.m_6844_(EquipmentSlot.HEAD).m_41720_() == AMItemRegistry.FRONTIER_CAP.get() || attributes.m_22109_(SNEAK_SPEED_BONUS)) {
               boolean shift = player.m_6144_();
               if (shift && !attributes.m_22109_(SNEAK_SPEED_BONUS)) {
                  attributes.m_22125_(SNEAK_SPEED_BONUS);
               }

               if ((!shift || player.m_6844_(EquipmentSlot.HEAD).m_41720_() != AMItemRegistry.FRONTIER_CAP.get()) && attributes.m_22109_(SNEAK_SPEED_BONUS)) {
                  attributes.m_22130_(SNEAK_SPEED_BONUS);
               }
            }
         }

         if (player.m_6844_(EquipmentSlot.HEAD).m_41720_() == AMItemRegistry.SPIKED_TURTLE_SHELL.get() && !player.m_204029_(FluidTags.f_13131_)) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19608_, 310, 0, false, false, true));
         }
      }

      ItemStack boots = entity.m_6844_(EquipmentSlot.FEET);
      if (!boots.m_41619_() && boots.m_41782_() && boots.m_41784_().m_128441_("BisonFur") && boots.m_41784_().m_128471_("BisonFur")) {
         BlockPos posBelow = new BlockPos((int)event.getEntity().m_20185_(), (int)(entity.m_20191_().f_82289_ - 0.1F), (int)entity.m_20189_());
         if (entity.m_9236_().m_8055_(posBelow).m_60713_(Blocks.f_152499_)) {
            entity.m_6853_(true);
            entity.m_146917_(0);
            entity.m_6034_(entity.m_20185_(), Math.max(entity.m_20186_(), (double)(posBelow.m_123342_() + 1.0F)), entity.m_20189_());
         }

         if (entity.f_146808_) {
            entity.m_6853_(true);
            entity.m_20256_(entity.m_20184_().m_82520_(0.0, 0.1F, 0.0));
         }
      }

      if (entity.m_6844_(EquipmentSlot.LEGS).m_41720_() == AMItemRegistry.CENTIPEDE_LEGGINGS.get() && entity.f_19862_ && !entity.m_20069_()) {
         entity.f_19789_ = 0.0F;
         Vec3 motion = entity.m_20184_();
         double d2 = 0.1;
         if (entity.m_6144_() || !entity.m_146900_().isScaffolding(entity) && entity.m_5791_()) {
            d2 = 0.0;
         }

         motion = new Vec3(Mth.m_14008_(motion.f_82479_, -0.15F, 0.15F), d2, Mth.m_14008_(motion.f_82481_, -0.15F, 0.15F));
         entity.m_20256_(motion);
      }

      if (entity.m_6844_(EquipmentSlot.HEAD).m_41720_() == AMItemRegistry.SOMBRERO.get()
         && !entity.m_9236_().f_46443_
         && AlexsMobs.isAprilFools()
         && entity.m_20072_()) {
         RandomSource random = entity.m_217043_();
         if (random.m_188503_(245) == 0 && !EntitySeaBear.isMobSafe(entity)) {
            int dist = 32;
            List<EntitySeaBear> nearbySeabears = entity.m_9236_().m_45976_(EntitySeaBear.class, entity.m_20191_().m_82377_(32.0, 32.0, 32.0));
            if (nearbySeabears.isEmpty()) {
               EntitySeaBear bear = (EntitySeaBear)((EntityType)AMEntityRegistry.SEA_BEAR.get()).m_20615_(entity.m_9236_());
               BlockPos at = entity.m_20183_();
               BlockPos farOff = null;

               for (int i = 0; i < 15; i++) {
                  int f1 = (int)Math.signum(random.m_188502_() - 0.5F);
                  int f2 = (int)Math.signum(random.m_188502_() - 0.5F);
                  BlockPos pos1 = at.m_7918_(f1 * (10 + random.m_188503_(22)), random.m_188503_(1), f2 * (10 + random.m_188503_(22)));
                  if (entity.m_9236_().m_46801_(pos1)) {
                     farOff = pos1;
                  }
               }

               if (farOff != null) {
                  bear.m_6034_(farOff.m_123341_() + 0.5F, farOff.m_123342_() + 0.5F, farOff.m_123343_() + 0.5F);
                  bear.m_146922_(random.m_188501_() * 360.0F);
                  bear.m_6710_(entity);
                  entity.m_9236_().m_7967_(bear);
               }
            } else {
               for (EntitySeaBear bear : nearbySeabears) {
                  bear.m_6710_(entity);
               }
            }
         }
      }

      if (VineLassoUtil.hasLassoData(entity)) {
         VineLassoUtil.tickLasso(entity);
      }

      if (RockyChestplateUtil.isWearing(entity)) {
         RockyChestplateUtil.tickRockyRolling(entity);
      }

      if (FlyingFishBootsUtil.isWearing(entity)) {
         FlyingFishBootsUtil.tickFlyingFishBoots(entity);
      }
   }

   private BlockPos getDownPos(BlockPos entered, LevelAccessor world) {
      for (int i = 0; world.m_46859_(entered) && i < 3; i++) {
         entered = entered.m_7495_();
      }

      return entered;
   }

   @SubscribeEvent
   public void onFOVUpdate(ComputeFovModifierEvent event) {
      if (event.getPlayer().m_21023_((MobEffect)AMEffectRegistry.FEAR.get()) || event.getPlayer().m_21023_((MobEffect)AMEffectRegistry.POWER_DOWN.get())) {
         event.setNewFovModifier(1.0F);
      }
   }

   @SubscribeEvent
   public void onLivingAttack(LivingAttackEvent event) {
      if (!event.getEntity().m_21211_().m_41619_()
         && event.getSource() != null
         && event.getSource().m_7639_() != null
         && event.getEntity().m_21211_().m_41720_() == AMItemRegistry.SHIELD_OF_THE_DEEP.get()
         && event.getSource().m_7639_() instanceof LivingEntity living) {
         boolean flag = false;
         if (living.m_20270_(event.getEntity()) <= 4.0F && !living.m_21023_((MobEffect)AMEffectRegistry.EXSANGUINATION.get())) {
            living.m_7292_(new MobEffectInstance((MobEffect)AMEffectRegistry.EXSANGUINATION.get(), 60, 2));
            flag = true;
         }

         if (event.getEntity().m_20072_()) {
            event.getEntity().m_20301_(Math.min(event.getEntity().m_6062_(), event.getEntity().m_20146_() + 150));
            flag = true;
         }

         if (flag) {
            event.getEntity().m_21211_().m_41622_(1, event.getEntity(), player -> player.m_21190_(event.getEntity().m_7655_()));
         }
      }
   }

   @SubscribeEvent
   public void onTooltip(ItemTooltipEvent event) {
      CompoundTag tag = event.getItemStack().m_41783_();
      if (tag != null && tag.m_128441_("BisonFur") && tag.m_128471_("BisonFur")) {
         event.getToolTip().add(Component.m_237115_("item.alexsmobs.insulated_with_fur").m_130940_(ChatFormatting.AQUA));
      }
   }

   @SubscribeEvent
   public void onAddReloadListener(AddReloadListenerEvent event) {
      AlexsMobs.LOGGER.info("Adding datapack listener capsid_recipes");
      event.addListener(AlexsMobs.PROXY.getCapsidRecipeManager());
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onHarvestCheck(HarvestCheck event) {
      if (event.getEntity() != null
         && event.getEntity().m_21055_((Item)AMItemRegistry.GHOSTLY_PICKAXE.get())
         && ItemGhostlyPickaxe.shouldStoreInGhost(event.getEntity(), event.getEntity().m_21205_())) {
         event.setCanHarvest(false);
      }
   }
}
