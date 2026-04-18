package cardejibka.commandbinds.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ModernButton extends Button {

    private boolean toggled = false;
    private final boolean isToggleButton;

    public ModernButton(int x, int y, int width, int height,
                        Component message, OnPress onPress) {
        this(x, y, width, height, message, onPress, false);
    }

    public ModernButton(int x, int y, int width, int height,
                        Component message, OnPress onPress, boolean isToggle) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.isToggleButton = isToggle;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean isToggled() {
        return toggled;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;

        Minecraft mc = Minecraft.getInstance();
        boolean hovered = this.isHovered();

        int topColor, bottomColor;
        if (isToggleButton && toggled) {
            topColor    = hovered ? 0xFF4A7A4A : 0xFF3A6A3A;
            bottomColor = hovered ? 0xFF3A6A3A : 0xFF2A5A2A;
        } else {
            topColor    = hovered ? 0xFF5A5A5A : 0xFF3A3A3A;
            bottomColor = hovered ? 0xFF4A4A4A : 0xFF2A2A2A;
        }

        graphics.fillGradient(getX(), getY(), getX() + width, getY() + height,
                topColor, bottomColor);

        int borderColor = (isToggleButton && toggled) ? 0xFF88FF88 : 0xFF666666;
        graphics.fill(getX(),           getY(),            getX() + width, getY() + 1,      borderColor);
        graphics.fill(getX(),           getY(),            getX() + 1,     getY() + height, borderColor);
        graphics.fill(getX() + width-1, getY(),            getX() + width, getY() + height, borderColor);
        graphics.fill(getX(),           getY() + height-1, getX() + width, getY() + height, borderColor);

        int textColor = (isToggleButton && toggled) ? 0xFFCCFFCC : 0xFFFFFFFF;
        if (hovered) textColor = 0xFFFFFFDD;

        int textX = getX() + (width  - mc.font.width(getMessage())) / 2;
        int textY = getY() + (height - 8) / 2;

        graphics.drawString(mc.font, getMessage(), textX, textY, textColor);
    }
}
