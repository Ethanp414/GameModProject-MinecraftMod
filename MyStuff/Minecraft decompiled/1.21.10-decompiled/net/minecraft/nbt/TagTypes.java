package net.minecraft.nbt;

public class TagTypes {
   private static final TagType<?>[] TYPES = new TagType[]{
      EndTag.TYPE,
      ByteTag.TYPE,
      ShortTag.TYPE,
      IntTag.TYPE,
      LongTag.TYPE,
      FloatTag.TYPE,
      DoubleTag.TYPE,
      ByteArrayTag.TYPE,
      StringTag.TYPE,
      ListTag.TYPE,
      CompoundTag.TYPE,
      IntArrayTag.TYPE,
      LongArrayTag.TYPE
   };

   public static TagType<?> getType(int $$0) {
      return $$0 >= 0 && $$0 < TYPES.length ? TYPES[$$0] : TagType.createInvalid($$0);
   }
}
