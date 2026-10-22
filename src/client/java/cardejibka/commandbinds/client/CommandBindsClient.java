package cardejibka.commandbinds.client;

import cardejibka.commandbinds.CommandBind;
import cardejibka.commandbinds.CommandBinds;
import cardejibka.commandbinds.CommandBindsConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

import java.util.List;

public class CommandBindsClient implements ClientModInitializer {

    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        CommandBindsConfig.load();
        CommandBinds.LOGGER.info("[CommandBinds] Client initialized, {} bind(s) loaded.",
                CommandBindsConfig.getBinds().size());

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.commandbinds.openmenu",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_V,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMenuKey.consumeClick()) {
                client.gui.setScreen(new CommandBindsScreen());
            }

            if (client.gui.screen() != null || client.player == null) return;

            List<CommandBind> binds = CommandBindsConfig.getBinds();
            for (CommandBind bind : binds) {
                boolean pressed = InputConstants.isKeyDown(bind.keyCode());

                if (pressed) {
                    if (KeyPressTracker.risingEdge(bind.keyCode())) {
                        sendBind(client, bind);
                    }
                } else {
                    KeyPressTracker.release(bind.keyCode());
                }
            }
        });
    }

    private static void sendBind(net.minecraft.client.Minecraft client, CommandBind bind) {
        if (client.player == null || client.getConnection() == null) return;
        String text = bind.text().trim();
        if (text.isEmpty()) return;

        if (text.startsWith("/")) {
            client.getConnection().sendCommand(text.substring(1));
        } else {
            client.getConnection().sendChat(text);
        }
    }
}