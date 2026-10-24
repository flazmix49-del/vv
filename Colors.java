package ru.blagovisual;

import net.minecraft.util.math.MathHelper;

public final class Colors {
    private Colors() {}

    public static int rainbow(float speed, float offset, int alpha) {
        float h = ((System.currentTimeMillis() % (long) (speed * 1000)) / (speed * 1000f) + offset) % 1f;
        return (alpha << 24) | MathHelper.hsvToRgb(h, 0.65f, 1f);
    }

    /** Мягкий градиент розовый -> фиолетовый -> циан, плавно бегущий по времени. */
    public static int gradient(float offset, int alpha) {
        float t = (float) ((Math.sin(System.currentTimeMillis() / 900.0 + offset * 6.0) + 1) / 2);
        int r = (int) MathHelper.lerp(t, 255, 80);
        int g = (int) MathHelper.lerp(t, 105, 220);
        int b = (int) MathHelper.lerp(t, 220, 255);
        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    public static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    public static int crosshairColor(int mode, int alpha) {
        return switch (mode) {
            case 1 -> withAlpha(0x4DE8FF, alpha);
            case 2 -> withAlpha(0xFF6EC7, alpha);
            case 3 -> withAlpha(0xFFFFFF, alpha);
            case 4 -> withAlpha(0xB070FF, alpha);
            default -> rainbow(4f, 0f, alpha);
        };
    }
}
