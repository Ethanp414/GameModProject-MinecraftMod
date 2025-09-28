package Entity.client;

import Entity.ModEntities;
import Entity.custom.GeckoEntity;
import dibs.bossfight.DePaulDibsBossFight;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;


public final class ModCommonEvents {

    public static void onAttributes(EntityAttributeCreationEvent e) {
        e.put(ModEntities.GECKO.get(), GeckoEntity.createAttributes().build());
    }
    private ModCommonEvents() {}
}