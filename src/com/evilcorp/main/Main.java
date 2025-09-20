package com.evilcorp.main;

import java.util.Map;
import java.util.HashMap;
import java.nio.file.Path;

import com.evilcorp.classes.SortAlgorithm;
import com.evilcorp.algorithm.ExternalSortAi;
import com.evilcorp.algorithm.BalancedSortingMod;
import com.evilcorp.algorithm.BalancedSortingNoMod;
import com.evilcorp.filegenerator.LargeFileGenerator;

public class Main {
    private static final int SIZE_ONE_MB = 1024 * 1024;
    private static final Path PATH_DIRECTORY_INPUT = Path.of("D:\\java_projects\\LaboratoryWorkOne\\src\\com\\evilcorp\\input");
    private static final Path PATH_DIRECTORY_OUTPUT = Path.of("D:\\java_projects\\LaboratoryWorkOne\\src\\com\\evilcorp\\output");

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("No arguments provided. Please specify arguments.");
            return;
        }

        long memInMB = Runtime.getRuntime().maxMemory() / (SIZE_ONE_MB);

        System.out.println("Application started.");
        System.out.println("Max memory (MB): " + memInMB);

        Map<Command, String> commandArgs = createHashMapArgs(args);

        try {

            if (commandArgs.containsKey(Command.GENERATE)) {
                int sizeFile =  Integer.parseInt(commandArgs.get(Command.GENERATE));
                LargeFileGenerator fg = new LargeFileGenerator(PATH_DIRECTORY_INPUT, sizeFile);
                fg.createLargeFile();
                return;
            }

            SortAlgorithm sortAlgorithm = switch (commandArgs.get(Command.SORT)) {
                case "nomod" -> new BalancedSortingNoMod(PATH_DIRECTORY_INPUT, PATH_DIRECTORY_OUTPUT);
                case "mod" -> new BalancedSortingMod(PATH_DIRECTORY_INPUT, PATH_DIRECTORY_OUTPUT);
                case "ai" -> new ExternalSortAi(PATH_DIRECTORY_INPUT, PATH_DIRECTORY_OUTPUT);
                default -> throw new IllegalStateException("Unexpected value: " + commandArgs.get(Command.SORT));
            };

            long start = System.nanoTime();

            sortAlgorithm.sortFile(commandArgs.get(Command.INPUT), commandArgs.get(Command.OUTPUT));

            long end = System.nanoTime();
            long duration = end - start;

            System.out.println("Execution time: " + duration + " ns");
            System.out.println("Execution time: " + duration / 1_000_000.0 + " ms");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Map<Command, String> createHashMapArgs(String[] args){
        HashMap<Command, String> mapArgs = new HashMap<>();
        for (String arg : args) {
            String[] parts = arg.split("=", 2);
            if (parts.length == 2) {
                mapArgs.put(Command.fromKey(parts[0]), parts[1]);
            } else {
                throw new IllegalArgumentException("Invalid argument format: " + arg);
            }
        }
        return mapArgs;
    }
}
