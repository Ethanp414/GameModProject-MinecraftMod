package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.parsing.packrat.commands.Grammar;

public class TagParser<T> {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(Component.translatable("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_COMPOUND = new SimpleCommandExceptionType(
      Component.translatable("argument.nbt.expected.compound")
   );
   public static final char ELEMENT_SEPARATOR = ',';
   public static final char NAME_VALUE_SEPARATOR = ':';
   private static final TagParser<Tag> NBT_OPS_PARSER = create(NbtOps.INSTANCE);
   public static final Codec<CompoundTag> FLATTENED_CODEC = Codec.STRING.comapFlatMap($$0 -> {
      try {
         Tag $$1 = NBT_OPS_PARSER.parseFully($$0);
         return $$1 instanceof CompoundTag $$2 ? DataResult.success($$2, Lifecycle.stable()) : DataResult.error(() -> "Expected compound tag, got " + $$1);
      } catch (CommandSyntaxException var3) {
         return DataResult.error(var3::getMessage);
      }
   }, CompoundTag::toString);
   public static final Codec<CompoundTag> LENIENT_CODEC = Codec.withAlternative(FLATTENED_CODEC, CompoundTag.CODEC);
   private final DynamicOps<T> ops;
   private final Grammar<T> grammar;

   private TagParser(DynamicOps<T> $$0, Grammar<T> $$1) {
      this.ops = $$0;
      this.grammar = $$1;
   }

   public DynamicOps<T> getOps() {
      return this.ops;
   }

   public static <T> TagParser<T> create(DynamicOps<T> $$0) {
      return new TagParser<>($$0, SnbtGrammar.createParser($$0));
   }

   private static CompoundTag castToCompoundOrThrow(StringReader $$0, Tag $$1) throws CommandSyntaxException {
      if ($$1 instanceof CompoundTag) {
         return (CompoundTag)$$1;
      } else {
         throw ERROR_EXPECTED_COMPOUND.createWithContext($$0);
      }
   }

   public static CompoundTag parseCompoundFully(String $$0) throws CommandSyntaxException {
      StringReader $$1 = new StringReader($$0);
      return castToCompoundOrThrow($$1, NBT_OPS_PARSER.parseFully($$1));
   }

   public T parseFully(String $$0) throws CommandSyntaxException {
      return this.parseFully(new StringReader($$0));
   }

   public T parseFully(StringReader $$0) throws CommandSyntaxException {
      T $$1 = this.grammar.parseForCommands($$0);
      $$0.skipWhitespace();
      if ($$0.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext($$0);
      } else {
         return $$1;
      }
   }

   public T parseAsArgument(StringReader $$0) throws CommandSyntaxException {
      return this.grammar.parseForCommands($$0);
   }

   public static CompoundTag parseCompoundAsArgument(StringReader $$0) throws CommandSyntaxException {
      Tag $$1 = NBT_OPS_PARSER.parseAsArgument($$0);
      return castToCompoundOrThrow($$0, $$1);
   }
}
