package io.github.proxima812.proximahammers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface HammerTags {
    TagKey<Block> HAMMER_NO_SMASHY = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "hammer_no_smashy"));
    TagKey<Item> HAMMERS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "hammer"));
    TagKey<Item> COAL_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "coal_tool_materials"));
    TagKey<Item> COPPER_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "copper_tool_materials"));
    TagKey<Item> LAPIS_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "lapis_tool_materials"));
    TagKey<Item> REDSTONE_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "redstone_tool_materials"));
    TagKey<Item> GOLD_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "gold_tool_materials"));
    TagKey<Item> QUARTZ_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "quartz_tool_materials"));
    TagKey<Item> AMETHYST_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "amethyst_tool_materials"));
    TagKey<Item> EMERALD_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "emerald_tool_materials"));
    TagKey<Item> OBSIDIAN_TOOL_MATERIALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Hammers.MOD_ID, "obsidian_tool_materials"));
}
