package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface ProblemReporter {
   ProblemReporter DISCARDING = new ProblemReporter() {
      @Override
      public ProblemReporter forChild(ProblemReporter.PathElement $$0) {
         return this;
      }

      @Override
      public void report(ProblemReporter.Problem $$0) {
      }
   };

   ProblemReporter forChild(ProblemReporter.PathElement var1);

   void report(ProblemReporter.Problem var1);

   public static class Collector implements ProblemReporter {
      public static final ProblemReporter.PathElement EMPTY_ROOT = () -> "";
      @Nullable
      private final ProblemReporter.Collector parent;
      private final ProblemReporter.PathElement element;
      private final Set<ProblemReporter.Collector.Entry> problems;

      public Collector() {
         this(EMPTY_ROOT);
      }

      public Collector(ProblemReporter.PathElement $$0) {
         this.parent = null;
         this.problems = new LinkedHashSet();
         this.element = $$0;
      }

      private Collector(ProblemReporter.Collector $$0, ProblemReporter.PathElement $$1) {
         this.problems = $$0.problems;
         this.parent = $$0;
         this.element = $$1;
      }

      @Override
      public ProblemReporter forChild(ProblemReporter.PathElement $$0) {
         return new ProblemReporter.Collector(this, $$0);
      }

      @Override
      public void report(ProblemReporter.Problem $$0) {
         this.problems.add(new ProblemReporter.Collector.Entry(this, $$0));
      }

      public boolean isEmpty() {
         return this.problems.isEmpty();
      }

      public void forEach(BiConsumer<String, ProblemReporter.Problem> $$0) {
         List<ProblemReporter.PathElement> $$1 = new ArrayList();
         StringBuilder $$2 = new StringBuilder();

         for(ProblemReporter.Collector.Entry $$3 : this.problems) {
            for(ProblemReporter.Collector $$4 = $$3.source; $$4 != null; $$4 = $$4.parent) {
               $$1.add($$4.element);
            }

            for(int $$5 = $$1.size() - 1; $$5 >= 0; --$$5) {
               $$2.append(((ProblemReporter.PathElement)$$1.get($$5)).get());
            }

            $$0.accept($$2.toString(), $$3.problem());
            $$2.setLength(0);
            $$1.clear();
         }
      }

      public String getReport() {
         Multimap<String, ProblemReporter.Problem> $$0 = HashMultimap.create();
         this.forEach($$0::put);
         return (String)$$0.asMap()
            .entrySet()
            .stream()
            .map(
               $$0x -> " at "
                     + (String)$$0x.getKey()
                     + ": "
                     + (String)((Collection)$$0x.getValue()).stream().map(ProblemReporter.Problem::description).collect(Collectors.joining("; "))
            )
            .collect(Collectors.joining("\n"));
      }

      public String getTreeReport() {
         List<ProblemReporter.PathElement> $$0 = new ArrayList();
         ProblemReporter.Collector.ProblemTreeNode $$1 = new ProblemReporter.Collector.ProblemTreeNode(this.element);

         for(ProblemReporter.Collector.Entry $$2 : this.problems) {
            for(ProblemReporter.Collector $$3 = $$2.source; $$3 != this; $$3 = $$3.parent) {
               $$0.add($$3.element);
            }

            ProblemReporter.Collector.ProblemTreeNode $$4 = $$1;

            for(int $$5 = $$0.size() - 1; $$5 >= 0; --$$5) {
               $$4 = $$4.child((ProblemReporter.PathElement)$$0.get($$5));
            }

            $$0.clear();
            $$4.problems.add($$2.problem);
         }

         return String.join("\n", $$1.getLines());
      }

      static record Entry(ProblemReporter.Collector source, ProblemReporter.Problem problem) {
         final ProblemReporter.Collector source;
         final ProblemReporter.Problem problem;
      }

      static record ProblemTreeNode(
         ProblemReporter.PathElement element,
         List<ProblemReporter.Problem> problems,
         Map<ProblemReporter.PathElement, ProblemReporter.Collector.ProblemTreeNode> children
      ) {
         final List<ProblemReporter.Problem> problems;

         public ProblemTreeNode(ProblemReporter.PathElement $$0) {
            this($$0, new ArrayList(), new LinkedHashMap());
         }

         public ProblemReporter.Collector.ProblemTreeNode child(ProblemReporter.PathElement $$0) {
            return (ProblemReporter.Collector.ProblemTreeNode)this.children.computeIfAbsent($$0, ProblemReporter.Collector.ProblemTreeNode::new);
         }

         public List<String> getLines() {
            int $$0 = this.problems.size();
            int $$1 = this.children.size();
            if ($$0 == 0 && $$1 == 0) {
               return List.of();
            } else if ($$0 == 0 && $$1 == 1) {
               List<String> $$2 = new ArrayList();
               this.children.forEach(($$1x, $$2) -> $$2.addAll($$2.getLines()));
               $$2.set(0, this.element.get() + (String)$$2.get(0));
               return $$2;
            } else if ($$0 == 1 && $$1 == 0) {
               return List.of(this.element.get() + ": " + ((ProblemReporter.Problem)this.problems.getFirst()).description());
            } else {
               List<String> $$3 = new ArrayList();
               this.children.forEach(($$1x, $$2) -> $$3.addAll($$2.getLines()));
               $$3.replaceAll($$0x -> "  " + $$0x);

               for(ProblemReporter.Problem $$4 : this.problems) {
                  $$3.add("  " + $$4.description());
               }

               $$3.addFirst(this.element.get() + ":");
               return $$3;
            }
         }
      }
   }

   public static record ElementReferencePathElement(ResourceKey<?> id) implements ProblemReporter.PathElement {
      @Override
      public String get() {
         return "->{" + this.id.location() + "@" + this.id.registry() + "}";
      }
   }

   public static record FieldPathElement(String name) implements ProblemReporter.PathElement {
      @Override
      public String get() {
         return "." + this.name;
      }
   }

   public static record IndexedFieldPathElement(String name, int index) implements ProblemReporter.PathElement {
      @Override
      public String get() {
         return "." + this.name + "[" + this.index + "]";
      }
   }

   public static record IndexedPathElement(int index) implements ProblemReporter.PathElement {
      @Override
      public String get() {
         return "[" + this.index + "]";
      }
   }

   @FunctionalInterface
   public interface PathElement {
      String get();
   }

   public interface Problem {
      String description();
   }

   public static record RootElementPathElement(ResourceKey<?> id) implements ProblemReporter.PathElement {
      @Override
      public String get() {
         return "{" + this.id.location() + "@" + this.id.registry() + "}";
      }
   }

   public static record RootFieldPathElement(String name) implements ProblemReporter.PathElement {
      @Override
      public String get() {
         return this.name;
      }
   }

   public static class ScopedCollector extends ProblemReporter.Collector implements AutoCloseable {
      private final Logger logger;

      public ScopedCollector(Logger $$0) {
         this.logger = $$0;
      }

      public ScopedCollector(ProblemReporter.PathElement $$0, Logger $$1) {
         super($$0);
         this.logger = $$1;
      }

      public void close() {
         if (!this.isEmpty()) {
            this.logger.warn("[{}] Serialization errors:\n{}", this.logger.getName(), this.getTreeReport());
         }
      }
   }
}
