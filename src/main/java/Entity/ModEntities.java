package Entity;

import java.util.function.Supplier;

import Entity.custom.DibsEntity;
import Entity.custom.GeckoEntity;
import Entity.custom.BasketballProjectileEntity;
import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities 
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DePaulDibsBossFight.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<GeckoEntity>> GECKO =
            ENTITY_TYPES.register("gecko",
                (Supplier<EntityType<GeckoEntity>>) () -> {
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DePaulDibsBossFight.MODID, "gecko");
                    ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);

                    return EntityType.Builder.<GeckoEntity>of(GeckoEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 0.6f)
                            .requiredFeatures(FeatureFlags.VANILLA)
                            .build(key);                                
                });

    public static final DeferredHolder<EntityType<?>, EntityType<DibsEntity>> DIBS = 
                ENTITY_TYPES.register("dibs",
                    (Supplier<EntityType<DibsEntity>>) () -> {
                        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DePaulDibsBossFight.MODID, "dibs");
                        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);

                        return EntityType.Builder.<DibsEntity>of(DibsEntity::new, MobCategory.MONSTER)
                            .sized(1, 4)
                            .requiredFeatures(FeatureFlags.VANILLA)
                            .build(key);
                    });


    // not working copy pasted like this, prob need to alter to fit the deferred register style above
    public static final Supplier<EntityType<BasketballProjectileEntity>> BASKETBALL = ENTITY_TYPES.registerEntityType(
    "basketball", BasketballProjectileEntity::new, MobCategory.MISC,
    builder -> builder.sized(0.5f, 0.5f));

    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }
}
