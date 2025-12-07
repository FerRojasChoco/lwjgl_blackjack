# -----------------------------------------------------------
# Hard-coded OBJ file path — EDIT THIS
# -----------------------------------------------------------
OBJ_FILE_PATH = r"fuckblender\clubs_10.obj"   # <-- put your .obj path here


def clean_obj_file(input_path):
    """
    Removes all OBJ lines that begin with 'l ' (polyline commands).
    Overwrites the same file.
    """
    cleaned_lines = []

    with open(input_path, "r", encoding="utf-8") as f:
        for line in f:
            # Skip lines that start with 'l '
            if not line.strip().startswith("l "):
                cleaned_lines.append(line)

    with open(input_path, "w", encoding="utf-8") as f:
        f.writelines(cleaned_lines)

    print(f"Cleaned OBJ file: {input_path}")


# -----------------------------------------------------------
# Run the cleaning
# -----------------------------------------------------------
clean_obj_file(OBJ_FILE_PATH)
