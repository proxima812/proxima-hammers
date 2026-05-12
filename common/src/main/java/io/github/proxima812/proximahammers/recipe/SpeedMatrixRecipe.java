package io.github.proxima812.proximahammers.recipe;

import com.mojang.serialization.MapCodec;
import io.github.proxima812.proximahammers.HammerItem;
import io.github.proxima812.proximahammers.HammerModule;
import io.github.proxima812.proximahammers.HammerModuleData;
import io.github.proxima812.proximahammers.HammerModuleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpeedMatrixRecipe extends CustomRecipe {
    public static final SpeedMatrixRecipe INSTANCE = new SpeedMatrixRecipe();

    public static final MapCodec<SpeedMatrixRecipe> CODEC = MapCodec.unit(() -> INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, SpeedMatrixRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public SpeedMatrixRecipe() {
        super();
    }

    @Override
    public boolean matches(CraftingInput recipeInput, Level level) {
        return getTargetHammer(recipeInput) != null;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput recipeInput) {
        ItemStack hammer = getTargetHammer(recipeInput);
        if (hammer == null) {
            return ItemStack.EMPTY;
        }

        HammerModuleItem moduleItem = getModuleItem(recipeInput);
        if (moduleItem == null) {
            return ItemStack.EMPTY;
        }

        if (moduleItem.module() == HammerModule.SPEED) {
            return HammerModuleData.installSpeed(hammer, moduleItem.speedBonus());
        }

        return HammerModuleData.install(hammer, java.util.List.of(moduleItem.module()));
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput recipeInput) {
        return NonNullList.withSize(recipeInput.size(), ItemStack.EMPTY);
    }

    @Override
    public @NotNull RecipeSerializer<SpeedMatrixRecipe> getSerializer() {
        return HammerRecipes.SPEED_MATRIX_RECIPE_SERIALIZER.get();
    }

    @Nullable
    private ItemStack getTargetHammer(CraftingInput recipeInput) {
        if (recipeInput.ingredientCount() != 2) {
            return null;
        }

        ItemStack hammer = ItemStack.EMPTY;
        HammerModuleItem module = null;

        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack stack = recipeInput.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getItem() instanceof HammerItem) {
                if (!hammer.isEmpty()) {
                    return null;
                }

                hammer = stack;
            } else if (stack.getItem() instanceof HammerModuleItem moduleItem) {
                if (module != null) {
                    return null;
                }

                module = moduleItem;
            } else {
                return null;
            }
        }

        if (hammer.isEmpty() || module == null || !HammerModuleData.canInstall(hammer, module.module())) {
            return null;
        }

        return hammer;
    }

    @Nullable
    private HammerModuleItem getModuleItem(CraftingInput recipeInput) {
        for (int i = 0; i < recipeInput.size(); i++) {
            ItemStack stack = recipeInput.getItem(i);
            if (stack.getItem() instanceof HammerModuleItem moduleItem) {
                return moduleItem;
            }
        }

        return null;
    }
}
