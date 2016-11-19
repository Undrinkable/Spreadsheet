package com.me.hannah.spreadsheet;

import android.net.Uri;
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

public class SpreadsheetActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SpreadsheetFragment.OnFragmentInteractionListener {

    private String[][] _model = new String[][]{new String[]{"A1", "A2", "A3", "A4"},
            new String[]{"B1", "B2", "B3", "B4"}, new String[]{"C1", "C2", "C3", "C4"},
            new String[]{"D1", "D2", "D3", "D4"},};
    private ViewGroup _tableLayout;
    private EditText _editCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(inflateView());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
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
        setupTableView();

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
        fillSpreadsheet();
    }

    private void editCell(final int x, final int y) {
        _editCell.setVisibility(View.VISIBLE);
        _editCell.setText(_model[x][y]);
        _editCell.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent == null || keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        i == KeyEvent.KEYCODE_ENDCALL) {
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
    public void onFragmentInteraction(Uri uri) {

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
        Toast.makeText(this, "Clear pressed", Toast.LENGTH_SHORT).show();
    }

    private void reload() {
        Toast.makeText(this, "Reload pressed", Toast.LENGTH_SHORT).show();
    }

    private void save() {
        Toast.makeText(this, "Save pressed", Toast.LENGTH_SHORT).show();
    }

    private void addRow() {
        String[][] newModel = new String[_model.length + 1][_model[0].length];
        for (int rowIndex = 0; rowIndex < _model.length; rowIndex++) {
            newModel[rowIndex] = Arrays.copyOf(_model[rowIndex], _model[rowIndex].length);
        }
        newModel[_model.length] = new String[_model[0].length];

        _model = newModel;
        setupTableView();
        fillSpreadsheet();
    }

    private void addColumn() {
        String[][] newModel = new String[_model.length][_model[0].length + 1];
        for (int rowIndex = 0; rowIndex < _model.length; rowIndex++) {
            newModel[rowIndex] = Arrays.copyOf(_model[rowIndex], _model[rowIndex].length + 1);
        }
        _model = newModel;
        setupTableView();
        fillSpreadsheet();
    }
}
