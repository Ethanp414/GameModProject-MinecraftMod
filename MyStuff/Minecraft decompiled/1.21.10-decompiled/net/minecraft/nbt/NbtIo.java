package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.util.DelegateDataOutput;
import net.minecraft.util.FastBufferedInputStream;

public class NbtIo {
   private static final OpenOption[] SYNC_OUTPUT_OPTIONS = new OpenOption[]{
      StandardOpenOption.SYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
   };

   public static CompoundTag readCompressed(Path $$0, NbtAccounter $$1) throws IOException {
      InputStream $$2 = Files.newInputStream($$0);

      CompoundTag var4;
      try {
         InputStream $$3 = new FastBufferedInputStream($$2);

         try {
            var4 = readCompressed($$3, $$1);
         } catch (Throwable var8) {
            try {
               $$3.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         $$3.close();
      } catch (Throwable var9) {
         if ($$2 != null) {
            try {
               $$2.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if ($$2 != null) {
         $$2.close();
      }

      return var4;
   }

   private static DataInputStream createDecompressorStream(InputStream $$0) throws IOException {
      return new DataInputStream(new FastBufferedInputStream(new GZIPInputStream($$0)));
   }

   private static DataOutputStream createCompressorStream(OutputStream $$0) throws IOException {
      return new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream($$0)));
   }

   public static CompoundTag readCompressed(InputStream $$0, NbtAccounter $$1) throws IOException {
      DataInputStream $$2 = createDecompressorStream($$0);

      CompoundTag var3;
      try {
         var3 = read($$2, $$1);
      } catch (Throwable var6) {
         if ($$2 != null) {
            try {
               $$2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if ($$2 != null) {
         $$2.close();
      }

      return var3;
   }

   public static void parseCompressed(Path $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
      InputStream $$3 = Files.newInputStream($$0);

      try {
         InputStream $$4 = new FastBufferedInputStream($$3);

         try {
            parseCompressed($$4, $$1, $$2);
         } catch (Throwable var9) {
            try {
               $$4.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         $$4.close();
      } catch (Throwable var10) {
         if ($$3 != null) {
            try {
               $$3.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if ($$3 != null) {
         $$3.close();
      }
   }

   public static void parseCompressed(InputStream $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
      DataInputStream $$3 = createDecompressorStream($$0);

      try {
         parse($$3, $$1, $$2);
      } catch (Throwable var7) {
         if ($$3 != null) {
            try {
               $$3.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if ($$3 != null) {
         $$3.close();
      }
   }

   public static void writeCompressed(CompoundTag $$0, Path $$1) throws IOException {
      OutputStream $$2 = Files.newOutputStream($$1, SYNC_OUTPUT_OPTIONS);

      try {
         OutputStream $$3 = new BufferedOutputStream($$2);

         try {
            writeCompressed($$0, $$3);
         } catch (Throwable var8) {
            try {
               $$3.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         $$3.close();
      } catch (Throwable var9) {
         if ($$2 != null) {
            try {
               $$2.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if ($$2 != null) {
         $$2.close();
      }
   }

   public static void writeCompressed(CompoundTag $$0, OutputStream $$1) throws IOException {
      DataOutputStream $$2 = createCompressorStream($$1);

      try {
         write($$0, $$2);
      } catch (Throwable var6) {
         if ($$2 != null) {
            try {
               $$2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if ($$2 != null) {
         $$2.close();
      }
   }

   public static void write(CompoundTag $$0, Path $$1) throws IOException {
      OutputStream $$2 = Files.newOutputStream($$1, SYNC_OUTPUT_OPTIONS);

      try {
         OutputStream $$3 = new BufferedOutputStream($$2);

         try {
            DataOutputStream $$4 = new DataOutputStream($$3);

            try {
               write($$0, $$4);
            } catch (Throwable var10) {
               try {
                  $$4.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            $$4.close();
         } catch (Throwable var11) {
            try {
               $$3.close();
            } catch (Throwable var8) {
               var11.addSuppressed(var8);
            }

            throw var11;
         }

         $$3.close();
      } catch (Throwable var12) {
         if ($$2 != null) {
            try {
               $$2.close();
            } catch (Throwable var7) {
               var12.addSuppressed(var7);
            }
         }

         throw var12;
      }

      if ($$2 != null) {
         $$2.close();
      }
   }

   @Nullable
   public static CompoundTag read(Path $$0) throws IOException {
      if (!Files.exists($$0, new LinkOption[0])) {
         return null;
      } else {
         InputStream $$1 = Files.newInputStream($$0);

         CompoundTag var3;
         try {
            DataInputStream $$2 = new DataInputStream($$1);

            try {
               var3 = read($$2, NbtAccounter.unlimitedHeap());
            } catch (Throwable var7) {
               try {
                  $$2.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            $$2.close();
         } catch (Throwable var8) {
            if ($$1 != null) {
               try {
                  $$1.close();
               } catch (Throwable var5) {
                  var8.addSuppressed(var5);
               }
            }

            throw var8;
         }

         if ($$1 != null) {
            $$1.close();
         }

         return var3;
      }
   }

   public static CompoundTag read(DataInput $$0) throws IOException {
      return read($$0, NbtAccounter.unlimitedHeap());
   }

   public static CompoundTag read(DataInput $$0, NbtAccounter $$1) throws IOException {
      Tag $$2 = readUnnamedTag($$0, $$1);
      if ($$2 instanceof CompoundTag) {
         return (CompoundTag)$$2;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(CompoundTag $$0, DataOutput $$1) throws IOException {
      writeUnnamedTagWithFallback($$0, $$1);
   }

   public static void parse(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
      TagType<?> $$3 = TagTypes.getType($$0.readByte());
      if ($$3 == EndTag.TYPE) {
         if ($$1.visitRootEntry(EndTag.TYPE) == StreamTagVisitor.ValueResult.CONTINUE) {
            $$1.visitEnd();
         }
      } else {
         switch($$1.visitRootEntry($$3)) {
            case HALT:
            default:
               break;
            case BREAK:
               StringTag.skipString($$0);
               $$3.skip($$0, $$2);
               break;
            case CONTINUE:
               StringTag.skipString($$0);
               $$3.parse($$0, $$1, $$2);
         }
      }
   }

   public static Tag readAnyTag(DataInput $$0, NbtAccounter $$1) throws IOException {
      byte $$2 = $$0.readByte();
      return (Tag)($$2 == 0 ? EndTag.INSTANCE : readTagSafe($$0, $$1, $$2));
   }

   public static void writeAnyTag(Tag $$0, DataOutput $$1) throws IOException {
      $$1.writeByte($$0.getId());
      if ($$0.getId() != 0) {
         $$0.write($$1);
      }
   }

   public static void writeUnnamedTag(Tag $$0, DataOutput $$1) throws IOException {
      $$1.writeByte($$0.getId());
      if ($$0.getId() != 0) {
         $$1.writeUTF("");
         $$0.write($$1);
      }
   }

   public static void writeUnnamedTagWithFallback(Tag $$0, DataOutput $$1) throws IOException {
      writeUnnamedTag($$0, new NbtIo.StringFallbackDataOutput($$1));
   }

   @VisibleForTesting
   public static Tag readUnnamedTag(DataInput $$0, NbtAccounter $$1) throws IOException {
      byte $$2 = $$0.readByte();
      if ($$2 == 0) {
         return EndTag.INSTANCE;
      } else {
         StringTag.skipString($$0);
         return readTagSafe($$0, $$1, $$2);
      }
   }

   private static Tag readTagSafe(DataInput $$0, NbtAccounter $$1, byte $$2) {
      try {
         return TagTypes.getType($$2).load($$0, $$1);
      } catch (IOException var6) {
         CrashReport $$4 = CrashReport.forThrowable(var6, "Loading NBT data");
         CrashReportCategory $$5 = $$4.addCategory("NBT Tag");
         $$5.setDetail("Tag type", $$2);
         throw new ReportedNbtException($$4);
      }
   }

   public static class StringFallbackDataOutput extends DelegateDataOutput {
      public StringFallbackDataOutput(DataOutput $$0) {
         super($$0);
      }

      @Override
      public void writeUTF(String $$0) throws IOException {
         try {
            super.writeUTF($$0);
         } catch (UTFDataFormatException var3) {
            Util.logAndPauseIfInIde("Failed to write NBT String", var3);
            super.writeUTF("");
         }
      }
   }
}
