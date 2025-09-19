package com.evilcorp.algorithm.nomod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.evilcorp.classes.FileItem;

public class BalancedMergerNoMod {
    private final int N;

    public BalancedMergerNoMod(int n) {
        this.N = n;
    }

    public int merge(File[] sources, File[] targets) throws IOException {
        // Ініціалізація читачів
        FileReader[] readers = new FileReader[N];
        // Зберігаємо поточні елементи з кожного джерела
        FileItem[] currentItems = new FileItem[N];
        // Флаги наявності даних у джерелах
        boolean[] hasMoreData = new boolean[N];

        // кількість активних джерел
        int k1 = 0;

        for (int i = 0; i < N; i++) {
            if (sources[i].exists() && sources[i].length() > 0) {
                readers[i] = new FileReader(sources[i]);
                currentItems[i] = readNextItem(readers[i]);
                hasMoreData[i] = (currentItems[i] != null);
                if (hasMoreData[i]) {
                    k1++;
                }
            } else {
                readers[i] = null;
                currentItems[i] = null;
                hasMoreData[i] = false;
            }
        }

        if (k1 == 0) {
            return 0;
        }

        // Ініціалізація приймача
        FileWriter[] writers = new FileWriter[N];
        for (int i = 0; i < N; i++) {
            writers[i] = new FileWriter(targets[i]);
        }

        // Масив індексів активних джерел
        int[] t = new int[k1];
        for (int i = 0, j = 0; i < N; i++) {
            if (hasMoreData[i]) {
                t[j++] = i;
            }
        }

        // Лічильник серій
        int L = 0;
        // Індекс поточного приймача
        int j = 0;

        while (k1 > 0) {
            L++;
            // Кількість активних джерел на початок слияния серії
            int k2 = k1;

            while (k2 > 0) {
                int maxIndex = 0;
                FileItem maxItem = currentItems[t[0]];

                for (int i = 1; i < k2; i++) {
                    FileItem currentItem = currentItems[t[i]];
                    if (currentItem.getKey() > maxItem.getKey()) {
                        maxIndex = i;
                        maxItem = currentItem;
                    }
                }

                // Записуємо максимальний елемент у поточний приймач
                writers[j].write(maxItem.toString() + "\n");

                // Чтение следующего элемента из источника
                int sourceIndex = t[maxIndex];
                FileItem nextItem = readNextItem(readers[sourceIndex]);

                // Закінчилися серії в цьому джерелі
                if (nextItem == null) {
                    hasMoreData[sourceIndex] = false;
                    swap(t, maxIndex, k2 - 1);
                    swap(t, k2 - 1, k1 - 1);
                    k1--;
                    k2--;
                } else if (nextItem.getKey() > maxItem.getKey()) {
                    // Кінець серії
                    currentItems[sourceIndex] = nextItem;
                    // спускаємо джерело в кінець активних
                    swap(t, maxIndex, k2 - 1);
                    // зменшуємо кількість активних джерел для поточної серії для того
                    // щоб не працювати з файлами які вже відпрацювали серію
                    k2--;
                } else {
                    // Продовження серії
                    currentItems[sourceIndex] = nextItem;
                }
            }

            j = (j + 1) % N;
        }

        for (FileReader reader : readers) {
            if (reader != null) {
                reader.close();
            }
        }

        for (FileWriter writer : writers) {
            if (writer != null) {
                writer.close();
            }
        }

        return L;
    }

    public static FileItem readNextItem(FileReader reader) throws IOException {
        StringBuilder lineBuilder = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            char character = (char) c;
            if (character == '\n' || character == '\r') {
                break;
            }
            lineBuilder.append(character);
        }
        if (c == -1 && lineBuilder.isEmpty()) {
            return null;
        }
        return FileItem.fromLine(lineBuilder.toString());
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static int countRunsInFile(File file) throws IOException {
        int count = 0;
        try (FileReader reader = new FileReader(file)) {
            StringBuilder stringBuilder = new StringBuilder();

            int prevValue = Integer.MIN_VALUE;
            int ch;

            while ((ch = reader.read()) != -1){
                char character = (char) ch;
                if (character == '\n' || character == '\r') {
                    FileItem fileItem = FileItem.fromLine(stringBuilder.toString());
                    int currentValue = fileItem.getKey();

                    if (prevValue == Integer.MIN_VALUE) {
                        count++; // Перша серія
                    } else if (currentValue > prevValue) {
                        count++; // Нова серія
                    }

                    prevValue = currentValue;
                    stringBuilder.setLength(0);
                } else {
                    stringBuilder.append(character);
                }
            }
        }
        return count;
    }

    public static int countTotalRuns(File[] files) throws IOException {
        int total = 0;
        for (File file : files) {
            total += countRunsInFile(file);
        }
        return total;
    }
}
