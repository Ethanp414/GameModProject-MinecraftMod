package net.minecraft.nbt.visitors;

import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public interface SkipAll extends StreamTagVisitor {
   SkipAll INSTANCE = new SkipAll() {
   };

   @Override
   default StreamTagVisitor.ValueResult visitEnd() {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(String $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(byte $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(short $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(int $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(long $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(float $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(double $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(byte[] $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(int[] $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visit(long[] $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visitList(TagType<?> $$0, int $$1) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.EntryResult visitElement(TagType<?> $$0, int $$1) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   @Override
   default StreamTagVisitor.EntryResult visitEntry(TagType<?> $$0) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   @Override
   default StreamTagVisitor.EntryResult visitEntry(TagType<?> $$0, String $$1) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   @Override
   default StreamTagVisitor.ValueResult visitContainerEnd() {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   @Override
   default StreamTagVisitor.ValueResult visitRootEntry(TagType<?> $$0) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }
}
