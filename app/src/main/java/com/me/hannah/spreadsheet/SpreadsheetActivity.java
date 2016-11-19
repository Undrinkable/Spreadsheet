package com.me.hannah.spreadsheet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Stack;

public class SpreadsheetActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String MODEL_KEY = "Spreadsheet.Model";
    private static final String EDIT_HISTORY_KEY = "Spreadsheet.EditHistory";

    private String[][] _model;
    private SpreadsheetSaveDataManager _saveDataManager;

    private ViewGroup _tableLayout;
    private EditText _editCell;

    private Stack<String[][]> _editHistory;

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
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MODEL_KEY, SpreadsheetEncoder.encodeSpreadsheetData(_model));
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
     *
     * @param savedInstanceState
     */
    private void initializeModel(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            _model = SpreadsheetEncoder
                    .decodeSpreadsheetData(savedInstanceState.getString(MODEL_KEY));
            _editHistory = (Stack<String[][]>) savedInstanceState.getSerializable(EDIT_HISTORY_KEY);
        } else if (_saveDataManager.hasSavedModel()) {
            _model = SpreadsheetEncoder.decodeSpreadsheetData(_saveDataManager.loadModelString());
        }


        if (_model == null) _model = new String[][]{new String[]{"", ""}, new String[]{"", ""}};
        if (_editHistory == null) _editHistory = new Stack<>();
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

        _editCell = (EditText) contentView.findViewById(R.id.editText);
        _tableLayout = (TableLayout) contentView.findViewById(R.id.tableLayout);

        return contentView;
    }

    private void setupTableView() {
        _tableLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int rowIndex = 0; rowIndex < _model.length; rowIndex++) {

            ViewGroup row =
                    (ViewGroup) inflater.inflate(R.layout.spreadsheet_row, _tableLayout, false);
            _tableLayout.addView(row);

            for (int columnIndex = 0; columnIndex < _model[0].length; columnIndex++) {
                View cell = inflater.inflate(R.layout.spreadsheet_cell, row, false);
                row.addView(cell);

                final int x = rowIndex, y = columnIndex;
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editCell(x, y);
                    }
                });

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void editCell(final int x, final int y) {
        _editCell.setVisibility(View.VISIBLE);
        _editCell.setText(_model[x][y]);
        _editCell.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent == null || keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        i == KeyEvent.KEYCODE_ENDCALL) {
                    logStateOfModel();

                    _model[x][y] = _editCell.getText().toString();
                    fillSpreadsheet();
                    _editCell.setVisibility(View.INVISIBLE);
                    return true;
                }
                return false;
            }
        });
        _editCell.callOnClick();
    }

    private void fillSpreadsheet() {
        for (int rowIndex = 0; rowIndex < _tableLayout.getChildCount(); rowIndex++) {
            TableRow row = (TableRow) _tableLayout.getChildAt(rowIndex);
            for (int cellIndex = 0; cellIndex < row.getChildCount(); cellIndex++) {
                TextView cell = (TextView) row.getChildAt(cellIndex);
                cell.setText(_model[rowIndex][cellIndex]);
            }
        }
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
            clear();
        } else if (id == R.id.nav_reload) {
            reload();
        } else if (id == R.id.nav_save) {
            save();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clear() {
        logStateOfModel();

        for (int rowIndex = 0; rowIndex < _model.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < _model[0].length; columnIndex++) {
                _model[rowIndex][columnIndex] = "";
            }
        }
        updateView();

        Toast.makeText(this, R.string.spreadsheet_cleared, Toast.LENGTH_SHORT).show();
    }

    private void reload() {
        logStateOfModel();

        _model = SpreadsheetEncoder.decodeSpreadsheetData(_saveDataManager.loadModelString());
        updateView();

        Toast.makeText(this, R.string.spreadsheet_loaded, Toast.LENGTH_SHORT).show();
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
        logStateOfModel();

        String[][] newModel = new String[_model.length + 1][_model[0].length];
        for (int rowIndex = 0; rowIndex < _model.length; rowIndex++) {
            newModel[rowIndex] = Arrays.copyOf(_model[rowIndex], _model[rowIndex].length);
        }
        newModel[_model.length] = new String[_model[0].length];

        _model = newModel;
        updateView();
    }

    private void logStateOfModel() {
        String[][] model = new String[_model.length][_model[0].length];
        for (int rowIndex = 0; rowIndex < _model.length; rowIndex++) {
            model[rowIndex] = Arrays.copyOf(_model[rowIndex], _model[rowIndex].length);
        }
        _editHistory.push(model);
    }

    private void addColumn() {
        logStateOfModel();

        String[][] newModel = new String[_model.length][_model[0].length + 1];
        for (int rowIndex = 0; rowIndex < _model.length; rowIndex++) {
            newModel[rowIndex] = Arrays.copyOf(_model[rowIndex], _model[rowIndex].length + 1);
        }
        _model = newModel;
        updateView();
    }

    private void updateView() {
        setupTableView();
        fillSpreadsheet();
    }
}
