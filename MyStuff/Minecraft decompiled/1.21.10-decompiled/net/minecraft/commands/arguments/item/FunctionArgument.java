package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FunctionArgument implements ArgumentType<FunctionArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("arguments.function.tag.unknown", $$0)
   );
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("arguments.function.unknown", $$0)
   );

   public static FunctionArgument functions() {
      return new FunctionArgument();
   }

   public FunctionArgument.Result parse(StringReader $$0) throws CommandSyntaxException {
      if ($$0.canRead() && $$0.peek() == '#') {
         $$0.skip();
         final ResourceLocation $$1 = ResourceLocation.read($$0);
         return new FunctionArgument.Result(this) {
            @Override
            public Collection<CommandFunction<CommandSourceStack>> create(CommandContext<CommandSourceStack> $$0) throws CommandSyntaxException {
               return FunctionArgument.getFunctionTag($$0, $$1);
            }

            @Override
            public Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> unwrap(
               CommandContext<CommandSourceStack> $$0
            ) throws CommandSyntaxException {
               return Pair.of($$1, Either.right(FunctionArgument.getFunctionTag($$0, $$1)));
            }

            @Override
            public Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> unwrapToCollection(CommandContext<CommandSourceStack> $$0) throws CommandSyntaxException {
               return Pair.of($$1, FunctionArgument.getFunctionTag($$0, $$1));
            }
         };
      } else {
         final ResourceLocation $$2 = ResourceLocation.read($$0);
         return new FunctionArgument.Result(this) {
            @Override
            public Collection<CommandFunction<CommandSourceStack>> create(CommandContext<CommandSourceStack> $$0) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.getFunction($$0, $$2));
            }

            @Override
            public Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> unwrap(
               CommandContext<CommandSourceStack> $$0
            ) throws CommandSyntaxException {
               return Pair.of($$2, Either.left(FunctionArgument.getFunction($$0, $$2)));
            }

            @Override
            public Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> unwrapToCollection(CommandContext<CommandSourceStack> $$0) throws CommandSyntaxException {
               return Pair.of($$2, Collections.singleton(FunctionArgument.getFunction($$0, $$2)));
            }
         };
      }
   }

   static CommandFunction<CommandSourceStack> getFunction(CommandContext<CommandSourceStack> $$0, ResourceLocation $$1) throws CommandSyntaxException {
      return (CommandFunction<CommandSourceStack>)$$0.getSource()
         .getServer()
         .getFunctions()
         .get($$1)
         .orElseThrow(() -> ERROR_UNKNOWN_FUNCTION.create($$1.toString()));
   }

   static Collection<CommandFunction<CommandSourceStack>> getFunctionTag(CommandContext<CommandSourceStack> $$0, ResourceLocation $$1) throws CommandSyntaxException {
      Collection<CommandFunction<CommandSourceStack>> $$2 = $$0.getSource().getServer().getFunctions().getTag($$1);
      if ($$2 == null) {
         throw ERROR_UNKNOWN_TAG.create($$1.toString());
      } else {
         return $$2;
      }
   }

   public static Collection<CommandFunction<CommandSourceStack>> getFunctions(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
      return $$0.<FunctionArgument.Result>getArgument($$1, FunctionArgument.Result.class).create($$0);
   }

   public static Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> getFunctionOrTag(
      CommandContext<CommandSourceStack> $$0, String $$1
   ) throws CommandSyntaxException {
      return $$0.<FunctionArgument.Result>getArgument($$1, FunctionArgument.Result.class).unwrap($$0);
   }

   public static Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> getFunctionCollection(
      CommandContext<CommandSourceStack> $$0, String $$1
   ) throws CommandSyntaxException {
      return $$0.<FunctionArgument.Result>getArgument($$1, FunctionArgument.Result.class).unwrapToCollection($$0);
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public interface Result {
      Collection<CommandFunction<CommandSourceStack>> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

      Pair<ResourceLocation, Either<CommandFunction<CommandSourceStack>, Collection<CommandFunction<CommandSourceStack>>>> unwrap(
         CommandContext<CommandSourceStack> var1
      ) throws CommandSyntaxException;

      Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> unwrapToCollection(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
   }
}
