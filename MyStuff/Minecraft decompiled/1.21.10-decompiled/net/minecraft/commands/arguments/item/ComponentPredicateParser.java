package net.minecraft.commands.arguments.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.ResourceLocationParseRule;
import net.minecraft.util.parsing.packrat.commands.ResourceLookupRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.TagParseRule;

public class ComponentPredicateParser {
   public static <T, C, P> Grammar<List<T>> createGrammar(ComponentPredicateParser.Context<T, C, P> $$0) {
      Atom<List<T>> $$1 = Atom.of("top");
      Atom<Optional<T>> $$2 = Atom.of("type");
      Atom<Unit> $$3 = Atom.of("any_type");
      Atom<T> $$4 = Atom.of("element_type");
      Atom<T> $$5 = Atom.of("tag_type");
      Atom<List<T>> $$6 = Atom.of("conditions");
      Atom<List<T>> $$7 = Atom.of("alternatives");
      Atom<T> $$8 = Atom.of("term");
      Atom<T> $$9 = Atom.of("negation");
      Atom<T> $$10 = Atom.of("test");
      Atom<C> $$11 = Atom.of("component_type");
      Atom<P> $$12 = Atom.of("predicate_type");
      Atom<ResourceLocation> $$13 = Atom.of("id");
      Atom<Dynamic<?>> $$14 = Atom.of("tag");
      Dictionary<StringReader> $$15 = new Dictionary<>();
      NamedRule<StringReader, ResourceLocation> $$16 = $$15.put($$13, ResourceLocationParseRule.INSTANCE);
      NamedRule<StringReader, List<T>> $$17 = $$15.put(
         $$1,
         Term.alternative(
            Term.sequence($$15.named($$2), StringReaderTerms.character('['), Term.cut(), Term.optional($$15.named($$6)), StringReaderTerms.character(']')),
            $$15.named($$2)
         ),
         $$2x -> {
            Builder<T> $$3xx = ImmutableList.builder();
            ((Optional)$$2x.getOrThrow($$2)).ifPresent($$3xx::add);
            List<T> $$4xx = $$2x.get($$6);
            if ($$4xx != null) {
               $$3xx.addAll($$4xx);
            }
   
            return (T)$$3xx.build();
         }
      );
      $$15.put(
         $$2,
         Term.alternative($$15.named($$4), Term.sequence(StringReaderTerms.character('#'), Term.cut(), $$15.named($$5)), $$15.named($$3)),
         $$2x -> (T)Optional.ofNullable($$2x.getAny($$4, $$5))
      );
      $$15.put($$3, StringReaderTerms.character('*'), $$0x -> (T)Unit.INSTANCE);
      $$15.put($$4, new ComponentPredicateParser.ElementLookupRule<>($$16, $$0));
      $$15.put($$5, new ComponentPredicateParser.TagLookupRule<>($$16, $$0));
      $$15.put($$6, Term.sequence($$15.named($$7), Term.optional(Term.sequence(StringReaderTerms.character(','), $$15.named($$6)))), $$3x -> {
         T $$4xx = $$0.anyOf($$3x.getOrThrow($$7));
         return (T)((List)Optional.ofNullable((List)$$3x.get($$6)).map($$1xx -> Util.copyAndAdd($$4x, $$1xx)).orElse(List.of($$4xx)));
      });
      $$15.put($$7, Term.sequence($$15.named($$8), Term.optional(Term.sequence(StringReaderTerms.character('|'), $$15.named($$7)))), $$2x -> {
         T $$3xx = $$2x.getOrThrow($$8);
         return (T)((List)Optional.ofNullable((List)$$2x.get($$7)).map($$1xx -> Util.copyAndAdd($$3x, $$1xx)).orElse(List.of($$3xx)));
      });
      $$15.put(
         $$8, Term.alternative($$15.named($$10), Term.sequence(StringReaderTerms.character('!'), $$15.named($$9))), $$2x -> $$2x.getAnyOrThrow($$10, $$9)
      );
      $$15.put($$9, $$15.named($$10), $$2x -> $$0.negate($$2x.getOrThrow($$10)));
      $$15.putComplex(
         $$10,
         Term.alternative(
            Term.sequence($$15.named($$11), StringReaderTerms.character('='), Term.cut(), $$15.named($$14)),
            Term.sequence($$15.named($$12), StringReaderTerms.character('~'), Term.cut(), $$15.named($$14)),
            $$15.named($$11)
         ),
         $$4x -> {
            Scope $$5xx = $$4x.scope();
            P $$6xx = $$5xx.get($$12);
   
            try {
               if ($$6xx != null) {
                  Dynamic<?> $$7xx = $$5xx.getOrThrow($$14);
                  return $$0.createPredicateTest((ImmutableStringReader)$$4x.input(), $$6xx, $$7xx);
               } else {
                  C $$8xx = $$5xx.getOrThrow($$11);
                  Dynamic<?> $$9xx = $$5xx.get($$14);
                  return (T)($$9xx != null
                     ? $$0.createComponentTest((ImmutableStringReader)$$4x.input(), $$8xx, $$9xx)
                     : $$0.createComponentTest((ImmutableStringReader)$$4x.input(), $$8xx));
               }
            } catch (CommandSyntaxException var9xx) {
               $$4x.errorCollector().store($$4x.mark(), var9xx);
               return null;
            }
         }
      );
      $$15.put($$11, new ComponentPredicateParser.ComponentLookupRule<>($$16, $$0));
      $$15.put($$12, new ComponentPredicateParser.PredicateLookupRule<>($$16, $$0));
      $$15.put($$14, new TagParseRule<>(NbtOps.INSTANCE));
      return new Grammar($$15, $$17);
   }

   static class ComponentLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, C> {
      ComponentLookupRule(NamedRule<StringReader, ResourceLocation> $$0, ComponentPredicateParser.Context<T, C, P> $$1) {
         super($$0, $$1);
      }

      @Override
      protected C validateElement(ImmutableStringReader $$0, ResourceLocation $$1) throws Exception {
         return this.context.lookupComponentType($$0, $$1);
      }

      @Override
      public Stream<ResourceLocation> possibleResources() {
         return this.context.listComponentTypes();
      }
   }

   public interface Context<T, C, P> {
      T forElementType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException;

      Stream<ResourceLocation> listElementTypes();

      T forTagType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException;

      Stream<ResourceLocation> listTagTypes();

      C lookupComponentType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException;

      Stream<ResourceLocation> listComponentTypes();

      T createComponentTest(ImmutableStringReader var1, C var2, Dynamic<?> var3) throws CommandSyntaxException;

      T createComponentTest(ImmutableStringReader var1, C var2);

      P lookupPredicateType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException;

      Stream<ResourceLocation> listPredicateTypes();

      T createPredicateTest(ImmutableStringReader var1, P var2, Dynamic<?> var3) throws CommandSyntaxException;

      T negate(T var1);

      T anyOf(List<T> var1);
   }

   static class ElementLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, T> {
      ElementLookupRule(NamedRule<StringReader, ResourceLocation> $$0, ComponentPredicateParser.Context<T, C, P> $$1) {
         super($$0, $$1);
      }

      @Override
      protected T validateElement(ImmutableStringReader $$0, ResourceLocation $$1) throws Exception {
         return this.context.forElementType($$0, $$1);
      }

      @Override
      public Stream<ResourceLocation> possibleResources() {
         return this.context.listElementTypes();
      }
   }

   static class PredicateLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, P> {
      PredicateLookupRule(NamedRule<StringReader, ResourceLocation> $$0, ComponentPredicateParser.Context<T, C, P> $$1) {
         super($$0, $$1);
      }

      @Override
      protected P validateElement(ImmutableStringReader $$0, ResourceLocation $$1) throws Exception {
         return this.context.lookupPredicateType($$0, $$1);
      }

      @Override
      public Stream<ResourceLocation> possibleResources() {
         return this.context.listPredicateTypes();
      }
   }

   static class TagLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, T> {
      TagLookupRule(NamedRule<StringReader, ResourceLocation> $$0, ComponentPredicateParser.Context<T, C, P> $$1) {
         super($$0, $$1);
      }

      @Override
      protected T validateElement(ImmutableStringReader $$0, ResourceLocation $$1) throws Exception {
         return this.context.forTagType($$0, $$1);
      }

      @Override
      public Stream<ResourceLocation> possibleResources() {
         return this.context.listTagTypes();
      }
   }
}
