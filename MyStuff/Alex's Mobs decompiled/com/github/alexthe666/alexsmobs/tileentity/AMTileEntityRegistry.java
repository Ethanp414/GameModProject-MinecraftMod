package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(
   modid = "alexsmobs",
   bus = Bus.MOD
)
public class AMTileEntityRegistry {
   public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "alexsmobs");
   public static final RegistryObject<BlockEntityType<TileEntityLeafcutterAnthill>> LEAFCUTTER_ANTHILL = DEF_REG.register(
      "leafcutter_anthill_te",
      () -> Builder.m_155273_(TileEntityLeafcutterAnthill::new, new Block[]{(Block)AMBlockRegistry.LEAFCUTTER_ANTHILL.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<TileEntityCapsid>> CAPSID = DEF_REG.register(
      "capsid_te", () -> Builder.m_155273_(TileEntityCapsid::new, new Block[]{(Block)AMBlockRegistry.CAPSID.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<TileEntityVoidWormBeak>> VOID_WORM_BEAK = DEF_REG.register(
      "void_worm_beak_te", () -> Builder.m_155273_(TileEntityVoidWormBeak::new, new Block[]{(Block)AMBlockRegistry.VOID_WORM_BEAK.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<TileEntityTerrapinEgg>> TERRAPIN_EGG = DEF_REG.register(
      "terrapin_egg_te", () -> Builder.m_155273_(TileEntityTerrapinEgg::new, new Block[]{(Block)AMBlockRegistry.TERRAPIN_EGG.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<TileEntityTransmutationTable>> TRANSMUTATION_TABLE = DEF_REG.register(
      "transmutation_table",
      () -> Builder.m_155273_(TileEntityTransmutationTable::new, new Block[]{(Block)AMBlockRegistry.TRANSMUTATION_TABLE.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<TileEntitySculkBoomer>> SCULK_BOOMER = DEF_REG.register(
      "sculk_boomer", () -> Builder.m_155273_(TileEntitySculkBoomer::new, new Block[]{(Block)AMBlockRegistry.SCULK_BOOMER.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<TileEntityEndPirateDoor>> END_PIRATE_DOOR = null;
   public static final RegistryObject<BlockEntityType<TileEntityEndPirateAnchor>> END_PIRATE_ANCHOR = null;
   public static final RegistryObject<BlockEntityType<TileEntityEndPirateAnchorWinch>> END_PIRATE_ANCHOR_WINCH = null;
   public static final RegistryObject<BlockEntityType<TileEntityEndPirateShipWheel>> END_PIRATE_SHIP_WHEEL = null;
   public static final RegistryObject<BlockEntityType<TileEntityEndPirateFlag>> END_PIRATE_FLAG = null;
}
