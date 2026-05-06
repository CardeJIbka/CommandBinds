package cardejibka.commandbinds.client;

import cardejibka.commandbinds.CommandBind;
import cardejibka.commandbinds.CommandBinds;
import cardejibka.commandbinds.CommandBindsConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class CommandBindsClient implements ClientModInitializer {

    private static KeyMapping openMenuKey;

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(
                    ResourceLocation.fromNamespaceAndPath("commandbinds", "commandbinds")
            );

    @Override
    public void onInitializeClient() {
        CommandBindsConfig.load();
        CommandBinds.LOGGER.info("[CommandBinds] Client initialized, {} bind(s) loaded.",
                CommandBindsConfig.getBinds().size());

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.commandbinds.openmenu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMenuKey.consumeClick()) {
                client.setScreen(new CommandBindsScreen());
            }

            if (client.screen != null || client.player == null) return;

            List<CommandBind> binds = CommandBindsConfig.getBinds();
            for (CommandBind bind : binds) {
                boolean pressed = InputConstants.isKeyDown(
                        client.getWindow(), bind.keyCode());

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