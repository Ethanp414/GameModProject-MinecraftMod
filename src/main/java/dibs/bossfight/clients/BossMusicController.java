package dibs.bossfight.clients;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

import Entity.custom.DibsEntity;
import dibs.bossfight.ModSounds;

public final class BossMusicController {

    private static int lastFightDibsId = -1;

    private static boolean defeatMusicPlaying = false;
    private static BlockPos defeatPos = null;

    private BossMusicController() {}

    public static void clientTick() {

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) return;

        Player player = mc.player;
        if (player == null) return;

        // ============================================================
        // 1. STOP DEFEAT MUSIC IF PLAYER TOO FAR
        // ============================================================
        if (defeatMusicPlaying && defeatPos != null) {

            double dist = player.blockPosition().distSqr(defeatPos);

            if (dist > (30 * 30)) {
                mc.getSoundManager().stop();   // Stop all sounds
                defeatMusicPlaying = false;
                defeatPos = null;
                System.out.println("[BOSS MUSIC] Stopped defeat music due to distance");
            }
        }

        // ============================================================
        // 2. FIND DIBS
        // ============================================================
        DibsEntity dibs = mc.level.getEntitiesOfClass(
                DibsEntity.class,
                player.getBoundingBox().inflate(48.0D)
        ).stream().findFirst().orElse(null);

        if (dibs == null || !dibs.isAlive()) {
            lastFightDibsId = -1;
            return;
        }

        // ============================================================
        // 3. CHECK AGGRO PLAYER TARGET
        // ============================================================
        int targetId = dibs.getSyncedTargetId();
        Entity raw = mc.level.getEntity(targetId);
        Player actualTarget = raw instanceof Player p ? p : null;

        boolean aggroOnPlayer =
            actualTarget != null &&
            actualTarget.getUUID().equals(player.getUUID());

        // ============================================================
        // 4. TRIGGER BOSSFIGHT1 MUSIC (only once)
        // ============================================================
        if (aggroOnPlayer && dibs.isAggroed()) {
            if (lastFightDibsId != dibs.getId()) {
                lastFightDibsId = dibs.getId();
                playBossfight1();
            }
        }

        // ============================================================
        // 5. IF DIBS IS DEAD, PLAY BOSSDEFEAT1
        // ============================================================
        if (dibs.isDeadOrDying()) {

            if (!defeatMusicPlaying) {

                defeatPos = dibs.getDeathPos();
                playBossDefeat();

                defeatMusicPlaying = true;
            }
        }
    }


    // ============================================================
    //            AUDIO FUNCTIONS
    // ============================================================
    private static void playBossfight1() {
        Minecraft mc = Minecraft.getInstance();
        mc.getMusicManager().stopPlaying();

        SimpleSoundInstance s =
                SimpleSoundInstance.forMusic(ModSounds.BOSSFIGHT1.get(), 0);

        mc.getSoundManager().play(s);
        System.out.println("[BOSS MUSIC] Played bossfight1 intro");
    }

    private static void playBossDefeat() {
        Minecraft mc = Minecraft.getInstance();
        mc.getMusicManager().stopPlaying();

        SimpleSoundInstance s =
                SimpleSoundInstance.forMusic(ModSounds.BOSSDEFEAT1.get(), 0);

        mc.getSoundManager().play(s);
        System.out.println("[BOSS MUSIC] Played bossdefeat1");
    }
}
