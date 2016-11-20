package com.me.hannah.spreadsheet;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hannah on 11/19/16.
 * A stack of two dimensional string arrays. Items are copied before being pushed.
 */
class SpreadsheetModel extends ArrayList<ArrayList<String>> {

    public SpreadsheetModel() {
    }

    public SpreadsheetModel(SpreadsheetModel model) {
        for (ArrayList<String> list : model) {
            add(new ArrayList<>(list));
        }
    }

    /**
     * default of 1 will be used if the given default is less than 1.
     *
     * @param size number >= 1
     * @return a SpreadsheetModel with the given dimensions and all "" values
     */
    public static SpreadsheetModel blankModel(int size) {
        SpreadsheetModel model = new SpreadsheetModel();
        if (size < 1) size = 1;
        for (int i = 0; i < size; i++) {
            model.add(emptyStringListOfSize(size));
        }
        return model;
    }

    @NonNull
    private static ArrayList<String> emptyStringListOfSize(int size) {
        ArrayList<String> list = new ArrayList<>();
        for (int j = 0; j < size; j++) {
            list.add("");
        }
        return list;
    }

    @Override
    public void clear() {
        for (ArrayList<String> list : this) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, "");
            }
        }
    }

    public void addColumn() {
        for (ArrayList<String> list : this) {
            list.add("");
        }
    }

    public void addRow() {
        int length = get(0).size();
        add(emptyStringListOfSize(length));
    }

    public void ensureDimensions() {
        int width = 0;
        for (List list : this) {
            if (width < list.size()) width = list.size();
        }
        for (List<String> list : this) {
            while (list.size() < width) {
                list.add("");
            }
        }
    }
}
