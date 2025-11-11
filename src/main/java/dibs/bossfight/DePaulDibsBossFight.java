package dibs.bossfight;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import Entity.ModEntities;
import Entity.client.ClientEventHandler;
import Entity.client.ModCommonEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(DePaulDibsBossFight.MOD_ID)
public class DePaulDibsBossFight {

    public static final String MOD_ID = "depauldibsbossfight";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Registers
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    // Example block (kept from template)
    public static final DeferredBlock<Block> EXAMPLE_BLOCK =
            BLOCKS.registerSimpleBlock("example_block",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE));

    // Creative tab (uses Chicago Dog as icon)
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
    CREATIVE_MODE_TABS.register("main_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.depauldibsbossfight"))
        .withTabsBefore(CreativeModeTabs.COMBAT)
        // Use a lambda, but DON'T call .get() here during class init.
        .icon(() -> ModItems.CHICAGO_DOG.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(ModItems.CHICAGO_DOG.get());
            output.accept(ModItems.BASKETBALL.get());
        })
        .build());

    // Remove this line entirely, or:
public static final String MODID = MOD_ID; // keep only if you really want both names


public DePaulDibsBossFight(IEventBus modEventBus, ModContainer modContainer) {
    // 1) Registries FIRST
    ModItems.register(modEventBus);
    BLOCKS.register(modEventBus);
    CREATIVE_MODE_TABS.register(modEventBus);

    // 2) Listeners on the MOD bus
    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(this::addCreative);

    // 3) Global bus + config
    NeoForge.EVENT_BUS.register(this);
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    ModEntities.register(modEventBus);
    modEventBus.addListener(ModCommonEvents::onAttributes);

    if (FMLEnvironment.dist == Dist.CLIENT) 
    {
        ClientEventHandler.init(modEventBus);
    }

ModSounds.SOUND_EVENTS.register(modEventBus);

}



    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }
        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());
        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.CHICAGO_DOG);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
