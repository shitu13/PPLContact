package com.example.rowan.pplcontact;

import java.util.Comparator;

public class CustomComparator implements Comparator<DataHolder> {
    @Override
    public int compare(DataHolder o1, DataHolder o2) {

        return o1.getName().compareTo(o2.getName());
    }
}
