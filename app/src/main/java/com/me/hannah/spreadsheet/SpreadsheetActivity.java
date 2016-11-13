package com.me.hannah.spreadsheet;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class SpreadsheetActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SpreadsheetFragment.OnFragmentInteractionListener {


    private TableLayout _tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentView = getView();
        setContentView(contentView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @NonNull
    private View getView() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_spreadsheet, null);
        _tableLayout = (TableLayout) contentView.findViewById(R.id.tableLayout);
        for (int i = 0; i < 4; i++) {
            TableRow row = new TableRow(this);
            row.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_END | LinearLayout.SHOW_DIVIDER_MIDDLE);
            row.setDividerDrawable(new ColorDrawable(Color.GRAY));
            for (int j = 0; j < 4; j++) {
                EditText t = new EditText(this);
                t.setMinWidth(150);
                row.addView(t);
            }
            _tableLayout.addView(row);
        }
        _tableLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_END | LinearLayout.SHOW_DIVIDER_MIDDLE);
        _tableLayout.setDividerDrawable(new ColorDrawable(Color.GRAY));
        return contentView;
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

    @SuppressWarnings("StatementWithEmptyBody")
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

    }

    private void reload() {

    }

    private void save() {

    }
}
