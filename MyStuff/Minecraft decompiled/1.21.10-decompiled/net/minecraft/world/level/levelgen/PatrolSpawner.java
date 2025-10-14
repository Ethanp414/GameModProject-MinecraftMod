package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public class PatrolSpawner implements CustomSpawner {
   private int nextTick;

   @Override
   public void tick(ServerLevel $$0, boolean $$1) {
      if ($$1) {
         if ($$0.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            RandomSource $$2 = $$0.random;
            --this.nextTick;
            if (this.nextTick <= 0) {
               this.nextTick += 12000 + $$2.nextInt(1200);
               long $$3 = $$0.getDayTime() / 24000L;
               if ($$3 >= 5L && $$0.isBrightOutside()) {
                  if ($$2.nextInt(5) == 0) {
                     int $$4 = $$0.players().size();
                     if ($$4 >= 1) {
                        Player $$5 = (Player)$$0.players().get($$2.nextInt($$4));
                        if (!$$5.isSpectator()) {
                           if (!$$0.isCloseToVillage($$5.blockPosition(), 2)) {
                              int $$6 = (24 + $$2.nextInt(24)) * ($$2.nextBoolean() ? -1 : 1);
                              int $$7 = (24 + $$2.nextInt(24)) * ($$2.nextBoolean() ? -1 : 1);
                              BlockPos.MutableBlockPos $$8 = $$5.blockPosition().mutable().move($$6, 0, $$7);
                              int $$9 = 10;
                              if ($$0.hasChunksAt($$8.getX() - 10, $$8.getZ() - 10, $$8.getX() + 10, $$8.getZ() + 10)) {
                                 Holder<Biome> $$10 = $$0.getBiome($$8);
                                 if (!$$10.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                    int $$11 = (int)Math.ceil((double)$$0.getCurrentDifficultyAt($$8).getEffectiveDifficulty()) + 1;

                                    for(int $$12 = 0; $$12 < $$11; ++$$12) {
                                       $$8.setY($$0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, $$8).getY());
                                       if ($$12 == 0) {
                                          if (!this.spawnPatrolMember($$0, $$8, $$2, true)) {
                                             break;
                                          }
                                       } else {
                                          this.spawnPatrolMember($$0, $$8, $$2, false);
                                       }

                                       $$8.setX($$8.getX() + $$2.nextInt(5) - $$2.nextInt(5));
                                       $$8.setZ($$8.getZ() + $$2.nextInt(5) - $$2.nextInt(5));
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean spawnPatrolMember(ServerLevel $$0, BlockPos $$1, RandomSource $$2, boolean $$3) {
      BlockState $$4 = $$0.getBlockState($$1);
      if (!NaturalSpawner.isValidEmptySpawnBlock($$0, $$1, $$4, $$4.getFluidState(), EntityType.PILLAGER)) {
         return false;
      } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, $$0, EntitySpawnReason.PATROL, $$1, $$2)) {
         return false;
      } else {
         PatrollingMonster $$5 = EntityType.PILLAGER.create($$0, EntitySpawnReason.PATROL);
         if ($$5 != null) {
            if ($$3) {
               $$5.setPatrolLeader(true);
               $$5.findPatrolTarget();
            }

            $$5.setPos((double)$$1.getX(), (double)$$1.getY(), (double)$$1.getZ());
            $$5.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$1), EntitySpawnReason.PATROL, null);
            $$0.addFreshEntityWithPassengers($$5);
            return true;
         } else {
            return false;
         }
      }
   }
}
