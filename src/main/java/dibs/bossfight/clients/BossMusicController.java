package dibs.bossfight.clients;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import Entity.custom.DibsEntity;          // your project’s entity package
import dibs.bossfight.ModSounds;         // must expose BOSSFIGHT1 (bossdefeat1 is handled elsewhere)

/**
 * Minimal controller to start bossfight1 when Dibs aggroes the player.
 * We intentionally ignore bossfight2 for now (per your request).
 */
public class BossMusicController {

    private static final RandomSource RNG = RandomSource.create();

    // simple state so bossfight1 only plays once per “fight”
    private static boolean fightStarted = false;

    /**
     * Called every client tick (see ClientTicks below).
     */
    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) return;
        Player player = mc.player;
        if (player == null) return;

        // Look for ANY Dibs within ~48 blocks
        DibsEntity dibs = player.level().getEntitiesOfClass(
                DibsEntity.class,
                player.getBoundingBox().inflate(48.0D)
        ).stream().findFirst().orElse(null);

        if (dibs == null) {
            // No boss around — allow next encounter to play intro again.
            fightStarted = false;
            return;
        }

        // "Aggroed" = boss target is this player
        boolean aggroOnPlayer = dibs.getTarget() != null && dibs.getTarget() == player;

        if (aggroOnPlayer && !fightStarted) {
            fightStarted = true;
            playIntroOnce();
        }
    }

    private static void playIntroOnce() {
        var ev = ModSounds.BOSSFIGHT1.get();
        if (ev != null) {
            Minecraft.getInstance().getSoundManager()
                    .play(SimpleSoundInstance.forMusic(ev, 1.0f));
        }
    }
}
