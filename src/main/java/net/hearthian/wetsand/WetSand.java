package net.hearthian.wetsand;

import com.mojang.logging.LogUtils;
import net.hearthian.wetsand.events.Events;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WetSand.MOD_ID)
public class WetSand {
    public static final String MOD_ID = "wet_sand";
    public static final String RESOURCE_NAMESPACE = "wet-sand";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WetSand() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        modEventBus.addListener(ModBlocks::addCreative);

        MinecraftForge.EVENT_BUS.register(Events.class);
        LOGGER.info("Initializing Wet Sand for Forge 1.20.1...");
    }
}
