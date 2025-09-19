package com.evilcorp.algorithm.ai;

public class Record implements Comparable<Record> {
    private int key;
    private String data;

    public Record(int key, String data) {
        this.key = key;
        this.data = data;
    }

    public static Record parse(String line) {
        int idx = line.indexOf('-');
        int key = Integer.parseInt(line.substring(0, idx));
        String data = line.substring(idx + 1);
        return new Record(key, data);
    }

    @Override
    public int compareTo(Record o) {
        return Integer.compare(this.key, o.key);
    }

    @Override
    public String toString() {
        return key + "-" + data;
    }
}
