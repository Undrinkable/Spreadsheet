package com.me.hannah.spreadsheet;

import com.google.gson.Gson;

/**
 * Created by hannah on 11/19/16.
 * <p>
 * Turns two-dimensional String arrays into a single String and back.
 */

public class SpreadsheetEncoder {

    public static String[][] decodeSpreadsheetData(String modelString) {
        Gson gson = new Gson();
        return gson.fromJson(modelString, String[][].class);
    }

    public static String encodeSpreadsheetData(String[][] model) {
        Gson gson = new Gson();
        return gson.toJson(model);
    }
}
