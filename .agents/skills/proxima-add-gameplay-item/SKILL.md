---
name: proxima-add-gameplay-item
description: Use when adding new gameplay content to the Proxima Hammers Fabric-only mod, including items, blocks, block items, tool materials, hammer variants, recipes, tags, datagen, translations, textures, creative tab ordering, menus/screens, and item or block functionality.
---

# Proxima Add Gameplay Item

## Purpose

Add new functional content to Proxima Hammers using the existing Fabric-only architecture. Prefer extending current registries and datagen over inventing parallel systems.

## Inspect First

- `common/src/main/java/io/github/proxima812/proximahammers/HammerItems.java`
- `common/src/main/java/io/github/proxima812/proximahammers/HammerBlocks.java`
- `common/src/main/java/io/github/proxima812/proximahammers/HammerMenus.java`
- `common/src/main/java/io/github/proxima812/proximahammers/HammerItem.java`
- `fabric/src/main/java/io/github/proxima812/proximahammers/fabric/DataGenerators.java`
- `fabric/src/main/java/io/github/proxima812/proximahammers/fabric/HammersFabric.java`
- `fabric/src/main/java/io/github/proxima812/proximahammers/fabric/HammersFabricClient.java`
- `common/src/main/resources/assets/proximahammers/`
- `common/src/main/generated/`

## Rules

- Fabric only. Do not add Forge or NeoForge modules.
- Keep Java packages under `io.github.proxima812.proximahammers`.
- Keep ids lowercase and namespaced as `proximahammers`.
- Preserve existing hammers, recipes, models, textures, translations, and generated data unless explicitly asked to remove them.
- Keep creative tab ordering intentional. For variants, register them next to their base item.
- Keep English and Russian localization only unless the user asks for more languages.

## Item Workflow

1. Choose a stable item id, for example `stability_core` or `improved_diamond_hammer`.
2. Register it in `HammerItems`.
   - Hammers should be added to `HAMMERS`.
   - Every item or block item should be added to `ITEMS`.
3. Add behavior in a dedicated class when logic grows beyond simple registration.
4. Add datagen coverage in `DataGenerators`:
   - language
   - item model
   - recipe
   - item tags
5. Add texture under `common/src/main/resources/assets/proximahammers/textures/item/`.
6. Run `.\gradlew.bat :fabric:runDatagen`.
7. Run `.\.agents\hooks\preflight.ps1`.

## Block Workflow

1. Register the block in `HammerBlocks`.
2. Register its `BlockItem` in `HammerItems`.
3. Register the block in `HammersFabric` before item registration.
4. Add assets/resources:
   - `assets/proximahammers/blockstates/<id>.json`
   - `assets/proximahammers/models/block/<id>.json`
   - `assets/proximahammers/models/item/<id>.json`
   - `assets/proximahammers/textures/block/<id>*.png`
   - loot table under `data/proximahammers/loot_table/blocks/<id>.json`
   - mining tag under `data/minecraft/tags/block/mineable/` when needed
5. Add lang keys for both item and container names if it opens UI.

## Menu/Screen Workflow

Use this when a block opens slots or a custom interface.

1. Register the `MenuType` in `HammerMenus`.
2. Register menus in `HammersFabric`.
3. Register screens in `HammersFabricClient`.
4. Put shared menu logic under `common/src/main/java/.../menu/`.
5. Put client screen code under `common/src/main/java/.../client/`.
6. Restrict slots with `Slot#mayPlace` when the user describes slot roles.
7. If data must persist in-world, add a block entity instead of using only `SimpleContainer`.

## Localization

- Datagen owns English: `common/src/main/generated/assets/proximahammers/lang/en_us.json`.
- Manual Russian lives in `common/src/main/resources/assets/proximahammers/lang/ru_ru.json`.
- Keep visible item names, container titles, tooltips, and controls translated.

## Verification

Always run:

```powershell
.\gradlew.bat :fabric:runDatagen
.\.agents\hooks\preflight.ps1
```

Also check generated JSON for namespace mistakes:

```powershell
Get-ChildItem -Path common\src\main\generated,common\src\main\resources -Recurse -File -Include *.json |
  Select-String -Pattern 'ProximaHammers' -CaseSensitive
```
