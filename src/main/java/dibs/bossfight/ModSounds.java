package dibs.bossfight;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final String MOD_ID = "depauldibsbossfight";

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MOD_ID);

    // Ambient mumbling
    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_MUMBLE =
            SOUND_EVENTS.register("entity/dibs/mumble", SoundEvent::createVariableRangeEvent);

    // Hurt (we’ll randomize via sounds.json list)
    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_HURT =
            SOUND_EVENTS.register("entity/dibs/hurt", SoundEvent::createVariableRangeEvent);

    // Death
    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_DEATH =
            SOUND_EVENTS.register("entity/dibs/death", SoundEvent::createVariableRangeEvent);

    private ModSounds() {}
}
