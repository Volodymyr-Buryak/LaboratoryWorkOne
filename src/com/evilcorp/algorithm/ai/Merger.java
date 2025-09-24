package com.evilcorp.algorithm.ai;

import java.io.File;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.PriorityQueue;

public class Merger {
    public static void mergeSortedChunks(List<File> chunks, File output) throws IOException {
        PriorityQueue<RecordReader> pq = new PriorityQueue<>();
        // Читаємо по одному запису з кожної серії і додаємо
        // в пріоритетну чергу в об'єктах RecordReader
        for (File chunk : chunks) { // працює до k файлів
            RecordReader rr = new RecordReader(chunk);
            if (rr.hasNext()) {
                pq.add(rr); // O(log k)
            }
        }
        // O(k log k)

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
            while (!pq.isEmpty()) { // працює до n елементів
                RecordReader rr = pq.poll();  // O(log k)
                Record rec = rr.next();
                bw.write(rec.toString());
                bw.newLine();
                if (rr.hasNext()) {
                    pq.add(rr); // O(log k)
                } else {
                    rr.close();
                }
            }
        }
        //O(n log k) но k << n
    }
}
