package com.evilcorp.algorithm.ai;

import java.io.*;

public class RecordReader implements Comparable<RecordReader>, Closeable {
    private BufferedReader br;
    private Record current;
    private File file;

    public RecordReader(File file) throws IOException {
        this.file = file;
        this.br = new BufferedReader(new FileReader(file));
        advance();
    }

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
        return other.peek().compareTo(this.peek()); // зворотній порядок
    }

    @Override
    public void close() throws IOException {
        br.close();
    }
}