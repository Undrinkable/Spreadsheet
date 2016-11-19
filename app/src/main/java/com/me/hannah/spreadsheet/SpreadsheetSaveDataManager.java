package com.me.hannah.spreadsheet;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hannah on 11/19/16.
 */

public class SpreadsheetSaveDataManager {

    private static final String MODEL_KEY = "SpreadsheetPreference.Model";
    private Activity _activity;

    public SpreadsheetSaveDataManager(Activity activity) {
        _activity = activity;
    }

    public String loadModelString() {
        SharedPreferences preferences = _activity.getPreferences(MODE_PRIVATE);
        return preferences.getString(MODEL_KEY, "");
    }

    public void saveModelString(String modelString) {
        SharedPreferences preferences = _activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MODEL_KEY, modelString);
        editor.commit();
    }

    public boolean hasSavedModel() {
        return _activity.getPreferences(MODE_PRIVATE).contains(MODEL_KEY);
    }
}
