package io.github.proxima812.proximahammers.fabric;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import io.github.proxima812.proximahammers.HammerItem;
import io.github.proxima812.proximahammers.HammerItems;
import io.github.proxima812.proximahammers.HammerModule;
import io.github.proxima812.proximahammers.HammerTags;
import io.github.proxima812.proximahammers.Hammers;
import io.github.proxima812.proximahammers.recipe.RepairRecipe;
import io.github.proxima812.proximahammers.recipe.SpeedMatrixRecipe;
import io.github.proxima812.proximahammers.utils.DeferredResource;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DataGenerators implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator event) {
        var pack = event.createPack();

        pack.addProvider(RecipeGen::new);
        pack.addProvider(LangGen::new);
        pack.addProvider(ItemModelGen::new);
        pack.addProvider(ItemTagsGen::new);
    }

    public static class ItemTagsGen extends FabricTagsProvider<Item> {
        public ItemTagsGen(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, Registries.ITEM, registryLookupFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            List<ResourceKey<Item>> hammers = HammerItems.HAMMERS.stream()
                    .map(e -> e.createKey(Registries.ITEM))
                    .toList();

            this.builder(ItemTags.DURABILITY_ENCHANTABLE).addAll(hammers);
            this.builder(ItemTags.MINING_LOOT_ENCHANTABLE).addAll(hammers);
            this.builder(ItemTags.VANISHING_ENCHANTABLE).addAll(hammers);
            this.builder(ItemTags.MINING_ENCHANTABLE).addAll(hammers);

            this.builder(HammerTags.HAMMERS).addAll(hammers);
            this.builder(ItemTags.PICKAXES).addAll(hammers);

            HammerItems.HAMMER_REGISTRATIONS.stream()
                    .map(HammerItems.HammerRegistration::family)
                    .filter(family -> family.hasGeneratedRepairTag())
                    .forEach(family -> this.builder(family.repairTag()).add(family.repairIngredient()));
        }
    }

    public static class RecipeGen extends FabricRecipeProvider {
        public RecipeGen(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public String getName() {
            return "Proxima Hammer Recipes";
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new RecipeProvider(provider, recipeOutput) {
                @Override
                public void buildRecipes() {
                    SpecialRecipeBuilder.special(RepairRecipe::new)
                            .save(this.output, Hammers.MOD_ID + ":repair");

                    SpecialRecipeBuilder.special(SpeedMatrixRecipe::new)
                            .save(this.output, Hammers.MOD_ID + ":speed_matrix_upgrade");

                    HammerItems.HAMMER_REGISTRATIONS.forEach(registration -> {
                        standardHammer(registration.base(), registration.family().baseRecipeMaterial());
                        standardHammer(registration.improved(), registration.family().improvedRecipeMaterial());
                    });

                    module(HammerItems.SPEED_MODULE, Items.SUGAR, Items.COPPER_INGOT, Items.REDSTONE);
                    matrixUpgrade(HammerItems.IMPROVED_SPEED_MODULE, HammerItems.SPEED_MODULE.get());
                    matrixUpgrade(HammerItems.HYBRID_IMPROVED_SPEED_MODULE, HammerItems.IMPROVED_SPEED_MODULE.get());
                    matrixUpgrade(HammerItems.ULTIMATE_LEGAL_SPEED_MODULE, HammerItems.HYBRID_IMPROVED_SPEED_MODULE.get());
                    magnetureModule();
                    knowledgeModule();
                    powerModule();
                    thorModule();
                }

                private void standardHammer(DeferredResource<Item, HammerItem> hammer, ItemLike material) {
                    this.shaped(RecipeCategory.TOOLS, hammer.get())
                            .define('a', material)
                            .define('b', Items.STICK)
                            .pattern("aaa")
                            .pattern("aaa")
                            .pattern(" b ")
                            .unlockedBy("has_material", has(material))
                            .save(recipeOutput);
                }

                private void module(DeferredResource<Item, ?> module, ItemLike left, ItemLike center, ItemLike right) {
                    this.shaped(RecipeCategory.TOOLS, module.get())
                            .define('a', left)
                            .define('b', center)
                            .define('c', right)
                            .define('r', Items.REDSTONE)
                            .pattern(" a ")
                            .pattern("rbr")
                            .pattern(" c ")
                            .unlockedBy("has_module_core", has(center))
                            .save(recipeOutput);
                }

                private void matrixUpgrade(DeferredResource<Item, ?> result, ItemLike ingredient) {
                    this.shaped(RecipeCategory.TOOLS, result.get())
                            .define('m', ingredient)
                            .pattern("mmm")
                            .pattern("mmm")
                            .pattern("mmm")
                            .unlockedBy("has_matrix", has(ingredient))
                            .save(recipeOutput);
                }

                private void magnetureModule() {
                    this.shaped(RecipeCategory.TOOLS, HammerItems.MAGNETURE_MODULE.get())
                            .define('e', Items.ENDER_PEARL)
                            .define('h', Blocks.HOPPER)
                            .define('r', Blocks.REDSTONE_BLOCK)
                            .define('c', Items.COMPASS)
                            .pattern("ehe")
                            .pattern("rcr")
                            .pattern("ehe")
                            .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                            .save(recipeOutput);
                }

                private void knowledgeModule() {
                    this.shaped(RecipeCategory.TOOLS, HammerItems.KNOWLEDGE_MODULE.get())
                            .define('b', Blocks.BOOKSHELF)
                            .define('e', Items.EXPERIENCE_BOTTLE)
                            .define('l', Blocks.LAPIS_BLOCK)
                            .define('c', Blocks.ENCHANTING_TABLE)
                            .pattern("beb")
                            .pattern("lcl")
                            .pattern("beb")
                            .unlockedBy("has_experience_bottle", has(Items.EXPERIENCE_BOTTLE))
                            .save(recipeOutput);
                }

                private void powerModule() {
                    this.shaped(RecipeCategory.TOOLS, HammerItems.POWER_MODULE.get())
                            .define('d', Blocks.DIAMOND_BLOCK)
                            .define('b', Items.BLAZE_ROD)
                            .define('a', Items.ANCIENT_DEBRIS)
                            .define('n', Items.NETHERITE_INGOT)
                            .pattern("dbd")
                            .pattern("ana")
                            .pattern("dbd")
                            .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                            .save(recipeOutput);
                }

                private void thorModule() {
                    this.shaped(RecipeCategory.TOOLS, HammerItems.THOR_MODULE.get())
                            .define('r', Items.LIGHTNING_ROD)
                            .define('t', Items.TRIDENT)
                            .define('e', Items.ELYTRA)
                            .define('s', Items.NETHER_STAR)
                            .define('b', Blocks.NETHERITE_BLOCK)
                            .define('m', HammerItems.ULTIMATE_LEGAL_SPEED_MODULE.get())
                            .pattern("rtr")
                            .pattern("ese")
                            .pattern("bmb")
                            .unlockedBy("has_nether_star", has(Items.NETHER_STAR))
                            .save(recipeOutput);
                }

            };
        }
    }

    public static class LangGen extends FabricLanguageProvider {
        protected LangGen(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(packOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            HammerItems.HAMMER_REGISTRATIONS.forEach(registration -> {
                translationBuilder.add(registration.base().get(), registration.family().baseEnglishName());
                translationBuilder.add(registration.improved().get(), registration.family().improvedEnglishName());
            });
            Set<HammerModule> describedModules = new HashSet<>();
            for (HammerItems.ModuleRegistration module : HammerItems.MODULE_ITEMS) {
                translationBuilder.add(module.item().get(), module.englishName());
                if (describedModules.add(module.module())) {
                    translationBuilder.add(module.module().descriptionKey(), module.module().englishDescription());
                }
            }

            translationBuilder.add("proximahammers.tooltip.durability", "Durability: %s / %s left");
            translationBuilder.add("proximahammers.tooltip.mining_speed", "Mining speed: %s");
            translationBuilder.add("proximahammers.tooltip.attack_damage", "Attack damage: %s");
            translationBuilder.add("proximahammers.tooltip.attack_speed", "Attack speed: %s");
            translationBuilder.add("proximahammers.tooltip.area", "Mining area: %sx%sx%s");
            translationBuilder.add("proximahammers.tooltip.enchantment_value", "Enchantability: %s");
            translationBuilder.add("proximahammers.tooltip.modules", "Installed modules:");
            translationBuilder.add("proximahammers.module.install_hint", "Install in a crafting table.");
            translationBuilder.add("proximahammers.module.speed_bonus", "+%s mining speed");

            translationBuilder.add("itemGroup.proximahammers.proximahammers_tab", "Proxima Hammers");
        }
    }

    public static class ItemModelGen extends FabricModelProvider {
        public ItemModelGen(FabricPackOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            HammerItems.HAMMERS.forEach(e -> handHeldItemHandheld(itemModelGenerator, e));
            HammerItems.MODULE_ITEMS.forEach(e -> itemModelGenerator.generateFlatItem(e.item().get(), ModelTemplates.FLAT_ITEM));
        }

        private <T extends Item> void handHeldItemHandheld(ItemModelGenerators itemModelGenerator, DeferredResource<Item, T> item) {
            itemModelGenerator.generateFlatItem(item.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        }

    }
}
