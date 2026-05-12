---
name: proxima-add-item
description: Use when adding a new item to the Proxima Hammers Fabric-only mod, including Java registration, datagen, recipes, item models, textures, lang keys, tags, creative tab exposure, and verification.
metadata:
  short-description: Add Proxima Hammers items
---

# Proxima Add Item

Use this skill for requests like "add a new hammer", "create a new core", "add an item", or "make a new upgrade item" in this repo.

## Ground Rules

- Fabric only: do not create Forge or NeoForge files/modules.
- Keep namespace exactly `proximahammers` everywhere.
- Keep Java under `io.github.proxima812.proximahammers`.
- Preserve existing hammers, cores, recipes, textures, lang files, generated data, and translations unless the user explicitly asks to remove them.
- Prefer extending the current patterns over adding new registries or frameworks.

## Files To Inspect First

- `common/src/main/java/io/github/proxima812/proximahammers/HammerItems.java`
- `common/src/main/java/io/github/proxima812/proximahammers/HammerItem.java`
- `common/src/main/java/io/github/proxima812/proximahammers/Hammers.java`
- `fabric/src/main/java/io/github/proxima812/proximahammers/fabric/DataGenerators.java`
- `fabric/src/main/java/io/github/proxima812/proximahammers/fabric/HammersFabric.java`
- `common/src/main/resources/assets/proximahammers/textures/item/`
- `common/src/main/resources/assets/proximahammers/lang/`
- `common/src/main/generated/`

## Workflow

1. Choose a lowercase item id with underscores, for example `copper_hammer` or `stability_core`.
2. Register the item in `HammerItems`:
   - hammers use `registerHammer(baseName, ToolMaterial, SizeOption, level)`, which appends `_hammer`.
   - simple item cores use `register("item_id", () -> new Item(DEFAULT_PROPERTIES.apply("item_id")))`.
   - add hammer variants to `HAMMERS`; all items must be in `ITEMS`.
3. Add datagen coverage in `DataGenerators`:
   - `LangGen` for English display names and any new tooltip keys.
   - `ItemModelGen` for item model generation.
   - `RecipeGen` when the item is craftable.
   - `ItemTagsGen` when it should be in hammer/pickaxe/enchantable tags.
4. Add or reuse a texture in `common/src/main/resources/assets/proximahammers/textures/item/<item_id>.png`.
5. Run `.\gradlew.bat :fabric:runDatagen` so generated models, item definitions, recipes, advancements, and tags are refreshed.
6. Check generated JSON for uppercase namespace leaks:
   - no `ProximaHammers:*`
   - no `item.ProximaHammers.*`
   - use `proximahammers:*` and `item.proximahammers.*`
7. Run `.\.agents\hooks\preflight.ps1` before handing work back.

## Common Patterns

Hammer registration:

```java
DeferredResource<Item, HammerItem> COPPER_HAMMER =
        registerHammer("copper", ToolMaterial.IRON, SizeOption.THREE_THREE, 1);
```

Core registration:

```java
DeferredResource<Item, Item> STABILITY_CORE =
        register("stability_core", () -> new Item(DEFAULT_PROPERTIES.apply("stability_core")));
```

Language keys must match the real item registry id:

```json
"item.proximahammers.stability_core": "Stability Core"
```

Item/model JSON references must use lowercase namespace:

```json
"model": "proximahammers:item/stability_core"
```

