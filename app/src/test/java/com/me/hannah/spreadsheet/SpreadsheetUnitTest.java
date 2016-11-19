package com.me.hannah.spreadsheet;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SpreadsheetUnitTest {

    @Test
    public void test_encodeAndRestoreModel() {
        String[][] input = new String[][]{new String[]{"A1", "A2", "A3", "A4"},
                new String[]{"B1", "B2", "B3", "B4"}, new String[]{"C1", "C2", "C3", "C4"},
                new String[]{"D1", "D2", "D3", "D4"}};

        assertArrayEquals(SpreadsheetEncoder
                .decodeSpreadsheetData(SpreadsheetEncoder.encodeSpreadsheetData(input)), input);
    }
}