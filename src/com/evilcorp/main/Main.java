package com.evilcorp.main;

import java.nio.file.Path;

import com.evilcorp.algorithm.BalancedSortingMod;
import com.evilcorp.algorithm.BalancedSortingNoMod;
import com.evilcorp.algorithm.ExternalSortAi;
import com.evilcorp.classes.SortAlgorithm;
import com.evilcorp.filegenerator.LargeFileGenerator;

public class Main {
    private static final String ARG_GENERATE = "fg";
    private static final String ARG_SORT = "fs";

    private static final Path PATH_DIRECTORY_INPUT = Path.of("D:\\java_projects\\LaboratoryWorkOne\\src\\com\\evilcorp\\input");
    private static final Path PATH_DIRECTORY_OUTPUT = Path.of("D:\\java_projects\\LaboratoryWorkOne\\src\\com\\evilcorp\\output");

    public static void main(String[] args) {

        System.out.println("Application started.");
        System.out.println(Runtime.getRuntime().maxMemory());

        if (args.length == 0) {
            System.out.println("No arguments provided.");
            return;
        }

        try {

            String command = args[0].split("=")[0];

            switch (command) {
                case ARG_GENERATE -> {
                    int fileSize = Integer.parseInt(args[0].split("=")[1]);
                    LargeFileGenerator largeFileGenerator = new LargeFileGenerator(PATH_DIRECTORY_INPUT, fileSize);
                    largeFileGenerator.createLargeFile();
                }
                case ARG_SORT -> {

                    if (args.length < 3) {
                        System.out.println("Short arguments for sorting.");
                        return;
                    }

                    String algorithmType = args[0].split("=")[1];
                    String inputFile = args[1].split("=")[1];
                    System.out.println(inputFile);
                    String outputFile = args[2].split("=")[1];

                    System.out.println(algorithmType);

                    SortAlgorithm algorithm;

                    if (algorithmType.equals("mod")) {
                        algorithm = new BalancedSortingMod(PATH_DIRECTORY_INPUT, PATH_DIRECTORY_OUTPUT);
                    } else if (algorithmType.equals("nomod")) {
                        algorithm = new BalancedSortingNoMod(PATH_DIRECTORY_INPUT, PATH_DIRECTORY_OUTPUT);
                    } else if (algorithmType.equals("ai")) {
                        algorithm = new ExternalSortAi(PATH_DIRECTORY_INPUT, PATH_DIRECTORY_OUTPUT);
                    }  else {
                        System.out.println("Unknown sorting algorithm: " + algorithmType);
                        return;
                    }

                    algorithm.sortFile(inputFile, outputFile);
                    System.out.println("Sorting completed successfully.");
                }
                default -> System.out.println("Unknown command: " + command);
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

    }
}
