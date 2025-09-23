package Entity;

import java.util.function.Supplier;

import Entity.custom.GeckoEntity;
import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities 
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DePaulDibsBossFight.MODID);

    public static final Supplier<EntityType<GeckoEntity>> GECKO = 
        ENTITY_TYPES.register("gecko", ()-> EntityType.Builder.of(GeckoEntity::new, MobCategory.CREATURE)
        .sized(0.75f, 0.35f).build(ResourceKey.create(
                    Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(DePaulDibsBossFight.MODID, "gecko"))));

    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }
}
