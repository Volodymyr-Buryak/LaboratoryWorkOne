package com.evilcorp.algorithm.ai;

import java.io.File;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class RecordReader implements Comparable<RecordReader>, Closeable {
    private File file;
    private Record current;
    private BufferedReader br;

    public RecordReader(File file) throws IOException {
        this.file = file;
        this.br = new BufferedReader(new FileReader(file));
        advance();
    }

    // читаємо перший елемент з файлу (серії)
    private void advance() throws IOException {
        String line = br.readLine();
        if (line != null) {
            current = Record.parse(line);
        } else {
            current = null;
        }
    }

    public boolean hasNext() {
        return current != null;
    }

    public Record next() throws IOException {
        Record temp = current;
        advance();
        return temp;
    }

    public Record peek() {
        return current;
    }

    @Override
    public int compareTo(RecordReader other) {
        // використовуємо compereTo з Record, але в зворотному порядку для max-heap
        return other.peek().compareTo(this.peek());
    }

    @Override
    public void close() throws IOException {
        br.close();
    }
}