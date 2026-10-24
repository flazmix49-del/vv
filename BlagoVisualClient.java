package ru.blagovisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class BlagoVisualClient implements ClientModInitializer {
    public static KeyBinding swapKey;
    public static KeyBinding menuKey;
    public static KeyBinding crosshairKey;

    @Override
    public void onInitializeClient() {
        BlagoConfig.load();

        swapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blagovisual.swap", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.blagovisual"));
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blagovisual.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.blagovisual"));
        crosshairKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blagovisual.crosshair", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.blagovisual"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new ConfigScreen());
            }
            while (crosshairKey.wasPressed()) {
                BlagoConfig.crosshairEnabled = !BlagoConfig.crosshairEnabled;
                BlagoConfig.save();
            }
            while (swapKey.wasPressed()) {
                if (mc.currentScreen == null) AutoSwap.swap(mc);
            }
        });

        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            var mc = net.minecraft.client.MinecraftClient.getInstance();
            CrosshairRenderer.render(ctx, mc);
            HudRenderer.render(ctx, mc);
        });
    }
}
