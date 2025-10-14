package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class TextComponentHoverAndClickEventFix extends DataFix {
   public TextComponentHoverAndClickEventFix(Schema $$0) {
      super($$0, true);
   }

   @Override
   protected TypeRewriteRule makeRule() {
      Type<? extends Pair<String, ?>> $$0 = this.getInputSchema().getType(References.TEXT_COMPONENT).findFieldType("hoverEvent");
      return this.createFixer(this.getInputSchema().getTypeRaw(References.TEXT_COMPONENT), this.getOutputSchema().getType(References.TEXT_COMPONENT), $$0);
   }

   private <C1, C2, H extends Pair<String, ?>> TypeRewriteRule createFixer(Type<C1> $$0, Type<C2> $$1, Type<H> $$2) {
      Type<Pair<String, Either<Either<String, List<C1>>, Pair<Either<List<C1>, Unit>, Pair<Either<C1, Unit>, Pair<Either<H, Unit>, Dynamic<?>>>>>>> $$3 = DSL.named(
         References.TEXT_COMPONENT.typeName(),
         DSL.or(
            DSL.or(DSL.string(), DSL.list($$0)),
            DSL.and(
               DSL.optional(DSL.field("extra", DSL.list($$0))),
               DSL.optional(DSL.field("separator", $$0)),
               DSL.optional(DSL.field("hoverEvent", $$2)),
               DSL.remainderType()
            )
         )
      );
      if (!$$3.equals(this.getInputSchema().getType(References.TEXT_COMPONENT))) {
         throw new IllegalStateException(
            "Text component type did not match, expected " + $$3 + " but got " + this.getInputSchema().getType(References.TEXT_COMPONENT)
         );
      } else {
         Type<?> $$4 = ExtraDataFixUtils.patchSubType($$3, $$3, $$1);
         return this.fixTypeEverywhere(
            "TextComponentHoverAndClickEventFix",
            $$3,
            $$1,
            $$2x -> $$3x -> {
                  boolean $$4xx = ((Either)$$3x.getSecond()).map($$0xxx -> false, $$0xxx -> {
                     Pair<Either<H, Unit>, Dynamic<?>> $$1xxxx = (Pair)((Pair)$$0xxx.getSecond()).getSecond();
                     boolean $$2xxxx = $$1xxxx.getFirst().left().isPresent();
                     boolean $$3xxx = $$1xxxx.getSecond().get("clickEvent").result().isPresent();
                     return $$2xxxx || $$3xxx;
                  });
                  return !$$4xx
                     ? $$3x
                     : Util.writeAndReadTypedOrThrow(ExtraDataFixUtils.cast($$4, $$3x, $$2x), $$1, TextComponentHoverAndClickEventFix::fixTextComponent)
                        .getValue();
               }
         );
      }
   }

   private static Dynamic<?> fixTextComponent(Dynamic<?> $$0) {
      return $$0.renameAndFixField("hoverEvent", "hover_event", TextComponentHoverAndClickEventFix::fixHoverEvent)
         .renameAndFixField("clickEvent", "click_event", TextComponentHoverAndClickEventFix::fixClickEvent);
   }

   private static Dynamic<?> copyFields(Dynamic<?> $$0, Dynamic<?> $$1, String... $$2) {
      for(String $$3 : $$2) {
         $$0 = Dynamic.copyField($$1, $$3, $$0, $$3);
      }

      return $$0;
   }

   private static Dynamic<?> fixHoverEvent(Dynamic<?> $$0) {
      String $$1 = $$0.get("action").asString("");

      return switch($$1) {
         case "show_text" -> $$0.renameField("contents", "value");
         case "show_item" -> {
            Dynamic<?> $$2 = $$0.get("contents").orElseEmptyMap();
            Optional<String> $$3 = $$2.asString().result();
            yield $$3.isPresent() ? $$0.renameField("contents", "id") : copyFields($$0.remove("contents"), $$2, "id", "count", "components");
         }
         case "show_entity" -> {
            Dynamic<?> $$4 = $$0.get("contents").orElseEmptyMap();
            yield copyFields($$0.remove("contents"), $$4, "id", "type", "name").renameField("id", "uuid").renameField("type", "id");
         }
         default -> $$0;
      };
   }

   @Nullable
   private static <T> Dynamic<T> fixClickEvent(Dynamic<T> $$0) {
      String $$1 = $$0.get("action").asString("");
      String $$2 = $$0.get("value").asString("");
      Dynamic var10000;
      switch($$1) {
         case "open_url":
            var10000 = !validateUri($$2) ? null : $$0.renameField("value", "url");
            break;
         case "open_file":
            var10000 = $$0.renameField("value", "path");
            break;
         case "run_command":
         case "suggest_command":
            var10000 = !validateChat($$2) ? null : $$0.renameField("value", "command");
            break;
         case "change_page":
            Integer $$3 = (Integer)$$0.get("value").result().map(TextComponentHoverAndClickEventFix::parseOldPage).orElse(null);
            if ($$3 == null) {
               var10000 = null;
            } else {
               int $$4 = Math.max($$3, 1);
               var10000 = $$0.remove("value").set("page", $$0.createInt($$4));
            }
            break;
         default:
            var10000 = $$0;
      }

      return var10000;
   }

   @Nullable
   private static Integer parseOldPage(Dynamic<?> $$0) {
      Optional<Number> $$1 = $$0.asNumber().result();
      if ($$1.isPresent()) {
         return ((Number)$$1.get()).intValue();
      } else {
         try {
            return Integer.parseInt($$0.asString(""));
         } catch (Exception var3) {
            return null;
         }
      }
   }

   private static boolean validateUri(String $$0) {
      try {
         URI $$1 = new URI($$0);
         String $$2 = $$1.getScheme();
         if ($$2 == null) {
            return false;
         } else {
            String $$3 = $$2.toLowerCase(Locale.ROOT);
            return "http".equals($$3) || "https".equals($$3);
         }
      } catch (URISyntaxException var4) {
         return false;
      }
   }

   private static boolean validateChat(String $$0) {
      for(int $$1 = 0; $$1 < $$0.length(); ++$$1) {
         char $$2 = $$0.charAt($$1);
         if ($$2 == 167 || $$2 < ' ' || $$2 == 127) {
            return false;
         }
      }

      return true;
   }
}
