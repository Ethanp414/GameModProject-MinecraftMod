package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityAnchorArgument implements ArgumentType<EntityAnchorArgument.Anchor> {
   private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
   private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("argument.anchor.invalid", $$0)
   );

   public static EntityAnchorArgument.Anchor getAnchor(CommandContext<CommandSourceStack> $$0, String $$1) {
      return $$0.getArgument($$1, EntityAnchorArgument.Anchor.class);
   }

   public static EntityAnchorArgument anchor() {
      return new EntityAnchorArgument();
   }

   public EntityAnchorArgument.Anchor parse(StringReader $$0) throws CommandSyntaxException {
      int $$1 = $$0.getCursor();
      String $$2 = $$0.readUnquotedString();
      EntityAnchorArgument.Anchor $$3 = EntityAnchorArgument.Anchor.getByName($$2);
      if ($$3 == null) {
         $$0.setCursor($$1);
         throw ERROR_INVALID.createWithContext($$0, $$2);
      } else {
         return $$3;
      }
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
      return SharedSuggestionProvider.suggest(EntityAnchorArgument.Anchor.BY_NAME.keySet(), $$1);
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static enum Anchor {
      FEET("feet", ($$0, $$1) -> $$0),
      EYES("eyes", ($$0, $$1) -> new Vec3($$0.x, $$0.y + (double)$$1.getEyeHeight(), $$0.z));

      static final Map<String, EntityAnchorArgument.Anchor> BY_NAME = Util.make(Maps.newHashMap(), $$0 -> {
         for(EntityAnchorArgument.Anchor $$1 : values()) {
            $$0.put($$1.name, $$1);
         }
      });
      private final String name;
      private final BiFunction<Vec3, Entity, Vec3> transform;

      private Anchor(final String param3, final BiFunction<Vec3, Entity, Vec3> param4) {
         this.name = $$0;
         this.transform = $$1;
      }

      @Nullable
      public static EntityAnchorArgument.Anchor getByName(String $$0) {
         return (EntityAnchorArgument.Anchor)BY_NAME.get($$0);
      }

      public Vec3 apply(Entity $$0) {
         return (Vec3)this.transform.apply($$0.position(), $$0);
      }

      public Vec3 apply(CommandSourceStack $$0) {
         Entity $$1 = $$0.getEntity();
         return $$1 == null ? $$0.getPosition() : (Vec3)this.transform.apply($$0.getPosition(), $$1);
      }
   }
}
