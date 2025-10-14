package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.Mirror;

public class TemplateMirrorArgument extends StringRepresentableArgument<Mirror> {
   private TemplateMirrorArgument() {
      super(Mirror.CODEC, Mirror::values);
   }

   public static StringRepresentableArgument<Mirror> templateMirror() {
      return new TemplateMirrorArgument();
   }

   public static Mirror getMirror(CommandContext<CommandSourceStack> $$0, String $$1) {
      return $$0.getArgument($$1, Mirror.class);
   }
}
