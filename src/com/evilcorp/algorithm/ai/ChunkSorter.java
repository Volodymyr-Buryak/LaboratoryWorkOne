package com.evilcorp.algorithm.ai;

import java.io.File;
import java.util.List;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Утиліта для розбиття великого файлу на чанки і сортування кожного з них.
 */

public class ChunkSorter {
    private static final int MAX_MEMORY = 100 * 1024 * 1024;

    public static List<File> splitAndSort(File input, File tmpDir) throws IOException {
        List<File> chunks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            List<Record> buffer = new ArrayList<>();
            int currentSize = 0;
            String line;

            while ((line = br.readLine()) != null) {
                Record rec = Record.parse(line);
                buffer.add(rec);
                currentSize += line.length() * 2; // приблизно в байтах (UTF-16)

                if (currentSize >= MAX_MEMORY) {
                    chunks.add(sortAndSave(buffer, tmpDir));
                    buffer.clear();
                    currentSize = 0;
                }
            }
            if (!buffer.isEmpty()) {
                chunks.add(sortAndSave(buffer, tmpDir));
            }
        }
        return chunks;
    }

    private static File sortAndSave(List<Record> buffer, File tmpDir) throws IOException {
        buffer.sort(Comparator.reverseOrder());
        File tempFile = File.createTempFile("chunk", ".txt", tmpDir);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            for (Record r : buffer) {
                bw.write(r.toString());
                bw.newLine();
            }
        }
        return tempFile;
    }
}
