#!/usr/bin/env python3
import os
import re
import sys

ROOT = "android-rockhound/app/src/main/java/com/rork/rockscout"
UTIL_IMPORT = "import com.rork.rockscout.ui.components.noAutoFocus"
FIELD_NAMES = ["OutlinedTextField", "BasicTextField", "TextField"]

def find_balanced_call(text, start_idx):
    """Return (open_paren_index, close_paren_index) for the call starting at start_idx."""
    i = start_idx
    while i < len(text) and text[i] != '(':
        i += 1
    if i >= len(text):
        return None
    open_idx = i
    depth = 1
    i += 1
    in_string = False
    string_char = None
    while i < len(text) and depth > 0:
        c = text[i]
        if in_string:
            if c == '\\':
                i += 1
            elif c == string_char:
                in_string = False
        else:
            if c in ('"', "'"):
                in_string = True
                string_char = c
            elif c == '(':
                depth += 1
            elif c == ')':
                depth -= 1
                if depth == 0:
                    return (open_idx, i)
        i += 1
    return None

def parse_value(text, start):
    """Parse the value of a parameter starting at start; return the substring."""
    i = start
    while i < len(text) and text[i].isspace():
        i += 1
    start = i
    if i >= len(text):
        return ""

    # String literal
    if text[i] in ('"', "'"):
        qc = text[i]
        i += 1
        while i < len(text):
            if text[i] == '\\':
                i += 1
            elif text[i] == qc:
                i += 1
                break
            i += 1
        return text[start:i]

    # Lambda block
    if text[i] == '{':
        brace_depth = 1
        i += 1
        while i < len(text) and brace_depth > 0:
            c = text[i]
            if c in ('"', "'"):
                qc = c
                i += 1
                while i < len(text) and text[i] != qc:
                    if text[i] == '\\':
                        i += 1
                    i += 1
                i += 1
                continue
            if c == '{':
                brace_depth += 1
            elif c == '}':
                brace_depth -= 1
            i += 1
        return text[start:i]

    # General expression: stop at top-level comma or closing paren
    depth = 0
    while i < len(text):
        c = text[i]
        if c in ('"', "'"):
            qc = c
            i += 1
            while i < len(text) and text[i] != qc:
                if text[i] == '\\':
                    i += 1
                i += 1
            i += 1
            continue
        if c == '(':
            depth += 1
        elif c == ')':
            if depth == 0:
                break
            depth -= 1
        elif c == ',' and depth == 0:
            break
        elif c == '{' and depth == 0:
            # Lambda as a value without preceding '='? shouldn't happen here
            brace_depth = 1
            j = i + 1
            while j < len(text) and brace_depth > 0:
                c2 = text[j]
                if c2 in ('"', "'"):
                    qc = c2
                    j += 1
                    while j < len(text) and text[j] != qc:
                        if text[j] == '\\':
                            j += 1
                        j += 1
                    j += 1
                    continue
                if c2 == '{':
                    brace_depth += 1
                elif c2 == '}':
                    brace_depth -= 1
                j += 1
            i = j
            continue
        i += 1
    return text[start:i]

def add_no_auto_focus_to_call(call_text):
    """Modify a single call's argument list to add .noAutoFocus() to the modifier."""
    # Look for an existing modifier assignment
    match = re.search(r"modifier\s*=\s*", call_text)
    if match:
        start = match.end()
        value = parse_value(call_text, start)
        if value:
            new_value = value + ".noAutoFocus()"
            return call_text[:start] + new_value + call_text[start + len(value):]

    # No modifier found; insert one before the closing paren.
    # Find the last top-level comma to keep formatting clean.
    last_comma = -1
    depth = 1
    in_string = False
    string_char = None
    for idx in range(1, len(call_text) - 1):
        c = call_text[idx]
        if in_string:
            if c == '\\':
                continue
            if c == string_char:
                in_string = False
        else:
            if c in ('"', "'"):
                in_string = True
                string_char = c
            elif c == '(':
                depth += 1
            elif c == ')':
                depth -= 1
            elif c == ',' and depth == 1:
                last_comma = idx

    if last_comma >= 0:
        insert_pos = last_comma + 1
        return (
            call_text[:insert_pos]
            + "\n                    modifier = Modifier.noAutoFocus(),"
            + call_text[insert_pos:]
        )
    else:
        # Single argument or no arguments
        return (
            call_text[:1]
            + "\n                    modifier = Modifier.noAutoFocus()"
            + call_text[1:]
        )

def process_file(path):
    with open(path, "r") as f:
        text = f.read()

    original = text
    replacements = []

    for name in FIELD_NAMES:
        pattern = re.compile(rf"\b{name}\s*\(")
        for m in pattern.finditer(text):
            result = find_balanced_call(text, m.start())
            if result is None:
                continue
            open_idx, close_idx = result
            call_text = text[open_idx : close_idx + 1]
            new_call_text = add_no_auto_focus_to_call(call_text)
            if new_call_text != call_text:
                replacements.append((open_idx, close_idx + 1, new_call_text))

    if not replacements:
        return False

    # Apply replacements from right to left so earlier indices remain valid.
    replacements.sort(key=lambda x: x[0], reverse=True)
    for start, end, new_text in replacements:
        text = text[:start] + new_text + text[end:]

    if UTIL_IMPORT not in text:
        lines = text.split("\n")
        insert_after = -1
        for i, line in enumerate(lines):
            if line.startswith("import "):
                insert_after = i
        if insert_after >= 0:
            lines.insert(insert_after + 1, UTIL_IMPORT)
        else:
            lines.insert(0, UTIL_IMPORT)
        text = "\n".join(lines)

    if text != original:
        with open(path, "w") as f:
            f.write(text)
        return True
    return False

if __name__ == "__main__":
    changed = []
    for root, dirs, files in os.walk(ROOT):
        for file in files:
            if file.endswith(".kt"):
                path = os.path.join(root, file)
                if process_file(path):
                    changed.append(path)
    for path in changed:
        print(path)
    print(f"Modified {len(changed)} files")
