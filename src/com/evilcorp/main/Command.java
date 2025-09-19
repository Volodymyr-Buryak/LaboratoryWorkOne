package com.evilcorp.main;

public enum Command {

    SORT("fs"), GENERATE("fg"), INPUT("input"), OUTPUT("output");

    private final String key;

    Command(String key){
        this.key = key;
    }

    public static Command fromKey(String k) {
        for (Command c : values()) {
            if (c.key.equals(k)) {
                return c;
            }
        }
        return null;
    }

}
