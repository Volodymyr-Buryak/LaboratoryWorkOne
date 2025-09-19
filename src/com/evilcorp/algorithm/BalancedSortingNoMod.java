package com.evilcorp.algorithm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.evilcorp.algorithm.nomod.BalancedMergerNoMod;
import com.evilcorp.algorithm.nomod.RunGeneratorNoMod;
import com.evilcorp.classes.SortAlgorithm;

import static com.evilcorp.algorithm.nomod.BalancedMergerNoMod.countTotalRuns;

public class BalancedSortingNoMod implements SortAlgorithm {
    private final Path pathInputFile;
    private final Path pathOutputFile;
    private final Path pathTempDir;

    private static final int N = 9; // Number of temporary files

    public BalancedSortingNoMod(Path inputFile, Path outputFile) throws IOException {
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

        RunGeneratorNoMod distributor = new RunGeneratorNoMod(new File(this.pathInputFile.toString(), inputFileName) , sources);
        distributor.distributeRuns();

        int l = countTotalRuns(sources);
        System.out.println("Початкова кількість серій: " + l);

        while (l > 1) {
            File[] targets = new File[N];
            for (int i = 0; i < N; i++) {
                targets[i] = new File(pathTempDir.toFile(), "target_" + System.currentTimeMillis() + "_" + i + ".txt");
            }

            BalancedMergerNoMod merger = new BalancedMergerNoMod(N);
            l = merger.merge(sources, targets);
            System.out.println("Кількість серій після злиття: " + l);

            // Видаляємо старі файли-джерела
            deleteFiles(sources);
            sources = targets;
        }

        File result = new File(this.pathOutputFile.toString(), outputFileName);
        Files.move(sources[0].toPath(), result.toPath(), StandardCopyOption.REPLACE_EXISTING);
        deleteFiles(sources);
        Files.deleteIfExists(pathTempDir);
        System.out.println("Сортування звершене. Результат в: " + result.getAbsolutePath());
    }

    private void deleteFiles(File[] files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
