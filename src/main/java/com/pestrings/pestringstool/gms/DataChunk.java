package com.pestrings.pestringstool.gms;

public class DataChunk {

    public String name;
    public int startAddress = 0;
    public int size = 0;

    public DataChunk(String name, int startAddress, int size) {
        this.name = name;
        this.startAddress = startAddress;
        this.size = size;
    }

    public String toString() {
        return name + " at " + startAddress + " len = " + size;
    }

}
