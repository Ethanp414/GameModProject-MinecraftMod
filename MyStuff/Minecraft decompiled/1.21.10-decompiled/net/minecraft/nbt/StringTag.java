package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public record StringTag(String value) implements PrimitiveTag {
   private static final int SELF_SIZE_IN_BYTES = 36;
   public static final TagType<StringTag> TYPE = new TagType.VariableSize<StringTag>() {
      public StringTag load(DataInput $$0, NbtAccounter $$1) throws IOException {
         return StringTag.valueOf(readAccounted($$0, $$1));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput $$0, StreamTagVisitor $$1, NbtAccounter $$2) throws IOException {
         return $$1.visit(readAccounted($$0, $$2));
      }

      private static String readAccounted(DataInput $$0, NbtAccounter $$1) throws IOException {
         $$1.accountBytes(36L);
         String $$2 = $$0.readUTF();
         $$1.accountBytes(2L, (long)$$2.length());
         return $$2;
      }

      @Override
      public void skip(DataInput $$0, NbtAccounter $$1) throws IOException {
         StringTag.skipString($$0);
      }

      @Override
      public String getName() {
         return "STRING";
      }

      @Override
      public String getPrettyName() {
         return "TAG_String";
      }
   };
   private static final StringTag EMPTY = new StringTag("");
   private static final char DOUBLE_QUOTE = '"';
   private static final char SINGLE_QUOTE = '\'';
   private static final char ESCAPE = '\\';
   private static final char NOT_SET = '\u0000';

   @Deprecated(
      forRemoval = true
   )
   public StringTag(String param1) {
      this.value = $$0;
   }

   public static void skipString(DataInput $$0) throws IOException {
      $$0.skipBytes($$0.readUnsignedShort());
   }

   public static StringTag valueOf(String $$0) {
      return $$0.isEmpty() ? EMPTY : new StringTag($$0);
   }

   @Override
   public void write(DataOutput $$0) throws IOException {
      $$0.writeUTF(this.value);
   }

   @Override
   public int sizeInBytes() {
      return 36 + 2 * this.value.length();
   }

   @Override
   public byte getId() {
      return 8;
   }

   @Override
   public TagType<StringTag> getType() {
      return TYPE;
   }

   @Override
   public String toString() {
      StringTagVisitor $$0 = new StringTagVisitor();
      $$0.visitString(this);
      return $$0.build();
   }

   public StringTag copy() {
      return this;
   }

   @Override
   public Optional<String> asString() {
      return Optional.of(this.value);
   }

   @Override
   public void accept(TagVisitor $$0) {
      $$0.visitString(this);
   }

   public static String quoteAndEscape(String $$0) {
      StringBuilder $$1 = new StringBuilder();
      quoteAndEscape($$0, $$1);
      return $$1.toString();
   }

   public static void quoteAndEscape(String $$0, StringBuilder $$1) {
      int $$2 = $$1.length();
      $$1.append(' ');
      char $$3 = 0;

      for(int $$4 = 0; $$4 < $$0.length(); ++$$4) {
         char $$5 = $$0.charAt($$4);
         if ($$5 == '\\') {
            $$1.append("\\\\");
         } else if ($$5 != '"' && $$5 != '\'') {
            String $$6 = SnbtGrammar.escapeControlCharacters($$5);
            if ($$6 != null) {
               $$1.append('\\');
               $$1.append($$6);
            } else {
               $$1.append($$5);
            }
         } else {
            if ($$3 == 0) {
               $$3 = (char)($$5 == '"' ? 39 : 34);
            }

            if ($$3 == $$5) {
               $$1.append('\\');
            }

            $$1.append($$5);
         }
      }

      if ($$3 == 0) {
         $$3 = '"';
      }

      $$1.setCharAt($$2, $$3);
      $$1.append($$3);
   }

   public static String escapeWithoutQuotes(String $$0) {
      StringBuilder $$1 = new StringBuilder();
      escapeWithoutQuotes($$0, $$1);
      return $$1.toString();
   }

   public static void escapeWithoutQuotes(String $$0, StringBuilder $$1) {
      for(int $$2 = 0; $$2 < $$0.length(); ++$$2) {
         char $$3 = $$0.charAt($$2);
         switch($$3) {
            case '"':
            case '\'':
            case '\\':
               $$1.append('\\');
               $$1.append($$3);
               break;
            default:
               String $$4 = SnbtGrammar.escapeControlCharacters($$3);
               if ($$4 != null) {
                  $$1.append('\\');
                  $$1.append($$4);
               } else {
                  $$1.append($$3);
               }
         }
      }
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor $$0) {
      return $$0.visit(this.value);
   }
}
