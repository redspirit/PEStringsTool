package com.pestrings.pestringstool.pe;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PEReplaceItem {

    public PEStringItem stringItem;
    public String newText;
    public byte[] bytes;
    public int localAddr;

    public PEReplaceItem(PEStringItem stringItem, String newText) {
        this.stringItem = stringItem;
        this.newText = newText;
    }

    public StringProperty origValueProperty() {
        return new SimpleStringProperty(stringItem.data);
    }

    public StringProperty newValueProperty() {
        return new SimpleStringProperty(newText);
    }

    public StringProperty offsetProperty() {
        return new SimpleStringProperty("0x" + Integer.toHexString(stringItem.offset));
    }

}
