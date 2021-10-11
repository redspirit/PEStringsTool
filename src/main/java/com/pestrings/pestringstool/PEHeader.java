package com.pestrings.pestringstool;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PEHeader {

    public String e_magic;
    public int e_lfanew;
    public String signature;
    public short machine;
    public short numberOfSections;
    public short sizeOfOptionalHeader;
    public short characteristics;
    public short magic;
    public int addressOfEntryPoint;
    public int imageBase;
    public int sectionAlignment;
    public int fileAlignment;
    public int sizeOfImage;
    public int sizeOfHeaders;
    public short subsystem;
    public int numberOfRvaAndSizes;
    public int importsVA;
    public List<PESection> sections = new ArrayList<>();

    // byte 8, short 16, int 32, long 64

    public void loadBuffer(ByteBuffer buffer) {

        int peHeaderAddress = buffer.getInt(60);

        // getting file headers

        this.e_magic = Character.toString(buffer.get(0))
                + Character.toString(buffer.get(1));
        this.e_lfanew = peHeaderAddress;
        this.signature = Character.toString(buffer.get(peHeaderAddress))
                + Character.toString(buffer.get(peHeaderAddress+1));
        this.machine = buffer.getShort(peHeaderAddress + 4);
        this.numberOfSections = buffer.getShort(peHeaderAddress + 6);
        this.sizeOfOptionalHeader = buffer.getShort(peHeaderAddress + 20);
        this.characteristics = buffer.getShort(peHeaderAddress + 22);
        this.magic = buffer.getShort(peHeaderAddress + 24);
        this.addressOfEntryPoint = buffer.getInt(peHeaderAddress + 40);
        this.imageBase = buffer.getInt(peHeaderAddress + 52);
        this.sectionAlignment = buffer.getInt(peHeaderAddress + 56);
        this.fileAlignment = buffer.getInt(peHeaderAddress + 60);
        this.sizeOfImage = buffer.getInt(peHeaderAddress + 80);
        this.sizeOfHeaders = buffer.getInt(peHeaderAddress + 84);
        this.subsystem = buffer.getShort(peHeaderAddress + 92);
        this.numberOfRvaAndSizes = buffer.getInt(peHeaderAddress + 132);
        this.importsVA = buffer.getInt(peHeaderAddress + 132 + 12);

        // calculate sections

        int startAddress = peHeaderAddress + this.sizeOfOptionalHeader + 24;

        for (int i = 0; i < this.numberOfSections; i++) {
            int addr = startAddress + i * 40;
            PESection sect = new PESection();

            int count = 0 ;
            for(int n = 0; n < 8; n++) {
                if(buffer.get(addr + n) >= 32) count++;
            }
            byte[] name = new byte[count];
            buffer.slice(addr, count).get(name);

            sect.name = new String(name, StandardCharsets.UTF_8);
            sect.virtualSize = buffer.getInt(addr + 8);
            sect.virtualAddress = buffer.getInt(addr + 12);
            sect.sizeOfRawData = buffer.getInt(addr + 16);
            sect.pointerToRawData = buffer.getInt(addr + 20);
            sect.characteristics = buffer.getInt(addr + 36);
            sect.headerPointer = addr;

            this.sections.add(sect);
        }

    }

    public PESection getSectionByName(String name) {

        List<PESection> matched = this.sections.stream().filter(item -> item.name.equals(name)).collect(Collectors.toList());
        if(matched.size() == 0) return null;
        return matched.get(0);

    }

}
