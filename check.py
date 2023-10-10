def read_file_lines(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()
    return lines


def rearrange_lines(lines):
    rearranged_lines = []
    for line in lines:
        parts = line.strip().split(' ')
        rearranged_lines.append(f"{parts[1]} {parts[0]}")
    return rearranged_lines


def compare_files(expected, submitted):
    expected_lines = sorted(read_file_lines(expected))
    submitted_lines = rearrange_lines(read_file_lines(submitted))
    submitted_lines = sorted(submitted_lines)
    if len(expected_lines) != len(submitted_lines):
        return False
    for i in range(len(expected_lines)):
        linef1 = expected_lines[i]
        linef2 = submitted_lines[i]
        if str(linef1).strip() != str(linef2).strip():
            return False
    return True


if __name__ == "__main__":
    expected_path = "expected.txt"
    submitted_path = "submitted.txt"

    if compare_files(expected=expected_path, submitted=submitted_path):
        print("Files are equal.")
    else:
        print("Files are not equal.")