package com.evilcorp.algorithm;

import java.io.File;
import java.util.List;
import java.nio.file.Path;
import java.io.IOException;

import com.evilcorp.algorithm.ai.Merger;
import com.evilcorp.classes.SortAlgorithm;
import com.evilcorp.algorithm.ai.ChunkSorter;

public class ExternalSortAi implements SortAlgorithm {
    private static final String TEMP_DIR = "tmp_chunks";

    private final Path pathInputFile;
    private final Path pathOutputFile;

    public ExternalSortAi(Path inputFile, Path outputFile) {
        this.pathInputFile = inputFile;
        this.pathOutputFile = outputFile;
    }

    @Override
    public void sortFile(String inputFileName, String outputFileName) throws IOException {
        File tmpDir = new File(TEMP_DIR);

        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }

        List<File> chunks = null;

        try {
            chunks = ChunkSorter.splitAndSort(new File(this.pathInputFile.toFile(), inputFileName), tmpDir);
            Merger.mergeSortedChunks(chunks, new File(this.pathOutputFile.toFile(), outputFileName));
            System.out.println("Сортування завершено.");
        } finally {
            if (chunks != null) {
                for (File f : chunks) {
                    if (f.exists()) {
                        f.delete();
                    }
                }
            }
            if (tmpDir.exists() && tmpDir.isDirectory()) {
                File[] leftovers = tmpDir.listFiles();
                if (leftovers != null) {
                    for (File f : leftovers) {
                        f.delete();
                    }
                }
                tmpDir.delete();
            }
        }

    }
}
