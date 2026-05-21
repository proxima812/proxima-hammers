package io.github.proxima812.proximahammers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class HammerModuleData {
    private static final String ROOT = "proximahammers_modules";
    private static final String SPEED_BONUS = "proximahammers_speed_bonus";

    private HammerModuleData() {}

    public static Set<HammerModule> getModules(ItemStack stack) {
        var data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        var modules = data.contains(ROOT) ? data.getCompound(ROOT) : new CompoundTag();
        Set<HammerModule> result = EnumSet.noneOf(HammerModule.class);

        for (HammerModule module : HammerModule.values()) {
            if (modules.contains(module.id()) && modules.getBoolean(module.id())) {
                result.add(module);
            }
        }

        return result;
    }

    public static boolean has(ItemStack stack, HammerModule module) {
        return getModules(stack).contains(module);
    }

    public static int count(ItemStack stack) {
        return getModules(stack).size();
    }

    public static boolean canInstall(ItemStack hammer, HammerModule module) {
        return hammer.getItem() instanceof HammerItem
                && !has(hammer, module)
                && count(hammer) < 2;
    }

    public static ItemStack install(ItemStack hammer, List<HammerModule> modules) {
        ItemStack result = hammer.copy();
        result.setCount(1);

        CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> {
            CompoundTag moduleTag = (tag.contains(ROOT) ? tag.getCompound(ROOT) : new CompoundTag()).copy();
            for (HammerModule module : modules) {
                moduleTag.putBoolean(module.id(), true);
            }
            tag.put(ROOT, moduleTag);
        });

        result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return result;
    }

    public static ItemStack installSpeed(ItemStack hammer, float speedBonus) {
        ItemStack result = install(hammer, List.of(HammerModule.SPEED));

        CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> tag.putFloat(SPEED_BONUS, speedBonus));
        return result;
    }

    public static float speedBonus(ItemStack stack) {
        if (!has(stack, HammerModule.SPEED)) {
            return 0.0F;
        }

        var data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return data.contains(SPEED_BONUS) ? data.getFloat(SPEED_BONUS) : 2.0F;
    }

    public static List<HammerModule> installableModules(ItemStack hammer, ItemStack left, ItemStack right) {
        List<HammerModule> modules = new ArrayList<>();
        addInstallable(hammer, left, modules);
        addInstallable(hammer, right, modules);
        return modules;
    }

    private static void addInstallable(ItemStack hammer, ItemStack stack, List<HammerModule> modules) {
        HammerModule.fromStack(stack).ifPresent(module -> {
            if (!modules.contains(module) && canInstall(hammer, module) && count(hammer) + modules.size() < 2) {
                modules.add(module);
            }
        });
    }
}
