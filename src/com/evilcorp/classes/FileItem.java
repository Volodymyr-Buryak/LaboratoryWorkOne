package com.evilcorp.classes;

public record FileItem(int key, String lineFull) implements Comparable<FileItem> {

    public static FileItem fromLine(String line) {
        return new FileItem(parseKey(line), line);
    }

    public static int parseKey(String line) {
        int dash = line.indexOf('-');
        if (dash <= 0) {
            throw new IllegalArgumentException("Bad line, no '-' : " + line);
        }
        try {
            return Integer.parseInt(line.substring(0, dash));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad numeric key in line: " + line, e);
        }
    }

    public int getKey() {
        return key;
    }

    @Override
    public int compareTo(FileItem o) {
        return Integer.compare(o.key, this.key);
    }

    @Override
    public String toString() {
        return  lineFull;
    }
}
