#!/usr/bin/env python3
import argparse
from pathlib import Path

from PIL import Image


def parse_color(value):
    value = value.strip().lstrip("#")
    if len(value) != 6:
        raise ValueError(f"Expected RRGGBB color, got {value!r}")
    return tuple(int(value[index:index + 2], 16) for index in (0, 2, 4))


def lerp(a, b, t):
    return round(a + (b - a) * t)


def palette_color(colors, t):
    t %= 1.0
    scaled = t * len(colors)
    index = int(scaled)
    local = scaled - index
    a = colors[index % len(colors)]
    b = colors[(index + 1) % len(colors)]
    return tuple(lerp(a[channel], b[channel], local) for channel in range(3))


def animate_texture(input_path, output_path, colors, frames, frametime):
    source = Image.open(input_path).convert("RGBA")
    base = source.crop((0, 0, 16, 16))
    output = Image.new("RGBA", (16, 16 * frames), (0, 0, 0, 0))

    for frame in range(frames):
        frame_image = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        for y in range(16):
            for x in range(16):
                red, green, blue, alpha = base.getpixel((x, y))
                if alpha == 0:
                    continue

                luma = ((red * 0.2126) + (green * 0.7152) + (blue * 0.0722)) / 255.0
                shade = max(0.08, min(1.0, luma * 1.35))
                tint = palette_color(colors, ((x + y) / 30.0) + (frame / frames))
                frame_image.putpixel((x, y), tuple(min(255, int(channel * shade)) for channel in tint) + (alpha,))

        output.paste(frame_image, (0, frame * 16))

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output.save(output_path)
    output_path.with_suffix(output_path.suffix + ".mcmeta").write_text(
        '{\n  "animation": {\n    "frametime": %d,\n    "interpolate": true\n  }\n}\n' % frametime,
        encoding="ascii",
    )


def main():
    parser = argparse.ArgumentParser(description="Create a 16-frame Minecraft animated texture sheet.")
    parser.add_argument("--input", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument("--palette", required=True, help='Comma-separated colors, for example "#41c7d9,#b5fff9,#178fa4"')
    parser.add_argument("--frames", type=int, default=16)
    parser.add_argument("--frametime", type=int, default=3)
    args = parser.parse_args()

    colors = [parse_color(color) for color in args.palette.split(",")]
    if len(colors) < 2:
        raise ValueError("Palette must contain at least two colors")

    animate_texture(args.input, args.output, colors, args.frames, args.frametime)


if __name__ == "__main__":
    main()
