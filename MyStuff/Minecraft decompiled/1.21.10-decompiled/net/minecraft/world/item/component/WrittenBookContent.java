package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public record WrittenBookContent(Filterable<String> title, String author, int generation, List<Filterable<Component>> pages, boolean resolved)
   implements BookContent<Component, WrittenBookContent>,
   TooltipProvider {
   public static final WrittenBookContent EMPTY = new WrittenBookContent(Filterable.passThrough(""), "", 0, List.of(), true);
   public static final int PAGE_LENGTH = 32767;
   public static final int TITLE_LENGTH = 16;
   public static final int TITLE_MAX_LENGTH = 32;
   public static final int MAX_GENERATION = 3;
   public static final int MAX_CRAFTABLE_GENERATION = 2;
   public static final Codec<Component> CONTENT_CODEC = ComponentSerialization.flatRestrictedCodec(32767);
   public static final Codec<List<Filterable<Component>>> PAGES_CODEC = pagesCodec(CONTENT_CODEC);
   public static final Codec<WrittenBookContent> CODEC = RecordCodecBuilder.create(
      $$0 -> $$0.group(
               Filterable.codec(Codec.string(0, 32)).fieldOf("title").forGetter(WrittenBookContent::title),
               Codec.STRING.fieldOf("author").forGetter(WrittenBookContent::author),
               ExtraCodecs.intRange(0, 3).optionalFieldOf("generation", 0).forGetter(WrittenBookContent::generation),
               PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WrittenBookContent::pages),
               Codec.BOOL.optionalFieldOf("resolved", Boolean.valueOf(false)).forGetter(WrittenBookContent::resolved)
            )
            .apply($$0, WrittenBookContent::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, WrittenBookContent> STREAM_CODEC = StreamCodec.composite(
      Filterable.streamCodec(ByteBufCodecs.stringUtf8(32)),
      WrittenBookContent::title,
      ByteBufCodecs.STRING_UTF8,
      WrittenBookContent::author,
      ByteBufCodecs.VAR_INT,
      WrittenBookContent::generation,
      Filterable.streamCodec(ComponentSerialization.STREAM_CODEC).apply(ByteBufCodecs.list()),
      WrittenBookContent::pages,
      ByteBufCodecs.BOOL,
      WrittenBookContent::resolved,
      WrittenBookContent::new
   );

   public WrittenBookContent(Filterable<String> param1, String param2, int param3, List<Filterable<Component>> param4, boolean param5) {
      if ($$2 >= 0 && $$2 <= 3) {
         this.title = $$0;
         this.author = $$1;
         this.generation = $$2;
         this.pages = $$3;
         this.resolved = $$4;
      } else {
         throw new IllegalArgumentException("Generation was " + $$2 + ", but must be between 0 and 3");
      }
   }

   private static Codec<Filterable<Component>> pageCodec(Codec<Component> $$0) {
      return Filterable.codec($$0);
   }

   public static Codec<List<Filterable<Component>>> pagesCodec(Codec<Component> $$0) {
      return pageCodec($$0).listOf();
   }

   @Nullable
   public WrittenBookContent tryCraftCopy() {
      return this.generation >= 2 ? null : new WrittenBookContent(this.title, this.author, this.generation + 1, this.pages, this.resolved);
   }

   public static boolean resolveForItem(ItemStack $$0, CommandSourceStack $$1, @Nullable Player $$2) {
      WrittenBookContent $$3 = $$0.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if ($$3 != null && !$$3.resolved()) {
         WrittenBookContent $$4 = $$3.resolve($$1, $$2);
         if ($$4 != null) {
            $$0.set(DataComponents.WRITTEN_BOOK_CONTENT, $$4);
            return true;
         }

         $$0.set(DataComponents.WRITTEN_BOOK_CONTENT, $$3.markResolved());
      }

      return false;
   }

   @Nullable
   public WrittenBookContent resolve(CommandSourceStack $$0, @Nullable Player $$1) {
      if (this.resolved) {
         return null;
      } else {
         Builder<Filterable<Component>> $$2 = ImmutableList.builderWithExpectedSize(this.pages.size());

         for(Filterable<Component> $$3 : this.pages) {
            Optional<Filterable<Component>> $$4 = resolvePage($$0, $$1, $$3);
            if ($$4.isEmpty()) {
               return null;
            }

            $$2.add((Filterable)$$4.get());
         }

         return new WrittenBookContent(this.title, this.author, this.generation, $$2.build(), true);
      }
   }

   public WrittenBookContent markResolved() {
      return new WrittenBookContent(this.title, this.author, this.generation, this.pages, true);
   }

   private static Optional<Filterable<Component>> resolvePage(CommandSourceStack $$0, @Nullable Player $$1, Filterable<Component> $$2) {
      return $$2.resolve($$2x -> {
         try {
            Component $$3 = ComponentUtils.updateForEntity($$0, $$2x, $$1, 0);
            return isPageTooLarge($$3, $$0.registryAccess()) ? Optional.empty() : Optional.of($$3);
         } catch (Exception var4) {
            return Optional.of($$2x);
         }
      });
   }

   private static boolean isPageTooLarge(Component $$0, HolderLookup.Provider $$1) {
      DataResult<JsonElement> $$2 = ComponentSerialization.CODEC.encodeStart($$1.createSerializationContext(JsonOps.INSTANCE), $$0);
      return $$2.isSuccess() && GsonHelper.encodesLongerThan($$2.getOrThrow(), 32767);
   }

   public List<Component> getPages(boolean $$0) {
      return Lists.transform(this.pages, $$1 -> (Component)$$1.get($$0));
   }

   public WrittenBookContent withReplacedPages(List<Filterable<Component>> $$0) {
      return new WrittenBookContent(this.title, this.author, this.generation, $$0, false);
   }

   @Override
   public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
      if (!StringUtil.isBlank(this.author)) {
         $$1.accept(Component.translatable("book.byAuthor", this.author).withStyle(ChatFormatting.GRAY));
      }

      $$1.accept(Component.translatable("book.generation." + this.generation).withStyle(ChatFormatting.GRAY));
   }
}
