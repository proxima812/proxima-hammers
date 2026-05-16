package io.github.proxima812.proximahammers.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import io.github.proxima812.proximahammers.utils.DeferredResource;

public class HammerRecipes {
    public static final DeferredResource<RecipeType<?>, RecipeType<RepairRecipe>> REPAIR_RECIPE =
            new DeferredResource<>("hammer_repair", () -> new RecipeType<>() {});

    public static final DeferredResource<RecipeSerializer<?>, RecipeSerializer<RepairRecipe>> REPAIR_RECIPE_SERIALIZER =
            new DeferredResource<>("repair", () -> new RecipeSerializer<>() {
                @Override
                public MapCodec<RepairRecipe> codec() {
                    return RepairRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, RepairRecipe> streamCodec() {
                    return RepairRecipe.STREAM_CODEC;
                }
            });

    public static final DeferredResource<RecipeType<?>, RecipeType<SpeedMatrixRecipe>> SPEED_MATRIX_RECIPE =
            new DeferredResource<>("speed_matrix_upgrade", () -> new RecipeType<>() {});

    public static final DeferredResource<RecipeSerializer<?>, RecipeSerializer<SpeedMatrixRecipe>> SPEED_MATRIX_RECIPE_SERIALIZER =
            new DeferredResource<>("speed_matrix_upgrade", () -> new RecipeSerializer<>() {
                @Override
                public MapCodec<SpeedMatrixRecipe> codec() {
                    return SpeedMatrixRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, SpeedMatrixRecipe> streamCodec() {
                    return SpeedMatrixRecipe.STREAM_CODEC;
                }
            });
}
