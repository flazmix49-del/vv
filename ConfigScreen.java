package ru.blagovisual;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Меню настроек (RIGHT SHIFT по умолчанию). */
public class ConfigScreen extends Screen {
    private static final String[] STYLES = {"Крест", "Точка", "Круг", "Ромб", "Крест + кольцо"};
    private static final String[] COLORS = {"Радуга", "Циан", "Розовый", "Белый", "Фиолетовый"};

    public ConfigScreen() {
        super(Text.literal("blagovisual"));
    }

    @Override
    protected void init() {
        int w = 190, h = 20, gap = 24;
        int left = width / 2 - w - 5;
        int right = width / 2 + 5;
        int top = height / 2 - 82;

        // --- левая колонка: прицел ---
        add(left, top, w, h, () -> "Прицел: " + onOff(BlagoConfig.crosshairEnabled),
                () -> BlagoConfig.crosshairEnabled = !BlagoConfig.crosshairEnabled);
        add(left, top + gap, w, h, () -> "Стиль: " + STYLES[BlagoConfig.crosshairStyle],
                () -> BlagoConfig.crosshairStyle = (BlagoConfig.crosshairStyle + 1) % STYLES.length);
        add(left, top + gap * 2, w, h, () -> "Цвет: " + COLORS[BlagoConfig.crosshairColor],
                () -> BlagoConfig.crosshairColor = (BlagoConfig.crosshairColor + 1) % COLORS.length);
        add(left, top + gap * 3, w, h, () -> "Размер: " + BlagoConfig.crosshairSize,
                () -> BlagoConfig.crosshairSize = BlagoConfig.crosshairSize >= 12 ? 2 : BlagoConfig.crosshairSize + 1);
        add(left, top + gap * 4, w, h, () -> "Зазор: " + BlagoConfig.crosshairGap,
                () -> BlagoConfig.crosshairGap = BlagoConfig.crosshairGap >= 10 ? 0 : BlagoConfig.crosshairGap + 1);
        add(left, top + gap * 5, w, h, () -> "Динамика: " + onOff(BlagoConfig.crosshairDynamic),
                () -> BlagoConfig.crosshairDynamic = !BlagoConfig.crosshairDynamic);
        add(left, top + gap * 6, w, h, () -> "Красный на цели: " + onOff(BlagoConfig.crosshairTargetRed),
                () -> BlagoConfig.crosshairTargetRed = !BlagoConfig.crosshairTargetRed);

        // --- правая колонка: HUD + AutoSwap ---
        add(right, top, w, h, () -> "Ватермарка: " + onOff(BlagoConfig.watermark),
                () -> BlagoConfig.watermark = !BlagoConfig.watermark);
        add(right, top + gap, w, h, () -> "Инфо-панель: " + onOff(BlagoConfig.infoPanel),
                () -> BlagoConfig.infoPanel = !BlagoConfig.infoPanel);
        add(right, top + gap * 2, w, h, () -> "TargetHUD: " + onOff(BlagoConfig.targetHud),
                () -> BlagoConfig.targetHud = !BlagoConfig.targetHud);
        add(right, top + gap * 3, w, h, () -> "Уведомление Swap: " + onOff(BlagoConfig.swapToast),
                () -> BlagoConfig.swapToast = !BlagoConfig.swapToast);
        add(right, top + gap * 5, w, h, () -> "AutoSwap предмет: " + AutoSwap.NAMES[BlagoConfig.swapItem],
                () -> BlagoConfig.swapItem = (BlagoConfig.swapItem + 1) % AutoSwap.NAMES.length);

        addDrawableChild(ButtonWidget.builder(Text.literal("Готово"), b -> close())
                .dimensions(width / 2 - 60, top + gap * 7 + 6, 120, h).build());
    }

    private void add(int x, int y, int w, int h, java.util.function.Supplier<String> label, Runnable action) {
        ButtonWidget[] ref = new ButtonWidget[1];
        ref[0] = ButtonWidget.builder(Text.literal(label.get()), b -> {
            action.run();
            b.setMessage(Text.literal(label.get()));
            BlagoConfig.save();
        }).dimensions(x, y, w, h).build();
        addDrawableChild(ref[0]);
    }

    private static String onOff(boolean v) {
        return v ? "§aВкл" : "§cВыкл";
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        String title = "blagovisual";
        int tx = width / 2 - textRenderer.getWidth(title) * 2 / 2;
        var m = ctx.getMatrices();
        m.push();
        m.translate(width / 2f, height / 2f - 112, 0);
        m.scale(2f, 2f, 1f);
        int x = -textRenderer.getWidth(title) / 2;
        for (int i = 0; i < title.length(); i++) {
            String ch = String.valueOf(title.charAt(i));
            ctx.drawText(textRenderer, ch, x, 0, Colors.gradient(i * 0.12f, 255), true);
            x += textRenderer.getWidth(ch);
        }
        m.pop();

        ctx.drawCenteredTextWithShadow(textRenderer,
                "Бинд AutoSwap меняется в Управлении → blagovisual",
                width / 2, height / 2 + 100, 0xFF8888A0);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
