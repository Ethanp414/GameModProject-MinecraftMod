package net.minecraft.resources;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class ResourceLocation implements Comparable<ResourceLocation> {
   public static final Codec<ResourceLocation> CODEC = Codec.STRING
      .<ResourceLocation>comapFlatMap(ResourceLocation::read, ResourceLocation::toString)
      .stable();
   public static final StreamCodec<ByteBuf, ResourceLocation> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
      .map(ResourceLocation::parse, ResourceLocation::toString);
   public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));
   public static final char NAMESPACE_SEPARATOR = ':';
   public static final String DEFAULT_NAMESPACE = "minecraft";
   public static final String REALMS_NAMESPACE = "realms";
   private final String namespace;
   private final String path;

   private ResourceLocation(String $$0, String $$1) {
      assert isValidNamespace($$0);

      assert isValidPath($$1);

      this.namespace = $$0;
      this.path = $$1;
   }

   private static ResourceLocation createUntrusted(String $$0, String $$1) {
      return new ResourceLocation(assertValidNamespace($$0, $$1), assertValidPath($$0, $$1));
   }

   public static ResourceLocation fromNamespaceAndPath(String $$0, String $$1) {
      return createUntrusted($$0, $$1);
   }

   public static ResourceLocation parse(String $$0) {
      return bySeparator($$0, ':');
   }

   public static ResourceLocation withDefaultNamespace(String $$0) {
      return new ResourceLocation("minecraft", assertValidPath("minecraft", $$0));
   }

   @Nullable
   public static ResourceLocation tryParse(String $$0) {
      return tryBySeparator($$0, ':');
   }

   @Nullable
   public static ResourceLocation tryBuild(String $$0, String $$1) {
      return isValidNamespace($$0) && isValidPath($$1) ? new ResourceLocation($$0, $$1) : null;
   }

   public static ResourceLocation bySeparator(String $$0, char $$1) {
      int $$2 = $$0.indexOf($$1);
      if ($$2 >= 0) {
         String $$3 = $$0.substring($$2 + 1);
         if ($$2 != 0) {
            String $$4 = $$0.substring(0, $$2);
            return createUntrusted($$4, $$3);
         } else {
            return withDefaultNamespace($$3);
         }
      } else {
         return withDefaultNamespace($$0);
      }
   }

   @Nullable
   public static ResourceLocation tryBySeparator(String $$0, char $$1) {
      int $$2 = $$0.indexOf($$1);
      if ($$2 >= 0) {
         String $$3 = $$0.substring($$2 + 1);
         if (!isValidPath($$3)) {
            return null;
         } else if ($$2 != 0) {
            String $$4 = $$0.substring(0, $$2);
            return isValidNamespace($$4) ? new ResourceLocation($$4, $$3) : null;
         } else {
            return new ResourceLocation("minecraft", $$3);
         }
      } else {
         return isValidPath($$0) ? new ResourceLocation("minecraft", $$0) : null;
      }
   }

   public static DataResult<ResourceLocation> read(String $$0) {
      try {
         return DataResult.success(parse($$0));
      } catch (ResourceLocationException var2) {
         return DataResult.error(() -> "Not a valid resource location: " + $$0 + " " + var2.getMessage());
      }
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public ResourceLocation withPath(String $$0) {
      return new ResourceLocation(this.namespace, assertValidPath(this.namespace, $$0));
   }

   public ResourceLocation withPath(UnaryOperator<String> $$0) {
      return this.withPath((String)$$0.apply(this.path));
   }

   public ResourceLocation withPrefix(String $$0) {
      return this.withPath($$0 + this.path);
   }

   public ResourceLocation withSuffix(String $$0) {
      return this.withPath(this.path + $$0);
   }

   public String toString() {
      return this.namespace + ":" + this.path;
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else if (!($$0 instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation $$1 = (ResourceLocation)$$0;
         return this.namespace.equals($$1.namespace) && this.path.equals($$1.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(ResourceLocation $$0) {
      int $$1 = this.path.compareTo($$0.path);
      if ($$1 == 0) {
         $$1 = this.namespace.compareTo($$0.namespace);
      }

      return $$1;
   }

   public String toDebugFileName() {
      return this.toString().replace('/', '_').replace(':', '_');
   }

   public String toLanguageKey() {
      return this.namespace + "." + this.path;
   }

   public String toShortLanguageKey() {
      return this.namespace.equals("minecraft") ? this.path : this.toLanguageKey();
   }

   public String toLanguageKey(String $$0) {
      return $$0 + "." + this.toLanguageKey();
   }

   public String toLanguageKey(String $$0, String $$1) {
      return $$0 + "." + this.toLanguageKey() + "." + $$1;
   }

   private static String readGreedy(StringReader $$0) {
      int $$1 = $$0.getCursor();

      while($$0.canRead() && isAllowedInResourceLocation($$0.peek())) {
         $$0.skip();
      }

      return $$0.getString().substring($$1, $$0.getCursor());
   }

   public static ResourceLocation read(StringReader $$0) throws CommandSyntaxException {
      int $$1 = $$0.getCursor();
      String $$2 = readGreedy($$0);

      try {
         return parse($$2);
      } catch (ResourceLocationException var4) {
         $$0.setCursor($$1);
         throw ERROR_INVALID.createWithContext($$0);
      }
   }

   public static ResourceLocation readNonEmpty(StringReader $$0) throws CommandSyntaxException {
      int $$1 = $$0.getCursor();
      String $$2 = readGreedy($$0);
      if ($$2.isEmpty()) {
         throw ERROR_INVALID.createWithContext($$0);
      } else {
         try {
            return parse($$2);
         } catch (ResourceLocationException var4) {
            $$0.setCursor($$1);
            throw ERROR_INVALID.createWithContext($$0);
         }
      }
   }

   public static boolean isAllowedInResourceLocation(char $$0) {
      return $$0 >= '0' && $$0 <= '9' || $$0 >= 'a' && $$0 <= 'z' || $$0 == '_' || $$0 == ':' || $$0 == '/' || $$0 == '.' || $$0 == '-';
   }

   public static boolean isValidPath(String $$0) {
      for(int $$1 = 0; $$1 < $$0.length(); ++$$1) {
         if (!validPathChar($$0.charAt($$1))) {
            return false;
         }
      }

      return true;
   }

   public static boolean isValidNamespace(String $$0) {
      for(int $$1 = 0; $$1 < $$0.length(); ++$$1) {
         if (!validNamespaceChar($$0.charAt($$1))) {
            return false;
         }
      }

      return true;
   }

   private static String assertValidNamespace(String $$0, String $$1) {
      if (!isValidNamespace($$0)) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + $$0 + ":" + $$1);
      } else {
         return $$0;
      }
   }

   public static boolean validPathChar(char $$0) {
      return $$0 == '_' || $$0 == '-' || $$0 >= 'a' && $$0 <= 'z' || $$0 >= '0' && $$0 <= '9' || $$0 == '/' || $$0 == '.';
   }

   private static boolean validNamespaceChar(char $$0) {
      return $$0 == '_' || $$0 == '-' || $$0 >= 'a' && $$0 <= 'z' || $$0 >= '0' && $$0 <= '9' || $$0 == '.';
   }

   private static String assertValidPath(String $$0, String $$1) {
      if (!isValidPath($$1)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + $$0 + ":" + $$1);
      } else {
         return $$1;
      }
   }
}
