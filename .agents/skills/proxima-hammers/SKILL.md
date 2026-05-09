# Proxima Hammers Mod Development

Use this skill when changing gameplay, assets, translations, publishing, or build configuration in this repository.

## Identity

- Name: Proxima Hammers
- Mod id: `proximahammers`
- Version: `1.0`
- GitHub: `proxima812/ProximaHammers`
- Deploy target: Modrinth only

## Guardrails

1. Preserve all existing modules: base hammers, impact hammers, reinforced hammers, reinforced impact hammers, destructor hammers, cores, repair recipe, tags, models, textures, and translations.
2. Keep all resource identifiers under `proximahammers`.
3. Keep Java under `io.github.proxima812.proximahammers`.
4. Do not reintroduce the old mod name, old mod id, old archive id, or any non-Modrinth publishing target.
5. When adding items, update registration, recipes, generated models/items, lang keys, textures, tags, and data generation together.

## Verification

Run:

```powershell
.\.agents\hooks\preflight.ps1
```

If the Gradle build is too slow while iterating, first run targeted checks, then finish with the full preflight before delivery.
