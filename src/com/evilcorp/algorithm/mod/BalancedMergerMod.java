package com.evilcorp.algorithm.mod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import com.evilcorp.classes.FileItem;

public class BalancedMergerMod {
    private final int n;

    public BalancedMergerMod(int n) {
        this.n = n;
    }

    public int merge(File[] sources, File[] targets) throws IOException {
        // Ініціалізація читачів
        BufferedReader[] readers = new BufferedReader[n];
        // Зберігаємо поточні елементи з кожного джерела
        FileItem[] currentItems = new FileItem[n];
        // Флаги наявності даних у джерелах
        boolean[] hasMoreData = new boolean[n];

        // кількість активних джерел
        int k1 = 0;

        for (int i = 0; i < n; i++) {
            if (sources[i].exists() && sources[i].length() > 0) {
                readers[i] = new BufferedReader(new FileReader(sources[i]));
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
        BufferedWriter[] writers = new BufferedWriter[n];
        for (int i = 0; i < n; i++) {
            writers[i] = new BufferedWriter(new FileWriter(targets[i]));
        }

        // Масив індексів активних джерел
        int[] t = new int[k1];

        for (int i = 0, j = 0; i < n; i++) {
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

            j = (j + 1) % n;
        }

        for (BufferedReader br : readers) {
            if (br != null) {
                br.close();
            }
        }

        for (BufferedWriter bw : writers) {
            if (bw != null) {
                bw.close();
            }
        }

        return L;
    }

    public static FileItem readNextItem(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return (line != null) ? FileItem.fromLine(line) : null;
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
