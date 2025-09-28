package Entity;

import java.util.function.Supplier;

import Entity.custom.GeckoEntity;
import dibs.bossfight.DePaulDibsBossFight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags.EntityTypes;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities 
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DePaulDibsBossFight.MODID);

    
    public static final Supplier<EntityType<Animal>> GECKO = 
        ENTITY_TYPES.register("gecko", ()-> new EntityType<>(GeckoEntity::new, MobCategory.CREATURE,
         false, true, false, false, null, null, 0, 0, 0, null, null, null)
    
);

    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }
}
