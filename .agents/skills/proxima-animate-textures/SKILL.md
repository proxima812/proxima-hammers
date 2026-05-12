---
name: proxima-animate-textures
description: Use when creating, recoloring, converting, or repairing animated item/block textures for the Proxima Hammers Fabric mod, especially PNG texture sheets, .png.mcmeta files, ore/material-colored gradients, improved hammer variants, and checks that Minecraft animated textures are 16px-wide vertical frame strips.
---

# Proxima Animate Textures

## Purpose

Create Minecraft-compatible animated textures for Proxima Hammers while preserving silhouettes, namespace, and resource layout.

## Project Paths

- Item textures: `common/src/main/resources/assets/proximahammers/textures/item/`
- Block textures: `common/src/main/resources/assets/proximahammers/textures/block/`
- Animation metadata: same path as PNG with `.png.mcmeta`
- Use namespace `proximahammers` everywhere.

## Workflow

1. Inspect the source texture first. Check dimensions and whether it is static `16x16` or an animated vertical sheet such as `16x256`.
2. Preserve transparency and silhouette. Use the first 16x16 frame as the base unless the user explicitly asks for a redraw.
3. Generate vertical frame strips. For standard Proxima hammer animations, use 16 frames of 16x16 pixels, producing `16x256`.
4. Add or update `<texture>.png.mcmeta`:

```json
{
  "animation": {
    "frametime": 3,
    "interpolate": true
  }
}
```

5. For material gradients, choose colors from the material/ore identity:
   - coal: black/gray
   - copper: orange with oxidized green accent
   - lapis: deep blue
   - redstone: red/orange glow
   - gold: yellow/gold
   - quartz: off-white/cream
   - obsidian: black/purple
   - amethyst: purple/pink
   - iron: silver/gray
   - diamond: cyan
   - emerald: green
   - netherite: dark gray with muted bronze
6. For improved variants, make the texture visibly upgraded without changing item identity: stronger contrast, brighter rim, small reinforcement marks, or material-specific accent pixels.
7. Verify dimensions with a local image check before finishing.
8. Run `.\.agents\hooks\preflight.ps1` after resource edits.

## Script

Use `scripts/animate_texture.py` when a deterministic 16-frame gradient is enough:

```powershell
python .\.agents\skills\proxima-animate-textures\scripts\animate_texture.py `
  --input common\src\main\resources\assets\proximahammers\textures\item\diamond_hammer.png `
  --output common\src\main\resources\assets\proximahammers\textures\item\diamond_hammer.png `
  --palette "#41c7d9,#b5fff9,#178fa4"
```

The script preserves alpha, uses the first 16x16 frame as the silhouette, writes a 16-frame vertical sheet, and writes `.png.mcmeta`.

## Guardrails

- Do not delete unrelated textures.
- Do not replace generated item/model JSON just to animate a texture.
- Keep `.bat`/shell output ASCII if adding helper launchers.
- If PowerShell displays Russian text as mojibake, do not treat that alone as file corruption; validate encoding with the game/resource load or byte-aware tooling.
