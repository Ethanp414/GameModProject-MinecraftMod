package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;

public class TagNetworkSerialization {
   public static Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> serializeTagsToNetwork(
      LayeredRegistryAccess<RegistryLayer> $$0
   ) {
      return (Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload>)RegistrySynchronization.networkSafeRegistries($$0)
         .map($$0x -> Pair.of($$0x.key(), serializeToNetwork($$0x.value())))
         .filter($$0x -> !((TagNetworkSerialization.NetworkPayload)$$0x.getSecond()).isEmpty())
         .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
   }

   private static <T> TagNetworkSerialization.NetworkPayload serializeToNetwork(Registry<T> $$0) {
      Map<ResourceLocation, IntList> $$1 = new HashMap();
      $$0.getTags().forEach($$2 -> {
         IntList $$3 = new IntArrayList($$2.size());

         for(Holder<T> $$4 : $$2) {
            if ($$4.kind() != Holder.Kind.REFERENCE) {
               throw new IllegalStateException("Can't serialize unregistered value " + $$4);
            }

            $$3.add($$0.getId($$4.value()));
         }

         $$1.put($$2.key().location(), $$3);
      });
      return new TagNetworkSerialization.NetworkPayload($$1);
   }

   static <T> TagLoader.LoadResult<T> deserializeTagsFromNetwork(Registry<T> $$0, TagNetworkSerialization.NetworkPayload $$1) {
      ResourceKey<? extends Registry<T>> $$2 = $$0.key();
      Map<TagKey<T>, List<Holder<T>>> $$3 = new HashMap();
      $$1.tags.forEach(($$3x, $$4) -> {
         TagKey<T> $$5 = TagKey.create($$2, $$3x);
         List<Holder<T>> $$6 = (List)$$4.intStream().mapToObj($$0::get).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
         $$3.put($$5, $$6);
      });
      return new TagLoader.LoadResult<>($$2, $$3);
   }

   public static final class NetworkPayload {
      public static final TagNetworkSerialization.NetworkPayload EMPTY = new TagNetworkSerialization.NetworkPayload(Map.of());
      final Map<ResourceLocation, IntList> tags;

      NetworkPayload(Map<ResourceLocation, IntList> $$0) {
         this.tags = $$0;
      }

      public void write(FriendlyByteBuf $$0) {
         $$0.writeMap(this.tags, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeIntIdList);
      }

      public static TagNetworkSerialization.NetworkPayload read(FriendlyByteBuf $$0) {
         return new TagNetworkSerialization.NetworkPayload($$0.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readIntIdList));
      }

      public boolean isEmpty() {
         return this.tags.isEmpty();
      }

      public int size() {
         return this.tags.size();
      }

      public <T> TagLoader.LoadResult<T> resolve(Registry<T> $$0) {
         return TagNetworkSerialization.deserializeTagsFromNetwork($$0, this);
      }
   }
}
