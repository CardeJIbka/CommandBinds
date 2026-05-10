package cardejibka.commandbinds.client;

import cardejibka.commandbinds.CommandBind;
import cardejibka.commandbinds.CommandBindsConfig;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CommandBindsScreen extends Screen {

    private static final int PANEL_WIDTH  = 500;
    private static final int PANEL_MARGIN = 30;
    private static final int LIST_START_Y = 110;
    private static final int LIST_PADDING = 4;
    private static final int ITEM_HEIGHT  = 32;

    private EditBox      textField;
    private ModernButton bindKeyButton;
    private ModernButton addButton;

    private final List<ModernButton> editButtons   = new ArrayList<>();
    private final List<ModernButton> deleteButtons = new ArrayList<>();

    private int    pendingKeyCode = -1;
    private double scrollOffset   = 0.0;
    private int    totalContentHeight;

    private String savedText = "";

    public CommandBindsScreen() {
        super(Component.translatable("screen.commandbinds.title"));
    }

    @Override
    protected void init() {
        clearWidgets();
        editButtons.clear();
        deleteButtons.clear();
        scrollOffset = 0;

        int cx     = width / 2;
        int panelL = cx - PANEL_WIDTH / 2;

        textField = new EditBox(
                minecraft.font,
                panelL + 10, 60,
                PANEL_WIDTH - 240, 20,
                Component.translatable("screen.commandbinds.text_hint")
        );
        textField.setMaxLength(256);
        textField.setHint(Component.translatable("screen.commandbinds.text_hint"));
        textField.setValue(savedText);
        addRenderableWidget(textField);

        bindKeyButton = new ModernButton(
                panelL + PANEL_WIDTH - 220, 60, 120, 20,
                keyButtonLabel(),
                btn -> {
                    savedText = textField.getValue();
                    minecraft.setScreen(new KeyCaptureScreen(this, code -> {
                        pendingKeyCode = code;
                    }));
                }
        );
        addRenderableWidget(bindKeyButton);

        addButton = new ModernButton(
                panelL + PANEL_WIDTH - 90, 60, 80, 20,
                Component.translatable("screen.commandbinds.add"),
                btn -> tryAddBind()
        );
        addRenderableWidget(addButton);

        addRenderableWidget(new ModernButton(
                cx - 60, height - 38, 120, 22,
                Component.translatable("screen.commandbinds.close"),
                btn -> {
                    CommandBindsConfig.save();
                    minecraft.setScreen(null);
                }
        ));

        refreshList();
    }

    public void refreshList() {
        editButtons.clear();
        deleteButtons.clear();

        int panelL = (width - PANEL_WIDTH) / 2;
        List<CommandBind> binds = CommandBindsConfig.getBinds();

        for (int i = 0; i < binds.size(); i++) {
            CommandBind bind = binds.get(i);
            int rowY = LIST_START_Y + i * ITEM_HEIGHT;

            final int idx = i;
            ModernButton editBtn = new ModernButton(
                    panelL + 10, rowY,
                    PANEL_WIDTH - 90, ITEM_HEIGHT - 4,
                    buildRowLabel(bind),
                    btn -> openEditDialog(idx)
            );
            editButtons.add(editBtn);

            ModernButton delBtn = new ModernButton(
                    panelL + PANEL_WIDTH - 72, rowY,
                    58, ITEM_HEIGHT - 4,
                    Component.translatable("screen.commandbinds.delete")
                            .copy()
                            .withStyle(s -> s.withColor(0xFF5555)),
                    btn -> {
                        CommandBindsConfig.removeBind(idx);
                        refreshList();
                    }
            );
            deleteButtons.add(delBtn);
        }

        totalContentHeight = binds.size() * ITEM_HEIGHT;
    }

    private Component buildRowLabel(CommandBind bind) {
        String keyName = InputConstants.Type.KEYSYM.getOrCreate(bind.keyCode())
                .getDisplayName().getString();
        String icon    = bind.isCommand() ? "§a/§r" : "§7T§r";
        return Component.literal(
                String.format("§e[%s]§r  %s  §f%s", keyName, icon, bind.displayText())
        );
    }

    private Component keyButtonLabel() {
        if (pendingKeyCode < 0) {
            return Component.translatable("screen.commandbinds.pick_key");
        }
        String name = InputConstants.Type.KEYSYM.getOrCreate(pendingKeyCode)
                .getDisplayName().getString();
        return Component.literal("§a" + name);
    }

    private void tryAddBind() {
        String text = textField.getValue().trim();
        if (text.isEmpty() || pendingKeyCode < 0) return;

        CommandBindsConfig.removeByKey(pendingKeyCode);

        String label = text.length() > 30 ? text.substring(0, 27) + "..." : text;
        CommandBindsConfig.addBind(new CommandBind(pendingKeyCode, text, label));

        textField.setValue("");
        savedText = "";
        pendingKeyCode = -1;
        bindKeyButton.setMessage(keyButtonLabel());
        refreshList();
    }

    private void openEditDialog(int index) {
        List<CommandBind> binds = CommandBindsConfig.getBinds();
        if (index < 0 || index >= binds.size()) return;

        CommandBind bind = binds.get(index);
        CommandBindsConfig.removeBind(index);

        pendingKeyCode = bind.keyCode();
        savedText = bind.text();
        textField.setValue(savedText);
        bindKeyButton.setMessage(keyButtonLabel());
        refreshList();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0x88000000);

        int cx     = width / 2;
        int panelL = cx - PANEL_WIDTH / 2;
        int panelR = cx + PANEL_WIDTH / 2;

        graphics.centeredText(minecraft.font, title, cx, PANEL_MARGIN, 0xFFFFFFFF);
        graphics.text(minecraft.font,
                Component.translatable("screen.commandbinds.input_label"),
                panelL + 10, 48, 0xFFAAAAAA);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int clipTop    = LIST_START_Y - LIST_PADDING;
        int clipBottom = height - 50;

        graphics.fill(panelL + 4, clipTop, panelR - 4, clipBottom, 0xA0111111);

        int ol = panelL + 4, ot = clipTop, or_ = panelR - 4, ob = clipBottom;
        int oc = 0x60FFFFFF;
        graphics.fill(ol,      ot,     or_,  ot + 1, oc);
        graphics.fill(ol,      ob - 1, or_,  ob,     oc);
        graphics.fill(ol,      ot,     ol + 1, ob,   oc);
        graphics.fill(or_ - 1, ot,     or_,  ob,     oc);

        if (editButtons.isEmpty()) {
            graphics.centeredText(minecraft.font,
                    Component.translatable("screen.commandbinds.empty"),
                    cx, clipTop + (clipBottom - clipTop) / 2 - 4, 0xFF666666);
        }

        graphics.enableScissor(panelL + 4, clipTop, panelR - 4, clipBottom);

        int scroll         = (int) scrollOffset;
        int size           = editButtons.size();
        int adjustedMouseY = (mouseY >= clipTop && mouseY <= clipBottom)
                ? (int)(mouseY + scrollOffset) : -9999;

        for (int i = 0; i < size; i++) {
            ModernButton eBtn = editButtons.get(i);
            ModernButton dBtn = deleteButtons.get(i);

            int screenY = eBtn.getY() - scroll;
            if (screenY + ITEM_HEIGHT < clipTop || screenY > clipBottom) continue;

            CommandBind bind = CommandBindsConfig.getBinds().get(i);
            int badgeColor = bind.isCommand() ? 0x8044AA44 : 0x804444AA;
            graphics.fill(panelL + 10, screenY, panelL + 14, screenY + ITEM_HEIGHT - 4, badgeColor);

            eBtn.setY(screenY);
            eBtn.extractRenderState(graphics, mouseX, adjustedMouseY, partialTick);
            eBtn.setY(screenY + scroll);

            dBtn.setY(screenY);
            dBtn.extractRenderState(graphics, mouseX, adjustedMouseY, partialTick);
            dBtn.setY(screenY + scroll);
        }

        graphics.disableScissor();
        renderScrollbar(graphics, panelR - 8, clipTop, 4, clipBottom - clipTop);
    }

    private void renderScrollbar(GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
        double max = maxScroll();
        if (max <= 0) return;
        int thumbH = Math.max(20, (int) (h * h / (h + max)));
        int thumbY = y + (int) (scrollOffset / max * (h - thumbH));
        graphics.fill(x, y,      x + w, y + h,           0x40FFFFFF);
        graphics.fill(x, thumbY, x + w, thumbY + thumbH, 0xA0FFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.max(0,
                Math.min(scrollOffset - verticalAmount * 20, maxScroll()));
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;

        double mouseX = event.x();
        double mouseY = event.y();

        int clipTop    = LIST_START_Y - LIST_PADDING;
        int clipBottom = height - 50;
        if (mouseY < clipTop || mouseY > clipBottom) return false;

        double adjY = mouseY + scrollOffset;

        for (ModernButton btn : deleteButtons) {
            if (isButtonHit(btn, mouseX, adjY)) {
                btn.mouseClicked(event, doubleClick);
                return true;
            }
        }
        for (ModernButton btn : editButtons) {
            if (isButtonHit(btn, mouseX, adjY)) {
                btn.mouseClicked(event, doubleClick);
                return true;
            }
        }
        return false;
    }

    private boolean isButtonHit(ModernButton btn, double mx, double my) {
        return mx >= btn.getX() && mx < btn.getX() + btn.getWidth()
                && my >= btn.getY() && my < btn.getY() + btn.getHeight();
    }

    private double maxScroll() {
        int listH = (height - 50) - (LIST_START_Y - LIST_PADDING);
        return Math.max(0, totalContentHeight - listH);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}