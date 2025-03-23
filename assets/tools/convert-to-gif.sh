#!/bin/sh
set -xeuC
out=$1
shift 1
find $1 -name "*.scaled.*" -exec rm "{}" \;
for img in $1/*; do
  ffmpeg -i "$img" -vf "scale=80:80,setsar=1:1" $(echo "$img" | sed "s/.png/.scaled.png/g");
done;
ffmpeg -pattern_type glob -i "$1/*.scaled.png" -vf palettegen=reserve_transparent=1 "$out"-palette.png
ffmpeg -framerate 30 -pattern_type glob -i "$1/*.scaled.png" -i "$out"-palette.png -lavfi paletteuse=alpha_threshold=128 -gifflags -offsetting "$out".gif