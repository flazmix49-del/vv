package ru.blagovisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;

/** Кастомный прицел: 5 стилей, анимация цвета, динамический разлёт, красный на цели. */
public final class CrosshairRenderer {
    private CrosshairRenderer() {}

    private static float spread = 0f;

    public static void render(DrawContext ctx, MinecraftClient mc) {
        if (!BlagoConfig.crosshairEnabled || mc.player == null || mc.options.hudHidden) return;
        if (!mc.options.getPerspective().isFirstPerson()) return;

        int cx = ctx.getScaledWindowWidth() / 2;
        int cy = ctx.getScaledWindowHeight() / 2;

        // Динамика: растёт при беге, прыжке и свинге
        float target = 0f;
        if (BlagoConfig.crosshairDynamic) {
            if (mc.player.isSprinting()) target += 3f;
            else if (mc.player.getVelocity().horizontalLengthSquared() > 0.001) target += 1.5f;
            if (!mc.player.isOnGround()) target += 3f;
            if (mc.player.handSwinging) target += 2f;
        }
        spread = MathHelper.lerp(0.25f, spread, target);

        boolean onEntity = isTargetingEntity(mc);
        int color = (onEntity && BlagoConfig.crosshairTargetRed)
                ? 0xFFFF4D5E
                : Colors.crosshairColor(BlagoConfig.crosshairColor, 255);
        int shadow = 0x90000000;

        int size = BlagoConfig.crosshairSize;
        int gap = BlagoConfig.crosshairGap + Math.round(spread);

        switch (BlagoConfig.crosshairStyle) {
            case 1 -> { // точка
                box(ctx, cx - 1, cy - 1, 3, 3, shadow);
                box(ctx, cx, cy, 1, 1, color);
                ctx.fill(cx - 1, cy - 1, cx + 2, cy + 2, color);
            }
            case 2 -> circle(ctx, cx, cy, gap + size, color, shadow);
            case 3 -> diamond(ctx, cx, cy, gap + size, color, shadow);
            case 4 -> {
                cross(ctx, cx, cy, gap + 3, size, color, shadow);
                circle(ctx, cx, cy, gap + size + 4, Colors.withAlpha(color, 150), 0);
            }
            default -> cross(ctx, cx, cy, gap, size, color, shadow);
        }
    }

    private static boolean isTargetingEntity(MinecraftClient mc) {
        HitResult hit = mc.crosshairTarget;
        if (hit instanceof EntityHitResult ehr) {
            Entity e = ehr.getEntity();
            return e instanceof LivingEntity;
        }
        return false;
    }

    private static void box(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    private static void cross(DrawContext ctx, int cx, int cy, int gap, int len, int color, int shadow) {
        // тень
        ctx.fill(cx - gap - len - 1, cy - 1, cx - gap + 1, cy + 2, shadow);
        ctx.fill(cx + gap, cy - 1, cx + gap + len + 1, cy + 2, shadow);
        ctx.fill(cx - 1, cy - gap - len - 1, cx + 2, cy - gap + 1, shadow);
        ctx.fill(cx - 1, cy + gap, cx + 2, cy + gap + len + 1, shadow);
        // линии
        ctx.fill(cx - gap - len, cy, cx - gap, cy + 1, color);
        ctx.fill(cx + gap, cy, cx + gap + len, cy + 1, color);
        ctx.fill(cx, cy - gap - len, cx + 1, cy - gap, color);
        ctx.fill(cx, cy + gap, cx + 1, cy + gap + len, color);
    }

    private static void circle(DrawContext ctx, int cx, int cy, int r, int color, int shadow) {
        int steps = Math.max(24, r * 8);
        for (int i = 0; i < steps; i++) {
            double a = (Math.PI * 2 * i) / steps;
            int x = cx + (int) Math.round(Math.cos(a) * r);
            int y = cy + (int) Math.round(Math.sin(a) * r);
            if (shadow != 0) ctx.fill(x - 1, y - 1, x + 2, y + 2, shadow);
        }
        for (int i = 0; i < steps; i++) {
            double a = (Math.PI * 2 * i) / steps;
            int x = cx + (int) Math.round(Math.cos(a) * r);
            int y = cy + (int) Math.round(Math.sin(a) * r);
            ctx.fill(x, y, x + 1, y + 1, color);
        }
    }

    private static void diamond(DrawContext ctx, int cx, int cy, int r, int color, int shadow) {
        for (int i = 0; i <= r; i++) {
            int[][] pts = {{cx + i, cy - r + i}, {cx - i, cy - r + i}, {cx + i, cy + r - i}, {cx - i, cy + r - i}};
            for (int[] p : pts) {
                ctx.fill(p[0] - 1, p[1] - 1, p[0] + 2, p[1] + 2, shadow);
            }
        }
        for (int i = 0; i <= r; i++) {
            int[][] pts = {{cx + i, cy - r + i}, {cx - i, cy - r + i}, {cx + i, cy + r - i}, {cx - i, cy + r - i}};
            for (int[] p : pts) {
                ctx.fill(p[0], p[1], p[0] + 1, p[1] + 1, color);
            }
        }
    }
}
