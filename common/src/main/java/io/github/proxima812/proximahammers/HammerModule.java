package io.github.proxima812.proximahammers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public enum HammerModule {
    DURABILITY("durability_module", "Durability Matrix", "Reduces durability loss while mining."),
    SPEED("speed_module", "Haste Matrix", "Improves block breaking speed."),
    FORTUNE("fortune_module", "Fortune Matrix", "Can duplicate mined drops."),
    SMELTING("smelting_module", "Smelting Matrix", "Smelts common raw mining drops."),
    EXPERIENCE("experience_module", "Experience Matrix", "Increases experience from mined blocks."),
    MAGNET("magnet_module", "Magnet Matrix", "Moves mined drops to the player."),
    MAGNETURE("magneture_module", "Magneture Matrix", "Pulls nearby item drops into your inventory."),
    KNOWLEDGE("knowledge_module", "Knowledge Matrix", "Doubles experience gained while mining."),
    POWER("power_module", "Power Matrix", "Adds 12 attack damage to the hammer."),
    THOR("thor_module", "Thor Matrix", "Lets you glide while holding the hammer and prevents hammer durability loss."),
    VOIDING("voiding_module", "Void Matrix", "Deletes common mining clutter."),
    EXPANSION("expansion_module", "Expansion Matrix", "Expands hammer mining depth."),
    REPAIR("repair_module", "Repair Matrix", "Occasionally repairs itself while mining."),
    LIGHT("light_module", "Light Matrix", "Places torches from your inventory while mining."),
    SAFETY("safety_module", "Safety Matrix", "Avoids blocks touching lava."),
    SILK("silk_module", "Silk Matrix", "Preserves mined block forms when possible.");

    private final String id;
    private final String englishName;
    private final String englishDescription;

    HammerModule(String id, String englishName, String englishDescription) {
        this.id = id;
        this.englishName = englishName;
        this.englishDescription = englishDescription;
    }

    public String id() {
        return id;
    }

    public String englishName() {
        return englishName;
    }

    public String englishDescription() {
        return englishDescription;
    }

    public String langKey() {
        return "item.proximahammers." + id;
    }

    public String descriptionKey() {
        return "proximahammers.module." + id + ".description";
    }

    public static Optional<HammerModule> fromItem(Item item) {
        if (item instanceof HammerModuleItem moduleItem) {
            return Optional.of(moduleItem.module());
        }

        return Optional.empty();
    }

    public static Optional<HammerModule> fromStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return fromItem(stack.getItem());
    }
}
