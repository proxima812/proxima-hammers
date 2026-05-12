package io.github.proxima812.proximahammers;

import io.github.proxima812.proximahammers.utils.DeferredResource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class HammerItems {
    public static final Function<String, Item.Properties> DEFAULT_PROPERTIES = (name) -> new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, Hammers.id(name)));

    public static final List<DeferredResource<Item, ? extends Item>> ITEMS = new ArrayList<>();
    public static final List<DeferredResource<Item, HammerItem>> HAMMERS = new ArrayList<>();
    public static final List<ModuleRegistration> MODULE_ITEMS = new ArrayList<>();
    public static final List<SpeedMatrixRegistration> SPEED_MATRICES = new ArrayList<>();
    public static final Map<HammerModule, DeferredResource<Item, HammerModuleItem>> MODULES = new EnumMap<>(HammerModule.class);
    public static final Map<String, HammerRegistration> HAMMER_REGISTRY = new LinkedHashMap<>();
    public static final List<HammerRegistration> HAMMER_REGISTRATIONS = registerHammers();

    public static final DeferredResource<Item, HammerModuleItem> SPEED_MODULE = registerSpeedMatrix("speed_module", "Haste Matrix", 2.0F, true);
    public static final DeferredResource<Item, HammerModuleItem> IMPROVED_SPEED_MODULE = registerSpeedMatrix("improved_speed_module", "Improved Haste Matrix", 6.5F, false);
    public static final DeferredResource<Item, HammerModuleItem> HYBRID_IMPROVED_SPEED_MODULE = registerSpeedMatrix("hybrid_improved_speed_module", "Hybrid Improved Haste Matrix", 9.5F, false);
    public static final DeferredResource<Item, HammerModuleItem> ULTIMATE_LEGAL_SPEED_MODULE = registerSpeedMatrix("ultimate_legal_speed_module", "Ultimate Legal Haste Matrix", 12.5F, false);
    public static final DeferredResource<Item, HammerModuleItem> MAGNETURE_MODULE = registerModule(HammerModule.MAGNETURE);
    public static final DeferredResource<Item, HammerModuleItem> KNOWLEDGE_MODULE = registerModule(HammerModule.KNOWLEDGE);
    public static final DeferredResource<Item, HammerModuleItem> POWER_MODULE = registerModule(HammerModule.POWER);
    public static final DeferredResource<Item, HammerModuleItem> THOR_MODULE = registerModule(HammerModule.THOR);

    private HammerItems() {}

    public static DeferredResource<Item, HammerItem> hammer(String familyId) {
        return hammer(familyId, false);
    }

    public static DeferredResource<Item, HammerItem> hammer(String familyId, boolean improved) {
        HammerRegistration registration = HAMMER_REGISTRY.get(familyId);
        if (registration == null) {
            throw new IllegalArgumentException("Unknown hammer family: " + familyId);
        }

        return improved ? registration.improved() : registration.base();
    }

    private static List<HammerRegistration> registerHammers() {
        List<HammerRegistration> registrations = new ArrayList<>();

        for (HammerFamily family : HammerFamilies.ALL) {
            HammerRegistration registration = registerHammerFamily(family);
            registrations.add(registration);
            HAMMER_REGISTRY.put(family.id(), registration);
        }

        return List.copyOf(registrations);
    }

    private static HammerRegistration registerHammerFamily(HammerFamily family) {
        DeferredResource<Item, HammerItem> base = registerHammer(family.baseItemId(),
                () -> new HammerItem(DEFAULT_PROPERTIES.apply(family.baseItemId()), family.material(), 3, 1, 1));
        DeferredResource<Item, HammerItem> improved = registerHammer(family.improvedItemId(),
                () -> new HammerItem(DEFAULT_PROPERTIES.apply(family.improvedItemId()), family.material(), 3, 3, 1, 1.5F, 1.1F, family.improvedDurabilityMultiplier()));

        return new HammerRegistration(family, base, improved);
    }

    private static DeferredResource<Item, HammerItem> registerHammer(String name, Supplier<HammerItem> supplier) {
        DeferredResource<Item, HammerItem> entry = register(name, supplier);
        HAMMERS.add(entry);
        return entry;
    }

    private static <T extends Item> DeferredResource<Item, T> register(String name, Supplier<T> supplier) {
        var entry = new DeferredResource<Item, T>(name, supplier);
        ITEMS.add(entry);
        return entry;
    }

    private static DeferredResource<Item, HammerModuleItem> registerSpeedMatrix(String id, String englishName, float speedBonus, boolean primary) {
        var entry = register(id, () -> new HammerModuleItem(DEFAULT_PROPERTIES.apply(id), HammerModule.SPEED, speedBonus));
        MODULE_ITEMS.add(new ModuleRegistration(HammerModule.SPEED, englishName, entry));
        SPEED_MATRICES.add(new SpeedMatrixRegistration(id, englishName, speedBonus, entry));
        if (primary) {
            MODULES.put(HammerModule.SPEED, entry);
        }
        return entry;
    }

    private static DeferredResource<Item, HammerModuleItem> registerModule(HammerModule module) {
        var entry = register(module.id(), () -> new HammerModuleItem(DEFAULT_PROPERTIES.apply(module.id()), module, 0.0F));
        MODULE_ITEMS.add(new ModuleRegistration(module, module.englishName(), entry));
        MODULES.put(module, entry);
        return entry;
    }

    public static DeferredResource<Item, HammerModuleItem> moduleItem(HammerModule module) {
        return MODULES.get(module);
    }

    public static void init() {}

    public record HammerRegistration(HammerFamily family, DeferredResource<Item, HammerItem> base, DeferredResource<Item, HammerItem> improved) {}
    public record ModuleRegistration(HammerModule module, String englishName, DeferredResource<Item, HammerModuleItem> item) {}
    public record SpeedMatrixRegistration(String id, String englishName, float speedBonus, DeferredResource<Item, HammerModuleItem> item) {}
}
