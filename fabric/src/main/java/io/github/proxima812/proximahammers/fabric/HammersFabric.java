package io.github.proxima812.proximahammers.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import io.github.proxima812.proximahammers.HammerItems;
import io.github.proxima812.proximahammers.HammerPowers;
import io.github.proxima812.proximahammers.Hammers;
import net.fabricmc.api.ModInitializer;
import io.github.proxima812.proximahammers.recipe.HammerRecipes;
import io.github.proxima812.proximahammers.utils.DeferredResource;

public class HammersFabric implements ModInitializer {
    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Hammers.id("item_group"));

    public static final CreativeModeTab CREATIVE_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(HammerItems.hammer("netherite").get()))
            .title(Component.translatable("itemGroup.proximahammers.proximahammers_tab"))
            .build();

    @Override
    public void onInitialize() {
        Hammers.init();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_KEY, CREATIVE_TAB);

        for (var item : HammerItems.ITEMS) {
            var key = item.createKey(Registries.ITEM);
            Registry.register(BuiltInRegistries.ITEM, key, item.get());
        }

        registerRecipe(HammerRecipes.REPAIR_RECIPE, HammerRecipes.REPAIR_RECIPE_SERIALIZER);
        registerRecipe(HammerRecipes.SPEED_MATRIX_RECIPE, HammerRecipes.SPEED_MATRIX_RECIPE_SERIALIZER);
        ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(HammerPowers::tick));

        ItemGroupEvents.modifyEntriesEvent(CREATIVE_TAB_KEY).register(itemGroup -> {
            for (var item : HammerItems.ITEMS) {
                itemGroup.accept(item.get());
            }
        });
    }

    private static void registerRecipe(DeferredResource<RecipeType<?>, ? extends RecipeType<?>> type,
                                       DeferredResource<RecipeSerializer<?>, ? extends RecipeSerializer<?>> serializer) {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, type.createKey(Registries.RECIPE_TYPE), type.get());
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, serializer.createKey(Registries.RECIPE_SERIALIZER), serializer.get());
    }
}
