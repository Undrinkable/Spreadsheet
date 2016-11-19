package com.me.hannah.spreadsheet;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SpreadsheetInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.me.hannah.spreadsheet", appContext.getPackageName());
    }

    public void testPreferenceSave() {
        SpreadsheetSaveDataManager manager =
                new SpreadsheetSaveDataManager(InstrumentationRegistry.getContext());
        String input = "Example preference string";
        manager.saveModelString(input);
        assertEquals(input, manager.loadModelString());
    }
}
