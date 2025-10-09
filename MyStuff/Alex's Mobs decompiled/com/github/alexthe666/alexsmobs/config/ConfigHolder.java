package com.github.alexthe666.alexsmobs.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import org.apache.commons.lang3.tuple.Pair;

public final class ConfigHolder {
   public static final ForgeConfigSpec COMMON_SPEC;
   public static final CommonConfig COMMON;

   static {
      Pair<CommonConfig, ForgeConfigSpec> specPair = new Builder().configure(CommonConfig::new);
      COMMON = (CommonConfig)specPair.getLeft();
      COMMON_SPEC = (ForgeConfigSpec)specPair.getRight();
   }
}
