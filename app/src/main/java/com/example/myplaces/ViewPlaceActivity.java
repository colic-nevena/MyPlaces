package com.example.myplaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplaces.data.MyPlace;
import com.example.myplaces.data.MyPlacesData;

public class ViewPlaceActivity extends AppCompatActivity {
    TextView nameTextView, descriptionTextView, latitudeTextVIew, longitudeTextView;
    Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int index = -1;
        try {
            Intent listIntent = getIntent();
            Bundle indexBundle = listIntent.getExtras();
            index = indexBundle.getInt("index");
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

        if (index >= 0) {
            MyPlace place = MyPlacesData.getInstance().getPlace(index);

            nameTextView = findViewById(R.id.tv_name_viewplace);
            descriptionTextView = findViewById(R.id.tv_desc_viewplace);
            latitudeTextVIew = findViewById(R.id.tv_lat_viewplace);
            longitudeTextView = findViewById(R.id.tv_long_viewplace);

            nameTextView.setText(place.name);
            descriptionTextView.setText(place.description);
            latitudeTextVIew.setText(place.latitude);
            longitudeTextView.setText(place.longitude);
        }
        okButton = findViewById(R.id.btn_ok_viewplace);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.show_map_item) {
            Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.my_places_list_item) {
            Intent i = new Intent(this, MyPlacesListActivity.class);
            startActivity(i);
        } else if (id == R.id.about_item) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else if (id == android.R.id.home) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
