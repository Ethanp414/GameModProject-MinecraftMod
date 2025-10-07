package dibs.bossfight.entity;

import dibs.bossfight.DePaulDibsBossFight;
import dibs.bossfight.entity.custom.TomahawkProjectileEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.Registries;
import net.neoforged.neoforge.registries.ResourceKey;
import net.neoforged.neoforge.registries.ResourceLocation;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DePaulDibsBossFight.MODID);

    public static final Supplier<EntityType<TomahawkProjectileEntity>> TOMAHAWK =
            ENTITY_TYPES.register("tomahawk", () -> EntityType.Builder.<TomahawkProjectileEntity>of(TomahawkProjectileEntity::new, MobCategory.MISC)
                    .sized(0.5f, 1.15f).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("DePaulDibsBossFight", "tomahawk"))));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}