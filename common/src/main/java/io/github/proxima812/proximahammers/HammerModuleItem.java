package io.github.proxima812.proximahammers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class HammerModuleItem extends Item {
    private final HammerModule module;
    private final float speedBonus;

    public HammerModuleItem(Properties properties, HammerModule module, float speedBonus) {
        super(properties);
        this.module = module;
        this.speedBonus = speedBonus;
    }

    public HammerModule module() {
        return module;
    }

    public float speedBonus() {
        return speedBonus;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.translatable(module.descriptionKey()).withStyle(ChatFormatting.GRAY));
        if (module == HammerModule.SPEED) {
            consumer.accept(Component.translatable("proximahammers.module.speed_bonus", HammerItem.formatNumber(speedBonus)).withStyle(ChatFormatting.AQUA));
        }
        consumer.accept(Component.translatable("proximahammers.module.install_hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
