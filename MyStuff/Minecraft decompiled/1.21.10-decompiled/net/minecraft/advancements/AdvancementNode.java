package net.minecraft.advancements;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class AdvancementNode {
   private final AdvancementHolder holder;
   @Nullable
   private final AdvancementNode parent;
   private final Set<AdvancementNode> children = new ReferenceOpenHashSet<>();

   @VisibleForTesting
   public AdvancementNode(AdvancementHolder $$0, @Nullable AdvancementNode $$1) {
      this.holder = $$0;
      this.parent = $$1;
   }

   public Advancement advancement() {
      return this.holder.value();
   }

   public AdvancementHolder holder() {
      return this.holder;
   }

   @Nullable
   public AdvancementNode parent() {
      return this.parent;
   }

   public AdvancementNode root() {
      return getRoot(this);
   }

   public static AdvancementNode getRoot(AdvancementNode $$0) {
      AdvancementNode $$1 = $$0;

      while(true) {
         AdvancementNode $$2 = $$1.parent();
         if ($$2 == null) {
            return $$1;
         }

         $$1 = $$2;
      }
   }

   public Iterable<AdvancementNode> children() {
      return this.children;
   }

   @VisibleForTesting
   public void addChild(AdvancementNode $$0) {
      this.children.add($$0);
   }

   public boolean equals(Object $$0) {
      if (this == $$0) {
         return true;
      } else {
         if ($$0 instanceof AdvancementNode $$1 && this.holder.equals($$1.holder)) {
            return true;
         }

         return false;
      }
   }

   public int hashCode() {
      return this.holder.hashCode();
   }

   public String toString() {
      return this.holder.id().toString();
   }
}
