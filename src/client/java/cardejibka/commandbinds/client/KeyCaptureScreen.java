package cardejibka.commandbinds.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xCC000000);

        graphics.centeredText(minecraft.font, title, width / 2, height / 2 - 20, 0xFFFFFFFF);
        graphics.centeredText(minecraft.font,
                Component.translatable("screen.commandbinds.capture_hint"),
                width / 2, height / 2, 0xFFAAAAAA);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.key();

        if (key == InputConstants.KEY_ESCAPE) {
            minecraft.gui.setScreen(parent);
            return true;
        }

        callback.accept(key);
        minecraft.gui.setScreen(parent);
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}