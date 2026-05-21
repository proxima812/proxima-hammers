package io.github.proxima812.proximahammers;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public record HammerFamily(
        String id,
        String englishName,
        Tier material,
        ItemLike baseRecipeMaterial,
        ItemLike improvedRecipeMaterial,
        float improvedDurabilityMultiplier,
        @Nullable TagKey<Item> repairTag,
        @Nullable ResourceKey<Item> repairIngredient
) {
    public String baseItemId() {
        return id + "_hammer";
    }

    public String improvedItemId() {
        return "improved_" + id + "_hammer";
    }

    public String baseEnglishName() {
        return englishName + " Hammer";
    }

    public String improvedEnglishName() {
        return "Improved " + englishName + " Hammer";
    }

    public boolean hasGeneratedRepairTag() {
        return repairTag != null && repairIngredient != null;
    }
}
