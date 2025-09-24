package com.evilcorp.algorithm.mod;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import com.evilcorp.classes.FileItem;
import java.nio.charset.StandardCharsets;

public class RunGeneratorMod {
    private final int sizeBuffer;
    private final File inputFile;
    private final  BufferedWriter[] writers;

    private static final long SIZE_OF_RUN_IN_BYTES = 1024 * 1024 * 100; // 100 MB

    public RunGeneratorMod (File inputFile, File[] outputFiles, int sizeBuffer) throws IOException {
        this.inputFile = inputFile;
        this.sizeBuffer = sizeBuffer;
        writers = new BufferedWriter[outputFiles.length];
        for (int i = 0; i < outputFiles.length; i++) {
            this.writers[i] = new BufferedWriter(new FileWriter(outputFiles[i]), sizeBuffer);
        }
    }

    public void distributeRuns() throws IOException {

        try(BufferedReader reader = new BufferedReader(new FileReader(inputFile), sizeBuffer)) {
            List<FileItem> buffer = new ArrayList<>();

            int currentWriterIndex = 0;
            long currentSize = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                FileItem item = FileItem.fromLine(line);
                buffer.add(item);
                currentSize += line.getBytes(StandardCharsets.UTF_8).length;

                if (currentSize >= SIZE_OF_RUN_IN_BYTES) {
                    writeBufferToWriter(buffer, writers[currentWriterIndex]);
                    buffer.clear();
                    currentSize = 0;
                    currentWriterIndex = (currentWriterIndex + 1) % this.writers.length;
                }
            }

            if (!buffer.isEmpty()) {
                writeBufferToWriter(buffer, writers[currentWriterIndex]);
            }

        } finally {
            for (BufferedWriter bw : writers) {
                if (bw != null) {
                    bw.close();
                }
            }
        }
    }

    private void writeBufferToWriter(List<FileItem> buffer, BufferedWriter writer) throws IOException {
        buffer.sort(null);
        for (FileItem fi : buffer) {
            writer.write(fi.toString());
            writer.newLine();
        }
        writer.flush();
    }

}
