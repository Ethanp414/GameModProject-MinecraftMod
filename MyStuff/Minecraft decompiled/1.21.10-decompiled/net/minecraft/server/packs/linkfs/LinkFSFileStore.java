package net.minecraft.server.packs.linkfs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import javax.annotation.Nullable;

class LinkFSFileStore extends FileStore {
   private final String name;

   public LinkFSFileStore(String $$0) {
      this.name = $$0;
   }

   public String name() {
      return this.name;
   }

   public String type() {
      return "index";
   }

   public boolean isReadOnly() {
      return true;
   }

   public long getTotalSpace() {
      return 0L;
   }

   public long getUsableSpace() {
      return 0L;
   }

   public long getUnallocatedSpace() {
      return 0L;
   }

   public boolean supportsFileAttributeView(Class<? extends FileAttributeView> $$0) {
      return $$0 == BasicFileAttributeView.class;
   }

   public boolean supportsFileAttributeView(String $$0) {
      return "basic".equals($$0);
   }

   @Nullable
   public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> $$0) {
      return null;
   }

   public Object getAttribute(String $$0) throws IOException {
      throw new UnsupportedOperationException();
   }
}
