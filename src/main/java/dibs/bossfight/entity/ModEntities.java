package dibs.bossfight.entity;

import java.util.function.Supplier;

import dibs.bossfight.DePaulDibsBossFight;
import dibs.bossfight.entity.custom.BasketballProjectileEntity;
import dibs.bossfight.entity.custom.TomahawkProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(DePaulDibsBossFight.MODID);

/*
    public static final Supplier<EntityType<TomahawkProjectileEntity>> TOMAHAWK = ENTITY_TYPES.register(
            "tomahawk", 
            () -> EntityType.Builder.of(
                TomahawkProjectileEntity::new, 
                MobCategory.MISC)

                .sized(0.5f, 1.15f)
                );
*/

public static final Supplier<EntityType<TomahawkProjectileEntity>> TOMAHAWK = ENTITY_TYPES.registerEntityType(
    "tomahawk", TomahawkProjectileEntity::new, MobCategory.MISC,
    builder -> builder.sized(0.5f, 1.15f));

public static final Supplier<EntityType<BasketballProjectileEntity>> BASKETBALL = ENTITY_TYPES.registerEntityType(
    "basketball", BasketballProjectileEntity::new, MobCategory.MISC,
    builder -> builder.sized(0.5f, 0.5f));


public static void register(IEventBus eventBus) {
    ENTITY_TYPES.register(eventBus);
}


}

