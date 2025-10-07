package Entity.client;

import java.util.EnumSet;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DibsCombatGoal extends Goal
{
    private final Mob dibsMob;
    private LivingEntity target;

    private final double meleeRange;
    private final double rangedRange;
    private final double moveSpeed;
    private int attackCooldown = 0;

    public DibsCombatGoal(Mob mob, double _meleeRange, double _rangedRange, double _moveSpeed)
    { 
        dibsMob = mob;
        meleeRange = _meleeRange;
        rangedRange = _rangedRange;
        moveSpeed = _moveSpeed;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() 
    {
        LivingEntity entity = dibsMob.getTarget();
        return entity != null && entity.isAlive() && dibsMob.canAttack(entity);
    }   

    @Override
    public boolean canContinueToUse() 
    {
        LivingEntity entity = dibsMob.getTarget();
        return entity != null && entity.isAlive() && dibsMob.distanceToSqr(entity) <= (rangedRange * rangedRange + 4.0); 
    }

    @Override
    public void start() 
    {
        target = dibsMob.getTarget();
        attackCooldown = 0;
    }

    @Override
    public void stop() 
    {
        target = null;
        dibsMob.getNavigation().stop();
    }

     @Override
    public void tick() 
    {
        if (target == null || !target.isAlive()) return;

        //look at target
        dibsMob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distSq = dibsMob.distanceToSqr(target);
        boolean inMelee = distSq <= meleeRange * meleeRange;
        boolean inRanged = distSq <= rangedRange * rangedRange;

        // Approach target if out of melee/ranged comfortable range
        if (!inMelee) 
        {
            dibsMob.getNavigation().moveTo(target, moveSpeed);
        } 
        else 
        {
            dibsMob.getNavigation().stop();
        }

        if (attackCooldown > 0)
        {
            attackCooldown--;
        }
        else
        {
            if (inMelee) 
            {
                tryMeleeAttack();
            } 
            else if (inRanged) 
            {
                tryRangedAttack();
            }
            attackCooldown = 1; // 20 ticks per second, 4 second cooldown between attacks
        }
    }

    private void tryMeleeAttack()
    {
        if(dibsMob.level() instanceof ServerLevel serverLevel)
        {
            dibsMob.doHurtTarget(serverLevel, target);
        }
    }

    private void tryRangedAttack()
    {
        // Example using a Snowball projectile, can be swapped for custom projectiles
        ItemStack sBall = new ItemStack(Items.FIRE_CHARGE);
        Snowball sb = new Snowball(dibsMob.level(), dibsMob, sBall);
        double dx = target.getX() - dibsMob.getX();
        double dy = (target.getY() + (double) target.getEyeHeight() - 1.1) - sb.getY();
        double dz = target.getZ() - dibsMob.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

    // set velocity — tune power and inaccuracy
        float velocity = (float) (1.6F);
        float inaccuracy = 4.0F;
        sb.shoot(dx, dy + distance * 0.2, dz, velocity, inaccuracy);

        dibsMob.level().addFreshEntity(sb);
        dibsMob.swing(InteractionHand.MAIN_HAND); // visual swing
    }
}
