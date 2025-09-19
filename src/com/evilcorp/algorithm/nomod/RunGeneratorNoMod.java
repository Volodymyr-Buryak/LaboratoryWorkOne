package com.evilcorp.algorithm.nomod;

import com.evilcorp.classes.FileItem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RunGeneratorNoMod {
    private final File inputFile;
    private final FileWriter[] writers;

    public RunGeneratorNoMod(File inputFile, File[] outputFiles) throws IOException {
        this.inputFile = inputFile;
        this.writers = new FileWriter[outputFiles.length];

        for (int i = 0; i < outputFiles.length; i++) {
            this.writers[i] = new FileWriter(outputFiles[i]);
        }
    }

    public void distributeRuns() throws IOException {
        try (FileReader reader = new FileReader(this.inputFile)) {
            StringBuilder stringBuilder = new StringBuilder();
            int currentWriterIndex = 0;
            int prevValue = Integer.MIN_VALUE;

            int ch;

            while ((ch = reader.read()) != -1) {
                char character = (char) ch;
                if (character == '\n' || character == '\r') {
                    FileItem fileItem = FileItem.fromLine(stringBuilder.toString());
                    int currentValue = fileItem.getKey();
                    if (currentValue > prevValue && prevValue != Integer.MIN_VALUE) {
                        currentWriterIndex = (currentWriterIndex + 1) % this.writers.length;
                    }
                    this.writers[currentWriterIndex].write(fileItem + "\n");
                    prevValue = currentValue;
                    stringBuilder.setLength(0);
                } else {
                    stringBuilder.append(character);
                }
            }

        } finally {
            for (FileWriter writer : this.writers) {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

}
