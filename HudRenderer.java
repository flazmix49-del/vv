package ru.blagovisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;

/** Ватермарка, инфо-панель и TargetHUD. */
public final class HudRenderer {
    private HudRenderer() {}

    private static LivingEntity lastTarget;
    private static long lastSeen = 0;
    private static float shownHealth = 0f;
    private static float shownAlpha = 0f;

    public static void render(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null || mc.options.hudHidden) return;

        if (BlagoConfig.watermark) watermark(ctx, mc);
        if (BlagoConfig.infoPanel) infoPanel(ctx, mc);
        if (BlagoConfig.targetHud) targetHud(ctx, mc);
        Toast.render(ctx, mc);
    }

    private static int ping(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null) return 0;
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return e == null ? 0 : e.getLatency();
    }

    private static void watermark(DrawContext ctx, MinecraftClient mc) {
        String name = "blagovisual";
        String tail = "  |  " + mc.getCurrentFps() + " fps  |  " + ping(mc) + " ms";
        int nameW = mc.textRenderer.getWidth(name);
        int w = nameW + mc.textRenderer.getWidth(tail) + 16;
        int x = 6, y = 6, h = 16;

        ctx.fill(x, y, x + w, y + h, 0xC00E0E16);
        // анимированная полоса сверху
        int segs = Math.max(1, w / 4);
        for (int i = 0; i < segs; i++) {
            int sx = x + i * w / segs;
            int ex = x + (i + 1) * w / segs;
            ctx.fill(sx, y, ex, y + 2, Colors.gradient(i / (float) segs, 255));
        }
        // имя с градиентом по буквам
        int tx = x + 8;
        for (int i = 0; i < name.length(); i++) {
            String ch = String.valueOf(name.charAt(i));
            ctx.drawText(mc.textRenderer, ch, tx, y + 5, Colors.gradient(i * 0.12f, 255), true);
            tx += mc.textRenderer.getWidth(ch);
        }
        ctx.drawText(mc.textRenderer, tail, tx, y + 5, 0xFFB8B8C8, true);
    }

    private static void infoPanel(DrawContext ctx, MinecraftClient mc) {
        String[] lines = {
                String.format("XYZ  %.1f  %.1f  %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()),
                String.format("Speed  %.2f b/s", mc.player.getVelocity().horizontalLength() * 20),
                "Swap  " + AutoSwap.NAMES[BlagoConfig.swapItem]
        };
        int w = 0;
        for (String l : lines) w = Math.max(w, mc.textRenderer.getWidth(l));
        w += 14;
        int h = lines.length * 11 + 8;
        int x = 6, y = ctx.getScaledWindowHeight() - h - 6;

        ctx.fill(x, y, x + w, y + h, 0xC00E0E16);
        ctx.fill(x, y, x + 2, y + h, Colors.gradient(0.5f, 255));
        for (int i = 0; i < lines.length; i++) {
            ctx.drawText(mc.textRenderer, lines[i], x + 8, y + 5 + i * 11, 0xFFE6E6F0, true);
        }
    }

    private static void targetHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.crosshairTarget instanceof EntityHitResult ehr) {
            Entity e = ehr.getEntity();
            if (e instanceof LivingEntity le) {
                lastTarget = le;
                lastSeen = System.currentTimeMillis();
            }
        }

        boolean visible = lastTarget != null && lastTarget.isAlive()
                && System.currentTimeMillis() - lastSeen < 2500;
        shownAlpha = MathHelper.lerp(0.15f, shownAlpha, visible ? 1f : 0f);
        if (shownAlpha < 0.03f || lastTarget == null) return;

        float max = Math.max(1f, lastTarget.getMaxHealth());
        float hp = MathHelper.clamp(lastTarget.getHealth(), 0f, max);
        shownHealth = MathHelper.lerp(0.12f, shownHealth, hp);

        int alpha = (int) (255 * shownAlpha);
        int w = 130, h = 34;
        int x = ctx.getScaledWindowWidth() / 2 + 30;
        int y = ctx.getScaledWindowHeight() / 2 + 20;

        ctx.fill(x, y, x + w, y + h, Colors.withAlpha(0x0E0E16, (int) (200 * shownAlpha)));
        ctx.fill(x, y, x + w, y + 2, Colors.gradient(0f, alpha));

        String name = lastTarget.getName().getString();
        ctx.drawText(mc.textRenderer, name, x + 8, y + 7, Colors.withAlpha(0xFFFFFF, alpha), true);

        String hpText = String.format("%.1f HP", hp);
        ctx.drawText(mc.textRenderer, hpText, x + w - 8 - mc.textRenderer.getWidth(hpText), y + 7,
                Colors.withAlpha(0xB8B8C8, alpha), true);

        int bx = x + 8, by = y + 21, bw = w - 16, bh = 5;
        ctx.fill(bx, by, bx + bw, by + bh, Colors.withAlpha(0x2A2A38, alpha));
        int fill = Math.round(bw * (shownHealth / max));
        int segs = Math.max(1, fill / 3);
        for (int i = 0; i < segs; i++) {
            int sx = bx + i * fill / segs;
            int ex = bx + (i + 1) * fill / segs;
            ctx.fill(sx, by, ex, by + bh, Colors.gradient(i / (float) segs, alpha));
        }
    }
}
