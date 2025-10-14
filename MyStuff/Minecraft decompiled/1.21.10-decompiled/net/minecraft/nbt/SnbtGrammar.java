package net.minecraft.nbt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.primitives.UnsignedBytes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.chars.CharList;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.GreedyPatternParseRule;
import net.minecraft.util.parsing.packrat.commands.GreedyPredicateParseRule;
import net.minecraft.util.parsing.packrat.commands.NumberRunParseRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.UnquotedStringParseRule;

public class SnbtGrammar {
   private static final DynamicCommandExceptionType ERROR_NUMBER_PARSE_FAILURE = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("snbt.parser.number_parse_failure", $$0)
   );
   static final DynamicCommandExceptionType ERROR_EXPECTED_HEX_ESCAPE = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("snbt.parser.expected_hex_escape", $$0)
   );
   private static final DynamicCommandExceptionType ERROR_INVALID_CODEPOINT = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("snbt.parser.invalid_codepoint", $$0)
   );
   private static final DynamicCommandExceptionType ERROR_NO_SUCH_OPERATION = new DynamicCommandExceptionType(
      $$0 -> Component.translatableEscape("snbt.parser.no_such_operation", $$0)
   );
   static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_INTEGER_TYPE = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_integer_type"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_FLOAT_TYPE = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_float_type"))
   );
   static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_NON_NEGATIVE_NUMBER = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_non_negative_number"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_INVALID_CHARACTER_NAME = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_character_name"))
   );
   static final DelayedException<CommandSyntaxException> ERROR_INVALID_ARRAY_ELEMENT_TYPE = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_array_element_type"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_INVALID_UNQUOTED_START = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_unquoted_start"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_UNQUOTED_STRING = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_unquoted_string"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_INVALID_STRING_CONTENTS = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_string_contents"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_BINARY_NUMERAL = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_binary_numeral"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_UNDESCORE_NOT_ALLOWED = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.underscore_not_allowed"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_DECIMAL_NUMERAL = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_decimal_numeral"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_HEX_NUMERAL = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_hex_numeral"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_EMPTY_KEY = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.empty_key"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_LEADING_ZERO_NOT_ALLOWED = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.leading_zero_not_allowed"))
   );
   private static final DelayedException<CommandSyntaxException> ERROR_INFINITY_NOT_ALLOWED = DelayedException.create(
      new SimpleCommandExceptionType(Component.translatable("snbt.parser.infinity_not_allowed"))
   );
   private static final HexFormat HEX_ESCAPE = HexFormat.of().withUpperCase();
   private static final NumberRunParseRule BINARY_NUMERAL = new NumberRunParseRule(ERROR_EXPECTED_BINARY_NUMERAL, ERROR_UNDESCORE_NOT_ALLOWED) {
      @Override
      protected boolean isAccepted(char $$0) {
         return switch($$0) {
            case '0', '1', '_' -> true;
            default -> false;
         };
      }
   };
   private static final NumberRunParseRule DECIMAL_NUMERAL = new NumberRunParseRule(ERROR_EXPECTED_DECIMAL_NUMERAL, ERROR_UNDESCORE_NOT_ALLOWED) {
      @Override
      protected boolean isAccepted(char $$0) {
         return switch($$0) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_' -> true;
            default -> false;
         };
      }
   };
   private static final NumberRunParseRule HEX_NUMERAL = new NumberRunParseRule(ERROR_EXPECTED_HEX_NUMERAL, ERROR_UNDESCORE_NOT_ALLOWED) {
      @Override
      protected boolean isAccepted(char $$0) {
         return switch($$0) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', '_', 'a', 'b', 'c', 'd', 'e', 'f' -> true;
            default -> false;
         };
      }
   };
   private static final GreedyPredicateParseRule PLAIN_STRING_CHUNK = new GreedyPredicateParseRule(1, ERROR_INVALID_STRING_CONTENTS) {
      @Override
      protected boolean isAccepted(char $$0) {
         return switch($$0) {
            case '"', '\'', '\\' -> false;
            default -> true;
         };
      }
   };
   private static final StringReaderTerms.TerminalCharacters NUMBER_LOOKEAHEAD = new StringReaderTerms.TerminalCharacters(CharList.of()) {
      @Override
      protected boolean isAccepted(char $$0) {
         return SnbtGrammar.canStartNumber($$0);
      }
   };
   private static final Pattern UNICODE_NAME = Pattern.compile("[-a-zA-Z0-9 ]+");

   static DelayedException<CommandSyntaxException> createNumberParseError(NumberFormatException $$0) {
      return DelayedException.create(ERROR_NUMBER_PARSE_FAILURE, $$0.getMessage());
   }

   @Nullable
   public static String escapeControlCharacters(char $$0) {
      return switch($$0) {
         case '\b' -> "b";
         case '\t' -> "t";
         case '\n' -> "n";
         default -> $$0 < ' ' ? "x" + HEX_ESCAPE.toHexDigits((byte)$$0) : null;
         case '\f' -> "f";
         case '\r' -> "r";
      };
   }

   private static boolean isAllowedToStartUnquotedString(char $$0) {
      return !canStartNumber($$0);
   }

   static boolean canStartNumber(char $$0) {
      return switch($$0) {
         case '+', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
         default -> false;
      };
   }

   static boolean needsUnderscoreRemoval(String $$0) {
      return $$0.indexOf(95) != -1;
   }

   private static void cleanAndAppend(StringBuilder $$0, String $$1) {
      cleanAndAppend($$0, $$1, needsUnderscoreRemoval($$1));
   }

   static void cleanAndAppend(StringBuilder $$0, String $$1, boolean $$2) {
      if ($$2) {
         for(char $$3 : $$1.toCharArray()) {
            if ($$3 != '_') {
               $$0.append($$3);
            }
         }
      } else {
         $$0.append($$1);
      }
   }

   static short parseUnsignedShort(String $$0, int $$1) {
      int $$2 = Integer.parseInt($$0, $$1);
      if ($$2 >> 16 == 0) {
         return (short)$$2;
      } else {
         throw new NumberFormatException("out of range: " + $$2);
      }
   }

   @Nullable
   private static <T> T createFloat(
      DynamicOps<T> $$0,
      SnbtGrammar.Sign $$1,
      @Nullable String $$2,
      @Nullable String $$3,
      @Nullable SnbtGrammar.Signed<String> $$4,
      @Nullable SnbtGrammar.TypeSuffix $$5,
      ParseState<?> $$6
   ) {
      StringBuilder $$7 = new StringBuilder();
      $$1.append($$7);
      if ($$2 != null) {
         cleanAndAppend($$7, $$2);
      }

      if ($$3 != null) {
         $$7.append('.');
         cleanAndAppend($$7, $$3);
      }

      if ($$4 != null) {
         $$7.append('e');
         $$4.sign().append($$7);
         cleanAndAppend($$7, $$4.value);
      }

      try {
         String $$8 = $$7.toString();
         byte var10 = 0;

         return (T)(switch(SwitchBootstraps.enumSwitch<"enumSwitch","FLOAT","DOUBLE">($$5, var10)) {
            case -1 -> (Object)convertDouble($$0, $$6, $$8);
            case 0 -> (Object)convertFloat($$0, $$6, $$8);
            case 1 -> (Object)convertDouble($$0, $$6, $$8);
            default -> {
               $$6.errorCollector().store($$6.mark(), ERROR_EXPECTED_FLOAT_TYPE);
               yield null;
            }
         });
      } catch (NumberFormatException var11) {
         $$6.errorCollector().store($$6.mark(), createNumberParseError(var11));
         return null;
      }
   }

   @Nullable
   private static <T> T convertFloat(DynamicOps<T> $$0, ParseState<?> $$1, String $$2) {
      float $$3 = Float.parseFloat($$2);
      if (!Float.isFinite($$3)) {
         $$1.errorCollector().store($$1.mark(), ERROR_INFINITY_NOT_ALLOWED);
         return null;
      } else {
         return $$0.createFloat($$3);
      }
   }

   @Nullable
   private static <T> T convertDouble(DynamicOps<T> $$0, ParseState<?> $$1, String $$2) {
      double $$3 = Double.parseDouble($$2);
      if (!Double.isFinite($$3)) {
         $$1.errorCollector().store($$1.mark(), ERROR_INFINITY_NOT_ALLOWED);
         return null;
      } else {
         return $$0.createDouble($$3);
      }
   }

   private static String joinList(List<String> $$0) {
      return switch($$0.size()) {
         case 0 -> "";
         case 1 -> (String)$$0.getFirst();
         default -> String.join("", $$0);
      };
   }

   public static <T> Grammar<T> createParser(DynamicOps<T> $$0) {
      T $$1 = $$0.createBoolean(true);
      T $$2 = $$0.createBoolean(false);
      T $$3 = $$0.emptyMap();
      T $$4 = $$0.emptyList();
      Dictionary<StringReader> $$5 = new Dictionary<>();
      Atom<SnbtGrammar.Sign> $$6 = Atom.of("sign");
      $$5.put(
         $$6,
         Term.alternative(
            Term.sequence(StringReaderTerms.character('+'), Term.marker($$6, (T)SnbtGrammar.Sign.PLUS)),
            Term.sequence(StringReaderTerms.character('-'), Term.marker($$6, (T)SnbtGrammar.Sign.MINUS))
         ),
         $$1x -> (T)((SnbtGrammar.Sign)$$1x.getOrThrow($$6))
      );
      Atom<SnbtGrammar.IntegerSuffix> $$7 = Atom.of("integer_suffix");
      $$5.put(
         $$7,
         Term.alternative(
            Term.sequence(
               StringReaderTerms.characters('u', 'U'),
               Term.alternative(
                  Term.sequence(
                     StringReaderTerms.characters('b', 'B'),
                     Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.BYTE)))
                  ),
                  Term.sequence(
                     StringReaderTerms.characters('s', 'S'),
                     Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.SHORT)))
                  ),
                  Term.sequence(
                     StringReaderTerms.characters('i', 'I'),
                     Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.INT)))
                  ),
                  Term.sequence(
                     StringReaderTerms.characters('l', 'L'),
                     Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.LONG)))
                  )
               )
            ),
            Term.sequence(
               StringReaderTerms.characters('s', 'S'),
               Term.alternative(
                  Term.sequence(
                     StringReaderTerms.characters('b', 'B'),
                     Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.BYTE)))
                  ),
                  Term.sequence(
                     StringReaderTerms.characters('s', 'S'),
                     Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.SHORT)))
                  ),
                  Term.sequence(
                     StringReaderTerms.characters('i', 'I'),
                     Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.INT)))
                  ),
                  Term.sequence(
                     StringReaderTerms.characters('l', 'L'),
                     Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.LONG)))
                  )
               )
            ),
            Term.sequence(StringReaderTerms.characters('b', 'B'), Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(null, SnbtGrammar.TypeSuffix.BYTE)))),
            Term.sequence(StringReaderTerms.characters('s', 'S'), Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(null, SnbtGrammar.TypeSuffix.SHORT)))),
            Term.sequence(StringReaderTerms.characters('i', 'I'), Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(null, SnbtGrammar.TypeSuffix.INT)))),
            Term.sequence(StringReaderTerms.characters('l', 'L'), Term.marker($$7, (T)(new SnbtGrammar.IntegerSuffix(null, SnbtGrammar.TypeSuffix.LONG))))
         ),
         $$1x -> (T)((SnbtGrammar.IntegerSuffix)$$1x.getOrThrow($$7))
      );
      Atom<String> $$8 = Atom.of("binary_numeral");
      $$5.put($$8, BINARY_NUMERAL);
      Atom<String> $$9 = Atom.of("decimal_numeral");
      $$5.put($$9, DECIMAL_NUMERAL);
      Atom<String> $$10 = Atom.of("hex_numeral");
      $$5.put($$10, HEX_NUMERAL);
      Atom<SnbtGrammar.IntegerLiteral> $$11 = Atom.of("integer_literal");
      NamedRule<StringReader, SnbtGrammar.IntegerLiteral> $$12 = $$5.put(
         $$11,
         Term.sequence(
            Term.optional($$5.named($$6)),
            Term.alternative(
               Term.sequence(
                  StringReaderTerms.character('0'),
                  Term.cut(),
                  Term.alternative(
                     Term.sequence(StringReaderTerms.characters('x', 'X'), Term.cut(), $$5.named($$10)),
                     Term.sequence(StringReaderTerms.characters('b', 'B'), $$5.named($$8)),
                     Term.sequence($$5.named($$9), Term.cut(), Term.fail(ERROR_LEADING_ZERO_NOT_ALLOWED)),
                     Term.marker($$9, (T)"0")
                  )
               ),
               $$5.named($$9)
            ),
            Term.optional($$5.named($$7))
         ),
         $$5x -> {
            SnbtGrammar.IntegerSuffix $$6xx = $$5x.getOrDefault($$7, SnbtGrammar.IntegerSuffix.EMPTY);
            SnbtGrammar.Sign $$7xx = $$5x.getOrDefault($$6, SnbtGrammar.Sign.PLUS);
            String $$8xx = $$5x.get($$9);
            if ($$8xx != null) {
               return (T)(new SnbtGrammar.IntegerLiteral($$7xx, SnbtGrammar.Base.DECIMAL, $$8xx, $$6xx));
            } else {
               String $$9xx = $$5x.get($$10);
               if ($$9xx != null) {
                  return (T)(new SnbtGrammar.IntegerLiteral($$7xx, SnbtGrammar.Base.HEX, $$9xx, $$6xx));
               } else {
                  String $$10xx = $$5x.getOrThrow($$8);
                  return (T)(new SnbtGrammar.IntegerLiteral($$7xx, SnbtGrammar.Base.BINARY, $$10xx, $$6xx));
               }
            }
         }
      );
      Atom<SnbtGrammar.TypeSuffix> $$13 = Atom.of("float_type_suffix");
      $$5.put(
         $$13,
         Term.alternative(
            Term.sequence(StringReaderTerms.characters('f', 'F'), Term.marker($$13, (T)SnbtGrammar.TypeSuffix.FLOAT)),
            Term.sequence(StringReaderTerms.characters('d', 'D'), Term.marker($$13, (T)SnbtGrammar.TypeSuffix.DOUBLE))
         ),
         $$1x -> (T)((SnbtGrammar.TypeSuffix)$$1x.getOrThrow($$13))
      );
      Atom<SnbtGrammar.Signed<String>> $$14 = Atom.of("float_exponent_part");
      $$5.put(
         $$14,
         Term.sequence(StringReaderTerms.characters('e', 'E'), Term.optional($$5.named($$6)), $$5.named($$9)),
         $$2x -> new SnbtGrammar.Signed<>($$2x.getOrDefault($$6, SnbtGrammar.Sign.PLUS), (String)$$2x.getOrThrow($$9))
      );
      Atom<String> $$15 = Atom.of("float_whole_part");
      Atom<String> $$16 = Atom.of("float_fraction_part");
      Atom<T> $$17 = Atom.of("float_literal");
      $$5.putComplex(
         $$17,
         Term.sequence(
            Term.optional($$5.named($$6)),
            Term.alternative(
               Term.sequence(
                  $$5.namedWithAlias($$9, $$15),
                  StringReaderTerms.character('.'),
                  Term.cut(),
                  Term.optional($$5.namedWithAlias($$9, $$16)),
                  Term.optional($$5.named($$14)),
                  Term.optional($$5.named($$13))
               ),
               Term.sequence(
                  StringReaderTerms.character('.'), Term.cut(), $$5.namedWithAlias($$9, $$16), Term.optional($$5.named($$14)), Term.optional($$5.named($$13))
               ),
               Term.sequence($$5.namedWithAlias($$9, $$15), $$5.named($$14), Term.cut(), Term.optional($$5.named($$13))),
               Term.sequence($$5.namedWithAlias($$9, $$15), Term.optional($$5.named($$14)), $$5.named($$13))
            )
         ),
         $$6x -> {
            Scope $$7xx = $$6x.scope();
            SnbtGrammar.Sign $$8xx = $$7xx.getOrDefault($$6, SnbtGrammar.Sign.PLUS);
            String $$9xx = $$7xx.get($$15);
            String $$10xx = $$7xx.get($$16);
            SnbtGrammar.Signed<String> $$11xx = $$7xx.get($$14);
            SnbtGrammar.TypeSuffix $$12xx = $$7xx.get($$13);
            return createFloat($$0, $$8xx, $$9xx, $$10xx, $$11xx, $$12xx, $$6x);
         }
      );
      Atom<String> $$18 = Atom.of("string_hex_2");
      $$5.put($$18, new SnbtGrammar.SimpleHexLiteralParseRule(2));
      Atom<String> $$19 = Atom.of("string_hex_4");
      $$5.put($$19, new SnbtGrammar.SimpleHexLiteralParseRule(4));
      Atom<String> $$20 = Atom.of("string_hex_8");
      $$5.put($$20, new SnbtGrammar.SimpleHexLiteralParseRule(8));
      Atom<String> $$21 = Atom.of("string_unicode_name");
      $$5.put($$21, new GreedyPatternParseRule(UNICODE_NAME, ERROR_INVALID_CHARACTER_NAME));
      Atom<String> $$22 = Atom.of("string_escape_sequence");
      $$5.putComplex(
         $$22,
         Term.alternative(
            Term.sequence(StringReaderTerms.character('b'), Term.marker($$22, (T)"\b")),
            Term.sequence(StringReaderTerms.character('s'), Term.marker($$22, (T)" ")),
            Term.sequence(StringReaderTerms.character('t'), Term.marker($$22, (T)"\t")),
            Term.sequence(StringReaderTerms.character('n'), Term.marker($$22, (T)"\n")),
            Term.sequence(StringReaderTerms.character('f'), Term.marker($$22, (T)"\f")),
            Term.sequence(StringReaderTerms.character('r'), Term.marker($$22, (T)"\r")),
            Term.sequence(StringReaderTerms.character('\\'), Term.marker($$22, (T)"\\")),
            Term.sequence(StringReaderTerms.character('\''), Term.marker($$22, (T)"'")),
            Term.sequence(StringReaderTerms.character('"'), Term.marker($$22, (T)"\"")),
            Term.sequence(StringReaderTerms.character('x'), $$5.named($$18)),
            Term.sequence(StringReaderTerms.character('u'), $$5.named($$19)),
            Term.sequence(StringReaderTerms.character('U'), $$5.named($$20)),
            Term.sequence(StringReaderTerms.character('N'), StringReaderTerms.character('{'), $$5.named($$21), StringReaderTerms.character('}'))
         ),
         $$5x -> {
            Scope $$6xx = $$5x.scope();
            String $$7xx = $$6xx.getAny($$22);
            if ($$7xx != null) {
               return (T)$$7xx;
            } else {
               String $$8xx = $$6xx.getAny($$18, $$19, $$20);
               if ($$8xx != null) {
                  int $$9xx = HexFormat.fromHexDigits($$8xx);
                  if (!Character.isValidCodePoint($$9xx)) {
                     $$5x.errorCollector().store($$5x.mark(), DelayedException.create(ERROR_INVALID_CODEPOINT, String.format(Locale.ROOT, "U+%08X", $$9xx)));
                     return null;
                  } else {
                     return (T)Character.toString($$9xx);
                  }
               } else {
                  String $$10xx = $$6xx.getOrThrow($$21);
   
                  int $$11;
                  try {
                     $$11x = Character.codePointOf($$10xx);
                  } catch (IllegalArgumentException var12xx) {
                     $$5x.errorCollector().store($$5x.mark(), ERROR_INVALID_CHARACTER_NAME);
                     return null;
                  }
   
                  return (T)Character.toString($$11x);
               }
            }
         }
      );
      Atom<String> $$23 = Atom.of("string_plain_contents");
      $$5.put($$23, PLAIN_STRING_CHUNK);
      Atom<List<String>> $$24 = Atom.of("string_chunks");
      Atom<String> $$25 = Atom.of("string_contents");
      Atom<String> $$26 = Atom.of("single_quoted_string_chunk");
      NamedRule<StringReader, String> $$27 = $$5.put(
         $$26,
         Term.alternative(
            $$5.namedWithAlias($$23, $$25),
            Term.sequence(StringReaderTerms.character('\\'), $$5.namedWithAlias($$22, $$25)),
            Term.sequence(StringReaderTerms.character('"'), Term.marker($$25, (T)"\""))
         ),
         $$1x -> (T)((String)$$1x.getOrThrow($$25))
      );
      Atom<String> $$28 = Atom.of("single_quoted_string_contents");
      $$5.put($$28, Term.repeated($$27, $$24), $$1x -> (T)joinList($$1x.getOrThrow($$24)));
      Atom<String> $$29 = Atom.of("double_quoted_string_chunk");
      NamedRule<StringReader, String> $$30 = $$5.put(
         $$29,
         Term.alternative(
            $$5.namedWithAlias($$23, $$25),
            Term.sequence(StringReaderTerms.character('\\'), $$5.namedWithAlias($$22, $$25)),
            Term.sequence(StringReaderTerms.character('\''), Term.marker($$25, (T)"'"))
         ),
         $$1x -> (T)((String)$$1x.getOrThrow($$25))
      );
      Atom<String> $$31 = Atom.of("double_quoted_string_contents");
      $$5.put($$31, Term.repeated($$30, $$24), $$1x -> (T)joinList($$1x.getOrThrow($$24)));
      Atom<String> $$32 = Atom.of("quoted_string_literal");
      $$5.put(
         $$32,
         Term.alternative(
            Term.sequence(StringReaderTerms.character('"'), Term.cut(), Term.optional($$5.namedWithAlias($$31, $$25)), StringReaderTerms.character('"')),
            Term.sequence(StringReaderTerms.character('\''), Term.optional($$5.namedWithAlias($$28, $$25)), StringReaderTerms.character('\''))
         ),
         $$1x -> (T)((String)$$1x.getOrThrow($$25))
      );
      Atom<String> $$33 = Atom.of("unquoted_string");
      $$5.put($$33, new UnquotedStringParseRule(1, ERROR_EXPECTED_UNQUOTED_STRING));
      Atom<T> $$34 = Atom.of("literal");
      Atom<List<T>> $$35 = Atom.of("arguments");
      $$5.put($$35, Term.repeatedWithTrailingSeparator($$5.forward($$34), $$35, StringReaderTerms.character(',')), $$1x -> (T)((List)$$1x.getOrThrow($$35)));
      Atom<T> $$36 = Atom.of("unquoted_string_or_builtin");
      $$5.putComplex(
         $$36,
         Term.sequence($$5.named($$33), Term.optional(Term.sequence(StringReaderTerms.character('('), $$5.named($$35), StringReaderTerms.character(')')))),
         $$5x -> {
            Scope $$6xx = $$5x.scope();
            String $$7xx = $$6xx.getOrThrow($$33);
            if (!$$7xx.isEmpty() && isAllowedToStartUnquotedString($$7xx.charAt(0))) {
               List<T> $$8xx = $$6xx.get($$35);
               if ($$8xx != null) {
                  SnbtOperations.BuiltinKey $$9xx = new SnbtOperations.BuiltinKey($$7xx, $$8xx.size());
                  SnbtOperations.BuiltinOperation $$10xx = (SnbtOperations.BuiltinOperation)SnbtOperations.BUILTIN_OPERATIONS.get($$9xx);
                  if ($$10xx != null) {
                     return $$10xx.run($$0, $$8xx, $$5x);
                  } else {
                     $$5x.errorCollector().store($$5x.mark(), DelayedException.create(ERROR_NO_SUCH_OPERATION, $$9xx.toString()));
                     return null;
                  }
               } else if ($$7xx.equalsIgnoreCase("true")) {
                  return $$1;
               } else {
                  return (T)($$7xx.equalsIgnoreCase("false") ? $$2 : $$0.createString($$7xx));
               }
            } else {
               $$5x.errorCollector().store($$5x.mark(), SnbtOperations.BUILTIN_IDS, ERROR_INVALID_UNQUOTED_START);
               return null;
            }
         }
      );
      Atom<String> $$37 = Atom.of("map_key");
      $$5.put($$37, Term.alternative($$5.named($$32), $$5.named($$33)), $$2x -> (T)((String)$$2x.getAnyOrThrow($$32, $$33)));
      Atom<Entry<String, T>> $$38 = Atom.of("map_entry");
      NamedRule<StringReader, Entry<String, T>> $$39 = $$5.putComplex(
         $$38, Term.sequence($$5.named($$37), StringReaderTerms.character(':'), $$5.named($$34)), $$2x -> {
            Scope $$3xx = $$2x.scope();
            String $$4xx = $$3xx.getOrThrow($$37);
            if ($$4xx.isEmpty()) {
               $$2x.errorCollector().store($$2x.mark(), ERROR_EMPTY_KEY);
               return null;
            } else {
               T $$5xx = $$3xx.getOrThrow($$34);
               return (T)Map.entry($$4xx, $$5xx);
            }
         }
      );
      Atom<List<Entry<String, T>>> $$40 = Atom.of("map_entries");
      $$5.put($$40, Term.repeatedWithTrailingSeparator($$39, $$40, StringReaderTerms.character(',')), $$1x -> (T)((List)$$1x.getOrThrow($$40)));
      Atom<T> $$41 = Atom.of("map_literal");
      $$5.put($$41, Term.sequence(StringReaderTerms.character('{'), $$5.named($$40), StringReaderTerms.character('}')), $$3x -> {
         List<Entry<String, T>> $$4xx = $$3x.getOrThrow($$40);
         if ($$4xx.isEmpty()) {
            return $$3;
         } else {
            Builder<T, T> $$5xx = ImmutableMap.builderWithExpectedSize($$4xx.size());

            for(Entry<String, T> $$6xx : $$4xx) {
               $$5xx.put($$0.createString((String)$$6xx.getKey()), (T)$$6xx.getValue());
            }

            return $$0.createMap($$5xx.buildKeepingLast());
         }
      });
      Atom<List<T>> $$42 = Atom.of("list_entries");
      $$5.put($$42, Term.repeatedWithTrailingSeparator($$5.forward($$34), $$42, StringReaderTerms.character(',')), $$1x -> (T)((List)$$1x.getOrThrow($$42)));
      Atom<SnbtGrammar.ArrayPrefix> $$43 = Atom.of("array_prefix");
      $$5.put(
         $$43,
         Term.alternative(
            Term.sequence(StringReaderTerms.character('B'), Term.marker($$43, (T)SnbtGrammar.ArrayPrefix.BYTE)),
            Term.sequence(StringReaderTerms.character('L'), Term.marker($$43, (T)SnbtGrammar.ArrayPrefix.LONG)),
            Term.sequence(StringReaderTerms.character('I'), Term.marker($$43, (T)SnbtGrammar.ArrayPrefix.INT))
         ),
         $$1x -> (T)((SnbtGrammar.ArrayPrefix)$$1x.getOrThrow($$43))
      );
      Atom<List<SnbtGrammar.IntegerLiteral>> $$44 = Atom.of("int_array_entries");
      $$5.put($$44, Term.repeatedWithTrailingSeparator($$12, $$44, StringReaderTerms.character(',')), $$1x -> (T)((List)$$1x.getOrThrow($$44)));
      Atom<T> $$45 = Atom.of("list_literal");
      $$5.putComplex(
         $$45,
         Term.sequence(
            StringReaderTerms.character('['),
            Term.alternative(Term.sequence($$5.named($$43), StringReaderTerms.character(';'), $$5.named($$44)), $$5.named($$42)),
            StringReaderTerms.character(']')
         ),
         $$5x -> {
            Scope $$6xx = $$5x.scope();
            SnbtGrammar.ArrayPrefix $$7xx = $$6xx.get($$43);
            if ($$7xx != null) {
               List<SnbtGrammar.IntegerLiteral> $$8xx = $$6xx.getOrThrow($$44);
               return (T)($$8xx.isEmpty() ? $$7xx.create($$0) : $$7xx.create($$0, $$8xx, $$5x));
            } else {
               List<T> $$9xx = $$6xx.getOrThrow($$42);
               return (T)($$9xx.isEmpty() ? $$4 : $$0.createList($$9xx.stream()));
            }
         }
      );
      NamedRule<StringReader, T> $$46 = $$5.putComplex(
         $$34,
         Term.alternative(
            Term.sequence(Term.positiveLookahead(NUMBER_LOOKEAHEAD), Term.alternative($$5.namedWithAlias($$17, $$34), $$5.named($$11))),
            Term.sequence(Term.positiveLookahead(StringReaderTerms.characters('"', '\'')), Term.cut(), $$5.named($$32)),
            Term.sequence(Term.positiveLookahead(StringReaderTerms.character('{')), Term.cut(), $$5.namedWithAlias($$41, $$34)),
            Term.sequence(Term.positiveLookahead(StringReaderTerms.character('[')), Term.cut(), $$5.namedWithAlias($$45, $$34)),
            $$5.namedWithAlias($$36, $$34)
         ),
         $$4x -> {
            Scope $$5xx = $$4x.scope();
            String $$6xx = $$5xx.get($$32);
            if ($$6xx != null) {
               return $$0.createString($$6xx);
            } else {
               SnbtGrammar.IntegerLiteral $$7xx = $$5xx.get($$11);
               return (T)($$7xx != null ? $$7xx.create($$0, $$4x) : $$5xx.getOrThrow($$34));
            }
         }
      );
      return new Grammar<>($$5, $$46);
   }

   static enum ArrayPrefix {
      BYTE(SnbtGrammar.TypeSuffix.BYTE) {
         private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

         @Override
         public <T> T create(DynamicOps<T> $$0) {
            return $$0.createByteList(EMPTY_BUFFER);
         }

         @Nullable
         @Override
         public <T> T create(DynamicOps<T> $$0, List<SnbtGrammar.IntegerLiteral> $$1, ParseState<?> $$2) {
            ByteList $$3 = new ByteArrayList();

            for(SnbtGrammar.IntegerLiteral $$4 : $$1) {
               Number $$5 = this.buildNumber($$4, $$2);
               if ($$5 == null) {
                  return null;
               }

               $$3.add($$5.byteValue());
            }

            return $$0.createByteList(ByteBuffer.wrap($$3.toByteArray()));
         }
      },
      INT(SnbtGrammar.TypeSuffix.INT, SnbtGrammar.TypeSuffix.BYTE, SnbtGrammar.TypeSuffix.SHORT) {
         @Override
         public <T> T create(DynamicOps<T> $$0) {
            return $$0.createIntList(IntStream.empty());
         }

         @Nullable
         @Override
         public <T> T create(DynamicOps<T> $$0, List<SnbtGrammar.IntegerLiteral> $$1, ParseState<?> $$2) {
            java.util.stream.IntStream.Builder $$3 = IntStream.builder();

            for(SnbtGrammar.IntegerLiteral $$4 : $$1) {
               Number $$5 = this.buildNumber($$4, $$2);
               if ($$5 == null) {
                  return null;
               }

               $$3.add($$5.intValue());
            }

            return $$0.createIntList($$3.build());
         }
      },
      LONG(SnbtGrammar.TypeSuffix.LONG, SnbtGrammar.TypeSuffix.BYTE, SnbtGrammar.TypeSuffix.SHORT, SnbtGrammar.TypeSuffix.INT) {
         @Override
         public <T> T create(DynamicOps<T> $$0) {
            return $$0.createLongList(LongStream.empty());
         }

         @Nullable
         @Override
         public <T> T create(DynamicOps<T> $$0, List<SnbtGrammar.IntegerLiteral> $$1, ParseState<?> $$2) {
            java.util.stream.LongStream.Builder $$3 = LongStream.builder();

            for(SnbtGrammar.IntegerLiteral $$4 : $$1) {
               Number $$5 = this.buildNumber($$4, $$2);
               if ($$5 == null) {
                  return null;
               }

               $$3.add($$5.longValue());
            }

            return $$0.createLongList($$3.build());
         }
      };

      private final SnbtGrammar.TypeSuffix defaultType;
      private final Set<SnbtGrammar.TypeSuffix> additionalTypes;

      ArrayPrefix(final SnbtGrammar.TypeSuffix param3, final SnbtGrammar.TypeSuffix... param4) {
         this.additionalTypes = Set.of($$1);
         this.defaultType = $$0;
      }

      public boolean isAllowed(SnbtGrammar.TypeSuffix $$0) {
         return $$0 == this.defaultType || this.additionalTypes.contains($$0);
      }

      public abstract <T> T create(DynamicOps<T> var1);

      @Nullable
      public abstract <T> T create(DynamicOps<T> var1, List<SnbtGrammar.IntegerLiteral> var2, ParseState<?> var3);

      @Nullable
      protected Number buildNumber(SnbtGrammar.IntegerLiteral $$0, ParseState<?> $$1) {
         SnbtGrammar.TypeSuffix $$2 = this.computeType($$0.suffix);
         if ($$2 == null) {
            $$1.errorCollector().store($$1.mark(), SnbtGrammar.ERROR_INVALID_ARRAY_ELEMENT_TYPE);
            return null;
         } else {
            return $$0.create(JavaOps.INSTANCE, $$2, $$1);
         }
      }

      @Nullable
      private SnbtGrammar.TypeSuffix computeType(SnbtGrammar.IntegerSuffix $$0) {
         SnbtGrammar.TypeSuffix $$1 = $$0.type();
         if ($$1 == null) {
            return this.defaultType;
         } else {
            return !this.isAllowed($$1) ? null : $$1;
         }
      }
   }

   static enum Base {
      BINARY,
      DECIMAL,
      HEX;
   }

   static record IntegerLiteral(SnbtGrammar.Sign sign, SnbtGrammar.Base base, String digits, SnbtGrammar.IntegerSuffix suffix) {
      final SnbtGrammar.IntegerSuffix suffix;

      private SnbtGrammar.SignedPrefix signedOrDefault() {
         if (this.suffix.signed != null) {
            return this.suffix.signed;
         } else {
            return switch(this.base.ordinal()) {
               case 0, 2 -> SnbtGrammar.SignedPrefix.UNSIGNED;
               case 1 -> SnbtGrammar.SignedPrefix.SIGNED;
               default -> throw new MatchException(null, null);
            };
         }
      }

      private String cleanupDigits(SnbtGrammar.Sign $$0) {
         boolean $$1 = SnbtGrammar.needsUnderscoreRemoval(this.digits);
         if ($$0 != SnbtGrammar.Sign.MINUS && !$$1) {
            return this.digits;
         } else {
            StringBuilder $$2 = new StringBuilder();
            $$0.append($$2);
            SnbtGrammar.cleanAndAppend($$2, this.digits, $$1);
            return $$2.toString();
         }
      }

      @Nullable
      public <T> T create(DynamicOps<T> $$0, ParseState<?> $$1) {
         return this.create($$0, (SnbtGrammar.TypeSuffix)Objects.requireNonNullElse(this.suffix.type, SnbtGrammar.TypeSuffix.INT), $$1);
      }

      @Nullable
      public <T> T create(DynamicOps<T> $$0, SnbtGrammar.TypeSuffix $$1, ParseState<?> $$2) {
         boolean $$3 = this.signedOrDefault() == SnbtGrammar.SignedPrefix.SIGNED;
         if (!$$3 && this.sign == SnbtGrammar.Sign.MINUS) {
            $$2.errorCollector().store($$2.mark(), SnbtGrammar.ERROR_EXPECTED_NON_NEGATIVE_NUMBER);
            return null;
         } else {
            String $$4 = this.cleanupDigits(this.sign);

            int $$5 = switch(this.base.ordinal()) {
               case 0 -> 2;
               case 1 -> 10;
               case 2 -> 16;
               default -> throw new MatchException(null, null);
            };

            try {
               if ($$3) {
                  return (T)(switch($$1.ordinal()) {
                     case 2 -> (Object)$$0.createByte(Byte.parseByte($$4, $$5));
                     case 3 -> (Object)$$0.createShort(Short.parseShort($$4, $$5));
                     case 4 -> (Object)$$0.createInt(Integer.parseInt($$4, $$5));
                     case 5 -> (Object)$$0.createLong(Long.parseLong($$4, $$5));
                     default -> {
                        $$2.errorCollector().store($$2.mark(), SnbtGrammar.ERROR_EXPECTED_INTEGER_TYPE);
                        yield null;
                     }
                  });
               } else {
                  return (T)(switch($$1.ordinal()) {
                     case 2 -> (Object)$$0.createByte(UnsignedBytes.parseUnsignedByte($$4, $$5));
                     case 3 -> (Object)$$0.createShort(SnbtGrammar.parseUnsignedShort($$4, $$5));
                     case 4 -> (Object)$$0.createInt(Integer.parseUnsignedInt($$4, $$5));
                     case 5 -> (Object)$$0.createLong(Long.parseUnsignedLong($$4, $$5));
                     default -> {
                        $$2.errorCollector().store($$2.mark(), SnbtGrammar.ERROR_EXPECTED_INTEGER_TYPE);
                        yield null;
                     }
                  });
               }
            } catch (NumberFormatException var8) {
               $$2.errorCollector().store($$2.mark(), SnbtGrammar.createNumberParseError(var8));
               return null;
            }
         }
      }
   }

   static record IntegerSuffix(@Nullable SnbtGrammar.SignedPrefix signed, @Nullable SnbtGrammar.TypeSuffix type) {
      @Nullable
      final SnbtGrammar.SignedPrefix signed;
      @Nullable
      final SnbtGrammar.TypeSuffix type;
      public static final SnbtGrammar.IntegerSuffix EMPTY = new SnbtGrammar.IntegerSuffix(null, null);
   }

   static enum Sign {
      PLUS,
      MINUS;

      public void append(StringBuilder $$0) {
         if (this == MINUS) {
            $$0.append("-");
         }
      }
   }

   static record Signed<T>(SnbtGrammar.Sign sign, T value) {
      final T value;
   }

   static enum SignedPrefix {
      SIGNED,
      UNSIGNED;
   }

   static class SimpleHexLiteralParseRule extends GreedyPredicateParseRule {
      public SimpleHexLiteralParseRule(int $$0) {
         super($$0, $$0, DelayedException.create(SnbtGrammar.ERROR_EXPECTED_HEX_ESCAPE, String.valueOf($$0)));
      }

      @Override
      protected boolean isAccepted(char $$0) {
         return switch($$0) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f' -> true;
            default -> false;
         };
      }
   }

   static enum TypeSuffix {
      FLOAT,
      DOUBLE,
      BYTE,
      SHORT,
      INT,
      LONG;
   }
}
