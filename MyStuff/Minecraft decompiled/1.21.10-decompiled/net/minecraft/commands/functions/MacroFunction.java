package net.minecraft.commands.functions;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.lang.runtime.SwitchBootstraps;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MacroFunction<T extends ExecutionCommandSource<T>> implements CommandFunction<T> {
   private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("#"), $$0 -> {
      $$0.setMaximumFractionDigits(15);
      $$0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
   });
   private static final int MAX_CACHE_ENTRIES = 8;
   private final List<String> parameters;
   private final Object2ObjectLinkedOpenHashMap<List<String>, InstantiatedFunction<T>> cache = new Object2ObjectLinkedOpenHashMap<>(8, 0.25F);
   private final ResourceLocation id;
   private final List<MacroFunction.Entry<T>> entries;

   public MacroFunction(ResourceLocation $$0, List<MacroFunction.Entry<T>> $$1, List<String> $$2) {
      this.id = $$0;
      this.entries = $$1;
      this.parameters = $$2;
   }

   @Override
   public ResourceLocation id() {
      return this.id;
   }

   @Override
   public InstantiatedFunction<T> instantiate(@Nullable CompoundTag $$0, CommandDispatcher<T> $$1) throws FunctionInstantiationException {
      if ($$0 == null) {
         throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_arguments", Component.translationArg(this.id())));
      } else {
         List<String> $$2 = new ArrayList(this.parameters.size());

         for(String $$3 : this.parameters) {
            Tag $$4 = $$0.get($$3);
            if ($$4 == null) {
               throw new FunctionInstantiationException(
                  Component.translatable("commands.function.error.missing_argument", Component.translationArg(this.id()), $$3)
               );
            }

            $$2.add(stringify($$4));
         }

         InstantiatedFunction<T> $$5 = this.cache.getAndMoveToLast($$2);
         if ($$5 != null) {
            return $$5;
         } else {
            if (this.cache.size() >= 8) {
               this.cache.removeFirst();
            }

            InstantiatedFunction<T> $$6 = this.substituteAndParse(this.parameters, $$2, $$1);
            this.cache.put($$2, $$6);
            return $$6;
         }
      }
   }

   // $VF: Inserted dummy exception handlers to handle obfuscated exceptions
   private static String stringify(Tag $$0) {
      Objects.requireNonNull($$0);
      byte var2 = 0;
      Throwable var29;
      switch(SwitchBootstraps.typeSwitch<"typeSwitch",FloatTag,DoubleTag,ByteTag,ShortTag,LongTag,StringTag>($$0, var2)) {
         case 0:
            FloatTag var3 = (FloatTag)$$0;
            FloatTag var40 = var3;

            try {
               var41 = var40.value();
            } catch (Throwable var23) {
               var29 = var23;
               boolean var46 = false;
               break;
            }

            float var24 = var41;
            return DECIMAL_FORMAT.format((double)var24);
         case 1:
            DoubleTag var5 = (DoubleTag)$$0;
            DoubleTag var38 = var5;

            try {
               var39 = var38.value();
            } catch (Throwable var22) {
               var29 = var22;
               boolean var45 = false;
               break;
            }

            double var25 = var39;
            return DECIMAL_FORMAT.format(var25);
         case 2:
            ByteTag var8 = (ByteTag)$$0;
            ByteTag var36 = var8;

            try {
               var37 = var36.value();
            } catch (Throwable var21) {
               var29 = var21;
               boolean var44 = false;
               break;
            }

            byte var26 = var37;
            return String.valueOf(var26);
         case 3:
            ShortTag var10 = (ShortTag)$$0;
            ShortTag var34 = var10;

            try {
               var35 = var34.value();
            } catch (Throwable var20) {
               var29 = var20;
               boolean var43 = false;
               break;
            }

            short var27 = var35;
            return String.valueOf(var27);
         case 4:
            LongTag var12 = (LongTag)$$0;
            LongTag var32 = var12;

            try {
               var33 = var32.value();
            } catch (Throwable var19) {
               var29 = var19;
               boolean var42 = false;
               break;
            }

            long var28 = var33;
            return String.valueOf(var28);
         case 5:
            StringTag var15 = (StringTag)$$0;
            StringTag var10000 = var15;

            try {
               var30 = var10000.value();
            } catch (Throwable var18) {
               var29 = var18;
               boolean var10001 = false;
               break;
            }

            return var30;
         default:
            return $$0.toString();
      }

      Throwable var1 = var29;
      throw new MatchException(var1.toString(), var1);
   }

   private static void lookupValues(List<String> $$0, IntList $$1, List<String> $$2) {
      $$2.clear();
      $$1.forEach($$2x -> $$2.add((String)$$0.get($$2x)));
   }

   private InstantiatedFunction<T> substituteAndParse(List<String> $$0, List<String> $$1, CommandDispatcher<T> $$2) throws FunctionInstantiationException {
      List<UnboundEntryAction<T>> $$3 = new ArrayList(this.entries.size());
      List<String> $$4 = new ArrayList($$1.size());

      for(MacroFunction.Entry<T> $$5 : this.entries) {
         lookupValues($$1, $$5.parameters(), $$4);
         $$3.add($$5.instantiate($$4, $$2, this.id));
      }

      return new PlainTextFunction<>(this.id().withPath((UnaryOperator<String>)($$1x -> $$1x + "/" + $$0.hashCode())), $$3);
   }

   interface Entry<T> {
      IntList parameters();

      UnboundEntryAction<T> instantiate(List<String> var1, CommandDispatcher<T> var2, ResourceLocation var3) throws FunctionInstantiationException;
   }

   static class MacroEntry<T extends ExecutionCommandSource<T>> implements MacroFunction.Entry<T> {
      private final StringTemplate template;
      private final IntList parameters;
      private final T compilationContext;

      public MacroEntry(StringTemplate $$0, IntList $$1, T $$2) {
         this.template = $$0;
         this.parameters = $$1;
         this.compilationContext = $$2;
      }

      @Override
      public IntList parameters() {
         return this.parameters;
      }

      @Override
      public UnboundEntryAction<T> instantiate(List<String> $$0, CommandDispatcher<T> $$1, ResourceLocation $$2) throws FunctionInstantiationException {
         String $$3 = this.template.substitute($$0);

         try {
            return CommandFunction.parseCommand($$1, this.compilationContext, new StringReader($$3));
         } catch (CommandSyntaxException var6) {
            throw new FunctionInstantiationException(
               Component.translatable("commands.function.error.parse", Component.translationArg($$2), $$3, var6.getMessage())
            );
         }
      }
   }

   static class PlainTextEntry<T> implements MacroFunction.Entry<T> {
      private final UnboundEntryAction<T> compiledAction;

      public PlainTextEntry(UnboundEntryAction<T> $$0) {
         this.compiledAction = $$0;
      }

      @Override
      public IntList parameters() {
         return IntLists.emptyList();
      }

      @Override
      public UnboundEntryAction<T> instantiate(List<String> $$0, CommandDispatcher<T> $$1, ResourceLocation $$2) {
         return this.compiledAction;
      }
   }
}
