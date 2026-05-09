package io.github.proxima812.proximahammers;

import net.minecraft.resources.Identifier;
import io.github.proxima812.proximahammers.config.SimpleJsonConfig;
import io.github.proxima812.proximahammers.utils.XPlatShim;

import java.util.ServiceLoader;

public class Hammers {
    public static final String MOD_ID = "proximahammers";

    public static final XPlatShim XPLAT = ServiceLoader.load(XPlatShim.class).findFirst().orElseThrow();

    public static void init() {
        SimpleJsonConfig.INSTANCE.load();
        HammerItems.init();
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
}
