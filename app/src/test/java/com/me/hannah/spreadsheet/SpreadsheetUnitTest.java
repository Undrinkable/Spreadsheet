package com.me.hannah.spreadsheet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SpreadsheetUnitTest {

    @Test
    public void test_encodeAndRestoreModel() {
        SpreadsheetModel input = new SpreadsheetModel();
        input.clear();

        input.add(new ArrayList<>(Arrays.asList(new String[]{"A1", "A2", "A3", "A4"})));
        input.add(new ArrayList<>(Arrays.asList(new String[]{"B1", "B2", "B3", "B4"})));
        input.add(new ArrayList<>(Arrays.asList(new String[]{"C1", "C2", "C3", "C4"})));
        input.add(new ArrayList<>(Arrays.asList(new String[]{"D1", "D2", "D3", "D4"})));

        assertEquals(input, SpreadsheetEncoder
                .decodeSpreadsheetData(SpreadsheetEncoder.encodeSpreadsheetData(input)));
    }

    @Test
    public void test_spreadSheetModelNew() {
        SpreadsheetModel model = SpreadsheetModel.blankModel(2);
        assertEquals(2, model.size());
        assertEquals(2, model.get(0).size());
        assertEquals(2, model.get(1).size());
        assertEquals("", model.get(0).get(0));
        assertEquals("", model.get(0).get(1));
        assertEquals("", model.get(1).get(0));
        assertEquals("", model.get(1).get(1));
    }

    @Test
    public void test_spreadSheetModel0Size() {
        SpreadsheetModel model = SpreadsheetModel.blankModel(0);
        assertEquals(1, model.size());
        assertEquals(1, model.get(0).size());
        assertEquals("", model.get(0).get(0));
    }

    @Test
    public void test_spreadSheetModelAddColumn() {
        SpreadsheetModel model = SpreadsheetModel.blankModel(2);
        int initialRowCount = model.size();
        int initialColumnCount = model.get(0).size();
        model.addColumn();
        assertEquals(initialRowCount, model.size());
        assertEquals(initialColumnCount + 1, model.get(0).size());
        assertEquals(initialColumnCount + 1, model.get(1).size());
    }

    @Test
    public void test_spreadSheetModelAddRow() {
        SpreadsheetModel model = SpreadsheetModel.blankModel(2);
        int initialRowCount = model.size();
        int initialColumnCount = model.get(0).size();
        model.addRow();
        assertEquals(initialRowCount + 1, model.size());
        assertEquals(initialColumnCount, model.get(0).size());
        assertEquals(initialColumnCount, model.get(1).size());
        assertEquals(initialColumnCount, model.get(2).size());
    }

    @Test
    public void test_spreadSheetModelClear() {
        SpreadsheetModel model = SpreadsheetModel.blankModel(2);
        int initialRowCount = model.size();
        int initialColumnCount = model.get(0).size();

        model.get(0).set(0, "A");
        model.get(0).set(1, "B");
        model.get(1).set(0, "C");
        model.get(1).set(1, "D");

        model.clear();

        // assert size didn't change
        assertEquals(initialRowCount, model.size());
        assertEquals(initialColumnCount, model.get(0).size());

        // assert values are blank
        assertEquals("", model.get(0).get(0));
        assertEquals("", model.get(0).get(1));
        assertEquals("", model.get(1).get(0));
        assertEquals("", model.get(1).get(1));
    }

    @Test
    public void test_ensureDimension() {
        SpreadsheetModel input = new SpreadsheetModel();
        input.clear();

        input.add(new ArrayList<>(Arrays.asList(new String[]{"A1", "A2"})));
        input.add(new ArrayList<>(Arrays.asList(new String[]{"B1", "B2", "B3", "B4"})));
        input.add(new ArrayList<>(Arrays.asList(new String[]{"C1", "C2", "C3", "C4"})));

        int initialRowCount = input.size();
        input.ensureDimensions();
        assertEquals(input.get(0).size(), input.get(1).size());
        assertEquals(initialRowCount, input.size());
    }

    @Test
    public void test_columnHeaderLabel() {
        assertEquals("A", SpreadsheetActivity.letterFromNumber(0));
        assertEquals("Z", SpreadsheetActivity.letterFromNumber(25));
        assertEquals("AA", SpreadsheetActivity.letterFromNumber(26));
        assertEquals("AB", SpreadsheetActivity.letterFromNumber(27));
        assertEquals("AZ", SpreadsheetActivity.letterFromNumber(51));
        assertEquals("BA", SpreadsheetActivity.letterFromNumber(52));
        assertEquals("BZ", SpreadsheetActivity.letterFromNumber(77));
        assertEquals("CZ", SpreadsheetActivity.letterFromNumber(103));
    }

}