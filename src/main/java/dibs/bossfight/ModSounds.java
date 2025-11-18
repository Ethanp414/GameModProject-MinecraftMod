package dibs.bossfight;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers all sound events for the mod.
 */
public final class ModSounds {

    private ModSounds() {}

    // Register to the SOUND_EVENT registry under our mod id
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, DePaulDibsBossFight.MOD_ID);

    // ------------------------
    // Boss music (streamed)
    // ------------------------
    public static final DeferredHolder<SoundEvent, SoundEvent> BOSSFIGHT1 =
            SOUND_EVENTS.register("music_bossfight1", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "music/bossfight1")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BOSSFIGHT2 =
            SOUND_EVENTS.register("music_bossfight2", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "music/bossfight2")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BOSSDEFEAT1 =
            SOUND_EVENTS.register("music_bossdefeat1", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "music/bossdefeat1")));

    // ------------------------
    // Dibs SFX
    // ------------------------

    // One event mapped to 4 files in sounds.json (hurt1..4)
    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_HURT =
            SOUND_EVENTS.register("entity_dibs_hurt", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "entity/dibs/hurt")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_ROAR =
            SOUND_EVENTS.register("entity_dibs_roar", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "entity/dibs/roar")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_SIGH1 =
            SOUND_EVENTS.register("entity_dibs_sigh1", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "entity/dibs/sigh1")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_SIGH2 =
            SOUND_EVENTS.register("entity_dibs_sigh2", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "entity/dibs/sigh2")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_FOOTSTEP =
            SOUND_EVENTS.register("entity_dibs_footstep", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "entity/dibs/footstep")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DIBS_DEATH =
            SOUND_EVENTS.register("entity_dibs_death", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    DePaulDibsBossFight.MOD_ID, "entity/dibs/death")));
}
