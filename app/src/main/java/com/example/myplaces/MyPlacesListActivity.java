package com.example.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myplaces.data.MyPlace;
import com.example.myplaces.data.MyPlacesData;

import java.util.ArrayList;

public class MyPlacesListActivity extends AppCompatActivity {
    ListView myPlacesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyPlacesListActivity.this, EditMyPlaceActivity.class);
                startActivity(i);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myPlacesListView = findViewById(R.id.lv_my_places);
        myPlacesListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MyPlacesData.getInstance().getMyPlaces()));

        myPlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle indexBundle = new Bundle();
                indexBundle.putInt("index", position);

                Intent i = new Intent(MyPlacesListActivity.this, ViewPlaceActivity.class);
                i.putExtras(indexBundle);

                startActivity(i);
            }
        });

        myPlacesListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

                MyPlace place = MyPlacesData.getInstance().getPlace(info.position);

                menu.setHeaderTitle(place.name);
                menu.add(0, 1, 1, "View place");
                menu.add(0,2,2,"Edit place");
                menu.add(0,3,3,"Delete place");
                menu.add(0,4,4,"Show on map");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Bundle indexBundle = new Bundle();
        indexBundle.putInt("index", info.position);
        Intent i = null;

        if (id == 1) { // view place
            i = new Intent(this, ViewPlaceActivity.class);
            i.putExtras(indexBundle);
            startActivity(i);
        } else if (id == 2) { // edit place
            i = new Intent(this, EditMyPlaceActivity.class);
            i.putExtras(indexBundle);
            startActivityForResult(i, 1);
        } else if (id == 3) {
            i = new Intent(this, DeletePlace.class);
            i.putExtras(indexBundle);
            startActivityForResult(i, 1);
        } else if (id == 4) {
            i = new Intent(this, MapsActivity.class);
            i.putExtra("state", MapsActivity.CENTER_PLACE);
            MyPlace place = MyPlacesData.getInstance().getPlace(info.position);
            i.putExtra("latitude", place.latitude);
            i.putExtra("longitude", place.longitude);
            startActivityForResult(i, 2);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_places_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.show_map_item) {
            Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.new_place_item) {
            Intent i = new Intent(this, EditMyPlaceActivity.class);
            startActivityForResult(i, 1);
        } else if (id == R.id.about_item) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else if (id == android.R.id.home) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            ListView myPlacesListView = findViewById(R.id.lv_my_places);
            myPlacesListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MyPlacesData.getInstance().getMyPlaces()));
        }
    }
}
