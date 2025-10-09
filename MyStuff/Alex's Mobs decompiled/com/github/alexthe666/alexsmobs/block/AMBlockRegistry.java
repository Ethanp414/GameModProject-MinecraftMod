package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.item.AMBlockItem;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.BlockItemAMRender;
import java.util.function.Supplier;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMBlockRegistry {
   public static final Properties PURPUR_PLANKS_PROPERTIES = Properties.m_284310_()
      .m_284180_(MapColor.f_283765_)
      .m_60913_(0.5F, 1.0F)
      .m_60918_(SoundType.f_56736_);
   public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, "alexsmobs");
   public static final RegistryObject<Block> BANANA_PEEL = registerBlockAndItem("banana_peel", () -> new BlockBananaPeel());
   public static final RegistryObject<Block> HUMMINGBIRD_FEEDER = registerBlockAndItem("hummingbird_feeder", () -> new BlockHummingbirdFeeder());
   public static final RegistryObject<Block> CROCODILE_EGG = registerBlockAndItem("crocodile_egg", () -> new BlockReptileEgg(AMEntityRegistry.CROCODILE));
   public static final RegistryObject<Block> GUSTMAKER = registerBlockAndItem("gustmaker", () -> new BlockGustmaker());
   public static final RegistryObject<Block> STRADDLITE_BLOCK = registerBlockAndItem(
      "straddlite_block",
      () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283906_).m_60999_().m_60913_(1.0F, 1200.0F).m_60918_(SoundType.f_56726_)),
      new net.minecraft.world.item.Item.Properties().m_41486_(),
      false
   );
   public static final RegistryObject<Block> PLATYPUS_EGG = registerBlockAndItem("platypus_egg", () -> new BlockReptileEgg(AMEntityRegistry.PLATYPUS));
   public static final RegistryObject<Block> LEAFCUTTER_ANTHILL = registerBlockAndItem("leafcutter_anthill", () -> new BlockLeafcutterAnthill());
   public static final RegistryObject<Block> LEAFCUTTER_ANT_CHAMBER = registerBlockAndItem("leafcutter_ant_chamber", () -> new BlockLeafcutterAntChamber());
   public static final RegistryObject<Block> CAPSID = registerBlockAndItem("capsid", () -> new BlockCapsid());
   public static final RegistryObject<Block> VOID_WORM_BEAK = registerBlockAndItem("void_worm_beak", () -> new BlockVoidWormBeak());
   public static final RegistryObject<Block> VOID_WORM_EFFIGY = registerBlockAndItem("void_worm_effigy", () -> new BlockVoidWormEffigy());
   public static final RegistryObject<Block> TERRAPIN_EGG = registerBlockAndItem("terrapin_egg", () -> new BlockTerrapinEgg());
   public static final RegistryObject<Block> RAINBOW_GLASS = registerBlockAndItem("rainbow_glass", () -> new BlockRainbowGlass());
   public static final RegistryObject<Block> BISON_FUR_BLOCK = registerBlockAndItem(
      "bison_fur_block", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_60913_(0.6F, 1.0F).m_60918_(SoundType.f_56745_))
   );
   public static final RegistryObject<Block> BISON_CARPET = registerBlockAndItem("bison_carpet", () -> new BlockBisonCarpet());
   public static final RegistryObject<Block> SAND_CIRCLE = registerBlockAndItem(
      "sand_circle", () -> new SandBlock(14406560, Properties.m_60926_(Blocks.f_49992_)), new net.minecraft.world.item.Item.Properties(), false
   );
   public static final RegistryObject<Block> RED_SAND_CIRCLE = registerBlockAndItem(
      "red_sand_circle", () -> new SandBlock(11098145, Properties.m_60926_(Blocks.f_49993_)), new net.minecraft.world.item.Item.Properties(), false
   );
   public static final RegistryObject<Block> ENDER_RESIDUE = registerBlockAndItem("ender_residue", () -> new BlockEnderResidue());
   public static final RegistryObject<Block> TRANSMUTATION_TABLE = registerBlockAndItem(
      "transmutation_table", () -> new BlockTransmutationTable(), new net.minecraft.world.item.Item.Properties().m_41497_(Rarity.EPIC).m_41486_(), true
   );
   public static final RegistryObject<Block> SCULK_BOOMER = registerBlockAndItem("sculk_boomer", () -> new BlockSculkBoomer());
   public static final RegistryObject<Block> SKUNK_SPRAY = DEF_REG.register("skunk_spray", () -> new BlockSkunkSpray());
   public static final RegistryObject<Block> BANANA_SLUG_SLIME_BLOCK = registerBlockAndItem("banana_slug_slime_block", () -> new BlockBananaSlugSlime());
   public static final RegistryObject<Block> CRYSTALIZED_BANANA_SLUG_MUCUS = registerBlockAndItem(
      "crystalized_banana_slug_mucus", () -> new BlockCrystalizedMucus()
   );
   public static final RegistryObject<Block> CAIMAN_EGG = registerBlockAndItem("caiman_egg", () -> new BlockReptileEgg(AMEntityRegistry.CAIMAN));
   public static final RegistryObject<Block> TRIOPS_EGGS = registerBlockAndItem("triops_eggs", () -> new BlockTriopsEggs());

   public static RegistryObject<Block> registerBlockAndItem(String name, Supplier<Block> block) {
      return registerBlockAndItem(name, block, new net.minecraft.world.item.Item.Properties(), false);
   }

   public static RegistryObject<Block> registerBlockAndItem(
      String name, Supplier<Block> block, net.minecraft.world.item.Item.Properties blockItemProps, boolean specialRender
   ) {
      RegistryObject<Block> blockObj = DEF_REG.register(name, block);
      AMItemRegistry.DEF_REG
         .register(name, () -> (AMBlockItem)(specialRender ? new BlockItemAMRender(blockObj, blockItemProps) : new AMBlockItem(blockObj, blockItemProps)));
      return blockObj;
   }
}
