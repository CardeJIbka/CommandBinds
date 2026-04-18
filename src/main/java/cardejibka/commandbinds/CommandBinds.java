package cardejibka.commandbinds;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandBinds implements ModInitializer {

    public static final String MOD_ID = "commandbinds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[CommandBinds] Initialized.");
    }
}
