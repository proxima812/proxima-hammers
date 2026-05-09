# Proxima Hammers Agent Guide

## Project Identity

- Mod name: Proxima Hammers
- Mod id: `proximahammers`
- Version: `1.0`
- GitHub owner: `proxima812`
- Deployment target: Modrinth only
- Java package root: `io.github.proxima812.proximahammers`

## Repository Layout

- `common/` contains shared gameplay code, generated data, assets, textures, lang files, recipes, tags, and item models.
- `fabric/` contains Fabric entrypoints, Fabric data generation, and Fabric mixins.
- `neoforge/` contains NeoForge entrypoints, events, and NeoForge mixins.
- `.agents/` contains local agent tools, hooks, and project-specific skills.

## Development Rules

- Preserve existing hammer modules, recipes, models, textures, descriptions, and translations unless the user explicitly asks to remove them.
- Keep the namespace consistent: use `proximahammers` for resource ids, lang keys, recipe ids, tags, mixin filenames, and generated assets.
- Keep Java imports and packages under `io.github.proxima812.proximahammers`.
- Do not add any non-Modrinth release deployment. Publishing is Modrinth only.
- Prefer small, focused changes and run the preflight hook before handing work back.

## Useful Commands

```powershell
.\gradlew.bat build
.\gradlew.bat :fabric:runDatagen
.\.agents\hooks\preflight.ps1
```

## Release Notes

Use `CHANGELOG.md` for user-facing release notes. Keep the current release line under `Proxima Hammers 1.0` until the version changes.
