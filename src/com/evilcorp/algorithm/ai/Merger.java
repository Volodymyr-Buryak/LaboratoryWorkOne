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

        for (File chunk : chunks) {
            RecordReader rr = new RecordReader(chunk);
            if (rr.hasNext()) {
                pq.add(rr);
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
            while (!pq.isEmpty()) {
                RecordReader rr = pq.poll();
                Record rec = rr.next();
                bw.write(rec.toString());
                bw.newLine();

                if (rr.hasNext()) {
                    pq.add(rr);
                } else {
                    rr.close();
                }
            }
        }
    }
}
