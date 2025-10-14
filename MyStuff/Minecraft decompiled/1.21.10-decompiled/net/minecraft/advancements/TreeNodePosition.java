package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class TreeNodePosition {
   private final AdvancementNode node;
   @Nullable
   private final TreeNodePosition parent;
   @Nullable
   private final TreeNodePosition previousSibling;
   private final int childIndex;
   private final List<TreeNodePosition> children = Lists.<TreeNodePosition>newArrayList();
   private TreeNodePosition ancestor;
   @Nullable
   private TreeNodePosition thread;
   private int x;
   private float y;
   private float mod;
   private float change;
   private float shift;

   public TreeNodePosition(AdvancementNode $$0, @Nullable TreeNodePosition $$1, @Nullable TreeNodePosition $$2, int $$3, int $$4) {
      if ($$0.advancement().display().isEmpty()) {
         throw new IllegalArgumentException("Can't position an invisible advancement!");
      } else {
         this.node = $$0;
         this.parent = $$1;
         this.previousSibling = $$2;
         this.childIndex = $$3;
         this.ancestor = this;
         this.x = $$4;
         this.y = -1.0F;
         TreeNodePosition $$5 = null;

         for(AdvancementNode $$6 : $$0.children()) {
            $$5 = this.addChild($$6, $$5);
         }
      }
   }

   @Nullable
   private TreeNodePosition addChild(AdvancementNode $$0, @Nullable TreeNodePosition $$1) {
      if ($$0.advancement().display().isPresent()) {
         $$1 = new TreeNodePosition($$0, this, $$1, this.children.size() + 1, this.x + 1);
         this.children.add($$1);
      } else {
         for(AdvancementNode $$2 : $$0.children()) {
            $$1 = this.addChild($$2, $$1);
         }
      }

      return $$1;
   }

   private void firstWalk() {
      if (this.children.isEmpty()) {
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
         } else {
            this.y = 0.0F;
         }
      } else {
         TreeNodePosition $$0 = null;

         for(TreeNodePosition $$1 : this.children) {
            $$1.firstWalk();
            $$0 = $$1.apportion($$0 == null ? $$1 : $$0);
         }

         this.executeShifts();
         float $$2 = (((TreeNodePosition)this.children.get(0)).y + ((TreeNodePosition)this.children.get(this.children.size() - 1)).y) / 2.0F;
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
            this.mod = this.y - $$2;
         } else {
            this.y = $$2;
         }
      }
   }

   private float secondWalk(float $$0, int $$1, float $$2) {
      this.y += $$0;
      this.x = $$1;
      if (this.y < $$2) {
         $$2 = this.y;
      }

      for(TreeNodePosition $$3 : this.children) {
         $$2 = $$3.secondWalk($$0 + this.mod, $$1 + 1, $$2);
      }

      return $$2;
   }

   private void thirdWalk(float $$0) {
      this.y += $$0;

      for(TreeNodePosition $$1 : this.children) {
         $$1.thirdWalk($$0);
      }
   }

   private void executeShifts() {
      float $$0 = 0.0F;
      float $$1 = 0.0F;

      for(int $$2 = this.children.size() - 1; $$2 >= 0; --$$2) {
         TreeNodePosition $$3 = (TreeNodePosition)this.children.get($$2);
         $$3.y += $$0;
         $$3.mod += $$0;
         $$1 += $$3.change;
         $$0 += $$3.shift + $$1;
      }
   }

   @Nullable
   private TreeNodePosition previousOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? (TreeNodePosition)this.children.get(0) : null;
      }
   }

   @Nullable
   private TreeNodePosition nextOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? (TreeNodePosition)this.children.get(this.children.size() - 1) : null;
      }
   }

   private TreeNodePosition apportion(TreeNodePosition $$0) {
      if (this.previousSibling == null) {
         return $$0;
      } else {
         TreeNodePosition $$1 = this;
         TreeNodePosition $$2 = this;
         TreeNodePosition $$3 = this.previousSibling;
         TreeNodePosition $$4 = (TreeNodePosition)this.parent.children.get(0);
         float $$5 = this.mod;
         float $$6 = this.mod;
         float $$7 = $$3.mod;

         float $$8;
         for($$8 = $$4.mod; $$3.nextOrThread() != null && $$1.previousOrThread() != null; $$6 += $$2.mod) {
            $$3 = $$3.nextOrThread();
            $$1 = $$1.previousOrThread();
            $$4 = $$4.previousOrThread();
            $$2 = $$2.nextOrThread();
            $$2.ancestor = this;
            float $$9 = $$3.y + $$7 - ($$1.y + $$5) + 1.0F;
            if ($$9 > 0.0F) {
               $$3.getAncestor(this, $$0).moveSubtree(this, $$9);
               $$5 += $$9;
               $$6 += $$9;
            }

            $$7 += $$3.mod;
            $$5 += $$1.mod;
            $$8 += $$4.mod;
         }

         if ($$3.nextOrThread() != null && $$2.nextOrThread() == null) {
            $$2.thread = $$3.nextOrThread();
            $$2.mod += $$7 - $$6;
         } else {
            if ($$1.previousOrThread() != null && $$4.previousOrThread() == null) {
               $$4.thread = $$1.previousOrThread();
               $$4.mod += $$5 - $$8;
            }

            $$0 = this;
         }

         return $$0;
      }
   }

   private void moveSubtree(TreeNodePosition $$0, float $$1) {
      float $$2 = (float)($$0.childIndex - this.childIndex);
      if ($$2 != 0.0F) {
         $$0.change -= $$1 / $$2;
         this.change += $$1 / $$2;
      }

      $$0.shift += $$1;
      $$0.y += $$1;
      $$0.mod += $$1;
   }

   private TreeNodePosition getAncestor(TreeNodePosition $$0, TreeNodePosition $$1) {
      return this.ancestor != null && $$0.parent.children.contains(this.ancestor) ? this.ancestor : $$1;
   }

   private void finalizePosition() {
      this.node.advancement().display().ifPresent($$0x -> $$0x.setLocation((float)this.x, this.y));
      if (!this.children.isEmpty()) {
         for(TreeNodePosition $$0 : this.children) {
            $$0.finalizePosition();
         }
      }
   }

   public static void run(AdvancementNode $$0) {
      if ($$0.advancement().display().isEmpty()) {
         throw new IllegalArgumentException("Can't position children of an invisible root!");
      } else {
         TreeNodePosition $$1 = new TreeNodePosition($$0, null, null, 1, 0);
         $$1.firstWalk();
         float $$2 = $$1.secondWalk(0.0F, 0, $$1.y);
         if ($$2 < 0.0F) {
            $$1.thirdWalk(-$$2);
         }

         $$1.finalizePosition();
      }
   }
}
