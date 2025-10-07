package Entity.client;

import Entity.ModEntities;
import Entity.custom.DibsEntity;
import Entity.custom.GeckoEntity;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;


public final class ModCommonEvents {

    public static void onAttributes(EntityAttributeCreationEvent e) 
    {
        e.put(ModEntities.GECKO.get(), GeckoEntity.createAttributes().build());
        e.put(ModEntities.DIBS.get(), DibsEntity.createAttributes().build());
    }
    private ModCommonEvents() {}
}