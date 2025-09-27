package com.evilcorp.algorithm;

import java.io.File;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.StandardCopyOption;
import com.evilcorp.classes.SortAlgorithm;
import com.evilcorp.algorithm.mod.RunGeneratorMod;
import com.evilcorp.algorithm.mod.BalancedMergerMod;

public class BalancedSortingMod implements SortAlgorithm {
    private final Path pathInputFile;
    private final Path pathOutputFile;
    private final Path pathTempDir;

    private static final int N = 9; // Number of temporary files
    private static final long SIZE_SECTOR_IN_BYTES = 4096; // 4 KB

    public BalancedSortingMod(Path inputFile, Path outputFile) throws IOException {
        this.pathInputFile = inputFile;
        this.pathOutputFile = outputFile;
        this.pathTempDir = Files.createTempDirectory(outputFile, "sort");
    }

    @Override
    public void sortFile(String inputFileName, String outputFileName) throws IOException {

        File[] sources = new File[N];
        for (int i = 0; i < N; i++) {
            sources[i] = new File(this.pathTempDir.toFile(), "source_" + i + ".txt");
        }

        int sizeBuffer = getSizeBuffer();
        RunGeneratorMod distributor = new RunGeneratorMod(new File(this.pathInputFile.toString(), inputFileName), sources, sizeBuffer);
        distributor.distributeRuns();

        System.out.println("series are ready");

        int l = N;
        System.out.println("Initial number of series: " + l);

        while (l > 1) {
            File[] targets = new File[N];
            for (int i = 0; i < N; i++) {
                targets[i] = new File(pathTempDir.toFile(), "target_" + System.currentTimeMillis() + "_" + i + ".txt");
            }

            BalancedMergerMod merger = new BalancedMergerMod(N, sizeBuffer);
            l = merger.merge(sources, targets);
            System.out.println("Number of series after merger: " + l);

            // Видаляємо старі файли-джерела
            deleteFiles(sources);
            sources = targets;
        }

        File result = new File(this.pathOutputFile.toString(), outputFileName);
        Files.move(sources[0].toPath(), result.toPath(), StandardCopyOption.REPLACE_EXISTING);
        deleteFiles(sources);
        Files.deleteIfExists(pathTempDir);
        System.out.println("Sorting is complete. Result in: " + result.getAbsolutePath());
    }

    private int getSizeBuffer() {
        long maxSizeMemory = Runtime.getRuntime().maxMemory();
        long sizeForOneBuffer = (long) ((0.1 * maxSizeMemory) / (N * 2L));
        long remainder = sizeForOneBuffer % SIZE_SECTOR_IN_BYTES;

        if (remainder != 0) {
            sizeForOneBuffer += (SIZE_SECTOR_IN_BYTES - remainder);
        }

        return Math.toIntExact(sizeForOneBuffer / 2);
    }

    private void deleteFiles(File[] files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

}
