package io.github.proxima812.proximahammers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public final class HammerFamilies {
    public static final ToolMaterial STONE = material(ToolMaterial.STONE, ToolMaterial.STONE.durability(), 4.0F, ToolMaterial.STONE.attackDamageBonus(), ToolMaterial.STONE.enchantmentValue());
    public static final ToolMaterial IRON = material(ToolMaterial.IRON, ToolMaterial.IRON.durability(), 6.3F, ToolMaterial.IRON.attackDamageBonus(), ToolMaterial.IRON.enchantmentValue());
    public static final ToolMaterial DIAMOND = material(ToolMaterial.DIAMOND, ToolMaterial.DIAMOND.durability(), 10.5F, ToolMaterial.DIAMOND.attackDamageBonus(), ToolMaterial.DIAMOND.enchantmentValue());
    public static final ToolMaterial NETHERITE = material(ToolMaterial.NETHERITE, ToolMaterial.NETHERITE.durability(), 12.727273F, ToolMaterial.NETHERITE.attackDamageBonus(), ToolMaterial.NETHERITE.enchantmentValue());
    public static final ToolMaterial COAL = material(ToolMaterial.STONE, 160, 4.8F, 0.0F, 8, HammerTags.COAL_TOOL_MATERIALS);
    public static final ToolMaterial COPPER = material(ToolMaterial.STONE, 250, 5.6F, 1.0F, 12, HammerTags.COPPER_TOOL_MATERIALS);
    public static final ToolMaterial LAPIS = material(ToolMaterial.IRON, 300, 6.8F, 1.0F, 25, HammerTags.LAPIS_TOOL_MATERIALS);
    public static final ToolMaterial REDSTONE = material(ToolMaterial.IRON, 360, 7.4F, 1.0F, 18, HammerTags.REDSTONE_TOOL_MATERIALS);
    public static final ToolMaterial GOLD = material(ToolMaterial.IRON, 420, 9.2F, 0.0F, 22, HammerTags.GOLD_TOOL_MATERIALS);
    public static final ToolMaterial QUARTZ = material(ToolMaterial.IRON, 520, 8.2F, 2.0F, 10, HammerTags.QUARTZ_TOOL_MATERIALS);
    public static final ToolMaterial OBSIDIAN = material(ToolMaterial.IRON, 650, 8.8F, 2.0F, 8, HammerTags.OBSIDIAN_TOOL_MATERIALS);
    public static final ToolMaterial AMETHYST = material(ToolMaterial.DIAMOND, 900, 9.6F, 3.0F, 20, HammerTags.AMETHYST_TOOL_MATERIALS);
    public static final ToolMaterial EMERALD = material(ToolMaterial.DIAMOND, 1600, 11.4F, 4.0F, 14, HammerTags.EMERALD_TOOL_MATERIALS);

    public static final List<HammerFamily> ALL = List.of(
            vanilla("stone", "Stone", STONE, Items.STONE, Blocks.STONE),
            custom("coal", "Coal", COAL, Items.COAL, Blocks.COAL_BLOCK, HammerTags.COAL_TOOL_MATERIALS, "coal"),
            custom("copper", "Copper", COPPER, Items.COPPER_INGOT, Blocks.COPPER_BLOCK, HammerTags.COPPER_TOOL_MATERIALS, "copper_ingot"),
            custom("lapis", "Lapis", LAPIS, Items.LAPIS_LAZULI, Blocks.LAPIS_BLOCK, HammerTags.LAPIS_TOOL_MATERIALS, "lapis_lazuli"),
            custom("redstone", "Redstone", REDSTONE, Items.REDSTONE, Blocks.REDSTONE_BLOCK, HammerTags.REDSTONE_TOOL_MATERIALS, "redstone"),
            custom("gold", "Gold", GOLD, Items.GOLD_INGOT, Blocks.GOLD_BLOCK, HammerTags.GOLD_TOOL_MATERIALS, "gold_ingot"),
            custom("quartz", "Quartz", QUARTZ, Items.QUARTZ, Blocks.QUARTZ_BLOCK, HammerTags.QUARTZ_TOOL_MATERIALS, "quartz"),
            custom("obsidian", "Obsidian", OBSIDIAN, Items.OBSIDIAN, Blocks.OBSIDIAN, HammerTags.OBSIDIAN_TOOL_MATERIALS, "obsidian"),
            custom("amethyst", "Amethyst", AMETHYST, Items.AMETHYST_SHARD, Blocks.AMETHYST_BLOCK, HammerTags.AMETHYST_TOOL_MATERIALS, "amethyst_shard"),
            vanilla("iron", "Iron", IRON, Items.IRON_INGOT, Blocks.IRON_BLOCK),
            vanilla("diamond", "Diamond", DIAMOND, Items.DIAMOND, Blocks.DIAMOND_BLOCK),
            custom("emerald", "Emerald", EMERALD, Items.EMERALD, Blocks.EMERALD_BLOCK, HammerTags.EMERALD_TOOL_MATERIALS, "emerald"),
            vanilla("netherite", "Netherite", NETHERITE, Items.NETHERITE_INGOT, Blocks.NETHERITE_BLOCK, 1.35F)
    );

    private HammerFamilies() {}

    private static ToolMaterial material(ToolMaterial base, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairTag) {
        return new ToolMaterial(base.incorrectBlocksForDrops(), durability, speed, attackDamageBonus, enchantmentValue, repairTag);
    }

    private static ToolMaterial material(ToolMaterial base, int durability, float speed, float attackDamageBonus, int enchantmentValue) {
        return new ToolMaterial(base.incorrectBlocksForDrops(), durability, speed, attackDamageBonus, enchantmentValue, base.repairItems());
    }

    private static HammerFamily vanilla(String id, String englishName, ToolMaterial material, ItemLike baseRecipeMaterial, ItemLike improvedRecipeMaterial) {
        return vanilla(id, englishName, material, baseRecipeMaterial, improvedRecipeMaterial, 1.5F);
    }

    private static HammerFamily vanilla(String id, String englishName, ToolMaterial material, ItemLike baseRecipeMaterial, ItemLike improvedRecipeMaterial, float improvedDurabilityMultiplier) {
        return new HammerFamily(id, englishName, material, baseRecipeMaterial, improvedRecipeMaterial, improvedDurabilityMultiplier, null, null);
    }

    private static HammerFamily custom(String id, String englishName, ToolMaterial material, ItemLike baseRecipeMaterial, ItemLike improvedRecipeMaterial, TagKey<Item> repairTag, String repairIngredientId) {
        return new HammerFamily(id, englishName, material, baseRecipeMaterial, improvedRecipeMaterial, 1.5F, repairTag,
                ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(repairIngredientId)));
    }
}
