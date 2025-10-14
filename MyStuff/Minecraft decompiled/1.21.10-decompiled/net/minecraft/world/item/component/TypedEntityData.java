package net.minecraft.world.item.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public final class TypedEntityData<IdType> implements TooltipProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TYPE_TAG = "id";
   final IdType type;
   final CompoundTag tag;

   public static <T> Codec<TypedEntityData<T>> codec(final Codec<T> $$0) {
      return new Codec<TypedEntityData<T>>() {
         @Override
         public <V> DataResult<Pair<TypedEntityData<T>, V>> decode(DynamicOps<V> $$0x, V $$1) {
            DataResult<T> $$2 = $$0.get($$1, "id").flatMap($$2x -> $$0.parse($$0, (V)$$2x).mapError($$0xxxx -> "Failed to parse 'id': " + $$0xxxx));
            DataResult<Pair<CompoundTag, V>> $$3 = CustomData.COMPOUND_TAG_CODEC.decode($$0, $$0.remove($$1, "id"));
            return $$2.apply2stable(($$0xx, $$1x) -> new Pair<>(new TypedEntityData<>($$0xx, (CompoundTag)$$1x.getFirst()), $$1x.getSecond()), $$3);
         }

         public <V> DataResult<V> encode(TypedEntityData<T> $$0x, DynamicOps<V> $$1, V $$2) {
            return $$0.encodeStart(asNbtOps($$1), $$0.type).flatMap($$3 -> {
               CompoundTag $$4 = $$0.tag.copy();
               $$4.put("id", $$3);
               return CustomData.COMPOUND_TAG_CODEC.encode($$4, $$1, $$2);
            });
         }

         private static <T> DynamicOps<Tag> asNbtOps(DynamicOps<T> $$0x) {
            return (DynamicOps<Tag>)($$0 instanceof RegistryOps $$1 ? $$1.withParent(NbtOps.INSTANCE) : NbtOps.INSTANCE);
         }
      };
   }

   public static <B extends ByteBuf, T> StreamCodec<B, TypedEntityData<T>> streamCodec(StreamCodec<B, T> $$0) {
      return StreamCodec.composite($$0, TypedEntityData::type, ByteBufCodecs.COMPOUND_TAG, TypedEntityData::tag, TypedEntityData::new);
   }

   TypedEntityData(IdType $$0, CompoundTag $$1) {
      this.type = $$0;
      this.tag = stripId($$1);
   }

   public static <T> TypedEntityData<T> of(T $$0, CompoundTag $$1) {
      return new TypedEntityData<>($$0, $$1);
   }

   private static CompoundTag stripId(CompoundTag $$0) {
      if ($$0.contains("id")) {
         CompoundTag $$1 = $$0.copy();
         $$1.remove("id");
         return $$1;
      } else {
         return $$0;
      }
   }

   public IdType type() {
      return this.type;
   }

   public boolean contains(String $$0) {
      return this.tag.contains($$0);
   }

   public boolean equals(Object $$0) {
      if ($$0 == this) {
         return true;
      } else if (!($$0 instanceof TypedEntityData)) {
         return false;
      } else {
         TypedEntityData<?> $$1 = (TypedEntityData)$$0;
         return this.type == $$1.type && this.tag.equals($$1.tag);
      }
   }

   public int hashCode() {
      return 31 * this.type.hashCode() + this.tag.hashCode();
   }

   public String toString() {
      return this.type + " " + this.tag;
   }

   public void loadInto(Entity $$0) {
      try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector($$0.problemPath(), LOGGER)) {
         TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0.registryAccess());
         $$0.saveWithoutId($$2);
         CompoundTag $$3 = $$2.buildResult();
         UUID $$4 = $$0.getUUID();
         $$3.merge(this.getUnsafe());
         $$0.load(TagValueInput.create($$1, $$0.registryAccess(), $$3));
         $$0.setUUID($$4);
      }
   }

   public boolean loadInto(BlockEntity $$0, HolderLookup.Provider $$1) {
      boolean $$6;
      try (ProblemReporter.ScopedCollector $$2 = new ProblemReporter.ScopedCollector($$0.problemPath(), LOGGER)) {
         TagValueOutput $$3 = TagValueOutput.createWithContext($$2, $$1);
         $$0.saveCustomOnly($$3);
         CompoundTag $$4 = $$3.buildResult();
         CompoundTag $$5 = $$4.copy();
         $$4.merge(this.getUnsafe());
         if (!$$4.equals($$5)) {
            try {
               $$0.loadCustomOnly(TagValueInput.create($$2, $$1, $$4));
               $$0.setChanged();
               return true;
            } catch (Exception var11) {
               LOGGER.warn("Failed to apply custom data to block entity at {}", $$0.getBlockPos(), var11);

               try {
                  $$0.loadCustomOnly(TagValueInput.create($$2.forChild(() -> "(rollback)"), $$1, $$5));
               } catch (Exception var10) {
                  LOGGER.warn("Failed to rollback block entity at {} after failure", $$0.getBlockPos(), var10);
               }
            }
         }

         $$6 = false;
      }

      return $$6;
   }

   private CompoundTag tag() {
      return this.tag;
   }

   @Deprecated
   public CompoundTag getUnsafe() {
      return this.tag;
   }

   public CompoundTag copyTagWithoutId() {
      return this.tag.copy();
   }

   @Override
   public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
      if (this.type.getClass() == EntityType.class) {
         EntityType<?> $$4 = (EntityType)this.type;
         if ($$0.isPeaceful() && !$$4.isAllowedInPeaceful()) {
            $$1.accept(Component.translatable("item.spawn_egg.peaceful").withStyle(ChatFormatting.RED));
         }
      }
   }
}
