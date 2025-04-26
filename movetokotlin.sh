#!/bin/bash
clear
# Define source and destination directories
src="$HOME/IdeaProjects/BingoNet/common/src/main/java"
dest="$HOME/IdeaProjects/BingoNet/common/src/main/kotlin"

# Find all .kt files in the source directory
find "$src" -type f -name "*.kt" | while read -r file; do
    # Create the destination directory structure
    relative_path="${file#$src/}"  # Get the relative path
    target_dir="$dest/$(dirname "$relative_path")"  # Target directory in Kotlin
    target_file="$target_dir/$(basename "$file")"

    # Create the target directory if it doesn't exist
    mkdir -p "$target_dir"

    # First copy the file normally
    cp "$file" "$target_file"

    # Then remove the original file
    rm "$file"

    # Now use git mv with --force to record the move
    echo "git mv --force \"$file\" \"$target_file\""

    git add "$target_file"
done

# Remove empty directories in the source path
find "$src" -type d -empty -delete