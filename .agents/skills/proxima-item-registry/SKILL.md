---
name: proxima-item-registry
description: Use when modifying, reusing, auditing, or explaining the existing Proxima Hammers item base, including HammerItems, DeferredResource, item tags, recipes, generated assets, translations, and Fabric creative tab registration.
metadata:
  short-description: Work with item registry
---

# Proxima Item Registry

Use this skill when the task is about the existing item base rather than creating a brand-new feature from scratch: reusing an item, changing a recipe, finding where an item is registered, fixing missing names/textures, updating tags, or explaining how items flow into Fabric.

## Current Item Pipeline

1. `HammerItems` defines all item handles as `DeferredResource<Item, ...>`.
2. `Hammers.id(name)` creates `proximahammers:<name>`.
3. Platform entrypoint `HammersFabric` registers every `HammerItems.ITEMS` entry into `BuiltInRegistries.ITEM`.
4. `HammersFabric` adds every `HammerItems.ITEMS` entry to the Fabric creative tab.
5. `DataGenerators` emits generated recipes, advancements, tags, item definitions, item models, and English lang.
6. Static textures and extra translations live under `common/src/main/resources/assets/proximahammers/`.

## Important Files

- `common/src/main/java/io/github/proxima812/proximahammers/HammerItems.java`
- `common/src/main/java/io/github/proxima812/proximahammers/utils/DeferredResource.java`
- `common/src/main/java/io/github/proxima812/proximahammers/HammerTags.java`
- `common/src/main/java/io/github/proxima812/proximahammers/HammerItem.java`
- `fabric/src/main/java/io/github/proxima812/proximahammers/fabric/HammersFabric.java`
- `fabric/src/main/java/io/github/proxima812/proximahammers/fabric/DataGenerators.java`
- `common/src/main/generated/data/proximahammers/recipe/`
- `common/src/main/generated/assets/proximahammers/items/`
- `common/src/main/generated/assets/proximahammers/models/item/`
- `common/src/main/resources/assets/proximahammers/textures/item/`
- `common/src/main/resources/assets/proximahammers/lang/`

## When Fixing Existing Items

- Missing name: verify the item id in `HammerItems`, then check `common/src/main/generated/assets/proximahammers/lang/en_us.json` and extra lang files under resources.
- Missing texture: verify the generated item definition, model file, and matching PNG in `textures/item`.
- Missing crafting: inspect `DataGenerators.RecipeGen`, generated recipe JSON, and generated advancement JSON.
- Missing enchantment/tool behavior: inspect `DataGenerators.ItemTagsGen` and `HammerTags`.
- Missing creative tab entry: `HammersFabric` should already accept all `HammerItems.ITEMS`; verify the item was added to `ITEMS`.

## Namespace Checks

All resource ids must be lowercase:

- Good: `proximahammers:stone_hammer`
- Bad: `ProximaHammers:stone_hammer`
- Good lang key: `item.proximahammers.stone_hammer`
- Bad lang key: `item.ProximaHammers.stone_hammer`

Use PowerShell checks when needed:

```powershell
Get-ChildItem -Path common\src\main\generated,common\src\main\resources,fabric\src\main\generated -Recurse -File -Include *.json |
  Select-String -Pattern 'ProximaHammers' -CaseSensitive
```

## Verification

After edits:

```powershell
.\gradlew.bat :fabric:runDatagen
.\.agents\hooks\preflight.ps1
```

If only reading/explaining behavior, do not mutate generated files; cite the source files that establish the behavior.

