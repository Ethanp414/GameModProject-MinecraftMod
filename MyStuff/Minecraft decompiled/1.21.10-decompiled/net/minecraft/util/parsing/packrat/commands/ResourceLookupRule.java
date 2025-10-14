package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public abstract class ResourceLookupRule<C, V> implements Rule<StringReader, V>, ResourceSuggestion {
   private final NamedRule<StringReader, ResourceLocation> idParser;
   protected final C context;
   private final DelayedException<CommandSyntaxException> error;

   protected ResourceLookupRule(NamedRule<StringReader, ResourceLocation> $$0, C $$1) {
      this.idParser = $$0;
      this.context = $$1;
      this.error = DelayedException.create(ResourceLocation.ERROR_INVALID);
   }

   @Nullable
   @Override
   public V parse(ParseState<StringReader> $$0) {
      $$0.input().skipWhitespace();
      int $$1 = $$0.mark();
      ResourceLocation $$2 = $$0.parse(this.idParser);
      if ($$2 != null) {
         try {
            return this.validateElement($$0.input(), $$2);
         } catch (Exception var5) {
            $$0.errorCollector().store($$1, this, var5);
            return null;
         }
      } else {
         $$0.errorCollector().store($$1, this, this.error);
         return null;
      }
   }

   protected abstract V validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception;
}
