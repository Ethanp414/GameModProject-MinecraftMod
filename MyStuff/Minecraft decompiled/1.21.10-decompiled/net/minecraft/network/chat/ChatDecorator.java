package net.minecraft.network.chat;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface ChatDecorator {
   ChatDecorator PLAIN = ($$0, $$1) -> $$1;

   Component decorate(@Nullable ServerPlayer var1, Component var2);
}
