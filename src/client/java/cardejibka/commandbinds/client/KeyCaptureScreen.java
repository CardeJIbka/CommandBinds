package cardejibka.commandbinds.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.function.IntConsumer;

public class KeyCaptureScreen extends Screen {

    private final Screen parent;
    private final IntConsumer callback;

    public KeyCaptureScreen(Screen parent, IntConsumer callback) {
        super(Component.translatable("screen.commandbinds.capture_title"));
        this.parent   = parent;
        this.callback = callback;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xCC000000);
        graphics.drawCenteredString(minecraft.font, title,
                width / 2, height / 2 - 20, 0xFFFFFFFF);
        graphics.drawCenteredString(minecraft.font,
                Component.translatable("screen.commandbinds.capture_hint"),
                width / 2, height / 2, 0xFFAAAAAA);
    }

    @Override
    public void tick() {
        long handle = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
        for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_LAST; key++) {
            if (GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS) {
                if (key == GLFW.GLFW_KEY_ESCAPE) {
                    minecraft.setScreen(parent);
                    return;
                }
                callback.accept(key);
                minecraft.setScreen(parent);
                return;
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}