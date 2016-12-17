package com.me.hannah.spreadsheet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

public class SpreadsheetActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String MODEL_KEY = "Spreadsheet.Model";
    private static final String EDIT_HISTORY_KEY = "Spreadsheet.EditHistory";

    private SpreadsheetModel _model;
    private SpreadsheetSaveDataManager _saveDataManager;
    private Stack<SpreadsheetModel> _editHistory;

    private ViewGroup _tableLayout;
    private EditText _editText;
    private View _cellBeingEdited;

    static String letterFromNumber(int i) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (i < 26) {
            return String.format("%s", alphabet.charAt(i));
        } else {
            int firstChar = (i / 26);
            int secondChar = (i % 26);
            return String
                    .format("%s%s", alphabet.charAt(firstChar - 1), alphabet.charAt(secondChar));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _saveDataManager = new SpreadsheetSaveDataManager(this);

        initializeModel(savedInstanceState);
        setContentView(inflateView());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undo();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MODEL_KEY, _model);
        outState.putSerializable(EDIT_HISTORY_KEY, _editHistory);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initializeModel(savedInstanceState);
    }

    /**
     * If there is a savedInstanceState and it has a saved model, use that.
     * Otherwise, retrieve model from preferences if it exists.
     * Otherwise, initialize a new blank 2x2 spreadsheet model.
     * preferences.
     */
    private void initializeModel(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            _model = (SpreadsheetModel) savedInstanceState.getSerializable(MODEL_KEY);
            _editHistory =
                    (Stack<SpreadsheetModel>) savedInstanceState.getSerializable(EDIT_HISTORY_KEY);
        } else if (_saveDataManager.hasSavedModel()) {
            _model = SpreadsheetEncoder.decodeSpreadsheetData(_saveDataManager.loadModelString());
        }


        if (_model == null) {
            createNewBlankModel();
        }
        if (_editHistory == null) {
            _editHistory = new Stack<>();
        }
    }

    private void createNewBlankModel() {
        _model = SpreadsheetModel.blankModel(2);
    }

    @NonNull
    private View inflateView() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_spreadsheet, null);

        contentView.findViewById(R.id.add_row).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRow();
            }
        });
        contentView.findViewById(R.id.add_column).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addColumn();
            }
        });

        _editText = (EditText) contentView.findViewById(R.id.editText);
        _tableLayout = (TableLayout) contentView.findViewById(R.id.tableLayout);

        return contentView;
    }

    private void setupTableView() {
        _tableLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int rowIndex = 0; rowIndex < _model.size() + 1; rowIndex++) {

            ViewGroup row =
                    (ViewGroup) inflater.inflate(R.layout.spreadsheet_row, _tableLayout, false);
            _tableLayout.addView(row);

            for (int columnIndex = 0; columnIndex < _model.get(0).size() + 1; columnIndex++) {
                View cell = inflater.inflate(R.layout.spreadsheet_cell, row, false);
                row.addView(cell);

                final int x = rowIndex, y = columnIndex;
                if (x != 0 && y != 0) {
                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editCell(view, x - 1, y - 1);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void editCell(View cell, final int x, final int y) {
        _editText.setVisibility(View.VISIBLE);
        _editText.setText(_model.get(x).get(y));
        _editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent == null || keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        i == KeyEvent.KEYCODE_ENDCALL) {
                    doneEditing(x, y);
                    return true;
                }
                return false;
            }
        });
        _editText.requestFocus();

        if (_cellBeingEdited != null) unhighlightCell();
        _cellBeingEdited = cell;
        highlightCell();
        showKeyboard();
    }

    private void highlightCell() {
        _cellBeingEdited.setBackgroundColor(Color.YELLOW);
    }

    private void doneEditing(int x, int y) {
        logModelState();
        _model.get(x).set(y, _editText.getText().toString());
        fillSpreadsheet();

        _editText.setVisibility(View.INVISIBLE);

        unhighlightCell();
        _cellBeingEdited = null;

        hideKeyboard();
    }

    private void unhighlightCell() {
        _cellBeingEdited.setBackgroundColor(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_clear) {
            clearSelected();
        } else if (id == R.id.nav_reload) {
            reloadSelected();
        } else if (id == R.id.nav_save) {
            saveSelected();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void saveSelected() {
        promptForConfirmation("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                save();
            }
        });
    }

    private void reloadSelected() {
        promptForConfirmation("Reload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reload();
            }
        });
    }

    private void clearSelected() {
        promptForConfirmation("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clear();
            }
        });
    }

    private void promptForConfirmation(String title,
                                       DialogInterface.OnClickListener confirmClickListener) {
        new AlertDialog.Builder(this).setTitle(title).setMessage("Are you sure?")
                .setPositiveButton("Yes", confirmClickListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create().show();
    }

    private void clear() {
        logModelState();
        _model.clear();
        _editHistory.clear();
        updateView();
        Toast.makeText(this, R.string.spreadsheet_cleared, Toast.LENGTH_SHORT).show();
    }


    private void reload() {
        logModelState();
        _model = SpreadsheetEncoder.decodeSpreadsheetData(_saveDataManager.loadModelString());
        if (_model != null) {
            updateView();
            Toast.makeText(this, R.string.spreadsheet_loaded, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No data to load", Toast.LENGTH_SHORT).show();
            createNewBlankModel();
            updateView();
        }
    }

    private void logModelState() {
        _editHistory.push(new SpreadsheetModel(_model));
    }

    private void save() {
        _saveDataManager.saveModelString(SpreadsheetEncoder.encodeSpreadsheetData(_model));
        Toast.makeText(this, R.string.changes_saved, Toast.LENGTH_SHORT).show();
    }

    private void undo() {
        if (!_editHistory.empty()) {
            _model = _editHistory.pop();
            updateView();
        } else {
            Toast.makeText(this, R.string.nothing_to_undo, Toast.LENGTH_SHORT).show();
        }
    }

    private void addRow() {
        logModelState();
        _model.addRow();
        updateView();
    }

    private void addColumn() {
        logModelState();
        _model.addColumn();
        updateView();
    }

    private void updateView() {
        setupTableView();
        fillSpreadsheet();
    }

    private void fillSpreadsheet() {
        for (int rowIndex = 0; rowIndex < _tableLayout.getChildCount(); rowIndex++) {
            TableRow row = (TableRow) _tableLayout.getChildAt(rowIndex);
            for (int cellIndex = 0; cellIndex < row.getChildCount(); cellIndex++) {
                TextView cell = (TextView) row.getChildAt(cellIndex);
                if (rowIndex == 0) {
                    if (cellIndex > 0) {
                        cell.setText(letterFromNumber(cellIndex - 1));
                    }
                    cell.setBackgroundColor(Color.LTGRAY);
                } else if (cellIndex == 0) {
                    cell.setText(String.format("%s", rowIndex));
                    cell.setBackgroundColor(Color.LTGRAY);
                } else {
                    cell.setText(_model.get(rowIndex - 1).get(cellIndex - 1));
                }
            }
        }
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(_editText, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(_editText.getWindowToken(), 0);
        }
    }
}
