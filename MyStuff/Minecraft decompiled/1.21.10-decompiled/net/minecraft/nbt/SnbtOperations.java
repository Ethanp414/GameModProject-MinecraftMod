package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public class SnbtOperations {
   static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_STRING_UUID = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_string_uuid"))
   );
   static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_NUMBER_OR_BOOLEAN = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_number_or_boolean"))
   );
   public static final String BUILTIN_TRUE = "true";
   public static final String BUILTIN_FALSE = "false";
   public static final Map<SnbtOperations.BuiltinKey, SnbtOperations.BuiltinOperation> BUILTIN_OPERATIONS = Map.of(
      new SnbtOperations.BuiltinKey("bool", 1), new SnbtOperations.BuiltinOperation() {
         @Override
         public <T> T run(DynamicOps<T> $$0, List<T> $$1, ParseState<StringReader> $$2) {
            Boolean $$3 = convert($$0, (T)$$1.getFirst());
            if ($$3 == null) {
               $$2.errorCollector().store($$2.mark(), SnbtOperations.ERROR_EXPECTED_NUMBER_OR_BOOLEAN);
               return null;
            } else {
               return $$0.createBoolean($$3);
            }
         }
   
         @Nullable
         private static <T> Boolean convert(DynamicOps<T> $$0, T $$1) {
            Optional<Boolean> $$2 = $$0.getBooleanValue($$1).result();
            if ($$2.isPresent()) {
               return (Boolean)$$2.get();
            } else {
               Optional<Number> $$3 = $$0.getNumberValue($$1).result();
               return $$3.isPresent() ? ((Number)$$3.get()).doubleValue() != 0.0 : null;
            }
         }
      }, new SnbtOperations.BuiltinKey("uuid", 1), new SnbtOperations.BuiltinOperation() {
         @Override
         public <T> T run(DynamicOps<T> $$0, List<T> $$1, ParseState<StringReader> $$2) {
            Optional<String> $$3 = $$0.getStringValue((T)$$1.getFirst()).result();
            if ($$3.isEmpty()) {
               $$2.errorCollector().store($$2.mark(), SnbtOperations.ERROR_EXPECTED_STRING_UUID);
               return null;
            } else {
               UUID $$4;
               try {
                  $$4 = UUID.fromString((String)$$3.get());
               } catch (IllegalArgumentException var7) {
                  $$2.errorCollector().store($$2.mark(), SnbtOperations.ERROR_EXPECTED_STRING_UUID);
                  return null;
               }
   
               return $$0.createIntList(IntStream.of(UUIDUtil.uuidToIntArray($$4)));
            }
         }
      }
   );
   public static final SuggestionSupplier<StringReader> BUILTIN_IDS = new SuggestionSupplier<StringReader>() {
      private final Set<String> keys = (Set<String>)Stream.concat(
            Stream.of("false", "true"), SnbtOperations.BUILTIN_OPERATIONS.keySet().stream().map(SnbtOperations.BuiltinKey::id)
         )
         .collect(Collectors.toSet());

      @Override
      public Stream<String> possibleValues(ParseState<StringReader> $$0) {
         return this.keys.stream();
      }
   };

   public static record BuiltinKey(String id, int argCount) {
      public String toString() {
         return this.id + "/" + this.argCount;
      }
   }

   public interface BuiltinOperation {
      @Nullable
      <T> T run(DynamicOps<T> var1, List<T> var2, ParseState<StringReader> var3);
   }
}
