package ru.blagovisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * AutoSwap: по нажатию бинда мгновенно меняет предмет в левой руке
 * на выбранный (тотем / яблоко / щит / жемчуг) из хотбара или инвентаря.
 * Одно нажатие = одно действие, без макросов и задержек.
 */
public final class AutoSwap {
    private AutoSwap() {}

    public static final String[] NAMES = {"Тотем", "Золотое яблоко", "Щит", "Зач. яблоко", "Жемчуг"};

    public static Item itemFor(int mode) {
        return switch (mode) {
            case 1 -> Items.GOLDEN_APPLE;
            case 2 -> Items.SHIELD;
            case 3 -> Items.ENCHANTED_GOLDEN_APPLE;
            case 4 -> Items.ENDER_PEARL;
            default -> Items.TOTEM_OF_UNDYING;
        };
    }

    /** @return true если обмен выполнен */
    public static boolean swap(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null) return false;

        Item wanted = itemFor(BlagoConfig.swapItem);

        // Уже в левой руке, ничего делать не надо
        if (player.getOffHandStack().isOf(wanted)) {
            Toast.push("Уже в руке: " + NAMES[BlagoConfig.swapItem], false);
            return false;
        }

        int slot = findSlot(player, wanted);
        if (slot == -1) {
            Toast.push("Нет предмета: " + NAMES[BlagoConfig.swapItem], false);
            return false;
        }

        // Кнопка 40 = обмен слота с левой рукой
        mc.interactionManager.clickSlot(
                player.playerScreenHandler.syncId, slot, 40, SlotActionType.SWAP, player);
        Toast.push("Swap: " + NAMES[BlagoConfig.swapItem], true);
        return true;
    }

    /** Индекс слота в playerScreenHandler: хотбар 36..44, инвентарь 9..35. */
    private static int findSlot(ClientPlayerEntity player, Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.isOf(item)) return 36 + i;
        }
        for (int i = 9; i < 36; i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.isOf(item)) return i;
        }
        return -1;
    }
}
