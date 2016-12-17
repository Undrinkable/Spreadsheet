package com.me.hannah.spreadsheet;

import com.google.gson.Gson;

/**
 * Created by hannah on 11/19/16.
 * <p>
 * Turns two-dimensional String arrays into a single String and back.
 */

class SpreadsheetEncoder {

    static String encodeSpreadsheetData(SpreadsheetModel model) {
        Gson gson = new Gson();
        return gson.toJson(model);
    }

    static SpreadsheetModel decodeSpreadsheetData(String modelString) {
        Gson gson = new Gson();
        SpreadsheetModel model = gson.fromJson(modelString, SpreadsheetModel.class);
        if (model != null) model.ensureDimensions();
        return model;
    }
}
