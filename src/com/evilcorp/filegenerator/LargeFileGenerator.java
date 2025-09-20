package com.evilcorp.filegenerator;

import java.io.File;
import java.nio.file.Path;
import java.util.Random;
import java.io.IOException;
import java.time.LocalDateTime;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.time.format.DateTimeFormatter;

public final class LargeFileGenerator {
    private static final int BUFFER_SIZE = 128 * 1024;
    private static final int CHARACTER_LENGTH = 20;
    private static final int MAX_FILE_SIZE_MB = 1024;
    private static final String DATE_DELIMITER = "-";
    private static final int BYTES_IN_MB = 1024 * 1024;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final Path directory;
    private final Random random;
    private final int sizeFileInMegabytes;

    public LargeFileGenerator(Path directory, int sizeFileInMegabytes) {
        if (directory == null) {
            throw new IllegalArgumentException("File object cannot be null.");
        }
        if (sizeFileInMegabytes <= 0 || sizeFileInMegabytes > MAX_FILE_SIZE_MB) {
            throw new IllegalArgumentException("File size must be greater than zero and not exceed 1 GB.");
        }
        this.directory = directory;
        this.random = new Random();
        this.sizeFileInMegabytes = sizeFileInMegabytes;
    }

    public static long convertMbToBytes(int sizeInMb) {
        return (long) sizeInMb * BYTES_IN_MB;
    }

    private File createFileInDirectory(Path path) throws IOException {
        File file = new File(path.toFile(), "test_file_" + sizeFileInMegabytes + ".txt");
        System.out.println("Creating file of size " + sizeFileInMegabytes + " MB in  " + file.getAbsolutePath());
        System.out.println("Please wait, this may take a while...");
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete existing file at: " + file.getAbsolutePath());
            }
        }
        if (!file.createNewFile()) {
            throw new IOException("Failed to create file at: " + file.getAbsolutePath());
        }
        return file;
    }

    public void createLargeFile() throws IOException {
        File inputFile = createFileInDirectory(directory);
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        StringBuilder record = new StringBuilder();
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(inputFile), BUFFER_SIZE)) {
            long writtenBytes = 0;
            while (writtenBytes < convertMbToBytes(sizeFileInMegabytes)) {
                byte[] recordBytes = composeKeySymbolDate(date, record);
                bufferedOutputStream.write(recordBytes);
                writtenBytes += recordBytes.length;
                record.setLength(0);
            }
        }
        System.out.println("File created");
    }

    private byte[] composeKeySymbolDate (String date, StringBuilder record) {
        record.append(random.nextInt(100000));
        record.append(DATE_DELIMITER);
        record.append(generateCharacters(CHARACTER_LENGTH));
        record.append(DATE_DELIMITER);
        record.append(date);
        record.append('\n');
        return record.toString().getBytes();
    }

    public String generateCharacters (int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }
}