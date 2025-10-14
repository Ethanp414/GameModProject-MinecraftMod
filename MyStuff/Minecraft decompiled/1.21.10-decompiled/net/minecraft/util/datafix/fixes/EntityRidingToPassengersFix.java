package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EntityRidingToPassengersFix extends DataFix {
   public EntityRidingToPassengersFix(Schema $$0, boolean $$1) {
      super($$0, $$1);
   }

   @Override
   public TypeRewriteRule makeRule() {
      Schema $$0 = this.getInputSchema();
      Schema $$1 = this.getOutputSchema();
      Type<?> $$2 = $$0.getTypeRaw(References.ENTITY_TREE);
      Type<?> $$3 = $$1.getTypeRaw(References.ENTITY_TREE);
      Type<?> $$4 = $$0.getTypeRaw(References.ENTITY);
      return this.cap($$0, $$1, $$2, $$3, $$4);
   }

   private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule cap(
      Schema $$0, Schema $$1, Type<OldEntityTree> $$2, Type<NewEntityTree> $$3, Type<Entity> $$4
   ) {
      Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> $$5 = DSL.named(
         References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", $$2)), $$4)
      );
      Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> $$6 = DSL.named(
         References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list($$3))), $$4)
      );
      Type<?> $$7 = $$0.getType(References.ENTITY_TREE);
      Type<?> $$8 = $$1.getType(References.ENTITY_TREE);
      if (!Objects.equals($$7, $$5)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if (!$$8.equals($$6, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> $$9 = DSL.typeFinder($$5);
         OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> $$10 = DSL.typeFinder($$6);
         OpticFinder<NewEntityTree> $$11 = DSL.typeFinder($$3);
         Type<?> $$12 = $$0.getType(References.PLAYER);
         Type<?> $$13 = $$1.getType(References.PLAYER);
         return TypeRewriteRule.seq(
            this.fixTypeEverywhere(
               "EntityRidingToPassengerFix",
               $$5,
               $$6,
               $$5x -> $$6x -> {
                     Optional<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> $$7xx = Optional.empty();
                     Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>> $$8xx = $$6x;
      
                     while(true) {
                        Either<List<NewEntityTree>, Unit> $$9xx = DataFixUtils.orElse(
                           $$7xx.map(
                              $$4xxx -> {
                                 Typed<NewEntityTree> $$5xxxx = (Typed)$$3.pointTyped($$5x)
                                    .orElseThrow(() -> new IllegalStateException("Could not create new entity tree"));
                                 NewEntityTree $$6xxx = (NewEntityTree)$$5xxxx.set($$10, $$4xxx)
                                    .getOptional($$11)
                                    .orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
                                 return Either.left(ImmutableList.<NewEntityTree>of($$6xxx));
                              }
                           ),
                           Either.right(DSL.unit())
                        );
                        $$7xx = Optional.of(Pair.of(References.ENTITY_TREE.typeName(), Pair.of($$9xx, $$8xx.getSecond().getSecond())));
                        Optional<OldEntityTree> $$10xx = $$8xx.getSecond().getFirst().left();
                        if ($$10xx.isEmpty()) {
                           return (Pair)$$7xx.orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
                        }
      
                        $$8xx = (Pair)new Typed<>($$2, $$5x, (OldEntityTree)$$10xx.get())
                           .getOptional($$9)
                           .orElseThrow(() -> new IllegalStateException("Should always have an entity here"));
                     }
                  }
            ),
            this.writeAndRead("player RootVehicle injecter", $$12, $$13)
         );
      }
   }
}
