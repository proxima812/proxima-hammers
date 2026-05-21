package io.github.proxima812.proximahammers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public final class HammerFamilies {
    public static final Tier STONE = material(Tiers.STONE, Tiers.STONE.getUses(), 4.0F, Tiers.STONE.getAttackDamageBonus(), Tiers.STONE.getEnchantmentValue());
    public static final Tier IRON = material(Tiers.IRON, Tiers.IRON.getUses(), 6.3F, Tiers.IRON.getAttackDamageBonus(), Tiers.IRON.getEnchantmentValue());
    public static final Tier DIAMOND = material(Tiers.DIAMOND, Tiers.DIAMOND.getUses(), 10.5F, Tiers.DIAMOND.getAttackDamageBonus(), Tiers.DIAMOND.getEnchantmentValue());
    public static final Tier NETHERITE = material(Tiers.NETHERITE, Tiers.NETHERITE.getUses(), 12.727273F, Tiers.NETHERITE.getAttackDamageBonus(), Tiers.NETHERITE.getEnchantmentValue());
    public static final Tier COAL = material(Tiers.STONE, 160, 4.8F, 0.0F, 8, HammerTags.COAL_TOOL_MATERIALS);
    public static final Tier COPPER = material(Tiers.STONE, 250, 5.6F, 1.0F, 12, HammerTags.COPPER_TOOL_MATERIALS);
    public static final Tier LAPIS = material(Tiers.IRON, 300, 6.8F, 1.0F, 25, HammerTags.LAPIS_TOOL_MATERIALS);
    public static final Tier REDSTONE = material(Tiers.IRON, 360, 7.4F, 1.0F, 18, HammerTags.REDSTONE_TOOL_MATERIALS);
    public static final Tier GOLD = material(Tiers.IRON, 420, 9.2F, 0.0F, 22, HammerTags.GOLD_TOOL_MATERIALS);
    public static final Tier QUARTZ = material(Tiers.IRON, 520, 8.2F, 2.0F, 10, HammerTags.QUARTZ_TOOL_MATERIALS);
    public static final Tier OBSIDIAN = material(Tiers.IRON, 650, 8.8F, 2.0F, 8, HammerTags.OBSIDIAN_TOOL_MATERIALS);
    public static final Tier AMETHYST = material(Tiers.DIAMOND, 900, 9.6F, 3.0F, 20, HammerTags.AMETHYST_TOOL_MATERIALS);
    public static final Tier EMERALD = material(Tiers.DIAMOND, 1600, 11.4F, 4.0F, 14, HammerTags.EMERALD_TOOL_MATERIALS);

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

    private static Tier material(Tier base, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairTag) {
        return new HammerTier(base.getIncorrectBlocksForDrops(), durability, speed, attackDamageBonus, enchantmentValue, Ingredient.of(repairTag));
    }

    private static Tier material(Tier base, int durability, float speed, float attackDamageBonus, int enchantmentValue) {
        return new HammerTier(base.getIncorrectBlocksForDrops(), durability, speed, attackDamageBonus, enchantmentValue, base.getRepairIngredient());
    }

    private static HammerFamily vanilla(String id, String englishName, Tier material, ItemLike baseRecipeMaterial, ItemLike improvedRecipeMaterial) {
        return vanilla(id, englishName, material, baseRecipeMaterial, improvedRecipeMaterial, 1.5F);
    }

    private static HammerFamily vanilla(String id, String englishName, Tier material, ItemLike baseRecipeMaterial, ItemLike improvedRecipeMaterial, float improvedDurabilityMultiplier) {
        return new HammerFamily(id, englishName, material, baseRecipeMaterial, improvedRecipeMaterial, improvedDurabilityMultiplier, null, null);
    }

    private static HammerFamily custom(String id, String englishName, Tier material, ItemLike baseRecipeMaterial, ItemLike improvedRecipeMaterial, TagKey<Item> repairTag, String repairIngredientId) {
        return new HammerFamily(id, englishName, material, baseRecipeMaterial, improvedRecipeMaterial, 1.5F, repairTag,
                ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(repairIngredientId)));
    }

    private record HammerTier(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus, int enchantmentValue, Ingredient repairIngredient) implements Tier {
        @Override
        public int getUses() {
            return uses;
        }

        @Override
        public float getSpeed() {
            return speed;
        }

        @Override
        public float getAttackDamageBonus() {
            return attackDamageBonus;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return incorrectBlocksForDrops;
        }

        @Override
        public int getEnchantmentValue() {
            return enchantmentValue;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repairIngredient;
        }
    }
}
