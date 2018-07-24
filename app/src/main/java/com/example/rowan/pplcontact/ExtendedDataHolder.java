package com.example.rowan.pplcontact;

public class ExtendedDataHolder {

    DataHolder holder;
    boolean isSelected;

    public ExtendedDataHolder(DataHolder holder, boolean isSelected) {
        this.holder = holder;
        this.isSelected = isSelected;
    }

    public DataHolder getHolder() {
        return holder;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
