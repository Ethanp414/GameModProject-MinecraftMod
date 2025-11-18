package dibs.bossfight.clients;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import Entity.custom.DibsEntity;
import dibs.bossfight.ModSounds;

/**
 * Handles bossfight1 music on the CLIENT side.
 */
public final class BossMusicController {

    // Track music by Dibs entity ID
    private static int lastFightDibsId = -1;

    private BossMusicController() {}

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) return;

        Player player = mc.player;
        if (player == null) return;

        // Search for Dibs near the player
        DibsEntity dibs = mc.level.getEntitiesOfClass(
                DibsEntity.class,
                player.getBoundingBox().inflate(48.0D)
        ).stream().findFirst().orElse(null);

        // No Dibs → reset tracking
        if (dibs == null || !dibs.isAlive()) {
            lastFightDibsId = -1;
            return;
        }

        // --- Use synced target from server ---
        Player actualTarget = (Player) dibs.getTarget();


        boolean aggroOnPlayer =
                actualTarget != null &&
                actualTarget.getUUID().equals(player.getUUID());

        if (aggroOnPlayer && dibs.isAggressive()) {
            // First time this Dibs starts fighting this client
            if (lastFightDibsId != dibs.getId()) {
                lastFightDibsId = dibs.getId();
                playIntroMusic();
            }
        }
    }

    private static void playIntroMusic() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        SoundEvent ev = ModSounds.BOSSFIGHT1.get();
        if (ev == null) {
            System.out.println("[BOSS MUSIC] ERROR: bossfight1 SoundEvent null");
            return;
        }

        // Stop vanilla background music
        mc.getMusicManager().stopPlaying();

        SimpleSoundInstance sound = SimpleSoundInstance.forMusic(ev, 0);
        mc.getSoundManager().play(sound);

        System.out.println("[BOSS MUSIC] bossfight1 music started");
    }
}
