package net.mirolls.bettertips;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterTips implements ModInitializer {
    public  static  final Logger LOGGER = LoggerFactory.getLogger(BetterTipsConfigs.MOD_ID);
    @Override
    public void onInitialize() {
        LOGGER.info("[BetterTips] BetterTips mod is loading");
    }
}
