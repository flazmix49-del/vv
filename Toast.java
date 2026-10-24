package ru.blagovisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Плавное уведомление "Swap" над хотбаром. */
public final class Toast {
    private Toast() {}

    private static String text = "";
    private static boolean ok = true;
    private static long start = -10_000;
    private static final long LIFE = 1800;

    public static void push(String msg, boolean success) {
        text = msg;
        ok = success;
        start = System.currentTimeMillis();
    }

    public static void render(DrawContext ctx, MinecraftClient mc) {
        if (!BlagoConfig.swapToast) return;
        long age = System.currentTimeMillis() - start;
        if (age > LIFE) return;

        float in = Math.min(1f, age / 200f);
        float out = Math.min(1f, (LIFE - age) / 400f);
        float a = Math.min(in, out);
        int alpha = (int) (255 * a);
        if (alpha < 8) return;

        int w = mc.textRenderer.getWidth(text) + 24;
        int h = 20;
        int x = ctx.getScaledWindowWidth() / 2 - w / 2;
        int y = ctx.getScaledWindowHeight() - 78 - (int) ((1 - in) * 8);

        int accent = ok ? Colors.gradient(0f, alpha) : Colors.withAlpha(0xFF5C6E, alpha);
        ctx.fill(x, y, x + w, y + h, Colors.withAlpha(0x0E0E16, (int) (190 * a)));
        ctx.fill(x, y + h - 2, x + w, y + h, accent);
        ctx.fill(x, y, x + 2, y + h, accent);
        ctx.drawText(mc.textRenderer, text, x + 12, y + 6, Colors.withAlpha(0xFFFFFF, alpha), true);
    }
}
