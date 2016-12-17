package com.me.hannah.spreadsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hannah on 11/19/16.
 * Implements the details of saving and loading the spreadsheet data.
 */

class SpreadsheetSaveDataManager {

    private static final String FILE_NAME = "SpreadsheetPreference.FileName";
    private static final String MODEL_KEY = "SpreadsheetPreference.Model";
    private final Context _context;

    SpreadsheetSaveDataManager(Context context) {
        _context = context;
    }

    String loadModelString() {
        SharedPreferences preferences = _context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        return preferences.getString(MODEL_KEY, "");
    }

    @SuppressLint("CommitPrefEdits")
    void saveModelString(String modelString) {
        SharedPreferences preferences = _context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MODEL_KEY, modelString);
        editor.commit();
    }

    boolean hasSavedModel() {
        return _context.getSharedPreferences(FILE_NAME, MODE_PRIVATE).contains(MODEL_KEY);
    }
}
